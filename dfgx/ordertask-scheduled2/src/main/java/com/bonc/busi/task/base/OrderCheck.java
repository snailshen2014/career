package com.bonc.busi.task.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.utils.HttpUtil;
import com.bonc.busi.statistic.service.StatisticService;

public class OrderCheck extends ParallelFunc{
	
	@Autowired
	private BusiTools AsynDataIns;
	
	private final static Logger log= LoggerFactory.getLogger(OrderCheck.class);
	private	JdbcTemplate JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	private	BusiTools		BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	BaseMapper		BaseMapperIns = SpringUtil.getBean(BaseMapper.class);
	private	StatisticService	StatisticServiceIns = SpringUtil.getBean(StatisticService.class);
	
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	Date							dateBegin = null;    // --- 开始时间  ---
	private	int  							SerialId =0;   // --- 日志序列号 ---
	// --- 定义当前帐期变量 ---
	private	String						curDateId = null;
	// --- 定义当前活动变量 ---
	private	int							curActivitySeqId = -1;
	// --- 定义当前工单变量 ---
	private	long							curOrderRecId = -1;
	private	int							m_iTotalNum = 0;
	private	List<Map<String, Object>>	listTenantInfo = null;			// --- 有效租户信息 ---
	// --- 定义当前租户变量 ---
	private	String					curTenantId = null;

	// --- 定义保存租户成功条件的变量 ---
	private	Map<String,List<ActivitySucessInfo>>  mapActivitySucessInfo= new HashMap<String,List<ActivitySucessInfo>>();
		// --- 定义活动对应的产品列表 ---
	private	Map<String,List<String>>  mapActivityProduct= new HashMap<String,List<String>>();
	// --- 定义是否需要调用查询有效租户和活动的变更 ---
	private	boolean		bNeedGetData = true;
	// --- 定义当前操作的是哪个表 ---
	private	String			m_strTableName = null;
	
	// --- 取到所有表的List
	private List<HashMap<String, Object>> m_tableList = null;
	
	
	public List<HashMap<String, Object>> geTableList() {
		return this.m_tableList;
	}
	public void setTableList(List<HashMap<String, Object>> tableList) {
		this.m_tableList = tableList;
	}
	
	public		void		setTableName(String  TableName){
		this.m_strTableName = TableName;
	}
	public		String		getTableName(){
		return this.m_strTableName;
	}

	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		// --- 纪录开始时间 ---
		dateBegin = new Date();
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		PltCommonLogIns.setLOG_TYPE("01");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("ORDERCHECK");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单检查开始");
		PltCommonLogIns.setBUSI_ITEM_3(m_strTableName);
		PltCommonLogIns.setBUSI_ITEM_10(""+m_tableList);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		PltCommonLogIns.setBUSI_ITEM_10(null);
		// --- 得到最大帐期 ---
//		curDateId = BaseMapperIns.getMaxDateId();
		String  monthTimeUrl  = BusiToolsIns.getValueFromGlobal("GET_MONTH_TIME_"+SysVars.getTenantId());
     /* curDateId = HttpUtil.sendPost(monthTimeUrl, "");*/
	 /*	curDateId = HttpUtil.doGet("http://clyxys.bonc.yz/usertool/rest/apidata/getmaxdate?cubeId=C12419292583", null).split(",")[1];*/
        curDateId = HttpUtil.doGet(monthTimeUrl, null).split(",")[1];
		
