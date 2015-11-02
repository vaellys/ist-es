package com.ist.ioc.service.common.elasticsearch;

import java.util.List;
import com.ist.dto.bmp.ESDto;

public interface EsSqlHandlerService {
    /**
     * 自定义sql执行
     * @param sql
     * @param organkeys
     * @return
     */
    public List<ESDto> sqlHandler(String sql, List<String> organkeys);
}
