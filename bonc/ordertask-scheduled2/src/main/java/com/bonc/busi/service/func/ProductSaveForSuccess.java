package com.bonc.busi.service.func;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.service.dao.CommonDao;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.mapper.ProductSaveMapper;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.utils.HttpUtil;

public class ProductSaveForSuccess extends ParallelFunc{
	private final static Logger log= LoggerFactory.getLogger(ProductSaveForSuccess.class);
	private ProductSaveMapper ProductSaveMapperIns = SpringUtil.getBean(ProductSaveMapper.class);
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();  // --- 日志变量 ---
	private	BusiTools					BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	Date							dateBegin = null;    // --- 开始时间  ---
	private String                      	newDate  = null;        //  --- 把租户id和时间作为唯一标志使用
	private	int  							avgCount = 0;   // --- 日志序列号 ---
	private	int  							SerialId =0;   // --- 日志序列号 ---
	// --- 定义当前工单变量 ---
	private	long							curOrderRecId = 0;
	// --- 定义总的数量 ---
	private	int							m_iTotalNum = 0;
	private	boolean					bSetLockFlag = false;

	private	String						TenantId = null;
	
	
	public		void		setTenantId(String tenantid){
		this.TenantId = tenantid;
	}

	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ProductSaveForSuccess.class+"-begin");
		SysLogIns.setTENANT_ID(TenantId);
		dateBegin  = new Date();
		
		
		if(TenantId == null){
			log.error("租户编号为空");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("租户编号为空");
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdft = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuilder		sb = new StringBuilder();
		String newTime = sdft.format(dateBegin);
		newDate = TenantId+"."+newTime;
		sb.append(sdf.format(dateBegin));
		 
		bSetLockFlag = true;
//		// --- 清空内存表 ---
//		sb.setLength(0);
//		sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
//		sb.append("TRUNCATE TABLE PLT_ORDER_PRODUCT_SAVE_MEM");
//		CommonDao.update(sb.toString());
		
		int   allRecNum = 0;
		// ----查询需要处理相关活动受理成功的用户
		List<HashMap<String, Object>> userList = ProductSaveMapperIns.getUserList(TenantId);
		
		if(userList==null||userList.size()<=0){
			log.error("无受理成功的用户处理");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("无受理成功的用户处理."+TenantId);
			SyslogDao.insert(SysLogIns);
			return -1;
		}
		
		for(HashMap<String, Object> user:userList){
			
			String   activityUrl = SyscommoncfgDao.query("ACTIVITY_INFO_ORDERUPDATEACTIVITY");
			//--- 调用活动接口获取相关活动 ---
			String activityResut = HttpUtil.doGet(activityUrl, user);
			HashMap<String, Object> dataMap= JSONObject.parseObject(activityResut,HashMap.class);
			
			if(dataMap.get("resultCode").equals("1")){
				// 得到相关活动的表名+活动批次号
				List<String> activityMap = (List<String>) dataMap.get("result");
				String activityIds = "";
				for(int i=0;i<activityMap.size();i++){
					if(i!=0)activityIds+=",";
					activityIds+="'";
					activityIds+=activityMap.get(i);
					activityIds+="'";
				}
				user.put("activityIds", activityIds);
				
//				user.put("activityIds", "'42923'");
				List<HashMap<String, Object>> tableNames = ProductSaveMapperIns.getTableNames(user);
				
				
				// --- 将所有纪录入内存表  ---
				sb.setLength(0);
				sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
				sb.append("INSERT INTO PLT_ORDER_PRODUCT_SAVE_MEM(USER_ID,DATE_ID,PRODUCT_ID,TENANT_ID,ACTIVITY_SEQ_ID,TABLE_NAME,NEW_TIME) VALUES ");
				//拼接需要插入的value
				for(int i = 0 ; i<tableNames.size() ; i++){
					sb.append("(");
						sb.append("'"+user.get("userId")+"',");
						sb.append("'"+user.get("DATE_ID")+"',");
						sb.append("'"+user.get("productId")+"',");
						sb.append("'"+TenantId+"',");
						sb.append("'"+tableNames.get(i).get("ACTIVITY_SEQ_ID")+"',");
						sb.append("'"+tableNames.get(i).get("TABLE_NAME")+"',");	
						sb.append("'"+newDate+"'");	
					sb.append(")");
					if(i<tableNames.size()-1)sb.append(",");
				}
				allRecNum += CommonDao.update(sb.toString());
			}else{
				log.error("无受理成功的相关活动");
				SysLogIns.setLOG_TIME(new Date());
				SysLogIns.setLOG_MESSAGE("无受理成功的相关活动."+TenantId);
				SyslogDao.insert(SysLogIns);
				return -1;
			}
		}
		// --- 取数据时一次去多少条 ---
		avgCount = allRecNum/4 + 1;
		log.info("newTime={}  入内存表数量={}  一次get数量={}",newDate,allRecNum,avgCount);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("受理成功在"+sdf.format(dateBegin)+"时刻，入内存表数量="+allRecNum);
		SyslogDao.insert(SysLogIns);	
		// --- 纪录日志 ---
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");

		PltCommonLogIns.setLOG_TYPE("333");
		PltCommonLogIns.setSPONSOR("PRODUCTSAVE");
		PltCommonLogIns.setBUSI_DESC("受理成功开始");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_ITEM_4(TenantId);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("受理成功开始."+TenantId);
		SyslogDao.insert(SysLogIns);
		return 0;
	}

	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		// --- 解锁标识 ---
		SyscommoncfgDao.update("ASYNPRODUCTSAVE.RUN.FLAG."+TenantId,"FALSE");
		bSetLockFlag = false;
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ProductSaveForSuccess.class+"-end");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("受理成功正常结束."+TenantId);
		SyslogDao.insert(SysLogIns);
		
		Date				dateEnd = new Date();
		log.info("--- 受理成功结束 耗时:{}", dateEnd.getTime() - dateBegin.getTime());
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("受理成功结束");
		PltCommonLogIns.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		
		return 0;
	}
	/*
	 * 退出函数
	 */
	@Override
	public		int		finallyFunc(){
		
//		// --- 清空内存表 ---
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		sb.append(SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
		sb.append("DELETE FROM PLT_ORDER_PRODUCT_SAVE_MEM WHERE NEW_TIME ='"+newDate+"'");
		CommonDao.update(sb.toString());
		
		if(bSetLockFlag){
			SyscommoncfgDao.update("ASYNPRODUCTSAVE.RUN.FLAG."+TenantId,"FALSE");
			bSetLockFlag = false;			
		}
		return 0;
	}
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		List<HashMap<String, Object>>	listOrderCheckInfo = null;
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ProductSaveForSuccess.class+"-get");
		SysLogIns.setTENANT_ID(TenantId);
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("开始获取工单数据."+TenantId);
		SyslogDao.insert(SysLogIns);
		
		String sql = (SyscommoncfgDao.query("ORDERSUCESSCHECK.SQLPRE").replaceFirst("TTTTTENANT_ID", TenantId));
		listOrderCheckInfo = ProductSaveMapperIns.getOrderInfo(sql, TenantId, curOrderRecId,newDate,avgCount);
	
		if(listOrderCheckInfo == null || listOrderCheckInfo.size() == 0){  // --- 无数据 ---
			log.info("租户ID:"+TenantId +" 受理成功标识段:"+newDate+ "  起始工单号:"+curOrderRecId 
					+ "  无数据,总纪录数 " +m_iTotalNum );
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("无数据."+TenantId);
			return null;
		}
		
		
		// --- 累加工单数量 ---
		m_iTotalNum += listOrderCheckInfo.size();
		
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("获取受理成功----工单数据."+TenantId);
		SysLogIns.setBUSI_ITEM_1("纪录数:"+listOrderCheckInfo.size());
		SysLogIns.setBUSI_ITEM_2(" 受理成功时间:"+newDate);
		SysLogIns.setBUSI_ITEM_3(" 起始工单号:"+curOrderRecId);
		SysLogIns.setBUSI_ITEM_4(" 前总纪录数:"+m_iTotalNum);
		SyslogDao.insert(SysLogIns);
		
		// --- 设置传递的数据 ---
		Map<String,Object>		map = new HashMap<String,Object>();
		map.put("minId", curOrderRecId);
		
		// --- 设置当前最大工单号 ---
		curOrderRecId = Long.parseLong(String.valueOf(listOrderCheckInfo.get(listOrderCheckInfo.size()-1).get("orderRecId")));
		
		map.put("maxId", curOrderRecId);
		map.put("TENANTID", TenantId);
