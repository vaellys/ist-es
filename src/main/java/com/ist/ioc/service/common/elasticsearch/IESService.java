package com.ist.ioc.service.common.elasticsearch;

import java.io.IOException;
import java.util.List;

import com.ist.dto.bmp.ESDto;

/**
 * 搜索服务提供者
 * 
 * @author qianguobing
 * @date 2015-07-17
 */
public interface IESService {

    /**
     * <p>
     * 根据机构Key列表查询对应分行与支行
     * <p>
     * <p>
     * 然后进行文档索引
     * </p>
     * 
     * @param documents
     *            索引文档列表
     * @param organkeys
     *            机构key列表
     * @param action
     *            处理文档动作 1:创建 2:删除
     * @throws IOException
     *             
     */
    public void documentHandler(List<ESDto> documents, List<String> organkeys, Integer action) throws IOException;
    
    /**
     * 处理自定义sql情况，文档中需要指定机构key字段，表示其所属的机构
     * @param documents
     *              索引文档列表
     * @param action
     *              处理文档动作 1:创建 2:删除
     * @throws IOException
     */
    public void sqlHandler(ESDto esDto, List<String> organkeys, Integer action) throws IOException;

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
     * @return List<ESDto>
     * @throws IOException
     */
    public List<ESDto> documentSearch(String keywords, List<String> organkeys, Integer pageNow, Integer pageSize) throws IOException;

    /**
     * 创建索引
     * 
     * @param indexName
     *            索引名称
     * @return boolean
     * @throws Exception
     */
    public boolean addIndex(String indexName) throws IOException;

    /**
     * 删除索引
     * 
     * @param indexName
     *            索引名称
     * @return boolean
     * @throws Exception
     */
    public boolean deleteIndex(String indexName) throws IOException;

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
     */
    public boolean deleteDoc(String indexName, String indexType, String docId) throws IOException;

    /**
     * 根据从索引关联表中查找对应的索引类型进行删除文档
     * @param id 文档id 
     * @param type 1:sql 2:附件 3:目录
     * @return
     * @throws IOException
     */
    public boolean deleteDoc(String id, String type) throws IOException;

    /**
     * 对文档目录进行索引，参数esDto中需要设置id,path(文档目录路径)
     * @param esDto  
     * @param organkeys
     * @param action
     * @throws IOException
     */
    public void dirHandler(ESDto esDto, List<String> organkeys, Integer action) throws IOException;
}
