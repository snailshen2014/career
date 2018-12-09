package com.bonc.busi.interfaces.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;

import com.bonc.busi.interfaces.model.frontline.OrderQueryReq;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

public class FrontLineProvider {
	
	public String sumOrdersStatistic(HashMap<String, Object> req){
		BEGIN();
		SELECT("IFNULL(SUM(o.VALID_NUMS),0) validNums");
		SELECT("IFNULL(SUM(o.VISIT_NUMS_TODAY),0) visitNumsToday");
		SELECT("IFNULL(SUM(o.VISITED_SUCCESS),0) visitedSuccess");
		SELECT("IFNULL(SUM(o.VISIT_NUMS_TOTAL),0) visitNumsTotal");
		SELECT("IFNULL(SUM(o.VISITED_NO_SUCCESS),0) visitNoSuccess");
		SELECT("IFNULL(SUM(o.INTER_SUCCESS),0) interSuccess");
		SELECT("IFNULL(SUM(o.VALID_NUMS-o.VISIT_NUMS_TOTAL),0) noVisitNums");
		SELECT("IFNULL(SUM(o.ITEM0),0) noContactNums");
		SELECT("IFNULL(SUM(o.TYPE1),0) type1");
		SELECT("IFNULL(SUM(o.TYPE2),0) type2");
		SELECT("IFNULL(SUM(o.TYPE3),0) type3");
		SELECT("IFNULL(SUM(o.ITEM101),0) item101");
		SELECT("IFNULL(SUM(o.ITEM102),0) item102");
		SELECT("IFNULL(SUM(o.ITEM103),0) item103");
		SELECT("IFNULL(SUM(o.ITEM104),0) item104");
		SELECT("IFNULL(SUM(o.ITEM121),0) item121");
		SELECT("IFNULL(SUM(o.ITEM201),0) item201");
		SELECT("IFNULL(SUM(o.ITEM202),0) item202");
		SELECT("IFNULL(SUM(o.ITEM203),0) item203");
		SELECT("IFNULL(SUM(o.ITEM204),0) item204");
		FROM(" PLT_ORDER_STATISTIC o, PLT_ACTIVITY_INFO a ");
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("a.TENANT_ID=#{tenantId}");
		WHERE("o.ACTIVITY_SEQ_ID=a.REC_ID");
		
		ROLE_TYPE(req.get("roleType")+"", req.get("orgPath")+"",true);
		
		WHERE("a.ACTIVITY_STATUS IN ('1','8','9')");
		if(req.get("acitveName")!=null&&!"".equals(req.get("acitveName"))){
			WHERE("a.ACTIVITY_NAME like '%"+req.get("acitveName")+"%'");
		}
		if(req.get("activityThemeId")!=null&&!"".equals(req.get("activityThemeId"))){
			WHERE("a.ACTIVITY_THEMEID=#{activityThemeId}");
		}
		return  SQL();
	}
	
	public String custManagerStatistic(HashMap<String, Object> req){
		
		//算出当前人的上一级别PATH
		String orgPath = req.get("orgPath")+"";
		String parentPath = orgPath.substring(0,orgPath.lastIndexOf("/"));
		req.put("parentPath", parentPath);

		BEGIN();
		SELECT("#{parentPath} AS orgName");	//上级别的组织路径
		//如果是比较统计下面的走下面的SQL
//		SELECT("CONCAT(#{parentPath}, SUBSTRING_INDEX(SUBSTRING_INDEX(s.ORG_PATH,#{parentPath},-1),'/',2)) `name`");
		SELECT("s.ORG_PATH name");	//组织PATH
		SELECT("SUM(s.VALID_NUMS) validNums");	//有效工单数
		SELECT("SUM(s.VISIT_NUMS_TOTAL) visitNums");	//回访书
		SELECT("SUM(s.VISITED_SUCCESS) successNums");	//干预成功数
		SELECT("IF((SUM(s.VALID_NUMS)=0),0,(SUM(s.VISIT_NUMS_TOTAL)/SUM(s.VALID_NUMS))) vistiRate");
		SELECT("IF((SUM(s.VISIT_NUMS_TOTAL)=0),0,(SUM(s.VISITED_SUCCESS)/SUM(s.VISIT_NUMS_TOTAL))) successRate");
		
		FROM(" PLT_ORDER_STATISTIC s,PLT_ACTIVITY_INFO a");
		WHERE("s.TENANT_ID=#{tenantId}");
		WHERE("a.TENANT_ID=#{tenantId}");
		WHERE("a.ACTIVITY_ID=#{activityId}");
		WHERE("a.REC_ID=s.ACTIVITY_SEQ_ID");
		WHERE("a.ACTIVITY_STATUS IN (1,8,9)");
		WHERE("(s.ORG_PATH LIKE '"+parentPath+"/%' OR s.ORG_PATH='"+parentPath+"')");
		GROUP_BY("s.ORG_PATH");
		//如果是比较统计下面的走下面的SQL
//		GROUP_BY("CONCAT(#{parentPath}, SUBSTRING_INDEX(SUBSTRING_INDEX(s.ORG_PATH,#{parentPath},-1),'/',2))");
		ORDER_BY(" vistiRate DESC ");
		String limit = " limit 0, "+(Integer.parseInt(String.valueOf(req.get("pageSize")))) ;
		return  SQL()+limit;
	}
	
