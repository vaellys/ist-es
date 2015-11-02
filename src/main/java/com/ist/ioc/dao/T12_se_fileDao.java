package com.ist.ioc.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class T12_se_fileDao extends JdbcDaoSupport {
    /**
     * 定时器调用此方法，查询所有存在关联表中的sql，然后定时执行
     * 
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> querySqlForList() {
        String sql = "select db.id, db.sql, a.organ_key as organkey from t12_es_organ_index_asso a, t12_se_file_db db where db.id = a.es_id";
        return this.getJdbcTemplate().queryForList(sql);
    }

    /**
     * 定时器调用此方法，查询所有存在关联表中的dir，然后定时执行
     * 
     * @return
     */
    public List<Map<String, Object>> queryDirForList() {
        String sql = "select path.id, path.path, a.organ_key as organkey from t12_es_organ_index_asso a, t12_se_file_path path where path.id = a.es_id";
        return this.getJdbcTemplate().queryForList(sql);
    }
}
