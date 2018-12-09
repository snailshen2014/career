package com.bonc.busi.backpage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bonc.busi.backpage.bo.CreateTenantBo;
import com.bonc.busi.backpage.mapper.BaseFunc;
import com.bonc.common.utils.BoncExpection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.busi.backpage.StartTenantService;
import com.bonc.busi.backpage.mapper.BackPageMapper;
import com.bonc.busi.backpage.mapper.StartTenantFunc;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.StringUtil;



/**
* @desc:
* @author:lizhen
* @data:2018年1月2日 
*/
@Service("StartTenantService")
public class StartTenantServiceImpl implements StartTenantService{
	@Autowired      private	            BackPageMapper mapper;
	@Autowired      private             StartTenantFunc starttenantfunc;
	@Autowired 		private  			BackPageMapper backPageMapper;
	@Autowired 		private 			BaseFunc baseFunc;
	@Autowired 		private 			BackPageServiceImpl backPageServiceImpl;
	@Override
	public boolean insertmessage(HashMap<String, Object> tenant) {
		boolean result=false;
		JSONObject obj =  new JSONObject(tenant);
		String tenantid=String.valueOf(tenant.get("tenantId"));
		String message =obj.toJSONString();
		String BUSI_ITEM_2="租户报文";String BUSI_ITEM_3="租户完整报文JSON串";String appname="TENANTMESSAGE"+StartTenantServiceImpl.class.getName();
		starttenantfunc.TenantLog(tenantid,appname,BUSI_ITEM_2,BUSI_ITEM_3,message);
		result=true;
		return result;
	}	
	@SuppressWarnings("unchecked")
	@Override
	 //第一步：初始化主机配置
	public boolean inithostControl(HashMap<String, Object> tenant) {			
	    boolean Inithost=false;
	    String BUSI_ITEM_2="";String BUSI_ITEM_3="";
		String url=mapper.getCfgValue("EPM_INIT_HOST");
		String tenantid=String.valueOf(tenant.get("tenantId"));
		String appname="OEDERCONTROL-"+StartTenantServiceImpl.class.getName()+"-insertAll";
		HashMap<String,Object> masterhost=(HashMap<String, Object>) tenant.get("mysql-order");
		String dbtype="0";
		HashMap<String,Object>Odatahost=starttenantfunc.Mysqlmessage(masterhost,dbtype);
		String datahostresult=HttpUtil.sendPost(url, JSON.toJSONString(Odatahost,SerializerFeature.DisableCircularReferenceDetect));         //工单Mysql调用接口;						      
		HashMap<String,Object> dataresult = JSON.parseObject(datahostresult,HashMap.class);    //返回值可获取key为"RESULT"值，0为失败，1为成功。			
		String mysqlhost=String.valueOf(dataresult.get("RESULT"));
			
		String message=String.valueOf(dataresult.get("message"));
		
        BUSI_ITEM_2="模块一";BUSI_ITEM_3="ORDER主机初始化";
		if(mysqlhost.equals("0")){               
			starttenantfunc.TenantLog(tenantid,appname,BUSI_ITEM_2,BUSI_ITEM_3,message);
		}
	
	    HashMap<String,Object> XCLOUDHOSTS=(HashMap<String, Object>) tenant.get("mpp");//初始化行云	
		String Xdbtype="1";
		HashMap<String,Object>Xdatahost=starttenantfunc.Mysqlmessage(XCLOUDHOSTS,Xdbtype);
		String Xcloudhostresult=HttpUtil.sendPost(url, JSON.toJSONString(Xdatahost,SerializerFeature.DisableCircularReferenceDetect));         //Xcloud调用接口;
		HashMap<String,Object> Xcloudresult = JSON.parseObject(Xcloudhostresult,HashMap.class);
		String xcloudhost=String.valueOf(Xcloudresult.get("RESULT"));
		String Xmessage=String.valueOf(Xcloudresult.get("message"));
        BUSI_ITEM_2="模块一";BUSI_ITEM_3="Xcloud主机初始化";
        if(xcloudhost.equals("0")){               
			starttenantfunc.TenantLog(tenantid,appname,BUSI_ITEM_2,BUSI_ITEM_3,Xmessage);
		}
        HashMap<String,Object> Cmasterhost=(HashMap<String, Object>) tenant.get("mysql-channel");  //渠道协同初始化
        String Cdbtype="0";
		HashMap<String,Object>Cdatahost=starttenantfunc.Mysqlmessage(Cmasterhost,Cdbtype);
		String Cdatahostresult=HttpUtil.sendPost(url, JSON.toJSONString(Cdatahost,SerializerFeature.DisableCircularReferenceDetect));         //工单Mysql调用接口;						      
		HashMap<String,Object> Cdataresult = JSON.parseObject(Cdatahostresult,HashMap.class);    //返回值可获取key为"RESULT"值，0为失败，1为成功。			
		String Cmysqlhost=String.valueOf(Cdataresult.get("RESULT"));
		BUSI_ITEM_2="模块一";BUSI_ITEM_3="ORDER主机初始化";
		String Omessage=String.valueOf(Cdataresult.get("message"));
	    if(Cmysqlhost.equals("0")){               
				starttenantfunc.TenantLog(tenantid,appname,BUSI_ITEM_2,BUSI_ITEM_3,Omessage);
		}		
		if(Cmysqlhost.equals("1")&&xcloudhost.equals("1")&&Cmysqlhost.equals("1")){
			Inithost=true;         // 初始化主机配置成功
		}
		return Inithost;	//返回为list,list.get(0)取到mysql初始化的HashMap，list.get(1)取到Xcloud初始化的HashMap，取key为"RESULT"的值，0为失败，1为成功。					
}

