package com.bonc.busi.task.instance;
/*
 * @desc:工单生成后过滤已经具备成功条件的工单
 * @author:曾定勇
 * @time:2016-01-08
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.h2.util.TempFileDeleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.busi.task.bo.ActivitySucessInfo;
//import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.activity.SuccessProductPo;

public class OrderFilterSucess extends ParallelFunc{
	
	

	@Autowired
	private BusiTools  AsynDataIns;
	
	private final static Logger 		log= LoggerFactory.getLogger(OrderFilterSucess.class);
	private	JdbcTemplate 			JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	private	BusiTools					BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	BaseMapper				BaseMapperIns = SpringUtil.getBean(BaseMapper.class);
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	Date							dateBegin = null;    // --- 开始时间  ---
	private	Date							dateCur = null;    // --- 当前时间  ---
	private	int  							SerialId =0;   // --- 日志序列号 ---

	private	int							m_iCurTablePos = 0;
	
	// --- 对当前的表名进行排序
	private List<HashMap<String, Object>>                 tableRecs = null  ;
	

	// --- 定义当前工单变量 ---
	private	long							curOrderRecId = 0;
	// --- 定义工单数量变量  ---
	private	int							m_iTotalRecs = 0;
	// --- 定义当前帐期变量 ---
	private	String						curDateId = null;
	// --- 定义活动序列号 ---
	private	int							m_iActivitySeqId = -1;
	// --- 定义租户编号  ---
	private	String						m_strTenantId = null;
	// --- 定义成功条件的PO ---
	public		SuccessStandardPo	m_cssSuccessStandardPo= null;
	// --- 定义当前操作的是哪个表 ---
	//private	String						m_strTableName = null;
	// --- 定义成功SQL ---
	private	String						m_strSucessSql = null;
		

	

	public void setTable(List<HashMap<String, Object>> tableRecs) {
		this.tableRecs = tableRecs;
	}

	public		void		setSuccessStandardPo(SuccessStandardPo SuccessStandardPo){
		this.m_cssSuccessStandardPo = SuccessStandardPo;
	}
	
	public		void		setTenantId(String TenantId){
		this.m_strTenantId = TenantId;
	}
	
	public		void		setActivitySeqid(int ActivitySeqId){
		this.m_iActivitySeqId = ActivitySeqId;
	}
	
	
	
	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		// --- 纪录开始时间 ---
		dateBegin = new Date();
		dateCur = new Date();
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		PltCommonLogIns.setLOG_TYPE("21");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("OrderFilterSucess");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单成功过滤检查开始");
		PltCommonLogIns.setDEST_NUM(m_iActivitySeqId);
		PltCommonLogIns.setBUSI_ITEM_4(m_strTenantId);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		// --- 得到最大帐期 ---
		curDateId = BaseMapperIns.getMaxDateId();
		if(curDateId == null){
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，curDateid为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		m_strSucessSql = getSqlForSucessType();
		if(m_strSucessSql == null){
			log.warn("--- 取成功条件SQL得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，成功条件SQL为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		return 0;
	}
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		Date				dateEnd = new Date();
		log.info("--- 工单成功过滤检查结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("工单成功过滤检查结束");
		PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		return 0;
	}
	/*
	 * 提取数据
	 */
	@Override
	public	Object		get(){
		// --- 根据不同的表名从不同的表中提取工单 ---	
		List<OrderCheckInfo>	listOrderCheckInfo = null;
		while(1> 0){
			switch(m_iCurTablePos){
				case 0:
					listOrderCheckInfo= BaseMapperIns.getOrderListForSucess(m_strTenantId, 
							m_iActivitySeqId, curOrderRecId, String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
					break;
				case 1:
					listOrderCheckInfo= BaseMapperIns.getOrderListForSucess(m_strTenantId, 
							m_iActivitySeqId, curOrderRecId, String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
					break;
				case 2:
					listOrderCheckInfo= BaseMapperIns.getOrderListForSucess(m_strTenantId, 
							m_iActivitySeqId, curOrderRecId, String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
					break;
				case 3:
					listOrderCheckInfo= BaseMapperIns.getOrderListForSucess(m_strTenantId, 
							m_iActivitySeqId, curOrderRecId, String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
					break;
				case 4:
					listOrderCheckInfo= BaseMapperIns.getOrderListForSucess(m_strTenantId, 
							m_iActivitySeqId, curOrderRecId, String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
					break;
				default:
					return null;	
			}
			if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){   // --- 没有取到数据 ---
				// --- 纪录一个表结束了  ---
				PltCommonLog			PltCommonLogLocal = new PltCommonLog();
				PltCommonLogLocal.setLOG_TYPE("21");
				PltCommonLogLocal.setSERIAL_ID(SerialId);
				PltCommonLogLocal.setSPONSOR("OrderFilterSucess");
				PltCommonLogLocal.setDEST_NUM(m_iActivitySeqId);
				PltCommonLogLocal.setBUSI_ITEM_4(m_strTenantId);
				PltCommonLogLocal.setSTART_TIME(new Date());
				PltCommonLogLocal.setBUSI_CODE("TABLE END");
				PltCommonLogLocal.setBUSI_DESC(String.valueOf(m_iCurTablePos));
				PltCommonLogLocal.setDURATION((int)(new Date().getTime() - dateCur.getTime()));
				BusiToolsIns.insertPltCommonLog(PltCommonLogLocal);
				// --- 开始下一个表  ---
				dateCur = new Date();
				m_iCurTablePos++;
				
//				if(m_iCurTablePos >= tableRecs.size() - 1)
//					break;
				
				curOrderRecId = 0;
				m_iTotalRecs = 0;
				continue;
			}	
			break;
		}
		// --- 设置当前最大工单号 ---
		curOrderRecId = listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId();
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("ORDERCHECKINFO",listOrderCheckInfo);
		map.put("CURTABLEPOS",m_iCurTablePos);
		m_iTotalRecs  += listOrderCheckInfo.size();
		// --- 纪录  ---
		PltCommonLog			PltCommonLogLocal = new PltCommonLog();
		PltCommonLogLocal.setLOG_TYPE("21");
		PltCommonLogLocal.setSERIAL_ID(SerialId);
		PltCommonLogLocal.setSPONSOR("OrderFilterSucess");
		PltCommonLogLocal.setDEST_NUM(m_iActivitySeqId);
		PltCommonLogLocal.setBUSI_ITEM_4(m_strTenantId);
		PltCommonLogLocal.setSTART_TIME(new Date());
		PltCommonLogLocal.setBUSI_CODE("GET");
		PltCommonLogLocal.setBUSI_DESC(String.valueOf(m_iCurTablePos));
		PltCommonLogLocal.setBUSI_NUM_1(m_iTotalRecs);
		PltCommonLogLocal.setBUSI_NUM_2(listOrderCheckInfo.size());
		PltCommonLogLocal.setBUSI_ITEM_2(String.valueOf(listOrderCheckInfo.get(0).getOrderRedId()));
		PltCommonLogLocal.setBUSI_ITEM_3(String.valueOf(curOrderRecId));
		BusiToolsIns.insertPltCommonLog(PltCommonLogLocal);
	
		return map;
	
	}
	/*
	 * 处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public		int		handle(Object data){
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;
		List<OrderCheckInfo>	listOrderCheckInfo = (List<OrderCheckInfo>)mapData.get("ORDERCHECKINFO");
		int				curTablePos = (int)mapData.get("CURTABLEPOS");
		StringBuilder		sb = new StringBuilder();
		sb.append(m_strSucessSql);
		
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
		
		// --- 加入用户id ---
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
				
		// --- 执行SQL ---
		List<Map<String,Object>>   listUserId  =(List<Map<String,Object>>)JdbcTemplateIns.queryForList(sb.toString());
		
		//一条成功标准sql插入一条日志
		PltCommonLog		logdb = new PltCommonLog();
//		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("221");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("工单过滤sql");
		logdb.setBUSI_CODE("getSqlForSucessType");
		logdb.setBUSI_DESC("Execute successfully+SuccessSql");
		logdb.setBUSI_ITEM_2(""+m_iActivitySeqId);
		logdb.setBUSI_ITEM_3(String.valueOf(m_iCurTablePos));
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
			PltCommonLog			PltCommonLogLocal = new PltCommonLog();
			PltCommonLogLocal.setLOG_TYPE("21");
			PltCommonLogLocal.setSERIAL_ID(SerialId);
			PltCommonLogLocal.setSPONSOR("OrderFilterSucess");
			PltCommonLogLocal.setDEST_NUM(m_iActivitySeqId);
			PltCommonLogLocal.setBUSI_ITEM_4(m_strTenantId);
			PltCommonLogLocal.setSTART_TIME(new Date());
			PltCommonLogLocal.setBUSI_CODE("HANDLE");
			PltCommonLogLocal.setBUSI_DESC(String.valueOf(tableRecs.get(curTablePos).get("tableName")));
			PltCommonLogLocal.setBUSI_NUM_1(listOrderCheckInfo.size());
			PltCommonLogLocal.setBUSI_NUM_2(0);
			PltCommonLogLocal.setBUSI_ITEM_2(String.valueOf(listOrderCheckInfo.get(0).getOrderRedId()));
			PltCommonLogLocal.setBUSI_ITEM_3(String.valueOf(listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId()));
			BusiToolsIns.insertPltCommonLog(PltCommonLogLocal);
	
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
		
		// --- 如果是第一张表，同时更新其他表数据 ---
		if (curTablePos == 0) {
			for(int t = 0;t<tableRecs.size();t++){
				sbb.append("UPDATE "+tableRecs.get(t).get("tableName"));
				sbb.append(" SET  ORDER_STATUS = '6' ");
				sbb.append(" WHERE TENANT_ID='");
				sbb.append(m_strTenantId);
				sbb.append("'");
				sbb.append("  AND ACTIVITY_SEQ_ID =");
				sbb.append(m_iActivitySeqId);
				sbb.append("  AND USER_ID IN (");
				sbb.append(sb.toString());
				sbb.append(")");
				int result = JdbcTemplateIns.update(sbb.toString());
				sbb.setLength(0); 
				log.info("第 " +t+ "波,工单过 滤成功,activityseqid={},更新数量={}",m_iActivitySeqId,result);
				log.info(""+tableRecs);
				PltCommonLog			PltCommonLogLocal = new PltCommonLog();
				PltCommonLogLocal.setLOG_TYPE("21");
				PltCommonLogLocal.setSERIAL_ID(SerialId);
				PltCommonLogLocal.setSPONSOR("OrderFilterSucess");
				PltCommonLogLocal.setDEST_NUM(m_iActivitySeqId);
				PltCommonLogLocal.setBUSI_ITEM_4(m_strTenantId);
				PltCommonLogLocal.setSTART_TIME(new Date());
				PltCommonLogLocal.setBUSI_CODE("HANDLE");
				PltCommonLogLocal.setBUSI_DESC(String.valueOf(tableRecs.get(t).get("tableName")));
				PltCommonLogLocal.setBUSI_NUM_1(listOrderCheckInfo.size());
				PltCommonLogLocal.setBUSI_NUM_2(result);
				PltCommonLogLocal.setBUSI_ITEM_2(String.valueOf(listOrderCheckInfo.get(0).getOrderRedId()));
				PltCommonLogLocal.setBUSI_ITEM_3(String.valueOf(listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId()));
				BusiToolsIns.insertPltCommonLog(PltCommonLogLocal);
			}
		}else{

			sbb.append("UPDATE "+tableRecs.get(curTablePos).get("tableName"));
			sbb.append(" SET  ORDER_STATUS = '6' ");
			sbb.append(" WHERE TENANT_ID='");
			sbb.append(m_strTenantId);
			sbb.append("'");
			sbb.append("  AND ACTIVITY_SEQ_ID =");
			sbb.append(m_iActivitySeqId);
			sbb.append("  AND USER_ID IN (");
			sbb.append(sb.toString());
			sbb.append(")");
			int  result = JdbcTemplateIns.update(sbb.toString());
			log.info("工单过 滤成功,activityseqid={},更新数量={}",m_iActivitySeqId,result);
		
		
			PltCommonLog			PltCommonLogLocal = new PltCommonLog();
			PltCommonLogLocal.setLOG_TYPE("21");
			PltCommonLogLocal.setSERIAL_ID(SerialId);
			PltCommonLogLocal.setSPONSOR("OrderFilterSucess");
			PltCommonLogLocal.setDEST_NUM(m_iActivitySeqId);
			PltCommonLogLocal.setBUSI_ITEM_4(m_strTenantId);
			PltCommonLogLocal.setSTART_TIME(new Date());
			PltCommonLogLocal.setBUSI_CODE("HANDLE");
			PltCommonLogLocal.setBUSI_DESC(String.valueOf(tableRecs.get(curTablePos).get("tableName")));
			PltCommonLogLocal.setBUSI_NUM_1(listOrderCheckInfo.size());
			PltCommonLogLocal.setBUSI_NUM_2(result);
			PltCommonLogLocal.setBUSI_ITEM_2(String.valueOf(listOrderCheckInfo.get(0).getOrderRedId()));
			PltCommonLogLocal.setBUSI_ITEM_3(String.valueOf(listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId()));
			BusiToolsIns.insertPltCommonLog(PltCommonLogLocal);
		}
		return 0;
	}
	
	private	String		getSqlForSucessType(){
		if(m_cssSuccessStandardPo == null)  return null;
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessType()) == false){
			return null;
		}
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		//SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		OrderCreateTime = sdfYm.format(new Date()) +"-01";     // --- 固定从1号开始判断是否办理了产品 ---
		
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  //1
//		Calendar lastDate = Calendar.getInstance();  
//		lastDate.set(Calendar.DATE,1);//设为当前月的1 号  
//		lastDate.add(Calendar.MONTH,-1);//减一个月，变为下月的1 号  
//		//lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天  
//		String		OrderCreateTime=sdf.format(lastDate.getTime());//1	
		
		String			strMatchingType = m_cssSuccessStandardPo.getMatchingType();
		if(StringUtils.isNotNull(strMatchingType)){
			if(strMatchingType.equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				List<SuccessProductPo> listProduct = m_cssSuccessStandardPo.getSuccessProductList();
				bProduct = true; 
				int		i = 0;
				if(listProduct != null && listProduct.size() > 0){   // --- 得到SQL  ---
					 
					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for(i = 0;i < listProduct.size();++i){
						if(i > 0)  sbProduct.append(",");
							sbProduct.append("'");
							sbProduct.append(listProduct.get(i).getProductCode());
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
		String				strTmp = null;
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessConditionSQL())){
			strAddSql = m_cssSuccessStandardPo.getSuccessConditionSQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessTypeConditionSql())){
			strTmp = m_cssSuccessStandardPo.getSuccessTypeConditionSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = m_cssSuccessStandardPo.getSuccessType();
		//精准营销时
		if(bProduct){
			if(strTmp!=null){                 
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append(strTmp);
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				if(strAddSql!=null){
					sbProduct.append(" AND " + strAddSql  );
				}
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					sbProduct.append(" AND " + strAddSql  );
				}
			}
			log.info("精准营销：");

		}else{
			//无精准营销但有成功标准
			if(strTmp!=null){
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append(strTmp);
				sb.append(" AND DATE_ID = '");
				sb.append(curDateId);
				sb.append("'");
				if(strAddSql!=null){
					sb.append(" AND " + strAddSql  );
				}

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
					sb.append(strAddSql);
					sb.append(" AND DATE_ID = '");
					sb.append(curDateId);
					sb.append("' ");

				}else {                                 // --- 全没有时  ---
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}			
			}	
		}
		
		log.info("工单检查sql="+sb.toString()+sbProduct.toString()+"), 成功类型="+successType);
		
		//一条成功标准sql插入一条日志
		PltCommonLog		logdb = new PltCommonLog();
//		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		logdb.setLOG_TYPE("221");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("工单过滤sql");
		logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType);
		logdb.setBUSI_DESC("SuccessSql,select未加USER_ID");
		logdb.setBUSI_ITEM_1(sb.toString()+sbProduct.toString());
		logdb.setBUSI_ITEM_2(""+m_iActivitySeqId);
		logdb.setBUSI_ITEM_3(String.valueOf(tableRecs.get(m_iCurTablePos).get("tableName")));
		BusiToolsIns.insertPltCommonLog(logdb);
		
		if(bProduct){
			return sbProduct.toString();
		}else {
			return sb.toString();
		}
		
//		if(m_cssSuccessStandardPo.getSuccessType().equals("1")){   // --- 换卡 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("2")){  // --- 换机 ---	
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("3")){  // --- 办理流量包 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("4")){  // --- 办理续约 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("5")){  // --- 换套餐 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("6")){  // --- 实名登记 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("7")){  // --- 宽带续约 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("8")){  // --- 办理副卡 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("9")){  // --- 4G登网 ---
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
//		else if(m_cssSuccessStandardPo.getSuccessType().equals("10")){ // --- 承诺抵消 ---
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
//			if(StringUtils.isNotNull(m_cssSuccessStandardPo.getSuccessTypeConditionSql())== false){
//				log.error("成功类型为10以后，但没有对应的SQL语句:SUCCESS_TYPE_CON_SQL is null");
//				return null;
//			}
//			if(bProduct){
//				sbProduct.append("  AND USER_ID IN ( ");
//				sbProduct.append(m_cssSuccessStandardPo.getSuccessTypeConditionSql());
//				String   strTmp = m_cssSuccessStandardPo.getSuccessTypeConditionSql().toUpperCase();
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
//				sb.append(m_cssSuccessStandardPo.getSuccessTypeConditionSql());
//				String   strTmp = m_cssSuccessStandardPo.getSuccessTypeConditionSql().toUpperCase();
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
//			
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