	/**
	 * roleType 组织拆分改造
	 * @param roleType	角色类型
	 * @param orgPath	组织路径列表
	 * @param flag	是否是统计表
	 */
	private void ROLE_TYPE(String roleType, String orgPath,Boolean flag) {
		//roleType 改造
		if("0".equals(roleType)){
			String [] orgPaths = orgPath.split(IContants.CO_SPLIT);
			StringBuilder path = new StringBuilder();
			for(int i=0,count=orgPaths.length-1;i<=count;i++){
				path.append(i==0?"(":"OR").append(" (o.ORG_PATH LIKE '").append(orgPaths[i]).append("/%' OR o.ORG_PATH='").append(orgPaths[i]).append("') ").append(i==count?")":" " );
			}
			WHERE(path.toString());
		}else{
			if(flag){
				WHERE("o.LOGIN_ID=#{loginId}");
			}else{
				WHERE("o.WENDING_FLAG=#{loginId}");
			}
		}
	}

	public String findActivityStatistic(HashMap<String, Object> req){
		BEGIN();
		SELECT("a.ACTIVITY_ID activityId,a.ACTIVITY_ID oldActivityId,"
				+ " a.ACTIVITY_NAME activityName,"
				+ " a.ACTIVITY_THEMEID activityType,"
				+ " a.ACTIVITY_STATUS activityStatus, "
				+ " DATE_FORMAT(a.CREATE_DATE,'%Y-%m-%d %H:%i:%s') createDate, "	//活动创建时间
				+ " o.SERVICE_TYPE serviceType, "
				+ " SUM(o.INTER_SUCCESS)/SUM(o.VALID_NUMS) successVate, "	//成功率
				+ " IFNULL(SUM(o.VISITED_SUCCESS)/SUM(o.VISIT_NUMS_TOTAL),0) visitSuccessVate, "	//干预成功率
				+ " SUM(o.VALID_NUMS) validNums,"
				+ " SUM(o.VISIT_NUMS_TODAY) visitNumsToday,"
				+ " SUM(o.INTER_SUCCESS) interSuccess, "
				+ " SUM(o.VISITED_SUCCESS) visitedSuccess,"
				+ " SUM(o.VISITED_NO_SUCCESS) visitNoSuccess, "
				+ " SUM(o.VISIT_NUMS_TOTAL) visitNumsTotal,"
				+ " SUM(o.VALID_NUMS-o.VISIT_NUMS_TOTAL) noVisitNums,"
				+ " SUM(o.ITEM0) noContactNums, SUM(o.TYPE1) type1, "
				+ " SUM(o.TYPE2) type2,SUM(o.TYPE3) type3,"
				+ " SUM(o.ITEM101) item101, SUM(o.ITEM102) item102, "
				+ " SUM(o.ITEM103) item103, SUM(o.ITEM104) item104, "
				+ " SUM(o.ITEM121) item121, SUM(o.ITEM201) item201, "
				+ " SUM(o.ITEM202) item202, SUM(o.ITEM203) item203, "
				+ " SUM(o.ITEM204) item204 ");
		if((req.get("orgPath")+"").equals("/root")){
			FROM(" PLT_ORDER_STATISTIC o ignore index(IDX_ORG_PATH), PLT_ACTIVITY_INFO a ");
		}else{
			FROM(" PLT_ORDER_STATISTIC o, PLT_ACTIVITY_INFO a ");
		}
		getActivityStatisticSql(req);
		GROUP_BY(" a.ACTIVITY_ID,o.SERVICE_TYPE,a.ACTIVITY_NAME,a.ACTIVITY_THEMEID ,a.ACTIVITY_STATUS ,a.CREATE_DATE");
		//按活动创建时间、按任意接口字段排序
		Object sort = req.get("sort");
		Object order = req.get("order");
		//默认按 a.CREATE_DATE 排序
		if(sort==null||"".equals(sort)){
			sort = "a.ACTIVITY_ID";
		}
		if(null==order){
			ORDER_BY(sort +" DESC ");
		}else{
			if(new Integer(1).equals(order)){
				ORDER_BY(sort+" ASC ");
			}else if(new Integer(0).equals(order)){
				ORDER_BY(sort+" DESC ");
			}else{
				ORDER_BY(sort+" DESC ");
			}
		}
		String limit = " LIMIT "+(Integer.parseInt(String.valueOf(req.get("pageNum")))-1)*(Integer.parseInt(String.valueOf(req.get("pageSize"))))+","+(Integer.parseInt(String.valueOf(req.get("pageSize"))));
		return SQL()+limit;
	}
	
