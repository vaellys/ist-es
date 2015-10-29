package com.ist.ioc.service.common.elasticsearch.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ist.ioc.dao.T12_se_fileDao;
import com.ist.ioc.service.common.elasticsearch.T12_se_fileService;

public class T12_se_fileServiceImpl implements T12_se_fileService {

    @Autowired
    private T12_se_fileDao t12_se_fileDao;

    @Override
    public List<Map<String, Object>> querySqlForList() {
        return t12_se_fileDao.querySqlForList();
    }

    @Override
    public List<Map<String, Object>> queryDirForList() {
        return t12_se_fileDao.queryDirForList();
    }

    

}
