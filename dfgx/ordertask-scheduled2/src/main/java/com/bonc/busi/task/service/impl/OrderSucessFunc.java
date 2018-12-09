package com.bonc.busi.task.service.impl;
/*
 * @desc:工单成功检查函数，主要的逻辑在这里
 * @author:曾定勇
 * @time:2016-11-26
 */



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.utils.HttpUtil;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.busi.task.base.SysVars;

@Service("OrderSucessFunc")
@Transactional
public class OrderSucessFunc extends ThreadBaseFunction{
	// --- 定义日志变量 ---
	private final static Logger log = LoggerFactory.getLogger(OrderSucessFunc.class);
	// --- 定义spring jdbcTemplate 模板 ---
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	// --- 定义mybatis支持的mapper dao ---
	@Autowired
	BaseMapper		TaskBaseMapperDao;
	@Autowired
	private BusiTools  BusiTools;

	
	// --- 定义当前帐期变量 ---
	String		curDateId = null;
	// --- 定义当前租户变量 ---
	String		curTenantId = null;
	// --- 定义当前活动变量 ---
	int		curActivitySeqId = -1;
	// --- 定义当前工单变量 ---
	long			curOrderRecId = -1;
	// --- 定义租户保存变量 ---
	List<Map<String,Object>>   listTenantInfo = null;
	// --- 定义保存租户成功条件的变量 ---
	Map<String,List<ActivitySucessInfo>>  mapActivitySucessInfo= new HashMap<String,List<ActivitySucessInfo>>();
	// --- 定义活动对应的产品列表 ---
	Map<String,List<String>>  mapActivityProduct= new HashMap<String,List<String>>();
	// --- 定义是否需要调用查询有效租户和活动的变更 ---
	boolean		bNeedGetData = true;
		
