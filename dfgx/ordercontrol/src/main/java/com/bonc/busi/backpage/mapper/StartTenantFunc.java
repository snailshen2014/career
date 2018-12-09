package com.bonc.busi.backpage.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.utils.StringUtil;

/**
* @desc:
* @author:lizhen
* @data:2018年1月12日 
*/
@Service
public class StartTenantFunc {
	
	@Autowired		private 			SysFunction	SysFunctionIns;
    
	private static final  Logger log = LoggerFactory.getLogger(StartTenantFunc.class);
	/**
	* @desc: 获取工单和渠道及行云的初始化主机所需要的报文
	*/
	public HashMap<String, Object> Mysqlmessage(HashMap<String, Object> masterhost,String dbtype) {
		 String ips=(String.valueOf(masterhost.get("ip")));
		 String xurl = "";
		 for(int i=0;i<(ips.split(",")).length;i++){
			xurl+="@"+(ips.split(","))[i]+":"+masterhost.get("port")+"/"+masterhost.get("databaseName");
		 }
		 xurl="jdbc:xcloud:"+xurl+"?connectRetry=1&socketTimeOut=30000&connectDirect=false";
		 String ip=(ips.split(","))[0];
		 String Ip= (ip.split("\\."))[ip.split("\\.").length-1];
		 HashMap<String,Object>datahost=new HashMap<String,Object>();
		 List<HashMap<String,Object>>writehostlist=new ArrayList<HashMap<String,Object>>();
		 List<HashMap<String,Object>> datanodelist =new ArrayList<HashMap<String,Object>>();
		 if(dbtype.equals("0")){
		 List<HashMap<String,Object>> readhoststlist  = (List<HashMap<String, Object>>) masterhost.get("slave");
	        int j=1;
	        if(readhoststlist!=null&&readhoststlist.size()!=0){
			for(HashMap<String,Object> read:readhoststlist){
				read.put("HOST", "mysql-"+read.get("ip")+"-S"+(j++));
				read.put("URL", read.get("ip")+":"+read.get("port"));
				read.put("USER", read.get("userName"));
				read.put("PASSWORD", read.get("password"));
			}
			masterhost.put("READHOST_INFOS",readhoststlist);
	        }
		 }
		    masterhost.put("URL", dbtype.equals("0")?(masterhost.get("ip")+":"+masterhost.get("port")):(xurl));
			masterhost.put("HOST", dbtype.equals("0")?("mysql-"+masterhost.get("ip")+"-M1"):("Xcloud-"+ip+"-M1"));			
			masterhost.put("USER", masterhost.get("userName"));
			masterhost.put("PASSWORD", masterhost.get("password"));
			writehostlist.add(masterhost); 
			String nodesize=String.valueOf(masterhost.get("nodeSize"));
			if(StringUtil.isNotNull(nodesize)){
				int nodenumber=Integer.parseInt(nodesize);
				Integer number=0;
				for(int node=0;node<nodenumber;node++){
					HashMap<String,Object>Cdatanode=new HashMap<String,Object>();
					String databasename=String.valueOf(masterhost.get("databaseName"))+(number++);
					Cdatanode.put("NAME",Ip+"-dn-"+databasename);
					Cdatanode.put("DATABASE",databasename);
					datanodelist.add(Cdatanode);
				}
			}
			else{
				   HashMap<String,Object>Odatanode=new HashMap<String,Object>();
				   Odatanode.put("NAME",Ip+"-dn-"+masterhost.get("databaseName"));
				   Odatanode.put("DATABASE",masterhost.get("databaseName"));
				   datanodelist.add(Odatanode);
			}
			datahost.put("WRITEHOST_INFOS", writehostlist);
			datahost.put("DATANODE_INFOS", datanodelist);
			datahost.put("NAME", dbtype.equals("0")?("mysql-"+ip+"-"+masterhost.get("USER")):("Xcloud-"+ip+"-"+masterhost.get("USER")));
			datahost.put("DBTYPE", dbtype);    //封装报文成功	
		return     datahost;
	}
	/**
	* @desc: 开租户失败后日志信息的记录
	*/
	public void TenantLog(String tenant,String appname, String bUSI_ITEM_2, String bUSI_ITEM_3,String message) {
		SysLog	SysLogIns = new SysLog();
		SysLogIns.setTENANT_ID(tenant);
		SysLogIns.setAPP_NAME(appname);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setBUSI_ITEM_1("一键开租户");
		SysLogIns.setBUSI_ITEM_2(bUSI_ITEM_2);
		SysLogIns.setBUSI_ITEM_3(bUSI_ITEM_3);
		SysLogIns.setLOG_MESSAGE(message);
		SysFunctionIns.saveSysLog(SysLogIns);		
	}	
}
