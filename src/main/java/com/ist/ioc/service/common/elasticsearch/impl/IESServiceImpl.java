package com.ist.ioc.service.common.elasticsearch.impl;

import static com.ist.ioc.service.common.Constants.ES_ADD_ACTION;
import static com.ist.ioc.service.common.Constants.ES_ATTACHMENT_TYPE;
import static com.ist.ioc.service.common.Constants.ES_DIR_PREFIX;
import static com.ist.ioc.service.common.Constants.ES_DIR_TYPE;
import static com.ist.ioc.service.common.Constants.ES_ORGANKEY_ZONGHANG;
import static com.ist.ioc.service.common.Constants.ES_PUBLIC_ATTACHMENT_TYPE;
import static com.ist.ioc.service.common.Constants.ES_PUBLIC_INDEX;
import static com.ist.ioc.service.common.Constants.ES_SQL_PREFIX;
import static com.ist.ioc.service.common.Constants.ES_SQL_TYPE;
import static com.ist.ioc.service.common.Constants.ES_UPDATE_ACTION;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ist.common.es.util.CollectionUtils;
import com.ist.common.es.util.FileUtils;
import com.ist.common.es.util.LogUtils;
import com.ist.common.es.util.ParseDocumentUtil;
import com.ist.dto.bmp.ESDto;
import com.ist.dto.bmp.T12_es_organ_index_assoDto;
import com.ist.ioc.service.common.Constants;
import com.ist.ioc.service.common.elasticsearch.AbstractIESService;
import com.ist.ioc.service.common.elasticsearch.EsSqlHandlerService;
import com.ist.ioc.service.common.elasticsearch.T00_organ_dimService;
import com.ist.ioc.service.common.elasticsearch.T12_es_organ_index_assoService;

public class IESServiceImpl extends AbstractIESService {
    private static final Log logger = LogFactory.getLog(IESServiceImpl.class);

    @Autowired
    private T00_organ_dimService t00_organ_dimService;

    @Autowired
    private EsSqlHandlerService esSqlHandlerService;

    @Autowired
    private T12_es_organ_index_assoService t12_es_organ_index_assoService;

    /**
     * 批量创建索引 文档对象所代表的dto要指定索引文档的id 一般将对象本身的id设置为要索引文档的id 设置方式以注解的形式：@JestId
     * 上传附件：id path private int id;
     * 
     * @param documents
     *            要索引的结果集
     * @param organkeys
     *            机构key列表
     * @throws IOException
     */
    public void documentHandler(List<ESDto> documents, List<String> organkeys, Integer action) throws IOException {
        try {
            List<ESDto> list = new ArrayList<ESDto>();
            for (ESDto bean : documents) {
                // 获取文件上传的目录
                String dir = sysConfigEs.getProperty("data.import");
                String url = dir + bean.getPath();
                String text = ParseDocumentUtil.getText(url);
                String fileName = FileUtils.getFileNameByUrl(url);
                bean.setDescription(text);
                bean.setName(fileName);
                list.add(bean);
            }
            // 当机构没有填时，上传到公共库
            if (null == organkeys || organkeys.isEmpty()) {
                publicIndexHandler(documents, action, ES_PUBLIC_ATTACHMENT_TYPE);
            } else {
                indexHandler(documents, organkeys, action, ES_ATTACHMENT_TYPE, null, false, false);
            }
        } catch (IOException e) {
            logger.error("批量生成索引失败: " + LogUtils.format("documents", documents, "organkeys", organkeys), e);
            throw new IOException("批量生成索引失败", e);
        }
    }

    /**
     * 附件上传时创建公共库并建立索引
     * 
     * @param documents
     * @param action
     * @param publicType
     * @throws IOException
     */
    private void publicIndexHandler(List<ESDto> documents, Integer action, String publicType) throws IOException {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        list.add(publicType);
        map.put(Constants.ES_PUBLIC_INDEX, list);
        this.documentHandler(map, documents, action);
    }

