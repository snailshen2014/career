package com.bonc.busi.task.service.impl;
/*
 * @desc:基础任务实现
 * @author:曾定勇
 * @time:2016-11-26
 */

import com.bonc.busi.activity.SuccessStandardPo;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.InnerDbQuerySrv;
import com.bonc.busi.task.base.ParallelManage;
import com.bonc.busi.task.instance.OrderFilterSucess;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.busi.task.service.BaseTaskSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;



@Service()
//@Transactional
public class BaseTaskSrvImpl implements BaseTaskSrv{
	
	private final static Logger log = LoggerFactory.getLogger(BaseTaskSrvImpl.class);
	

	@Autowired
	private  OrderSucessFunc  OrderSucessFuncIns;
	//@Autowired
	//private OrderCheckSucessFunc OrderCheckSucessFuncIns;
	@Autowired
	private BusiTools  BusiToolsIns;
	@Autowired
	private BaseMapper  TaskBaseMapperDao;
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	//@Autowired
	// private PltActivityInfoDao TaskActivityDao;

//	@Autowired	private ParrelHandleFunc ParrelHandleFunc;
	@Autowired	private InnerDbQuerySrv InnerDbQuerySrv;
	
	/*
	 * 工单生成后过滤已经成功的工单
	 */
	public		boolean			orderFilterSucess(int  activitySeqId,SuccessStandardPo sucessCon,String tenantId){
		// --- 定义2个线程，无数据退出 ---
		int			iThreadNum = 2;
		String		strTmp = BusiToolsIns.getValueFromGlobal("ORDERSUCESSCHECK.THREADSNUM");
		if(strTmp != null){
			iThreadNum = Integer.parseInt(strTmp);
			if(iThreadNum < 2)  iThreadNum = 2;
		}
		log.info("thread num:{}",strTmp);
		
		// --- 查询各个表当前活动下的工单量，并按工单量大小排序 ---
		List<String> tableNames = new ArrayList<String>();
		tableNames.add("PLT_ORDER_INFO_TEMP");
		List<HashMap<String, Object>> countRecs = new ArrayList<HashMap<String, Object>>();
		for(String tableName:tableNames){
			HashMap<String, Object> countRec =  TaskBaseMapperDao.getCountForActivity(tableName,activitySeqId,tenantId);
//			if(!"0".equals(countRec.get("num")+"")){
				countRecs.add(countRec);
//			}	
		}
		
		for(int j = countRecs.size()-1;j>=0;j--){
			for(int i=0;i<j;i++){
				if(Integer.parseInt(String.valueOf(countRecs.get(i).get("num"))) < Integer.parseInt(String.valueOf(countRecs.get(i+1).get("num")))){
					HashMap<String, Object> temp = countRecs.get(i);
					countRecs.set(i, countRecs.get(i+1));
					countRecs.set(i+1, temp);
				}
			}
		}
		log.info("----根据表工单量排序后----->"+countRecs);
		
		// --- 启动任务  ---
		OrderFilterSucess		OrderFilterSucessIns = new OrderFilterSucess();
		OrderFilterSucessIns.setActivitySeqid(activitySeqId);
		OrderFilterSucessIns.setTenantId(tenantId);
		OrderFilterSucessIns.setTable(countRecs);
		OrderFilterSucessIns.setSuccessStandardPo(sucessCon);		
		ParallelManage ParallelManageIns = new ParallelManage(OrderFilterSucessIns,iThreadNum);
		ParallelManageIns.setMaxWaitSeconds(1200);
		ParallelManageIns.execute();
		return true;
	}
	
