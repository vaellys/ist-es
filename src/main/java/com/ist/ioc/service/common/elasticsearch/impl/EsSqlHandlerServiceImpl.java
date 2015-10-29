package com.ist.ioc.service.common.elasticsearch.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ist.dto.bmp.ESDto;
import com.ist.ioc.dao.EsSqlHandlerDao;
import com.ist.ioc.service.common.elasticsearch.EsSqlHandlerService;

public class EsSqlHandlerServiceImpl implements EsSqlHandlerService {

    @Autowired
    private EsSqlHandlerDao esSqlHandlerDao;
    
    @Override
    public List<ESDto> sqlHandler(String sql, List<String> organkeys) {
        return esSqlHandlerDao.sqlHandler(sql, organkeys);
    }

}