    /**
     * 对文档进行索引
     * 
     * @param documents
     * @param organkeys
     * @param action
     * @param type
     *            1 自定义sql 2 附件 3 文档目录
     * @param id
     *            文档id
     * @param isHead
     *            是否公共库
     * @param isQuartz
     *            是否定时任务
     * @throws IOException
     */
    private void indexHandler(List<ESDto> documents, List<String> organkeys, Integer action, String type, String id, boolean isHead, boolean isQuartz)
            throws IOException {
        try {
            Map<String, List<String>> dataMaps = new HashMap<String, List<String>>();
            // this.documentHandler(maps, documents, action);
            if (ES_ATTACHMENT_TYPE.equals(type)) {
                List<Map<String, Object>> result = t00_organ_dimService.getOrganDimByOrgankeys(organkeys);
                Map<String, List<String>> maps = CollectionUtils.classify(result, "LEVEL_CODE_2", "LEVEL_CODE_3");
                logger.debug(LogUtils.format("分类后的结果：", maps));
                this.documentHandler(maps, documents, action);
                for (Map<String, Object> map : result) {
                    for (ESDto dto : documents) {
                        insertOrganIndexAsso(dto.getId(), map, ES_ATTACHMENT_TYPE, isQuartz);
                    }
                }
            } else if (ES_SQL_TYPE.equals(type)) {
                dataMaps.put(ES_SQL_PREFIX + id, organkeys);
                // this.documentHandler(dataMaps, documents, action);
                indexHead(organkeys, id, isHead, null, ES_SQL_TYPE, isQuartz);
            } else if (ES_DIR_TYPE.equals(type)) {
                dataMaps.put(ES_DIR_PREFIX + id, organkeys);
                // this.documentHandler(dataMaps, documents, action);
                indexHead(organkeys, id, isHead, null, ES_DIR_TYPE, isQuartz);
            }
            if (ES_SQL_TYPE.equals(type) || ES_DIR_TYPE.equals(type)) {
                this.documentHandler(dataMaps, documents, action);
            }
        } catch (SQLException e) {
            logger.error("插入失败", e);
            e.printStackTrace();
        }
    }

