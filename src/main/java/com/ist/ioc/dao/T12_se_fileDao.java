package com.ist.ioc.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class T12_se_fileDao extends JdbcDaoSupport{
    public List<Map<String, Object>> querySqlForList(){
        String sql = "select db.id, db.sql, a.organ_key as organkey from t12_es_organ_index_asso a, t12_se_file_db db where db.id = a.es_id";
        return this.getJdbcTemplate().queryForList(sql);
    }
    
    public List<Map<String, Object>> queryDirForList(){
        String sql = "select path.id, path.path, a.organ_key as organkey from t12_es_organ_index_asso a, t12_se_file_path path where path.id = a.es_id";
        return this.getJdbcTemplate().queryForList(sql);
    }
}
