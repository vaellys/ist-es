package com.ist.ioc.service.common.elasticsearch.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ist.dto.bmp.T12_es_organ_index_assoDto;
import com.ist.ioc.dao.T12_es_organ_index_assoDao;
import com.ist.ioc.service.common.elasticsearch.T12_es_organ_index_assoService;

public class T12_es_organ_index_assoServiceImpl implements T12_es_organ_index_assoService {

    @Autowired
    private T12_es_organ_index_assoDao t12_es_organ_index_assoDao;

    @Override
    public boolean add(T12_es_organ_index_assoDto dto) throws SQLException {
        return t12_es_organ_index_assoDao.add(dto);
    }

    @Override
    public boolean delete(String id, String type) throws SQLException {
        return t12_es_organ_index_assoDao.delete(id, type);
    }

    @Override
    public List<T12_es_organ_index_assoDto> query(String id, String type) {
        return t12_es_organ_index_assoDao.query(id, type);
    }
    
    @Override
    public boolean update(T12_es_organ_index_assoDto dto){
        return t12_es_organ_index_assoDao.update(dto);
    }
    
    @Override
    public List<T12_es_organ_index_assoDto> queryByOrgankey(List<String> organkeys){
        return t12_es_organ_index_assoDao.queryByOrgankey(organkeys);
    }
}
