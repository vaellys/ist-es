package com.ist.ioc.service.common.elasticsearch;

import java.util.List;
import java.util.Map;

public interface T00_organ_dimService {

    /**
     * 根据机构key查询机构层次
     * 
     * @param organkeys
     *            机构key列表
     * @return List<T00_organ_dimDTO>
     */
    public List<Map<String, Object>> getOrganDimByOrgankeys(List<String> organkeys);

    /**
     * 查询所有的机构key
     * 
     * @return
     */
    public List<String> getOrganByOrgankeys();

}
