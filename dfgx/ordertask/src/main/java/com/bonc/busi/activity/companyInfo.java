package com.bonc.busi.activity;

import java.io.Serializable;

public class companyInfo implements Serializable {
	
	private static final long serialVersionUID = -3682952863984791726L;

	//ftp 密码
	private String ftpPassWord;
	
	//公司id
	private String companyId;
	
	//ftp回执路径
	private String ftpReturnPath;
	
	//ftp 用户名
	private String ftpUserName;
	
	//归属地市名称
	private String ascriptionCityName;
	
	//公司名称
	private String companyName;
	
	//归属地市id
	private String ascriptionCityId;
	
	//业务类型id
	private String serviceTypeId;
	
	//文件密钥
	private String fileKey;
	
	//业务类型名称
	private String serviceTypeName;
	
	//ftp 端口
	private String ftpPort;
	
	//ftp ip
	private String ftpIp;
	
	//ftp下发路径
	private String ftpDownPath;

	public String getFtpPassWord() {
		return ftpPassWord;
	}

	public void setFtpPassWord(String ftpPassWord) {
		this.ftpPassWord = ftpPassWord;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getFtpReturnPath() {
		return ftpReturnPath;
	}

	public void setFtpReturnPath(String ftpReturnPath) {
		this.ftpReturnPath = ftpReturnPath;
	}

	public String getFtpUserName() {
		return ftpUserName;
	}

	public void setFtpUserName(String ftpUserName) {
		this.ftpUserName = ftpUserName;
	}

	public String getAscriptionCityName() {
		return ascriptionCityName;
	}

	public void setAscriptionCityName(String ascriptionCityName) {
		this.ascriptionCityName = ascriptionCityName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAscriptionCityId() {
		return ascriptionCityId;
	}

	public void setAscriptionCityId(String ascriptionCityId) {
		this.ascriptionCityId = ascriptionCityId;
	}

	public String getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(String serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public String getFtpDownPath() {
		return ftpDownPath;
	}

	public void setFtpDownPath(String ftpDownPath) {
		this.ftpDownPath = ftpDownPath;
	}

}
