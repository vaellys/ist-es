package com.ist.dto.bmp;

import io.searchbox.annotations.JestId;

import java.io.Serializable;

/**
 * elasticsearch搜索和索引实体
 * 
 * @author qianguobing
 */
public class ESDto implements Serializable {
    /**
     * 序列化Id
     */
    private static final long serialVersionUID = 7712247850042953111L;
    /**
     * 文档id 用注解指定es索引id
     */
    @JestId
    private String id;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档摘要
     */
    private String description;
    /**
     * 文档路径
     */
    private String path;
    /**
     * 自定义sql
     */
    private String sql;
    /**
     * 图片地址（保留字段）
     */
    private String imgUrl;
    /**
     * 生成时间
     */
    private String createTime;
    /**
     * 机构key
     */
    private String organkey;
    /**
     * 文档名称
     */
    private String name;
    /**
     * 是否定时
     */
    private boolean isQuartz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getOrgankey() {
        return organkey;
    }

    public void setOrgankey(String organkey) {
        this.organkey = organkey;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isQuartz() {
        return isQuartz;
    }

    public void setQuartz(boolean isQuartz) {
        this.isQuartz = isQuartz;
    }
}
