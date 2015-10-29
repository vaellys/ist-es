package com.ist.ioc.service.common.elasticsearch;

import java.util.List;

public interface Bmp_user_organ_authorityService {
    /**
     * <p>
     * 根据用户名查询对应的机构key列表
     * </p>
     * 
     * @param username
     *            用户名
     * @return List<String> 机构key列表
     */
    public List<String> getBmpOrganKeysByUsername(String username);
}
