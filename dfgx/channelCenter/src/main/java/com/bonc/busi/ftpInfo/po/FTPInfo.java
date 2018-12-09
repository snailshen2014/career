/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ProvFTPInfo.java
 * @Prject: channelCenter
 * @Package: com.bonc.busi.provFTPInfo.po
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年11月18日 下午2:12:02
 * @version: V1.0  
 */

package com.bonc.busi.ftpInfo.po;

/**
 * @ClassName: FTPInfo
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年11月18日 下午2:12:02
 */
public class FTPInfo {
	
	private Integer id;
	
	private String provId;
	
	private String provDesc;
	
	private String host;
	
	private String port;
	
	private String username;
	
	private String password;
	
	private String filePath;
	
	private String tenantId;

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getProvDesc() {
		return provDesc;
	}

	public void setProvDesc(String provDesc) {
		this.provDesc = provDesc;
	}

	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public String toString() {
		return "ProvFTPInfo [id=" + id + ", provId=" + provId + ", provDesc=" + provDesc + ", host=" + host + ", port="
				+ port + ", username=" + username + ", password=" + password + ", filePath=" + filePath + ", tenantId="
				+ tenantId + "]";
	}

	

}
