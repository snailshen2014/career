package com.bonc.busi.service.func;
/*
 * @desc:工单用户资料更新
 * @author:zengdingyong
 * @time:2017-06-06
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.service.dao.CommonDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;

public class OrderUserlabelUpdate extends ParallelFunc{
	private final static Logger log= LoggerFactory.getLogger(OrderUserlabelUpdate.class);
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	BusiTools					BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	Date							dateBegin = null;    				// --- 开始时间  ---
	private	int  							SerialId =0;   						// --- 日志序列号 ---
	private	boolean					bLockFlag = false;				// --- 加锁标识 ---
	private	long							lCurBeginRecId = -1;						// --- 开始序列号  ---
	private	long							lCurEndRecId = -1;				// --- 结束序列号 ---
	private	int							iCurTablePos = 0;					// --- 当前表在LIST中的位置  --0
	private	List<Map<String,Object>>   listTable = null;
	private	long							lOneUpdate  = 10000;			// --- 一次更新条数 ---
	private AtomicInteger 							count = new AtomicInteger(0); 					//--更新用户资料的工单数量--
	
	// --- 定义外部传入变量 -------------------------
	private	String						TenantId = null;
	private String 						channelId = null;
	private String 						updateSql = null;
	private String 						StrCurMonthDay = null;
	private short 						updateType = -1;//--判断修改字段调用更新还是账期更改更新，修改字段调用更新：1，账期更改更新：0--
	public		void		setTenantId(String tenantid){
		this.TenantId = tenantid;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}
	public 		void 		setStrCurMonthDay(String strCurMonthDay){
		this.StrCurMonthDay = strCurMonthDay;
	}
	public short getUpdateType() {
		return updateType;
	}
	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}
	
	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){  // ---   ---
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderUserlabelUpdate.class+"-begin");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_3(channelId);
		SysLogIns.setBUSI_ITEM_4(StrCurMonthDay);
		SysLogIns.setBUSI_ITEM_5("05");
		dateBegin  = new Date();
		if(TenantId == null){
			log.error("租户编号为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("租户编号为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		// --- 得到所有的需要更新表名 ---
		String			sqlTableName = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.SQL.TABLES");
		String sqlTmpTableName = sqlTableName.replaceFirst("CCCCCHANNEL_ID",channelId);
		String sqlLocalTableName = sqlTmpTableName.replaceFirst("TTTTTENANT_ID",TenantId);
				
		PltCommonLogIns.setBUSI_ITEM_1(sqlTableName);
		//--渠道ID--
		listTable = CommonDao.queryList(sqlLocalTableName);
			if(listTable == null || listTable.size() == 0){
				PltCommonLogIns.setBUSI_ITEM_1(channelId+"：没有需要更新的表");
				log.warn(channelId+"：没有需要更新的表 ");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE(channelId+"：没有需要更新的表 ");
				SyslogDao.insert(SysLogIns);
				return -1;
			}
		// --- 更新当前运行标识 ---
		SyscommoncfgDao.update("ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId,"TRUE");
		bLockFlag = true;
		
		// --- 纪录日志 ---
				// --- 得到序列号 ---
				SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
				PltCommonLogIns.setLOG_TYPE("05");
				PltCommonLogIns.setTENANT_ID(TenantId);
				PltCommonLogIns.setSPONSOR("ORDERUSERLABELUPDATE");
				PltCommonLogIns.setSERIAL_ID(SerialId);
				PltCommonLogIns.setSTART_TIME(dateBegin);
				PltCommonLogIns.setBUSI_CODE("BEGIN");
				PltCommonLogIns.setBUSI_DESC("工单用户资料更新开始");
				PltCommonLogIns.setBUSI_ITEM_2(channelId);
				PltCommonLogIns.setBUSI_ITEM_6(StrCurMonthDay);
				System.out.println(PltCommonLogIns.toString());
				BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		
		
		String			oneNums = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.ONE.NUMS");
		long				dbOneNums = Long.parseLong(oneNums);
		if(dbOneNums > lOneUpdate) lOneUpdate = dbOneNums;
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("工单用户资料更新开始");
		SyslogDao.insert(SysLogIns);
		
		

		return 0;
	}
	
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		SysLog			SysLogIns = new SysLog();
		// --- 提取本地账期 ---
		String		dbDateId = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.XCLOUD.DATEID."+TenantId);
		//--标签字段发生变化，因调用接口时执行过程正在进行，而延迟，判断标签是否变化的标识--
		String		   asynOrderLabelUpdateFlag = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.LABELUPDATE.FLAG."+TenantId);
		
		if(StrCurMonthDay != null|| dbDateId!= null){
			if(asynOrderLabelUpdateFlag!=null){
				if(StrCurMonthDay.equals(dbDateId) && asynOrderLabelUpdateFlag.equals("TRUE")){
					SyscommoncfgDao.update("ORDER.USERLABEL.UPDATE.LABELUPDATE.FLAG."+TenantId,"FALSE");
				}	
			}
			//--更新账期--
			if(!(StrCurMonthDay.equals(dbDateId))){
				SyscommoncfgDao.update("ORDER.USERLABEL.UPDATE.XCLOUD.DATEID."+TenantId,StrCurMonthDay);
			}
		}
		
		
		// --- 更新当前运行标识 ---
		SyscommoncfgDao.update("ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId,"FALSE");
		bLockFlag = false;
		Date				dateEnd = new Date();
		log.info("--- 工单用户资料更新结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
		PltCommonLogIns.setEND_TIME(new Date());
		PltCommonLogIns.setLOG_TYPE("05");
		PltCommonLogIns.setBUSI_DESC("工单用户资料更新结束");
		PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
		//PltCommonLogIns.setBUSI_ITEM_1(null);
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setDEST_NUM(count.get());
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderUserlabelUpdate.class+"-end");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setBUSI_ITEM_3(channelId);
		SysLogIns.setBUSI_ITEM_4(StrCurMonthDay);
		SysLogIns.setBUSI_ITEM_5("05");
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("工单用户资料更新结束");
		SyslogDao.insert(SysLogIns);
		return 0;
	}
	/*
	 * 退出函数
	 */
	@Override
	public		int		finallyFunc(){
		if(bLockFlag){
			
			SyscommoncfgDao.update("ORDER.USERLABEL.UPDATE.RUNFLAG."+TenantId,"FALSE");
			Date				dateEnd = new Date();
			log.info("--- 工单用户资料更新强制结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
			PltCommonLogIns.setEND_TIME(new Date());
			PltCommonLogIns.setLOG_TYPE("05");
			PltCommonLogIns.setBUSI_DESC("工单用户资料强制更新结束");
			PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
			//PltCommonLogIns.setBUSI_ITEM_1(null);
			PltCommonLogIns.setDEST_NUM(count.get());
			PltCommonLogIns.setBUSI_CODE("FAIL");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		}
		return 0;
	}
	
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderUserlabelUpdate.class+"-get");
		SysLogIns.setTENANT_ID(TenantId);
		StringBuilder			sb = new StringBuilder();
		while(1 > 0){
			if(lCurEndRecId == -1){          // --- 当前表的最小，最大纪录还没有提取
				// --- 提取最大和最小纪录 ---
				sb.setLength(0);
				sb.append(" SELECT IFNULL(MAX(REC_ID),0) MAXID,IFNULL(MIN(REC_ID),0) MINID FROM ");
				sb.append(listTable.get(iCurTablePos).get("TABLE_NAME"));
				sb.append("  WHERE CHANNEL_ID = '"+channelId+"' ");
				sb.append(" AND TENANT_ID='"+TenantId +"' ");
				
				if(updateType == (short)0){
					//--ORDER_DEAL_MONTH != StrCurMonthDay--只查询旧账期下的工单数据
					sb.append(" AND ORDER_DEAL_MONTH <> '"+StrCurMonthDay+"' ");
				}else if (updateType == (short)1){
					//--查询所有的工单数据--
				}
					
				Map<String,Object >   recData = CommonDao.queryOne(sb.toString());
				lCurBeginRecId = (Long)recData.get("MINID");
				lCurEndRecId =  (Long)recData.get("MAXID");
				continue;
			}
			else{
				if(lCurBeginRecId >= lCurEndRecId){                 // --- 该换新表了 ---
					SysLogIns.setLOG_TIME(new Date());
					SysLogIns.setLOG_MESSAGE("换新表 ");
					SysLogIns.setBUSI_ITEM_1("旧表名:"+listTable.get(iCurTablePos).get("TABLE_NAME"));
					SysLogIns.setBUSI_ITEM_2("开始REC_ID:"+lCurBeginRecId+",结束REC_ID:"+lCurEndRecId);
					SysLogIns.setBUSI_ITEM_3(channelId);
					SysLogIns.setBUSI_ITEM_4(StrCurMonthDay);
					SysLogIns.setBUSI_ITEM_5("05");
					SyslogDao.insert(SysLogIns);
					if(iCurTablePos  >= listTable.size() -1)  {
						SysLogIns.setLOG_TIME(new Date());
						SysLogIns.setLOG_MESSAGE("没有表了，当前序列:"+iCurTablePos);
						SysLogIns.setBUSI_ITEM_1("旧表名:"+listTable.get(iCurTablePos).get("TABLE_NAME"));
						SysLogIns.setBUSI_ITEM_2("开始REC_ID:"+lCurBeginRecId+",结束REC_ID:"+lCurEndRecId);
						SysLogIns.setBUSI_ITEM_3(channelId);
						SysLogIns.setBUSI_ITEM_4(StrCurMonthDay);
						SysLogIns.setBUSI_ITEM_5("05");
						SyslogDao.insert(SysLogIns);
						return null;   // --- 表结束了  ---
					}
					iCurTablePos++;
					// --- 提取最大和最小纪录 ---
					sb.setLength(0);
					sb.append("SELECT IFNULL(MAX(REC_ID),0) MAXID,IFNULL(MIN(REC_ID),0) MINID FROM ");
					sb.append(listTable.get(iCurTablePos).get("TABLE_NAME"));
					/*sb.append("  WHERE CHANNEL_ID = '5' AND TENANT_ID='");
					sb.append(TenantId);*/
					sb.append("  WHERE CHANNEL_ID = '"+channelId+"' ");
					sb.append(" AND TENANT_ID= '"+TenantId+"'");
					
					if(updateType == (short)0){
						//--ORDER_DEAL_MONTH != StrCurMonthDay--只查询旧账期下的工单数据
						sb.append(" AND ORDER_DEAL_MONTH <> '"+StrCurMonthDay+"' ");
					}else if (updateType == (short)1){
						//--查询所有的工单数据--
					}
					
					Map<String,Object >   recData = CommonDao.queryOne(sb.toString());
					lCurBeginRecId = (Long)recData.get("MINID");
					lCurEndRecId =  (Long)recData.get("MAXID");	
				}
			}
			if(lCurBeginRecId >= lCurEndRecId)  continue;
			else break;
		}
		// --- 计算开始值和结束值 ---
		// --- 设置传递的数据 ---
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("BEGINRECID", lCurBeginRecId);
		map.put("TABLENAME", listTable.get(iCurTablePos).get("TABLE_NAME"));	
		lCurBeginRecId +=  lOneUpdate;               // --- ----
