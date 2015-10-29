package com.ist.ioc.service.common.elasticsearch;

import java.util.List;
import java.util.Map;

public interface T12_se_fileService {
    
    public List<Map<String, Object>> querySqlForList();
    
    public List<Map<String, Object>> queryDirForList();
}
