package com.ist.ioc.service.common.elasticsearch;

import static com.ist.ioc.service.common.Constants.ES_ADD_ACTION;
import static com.ist.ioc.service.common.Constants.ES_DELETE_ACTION;
import static com.ist.ioc.service.common.MapperField.ES_CREATETIME;
import static com.ist.ioc.service.common.MapperField.ES_DESCRIPTION;
import static com.ist.ioc.service.common.MapperField.ES_ID;
import static com.ist.ioc.service.common.MapperField.ES_NAME;
import static com.ist.ioc.service.common.MapperField.ES_PATH;
import static com.ist.ioc.service.common.MapperField.ES_TITLE;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.SearchType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.ist.assemble.CustomImmutableSetting;
import com.ist.common.es.util.DateUtils;
import com.ist.common.es.util.JsonUtils;
import com.ist.common.es.util.LogUtils;
import com.ist.common.es.util.XMLProperties;
import com.ist.dto.bmp.ESDto;

/**
 * 提供创建映射，索引，搜索等服务
 * 
 * @author qianguobing
 */
public abstract class AbstractIESService  implements IESService {

    private static final Log logger = LogFactory.getLog(AbstractIESService.class);
    @Resource
    private JestClient client = null;
    @Resource
    private CustomImmutableSetting customImmutableSetting = null;
    @Resource
    public XMLProperties sysConfigEs;
    
