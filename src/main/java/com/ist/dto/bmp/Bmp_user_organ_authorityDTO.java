package com.ist.dto.bmp;

/**
 * 用户名所对应机构实体
 * @author qianguobing
 */
public class Bmp_user_organ_authorityDTO {
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 机构key
	 */
	private String organkey;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOrgankey() {
		return organkey;
	}
	public void setOrgankey(String organkey) {
		this.organkey = organkey;
	}
}
