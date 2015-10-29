package com.ist.ioc.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.ist.ioc.service.common.Constants.ES_ADD_ACTION;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_ID;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_ORGANKEY;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_PATH;
import static com.ist.ioc.service.common.Constants.ES_QUARTZ_SQL;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ist.dto.bmp.ESDto;
import com.ist.ioc.service.common.elasticsearch.IESService;
import com.ist.ioc.service.common.elasticsearch.T12_se_fileService;

public class ReimportScheduler implements Job {
    
    @Autowired
    T12_se_fileService t12_se_fileService;
    @Autowired
    IESService iesService;
    
    private static final Log log = LogFactory.getLog(ReimportScheduler.class);
    
    public ReimportScheduler(){
        start();
    }
    
    public static void start()  {
        try {
            if(log.isInfoEnabled())
            log.info("start ReimportScheduler");
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            JobDetail userDeltaImportJob = newJob(ReimportScheduler.class).withIdentity("userDeltaImportJob", "group1").build();
            
            CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("*/1 * * * * ?")).build();
            
            sched.scheduleJob(userDeltaImportJob, trigger);
            sched.start();
        } catch (SchedulerException e) {
            log.debug("", e);
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            if(log.isInfoEnabled())
            log.info("reimport requirement and user index");
            dirQuartz();
            sqlQuartz();
        } catch (IOException e) {
            log.error("" ,e);
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
