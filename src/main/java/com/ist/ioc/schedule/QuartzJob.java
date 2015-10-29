package com.ist.ioc.schedule;

import static com.ist.ioc.service.common.Constants.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ist.dto.bmp.ESDto;
import com.ist.ioc.service.common.elasticsearch.IESService;
import com.ist.ioc.service.common.elasticsearch.T12_se_fileService;

public class QuartzJob {
    private static final Log logger = LogFactory.getLog(QuartzJob.class);
    
    @Autowired
    T12_se_fileService t12_se_fileService;
    @Autowired
    IESService iesService;
    public void work(){
        try {
            sqlQuartz();
            dirQuartz();
        } catch (IOException e) {
            logger.debug("定时执行失败", e);
            e.printStackTrace();
        }
    }
    
    private void sqlQuartz() throws IOException {
        List<Map<String, Object>> sqlList = t12_se_fileService.querySqlForList();
        if(null != sqlList && !sqlList.isEmpty()){
            for (Map<String, Object> map : sqlList) {
                ESDto esDto = new ESDto();
                String id = (String) map.get(ES_QUARTZ_ID);
                String sql = (String) map.get(ES_QUARTZ_SQL);
                String organkey = (String) map.get(ES_QUARTZ_ORGANKEY);
                esDto.setId(id);
                esDto.setSql(sql);
                esDto.setQuartz(true);
                List<String> organkeys = new ArrayList<String>();
                organkeys.add(organkey);
                iesService.sqlHandler(esDto, organkeys, ES_ADD_ACTION);
            }
        }
    }
    
    private void dirQuartz() throws IOException {
        List<Map<String, Object>> dirList = t12_se_fileService.queryDirForList();
        if(null != dirList && !dirList.isEmpty()){
            for (Map<String, Object> map : dirList) {
                ESDto esDto = new ESDto();
                String id = (String) map.get(ES_QUARTZ_ID);
                String path = (String) map.get(ES_QUARTZ_PATH);
                String organkey = (String) map.get(ES_QUARTZ_ORGANKEY);
                esDto.setId(id);
                esDto.setPath(path);
                esDto.setQuartz(true);
                List<String> organkeys = new ArrayList<String>();
                organkeys.add(organkey);
                iesService.dirHandler(esDto, organkeys, ES_ADD_ACTION);
            }
        }
    }
}