	public String countActivityStatistic(HashMap<String, Object> req){
		BEGIN();
		SELECT(" COUNT(*) ");
		FROM(" PLT_ORDER_STATISTIC o, PLT_ACTIVITY_INFO a ");
		getActivityStatisticSql(req);
		GROUP_BY(" a.ACTIVITY_ID,o.SERVICE_TYPE");
		return SQL();
	}
	
	private void getActivityStatisticSql(HashMap<String, Object> req){
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("a.TENANT_ID=#{tenantId}");
		WHERE("o.ACTIVITY_SEQ_ID=a.REC_ID" );
		//查询无效的活动
		if(null!=req.get("activityStatus")&&"0".equals(req.get("activityStatus"))){
			WHERE("a.ACTIVITY_STATUS='2'");
		}else{
			WHERE("a.ACTIVITY_STATUS IN ('1','8','9')");
		}
		ROLE_TYPE(req.get("roleType")+"",req.get("orgPath")+"",true);
		if(req.get("acitveName")!=null&&!"".equals(req.get("acitveName"))){
			WHERE("a.ACTIVITY_NAME like '%"+req.get("acitveName")+"%'");
		}
		if(req.get("activityId")!=null&&!"".equals(req.get("activityId"))){
			WHERE("a.ACTIVITY_ID = #{activityId}");
		}
		if(req.get("activityThemeId")!=null&&!"".equals(req.get("activityThemeId"))){
			WHERE("a.ACTIVITY_THEMEID=#{activityThemeId}");
		}
	}
	
