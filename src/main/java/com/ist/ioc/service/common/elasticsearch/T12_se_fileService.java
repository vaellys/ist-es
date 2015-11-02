package com.ist.ioc.service.common.elasticsearch;

import java.util.List;
import java.util.Map;

public interface T12_se_fileService {
    
    /**
     * 定时器调用此方法，查询所有存在关联表中的sql，然后定时执行
     * 
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> querySqlForList();
    
    /**
     * 定时器调用此方法，查询所有存在关联表中的dir，然后定时执行
     * 
     * @return
     */
    public List<Map<String, Object>> queryDirForList();
}