//		map.put("ACTIVITYSEQID", ActivitySeqId);
		map.put("ORDERCHECKINFO",listOrderCheckInfo);
		return map;	
	}
	
	/*
	 * 处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public		int		handle(Object data){
		
		//得到需要处理的用户数据
		HashMap<String,Object>		mapData = (HashMap<String,Object>) data;
		List<HashMap<String, Object>>	listOrderCheckInfo = (List<HashMap<String, Object>>) mapData.get("ORDERCHECKINFO");
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(int i = 0;i<listOrderCheckInfo.size();i++){
			sb.setLength(0);
			sb.append("UPDATE ");
			sb.append(listOrderCheckInfo.get(i).get("tableName"));
			sb.append(" SET  SUCCESS_STATUS = 1 ,SUCCESS_UPDATE_TIME = now(),");
			sb.append(" SUCCESS_DATEID = '"+listOrderCheckInfo.get(i).get("dateId")+"',");
			sb.append(" BUSINESS_RESERVE47 = '"+listOrderCheckInfo.get(i).get("productId")+"'");
			sb.append(" WHERE TENANT_ID='");
			sb.append(TenantId);
			sb.append("'");
			sb.append("  AND ACTIVITY_SEQ_ID =");
			sb.append(listOrderCheckInfo.get(i).get("activitySeqId"));
			sb.append("  AND USER_ID =");
			sb.append(listOrderCheckInfo.get(i).get("userId"));
			log.info(sb.toString());
			count  +=   CommonDao.update(sb.toString());
		}
					
		StringBuilder users = new StringBuilder();
		users.setLength(0);		
		for(int i =0;i < listOrderCheckInfo.size();++i){
			if(i != 0) users.append(",");
			users.append("'");
			users.append(listOrderCheckInfo.get(i).get("userId"));
			users.append("'");
		}
		String userIds = users.toString();
		//更新受理表更新标识RESERVE2
		ProductSaveMapperIns.updateProductSave(userIds);
			
		log.info("--- end to handle data ,更新数量：{}  ,时间：{}",count,new Date());	
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ProductSaveForSuccess.class+"-handle");
		SysLogIns.setLOG_TIME(new Date());
		SysLogIns.setLOG_MESSAGE("handle--工单数据完成"+"处理数:"+count+","+TenantId);
		
		return 0;
	}
	
}