	/*
	 * 获取数据（建议一次从数据库中取一批数据，如：500,2000
	 */
	@Override
	public	synchronized Object	getData(){
		
		List<OrderCheckInfo>	listOrderCheckInfo = null;
		log.info("--- begin to get data ，时间:"+ new Date());
		// --- 提取数据暂不考虑一次提取多条纪录 ---
		if(bNeedGetData){
			if(getValidTenantIdAndAvitivtySeqId() == false){   // --- 无有效数据,退出 ---
				return null;
			}
			bNeedGetData = false;
		}
		// --- 根据当前的租户编号，活动序列号，工单起始号提取数据 ---
		while(1 > 0){
			listOrderCheckInfo= TaskBaseMapperDao.getOrderListForActivity(curTenantId, 
					curActivitySeqId, curOrderRecId);
			if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){   // --- 没有取到数据 ---
				log.info("租户ID:"+curTenantId +" 活动序列号:"+curActivitySeqId + "  起始工单号:"+curOrderRecId 
						+ "  无数据 ");
				// --- 判断下一个活动ID是否存在 ---
				if(getValidTenantIdAndAvitivtySeqId() == false){   // --- 无有效数据,退出 ---
					return null;
				}
				continue;
			}
			else if(listOrderCheckInfo.size() < 2000){
				bNeedGetData = true;
				break;
			} // --- < 2000 ---
		    break;
		}// --- while (1 > 0)
		// --- 设置当前最大工单号 ---
		curOrderRecId = listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId();
		log.info("租户ID:"+curTenantId +" 活动序列号:"+curActivitySeqId + "  起始工单号:"+curOrderRecId 
				+ "  数据条数: "+listOrderCheckInfo.size());
		
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("TENANTID", curTenantId);
		map.put("ACTIVITYSEQID", curActivitySeqId);
		map.put("ORDERCHECKINFO",listOrderCheckInfo);
		log.info("--- end to get data ，时间:"+ new Date());
		return map;
	}
	/*
	 * 执行数据（建议批量执行）
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int	handleData(Object data){
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;
		List<OrderCheckInfo>	listOrderCheckInfo = (List<OrderCheckInfo>)mapData.get("ORDERCHECKINFO");
		String			TenantId = (String)mapData.get("TENANTID");
		int				ActivitySeqId = (int)mapData.get("ACTIVITYSEQID");
		log.info("--- begin to handle data 时间:"+new Date());
		StringBuilder		sb = new StringBuilder();
		String			SucessTypeSql = getSqlForSucessType(TenantId,ActivitySeqId);
		if(SucessTypeSql == null)  return 0;
		sb.append(SucessTypeSql);
		boolean bProduct = false;
		if(SucessTypeSql.contains("(")) {        // --- 子查询 ---
			bProduct = true;
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
		if(bProduct)  sb.append(")");
		//log.info("sql="+sb.toString());
		//log.info("size={}",sb.length());
		
		// --- 执行SQL ---
		List<Map<String,Object>>   listUserId  =(List<Map<String,Object>>)jdbcTemplate.queryForList(sb.toString());
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
		// --- 更新工单总表成功状态 ---
		StringBuilder    sbb = new StringBuilder();
		sbb.setLength(0);
		sbb.append("UPDATE PLT_ORDER_INFO SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ");
		sbb.append(" WHERE TENANT_ID='");
		sbb.append(TenantId);
		sbb.append("'");
		sbb.append("  AND ACTIVITY_SEQ_ID =");
		sbb.append(ActivitySeqId);
		sbb.append("  AND USER_ID IN (");
		sbb.append(sb.toString());
		sbb.append(")");
		int result = jdbcTemplate.update(sbb.toString());
	
		log.info("--- end to handle data ,时间："+ new Date());
		return 0;
	}
	/*
	 * 初始化变量
	 */
	private	void		clear(){
		curDateId = null;
		curTenantId = null;
		curActivitySeqId = -1;
		curOrderRecId = -1;
		listTenantInfo = null;
		mapActivitySucessInfo = null;
		mapActivitySucessInfo= new HashMap<String,List<ActivitySucessInfo>>();
		mapActivityProduct = null;
		mapActivityProduct= new HashMap<String,List<String>>();
		bNeedGetData = true;
	}
	/*
	 * 开始
	 */
	@Override
	public	int	begin(){
		log.info("--- 工单成功检查启动 时间:"+new Date());
		// --- 纪录日志表  ---
		int  SerialId = BusiTools.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("01");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("ORDERCHECK."+Thread.currentThread().getName());
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单检查开始");
		BusiTools.insertPltCommonLog(PltCommonLogIns);
		// --- 初始化相关变量 ---
		clear();
		
		// ---取当前帐期 --
//		curDateId = TaskBaseMapperDao.getMaxDateId();
		String  monthTimeUrl  = BusiTools.getValueFromGlobal("GET_MONTH_TIME_"+SysVars.getTenantId());
        curDateId = HttpUtil.sendPost(monthTimeUrl, "");
		if(curDateId == null){
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			// --- 当前天数减一 ---
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();
			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间

			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd"); //设置时间格式
			curDateId = sdf.format(dBefore);    //格式化前一天
		}
		log.debug(" 当前帐期值 :"+curDateId);
		StringBuilder   sb = new StringBuilder();
		// --- 提取租户信息 ---
		sb.setLength(0);
		sb.append(" SELECT TENANT_ID FROM TENANT_INFO WHERE STATE='1' ");
		listTenantInfo  =(List<Map<String,Object>>)jdbcTemplate.queryForList(sb.toString());
		if(listTenantInfo == null || listTenantInfo.size() == 0){   // ---  无有效租户纪录 ---
			log.error("--- 无有效租户纪录---");
			Exception e = new Exception(" 无有效租户纪录");
			throw new RuntimeException(e);
		}
		else{   // --- 设置当前租户编号  ---
			curTenantId = (String)listTenantInfo.get(0).get("TENANT_ID");
		}
		// --- 提取租户对应的活动信息 ---
		for(Map<String,Object> mapTenantInfo:listTenantInfo){
			String		TENANT_ID = (String)mapTenantInfo.get("TENANT_ID");
			List<ActivitySucessInfo> listTmp = TaskBaseMapperDao.getActivityForTenantId(TENANT_ID);
			// --- 根据每个活动提取活动对应的产品列表 ---
			if(listTmp != null && listTmp.size() > 0){
				mapActivitySucessInfo.put(TENANT_ID, listTmp);
				for(ActivitySucessInfo item:listTmp){
					List<String>   listTemp = TaskBaseMapperDao.getProductListForActivity(item.getACTIVITY_SEQ_ID(),TENANT_ID);
					if(listTemp != null && listTemp.size() > 0){
						mapActivityProduct.put(TENANT_ID+"-"+item.getACTIVITY_SEQ_ID(), listTemp);
					}
				}
			}
		}// --- for(Map< ---	
		return 0;
	}
	/*
	 * 结束
	 */
	@Override
	public	int	end(){
		log.info("--- 工单成功检查结束 ,时间:"+ new Date());
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("01");
		//PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("ORDERCHECK."+Thread.currentThread().getName());
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("工单检查结束");
		BusiTools.insertPltCommonLog(PltCommonLogIns);
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
		List<ActivitySucessInfo> listTmp = TaskBaseMapperDao.getActivityForTenantId(TenantId);
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
			OrderCreateTime = sdfYm.format(new Date()) +"01";
		}
		if(ActivitySucessInfoIns.getMatchingType().equals("2")){  // --- 精确匹配产品  ---
			// --- 提取活动成功产品列表 ---
			List<String>   listProductCode = mapActivityProduct.get(tenant_id+"-"+ActivitySucessInfoIns.getACTIVITY_SEQ_ID());;   
			int		i = 0;
			if(listProductCode != null && listProductCode.size() > 0){   // --- 得到SQL  ---
				bProduct = true;  
				sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE ACCEPTED_ID IN (");
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
			}
		}
		StringBuilder		sb = new StringBuilder();
		if(ActivitySucessInfoIns.getSucessType().equals("1")){   // --- 换卡 ---
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE IS_SIM = '0' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE IS_SIM = '0'  ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");

				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE IS_SIM = '0' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("2")){  // --- 换机 ---	
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE TE_N_04='4' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TE_N_04='4' ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TE_N_04='4' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("3")){  // --- 办理流量包 ---
			if(bProduct){ 
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='03' ");
				sb.append(" AND ACCEPTED_DATE > '");
				sb.append(OrderCreateTime);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			//return sbProduct.toString();
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("4")){  // --- 办理续约 ---
			if(bProduct){  
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='02' ");
				sb.append(" AND ACCEPTED_DATE > '");
				sb.append(OrderCreateTime);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			//return sbProduct.toString();
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("5")){  // --- 换套餐 ---
			if(bProduct){
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='01' ");
				sb.append(" AND ACCEPTED_DATE > '");
				sb.append(OrderCreateTime);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			//return sbProduct.toString();
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("6")){  // --- 实名登记 ---
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE BA_S_0='1' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE BA_S_0='1' ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE BA_S_0='1' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("7")){  // --- 宽带续约 ---
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE KD_IS_KDDQX='1' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE KD_IS_KDDQXQ='1' ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//	if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE KD_IS_KDDQXQ='1' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("8")){  // --- 办理副卡 ---
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE CHR_17='1' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_17='1' ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE CHR_17='1' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("9")){  // --- 4G登网 ---
			//sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
			if(bProduct){
				sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
				sbProduct.append(" AND DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
			else{
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE TYPE_HYBL_CUST_TYPE='40AAAAAA' ");
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'  ");
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sb.append(" AND ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
				}
			}
		}
		else if(ActivitySucessInfoIns.getSucessType().equals("10")){ // --- 承诺抵消 ---
			if(bProduct){
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
				//if(ActivitySucessInfoIns.getSucessConSql() != null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
					sbProduct.append(" AND DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'  ");
				}
			}
			else{
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
					sb.append("  SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
					sb.append(" AND DATE_ID = '");
					sb.append(curDateId);
					sb.append("'  ");
				}
				else{
					log.error("低消没有指定条件 ");
					return null;  // --- 低销必须要指定产品，否则指定条件，什么都不指定不执行 ---
				}
			}
			//return sbProduct.toString();
		}
		else{   // --- 非约定成功类型 ---
			if(bProduct){
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
					sbProduct.append("  AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE ");
					sbProduct.append(ActivitySucessInfoIns.getSucessConSql());
					sbProduct.append(" AND DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'  ");
				}
			}
			else{
				if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
					sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO  WHERE ");
					sb.append(ActivitySucessInfoIns.getSucessConSql());
					sb.append(" AND DATE_ID = '");
					sb.append(curDateId);
					sb.append("'  ");
				}
				else{
					log.error("成功类型为其它，但没有指定条件");
					return null;   // --- 必须有条件，否则无法判断  ---
				}
			}
		}
		log.info(" 工单检查:{},{}",sb.toString(),sbProduct.toString());
		if(bProduct){
			return sbProduct.toString();
		}
		else{
			return sb.toString();
		}
		//return sb.toString();
	}
	

}
