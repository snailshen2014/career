package com.bonc.busi.service.func;
/*
 * @desc:工单成功检查
 * @author:zengdingyong
 * @time:2017-06-05
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.base.StringUtils;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.service.dao.PltorderinfoDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.dao.XcloudDao;
import com.bonc.busi.service.entity.SysLog;
import com.alibaba.fastjson.JSON;
import com.bonc.busi.service.dao.CommonDao;
import com.bonc.busi.service.entity.PltActivityExecuteLog;
import com.bonc.busi.service.dao.PltActivityExecuteLogDao;;


public class OrderSucessCheck extends ParallelFunc{
	private final static Logger log= LoggerFactory.getLogger(OrderSucessCheck.class);
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	BusiTools					BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	Date							dateBegin = null;    // --- 开始时间  ---
	private	int  							SerialId =0;   // --- 日志序列号 ---
	// --- 定义当前工单变量 ---
	private	long							curOrderRecId = -1;
	// --- 定义总的数量 ---
	private	int							m_iTotalNum = 0;
	// --- 定义工单成功检查时的表全量 ---
	private	List<String>				listTableName = new ArrayList<String>();
	private	boolean					bSetLockFlag = false;
	private	boolean					bOrderExecuteLogSet = false;
	
	
	// --- 定义变量 ---
	private	String						TableName = null;
	private	String						ActivityId = null;
//	private	String						ChannelId = null;
	private	List<String>   			ProductInfo = null;
	private	int							ActivitySeqId = -1;
	private	String						MaxMonthDay = null;			// --- 最大帐期  ---
	private	String						TenantId = null;
	private	ActivitySucessInfo	ActivitySucessInfoIns = null;
	private	short						CheckType = -1;   // --- 检查类型,0-事后检查,1-事前过滤  ---
	private	String SucessTypeSql  = null;                 //--成功标准SQL--
	//--接触过滤用--
	private	int 							lCurTableIndex = 0;						// --- 开始序列号  ---
	private	long							lCurEndRecId = -1;				// --- 结束序列号 ---

	public String getSucessTypeSql() {
		return SucessTypeSql;
	}

	public void setSucessTypeSql(String sucessTypeSql) {
		SucessTypeSql = sucessTypeSql;
	}

	public		void		setCheckType(short checktype){
		this.CheckType = checktype;
	}
	
	public		void		setActivitySucessInfo(ActivitySucessInfo activitysucessinfo){
		this.ActivitySucessInfoIns = activitysucessinfo;
	}
	public		void		setActivityId(String ActivityId){
		this.ActivityId = ActivityId;
	}
	
	public		void		setTenantId(String tenantid){
		this.TenantId = tenantid;
	}
	
	public		void		setMaxMonthDay(String maxmonthday){
		this.MaxMonthDay = maxmonthday;
	}
	
	public		void		setActivitySeqId(int activityseqid){
		this.ActivitySeqId = activityseqid;
	}
	
	public		void		setTableName(String tablename){
		this.TableName = tablename;
	}
//	public		void		setChannelId(String  channelid){
//		this.ChannelId = channelid;
//	}
	public		void		setProductInfo(List<String> productinfo){
		this.ProductInfo = productinfo;
	}
	
	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-begin");
		SysLogIns.setTENANT_ID(TenantId);
		dateBegin  = new Date();
		// --- 参数检查  ---
		if(MaxMonthDay == null ){
			log.error("最大帐期为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("最大帐期为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		if(ActivityId == null  && CheckType == (short)1){
			log.error("活动号为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("活动号为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
//		if(ChannelId == null){
//			log.error("渠道编号为空");
//			return -1;
//		}
		if(ActivitySeqId == -1){
			log.error("活动序列号为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("活动序列号为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		if(TenantId == null){
			log.error("租户编号为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("租户编号为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		//--成功标准，接触检查是否存在--
		String providerType = SyscommoncfgDao.query("SERVICE_PROVIDER_TYPE");
		SucessTypeSql = 	getSqlForSucessType(TenantId, ActivitySeqId, providerType);
		
		if(SucessTypeSql==null && !ActivitySucessInfoIns.getMatchingType().equals("9")){
			log.error(ActivitySeqId + "成功标准SQL："+SucessTypeSql+"，MatchingType:"+ActivitySucessInfoIns.getMatchingType());
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE(ActivitySeqId + "成功标准SQL："+SucessTypeSql+"，MatchingType:"+ActivitySucessInfoIns.getMatchingType());
			SyslogDao.insert(SysLogIns);
			// --- 纪录工单运行表 ---
			if(CheckType == (short)1){
				PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
				PltActivityExecuteLogIns.setCHANNEL_ID("0");
				PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
				PltActivityExecuteLogIns.setTENANT_ID(TenantId);
				PltActivityExecuteLogIns.setBUSI_CODE(1011);
				PltActivityExecuteLogIns.setBEGIN_DATE(new Date());
				PltActivityExecuteLogIns.setPROCESS_STATUS(1);
				PltActivityExecuteLogIns.setACTIVITY_ID(ActivityId);
				PltActivityExecuteLogIns.setEND_DATE(new Date());
			PltActivityExecuteLogDao.insert(PltActivityExecuteLogIns);
			}
			
			return -1;
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder		sb = new StringBuilder();
		sb.append(ActivitySeqId);
		sb.append("|");
		sb.append(sdf.format(dateBegin));
		
		SysLogIns.setBUSI_ITEM_2(String.valueOf(ActivitySeqId));
		SysLogIns.setBUSI_ITEM_4(MaxMonthDay);
		if(CheckType == (short)0){
			SysLogIns.setBUSI_ITEM_5("01");
			
			// --- 清空内存表 ---
			sb.setLength(0);
			sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
			sb.append("TRUNCATE TABLE PLT_ORDER_CHECK_SUCESS_MEM");
			CommonDao.update(sb.toString());
						
			// --- 提取所有的表  ---
			String   sql = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.QRYTABLENAME").replaceFirst("TTTTTENANT_ID", TenantId).replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(ActivitySeqId));
			List<Map<String,Object>>  listTableNames = CommonDao.queryList(sql);
			if(listTableNames == null || listTableNames.size() == 0){
				log.error("ActivitySeqId = {}  没有对应的表纪录",ActivitySeqId);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ActivitySeqId = "+ActivitySeqId+" 没有对应的表纪录");
				SyslogDao.insert(SysLogIns);
				return -1;
			}
			//--正常成功标准检查--
				// --- 将所有纪录入内存表  ---
				sb.setLength(0);
				sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
				String		insertsql[]  = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.INSERTMEMTABLE").split("\\|");
				sb.append(insertsql[0]);
				String		selectSql = insertsql[1].replaceFirst("TTTTTENANT_ID", TenantId).
						replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(ActivitySeqId));
				int  i = 0;
				for(Map<String,Object> item:listTableNames){
					listTableName.add((String)item.get("TABLE_NAME"));
					++i;
					if(i > 1)  sb.append("  UNION ");
					sb.append(selectSql.replaceFirst("TTTTTABLENAME", (String)item.get("TABLE_NAME")));
				}
				int   recNum = CommonDao.update(sb.toString());
				log.info("ActivitySeqId={}  入内存表数量={}",ActivitySeqId,recNum);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("ActivitySeqId="+ActivitySeqId+"入内存表数量="+recNum);
				SyslogDao.insert(SysLogIns);	
			
		}
		else if(CheckType == (short)1){
			SysLogIns.setBUSI_ITEM_5("91");
			// --- 加锁标识 ---
			SyscommoncfgDao.update("ORDERCHECK.FILTER.RUNFLG."+TenantId, sb.toString());
			bSetLockFlag = true;
			// --- 纪录工单运行表 ---
			PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
			PltActivityExecuteLogIns.setCHANNEL_ID("0");
			PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
			PltActivityExecuteLogIns.setTENANT_ID(TenantId);
			PltActivityExecuteLogIns.setBUSI_CODE(1011);
			PltActivityExecuteLogIns.setBEGIN_DATE(new Date());
			PltActivityExecuteLogIns.setPROCESS_STATUS(0);
			PltActivityExecuteLogIns.setACTIVITY_ID(ActivityId);
			PltActivityExecuteLogDao.insert(PltActivityExecuteLogIns);
			bOrderExecuteLogSet = true;
			
			// --- 清空内存表 ---
			sb.setLength(0);
			sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
			sb.append("TRUNCATE TABLE PLT_ORDER_CHECK_FILTER_MEM");
			CommonDao.update(sb.toString());
			// --- 将所有纪录入内存表  ---
			sb.setLength(0);
			sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
			sb.append(SyscommoncfgDao.query("ORDERCHECK.FILTER.SQL.INSERTMEMTABLE").replaceFirst("TTTTTENANT_ID", TenantId).
				replaceFirst("AAAAACTIVITY_SEQ_ID", String.valueOf(ActivitySeqId)).
						replaceFirst("TTTTTABLENAME", TableName));
			String		sql = sb.toString();
			CommonDao.update(sql);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("工单成功过滤工单入内存表sql="+sql);
			SyslogDao.insert(SysLogIns);
		}
		
		// --- 纪录日志 ---
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		if(CheckType == (short)0){
			PltCommonLogIns.setLOG_TYPE("01");
			PltCommonLogIns.setSPONSOR("ORDERCHECKSUCESS");
			PltCommonLogIns.setBUSI_DESC("工单检查开始");
		}
		else if(CheckType == (short)1){
			PltCommonLogIns.setLOG_TYPE("91");
			PltCommonLogIns.setSPONSOR("ORDERFILTERSUCESS");
			PltCommonLogIns.setBUSI_DESC("工单过滤开始");
		}
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_ITEM_1(TableName);
	//	PltCommonLogIns.setBUSI_ITEM_2(ChannelId);
		PltCommonLogIns.setBUSI_ITEM_3(MaxMonthDay);
		PltCommonLogIns.setBUSI_ITEM_4(TenantId);
		PltCommonLogIns.setBUSI_NUM_1(ActivitySeqId);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("工单成功检查/过滤开始");
		SysLogIns.setBUSI_ITEM_1(Integer.toString(ActivitySeqId));
		SyslogDao.insert(SysLogIns);
		return 0;
	}
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		// --- 解锁标识 ---
		//StringBuilder   sb = new StringBuilder();
		if(CheckType == (short)1){
			SyscommoncfgDao.update("ORDERCHECK.FILTER.RUNFLG."+TenantId,"FALSE");
			bSetLockFlag = false;
			// --- 更新工单运行标识 ---
			if(bOrderExecuteLogSet){
			PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
			PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
			PltActivityExecuteLogIns.setTENANT_ID(TenantId);
			PltActivityExecuteLogIns.setPROCESS_STATUS(1);
			PltActivityExecuteLogIns.setEND_DATE(new Date());
			PltActivityExecuteLogDao.updateStatus(PltActivityExecuteLogIns);
			}
			Date				dateEnd = new Date();
			log.info("--- 工单成功过滤结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("END");
			PltCommonLogIns.setBUSI_DESC("工单过滤结束");
			PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		}else{
			Date				dateEnd = new Date();
			log.info("--- 工单成功检查结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("END");
			PltCommonLogIns.setBUSI_DESC("工单检查结束");
			PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		}
		
		
		// --- 成功过滤需要通知工单生成中心 ---
		if(CheckType == (short)1){
			
		}
		return 0;
	}
	/*
	 * 退出函数
	 */
	@Override
	public		int		finallyFunc(){
		if(bSetLockFlag){
			if(CheckType == (short)1){
				SyscommoncfgDao.update("ORDERCHECK.FILTER.RUNFLG."+TenantId,"FALSE");
				bSetLockFlag = false;
				// --- 更新工单运行标识 ---
				if(bOrderExecuteLogSet){
				PltActivityExecuteLog	PltActivityExecuteLogIns = new PltActivityExecuteLog();
				PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
				PltActivityExecuteLogIns.setTENANT_ID(TenantId);
				PltActivityExecuteLogIns.setPROCESS_STATUS(1);
				PltActivityExecuteLogIns.setEND_DATE(new Date());
				PltActivityExecuteLogDao.updateStatus(PltActivityExecuteLogIns);
				}
			}
		}
		return 0;
	}
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("TENANTID", TenantId);
		map.put("ACTIVITYSEQID", ActivitySeqId);
		List<OrderCheckInfo>	listOrderCheckInfo = null;
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-get");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_2(String.valueOf(ActivitySeqId));
		SysLogIns.setBUSI_ITEM_4(MaxMonthDay);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("开始获取工单数据");
		
		if(CheckType == (short)0){
			SysLogIns.setBUSI_ITEM_5("01");
			SyslogDao.insert(SysLogIns);
			//--接触过滤--
			if(ActivitySucessInfoIns.getMatchingType()!=null && ActivitySucessInfoIns.getMatchingType().equals("9")){
				// --- 设置传递的数据 ---
				map.put("CONTACTCHECKTABLE",listTableName.get(lCurTableIndex));
				
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("获取工单表表名");
				SysLogIns.setBUSI_ITEM_1("当前工单表表名id:"+lCurTableIndex+"，工单表数量"+listTableName.size());
				SysLogIns.setBUSI_ITEM_3(" 当前工单表："+listTableName.get(lCurTableIndex));
				SyslogDao.insert(SysLogIns);
				
				++lCurTableIndex;
				if(lCurTableIndex  == listTableName.size()){
					log.info("租户ID:"+TenantId +" 活动序列号:"+ActivitySeqId + " lCurTableIndex:"+lCurTableIndex+"，get结束");
					return null;
				}
			}else{
				//--正常成功标准检查--
				listOrderCheckInfo = PltorderinfoDao.getOrderListForCheck( ActivitySeqId, TenantId, curOrderRecId);
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("获取工单数据结束");
				SysLogIns.setBUSI_ITEM_1("纪录数:"+listOrderCheckInfo.size());
				SysLogIns.setBUSI_ITEM_3(" 起始工单号:"+curOrderRecId+" 前总纪录数:"+m_iTotalNum);
				//SysLogIns.setBUSI_ITEM_4(" 前总纪录数:"+m_iTotalNum);
				SyslogDao.insert(SysLogIns);
				if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){  // --- 无数据 ---
					log.info("租户ID:"+TenantId +" 活动序列号:"+ActivitySeqId + "  起始工单号:"+curOrderRecId 
							+ "  无数据,总纪录数 " +m_iTotalNum );
					return null;
				}
				// --- 累加工单数量 ---
				m_iTotalNum += listOrderCheckInfo.size();
				// --- 设置当前最大工单号 ---
				curOrderRecId = listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId();
				// --- 设置传递的数据 ---
				map.put("ORDERCHECKINFO",listOrderCheckInfo);
			}
			
		}else if(CheckType == (short)1){
			SysLogIns.setBUSI_ITEM_5("91");
			SyslogDao.insert(SysLogIns);
			listOrderCheckInfo = PltorderinfoDao.getOrderListForFilter( ActivitySeqId, TenantId, curOrderRecId);
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("获取工单数据结束");
			SysLogIns.setBUSI_ITEM_1("纪录数:"+listOrderCheckInfo.size());
			SysLogIns.setBUSI_ITEM_3(" 起始工单号:"+curOrderRecId+" 前总纪录数:"+m_iTotalNum);
			//SysLogIns.setBUSI_ITEM_4(" 前总纪录数:"+m_iTotalNum);
			SyslogDao.insert(SysLogIns);
			if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){  // --- 无数据 ---
				log.info("租户ID:"+TenantId +" 活动序列号:"+ActivitySeqId + "  起始工单号:"+curOrderRecId 
						+ "  无数据,总纪录数 " +m_iTotalNum );
				return null;
			}
			// --- 累加工单数量 ---
			m_iTotalNum += listOrderCheckInfo.size();
			// --- 设置当前最大工单号 ---
			curOrderRecId = listOrderCheckInfo.get(listOrderCheckInfo.size()-1).getOrderRedId();
			// --- 设置传递的数据 ---
			map.put("ORDERCHECKINFO",listOrderCheckInfo);
		}
		else{
			return null;
		}
		return map;
	}
	
	/*
	 * 处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public		int		handle(Object data){
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;//--取出map--
		String			TenantId = (String)mapData.get("TENANTID");	//--提取TENANTID--
		int				ActivitySeqId = (int)mapData.get("ACTIVITYSEQID");//--提取ACTIVITYSEQID--
		Date				handelBeginTime = new Date(); //--设置开始时间--
		SysLog			SysLogIns = new SysLog();//--记录日志--
			SysLogIns.setTENANT_ID(TenantId);
			SysLogIns.setBUSI_ITEM_2(String.valueOf(ActivitySeqId));
			SysLogIns.setBUSI_ITEM_4(MaxMonthDay);
		
		if(CheckType == (short)0 && ActivitySucessInfoIns.getMatchingType()!=null && ActivitySucessInfoIns.getMatchingType().equals("9")){
			String contactOrderTableName = (String)mapData.get("CONTACTCHECKTABLE");//---提取工单表表名，按表处理-
			if(contactOrderTableName==null || contactOrderTableName.equals("")){
				SysLogIns.setLOG_MESSAGE("获取工单表表名为null，当前线程处理结束");
				SyslogDao.insert(SysLogIns);
				return 0;              //  --- 无数据 ---
			}
			//--统计接触工单的数量--
			StringBuilder			sb = new StringBuilder();
			//--SELECT COUNT(1) FROM `plt_order_info_15` WHERE CONTACT_RESULT = '1'  AND  TENANT_ID = 'uni097'; -- AND ACTIVITY_SEQ_ID = --
			sb.append("SELECT ");
			sb.append(" COUNT(1) AS CONTACTNUM ");
			sb.append(" FROM ");
			sb.append(contactOrderTableName);
			sb.append(" WHERE TENANT_ID='");
			sb.append(TenantId);
			sb.append("'");
			sb.append("  AND ACTIVITY_SEQ_ID =");
			sb.append(ActivitySeqId);
			sb.append(" AND CONTACT_RESULT = '1'");
			Map<String,Object> countTemp  =  CommonDao.queryOne(sb.toString());
			long contactOrderNumTemp = (long) countTemp.get("CONTACTNUM");//--获取统计的数量--
			int contactOrderNum = (int) contactOrderNumTemp;
			int  updateContactOrderNum = 0;//--定义更新接触检查工单的记录数--
			if(contactOrderNum>10000){//--数量大于10000，批量处理--
				for(int i= 0 ;i<contactOrderNum;i=i+10000){
					//--更新接触工单记录的成功状态--
					int count = PltorderinfoDao.updateContactOrderForSuccess(contactOrderTableName, ActivitySeqId, TenantId,  MaxMonthDay, contactOrderNum);
					updateContactOrderNum = updateContactOrderNum + count;//--记录更新的数量--
				}
			}else{
				int count = PltorderinfoDao.updateContactOrderForSuccess(contactOrderTableName, ActivitySeqId, TenantId,  MaxMonthDay, contactOrderNum);
				updateContactOrderNum = updateContactOrderNum + count;
			}
			
			SysLogIns.setBUSI_ITEM_5("01");
			SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-handleContactOrder");
			SysLogIns.setBUSI_ITEM_3("更新工单的数量："+updateContactOrderNum+"接触工单的数量："+contactOrderNum);
			SysLogIns.setBUSI_ITEM_1(contactOrderTableName);
			SysLogIns.setLOG_MESSAGE("更新接触工单记录，当前线程处理结束");
			SyslogDao.insert(SysLogIns);
			log.info("--接触工单成功检查流程： end to handle data ,更新数量：{},接触工单数量：{}时间：{}",updateContactOrderNum,contactOrderNum,new Date());
		}else{
			List<OrderCheckInfo>	listOrderCheckInfo = (List<OrderCheckInfo>)mapData.get("ORDERCHECKINFO");
			//--记录日志--
			if(CheckType == (short)0){
				SysLogIns.setBUSI_ITEM_5("01");
			}else if(CheckType == (short)1){
				SysLogIns.setBUSI_ITEM_5("91");
			}
			SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-handle");
			
			if(listOrderCheckInfo==null || listOrderCheckInfo.size()==0){
				SysLogIns.setLOG_MESSAGE("获取工单数据记录数为0，当前线程处理结束");
				SyslogDao.insert(SysLogIns);
				return 0;              //  --- 无数据 ---
			}
			
			StringBuilder  userIdForCheck = new StringBuilder();//--内存表中的USERID--
			for(int i = 0; i < listOrderCheckInfo.size();++i){
				if(i > 0) userIdForCheck.append(",");
				userIdForCheck.append("'");
				userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
				userIdForCheck.append("'");
			}
			log.info("--- begin to handle data 时间:"+handelBeginTime);

			StringBuilder sb = new StringBuilder();
			String routesql = SyscommoncfgDao.query("GETDATAFROMXCLOUD").replaceFirst("TTTTTENANT_ID", TenantId);
			sb.append(routesql);
			
			// ---- 关键（提取成功标准)  ----
			String sucessTypeSql = getSucessTypeSql().replaceFirst("UUUUUSERIDFORCHECK", userIdForCheck.toString());
			sb.append(sucessTypeSql);
			
			log.info("--SuccessSql--"+getSucessTypeSql());
			SysLogIns.setLOG_TIME(new Date());
			if (sb.length()>=1024){
				String  s = sb.substring(0, 1024);
				log.info("--SuccessSql--"+s+",--s.length = "+sb.length());
				SysLogIns.setLOG_MESSAGE(sb.substring(0, 1024));
				
			}else {
				log.info("--SuccessSql--"+sb.toString()+",--s.length = "+sb.toString().length());
				SysLogIns.setLOG_MESSAGE(sb.toString());
			}
			SyslogDao.insert(SysLogIns);
			// --- 执行SQL ---
			List<Map<String,Object>>   listUserId  =XcloudDao.getSucessUse(sb.toString());
			if(listUserId == null || listUserId.size() ==0) {
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("从行云没有获取到数据");
				SyslogDao.insert(SysLogIns);
				return 0;              //  --- 无数据 ---
			}
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("从行云取到的数据长度:"+listUserId.size());
			SyslogDao.insert(SysLogIns);
			sb.setLength(0);
			
			int totalCount = 0;
			if(listUserId.size()<=10000){
				for(int i =0;i < listUserId.size();++i){
					if(i != 0) sb.append(",");
					sb.append("'");
					sb.append(listUserId.get(i).get("USER_ID"));
					sb.append("'");
				}
			// --- 更新工单总表成功状态 ---
			// --- 更新  (没有控制事物)---
			int		count = -1;
			if(CheckType == (short)0){
				
				count = PltorderinfoDao.updateOrderForCheck(listTableName, ActivitySeqId, TenantId, sb.toString(), MaxMonthDay);
			}else if(CheckType == (short)1){
				
				count = PltorderinfoDao.updateOrderForFilter(TableName, ActivitySeqId, TenantId, sb.toString(), MaxMonthDay);
			}else{
				// --- 暂不支持 ---
			}
			totalCount = totalCount + count;
			
			}else{
				int i = 0;
				for( i= 0 ;i<listUserId.size();i=i+10000){
					for(int j = i;j < 10000;++j){
						//for(int i =0;i < listUserId.size();++i){
							if(j != 0) sb.append(",");
							sb.append("'");
							sb.append(listUserId.get(j).get("USER_ID"));
							sb.append("'");
						}
					// --- 更新工单总表成功状态 ---
					// --- 更新  (没有控制事物)---
					int		count = -1;
					if(CheckType == (short)0){
						count = PltorderinfoDao.updateOrderForCheck(listTableName, ActivitySeqId, TenantId, sb.toString(), MaxMonthDay);
					}else if(CheckType == (short)1){
						count = PltorderinfoDao.updateOrderForFilter(TableName, ActivitySeqId, TenantId, sb.toString(), MaxMonthDay);
					}else{
						// --- 暂不支持 ---
					}
					totalCount = totalCount + count;
					
			}
			
			}
			log.info("--成功标准正常流程： end to handle data ,更新数量：{}  ,时间：{}",totalCount,new Date());
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("end to handle data ,更新数量："+totalCount);
			SyslogDao.insert(SysLogIns);
			
		}
		return 0;
	}
	/*
	 * 获取成功标准
	 */
	private	String		getSqlForSucessTypeOld2(String tenant_id,int activity_seq_id,List<OrderCheckInfo>	listOrderCheckInfo,String providerType){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-getSqlForSucessType");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_2(String.valueOf(activity_seq_id));
		SysLogIns.setBUSI_ITEM_4(MaxMonthDay);
		if(CheckType == (short)0){
			SysLogIns.setBUSI_ITEM_5("01");
		}else if(CheckType == (short)1){
			SysLogIns.setBUSI_ITEM_5("91");
		}
		// --- 检查是否存在成功标准 ---------------
		if(ActivitySucessInfoIns == null){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("不存在成功标准");
			SyslogDao.insert(SysLogIns);
			return null;
		}  
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("SucessType为空，不判断成功标准");
			SyslogDao.insert(SysLogIns);
			return null;
			
		}
		
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();//--精确匹配SQL--
		/*SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		if(ActivitySucessInfoIns.getLastOrderCreateTime() != null){
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		}
		else{
			OrderCreateTime = sdfYm.format(new Date()) +"-01";
		}*/
		String		OrderCreateTime = null ;
		OrderCreateTime = getLastOrderCreateTime(tenant_id);
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())){
			if(ActivitySucessInfoIns.getMatchingType().equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				bProduct = true; 
				int		i = 0;
				if(ProductInfo != null && ProductInfo.size() > 0){   // --- 得到SQL  ---
					 //配置的SQL：SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE ACCEPTED_DATE > 'OOOOORDERCREATETIME' AND BONC_PRODUCT_ID IN (PPPPRODUCTINFO)
					//替换的参数：OOOOORDERCREATETIME --》OrderCreateTime ，PPPPRODUCTINFO --》 ProductInfo的string串
					
					StringBuilder  product = new StringBuilder();
					for(i = 0;i < ProductInfo.size();++i){
						if(i > 0)  product.append(",");
						product.append("'");
						product.append(ProductInfo.get(i));
						product.append("'");
						}
					String CLJYProductList = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLJYALLACCEPTEDLIST").replaceFirst("OOOOORDERCREATETIME",OrderCreateTime).replaceFirst("PPPPRODUCTINFO",product.toString());
					
						sbProduct.append(CLJYProductList);
						sbProduct.append(" ");
				}else {
					log.info("精准匹配产品时，无产品编码");
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("精准匹配产品时，无产品编码");
					SyslogDao.insert(SysLogIns);
					return null;	
				}
			}
		}
		
		StringBuilder		sb = new StringBuilder();//--非精准匹配SQL--
		String				strAddSql = null;//--成功标准附加条件--
		String strTmp = null;//--成功标准条件--
		String tablePrefix =null;
		//--默认联通--
		if(providerType == null || providerType.trim().equals("")||providerType.trim().equals("0")){
			 tablePrefix = SyscommoncfgDao.query("CUCC_JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		//--电信运营商标识--
		}else if(providerType.trim().equals("1")){
			tablePrefix = SyscommoncfgDao.query("CTCC_JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		//--待废弃标识--
		}else{
			tablePrefix = SyscommoncfgDao.query("JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		}
		 
	
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll(tablePrefix, "");
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())){
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll(tablePrefix, "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		
	StringBuilder  userIdForCheck = new StringBuilder();//--内存表中的USERID--
		for(int i = 0; i < listOrderCheckInfo.size();++i){
			if(i > 0) userIdForCheck.append(",");
			userIdForCheck.append("'");
			userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
			userIdForCheck.append("'");
		}
		
		//精准营销时
		if(bProduct){
			String userIdCLJYForUserLabel = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLJYUSERLABELINFO").replaceFirst("MMMMMAXMONTHDAY",MaxMonthDay).replaceFirst("UUUUUSERIDFORCHECK", userIdForCheck.toString());
			if(strTmp!=null){  
				//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK) 
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sbProduct.append(userIdCLJYForUserLabel);
				sbProduct.append(" AND " + "("+strTmp+")"  );
				
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")"  );
				}
				sbProduct.append(" ) ");
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
					// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
					sbProduct.append(userIdCLJYForUserLabel);
					sbProduct.append(" AND " + "("+strAddSql+")"  );
					sbProduct.append(" ) ");
				}
			}
			log.info("精准营销");

		}else{
			//无精准营销但有成功标准
			String userIdCLQLForUserLabel = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLQLUSERLABELINFO").replaceFirst("MMMMMAXMONTHDAY",MaxMonthDay).replaceFirst("UUUUUSERIDFORCHECK", userIdForCheck.toString());
			if(strTmp!=null){
				//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sb.append(userIdCLQLForUserLabel);
				sb.append(" AND " + "("+strTmp+")"  );
				
				if(strAddSql!=null){
					sb.append(" AND " + "("+strAddSql+")"  );
				}
				sb.append("  ");
			}else{
				if(strAddSql!=null){
					// --- 无成功标准，有成功类型  ---
					//配置的SQL：SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='PPPPPRODTYPE' AND ACCEPTED_DATE > 'OOOOORDERCREATETIME' AND USER_ID IN (
					//successTypeJson:{"3":"03","4":"02","5":"01"}  ORDERCHECK.SUCESS.SQL.SUCCESSTYPE
					//String successTypeJson = null;
					String successTypeJson = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.SUCCESSTYPE");
					if(successTypeJson == null || successTypeJson.equals("")){
						SysLogIns.setLOG_TIME(new Date());
						SysLogIns.setLOG_MESSAGE("特殊成功标准类型没有配置");
						SyslogDao.insert(SysLogIns);
						log.info("特殊成功标准类型没有配置");
						return null;
					}else {
						//if(successTypeJson != null || !successTypeJson.equals("")){
						Map<String,Object> successTypeMap = new HashMap<String,Object>();
						try{
							successTypeMap = JSON.parseObject(successTypeJson,Map.class);
							
						}catch(Exception e){
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setLOG_MESSAGE(successTypeJson+"：特殊成功标准类型解析异常");
							SyslogDao.insert(SysLogIns);
							log.warn("特殊成功标准类型解析异常");
						}
						if(successTypeMap.size()!=0){
							StringBuilder specialTypeTemp = new StringBuilder();
							for(Map.Entry<String, Object> entry : successTypeMap.entrySet()){
								specialTypeTemp.append(entry.getKey());
								
							}
							String specialType = specialTypeTemp.toString();
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setLOG_MESSAGE("特殊成功标准类型解析:"+specialType+" 该活动成功标准类型："+successType);
							SyslogDao.insert(SysLogIns);
							if(successType!=null &&  specialType.contains(successType)){
								//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
								// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
								String CLQLProductListSql =SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLQLALLACCEPTEDLIST").replaceFirst("PPPPPRODTYPE", (String)successTypeMap.get(successType)).replaceFirst("OOOOORDERCREATETIME", OrderCreateTime); 
								sb.append(CLQLProductListSql);
								sb.append(" ");
								sb.append(userIdCLQLForUserLabel);
								sb.append(" AND " + "("+strAddSql+")"  );
								sb.append(" ) ");
							}else{
								sb.append(userIdCLQLForUserLabel);
								sb.append(" AND " + "("+strAddSql+")"  );	
							}
						}	
					}
							
				}else{
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("此产品不存在或者没有成功标准可以判断");
					SyslogDao.insert(SysLogIns);
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}
					
				}
				
		}
		
		
		String  successSql = "工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType;
		
			SysLogIns.setLOG_TIME(new Date());
			if (successSql.length()>=1024){
				
				SysLogIns.setLOG_MESSAGE(successSql.substring(0, 1024));
				
			}else {
				
				SysLogIns.setLOG_MESSAGE(successSql.toString());
			}
			
			SyslogDao.insert(SysLogIns);
			

			
			if(bProduct){
				return sbProduct.toString();
			}else {
				return sb.toString();
			}
			

	}
	/*
	 * 获取成功标准
	 */
	private	String		getSqlForSucessType(String tenant_id,int activity_seq_id,String providerType){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-getSqlForSucessType");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_2(String.valueOf(activity_seq_id));
		SysLogIns.setBUSI_ITEM_4(MaxMonthDay);
		if(CheckType == (short)0){
			SysLogIns.setBUSI_ITEM_5("01");
		}else if(CheckType == (short)1){
			SysLogIns.setBUSI_ITEM_5("91");
		}
		// --- 检查是否存在成功标准 ---------------
		if(ActivitySucessInfoIns == null){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("不存在成功标准");
			SyslogDao.insert(SysLogIns);
			return null;
		}  
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("SucessType为空，不判断成功标准");
			SyslogDao.insert(SysLogIns);
			return null;
			
		}
		
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();//--精确匹配SQL--
		/*SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		if(ActivitySucessInfoIns.getLastOrderCreateTime() != null){
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		}
		else{
			OrderCreateTime = sdfYm.format(new Date()) +"-01";
		}*/
		String		OrderCreateTime = null ;
		OrderCreateTime = getLastOrderCreateTime(tenant_id);
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())){
			if(ActivitySucessInfoIns.getMatchingType().equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				bProduct = true; 
				int		i = 0;
				if(ProductInfo != null && ProductInfo.size() > 0){   // --- 得到SQL  ---
					 //配置的SQL：SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE ACCEPTED_DATE > 'OOOOORDERCREATETIME' AND BONC_PRODUCT_ID IN (PPPPRODUCTINFO)
					//替换的参数：OOOOORDERCREATETIME --》OrderCreateTime ，PPPPRODUCTINFO --》 ProductInfo的string串
					
					StringBuilder  product = new StringBuilder();
					for(i = 0;i < ProductInfo.size();++i){
						if(i > 0)  product.append(",");
						product.append("'");
						product.append(ProductInfo.get(i));
						product.append("'");
						}
					String CLJYProductList = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLJYALLACCEPTEDLIST").replaceFirst("OOOOORDERCREATETIME",OrderCreateTime).replaceFirst("PPPPRODUCTINFO",product.toString());
					
						sbProduct.append(CLJYProductList);
						sbProduct.append(" ");
				}else {
					log.info("精准匹配产品时，无产品编码");
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("精准匹配产品时，无产品编码");
					SyslogDao.insert(SysLogIns);
					return null;	
				}
			}
		}
		
		StringBuilder		sb = new StringBuilder();//--非精准匹配SQL--
		String				strAddSql = null;//--成功标准附加条件--
		String strTmp = null;//--成功标准条件--
		String tablePrefix =null;
		//--默认联通--
		if(providerType == null || providerType.trim().equals("")||providerType.trim().equals("0")){
			 tablePrefix = SyscommoncfgDao.query("CUCC_JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		//--电信运营商标识--
		}else if(providerType.trim().equals("1")){
			tablePrefix = SyscommoncfgDao.query("CTCC_JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		//--待废弃标识--
		}else{
			tablePrefix = SyscommoncfgDao.query("JSON_TABLE_PREFIX_"+tenant_id);//--过滤条件表名前缀--
		}
		 
	
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll(tablePrefix, "");
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())){
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll(tablePrefix, "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		
	/*StringBuilder  userIdForCheck = new StringBuilder();//--内存表中的USERID--
		for(int i = 0; i < listOrderCheckInfo.size();++i){
			if(i > 0) userIdForCheck.append(",");
			userIdForCheck.append("'");
			userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
			userIdForCheck.append("'");
		}*/
		
		//精准营销时
		if(bProduct){
			String userIdCLJYForUserLabel = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLJYUSERLABELINFO").replaceFirst("MMMMMAXMONTHDAY",MaxMonthDay);
			if(strTmp!=null){  
				//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK) 
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sbProduct.append(userIdCLJYForUserLabel);
				sbProduct.append(" AND " + "("+strTmp+")"  );
				
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")"  );
				}
				sbProduct.append(" ) ");
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
					// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
					sbProduct.append(userIdCLJYForUserLabel);
					sbProduct.append(" AND " + "("+strAddSql+")"  );
					sbProduct.append(" ) ");
				}
			}
			log.info("精准营销");

		}else{
			//无精准营销但有成功标准
			String userIdCLQLForUserLabel = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLQLUSERLABELINFO").replaceFirst("MMMMMAXMONTHDAY",MaxMonthDay);
			if(strTmp!=null){
				//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sb.append(userIdCLQLForUserLabel);
				sb.append(" AND " + "("+strTmp+")"  );
				
				if(strAddSql!=null){
					sb.append(" AND " + "("+strAddSql+")"  );
				}
				sb.append("  ");
			}else{
				if(strAddSql!=null){
					// --- 无成功标准，有成功类型  ---
					//配置的SQL：SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='PPPPPRODTYPE' AND ACCEPTED_DATE > 'OOOOORDERCREATETIME' AND USER_ID IN (
					//successTypeJson:{"3":"03","4":"02","5":"01"}  ORDERCHECK.SUCESS.SQL.SUCCESSTYPE
					//String successTypeJson = null;
					String successTypeJson = SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.SUCCESSTYPE");
					if(successTypeJson == null || successTypeJson.equals("")){
						SysLogIns.setLOG_TIME(new Date());
						SysLogIns.setLOG_MESSAGE("特殊成功标准类型没有配置");
						SyslogDao.insert(SysLogIns);
						log.info("特殊成功标准类型没有配置");
						return null;
					}else {
						//if(successTypeJson != null || !successTypeJson.equals("")){
						Map<String,Object> successTypeMap = new HashMap<String,Object>();
						try{
							successTypeMap = JSON.parseObject(successTypeJson,Map.class);
							
						}catch(Exception e){
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setLOG_MESSAGE(successTypeJson+"：特殊成功标准类型解析异常");
							SyslogDao.insert(SysLogIns);
							log.warn("特殊成功标准类型解析异常");
						}
						if(successTypeMap.size()!=0){
							StringBuilder specialTypeTemp = new StringBuilder();
							for(Map.Entry<String, Object> entry : successTypeMap.entrySet()){
								specialTypeTemp.append(entry.getKey());
								
							}
							String specialType = specialTypeTemp.toString();
							SysLogIns.setLOG_TIME(new Date());
							SysLogIns.setLOG_MESSAGE("特殊成功标准类型解析:"+specialType+" 该活动成功标准类型："+successType);
							SyslogDao.insert(SysLogIns);
							if(successType!=null &&  specialType.contains(successType)){
								//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY' AND USER_ID IN (UUUUUSERIDFORCHECK)
								// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
								String CLQLProductListSql =SyscommoncfgDao.query("ORDERCHECK.SUCESS.SQL.GETCLQLALLACCEPTEDLIST").replaceFirst("PPPPPRODTYPE", (String)successTypeMap.get(successType)).replaceFirst("OOOOORDERCREATETIME", OrderCreateTime); 
								sb.append(CLQLProductListSql);
								sb.append(" ");
								sb.append(userIdCLQLForUserLabel);
								sb.append(" AND " + "("+strAddSql+")"  );
								sb.append(" ) ");
							}else{
								sb.append(userIdCLQLForUserLabel);
								sb.append(" AND " + "("+strAddSql+")"  );	
							}
						}	
					}
							
				}else{
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("此产品不存在或者没有成功标准可以判断");
					SyslogDao.insert(SysLogIns);
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}
					
				}
				
		}
		
		
		String  successSql = "工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType;
		
			SysLogIns.setLOG_TIME(new Date());
			if (successSql.length()>=1024){
				
				SysLogIns.setLOG_MESSAGE(successSql.substring(0, 1024));
				
			}else {
				
				SysLogIns.setLOG_MESSAGE(successSql.toString());
			}
			
			SyslogDao.insert(SysLogIns);
			

			
			if(bProduct){
				return sbProduct.toString();
			}else {
				return sb.toString();
			}
			

	}
	private	String		getSqlForSucessTypeOld1(String tenant_id,int activity_seq_id){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-getSqlForSucessType");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_1("ActivitySeqId="+activity_seq_id);
		// --- 检查是否存在成功标准 ---------------
		if(ActivitySucessInfoIns == null)  return null;
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false){
			return null;
		}
		
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
		SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
		String		OrderCreateTime ;
		log.info("----OrderSucessCheck-----OrderCreateTime="+ActivitySucessInfoIns.getLastOrderCreateTime()+"filter is null and  check is not null");
		if(ActivitySucessInfoIns.getLastOrderCreateTime() != null){
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		}
		else{
			OrderCreateTime = sdfYm.format(new Date()) +"-01";
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())){
			if(ActivitySucessInfoIns.getMatchingType().equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				bProduct = true; 
				int		i = 0;
				if(ProductInfo != null && ProductInfo.size() > 0){   // --- 得到SQL  ---
					 //配置的SQL：SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE ACCEPTED_DATE > 'OOOOORDERCREATETIME' AND BONC_PRODUCT_ID IN (PPPPRODUCTINFO)
					//替换的参数：OOOOORDERCREATETIME --》OrderCreateTime ，PPPPRODUCTINFO --》 ProductInfo的string串
					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for(i = 0;i < ProductInfo.size();++i){
						if(i > 0)  sbProduct.append(",");
							sbProduct.append("'");
							sbProduct.append(ProductInfo.get(i));
							sbProduct.append("'");
						}
						sbProduct.append(")");
						sbProduct.append(" AND ACCEPTED_DATE > '");
						sbProduct.append(OrderCreateTime);
						sbProduct.append("'  ");
				}else {
					log.info("精准匹配产品时，无产品编码");
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("精准匹配产品时，无产品编码");
					SyslogDao.insert(SysLogIns);
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
				//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MAXMONTHDAY'
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("("+strTmp+")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(MaxMonthDay);
				sbProduct.append("'");
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")"  );
				}
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					//配置的SQL：AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE DATE_ID = 'MMMMMAXMONTHDAY'
					// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(MaxMonthDay);
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
				//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY'
				// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("("+strTmp+")");
				sb.append(" AND DATE_ID =  '");
				sb.append(MaxMonthDay);
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
					//配置的SQL：SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE AND DATE_ID =  'MMMMMAXMONTHDAY'
					// 后续的添加SQL：sbProduct.append(" AND " + "("+strTmp+")"); (判断是否为空)  sbProduct.append(" AND " + "("+strAddSql+")"  );
					sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
					sb.append("("+strAddSql+")");
					sb.append(" AND DATE_ID = '");
					sb.append(MaxMonthDay);
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
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType);
			SyslogDao.insert(SysLogIns);
			
//			//一条成功标准sql插入一条日志
//			PltCommonLog		logdb = new PltCommonLog();
//	//		int  SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
//			logdb.setLOG_TYPE("01");
//			logdb.setSERIAL_ID(SerialId);
//			logdb.setSTART_TIME(new Date());
//			logdb.setSPONSOR("orderCheck");
//			logdb.setBUSI_CODE("getSqlForSucessType,成功类型="+successType);
//			logdb.setBUSI_DESC("SusccessSql+select还未加USER_ID");
//			logdb.setBUSI_ITEM_1(sb.toString()+sbProduct.toString());
//			logdb.setBUSI_ITEM_2(""+activity_seq_id);
//			//logdb.setBUSI_ITEM_3(m_strTableName);
//			BusiToolsIns.insertPltCommonLog(logdb);
			
			if(bProduct){
				return sbProduct.toString();
			}else {
				return sb.toString();
			}

	}
	private	String		getSqlForTelecomSucessType(String tenant_id,int activity_seq_id){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderSucessCheck.class+"-getSqlForTelecomSucessType");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_1("ActivitySeqId="+activity_seq_id);
		// --- 检查是否存在成功标准 ---------------
		if(ActivitySucessInfoIns == null)  return null;
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false){
			return null;
		}
		
		boolean			bProduct = false;    // --- 是否成功标准中有产品 ---
		StringBuilder		sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm=new SimpleDateFormat("yyyyMM"); //设置时间格式
		
		String		OrderCreateTime ;
		OrderCreateTime = sdfYm.format(new Date()) +"01";     // --- 固定从1号开始判断是否办理了产品 ---
		
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  //1
//		Calendar lastDate = Calendar.getInstance();  
//		lastDate.set(Calendar.DATE,1);//设为当前月的1 号  
//		lastDate.add(Calendar.MONTH,-1);//减一个月，变为下月的1 号  
//		//lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天  
//		String		OrderCreateTime=sdf.format(lastDate.getTime());//1	
		
		String			strMatchingType = ActivitySucessInfoIns.getMatchingType();
		if(StringUtils.isNotNull(strMatchingType)){
			if(strMatchingType.equals("2")){  // --- 精确匹配产品  ---
				// --- 提取活动成功产品列表 ---
				
				bProduct = true; 
				int		i = 0;
				if(ProductInfo != null && ProductInfo.size() > 0){   // --- 得到SQL  ---
					 
					//sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					sbProduct.append(" SELECT PROD_INST_ID AS USER_ID FROM M_PRODUCT_ACCEPTED WHERE OFFER_NBR IN (");
					for(i = 0;i < ProductInfo.size();++i){
						if(i > 0)  sbProduct.append(",");
							sbProduct.append("'");
							sbProduct.append(ProductInfo.get(i));
							sbProduct.append("'");
						}
						sbProduct.append(")");
						sbProduct.append(" AND COMPLETE_DT > '");
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
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())){
			//strAddSql = m_cssSuccessStandardPo.getSuccessConditionSQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll("a.", "");
		}
		if(StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())){
			//strTmp = m_cssSuccessStandardPo.getSuccessTypeConditionSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll("a.", "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		//精准营销时
		if(bProduct){
			if(strTmp!=null){                 
				sbProduct.append(" AND PROD_INST_ID IN ( SELECT PB0000 FROM CUST_LABEL WHERE ");
				sbProduct.append("("+strTmp+")");
				sbProduct.append(" AND ");
				sbProduct.append(" acct_day = '");
				sbProduct.append(MaxMonthDay);
				sbProduct.append("'");
				if(strAddSql!=null){
					sbProduct.append(" AND " + "("+strAddSql+")" );
				}
			}else{                               //--- 成功标准类型中，无成功标准    如3,4,5
				if(strAddSql!=null){
					sbProduct.append(" AND PROD_INST_ID IN ( SELECT PB0000  FROM CUST_LABEL WHERE ");
					sbProduct.append(" acct_day = '");
					sbProduct.append(MaxMonthDay);
					sbProduct.append("'");
					sbProduct.append(" AND " + "("+strAddSql+")"   );
				}
			}
			log.info("精准营销：");

		}else{
			//无精准营销但有成功标准
			if(strTmp!=null){
				sb.append(" SELECT PROD_INST_ID AS USER_ID FROM CUST_LABEL WHERE  ");
				sb.append("("+strTmp+")");
				sbProduct.append(" acct_day = '");
				sb.append(MaxMonthDay);
				sb.append("'");
				if(strAddSql!=null){
					sb.append(" AND " + "("+strAddSql+")"   );
				}

			}else{                                  // --- 无成功标准  ---

				if(strAddSql!=null){
					sb.append(" SELECT PROD_INST_ID AS USER_ID FROM CUST_LABEL WHERE  ");
					sb.append("("+strAddSql+")" );
					sbProduct.append(" acct_day = '");
					sb.append(MaxMonthDay);
					sb.append("' ");

				}else {                                 // --- 全没有时  ---
					log.info("此产品不存在或者没有成功标准可以判断");
					return null;
				}			
			}	
		}
		
		log.info("工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("工单检查sql="+sb.toString()+sbProduct.toString()+", 成功类型="+successType);
		SyslogDao.insert(SysLogIns);
		
		if(bProduct){
			return sbProduct.toString();
		}else {
			return sb.toString();
		}
		

	}
	//--省份不同，行云中ACCEPTED_DATE的格式也不尽相同--
	//--按照各自TENANT_ID指派不同的日期格式--
	private String getLastOrderCreateTime(String tenantId){
		String		OrderCreateTime = null ;
		Date lastOrderCreateTime = ActivitySucessInfoIns.getLastOrderCreateTime();
		if(tenantId.equals("uni081")){
			SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
			SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
			
			log.info("----OrderSucessCheck-----OrderCreateTime="+lastOrderCreateTime+"  filter is null and  check is not null");
			if(lastOrderCreateTime != null){
				OrderCreateTime = sdfYmd.format(lastOrderCreateTime);
			}
			else{
				OrderCreateTime = sdfYm.format(new Date()) +"-01";
			}
		}else if(tenantId.equals("uni097")){
			SimpleDateFormat sdfYm=new SimpleDateFormat("yyyyMM"); //设置时间格式
			SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyyMMdd"); //设置时间格式
			log.info("----OrderSucessCheck-----OrderCreateTime="+lastOrderCreateTime+"  filter is null and  check is not null");
			if(lastOrderCreateTime != null){
				OrderCreateTime = sdfYmd.format(lastOrderCreateTime);
			}
			else{
				OrderCreateTime = sdfYm.format(new Date()) +"01";
			}
		}else{

			SimpleDateFormat sdfYm=new SimpleDateFormat("yyyy-MM"); //设置时间格式
			SimpleDateFormat sdfYmd=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
			
			log.info("----OrderSucessCheck-----OrderCreateTime="+lastOrderCreateTime+"  filter is null and  check is not null");
			if(lastOrderCreateTime != null){
				OrderCreateTime = sdfYmd.format(lastOrderCreateTime);
			}
			else{
				OrderCreateTime = sdfYm.format(new Date()) +"-01";
			}
		
		}
		
		return OrderCreateTime;
	}

}