//		SysLogIns.setLOG_TIME(new Date());
//		SysLogIns.setLOG_MESSAGE("返回数据");
//		SysLogIns.setBUSI_ITEM_1("旧表名:"+listTable.get(iCurTablePos).get("TABLE_NAME"));
//		SysLogIns.setBUSI_ITEM_2("当前序列:"+iCurTablePos);
//		SysLogIns.setBUSI_ITEM_3("开始REC_ID:"+lCurBeginRecId);
//		SysLogIns.setBUSI_ITEM_4("结束REC_ID:"+lCurEndRecId);
//		SyslogDao.insert(SysLogIns);
		return map;
	}
	
	/*
	 * 处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public		int		handle(Object data){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+OrderUserlabelUpdate.class+"-handle");
		SysLogIns.setTENANT_ID(TenantId);
		Date				handelBeginTime = new Date();
		log.info("--- begin to handle data 时间:"+handelBeginTime);
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;
		long				BeginRecId = (Long)mapData.get("BEGINRECID");	
		String			TableName = (String)mapData.get("TABLENAME");	
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setBUSI_ITEM_1(TableName);
		SysLogIns.setBUSI_ITEM_2("开始REC_ID:"+BeginRecId+",结束REC_ID:"+(BeginRecId +lOneUpdate));
		SysLogIns.setBUSI_ITEM_3(channelId);
		SysLogIns.setBUSI_ITEM_4(StrCurMonthDay);
		SysLogIns.setBUSI_ITEM_5("05");
		StringBuilder			sb = new StringBuilder();
		sb.setLength(0);
		//String			updateSql = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.SQL.UPDATE."+TenantId);
		
		if(updateSql == null){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("处理SQL为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		// --- 提取有效的标签编号  ---
		String			labelId = SyscommoncfgDao.query("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId);
		if(labelId == null){
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("参数: ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+TenantId+"没有设置或设置有误");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		String		userlabelTableName = "PLT_USER_LABEL_"+labelId;
		// --- 替换  ---
		updateSql  =  updateSql.replaceFirst("TABLEAAAAA", TableName);           // --- 替换工单表名  ---
		updateSql  =  updateSql.replaceFirst("TABLEBBBBB", userlabelTableName);  // --- 替换标签表名 ---
		updateSql  =  updateSql.replaceFirst("TTTTTENANT_ID", TenantId); 				// --- 替换租户编号 ---
		updateSql  =  updateSql.replaceFirst("MINID", Long.toString(BeginRecId)); 				// --- 替换开始RECID ---
		updateSql  =  updateSql.replaceFirst("MAXID", Long.toString(BeginRecId + lOneUpdate)); 				// --- 替换结束RECID ---
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("更新SQL:"+updateSql);
		SyslogDao.insert(SysLogIns);
		
		// --- 开始执行 ---
		int		countTemp = CommonDao.update(updateSql);
		count.addAndGet(countTemp);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("执行结束:"+updateSql);
		SysLogIns.setBUSI_ITEM_1(Integer.toString(count.get()));
		SyslogDao.insert(SysLogIns);
		

		
		return 0;
	}
	

}