	/*
	 * 测试MYCAT
	 */
	//@Transactional
	public		int		testMycat(){
		
		// --- 更新统计  ---
				StringBuilder  sb  = new StringBuilder();
				// --- 提取有效租户信息 ---
				List<Map<String, Object>>	listTenantInfo = null;			// --- 有效租户信息 ---
				listTenantInfo = BusiToolsIns.getValidTenantInfo();
				if(listTenantInfo == null || listTenantInfo.size() == 0){   // ---  无有效租户纪录 ---
					return 0;
				}
				for(Map<String, Object>  item:listTenantInfo){
					String     strTenantId =(String) item.get("TENANT_ID");
					sb.setLength(0);
					sb.append("SELECT REC_ID FROM PLT_ACTIVITY_INFO WHERE ACTIVITY_STATUS !='2' ");
					sb.append(" AND TENANT_ID ='");
					sb.append(strTenantId);
					sb.append("'");
					long			iActivitySeqId = 0;
					List<Map<String,Object>>  listResult = jdbcTemplate.queryForList(sb.toString());
					for(Map<String,Object> rec: listResult ){
						iActivitySeqId = (Long)rec.get("REC_ID");
						log.info("seqid={}",iActivitySeqId);
						// --- begin to call statics ---
						//StatisticServiceIns.statisticBench(strTenantId, String.valueOf(iActivitySeqId));
					}
				}
				/*
		try{
			
			 List<Map<String, Object>> 	listTenantInfo = BusiToolsIns.getValidTenantInfo();
				if(listTenantInfo == null  || listTenantInfo.size() == 0){
					log.warn("没有找到有效的租户信息");
					return -1;
				}
			AsynUserLabelIns.handleDataDirect(listTenantInfo.get(0));
			*/
			
//			String		strSqlData = "SELECT 'TTTTTENANT_ID'  TENANT_ID,  PROV_ID , AREA_NO  AREA_ID, CITY_ID, USER_ID, "
//					+ "DEVICE_NUMBER, REPLACE(REPLACE(CUST_NAME,'\\','/'),'\"','')  CUST_NAME, case when NUM_21 is null then '0' else to_char(round(NUM_21*100)) end BALANCE_FEE,  case when KD_BALANCE_FEE is null then '0' else to_char(round(KD_BALANCE_FEE*100)) end KD_BALANCE_FEE, BA_S_01 USER_STATUS, DATA_TYPE USER_TYPE,  KD_PRODUCT_ID MB_PACKAGE_ID, KD_PRODUCT_ID KD_PACKAGE_ID, NUM_24 MB_FIRST_OWE_MONTH,  KD_CHR_2  KD_FIRST_OWE_MONTH, case when OWE_FEE>0 then '是'  else '否'  end OWE_FLAG,  case when OWE_FEE is null then '0' else to_char(round(OWE_FEE*100)) end MB_OWE_FEE,   case when KD_NUM_2 is null then '0' else to_char(round(KD_NUM_2*100)) end KD_OWE_FEE,   BA_G_02 MB_OWE_MONTHS,  case when ONLINE_DUR_LVL=0 or ONLINE_DUR_LVL is null then LAST3_TOTAL_FEE  when ONLINE_DUR_LVL >=3 then ROUND(LAST3_TOTAL_FEE/3,2)  when  ONLINE_DUR_LVL <3  then ROUND(LAST3_TOTAL_FEE/ONLINE_DUR_LVL,2)  end  MB_ARPU,  W3_PROD_ID_1 ELECCHANNEL_FLAG,  case when RENT_FEE > 96 then '是'  else '否'  end  HIGH_96_FLAG,  case when RENT_FEE is null then '0' else to_char(round(RENT_FEE*100)) end  RENT_FEE,  PR_R_01  MB_MIX_FLAG,  case WHEN CHR_46 is null then KD_CUST_TYPE  end MB_CUST_TYPE,case when ONLINE_DUR_LVL is null then 0 else ONLINE_DUR_LVL end  MB_ONLINE_DUR,  JIAZHI_FLAG   MB_VALUE_LEVEL,  PR_B_05  MB_PRODUCT_TYPE, CH_C_03  MB_NETIN_CHANNEL, ACTIVITY_TYPE  MB_AGREEMENT_TYPE, REPLACE(REPLACE(CHR_14,'\','/'),'"','')  MB_AGREEMENT_NAME, CHR_15 MB_AGREEMENT_BEGIN_TIME, EXP_DATE  MB_AGREEMENT_END_TIME, NUM_1   MB_AGREEMENT_REST_MONTHS, REPLACE(REPLACE(ba_n_11,'\\','/'),'\"','')  CONTACT_TELEPHONE_NO, SERVICE_TYPE MB_NET_TYPE,  case when LAST3_FLUX is null then 0 when ONLINE_DUR_LVL=0 or ONLINE_DUR_LVL is null then LAST3_FLUX when ONLINE_DUR_LVL>=3 then ROUND(LAST3_FLUX/3,0) when  ONLINE_DUR_LVL<3 then ROUND(LAST3_FLUX/ONLINE_DUR_LVL,0) end  MB_AVERAGE_FLOW,  TE_N_02  MB_TERMINAL_BRAND,  TE_N_04 MB_NET_MODE,  KD_DQ_MONTH KD_AGREEMENT_END_TIME,  KD_IS_KDDQXQ  KD_CONTINUE_AGREEMENT_FLAG,  KD_SERVICE_SUB_TYPE  KD_SUB_PACKAGE_NAME,  KD_COMB_SUB_TYPE  KD_MIX_TYPE,  KD_COMB_START_DATE  KD_MIX_BEGIN_TIME,  KD_MOBILE_NUMBER1  KD_MIX_MOBILE_NO,  KD_COMB_FX_NUMBER  KD_MIX_FIX_NO,  KD_BILLING_BANDWIDTH  KD_CUR_RATE,  case when KD_INNET_LENGTH is null then 0 else KD_INNET_LENGTH end  KD_NETIN_MONTHS ,  REPLACE(REPLACE(KD_MANAGER_NAME,'\','/'),'"','')  KD_OWNER_AREA, REPLACE(REPLACE(KD_ADDR_SIX_NAME,'\','/'),'"','')  KD_ADDR_SIX_NAME, CH_C_03 KD_CHANNEL_ID,  KD_XQ_DATE  KD_CONTINUE_AGREEMENT_TIME,  REPLACE(REPLACE(KD_MANAGER_NAME,'\','/'),'"','')  KD_CUST_MANAGER, IS_SIM  MB_SIM_TYPE,  TE_N_04  MB_CHG_MACHINE_FLAG,  BA_S_04 REAL_NAME_FLAG,  CHR_17  MB_ADDCARD_TYPE,  TYPE_HYBL_CUST_TYPE  MB_4G_NET_FLAG,CHR_30,  PAY_MODE,PRODUCT_BASE_CLASS,ACTIVITY_TYPE,WENDING_FLAG,case when ACCT_FEE is null then '0' else to_char(round(ACCT_FEE*100)) end ACCT_FEE,CO_B_01,C_100,PRODUCT_CLASS FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  DATE_ID ='DDDDDATEID'   AND PROV_ID = 'PPPPPROV_ID'  ";
//			BusiToolsIns.XcloudToMysql(sqlData, TenantId, MysqlTableName, MysqlCols);
		//	log.info("从行云出库begin");
			///*
	//		try{
	//		jdbcTemplate.execute("/*!mycat:sql=SELECT  max(date_id) DATE_ID FROM DIM_KFPT_BAND_DATE */"
	//				+ "EXPORT select is_sim,USER_ID from UNICOM_D_MB_DS_ALL_LABEL_INFO where DEVICE_NUMBER  IN"
	//				+"('15538337157','15538216073','13298177239','13253584580','13140144999') "
	//				+ "ATTRIBUTE(LOCATION('HDFS:/files/prov/076/dataasyn/test.csv') SEPARATOR('0x09'))");
	//		}catch(Exception e){
	//			e.printStackTrace();
	//		}
		//	*/
			/*
			log.info("从行云出库end");
			log.info("查询ORACLEbegin");
			jdbcTemplate.queryForList("select count(*) from user_tool_weights_conditions");
			log.info("查询ORACLEend");
			log.info("查询MYSQLbegin");
			jdbcTemplate.queryForList("select count(*) from TENANT_INFO");
			log.info("查询MYSQLend");
			*/
			/*   更新工单用户信息不需要了
			UpdateOrderUserInfo		UpdateOrderUserInfoIns = new UpdateOrderUserInfo();
			ParallelManage ParallelManageIns = new ParallelManage(UpdateOrderUserInfoIns);
			ParallelManageIns.execute();
			*/
			//UpdateOrderUserInfoIns.handleData(null);
			/*
			log.info("当前时间：{}"+new Date());
		//	log.info("休息10秒");
		//	Thread.sleep(10000);
			jdbcTemplate.execute("TRUNCATE TABLE DD_MYSQL");
		//	log.info("休息10秒");
		//	Thread.sleep(10000);
			List<Map<String, Object>> listSqlData = jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL");
			//log.info("休息10秒");
		//	Thread.sleep(10000);
			if(listSqlData != null){
				for(Map<String, Object> item:listSqlData){
				log.info(item.toString());
				}
			}
			jdbcTemplate.execute("INSERT INTO DD_MYSQL VALUES('13701091002')");
		//	log.info("休息30秒");
		//	Thread.sleep(30000);
			listSqlData = jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL");
			if(listSqlData != null){
				for(Map<String, Object> item:listSqlData){
				log.info(item.toString());
				}
			}
			jdbcTemplate.execute("INSERT INTO DD_MYSQL VALUES('13701091002')");
			*/
		//	log.info("休息180秒");
		//	Thread.sleep(180000);
			/*
			listSqlData = jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL");
			if(listSqlData != null){
				for(Map<String, Object> item:listSqlData){
				log.info(item.toString());
				}
			}
			jdbcTemplate.execute("INSERT INTO DD_MYSQL VALUES('13701091002')");
			log.info("休息400秒");
			Thread.sleep(400000);
			listSqlData = jdbcTemplate.queryForList("SELECT * FROM DD_MYSQL");
			if(listSqlData != null){
				for(Map<String, Object> item:listSqlData){
				log.info(item.toString());
				}
			}
			*/
				/*
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("结束:{}",new Date());
		*/
		return 0;
	}

	
	/*
	 * 得到工单数量 
	 */
	//@Transactional
	public		int		getOrderNum(){
		String partFlag = BusiToolsIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");

		Date			beginDate = new Date();
		Map<String, Object>	mapPara = new HashMap<String, Object>();
		StringBuilder		sbTmp = new StringBuilder();
		sbTmp.append("SELECT min(o.REC_ID) MINID,MAX(o.REC_ID) MAXID FROM PLT_ACTIVITY_INFO a,");
		sbTmp.append("PLT_ORDER_INFO o WHERE   1 = 1   AND a.TENANT_ID = 'uni076'    AND  ");
		sbTmp.append("o.TENANT_ID = 'uni076'   AND o.CHANNEL_ID = '5'   AND o.SERVICE_TYPE = '0'  ");
		sbTmp.append("  AND a.REC_ID = 61  AND o.ORDER_STATUS = '5'     AND o.ORG_PATH LIKE '/root%'  ");
		sbTmp.append("AND o.ACTIVITY_SEQ_ID = a.REC_ID    AND o.CONTACT_CODE NOT IN (101, 102, 103, 121) ");
		sbTmp.append("AND o.CHANNEL_STATUS = '0' ");
		mapPara.put("FIRSTSQL", sbTmp.toString());
		mapPara.put("KEYID", "o.REC_ID");
		sbTmp.setLength(0);
		sbTmp.append("SELECT COUNT(*) RECCOUNT FROM PLT_ACTIVITY_INFO a,PLT_USER_LABEL u,");
		sbTmp.append("PLT_ORDER_INFO o WHERE   1 = 1   AND a.TENANT_ID = 'uni076'    AND  ");
		sbTmp.append("o.TENANT_ID = 'uni076'   AND o.CHANNEL_ID = '5'   AND o.SERVICE_TYPE = '0'  ");
		sbTmp.append("  AND a.REC_ID = 61  AND o.ORDER_STATUS = '5'     AND o.ORG_PATH LIKE '/root%'  ");
		sbTmp.append("AND o.ACTIVITY_SEQ_ID = a.REC_ID    AND o.CONTACT_CODE NOT IN (101, 102, 103, 121) ");
		sbTmp.append("AND o.CHANNEL_STATUS = '0' ");
		sbTmp.append(" AND u.TENANT_ID = 'uni076' AND PARTITION_FLAG ="+partFlag+" AND o.USER_ID = u.USER_ID ");
		mapPara.put("SECONDSQL", sbTmp.toString());
		InnerDbQuerySrv.query(mapPara);
		List<Map<String, Object>> listSqlData=(List<Map<String, Object>>)mapPara.get("DATA");
		long		lTotalNum = 0;
		for(Map<String, Object> item:listSqlData){
			log.info(item.toString());
			lTotalNum += (long) item.get("RECCOUNT");
		}
		Date			dateEnd = new Date();
		log.info("totalnum={},时间：{}",lTotalNum,dateEnd.getTime() - beginDate.getTime());
		
		return (int)lTotalNum;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.bonc.busi.task.service.BaseTaskSrv#testDbConnection()
	 */
	public		int		testDbConnection( String dbtype,String timeDur){
		Date				begin = new Date();
		Connection connection = null;
		Statement statement =null;
		try{
			//Class.forName(getValueFromGlobal("DS.MYSQL.DRIVER"));
			
			 try{   
				    //加载MySql的驱动类   
				 if("mysql".equalsIgnoreCase(dbtype)){
				    Class.forName("com.mysql.jdbc.Driver") ;   
				 }
				 else  if("oracle".equalsIgnoreCase(dbtype)){
					 Class.forName("oracle.jdbc.OracleDriver") ;  
				 }
				    }catch(ClassNotFoundException e){   
				    System.out.println("找不到驱动程序类 ，加载驱动失败！");   
				    e.printStackTrace() ;   
				    return -1;
				    }   
				    
			 if("mysql".equalsIgnoreCase(dbtype)){
				 connection = DriverManager.getConnection("jdbc:mysql://10.162.2.119:31699/henan0?&useSSL=false" , "orderrun",
					"CLYXV3_HN_123");
				 statement = connection.createStatement();
					statement.execute("insert into dd values('1370109220')");
			 }
			 else  if("oracle".equalsIgnoreCase(dbtype)){
				 connection = DriverManager.getConnection("jdbc:oracle:thin:@//132.35.224.165:1521/dwtest" , "clyxv3_hn",
							"CLYXV3_HN_123");
				 statement = connection.createStatement();
					statement.execute("insert into dd values('1370109220')");
			 }
				
            log.info(" before  sleep:dbtype:{},time:{}",dbtype,timeDur);
			Thread.sleep(Integer.parseInt(timeDur));
			  log.info(" after  sleep:dbtype:{},time:{}",dbtype,timeDur);
				statement.execute("insert into dd values('1370109220')");
				log.info("  成功执行  !!!!!!!!!!!!!!!!!!!!!!!!!1");
		}catch(Exception e){
		
			e.printStackTrace();
			return -1;
		}finally
		{
			if(statement!=null)
			{
				try{
					statement.close();
					connection.close();
				}catch(Exception e){}
			}
		}	

		return 0;
	}
}