    /**
     * <p>
     * 批量添加索引文档
     * <p>
     * <p>
     * 这里使用bulk api 它会极大提高索引文档的速度
     * </p>
     * 
     * @param indexParams
     *            索引参数Map
     *            <p>
     *            Map中String分别key,value对应于分行，与支行列表 对应于es中的索引名称和索引类型
     *            </p>
     * @param documents
     *            要索引的文档列表
     * @param action
     *            处理文档动作 1:创建 2:删除
     * @throws IOException 
     */
    public void documentHandler(Map<String, List<String>> mapParams, List<ESDto> documents, Integer action) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug(LogUtils.format("indexParams", mapParams));
        }
        try {
            if (null != mapParams && !mapParams.isEmpty()) {
                for (Entry<String, List<String>> entry : mapParams.entrySet()) {
                    String key = entry.getKey();
                    String index = StringUtils.lowerCase(key);
                    List<String> typeList = entry.getValue();
                    // 删除索引
//                     this.deleteIndex(index);
                    // 创建索引
                    this.addIndex(index);
                    if (null != typeList && !typeList.isEmpty()) {
                        for (String type : typeList) {
                            Bulk.Builder builder = new Bulk.Builder().defaultIndex(index);
                            // 创建文档
                            if (action == ES_ADD_ACTION) {
                                // 创建索引映射
                                createIndexMapping(index, type);
                                // 若文档为空则只创建索引类型
                                if (null != documents && !documents.isEmpty()) {
                                    builder.addAction(Arrays.asList(buildIndexAction(documents)));
                                }
                                // 删除文档
                            } else if (action == ES_DELETE_ACTION && null != documents && !documents.isEmpty()) {
                                builder.addAction(Arrays.asList(buildDeleteAction(documents)));
                            }
                            builder.defaultType(type);
                            client.execute(builder.build());
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("构建索引败:" + LogUtils.format("indexParams", mapParams), e);
            throw new IOException("构建索引败", e);
        } 
    }
    
    /**
     * 构建批量创建索引动作
     * 
     * @param documents
     *            文档列表
     * @return Index[] 返回添加动作
     */
    private Index[] buildIndexAction(List<ESDto> documents) {
        Index[] indexs = new Index[documents.size()];
        for (int i = 0; i < documents.size(); i++) {
            ESDto bean = documents.get(i);
            bean.setCreateTime(DateUtils.getCurrTime());
            indexs[i] = new Index.Builder(bean).build();
        }
        return indexs;
    }

    /**
     * 构建批量删除动作
     * 
     * @param documents
     *            文档列表
     * @return Delete[] 返回删除动作
     */
    private Delete[] buildDeleteAction(List<ESDto> documents) {
        Delete[] deleteAction = new Delete[documents.size()];
        for (int i = 0; i < documents.size(); i++) {
            ESDto bean = documents.get(i);
            deleteAction[i] = new Delete.Builder(bean.getId()).build();
        }
        return deleteAction;
    }

    /**
     * 创建索引
     * 
     * @param index
     *            索引名称
     * @param type
     *            索引类型
     */
    private void createIndexMapping(String index, String type) {
        try {
            logger.debug("params: " + LogUtils.format("index", index, "type", type));
            String mappingJsonStr = buildMappingJsonStr(type);
            PutMapping putMapping = new PutMapping.Builder(index, type, mappingJsonStr).build();
            client.execute(putMapping);
        } catch (IOException e) {
            logger.error("创建索引映射失败 : " + LogUtils.format("index", index, "type", type), e);
        }
    }

    /**
     * 
     * "1364": { "_timestamp": { "enabled": true, "store": true, "path":
     * "createTime", "format": "yyyy-MM-dd HH:mm:ss" }, "properties": { "id": {
     * "type": "string" }, "createTime": { "store": true, "format":
     * "yyyy-MM-dd HH:mm:ss", "type": "date" }, "title": { "type": "string" },
     * "description": { "store": true, "analyzer": "ik", "boost": 4,
     * "term_vector": "with_positions_offsets", "type": "string", "fields": {
     * "py": { "store": true, "analyzer": "pinyin_analyzer", "boost": 4,
     * "term_vector": "with_positions_offsets", "type": "string" } } }, "path":
     * { "type": "string" } }, "_all": { "auto_boost": true } }
     * 通过一个根映射器构建器去设置各个字段来构建映射源
     * 
     * @param index
     *            索引名称
     * @param type
     *            索引类型
     * @return String 映射字符串
     * @throws IOException 
     */
    private String buildMappingJsonStr(String type) throws IOException {
        try {
            XContentBuilder content = XContentFactory
                    .jsonBuilder()
                    .startObject()
                    // 索引库名（类似数据库中的表）
                    .startObject(type).startObject("_timestamp").field("enabled", true).field("store", "yes").field("path", ES_CREATETIME)
                    .field("format", "yyyy-MM-dd HH:mm:ss").endObject().startObject("properties").startObject("id").field("type", "string")
                    .endObject().startObject(ES_TITLE).field("type", "string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject().startObject(ES_PATH).field("type", "string").endObject()
                    .startObject(ES_NAME).field("type", "string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
                    .startObject(ES_CREATETIME).field("type", "date").field("store", "yes").field("format", "yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject(ES_DESCRIPTION).field("type", "multi_field").field("index", "analyzed").startObject("fields")
                    .startObject(ES_DESCRIPTION).field("type", "string").field("store", "no").field("index", "analyzed")
                    .field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").field("term_vector", "with_positions_offsets")
                    .field("boost", 4.0)
                    // 打分(默认1.0)
                    .endObject().startObject("py").field("type", "string").field("store", "no").field("index", "analyzed")
                    .field("indexAnalyzer", "pinyin_analyzer").field("searchAnalyzer", "pinyin_analyzer")
                    .field("term_vector", "with_positions_offsets").field("include_in_all", "false").field("boost", 4.0) // 打分(默认1.0)
                    .endObject().endObject().endObject().endObject().endObject().endObject();
            logger.debug(LogUtils.format("----------------映射字符串---------------", content.string()));
            return content.string();
        } catch (IOException e) {
            logger.error("构建映射字符串失败 " + LogUtils.format("type", type), e);
            throw new IOException("构建映射字符串失败", e);
        }
    }

    /**
     * 根据关键字带分页的搜索文档
     * 
     * @param query
     *            搜索关键字
     * @param indexNames
     *            索引名称列表，可以搜索多个索引库
     * @param indexTypes
     *            索引类型，可以搜索多个索引类型
     * @param pageNow
     *            当前页no
     * @param pageSize
     *            每页显示数量
     * @return List<ESDto> eSDto列表
     * @throws IOException 
     */
    public List<ESDto> documentSearch(String query, List<String> indexNames, List<String> indexTypes, Integer pageNow, Integer pageSize) throws IOException {
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            // 使用query_string进行搜索
            QueryStringQueryBuilder queryStrBuilder = QueryBuilders.queryString(query);
            // 是否允许开头为通配符,是否分析通配符
            queryStrBuilder.analyzeWildcard(true).allowLeadingWildcard(true).field(ES_DESCRIPTION).field("description.py").field(ES_TITLE).field(ES_NAME)
                    .defaultOperator(org.elasticsearch.index.query.QueryStringQueryBuilder.Operator.OR);
            SearchSourceBuilder ssb = searchSourceBuilder.query(queryStrBuilder);
            // 设置分页
            ssb.from((pageNow-1)*pageSize).size(pageSize);
            TermsBuilder termsBuilder = new TermsBuilder("groupById");
            // 设置聚合
            termsBuilder.field(ES_ID);
            ssb.aggregation(termsBuilder);
            // 设置高亮
            HighlightBuilder highlight = SearchSourceBuilder.highlight();
            highlight.preTags("<b>").postTags("</b>").field(ES_DESCRIPTION);
            ssb.highlight(highlight);
            // 设置排序方式
            ssb.sort("_score", SortOrder.DESC);

            if (logger.isDebugEnabled()) {
                logger.debug("requestParams:" + ssb.toString());
            }
            Builder builder = new Search.Builder(ssb.toString());
            // 构建搜索
            Search search = builder.addIndex(indexNames).addType(indexTypes).allowNoIndices(true).ignoreUnavailable(true).setSearchType(SearchType.QUERY_THEN_FETCH).build();
            SearchResult result = client.execute(search);
            System.out.println("记录数：---------------------------" + result.getTotal());
            logger.debug("search result response: " + result.getJsonString());
            List<ESDto> esDtos = new ArrayList<ESDto>();
            if (null != result && result.isSucceeded()) {
                List<Hit<ESDto, Void>> hits = result.getHits(ESDto.class);
                for (Hit<ESDto, Void> hit : hits) {
                    ESDto source = hit.source;
                    Map<String, List<String>> hl = hit.highlight;
                    if (null != hl) {
                        List<String> list = hl.get(ES_DESCRIPTION);
                        source.setDescription(list.get(0));
                    }
                    esDtos.add(source);
                }
                logger.debug(LogUtils.format("esDtos", esDtos));
            } else {
                logger.debug("查询失败 :" + result.getErrorMessage());
            }
            // 去重
//            List<ESDto> newList = CollectionUtils.removeDuplicateWithOrder(esDtos);
            return esDtos;
        } catch (IOException e) {
            logger.error("查询失败 " + LogUtils.format("query", query, "indexNames", indexNames, "indexTypes", indexTypes), e);
            throw new IOException("查询失败 ", e);
        }
    }

    /**
     * 根据id删除指定的文档
     * 
     * @param indexName
     *            索引名称
     * @param indexType
     *            索引类型
     * @param docId
     *            文档id
     * @return boolean
     * @throws IOException 
     */
    @Override
    public boolean deleteDoc(String indexName, String indexType, String docId) throws IOException {
        JestResult result = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("indexName:" + indexName + " indexType:" + indexType + " docId:" + docId);
            }
            Delete d = new Delete.Builder(docId).index(indexName).type(indexType).build();
            result = client.execute(d);
            if (null != result && result.isSucceeded()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error("删除索引失败", e);
            throw new IOException("删除索引失败", e);
        }
    }

    /**
     * 创建索引
     * 
     * @param indexName
     *            索引名称
     * @return boolean
     * @throws IOException
     */
    public boolean addIndex(String indexName) throws IOException{
        JestResult result = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("indexName:" + indexName);
            }
            org.elasticsearch.common.settings.ImmutableSettings.Builder buildIndexSetting = this.buildIndexSetting();
            io.searchbox.indices.CreateIndex.Builder bIndex = new CreateIndex.Builder(indexName);
            CreateIndex cIndex = bIndex.settings(buildIndexSetting.build().getAsMap()).build();
            result = client.execute(cIndex);
            if (null != result && result.isSucceeded()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error("创建索引失败", e);
            throw new IOException("创建索引失败", e);
        }
    }

    /**
     * "analysis": { "analyzer": { "pinyin_analyzer": { "filter": [ "standard",
     * "nGram" ], "tokenizer": [ "my_pinyin" ] } }, "tokenizer": { "my_pinyin":
     * { "padding_char": "", "type": "pinyin", "first_letter": "only" } } }
     * 构建索引设置
     * 
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public ImmutableSettings.Builder buildIndexSetting() throws IOException {
        try {
            ImmutableSettings.Builder settingsBuilder = customImmutableSetting.getBuilder();
            XContentBuilder settings = XContentFactory.jsonBuilder().startObject().startObject("analysis").startObject("analyzer")
                    .startObject("pinyin_analyzer").startArray("filter").value("standard").value("word_delimiter").endArray().startArray("tokenizer")
                    .value("my_pinyin").endArray().endObject().endObject().startObject("tokenizer").startObject("my_pinyin").field("type", "pinyin")
                    .field("first_letter", "none").field("padding_char", "").endObject().endObject().endObject();
            settingsBuilder.put(JsonUtils.jsonToObject(settings.string(), Map.class));
            logger.debug(LogUtils.format("settings", settings.string()));
            return settingsBuilder;
        } catch (IOException e) {
            logger.error("构建索引配置失败", e);
            throw new IOException("构建索引配置失败");
        }
    }

    /**
     * 删除索引
     * 
     * @param indexName
     *            索引名称
     * @return boolean
     * @throws IOException
     */
    @Override
    public boolean deleteIndex(String indexName) throws IOException {
        JestResult result = null;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("indexName:" + indexName);
            }
            DeleteIndex dIndex = new DeleteIndex.Builder(indexName).build();
            result = client.execute(dIndex);
            if (null != result && result.isSucceeded()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.error("删除索引失败", e);
            throw new IOException("删除索引失败", e);
        }
    }

    @Override
    public void documentHandler(List<ESDto> documents, List<String> organkeys, Integer action) throws IOException {
    }

    @Override
    public List<ESDto> documentSearch(String keywords, List<String> organkeys, Integer pageNow, Integer pageSize) throws IOException {
        return Collections.emptyList();
    }

}