		if(curDateId == null){
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，curDateid为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// --- 提取有效租户信息 ---
		String tenantId = SysVars.getTenantId();
		listTenantInfo = new ArrayList<Map<String,Object>>();
		Map<String, Object > tenantInfo = new HashMap<String, Object>();
		tenantInfo.put("TENANT_ID", tenantId);
		listTenantInfo.add(tenantInfo);
		
//		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if(listTenantInfo == null || listTenantInfo.size() == 0){   // ---  无有效租户纪录 ---
			log.error("--- 无有效租户纪录---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，无有效租户纪录");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// --- 设置当前租户编号 ---
		curTenantId = (String)listTenantInfo.get(0).get("TENANT_ID");
		// --- 提取租户对应的活动信息 ---
		for(Map<String,Object> mapTenantInfo:listTenantInfo){ 
			String TENANT_ID = (String)mapTenantInfo.get("TENANT_ID");
			List<ActivitySucessInfo> listTmp = new ArrayList<ActivitySucessInfo>();
			if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_HIS")){
				listTmp = BaseMapperIns.getOrderHisInfo(TENANT_ID);
			}else{
				listTmp = BaseMapperIns.getActivityForTenantId(TENANT_ID);}
			// --- 根据每个活动提取活动对应的产品列表 ---
			if(listTmp != null && listTmp.size() > 0){
				mapActivitySucessInfo.put(TENANT_ID, listTmp);
				for(ActivitySucessInfo item:listTmp){
					List<String>   listTemp = BaseMapperIns.getProductListForActivity(item.getACTIVITY_SEQ_ID(),TENANT_ID);
					if(listTemp != null && listTemp.size() > 0){
						mapActivityProduct.put(TENANT_ID+"-"+item.getACTIVITY_SEQ_ID(), listTemp);	
					}
				}
			}
		}
	// --- for(Map< ---	
	return 0;
	}
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		Date				dateEnd = new Date();
		log.info("--- 工单成功检查结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("ORDERCHECK");
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("工单检查结束");
		PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		return 0;
	}
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		List<OrderCheckInfo>	listOrderCheckInfo = null;
		log.info("--- begin to get data ，时间:"+ new Date());
		// --- 提取数据暂不考虑一次提取多条纪录 ---
		if(bNeedGetData){
			m_iTotalNum = 0;
			if(getValidTenantIdAndAvitivtySeqId() == false){   // --- 无有效数据,退出 ---
				return null;
			}
			bNeedGetData = false;
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setSPONSOR("ORDERCHECK");
			PltCommonLogIns.setBUSI_CODE("GET");
			PltCommonLogIns.setBUSI_DESC("新活动编号");
			PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
			PltCommonLogIns.setBUSI_ITEM_2(String.valueOf(curActivitySeqId));
			PltCommonLogIns.setBUSI_ITEM_5(curDateId);
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			PltCommonLogIns.setBUSI_ITEM_1(null);
			PltCommonLogIns.setBUSI_ITEM_2(null);
			PltCommonLogIns.setBUSI_ITEM_5(null);
		}
		
		//获取当前月份，短信表入历史，判断短信发送完毕后 是几月就入几月的表
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		String		OrderInsertTime ;
		OrderInsertTime = sdfYm.format(new Date()).substring(5, 7);
		
		Calendar last = Calendar.getInstance();  
		last.setTime(new Date());
	    last.add(Calendar.MONTH,-1);//减一个月，变为下月的1 号
	    String OrderlastTime = sdfYm.format(last.getTime()).substring(5, 7);
				
		// --- 根据当前的租户编号，活动序列号，工单起始号提取数据 ---
		while(1 > 0){
			if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO") ||  m_strTableName == null){
				listOrderCheckInfo= BaseMapperIns.getOrderListForActivity(curTenantId, 
					curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_POPWIN")){
				listOrderCheckInfo= BaseMapperIns.getPopwinOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_WEIXIN")){
				listOrderCheckInfo= BaseMapperIns.getWeiXinOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_ONE")){
				listOrderCheckInfo= BaseMapperIns.getOneOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SMS_HIS_"+OrderInsertTime)){
				listOrderCheckInfo= BaseMapperIns.getSmsHisOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId,"PLT_ORDER_INFO_SMS_HIS_"+OrderInsertTime);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SMS_HIS_"+OrderlastTime)){
				listOrderCheckInfo= BaseMapperIns.getSmsHisOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId,"PLT_ORDER_INFO_SMS_HIS_"+OrderlastTime);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SCENEMARKET")){
				listOrderCheckInfo= BaseMapperIns.getScenemarketOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_REMAIN")){
				listOrderCheckInfo= BaseMapperIns.getRemainOrderListForActivity(curTenantId, 
						curActivitySeqId, curOrderRecId);
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_HIS")){
				listOrderCheckInfo= BaseMapperIns.getOrderHisListForActivity(curTenantId,
						curActivitySeqId, curOrderRecId);
			}/*else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_CALL")){
 				listOrderCheckInfo = BaseMapperIns.getCallOrderForAvtivity(curTenantId,
 						curActivitySeqId, curOrderRecId);  
 			}*/else{
				log.warn("表名:{} 不支持 !!!",m_strTableName);
				return null;
			}
			if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){   // --- 没有取到数据 ---
				log.info("租户ID:"+curTenantId +" 活动序列号:"+curActivitySeqId + "  起始工单号:"+curOrderRecId 
						+ "  无数据,总纪录数 " +m_iTotalNum );
				
				//synHandleData();   // --- 等待当前的序列号完全处理完  ---
				
				// --- 等待数据处理完成 --------------------
				//try{Thread.sleep(5000);}catch(Exception e){}
				
				//log.info("开始调用统计程序");
				// --- 调用统计程序统计 ---
				//if(m_iTotalNum > 0)
				//	StatisticServiceIns.statisticBench(curTenantId, String.valueOf(curActivitySeqId));
				//log.info("结束调用统计程序");
				// --------------------------
				
				// --- 判断下一个活动ID是否存在 ---
				if(getValidTenantIdAndAvitivtySeqId() == false){   // --- 无有效数据,退出 ---
					return null;
				}
				else{
					PltCommonLogIns.setSTART_TIME(new Date());
					PltCommonLogIns.setSPONSOR("ORDERCHECK");
					PltCommonLogIns.setBUSI_CODE("GET");
					PltCommonLogIns.setBUSI_DESC("新活动编号");
					PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
					PltCommonLogIns.setBUSI_ITEM_2(String.valueOf(curActivitySeqId));
					PltCommonLogIns.setBUSI_ITEM_5(curDateId);
					BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
					PltCommonLogIns.setBUSI_ITEM_1(null);
					PltCommonLogIns.setBUSI_ITEM_2(null);
					PltCommonLogIns.setBUSI_ITEM_5(null);
				}
				m_iTotalNum =0;
				continue;
			}
			else if(listOrderCheckInfo.size() < 5000){
			//	bNeedGetData = true;
				break;
			} // --- < 5000 ---
		    break;
		}// --- while (1 > 0)
		// --- 设置当前最大工单号 ---
		curOrderRecId = listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId();
		m_iTotalNum  += listOrderCheckInfo.size();
		log.info("租户ID:"+curTenantId +" 活动序列号:"+curActivitySeqId + "  起始工单号:"+curOrderRecId 
				+ "  数据条数: "+listOrderCheckInfo.size()  + "当前总条数:"+m_iTotalNum);
		
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("TENANTID", curTenantId);
		map.put("ACTIVITYSEQID", curActivitySeqId);
		map.put("ORDERCHECKINFO",listOrderCheckInfo);
		log.info("--- end to get data ，时间:"+ new Date());
		return map;
	}
	/*
	 * 处理
	 */
	@Override
	public		int		handle(Object data){
		
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;
		List<OrderCheckInfo>	listOrderCheckInfo = (List<OrderCheckInfo>)mapData.get("ORDERCHECKINFO");
		String			TenantId = (String)mapData.get("TENANTID");	
		int				ActivitySeqId = (int)mapData.get("ACTIVITYSEQID");
		log.info("--- begin to handle data 时间:"+new Date());
		StringBuilder str = new StringBuilder();
		
//		sb.append("/*!mycat:sql=select * FROM XCLOUD_uni076*/");
		str.append(BusiToolsIns.getValueFromGlobal("GETDATAFROMXCLOUD"));
//		GETDATAFROMXCLOUD:  /*!mycat:sql=select * FROM XCLOUD_TTTTTENANT_ID*/
		
		String sbsql = str.toString();
		String realsbsql = sbsql.replaceFirst("TTTTTENANT_ID", TenantId);
		StringBuilder sb = new StringBuilder(realsbsql);
		
		String			SucessTypeSql = getSqlForSucessType(TenantId,ActivitySeqId);
		if(SucessTypeSql == null)  return 0;
		sb.append(SucessTypeSql);
//		boolean bProduct = false;
//		if(SucessTypeSql.contains("(")) {        // --- 子查询 ---
//			bProduct = true;
//		}		
		//判断括号数量相不相同
		String sbString = sb.toString();
		int x = 0;
		int y = 0;
		for(int j = 0 ;j < sbString.length();j++){
			if(sbString.substring(j, j+1).equals("(")){
				x++;
			}
			if(sbString.substring(j, j+1).equals(")")){
				y++;
			}
		}
		
		sb.append(" AND USER_ID IN (");
		int i = 0;
		for(i = 0; i < listOrderCheckInfo.size();++i){
			if(i > 0) sb.append(",");
			sb.append("'");
			sb.append(listOrderCheckInfo.get(i).getUserId());
			sb.append("'");
		}
		sb.append(")");
		
		
		if(x==y){
			log.info("左右括号数量相同均为"+x);
		}else {
			sb.append(")");
			log.info("左右括号数量不相同，左括号为"+x+"右括号为"+y);
		}
		
//		if(bProduct)  sb.append(")");
		//log.info("sql="+sb.toString());
		//log.info("size={}",sb.length());
	    log.info("--SuccessSql--"+sb.toString());
		
	   
	    
		
		// --- 执行SQL ---
		List<Map<String,Object>>   listUserId  =(List<Map<String,Object>>)JdbcTemplateIns.queryForList(sb.toString());
		
		//一条成功标准sql插入一条日志
				PltCommonLog		logdb = new PltCommonLog();
//				int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
				logdb.setLOG_TYPE("22");
				logdb.setSERIAL_ID(SerialId);
				logdb.setSTART_TIME(new Date());
				logdb.setSPONSOR("orderCheck");
				logdb.setBUSI_CODE("getSqlForSucessType");
				logdb.setBUSI_DESC("Execute successfully+SuccessSql");
				logdb.setBUSI_ITEM_2(""+ActivitySeqId);
				logdb.setBUSI_ITEM_3(m_strTableName);
//				logdb.setBUSI_ITEM_4(sb.toString());
				String tmp = sb.toString();
				if(tmp.length()>1024){
					String tmp1 = tmp.substring(0, 1024);
					logdb.setBUSI_ITEM_1(tmp1);
				}else {
					logdb.setBUSI_ITEM_1(tmp);	
				}
				BusiToolsIns.insertPltCommonLog(logdb);
		
		if(listUserId == null || listUserId.size() ==0) {
			//log.info("无数据,sql:{}",sb.toString());
			return 0;              //  --- 无数据 ---
		}
		sb.setLength(0);
		for(i =0;i < listUserId.size();++i){
			if(i != 0) sb.append(",");
			sb.append("'");
			sb.append(listUserId.get(i).get("USER_ID"));
			sb.append("'");
		}
		

		//获取当前月份，短信表入历史，判断短信发送完毕后 是几月就入几月的表
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		String		OrderInsertTime ;
		OrderInsertTime = sdfYm.format(new Date()).substring(5, 7);
		
		Calendar last = Calendar.getInstance();  
		last.setTime(new Date());
	    last.add(Calendar.MONTH,-1);//减一个月，变为下月的1 号
	    String OrderlastTime = sdfYm.format(last.getTime()).substring(5, 7);
		
		// --- 更新工单总表成功状态 ---
		StringBuilder    sbb = new StringBuilder();
		if(m_strTableName == m_tableList.get(0).get("tableName") ){	
			for(HashMap<String, Object> tableName:m_tableList){
				sbb.setLength(0);
				sbb.append("UPDATE " +tableName.get("tableName")+" SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
				sbb.append(" WHERE TENANT_ID='");
				sbb.append(TenantId);
				sbb.append("'");
				sbb.append("  AND ACTIVITY_SEQ_ID =");
				sbb.append(ActivitySeqId);
				sbb.append("  AND USER_ID IN (");
				sbb.append(sb.toString());
				sbb.append(")");
				int result = JdbcTemplateIns.update(sbb.toString());
				log.info("--- end to handle data ,更新数量：{}  ,时间：{}",result,new Date());
			}
			
		}else{
			sbb.setLength(0);
			if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO") ||  m_strTableName == null){
				sbb.append("UPDATE PLT_ORDER_INFO SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_POPWIN")){
				sbb.append("UPDATE PLT_ORDER_INFO_POPWIN SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_WEIXIN")){
				sbb.append("UPDATE PLT_ORDER_INFO_WEIXIN SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_ONE")){
				sbb.append("UPDATE PLT_ORDER_INFO_ONE SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SMS_HIS_"+OrderInsertTime)){
				sbb.append("UPDATE PLT_ORDER_INFO_SMS_HIS_"+OrderInsertTime+" SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SMS_HIS_"+OrderlastTime)){
				sbb.append("UPDATE PLT_ORDER_INFO_SMS_HIS_"+OrderlastTime+" SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_SCENEMARKET")){
				sbb.append("UPDATE PLT_ORDER_INFO_SCENEMARKET SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_REMAIN")){
				sbb.append("UPDATE PLT_ORDER_INFO_REMAIN SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_HIS")){
				sbb.append("UPDATE PLT_ORDER_INFO_HIS SET CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
			}
			/*else if(m_strTableName.equalsIgnoreCase("PLT_ORDER_INFO_CALL")){
 				sbb.append("UPDATE PLT_ORDER_INFO_CALL SET CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "+curDateId+" ");
 			}*/
			sbb.append(" WHERE TENANT_ID='");
			sbb.append(TenantId);
			sbb.append("'");
			sbb.append("  AND ACTIVITY_SEQ_ID =");
			sbb.append(ActivitySeqId);
			sbb.append("  AND USER_ID IN (");
			sbb.append(sb.toString());
			sbb.append(")");
			int result = JdbcTemplateIns.update(sbb.toString());
			log.info("--- end to handle data ,更新数量：{}  ,时间：{}",result,new Date());
		}		
		return 0;
	}
	/*
	 * 得到当前的租户编号，活动序列号
	 */
	private	boolean	getValidTenantIdAndAvitivtySeqId(){
		int    Activity_Seq_Id = -1;
		int 	i = 0;
		if(curTenantId == null){   // --- 第一次提取 ---
			for(i = 0;i < listTenantInfo.size();++i){
				String		TENANT_ID = (String)listTenantInfo.get(i).get("TENANT_ID");  // --- 得到租户编号 ---
				Activity_Seq_Id = getValidActivitySeqIdForTenantId(TENANT_ID,-1);
				if(Activity_Seq_Id != -1){
					curTenantId = TENANT_ID;
					curActivitySeqId = Activity_Seq_Id;
					curOrderRecId = 0;
					return true;
				}
			}	
			return false;   // --- 到这一步表明无合适的数据 ---
		}// --- if ----
		for(i = 0;i < listTenantInfo.size();++i){
			String		TENANT_ID = (String)listTenantInfo.get(i).get("TENANT_ID");  // --- 得到租户编号 ---
			if(curTenantId.equals(TENANT_ID)){   
				// --- 判断是否还有后续的活动序列号 ---
				Activity_Seq_Id = getValidActivitySeqIdForTenantId(TENANT_ID,curActivitySeqId);
				log.info("curActivitySeqId={},Activity_Seq_Id={}",curActivitySeqId,Activity_Seq_Id);
				if(Activity_Seq_Id  != -1) {    // --- 找到了下一个活动序列号 ---
					curActivitySeqId = Activity_Seq_Id;
					curOrderRecId = 0;
					return true;
				}
				else{  // --- 没有下一个序列号 ---
					if(i +1 ==  listTenantInfo.size()){  // --- 已经是最后一个了 ---
						return false;
					}
					else{   // --- 此时启用下一个租户 (递归调用)---
						curTenantId =  (String)listTenantInfo.get(i+1).get("TENANT_ID");  
						curActivitySeqId = -1;
						curOrderRecId = 0;
						return getValidTenantIdAndAvitivtySeqId(); 
					}
				}
			}// --- if(curTenantId.equals(TENANT_ID)){   ----
			
		}
		// --- 如果到这里则是没有找到数据 ---
		return false;
	}
	/*
	 * 得到租户编号下面的下一个有效的活动序列号
	 */
	private	int		getValidActivitySeqIdForTenantId(String TenantId,int ActivitySeqId){
		List<ActivitySucessInfo> listTmp = BaseMapperIns.getActivityForTenantId(TenantId);
		if(listTmp == null || listTmp.size() == 0){
			return -1;
		}
		if(ActivitySeqId == -1){   // --- 先前未提取过 ---
			return listTmp.get(0).getRecId();
		}
		for(int i=0; i <  listTmp.size() ;++i){
			if(listTmp.get(i).getRecId() == ActivitySeqId){
				if(i +1  ==  listTmp.size() )  return -1;   // --- 无下一个 ---
				else{
					log.info("返回id:"+listTmp.get(i+1).getRecId());
					return listTmp.get(i+1).getRecId();
				}
			}
		}
		// --- 如果走到这一步，证明传入的序列号和租户编号无关  ---
		return -1;
	}
	/*
	 * 根据活动序列号得到成功标准
	 */
	private	ActivitySucessInfo getActivitySucessInfo(String tenant_id,int activity_seq_id){
		List<ActivitySucessInfo>  listAcitvitySucessInfo = mapActivitySucessInfo.get(tenant_id);
		int i=0;
		for(i=0;i <listAcitvitySucessInfo.size();++i ){
			if(listAcitvitySucessInfo.get(i).getRecId() == activity_seq_id) break;
		}
		if(i == listAcitvitySucessInfo.size() ){   // --- 活动数据发生了变化 ---
			return null;
		}
		return listAcitvitySucessInfo.get(i);
	}
	/*
	 * 根据活动序列号生成对应的SQL
	 */
//	private	String		getSqlForSucessType(String sucess_type){
	private	String		getSqlForSucessType(String tenant_id,int activity_seq_id){
		// --- 得到活动成功信息 ---
		ActivitySucessInfo	ActivitySucessInfoIns = getActivitySucessInfo(tenant_id,activity_seq_id);
		if(ActivitySucessInfoIns == null)  return null;
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false){
			return null;
		}
		//if(ActivitySucessInfoIns.getSucessType() == null)   return null;
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		if(ActivitySucessInfoIns.getLastOrderCreateTime() != null){
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		}
		else{
			OrderCreateTime = sdfYm.format(new Date()) +"-01";
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())){
			if(ActivitySucessInfoIns.getMatchingType().equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				List<String>   listProductCode = mapActivityProduct.get(tenant_id+"-"+ActivitySucessInfoIns.getACTIVITY_SEQ_ID()); 
				bProduct = true; 
				int		i = 0;
				if(listProductCode != null && listProductCode.size() > 0){   // --- 得到SQL  ---
					 
					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for(i = 0;i < listProductCode.size();++i){
						if(i > 0)  sbProduct.append(",");
							sbProduct.append("'");
							sbProduct.append(listProductCode.get(i));
							sbProduct.append("'");
						}
						sbProduct.append(")");
						sbProduct.append(" AND ACCEPTED_DATE > '");
						sbProduct.append(OrderCreateTime);
						sbProduct.append("'  ");
				}else {
					log.info("精准匹配产品时，无产品编码");
					return null;	
				}
			}
		}
		
		StringBuilder		sb = new StringBuilder();
		String				strAddSql = null;
		String strTmp = null;
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())){
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		//精准营销时
		if(bProduct){
			if(strTmp!=null){                 
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("("+strTmp+")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")"  );
				}
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					sbProduct.append(" AND " + "("+strAddSql+")"  );
				}
			}
			log.info("精准营销：");
