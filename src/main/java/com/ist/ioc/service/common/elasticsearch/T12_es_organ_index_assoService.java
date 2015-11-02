package com.ist.ioc.service.common.elasticsearch;

import java.sql.SQLException;
import java.util.List;

import com.ist.dto.bmp.T12_es_organ_index_assoDto;

public interface T12_es_organ_index_assoService {
    /**
     * 增加关联表，添加字段：机构 索引库 id 类型
     * 
     * @param dto
     * @return boolean
     * @throws SQLException
     */
    public boolean add(T12_es_organ_index_assoDto dto) throws SQLException;

    /**
     * 根据id和类型删除关联表
     * 
     * @param id
     * @param type
     *            有三种：1 sql类型 2 附件类型 3 目录类型
     * @return boolean
     * @throws SQLException
     */
    public boolean delete(String id, String type) throws SQLException;

    /**
     * 修改关联表
     * 
     * @param dto
     * @return
     */
    public List<T12_es_organ_index_assoDto> query(String id, String type);

    /**
     * 根据id和type进行查询
     * 
     * @param id
     * @param type
     * @return
     */
    public boolean update(T12_es_organ_index_assoDto dto);

    /**
     * 根据机构keys进行查询
     * 
     * @param organkeys
     * @return
     */
    public List<T12_es_organ_index_assoDto> queryByOrgankey(List<String> organkeys);
}
