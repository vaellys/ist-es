package com.ist.ioc.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Test extends TimerTask{
    @Override
    public void run() {
           System.out.println("定时器开始执行：" + new Date());
    }
    
    
    
    public static void main(String[] args) {
        
        //设置执行时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        calendar.set(year, month, day, 13, 07, 00);
        Date date = calendar.getTime();
        
        Timer timer = new Timer();
        timer.schedule(new Test(), date);
    }
   
}