//			log.info("工单检查sql="+sbProduct.toString()+"), 精准营销时，成功类型="+successType);
//			return sbProduct.toString();
		}else{
			//无精准营销但有成功标准
			if(strTmp!=null){
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("("+strTmp+")");
				sb.append(" AND DATE_ID =  '");
				sb.append(curDateId);
				sb.append("'");
				if(strAddSql!=null){
					sb.append(" AND " + "("+strAddSql+")"  );
				}
//				log.info("工单检查sql="+sb.toString()+"), 成功类型="+successType);
//				return sb.toString();
			}else{                                  // --- 无成功标准  ---
				if(successType.equals("3")||successType.equals("4")||successType.equals("5")){       //--- 成功标准类型在3,4,5之间时
					sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='");
					if (successType.equals("3")) {
						sb.append("03");
					}
					if (successType.equals("4")) {
						sb.append("02");
					}
					if (successType.equals("5")) {
						sb.append("01");
					}
					sb.append("' AND ACCEPTED_DATE > '");
					sb.append(OrderCreateTime);
					sb.append("' AND USER_ID IN ( ");
				}
				if(strAddSql!=null){
					sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
					sb.append("("+strAddSql+")");
					sb.append(" AND DATE_ID = '");
					sb.append(curDateId);
					sb.append("' ");
//					log.info("工单检查sql="+sb.toString()+"), 成功类型="+successType);
//					return sb.toString();
				}else {                                 // --- 全没有时  ---
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}			
			}	
		}
		
			log.info("工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType);
			
			//一条成功标准sql插入一条日志
			PltCommonLog		logdb = new PltCommonLog();
	//		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
			logdb.setLOG_TYPE("22");
			logdb.setSERIAL_ID(SerialId);
			logdb.setSTART_TIME(new Date());
			logdb.setSPONSOR("orderCheck");
			logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType);
			logdb.setBUSI_DESC("SusccessSql+select还未加USER_ID");
			logdb.setBUSI_ITEM_1(sb.toString()+sbProduct.toString());
			logdb.setBUSI_ITEM_2(""+activity_seq_id);
			logdb.setBUSI_ITEM_3(m_strTableName);
			BusiToolsIns.insertPltCommonLog(logdb);
			
			if(bProduct){
				return sbProduct.toString();
			}else {
				return sb.toString();
			}
			