	public String countOrdersQueryMB(OrderQueryReq req){
		//如果查询条件中没有用户信息不关联用户表，这里打个标识
		boolean userFlag = false;
		HashMap<String, String> pama = req.getPama();
		if(null!=pama){
			for(String field : pama.keySet()){
				if(pama.get(field).trim().startsWith("u.")){
					userFlag = true;
					break;
				}
			}
		}
		
		String orderTable = "";
		if(req.getOrgPath().equals("/root")){
			orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o IGNORE INDEX (IDX_ORG_PATH)":"PLT_ORDER_INFO_HIS o "+"IGNORE INDEX (IDX_ORG_PATH)";
		}else{
			orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o ":"PLT_ORDER_INFO_HIS o ";
		}
		
		orderTable = orderTable+(userFlag?", PLT_USER_LABEL u":"");
		
		BEGIN();
		SELECT(" COUNT(1) ");
		FROM(orderTable);
		
		WHERE("o.ACTIVITY_SEQ_ID IN ("+req.getActivitySeqs()+") ");
		
		//roleType 改造
		ROLE_TYPE(req.getRoleType(),req.getOrgPath(),false);
		
		if(userFlag){
			WHERE("u.PARTITION_FLAG=#{partFlag}");
			WHERE("o.USER_ID=u.USER_ID");
			WHERE("u.TENANT_ID=#{tenantId}");
		}
		if(null!=pama){
			for(String field : pama.keySet()){
				WHERE(pama.get(field));
			}
			//如果里面没有 传channelStatus 默认全部
			if(!pama.keySet().contains("channelStatus")){
				WHERE("o.CHANNEL_STATUS NOT IN ('401','402','403')");
			}
		}
		
		WHERE("o.CHANNEL_ID=#{channelId} ");
		WHERE("o.SERVICE_TYPE=#{serviceType}");
		WHERE("o.ORDER_STATUS='5'");
		if(null!=req.getTodayTime()&&!"".equals(req.getTodayTime())){
			WHERE("DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d')='"+req.getTodayTime().trim()+"' ");
	  	}
		WHERE("o.TENANT_ID=#{tenantId} ");
		return SQL();
		
	}
	
	public String findOrdersQueryMB(OrderQueryReq req){	
		
		String orderResult = "";
		for(String value:req.getQueryFields()){
			orderResult+=(""+value+",");
		}
		
		// 如果是有效的活动　从工单表中查询工单，如果是无效的活动　从历史表中进行查询
		String orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o ":"PLT_ORDER_INFO_HIS o ";
		BEGIN();
		SELECT(" o.REC_ID,o.CHANNEL_STATUS ORDER_STATUS,"
				+ " IF(o.CHANNEL_STATUS!=0 ,o.CONTACT_DATE,'--') ORDER_FINISH_DATE,"
				+ " o.CONTACT_TYPE ORDER_CONTACT_TYPE,"
				+ " o.CONTACT_CODE ORDER_RESULT_STATUS,"
				+ " o.SERVICE_TYPE,o.EXE_PATH, "
				+ " o.AREAID AREA_NO, o.CITYID CITY_ID,"
				+ orderResult
				+ " u.USER_ID,u.DEVICE_NUMBER PHONE_NUMBER,"
				+ " u.CUST_NAME,u.CONTACT_TELEPHONE_NO,"
				+ " u.MB_FIRST_OWE_MONTH FIRST_MONTH_MOTH,"
				+ " u.OWE_FLAG,"
				+ " u.MB_PACKAGE_ID PACKAGE_NAME,u.MB_MIX_FLAG MIX_FLAG,"
				+ " u.MB_NET_TYPE MOBILE_NET_TYPE,u.MB_CUST_TYPE CUST_TYPE,"
				+ " u.ELECCHANNEL_FLAG ELECCHANNEL_FLAG,"
				+ " u.HIGH_96_FLAG HIGH_FLAG,"
				+ " u.MB_PRODUCT_TYPE PRODUCT_TYPE,"
				+ " u.MB_NETIN_CHANNEL NETIN_CHANNEL,"
				+ " u.MB_AGREEMENT_TYPE AGREEMENT_TYPE,"
				+ " u.MB_AGREEMENT_NAME AGREEMENT_NAME,"
				+ " u.MB_AGREEMENT_BEGIN_TIME AGREEMENT_BEGIN_TIME,"
				+ " u.MB_AGREEMENT_REST_MONTHS AGREEMENT_REST_MONTHS,"
				+ " u.MB_AGREEMENT_END_TIME AGREEMENT_EXPIRE_TIME,"
				+ " u.MB_VALUE_LEVEL JIAZHI_FLAG,"
				+ " u.MB_TERMINAL_BRAND TERMINAL_BRAND,u.MB_NET_MODE NET_TYPE,"
				+ " u.MB_AVERAGE_FLOW AVERAGE_FLOW");
		FROM(orderTable+", PLT_USER_LABEL u");
		
		WHERE("o.ACTIVITY_SEQ_ID IN ("+req.getActivitySeqs()+") ");
		WHERE("o.CHANNEL_ID=#{channelId}");
		WHERE("u.PARTITION_FLAG=#{partFlag}");
		WHERE("o.USER_ID=u.USER_ID ");
		WHERE("o.SERVICE_TYPE=#{serviceType}");
		WHERE("o.ORDER_STATUS='5'");
		
		//roleType 改造
		ROLE_TYPE(req.getRoleType(),req.getOrgPath(),false);
		
		if(null!=req.getTodayTime()&&!"".equals(req.getTodayTime())){
			WHERE("DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d')='"+req.getTodayTime().trim()+"' ");
	  	}
		
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("u.TENANT_ID=#{tenantId}");
    	//租户约束d
		HashMap<String, String> pama = req.getPama();
		if(null!=pama){
			for(String field : pama.keySet()){
				WHERE(""+pama.get(field));
			}
			if(!pama.keySet().contains("channelStatus")){
				WHERE("o.CHANNEL_STATUS NOT IN ('401','402','403')");
			}
		}
		
		if(req.getSort()!=null&&!"".equals(req.getSort())){
			if(null==req.getOrder()){
				ORDER_BY(req.getSort());
			}else{
				if(1==req.getOrder()){
					ORDER_BY(req.getSort()+" ASC ");
				}
				if(0==req.getOrder()){
					ORDER_BY(req.getSort()+" DESC ");
				}
			}
		}
		String limit = " LIMIT "+(req.getPageNum()-1)*req.getPageSize()+","+req.getPageSize();
		return " SELECT CONVERT(s.ONLINE_DUR,CHAR) ONLINE_DUR,CONVERT(s.ARPU_VALUE,CHAR) ARPU_VALUE, s.* FROM ("+SQL()+" "+ limit +") s ";
	}
	
