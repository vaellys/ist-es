package com.ist.ioc.service.common.elasticsearch;

import java.sql.SQLException;
import java.util.List;

import com.ist.dto.bmp.T12_es_organ_index_assoDto;

public interface T12_es_organ_index_assoService {
    
    public boolean add(T12_es_organ_index_assoDto dto) throws SQLException;
    
    public boolean delete(String id, String type) throws SQLException;
    
    public List<T12_es_organ_index_assoDto> query(String id, String type);

    public boolean update(T12_es_organ_index_assoDto dto);

    public List<T12_es_organ_index_assoDto> queryByOrgankey(List<String> organkeys);
}