    private void indexHead(List<String> organkeys, String id, boolean isHead, List<Map<String, Object>> result, String type, boolean isQuartz)
            throws SQLException {
        if (isHead) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("LEVEL_CODE_3", organkeys.get(0));
            insertOrganIndexAsso(id, map, type, isQuartz);
        } else {
            for (String organkey : organkeys) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("LEVEL_CODE_3", organkey);
                insertOrganIndexAsso(id, map, type, isQuartz);
            }
        }
    }

    private void insertOrganIndexAsso(String id, Map<String, Object> map, String type, boolean isQuartz) throws SQLException {
        if (isQuartz) {
            return;
        }
        T12_es_organ_index_assoDto bean = new T12_es_organ_index_assoDto();
        bean.setEs_id(id);
        bean.setOrgankey((String) map.get("LEVEL_CODE_3"));
        bean.setEs_type(type);
        if (ES_ATTACHMENT_TYPE.equals(type)) {
            bean.setEs_index((String) map.get("LEVEL_CODE_2"));
        } else if (ES_SQL_TYPE.equals(type)) {
            bean.setEs_index(ES_SQL_PREFIX + id);
        } else if (ES_DIR_TYPE.equals(type)) {
            bean.setEs_index(ES_DIR_PREFIX + id);
        }
        t12_es_organ_index_assoService.add(bean);
    }

    /**
     * <p>
     * 根据查询关键字带分页的搜索文档
     * </p>
     * <p>
     * 机构key表去查询对应的分行与支行
     * </p>
     * 
     * @param keywords
     *            搜索关键词
     * @param organkeys
     *            机构码列表
     * @param pageNow
     *            当前页no
     * @param pageSize
     *            每页显示数量
     * @return
     * @throws IOException
     */
    public List<ESDto> documentSearch(String keywords, List<String> organkeys, Integer pageNow, Integer pageSize) throws IOException {
        try {
            List<String> indexNames = new ArrayList<String>();
            List<String> indexTypes = new ArrayList<String>();
            List<T12_es_organ_index_assoDto> result = t12_es_organ_index_assoService.queryByOrgankey(organkeys);
            // List<Map<String, Object>> result =
            // t00_organ_dimService.getOrganDimByOrgankeys(organkeys);
            // Map<String, List<String>> maps = CollectionUtils.classify(result,
            // "LEVEL_CODE_2", "LEVEL_CODE_3");
            for (T12_es_organ_index_assoDto dto : result) {
                String index = StringUtils.lowerCase(dto.getEs_index());
                indexNames.add(index);
                String type = dto.getOrgankey();
                indexTypes.add(type);
            }
            // 加上公共库
            indexNames.add(ES_PUBLIC_INDEX);
            indexTypes.add(ES_PUBLIC_ATTACHMENT_TYPE);
            String organkey = sysConfigEs.getProperty(ES_ORGANKEY_ZONGHANG);
            String sqlIndex = ES_SQL_PREFIX + organkey;
            String dirIndex = ES_DIR_PREFIX + organkey;
            indexNames.add(sqlIndex);
            indexNames.add(dirIndex);
            indexTypes.add(organkey);
            logger.debug(LogUtils.format("results", result));
            List<ESDto> searchList = this.documentSearch(keywords, indexNames, indexTypes, pageNow, pageSize);
            return searchList;
        } catch (IOException e) {
            logger.error("搜索失败: " + LogUtils.format("keywords", keywords, "organkeys", organkeys, "pageNow", pageNow, "pageSize", pageSize), e);
            throw new IOException("搜索失败", e);
        }
    }

    /**
     * 处理自定义sql情况，文档中需要指定机构key字段，表示其所属的机构
     * 
     * @param documents
     *            索引文档列表
     * @param action
     *            处理文档动作 1:创建 2:删除
     * @throws IOException
     */
    @Override
    public void sqlHandler(ESDto esDto, List<String> organkeys, Integer action) throws IOException {
        try {
            if (null != esDto) {
                if (ES_ADD_ACTION == action) {
                    sqlIndexHandler(esDto, organkeys, action);
                } else if (ES_UPDATE_ACTION == action) {
                    // 先删除关联表与索引库
                    boolean deleteDoc = deleteDoc(esDto.getId(), ES_SQL_TYPE);
                    if (deleteDoc) {
                        sqlIndexHandler(esDto, organkeys, ES_ADD_ACTION);
                    }
                }
                // if(null != documents && !documents.isEmpty()){
                // for (ESDto esDto : documents) {
                // List<String> list = new ArrayList<String>();
                // List<ESDto> esDtoList = new ArrayList<ESDto>();
                // organkey = esDto.getOrgankey();
                // if(!StringUtils.isBlank(organkey)){
                // esDtoList.add(esDto);
                // list.add(organkey);
                // Map<String, List<String>> maps = indexHandler(esDtoList,
                // list, action);
                // logger.debug(LogUtils.format("results", maps));
                // }else{
                // esDtoList.add(esDto);
                // publicIndexHandler(esDtoList, action);
                // }
                // }
                // }
            }
        } catch (IOException e) {
            logger.error("批量生成索引失败: " + LogUtils.format("sql", esDto.getSql()), e);
            throw new IOException("批量生成索引失败", e);
        }
    }

    private void sqlIndexHandler(ESDto esDto, List<String> organkeys, Integer action) throws IOException {
        List<ESDto> documents = esSqlHandlerService.sqlHandler(esDto.getSql(), organkeys);
        indexDocument(esDto, organkeys, action, ES_SQL_TYPE, documents);
    }

    private void indexDocument(ESDto esDto, List<String> organkeys, Integer action, String type, List<ESDto> documents) throws IOException {
        if (null != organkeys && !organkeys.isEmpty()) {
            indexHandler(documents, organkeys, action, type, esDto.getId(), false, esDto.isQuartz());
        } else {
            // publicIndexHandler(documents, action, publicType);
            // List<String> list = t00_organ_dimService.getOrganByOrgankeys();
            // indexHandler(documents, list, action, type, esDto.getId());
            String organkey = sysConfigEs.getProperty(ES_ORGANKEY_ZONGHANG);
            List<String> keys = new ArrayList<String>();
            keys.add(organkey);
            indexHandler(documents, keys, action, type, esDto.getId(), true, esDto.isQuartz());
        }
    }

    @Override
    public void dirHandler(ESDto esDto, List<String> organkeys, Integer action) throws IOException {
        try {
            if (null != esDto) {
                if (ES_ADD_ACTION == action) {
                    dirIndexHandler(esDto, organkeys, action);
                } else if (ES_UPDATE_ACTION == action) {
                    // 先删除关联表与索引库
                    boolean deleteDoc = deleteDoc(esDto.getId(), ES_DIR_TYPE);
                    if (deleteDoc) {
                        dirIndexHandler(esDto, organkeys, ES_ADD_ACTION);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("批量生成索引失败: " + LogUtils.format("sql", esDto.getSql()), e);
            throw new IOException("批量生成索引失败", e);
        } catch (Exception e) {
            logger.error("索引失败", e);
            e.printStackTrace();
        }
    }

    private void dirIndexHandler(ESDto esDto, List<String> organkeys, Integer action) throws Exception, IOException {
        List<File> listFiles = FileUtils.getListFiles(esDto.getPath(), null);
        List<ESDto> documents = new ArrayList<ESDto>();
        for (File file : listFiles) {
            String text = ParseDocumentUtil.getText(file.getAbsolutePath());
            ESDto dto = new ESDto();
            dto.setDescription(text);
            dto.setName(file.getName());
            documents.add(dto);
        }
        indexDocument(esDto, organkeys, action, ES_DIR_TYPE, documents);
    }

    public boolean deleteDoc(String id, String type) throws IOException {
        try {
            List<T12_es_organ_index_assoDto> list = t12_es_organ_index_assoService.query(id, type);
            t12_es_organ_index_assoService.delete(id, type);
            if (ES_ATTACHMENT_TYPE.equals(type)) {
                if (null != list) {
                    for (T12_es_organ_index_assoDto dto : list) {
                        // 删除机构下面的附件
                        this.deleteDoc(dto.getEs_index(), dto.getOrgankey(), id);
                    }
                }
                this.deleteDoc(ES_PUBLIC_INDEX, ES_PUBLIC_ATTACHMENT_TYPE, id);
            } else {
                if (null != list) {
                    for (T12_es_organ_index_assoDto dto : list) {
                        // 删除索引库
                        this.deleteIndex(dto.getEs_index());
                        break;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            logger.error("删除文档失败.", e);
            throw e;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