	public String countOrdersQueryKD(OrderQueryReq req){
		boolean userFlag = false;
		HashMap<String, String> pama = req.getPama();
		if(null!=pama){
			for(String field : pama.keySet()){
				if(pama.get(field).trim().startsWith("u.")){
					userFlag = true;
					break;
				}
			}
		}
		
		String orderTable = "";
		if(req.getOrgPath().equals("/root")){
			orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o IGNORE INDEX (IDX_ORG_PATH)":"PLT_ORDER_INFO_HIS o "+"IGNORE INDEX (IDX_ORG_PATH)";
		}else{
			orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o ":"PLT_ORDER_INFO_HIS o ";
		}
		orderTable = orderTable+(userFlag?", PLT_USER_LABEL u":"");
		
		BEGIN();
		SELECT(" COUNT(1) ");
		FROM(orderTable);
		
		WHERE("o.ACTIVITY_SEQ_ID IN ("+req.getActivitySeqs()+") ");
		if(userFlag){
			WHERE("u.PARTITION_FLAG=#{partFlag}");
			WHERE("o.USER_ID=u.USER_ID");
			WHERE("u.TENANT_ID=#{tenantId}");
		}
		//roleType 改造
		ROLE_TYPE(req.getRoleType(),req.getOrgPath(),false);
		
		WHERE("o.CHANNEL_ID=#{channelId} ");
		WHERE("o.SERVICE_TYPE=#{serviceType} ");
		WHERE("o.ORDER_STATUS='5' ");
		if(null!=req.getTodayTime()&&!"".equals(req.getTodayTime())){
			WHERE("DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d')='"+req.getTodayTime().trim()+"'");
	  	}
		
		if(null!=pama){
			for(String field : pama.keySet()){
				WHERE(pama.get(field));
			}
			if(!pama.keySet().contains("channelStatus")){
				WHERE("o.CHANNEL_STATUS NOT IN ('401','402','403')");
			}
		}
		WHERE("o.TENANT_ID=#{tenantId}");
		return SQL();	
	}
	