//		if(ActivitySucessInfoIns.getSucessType().equals("1")){   // --- 换卡 ---
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE IS_SIM = '0' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_18 = '0'  ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_18 = '0' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sb.append(" AND ");
//					sb.append(strAddSql);
//				}
//			}
//			
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("2")){  // --- 换机 ---	
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE TE_N_04='4' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TE_N_04='4' ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TE_N_04='4' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sb.append(" AND ");
//					sb.append(strAddSql);
//				}
//			}
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("3")){  // --- 办理流量包 ---
//			if(bProduct){ 
//				if(strAddSql != null){
//					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sbProduct.append(strAddSql);
//					sbProduct.append(" AND DATE_ID = '");
//					sbProduct.append(curDateId);
//					sbProduct.append("'  ");
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='03' ");
//				sb.append(" AND ACCEPTED_DATE > '");
//				sb.append(OrderCreateTime);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sb.append(strAddSql);
//					sb.append(" AND DATE_ID = '");
//					sb.append(curDateId);
//					sb.append("'  ");
//				}
//			}
//			//return sbProduct.toString();
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("4")){  // --- 办理续约 ---
//			if(bProduct){  
//				if(strAddSql != null){
//					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sbProduct.append(strAddSql);
//					sbProduct.append(" AND DATE_ID = '");
//					sbProduct.append(curDateId);
//					sbProduct.append("'  ");
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='02' ");
//				sb.append(" AND ACCEPTED_DATE > '");
//				sb.append(OrderCreateTime);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sb.append(strAddSql);
//					sb.append(" AND DATE_ID = '");
//					sb.append(curDateId);
//					sb.append("'  ");
//				}
//			}
//			//return sbProduct.toString();
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("5")){  // --- 换套餐 ---
//			if(bProduct){
//				if(strAddSql != null){
//					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sbProduct.append(strAddSql);
//					sbProduct.append(" AND DATE_ID = '");
//					sbProduct.append(curDateId);
//					sbProduct.append("'  ");
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='01' ");
//				sb.append(" AND ACCEPTED_DATE > '");
//				sb.append(OrderCreateTime);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sb.append(strAddSql);
//					sb.append(" AND DATE_ID = '");
//					sb.append(curDateId);
//					sb.append("'  ");
//				}
//			}
//			//return sbProduct.toString();
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("6")){  // --- 实名登记 ---
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE BA_S_04='1' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE BA_S_04='1' ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE BA_S_04='1' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("7")){  // --- 宽带续约 ---
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE KD_IS_KDDQX='1' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE KD_IS_KDDQXQ='1' ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE KD_IS_KDDQXQ='1' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("8")){  // --- 办理副卡 ---
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE CHR_17='1' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_17='1' ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_17='1' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("9")){  // --- 4G登网 ---
//			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
//				sbProduct.append(" AND DATE_ID = '");
//				sbProduct.append(curDateId);
//				sbProduct.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//			else{
//				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
//				sb.append(" AND DATE_ID = '");
//				sb.append(curDateId);
//				sb.append("'  ");
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//			}
//		}
//		else if(ActivitySucessInfoIns.getSucessType().equals("10")){ // --- 承诺抵消 ---
//			if(bProduct){
//				if(strAddSql != null){
//					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sbProduct.append(strAddSql);
//					sbProduct.append(" AND DATE_ID = '");
//					sbProduct.append(curDateId);
//					sbProduct.append("'  ");
//				}
//			}
//			else{
//				if(strAddSql != null){
//					sb.append("  SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
//					sb.append(strAddSql);
//					sb.append(" AND DATE_ID = '");
//					sb.append(curDateId);
//					sb.append("'  ");
//				}
//				else{
//					log.error("低消没有指定条件 ");
//					return null;  // --- 低销必须要指定产品，否则指定条件，什么都不指定不执行 ---
//				}
//			}
//			//return sbProduct.toString();
//		}
//		else{   // --- 非约定成功类型 ---
//			if(StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())== false){
//				log.error("成功类型为10以后，但没有对应的SQL语句:SUCCESS_TYPE_CON_SQL is null");
//				return null;
//			}
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( ");
//				sbProduct.append(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL());
//				String   strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().toUpperCase();
//				if(strTmp.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1){
//					sbProduct.append(" AND DATE_ID = '");
//					sbProduct.append(curDateId);
//					sbProduct.append("'  ");
//				}
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//				//sbProduct.append(")");
//				log.info("sql={}",sbProduct.toString());
//			}
//			else{
//				sb.append(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL());
//				String   strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().toUpperCase();
//				if(strTmp.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1){
//					sb.append(" AND DATE_ID = '");
//					sb.append(curDateId);
//					sb.append("'  ");
//				}
//				if(strAddSql != null){
//					sbProduct.append(" AND ");
//					sbProduct.append(strAddSql);
//				}
//				log.info("sql={}",sb.toString());
//			}	
//		}
//		log.info(" 工单检查:{},{}",sb.toString(),sbProduct.toString());
//		if(bProduct){
//			return sbProduct.toString();
//		}
//		else{
//			return sb.toString();
//		}
		//return sb.toString();
	}

}