	@SuppressWarnings("unchecked")
	@Override
	//第二部：初始化路由流程
	public boolean initadsparaControl(HashMap<String, Object> tenant) {
		boolean InitPara=true;
	    String BUSI_ITEM_2="";String BUSI_ITEM_3="";
	    List<HashMap<String,Object>>initadsparalist=new ArrayList<HashMap<String,Object>>();
	    String url=mapper.getCfgValue("EPM_INIT_ADSPARA");  //访问url
	    HashMap<String,Object> Omasterhost=(HashMap<String, Object>) tenant.get("mysql-order");//工单路由初始化
		String paratype=mapper.getCfgValue("SYS_ROUTE_RULE_PARATYPE");      //从配置表中获取路由规则类型
		String paravalueStr=mapper.getCfgValue("SYS_LOGIC_NODE_NAMES");     //从配置表中获取数据节点
		Integer pavavalue=paravalueStr.split(",").length;		            //长度即为para_value		
		  HashMap<String,Object>iniypara=new HashMap<String,Object>();
		  iniypara.put("PARA_TYPE", paratype);
		  iniypara.put("PARA_NAME", tenant.get("tenantId"));
		  iniypara.put("PARA_VALUE", pavavalue);
		initadsparalist.add(iniypara);//租户路由报文组装完成		
		String adspara=HttpUtil.sendPost(url, JSON.toJSONString(initadsparalist));    		//调接口
		HashMap<String,Object> results = JSON.parseObject(adspara,HashMap.class);
		String result= String.valueOf(results.get("RESULT"));
		String message= String.valueOf(results.get("message"));
		if(result.equals("1")){
			String ip=(String.valueOf(Omasterhost.get("ip")));
		    String Ip= (ip.split("\\."))[ip.split("\\.").length-1];
			String schemaname=Ip+"-dn-"+String.valueOf(Omasterhost.get("databaseName"));    //完成路由配置后追加数据节点，如果没有则不追加(RESULT等于0和2时都不追加)
			String nodename=","+schemaname;
			if(StringUtil.isNotNull(nodename)){
			   	mapper.addnodename(nodename);
			}			
	     }		
	    HashMap<String,Object> Cmasterhost=(HashMap<String, Object>) tenant.get("mysql-channel");//渠道协同初始化
		String Cparatype=mapper.getCfgValue("SYS_CHANNEL_RULE_PARATYPE");   //从配置表中获取路由规则类型
		String CparavalueStr=mapper.getCfgValue("SYS_CHANNEL_NODE_NAMES");  //从配置表中获取数据节点
		Integer Cpavavalue=0;
		if(StringUtil.isNotNull(CparavalueStr)){
              Cpavavalue=CparavalueStr.split(",").length;		         //长度即为para_value
		}
		initadsparalist.clear();
		int number=0;
		int j=1;
		int nodenumber=Integer.parseInt(String.valueOf(Cmasterhost.get("nodeSize")));
		int value=20/nodenumber;//每隔value值时para_value自增
		for(int i=0;i<20;i++){
			 HashMap<String,Object>Cinitpara=new HashMap<String,Object>();
			 Cinitpara.put("PARA_TYPE", Cparatype);
			 Cinitpara.put("PARA_NAME", String.valueOf(tenant.get("tenantId"))+String.format("%02d", number++));
			 Cinitpara.put("PARA_VALUE", ((j++)%value==0)?Cpavavalue++:Cpavavalue);
			 initadsparalist.add(Cinitpara);//租户路由报文组装完成
		}
		String Cadspara=HttpUtil.sendPost(url, JSON.toJSONString(initadsparalist));
		HashMap<String,Object> Cresults = JSON.parseObject(Cadspara,HashMap.class);
		String Cresult= String.valueOf(Cresults.get("RESULT"));
		String Cmessage= String.valueOf(Cresults.get("message"));
		if(Cresult.equals("1")){
			String ip=(String.valueOf(Cmasterhost.get("ip")));
		    String Ip= (ip.split("\\."))[ip.split("\\.").length-1];
			String nodename="";
			String Cschemaname=Ip+"-dn-"+String.valueOf(Cmasterhost.get("databaseName"));    //完成路由配置后追加数据节点，如果没有则不追加(RESULT等于0和2时都不追加)
			for(int h=0;h<nodenumber;h++){
				nodename+=","+Cschemaname+h;
			}
			if(StringUtil.isNotNull(nodename)){
			   	mapper.addCnodename(nodename);
			}			
	     }
		BUSI_ITEM_2="模块二";BUSI_ITEM_3="CHANNEL路由规则配置";
		String appname="OEDERCONTROL-"+StartTenantServiceImpl.class.getName()+"-insertadspara";
		if(Cresult.equals("0")){
			InitPara=false;
			starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")),appname,BUSI_ITEM_2,BUSI_ITEM_3,Cmessage);
			}
		BUSI_ITEM_2="模块二";BUSI_ITEM_3="OEDER路由规则配置";
		if(result.equals("0")){
			InitPara=false;
			starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")),appname,BUSI_ITEM_2,BUSI_ITEM_3,message);
		  }
		return InitPara;	
	}
	@Override
	@SuppressWarnings("unchecked")
	//第四部：表配置流程初始化
	public boolean inittableinfoControl(HashMap<String, Object> tenant) {
		boolean Inittableinfo=false;
	    String  appname="", BUSI_ITEM_2="",BUSI_ITEM_3="";
	    appname="OEDERCONTROL-"+StartTenantServiceImpl.class.getName()+"-inserttableinfo";
	    String url=mapper.getCfgValue("EPM_INIT_TABLEINFO");                              //工单table_info初始化
		String schamename=mapper.getCfgValue("SYS_LOGIC_SCHEMA_NAME");
		String rulename=mapper.getCfgValue("SYS_ROUTE_RULE_NAME");
		String tenantid=String.valueOf(tenant.get("tenantId"));
		String datanode=mapper.getCfgValue("SYS_LOGIC_NODE_NAMES");
		HashMap<String,Object> inittableinfo=new HashMap<String,Object>();
		inittableinfo.put("SCHEMA_NAME", schamename);
		inittableinfo.put("RULE", rulename);
		inittableinfo.put("DATANODE", datanode);//表配置报文组装完成
		String tableinfo=HttpUtil.sendPost(url, JSON.toJSONString(inittableinfo));		//调接口
		HashMap<String,Object> tableinforesult = JSON.parseObject(tableinfo,HashMap.class);  //  返回值可获取key为"CODE"的值，1成功，0失败。
		String inittable=String.valueOf(tableinforesult.get("CODE"));
		String message=String.valueOf(tableinforesult.get("message"));
		
		HashMap<String,Object> XCLOUDHOSTS=(HashMap<String, Object>) tenant.get("mpp");//初始化行云	(不穿规则名)
		 String ip=(String.valueOf(XCLOUDHOSTS.get("ip"))).split(",")[0];
		 String Ip= (ip.split("\\."))[ip.split("\\.").length-1];
		HashMap<String,Object> Xinittableinfo=new HashMap<String,Object>();
		Xinittableinfo.put("DATANODE", Ip+"-dn-"+XCLOUDHOSTS.get("databaseName"));
		Xinittableinfo.put("PHYSICALNAME", "XCLOUD_"+tenantid);
		Xinittableinfo.put("SCHEMA_NAME", schamename);
		Xinittableinfo.put("DATANODE", datanode);//表配置报文组装完成
		String Xtableinfo=HttpUtil.sendPost(url, JSON.toJSONString(inittableinfo));		//调接口
		HashMap<String,Object> Xtableinforesult = JSON.parseObject(Xtableinfo,HashMap.class);  //  返回值可获取key为"CODE"的值，1成功，0失败。
		String Xinittable=String.valueOf(Xtableinforesult.get("CODE"));
		String Xmessage=String.valueOf(Xtableinforesult.get("message"));
		
		String Cschamename=mapper.getCfgValue("SYS_CHANNEL_SCHEMA_NAME");     //渠道表配置
		String Crulename=mapper.getCfgValue("SYS_CHANNEL_RULE_NAME");
		String Cdatanode=mapper.getCfgValue("SYS_CHANNEL_NODE_NAMES");
		HashMap<String,Object> Cinittableinfo=new HashMap<String,Object>();
		Cinittableinfo.put("SCHEMA_NAME", Cschamename);
		Cinittableinfo.put("RULE", Crulename);
		Cinittableinfo.put("DATANODE", Cdatanode);//表配置报文组装完成
		String Ctableinfo=HttpUtil.sendPost(url, JSON.toJSONString(Cinittableinfo));
		HashMap<String,Object> Ctableinforesult = JSON.parseObject(Ctableinfo,HashMap.class);  //  返回值可获取key为"CODE"的值，1成功，0失败。
		String Cinittable=String.valueOf(Ctableinforesult.get("CODE"));
		String Cmessage=String.valueOf(Ctableinforesult.get("message"));
		BUSI_ITEM_2="模块四";BUSI_ITEM_3="ORDER表配置流程";
		if(inittable.equals("0")){
			starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")),appname,BUSI_ITEM_2,BUSI_ITEM_3,message);		
	     }
		BUSI_ITEM_2="模块四";BUSI_ITEM_3="XCLOUD表配置流程";
		if(Xinittable.equals("0")){
			starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")),appname,BUSI_ITEM_2,BUSI_ITEM_3,Xmessage);		
	     }
		BUSI_ITEM_2="模块四";BUSI_ITEM_3="CHANNEL表配置流程";
		if(Cinittable.equals("0")){			
			starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")),appname,BUSI_ITEM_2,BUSI_ITEM_3,Cmessage);		
	     }
		if(Cinittable.equals("1")&&inittable.equals("1")&&Xinittable.equals("1")){
			Inittableinfo=true;
		}
		return Inittableinfo;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean initTableStructure(CreateTenantBo tenantBo,HashMap<String, Object> tenant) {
		String appname = "", bUSI_ITEM_2 = "模块四", bUSI_ITEM_3 = "创建物理表", message = "";
        appname = "OEDERCONTROL-" + StartTenantServiceImpl.class.getName() + "-createPhysicalTable";
        try {
            // 创建工单表
            String tempSQLPathToOrder = baseFunc.removeMycatSQL(tenantBo.getTenantId(),
                    backPageServiceImpl.StructureSql);
            baseFunc.executeDdlOnMysql(tempSQLPathToOrder, tenantBo);
            baseFunc.deleteFile(tempSQLPathToOrder);
            // 创建渠道协同表
            // 1.获取已有参数信息
            String url = mapper.getCfgValue("CHANNEL_TENANT_URL")+tenant.get("tenantId");
            HashMap<String, Object> channelTempMap = (HashMap<String, Object>) tenant.get("mysql-channel");          
		    String channelSqlData = HttpUtil.sendPost(url, JSON.toJSONString(channelTempMap));
            HashMap<String,Object> channelSqlDataMap = JSON.parseObject(channelSqlData,HashMap.class);
            String code = (String)channelSqlDataMap.get("code");
            if("-1".equals(code)){
                message = "errCode："+channelSqlDataMap.get("errCode")+"errDesc："+channelSqlDataMap.get("errDesc");
                starttenantfunc.TenantLog(String.valueOf(tenant.get("tenantId")), appname, bUSI_ITEM_2, bUSI_ITEM_3, message);
                return false;
                }
		      } catch (Exception e) {
	            e.printStackTrace();
	            return false;
		 }
            return true;
	}

	@Override
	public boolean initTableData(CreateTenantBo tenant) {
		return  executeSql(tenant);

	}

	@Override
	public void initTenantRecord(CreateTenantBo tenant) {
		backPageMapper.addTenantRecord(tenant);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CreateTenantBo assembleJsonToBo(HashMap<String, Object> tenant) {
		CreateTenantBo cfg = new CreateTenantBo();
		try {
			HashMap<String,Object> mysql = (HashMap<String, Object>) tenant.get("mysql-order");
			HashMap<String,Object> mpp = (HashMap<String, Object>) tenant.get("mpp");
			HashMap<String,Object> ftp = (HashMap<String, Object>) tenant.get("ftp");
			//基础信息
			cfg.setTenantId((String) tenant.get("tenantId"));
			cfg.setProvId((String) tenant.get("provId"));
			cfg.setTenantName((String) tenant.get("tenantName"));
			// 默认属性
			cfg.setIsFullInit("false");
			cfg.setIsYW("false");
			cfg.setIsLiantong("true");
			cfg.setIsStructure("true");
			cfg.setIsliantongData("true");
			cfg.setIsdianxinywData("false");
			cfg.setIsdianxinData("false");
			cfg.setIsliantongywData("false");
			cfg.setMYSQL_PASS((String) mysql.get("password"));
			cfg.setMYSQL_USER((String) mysql.get("userName"));
			//jdbc:mysql://10.162.156.113:3306/clyx_app_gd_hainan?useUnicode=true&characterEncoding=utf-8&autoCommit=true&useSSL=false
			cfg.setMYSQL_Url("jdbc:mysql://"+(String) mysql.get("ip")+":"+mysql.get("port")+"/"+(String) mysql.get("databaseName")+
					"?useUnicode=true&characterEncoding=utf-8&autoCommit=true&useSSL=false");
			cfg.setXCloud_USER((String) mpp.get("userName"));
			cfg.setXCloud_Url((String) mpp.get("ip")+(String) mpp.get("databaseName")+
					"?connectRetry=3");
			cfg.setXCloud_PASS((String) mpp.get("password"));
			cfg.setFTP_Url((String) ftp.get("ip"));
			cfg.setFTP_Port((String) ftp.get("port"));
			cfg.setFTP_PASS((String) ftp.get("password"));
			cfg.setFTP_USER((String) ftp.get("userName"));
		}catch (Exception boncEx){
			BoncExpection boncExpection = new BoncExpection("0", "请求参数转换异常");
			throw  boncExpection;
		}
		return cfg;
	}

	private boolean executeSql(CreateTenantBo tenant){
		try {
			String tempSQLPath = baseFunc.replaceSQLbyTenantId(tenant.getTenantId(), backPageServiceImpl.LiantongData);
			baseFunc.executeDdlOnMycat(tempSQLPath );
			baseFunc.deleteFile(tempSQLPath);
			// ----------base库 追加静态数据----------
			tempSQLPath = baseFunc.replaceSQLbyTenantId(tenant.getTenantId(), backPageServiceImpl.BaseAppendStatic);
			baseFunc.executeDdlOnMycat(tempSQLPath);
			baseFunc.deleteFile(tempSQLPath);
			// ----------base库 追加动态数据----------
			tempSQLPath = baseFunc.replaceSQLbyParams(tenant.getTenantId(), backPageServiceImpl.BaseAppendDynamic, tenant);
			baseFunc.executeDdlOnMycat(tempSQLPath);
			baseFunc.deleteFile(tempSQLPath);
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