	public String findOrdersQueryKD(OrderQueryReq req){	
		//工单及活动信息
		String orderResult = "";
		for(String value:req.getQueryFields()){
			orderResult+=(""+value+",");
		}
		
		String orderTable ="1".equals(req.getIsVaild())?"PLT_ORDER_INFO o ":"PLT_ORDER_INFO_HIS o ";
		BEGIN();
		SELECT(" o.TENANT_ID TENANT_ID,o.REC_ID,o.CHANNEL_STATUS ORDER_STATUS, "
				+ " IF(o.CHANNEL_STATUS!=0 ,o.CONTACT_DATE,'--') ORDER_FINISH_DATE,"
				+ " o.CONTACT_TYPE ORDER_CONTACT_TYPE, "
				+ " o.CONTACT_CODE ORDER_RESULT_STATUS,o.EXE_PATH,"
				+ " o.ACTIVITY_SEQ_ID ACTIVITY_ID, "
				+ orderResult
				+ " u.USER_ID,u.DEVICE_NUMBER PHONE_NUMBER,u.CUST_NAME, "
				+ " u.CONTACT_TELEPHONE_NO,"
				+ " u.KD_FIRST_OWE_MONTH FIRST_MONTH_MOTH, "
				+ " u.OWE_FLAG, "
				+ " u.USER_TYPE SERVICE_TYPE, u.AREA_ID AREA_NO,u.CITY_ID CITY_ID, "
				+ " u.KD_AGREEMENT_END_TIME PACKAGE_EXPIRE_TIME, "
				+ " u.KD_CONTINUE_AGREEMENT_FLAG CONTINUE_AGREEMENT_FLAG, "
				+ " u.KD_CONTINUE_AGREEMENT_TIME CONTINUE_AGREEMENT_TIME, "
				+ " u.KD_SUB_PACKAGE_NAME SUB_PACKAGE_NAME,u.KD_MIX_TYPE BRAND_MIX_TYPE, "
				+ " u.KD_MIX_FIX_NO MIX_FIX_PHONE_NO,u.KD_CUR_RATE CUR_RATE, "
				+ " u.KD_OWNER_AREA OWNER_AREA,u.KD_ADDR_SIX_NAME BRAND_ADDRESS, "
				+ " u.KD_NETIN_HALL NETIN_HALL,u.KD_CUST_MANAGER CUST_MANAGER,"
				+ " u.KD_PACKAGE_ID PACKAGE_NAME, "
				+ " IF(DATE_FORMAT(u.KD_MIX_BEGIN_TIME,'%Y-%m-%d %H:%i:%s') IS NOT NULL,DATE_FORMAT(u.KD_MIX_BEGIN_TIME,'%Y-%m-%d %h:%i:%s'),'--') BRAND_MIX_BEGIN_TIME, "
				+ " u.KD_MIX_MOBILE_NO MIX_MOBILE_PHONE ");
		FROM(orderTable+", PLT_USER_LABEL u");

		WHERE("o.ACTIVITY_SEQ_ID IN ( "+req.getActivitySeqs()+" ) ");
		ROLE_TYPE(req.getRoleType(),req.getOrgPath(),false);
		
		WHERE("u.PARTITION_FLAG=#{partFlag}");
		WHERE("o.USER_ID=u.USER_ID");
		WHERE("o.ORDER_STATUS='5'");
		if(null!=req.getTodayTime()&&!"".equals(req.getTodayTime())){
			WHERE("DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d')='"+req.getTodayTime().trim()+"' ");
	  	}
		WHERE("o.SERVICE_TYPE=#{serviceType}");
		WHERE("o.CHANNEL_ID=#{channelId}");
		
		HashMap<String, String> pama = req.getPama();
		if(null!=pama){
			for(String field : pama.keySet()){
				WHERE(pama.get(field));
			}
			if(!pama.keySet().contains("channelStatus")){
				WHERE("o.CHANNEL_STATUS NOT IN ('401','402','403')");
			}
		}
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("u.TENANT_ID=#{tenantId}");
		
		if(req.getSort()!=null&&!"".equals(req.getSort())){
			if(null==req.getOrder()){
				ORDER_BY(req.getSort());
			}else{
				if(1==req.getOrder()){
					ORDER_BY(req.getSort()+" ASC ");
				}
				if(0==req.getOrder()){
					ORDER_BY(req.getSort()+" DESC ");
				}
			}
		}
		String limit = " LIMIT "+(req.getPageNum()-1)*req.getPageSize()+","+req.getPageSize();
		return " SELECT CONVERT(s.ONLINE_DUR,CHAR) ONLINE_DUR, s.* FROM ("+SQL()+" "+ limit +") s ";
	}
	
