package com.ist.ioc.service.common.elasticsearch.impl;

import static com.ist.ioc.service.common.Constants.ES_ATTACHMENT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.ist.dto.bmp.T12_es_organ_index_assoDto;
import com.ist.ioc.service.common.elasticsearch.T12_es_organ_index_assoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-es.xml"})
public class T12_es_organ_index_assoDaoTest {
    private static final Log logger = LogFactory.getLog(T12_es_organ_index_assoDaoTest.class);
    @Resource
    T12_es_organ_index_assoService t12_es_organ_index_assoService;
    
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
    public void testAdd() {
        T12_es_organ_index_assoDto dto = new T12_es_organ_index_assoDto();
        dto.setEs_id("12");
        dto.setEs_index("9908");
        dto.setOrgankey("12");
        dto.setEs_type("1");
        try {
            boolean add = t12_es_organ_index_assoService.add(dto);
            assertEquals(true, add);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDelete() {
        try {
            boolean delete = t12_es_organ_index_assoService.delete("12", ES_ATTACHMENT_TYPE);
            assertEquals(true, delete);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testUpdate() {
        T12_es_organ_index_assoDto dto = new T12_es_organ_index_assoDto();
        dto.setEs_id("11");
        dto.setOrgankey("1360");
        dto.setEs_type(ES_ATTACHMENT_TYPE);
        dto.setEs_index("1300");
        boolean update = t12_es_organ_index_assoService.update(dto);
        assertEquals(true, update);
    }
    
    @Test
    public void testQuery() {
        List<T12_es_organ_index_assoDto> query = t12_es_organ_index_assoService.query("11", ES_ATTACHMENT_TYPE);
        logger.debug(LogUtils.format("r", query));
        assertNotNull(query);
    }
    
    @Test
    public void testQueryByOrgankey() {
        List<String> organkeys = new ArrayList<String>();
        organkeys.add("1360");
        organkeys.add("1361");
        List<T12_es_organ_index_assoDto> query = t12_es_organ_index_assoService.queryByOrgankey(organkeys );
        logger.debug(LogUtils.format("r", query));
        assertNotNull(query);
    }

}
