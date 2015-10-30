package com.ist.ioc.service.common.elasticsearch.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ist.dto.bmp.ESDto;
import com.ist.ioc.service.common.elasticsearch.IESService;

import static com.ist.ioc.service.common.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-es.xml"})
public class IEServiceImplTest extends AbstractJUnit4SpringContextTests {

	@Resource
	private IESService iesService;

	private Log logger = LogFactory.getLog(this.getClass());

	@Before
	public void setUp() throws Exception {
		// iess = new IESServiceImpl();
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testDocumentHandler() {
	    List<ESDto> documents = new ArrayList<ESDto>();
	    ESDto dto = new ESDto();
	    dto.setId("1");
	    dto.setPath("软件需求规格说明书 - 全文检索.docx");
	    documents.add(dto);
	    
        List<String> organkeys = new ArrayList<String>();
        organkeys.add("1361");
        organkeys.add("1360");
        try {
            iesService.documentHandler(documents, organkeys, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@Test
    public void testDocumentHandlerPublic() {
        List<ESDto> documents = new ArrayList<ESDto>();
        ESDto dto = new ESDto();
        dto.setId("1");
        dto.setPath("软件需求规格说明书 - 全文检索.docx");
        documents.add(dto);
        try {
            iesService.documentHandler(documents, null, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Test
    public void testDeleteDoc() {
        try {
            iesService.deleteDoc("1", ES_ATTACHMENT_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	@Test
    public void testDeleteIndex() {
        try {
            iesService.deleteIndex("1300");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	@Test
	public void testSQLHandler() {
	    ESDto dto = new ESDto();
	    dto.setId("sql_fdsfvdsaffdsa");
	    dto.setSql("select organname as title, organkey, organname as description from T00_ORGAN_DIM");
	    List<String> organkeys = new ArrayList<String>();
        organkeys.add("0552");
        organkeys.add("0553");
	    try {
	        iesService.sqlHandler(dto, organkeys, 1);
	    }catch(IOException e){
	        e.printStackTrace();
	    }
	}
	
	@Test
    public void testSQLHandlerPublic() {
        ESDto dto = new ESDto();
        dto.setId("sql_hdvaefffsqgbsig");
        dto.setSql("select username as id, realname as title, oausername as description from t00_user");
        try {
            iesService.sqlHandler(dto, null, 1);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testUpdateSqlHandler() {
        ESDto dto = new ESDto();
        dto.setId("sql_hdvaefffsqgbsig");
        dto.setSql("select organkey  as id, organname as title, organsf as description from t00_organ");
        try {
            iesService.sqlHandler(dto, null, 2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testDeleteSQLHandler() {
        try {
            iesService.deleteDoc("1283", ES_SQL_TYPE);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testDirHandler() {
	    for (int i = 1; i < 3; i++) {
	        ESDto dto = new ESDto();
	        dto.setId("1283" + i);
	        dto.setPath("C:\\awp_data\\upload");
	        
	        List<String> organkeys = new ArrayList<String>();
	        organkeys.add("110000");
	        organkeys.add("110001");
	        try {
	            iesService.dirHandler(dto, organkeys, 1);
	        }catch(IOException e){
	            e.printStackTrace();
	        }
        }
    }
	
	@Test
    public void testDirHandlerPublic() {
        ESDto dto = new ESDto();
        dto.setId("path_fdsfvdsaffdsa");
        dto.setPath("C:\\awp_data\\upload");
        try {
            iesService.dirHandler(dto, null, 1);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testDeleteDirHandler() {
        try {
            iesService.deleteDoc("12832", ES_DIR_TYPE);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testUpdateDirHandler() {
        ESDto dto = new ESDto();
        dto.setId("path_fdsfvdsaffdsa");
        dto.setPath("C:\\awp_data\\upload");
        try {
            List<String> organkeys = new ArrayList<String>();
            organkeys.add("110000");
            organkeys.add("110001");
            iesService.dirHandler(dto, organkeys, 2);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
	@Test
    public void testDocumentSearch() {
        try {
            List<String> organkeys = new ArrayList<String>();
            organkeys.add("110000");
            String keywords = "开发";
            iesService.documentSearch(keywords , organkeys, 1, 10);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
	
}