	public String contactHistory(HashMap<String, Object> req){
		String month = (req.get("contactDate")+"").substring(5,7);
		Object channelId = req.get("contactChannel");
		String userFlag = "";
		if(!StringUtil.validateStr(req.get("userId"))){
			userFlag += " AND o.USER_ID=#{userId} ";
		}
		if(!StringUtil.validateStr(req.get("phoneNumber"))){
			userFlag += " AND o.PHONE_NUMBER=#{phoneNumber} ";
		}
		//默认是全渠道查询
		if(null==channelId){
			return "SELECT * FROM ("
					+ " SELECT o.ACTIVITY_SEQ_ID,"
					+ " o.TENANT_ID,"
					+ " o.ORDER_REC_ID,"
					+ " o.CONTACT_CODE CONTACT_RESULT_NO,"
					+ " o.CONTACT_TYPE,"
					+ " IF(o.CONTACT_CODE IN ('101','102','103','104','121'),'接触成功','接触失败') CONTACT_RESULT,"
					+ " IFNULL(o.ORG_PATH,'--') ORG_PATH,"
					+ " DATE_FORMAT(o.CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE,"
					+ " o.CHANNEL_ID CONTACT_CHANNEL,"
					+ " IFNULL(o.LOGIN_NAME,'--') LOGIN_NAME, "
					+ " o.CONTACT_CONTENT "
					+ " FROM PLT_ORDER_PROCESS_LOG_"+month+" o "
					+ " WHERE o.TENANT_ID=#{tenantId} "
					+ userFlag
					+ " AND o.CONTACT_DATE>=#{contactDate} "
					+ " AND o.CONTACT_DATE<=#{contactDateEnd} "
					+ " UNION "
					+ " SELECT o.ACTIVITY_SEQ_ID, "
					+ " o.TENANT_ID, "
					+ " o.REC_ID ORDER_REC_ID,"
					+ " o.CONTACT_CODE CONTACT_RESULT_NO,"
					+ " o.CONTACT_TYPE,"
					+ " IF(o.CONTACT_CODE=2 ,'发送成功','发送失败') CONTACT_RESULT,"
					+ " IFNULL(o.ORG_PATH,'--') ORG_PATH, "
					+ " DATE_FORMAT(o.CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE,"
					+ " o.CHANNEL_ID CONTACT_CHANNEL,"
					+ " '--' LOGIN_NAME,"
					+ " '' CONTACT_CONTENT "
					+ " FROM PLT_ORDER_INFO_SMS_HIS_"+month+" o "
					+ " WHERE o.TENANT_ID=#{tenantId} "
					+ userFlag
					+ " AND o.ORDER_STATUS=5 "
					+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')<=#{contactDateEnd} "
					+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')>=#{contactDate}) s "
					+ " ORDER BY s.CONTACT_DATE DESC";
		}else{
			//查询短信渠道的接触历史
			if(IContants.DX_CHANNEL.equals(channelId)){
				return "SELECT o.ACTIVITY_SEQ_ID, "
						+ " o.TENANT_ID, "
						+ " o.REC_ID ORDER_REC_ID,"
						+ " o.CONTACT_CODE CONTACT_RESULT_NO,"
						+ " o.CONTACT_TYPE,"
						+ " IF(o.CONTACT_CODE=2 ,'发送成功','发送失败') CONTACT_RESULT,"
						+ " IFNULL(o.ORG_PATH,'--') ORG_PATH, "
						+ " DATE_FORMAT(o.CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE,"
						+ " o.CHANNEL_ID CONTACT_CHANNEL,"
						+ " '--' LOGIN_NAME,"
						+ " '' CONTACT_CONTENT "
						+ " FROM PLT_ORDER_INFO_SMS_HIS_"+month+" o "
						+ " WHERE o.TENANT_ID=#{tenantId} "
						+ userFlag
						+ " AND o.ORDER_STATUS=5 "
						+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')<=#{contactDateEnd} "
						+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')>=#{contactDate} "
						+ " ORDER BY o.CONTACT_DATE DESC";
			}else{//查询其他渠道的接触历史
				return "SELECT o.ACTIVITY_SEQ_ID,"
						+ " o.TENANT_ID,"
						+ " o.ORDER_REC_ID,"
						+ " o.CONTACT_CODE CONTACT_RESULT_NO,"
						+ " o.CONTACT_TYPE,"
						+ " IF(o.CONTACT_CODE IN ('101','102','103','104','121'),'接触成功','接触失败') CONTACT_RESULT,"
						+ " IFNULL(o.ORG_PATH,'--') ORG_PATH,"
						+ " DATE_FORMAT(o.CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE,"
						+ " o.CHANNEL_ID CONTACT_CHANNEL,"
						+ " IFNULL(o.LOGIN_NAME,'--') LOGIN_NAME, "
						+ " o.CONTACT_CONTENT "
						+ " FROM PLT_ORDER_PROCESS_LOG_"+month+" o "
						+ " WHERE o.TENANT_ID=#{tenantId} "
						+ " AND o.CHANNEL_ID=#{contactChannel} "
						+ userFlag
						+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')<=#{contactDateEnd} "
						+ " AND DATE_FORMAT(o.CONTACT_DATE,'%Y%m%d%H%i%s')>=#{contactDate} "
						+ " ORDER BY o.CONTACT_DATE DESC";
			}
		}
	}

