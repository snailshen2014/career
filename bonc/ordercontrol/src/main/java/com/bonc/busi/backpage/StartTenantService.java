package com.bonc.busi.backpage;

import com.bonc.busi.backpage.bo.CreateTenantBo;

import java.util.HashMap;


/**
* @desc:一键开租户service
* @author:lizhen
* @data:2018年1月2日 
*/
public interface StartTenantService {
	//先将报文存入日志表中
	public boolean insertmessage(HashMap<String,Object> tenant);
	//第一步：初始化主机配置
	public boolean inithostControl(HashMap<String,Object> tenant);
	//第二部：初始化路由流程
	public boolean initadsparaControl(HashMap<String,Object> tenant);
	//第四部：表配置流程初始化
	public boolean inittableinfoControl(HashMap<String,Object> tenant);
	//第三步：初始化表结构
	boolean initTableStructure(CreateTenantBo tenantBo,HashMap<String, Object> tenant);
	//第五步：初始化基础数据
	boolean initTableData(CreateTenantBo tenant);
	//第六步：插入租户表记录
	void initTenantRecord(CreateTenantBo tenant);
	// 装配json为CreateTenantBo对象
	CreateTenantBo assembleJsonToBo(HashMap<String, Object> tenant);
}
