package com.ist.ioc.service.common.elasticsearch;

import java.util.List;
import com.ist.dto.bmp.ESDto;

public interface EsSqlHandlerService {
    public List<ESDto> sqlHandler(String sql, List<String> organkeys);
}