	/**
	 * 用户工单查询SQL
	 * @param req
	 * @return
	 */
	public String findUserOrder(HashMap<String, Object> req){
		BEGIN();
		SELECT("o.TENANT_ID");
		SELECT("o.REC_ID");
		SELECT("o.ACTIVITY_SEQ_ID ACTIVITY_ID");
		SELECT("o.USER_ID");
		SELECT("o.CHANNEL_STATUS ORDER_STATUS");
		SELECT("IF(o.CHANNEL_STATUS!=0 ,o.CONTACT_DATE,'--') ORDER_FINISH_DATE");
		SELECT("o.CONTACT_TYPE ORDER_CONTACT_TYPE");
		SELECT("o.CONTACT_CODE ORDER_RESULT_STATUS");
		SELECT("o.RESERVE2 ORDER_CONTACT_CONTENT");
		SELECT("DATE_FORMAT(o.BEGIN_DATE,'%Y-%m-%d %H:%i:%s') BEGIN_DATE");
		SELECT("DATE_FORMAT(o.END_DATE,'%Y-%m-%d %H:%i:%s') END_DATE");
		SELECT("o.MARKETING_WORDS REFERRALS_INFO");
		SELECT("o.EXE_PATH");
		SELECT("o.DEAL_MONTH");
		SELECT("o.RESERVE5");
		FROM("PLT_ORDER_INFO o");
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("o.CHANNEL_ID =#{channelId}");
		if(!StringUtil.validateStr(req.get("userId"))){
			WHERE("o.USER_ID=#{userId}");
		}
		if(!StringUtil.validateStr(req.get("phoneNum"))){
			WHERE("o.PHONE_NUMBER=#{phoneNum}");
		}
		WHERE("o.ORDER_STATUS='5'");//只有有效的工单才能执行
//		WHERE("o.CHANNEL_STATUS='0'");//只有为执行过切未成功的工单才能执行
		if(req.get("roleType")==null||!"0".equals(req.get("roleType"))){
			WHERE("o.WENDING_FLAG IS NOT NULL ");//只有分配到人才能执行
		}
		return SQL();
	}
	
	public String getActivitySeqIds(OrderQueryReq req){
		BEGIN();
		SELECT("REC_ID ACTIVITY_ID,ACTIVITY_ID OLD_ACTIVITY_ID ,ACTIVITY_NAME");
		FROM("PLT_ACTIVITY_INFO");
		WHERE("TENANT_ID=#{tenantId}");
		if(null!=req.getActivityId()&&!"".equals(req.getActivityId())){
			WHERE("ACTIVITY_ID=#{activityId}");
		}
		if("1".equals(req.getIsVaild())){
			WHERE("ACTIVITY_STATUS IN ('1','8','9')");
		}else{
			WHERE("ACTIVITY_STATUS=2");
		}
		
		return SQL();
	} 
}
 