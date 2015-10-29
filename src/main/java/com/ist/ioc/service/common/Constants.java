package com.ist.ioc.service.common;


public class Constants {
    /**
     * 创建动作
     */
    public static final Integer ES_ADD_ACTION = 1;
    /**
     * 删除动作
     */
    public static final Integer ES_DELETE_ACTION = 3;
    /**
     * 修改动作
     */
    public static final Integer ES_UPDATE_ACTION = 2;
    /**
     * es公共索引库
     */
    public static final String ES_PUBLIC_INDEX = "publicindex";
    /**
     * es公共索引类型
     */
    public static final String ES_PUBLIC_ATTACHMENT_TYPE = "attachment";
    
    /**
     * 索引方式
     */
    public static final String ES_SQL_TYPE = "1";
    public static final String ES_ATTACHMENT_TYPE = "2";
    public static final String ES_DIR_TYPE = "3";
    
    /**
     * 索引库前缀
     */
    public static final String ES_SQL_PREFIX = "s";
    public static final String ES_DIR_PREFIX = "p";
    
    /**
     * 总行
     */
    public static final String ES_ORGANKEY_ZONGHANG = "organkey_zonghang";
    
    /**
     * 定时任务常量
     */
    public static final String ES_QUARTZ_ID = "ID";
    public static final String ES_QUARTZ_SQL = "SQL";
    public static final String ES_QUARTZ_PATH = "PATH";
    public static final String ES_QUARTZ_ORGANKEY = "ORGANKEY";
    
    
    private Constants(){
    }
    /*public static final String ES_MAPPER_PROPERTY_TYPE = "type";
    
    public static final String ES_MAPPER_PROPERTY_TYPE_STRING = "string";
    
    public static final String ES_MAPPER_PROPERTY_TYPE_DATE = "date";
    
    public static final String ES_MAPPER_PROPERTY_TYPE_MULTI_FIELD = "multi_field";
    
    public static final String ES_MAPPER_PROPERTY_INDEX = "index";
    
    public static final String ES_MAPPER_PROPERTY_INDEX_ANALYZED = "analyzed";
    
    public static final String ES_MAPPER_PROPERTY_FIELDS = "fields";
    
    public static final String ES_MAPPER_PROPERTY_STORE = "store";
    
    public static final String ES_MAPPER_PROPERTY_STORE_YES = "yes";
    
    public static final String ES_MAPPER_PROPERTY_FORMAT = "format";
    
    public static final String ES_MAPPER_PROPERTY_FORMAT_VALUE = "yyyy-MM-dd HH:mm:ss";*/
    
    
}