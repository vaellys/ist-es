package com.ist.ioc.schedule;

import static com.ist.ioc.service.common.Constants.ES_ADD_ACTION;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_ID;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_ORGANKEY;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_PATH;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_SQL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ist.dto.bmp.ESDto;
import com.ist.ioc.service.common.elasticsearch.IESService;
import com.ist.ioc.service.common.elasticsearch.T12_se_fileService;

public class ReimportScheduler extends TimerTask {
    
    @Autowired
    T12_se_fileService t12_se_fileService;
    @Autowired
    IESService iesService;
    
    private static final Log log = LogFactory.getLog(ReimportScheduler.class);
    
    @Override
    public void run() {
        try {
            sqlQuartz();
            dirQuartz();
        } catch (IOException e) {
            log.debug("", e);
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
