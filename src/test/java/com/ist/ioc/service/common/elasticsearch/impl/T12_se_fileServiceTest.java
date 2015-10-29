package com.ist.ioc.service.common.elasticsearch.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ist.common.es.util.LogUtils;
import com.ist.ioc.service.common.elasticsearch.T12_se_fileService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-es.xml"})
public class T12_se_fileServiceTest {

    private static final Log logger = LogFactory.getLog(T12_se_fileServiceTest.class);
    @Resource
    T12_se_fileService t12_se_fileService;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testQuerySqlForList() {
       List<Map<String, Object>> sqlList = t12_se_fileService.querySqlForList();
       logger.debug(LogUtils.format("sql", sqlList));
       assertNotNull(sqlList);
    }

    @Test
    public void testQueryDirForList() {
        List<Map<String, Object>> dirList = t12_se_fileService.queryDirForList();
        logger.debug(LogUtils.format("dir", dirList));
        assertNotNull(dirList);
    }

}
