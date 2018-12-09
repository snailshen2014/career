package com.bonc.busi.task.instance;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import com.bonc.busi.task.base.*;
import com.bonc.utils.HttpUtil;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang.*;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.SceneMapper;
import com.bonc.utils.DateUtil;
import com.bonc.utils.DateUtil.DateFomart;
import org.springframework.context.annotation.Bean;

public class SceneGenOrder extends ParallelFunc{
	private static final Logger logger = Logger.getLogger(SceneGenOrder.class);
	
	private ConsumerConfig config = SpringUtil.getBean(ConsumerConfig.class);
	private SceneMapper mapper = SpringUtil.getBean(SceneMapper.class);
	private BusiTools  AsynDataIns = SpringUtil.getBean(BusiTools.class);
	
	private	PltCommonLog log = new PltCommonLog();
	private ConsumerConnector consumer = null;
	private KafkaStream<byte[], byte[]> stream = null;
	private ConsumerIterator<byte[], byte[]> iterator = null;
	private Date dateBegin = null;
	private String tenantId = null;

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	private static String today = null;



	@Override
	public	int	begin(){
		String topicName = AsynDataIns.getValueFromGlobal("KAFKA_TOPIC_NAME");
		//根据多租户拼接topic
		topicName +=   "_" +this.tenantId;
		dateBegin = new Date();
		// --- 得到序列号 ---
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		log.setLOG_TYPE("72");
		log.setTENANT_ID(this.tenantId);
		log.setSERIAL_ID(SerialId);
		log.setSTART_TIME(new Date());
		log.setSPONSOR("SceneGenOrder");
		log.setBUSI_CODE("BEGIN");
		log.setBUSI_DESC("start scene gen order");
		log.setBUSI_ITEM_1("topicName="+topicName);
		AsynDataIns.insertPltCommonLog(log);
		try{
			consumer = Consumer.createJavaConsumerConnector(config);
			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			topicCountMap.put(topicName, 1); // 一次从主题中获取一个数据
			Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
			stream = messageStreams.get(topicName).get(0);// 获取每次接收到的这个数据
			iterator = stream.iterator();

			log.setSTART_TIME(new Date());
			log.setBUSI_CODE("INIT CONFIG");
			log.setBUSI_DESC("success init kafka config");
			log.setBUSI_ITEM_1("topicName="+topicName);
			AsynDataIns.insertPltCommonLog(log);
		}catch (NoSuchElementException e) {
			e.printStackTrace();
			log.setSTART_TIME(new Date());
			log.setBUSI_CODE("EXCEPTION NoSuchElementException");
			log.setBUSI_DESC("exception for NoSuchElementException");
			log.setBUSI_ITEM_1("topicName="+topicName);
			AsynDataIns.insertPltCommonLog(log);
			return -1;
		}catch (Exception e) {
			e.printStackTrace();
			log.setSTART_TIME(new Date());
			log.setBUSI_CODE("EXCEPTION NoSuchElementException");
			log.setBUSI_DESC("exception for NoSuchElementException");
			log.setBUSI_ITEM_1("topicName="+topicName);
			AsynDataIns.insertPltCommonLog(log);
			return -1;
		}
		return 0;
	}
	
	
	/*
	 * 结束方法
	 */
	@Override
	public int end(){
		//shutdown consumer
		if(null!=consumer)  consumer.shutdown();
		
		Date dateEnd = new Date();
		logger.info("--- scene gen order exit! "+(dateEnd.getTime() - dateBegin.getTime())/1000.0+"s");
		log.setSTART_TIME(new Date());
		log.setBUSI_CODE("END");
		log.setBUSI_DESC("exit scene gen order");
		log.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
		AsynDataIns.insertPltCommonLog(log);
		return 0;
	}
	public static void main(String[] args) {
		int i=10;
		while (i-->0) {
			System.out.println(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public	void handleData(){
		System.out.println(" 线程启动:"+Thread.currentThread().getName());
		try{
			
			//按租户 活动ID 分组
			HashMap<String, List<HashMap<String, Object>>> orderMap = null;
			//场景营销的活动
			while(1 > 0){
//				Thread.sleep(60000); //-- 场景营销每1分钟执行一次
				int size = iMaxQueueSize/10;
				orderMap=new HashMap<String, List<HashMap<String,Object>>>();
				while (size-->0) {
					
					HashMap<String, Object> order = (HashMap<String, Object>)buffer.poll();
					if(null==order){
						break;
					}
					//将制定的  工单按活动分组
					if(orderMap.get(order.get("tenantId")+","+order.get("externalId"))==null){
						List<HashMap<String, Object>> orders = new ArrayList<HashMap<String,Object>>();
						orders.add(order);
						orderMap.put(order.get("tenantId")+","+order.get("externalId"),orders);
					}else{
						orderMap.get(order.get("tenantId")+","+order.get("externalId")).add(order);
					}
				}

				if(orderMap.isEmpty()){    // --- 没有取到数据 ---
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}   // --- 休息5秒钟 ---
					continue;
				}else{
					handle(orderMap);
				}
				
			}// --- while ---
		//--iCurHandleThreadNum;
		}  finally{
			minusCurHandleThreadNum();        // --- 确何任何情况下都可以减去一
			System.out.println(" 退出线程:"+Thread.currentThread().getName());
		}
	}
	
	@Override
	public	void		getData(){
		cGetThreadState = '1';					// --- 取数据运行中  ---
		try{
			while(1 > 0){
				try{
					if(buffer.size() >= iMaxQueueSize){  // --- 超过最大队列数 ---
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}   // --- 休息10秒钟 ---
						continue;
					}
					Object obj = get();
					if(null==obj){
						Thread.sleep(10000);
						continue;
//						break;
					}
					buffer.put(obj);
				}catch(Exception e){
					logger.info("get scene exception! "+e.getMessage());
				}
			}// --- while ---
		}finally{
			logger.info("get scene exit!");
			cGetThreadState = '9';   // --- 确保中途出错也置此状态 ---
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object get(){
		Date dateEnd= new Date();
		try{
			//如果日期增加一天 添加一条获取的日志
			String curDay=DateUtil.CurrentDate.currentDateFomart(DateFomart.DATE);
			if(today==null||!curDay.equals(today)){
				logger.info("--- scene gen order running! ");
				log.setSTART_TIME(new Date());
				log.setBUSI_CODE("RUNNING");
				log.setBUSI_DESC("scene gen order running");
				log.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
				AsynDataIns.insertPltCommonLog(log);
				today=curDay;
			}
			//即使场景营销工单定时拉3小时
//			long beginTime = System.currentTimeMillis();
//			long overTime =180 * 60 * 1000L;
			while (true) {
//				if((System.currentTimeMillis() - beginTime) > overTime){
//					logger.info("此次场景营销执行时间已到,执行时间为：{}"+(System.currentTimeMillis() - beginTime));
//					break;
//				}
//				if (!iterator.hasNext()){
//					logger.info("此次场景营销数据已全部拉出 ");
//					break;
//				}
				String message = new String(iterator.next().message());
				HashMap<String, Object> order = JSON.parseObject(message,HashMap.class);
				
				if(null==order){
					logger.info("scene get null order!");
					continue;
				}
				
				//only get scene order
//				if(!"3".equals(order.get("smsResource"))){
//					continue;
//				}
				return order;
			}
		}catch(NoSuchElementException e){
			logger.info("--- scene gen order exit! "+(dateEnd.getTime() - dateBegin.getTime())/1000.0+"s");
			log.setSTART_TIME(new Date());
			log.setBUSI_CODE("END");
			log.setBUSI_DESC("exit scene gen order");
			log.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
			AsynDataIns.insertPltCommonLog(log);
		}catch (Exception e){
			logger.info("--- scene gen exception exit! "+(dateEnd.getTime() - dateBegin.getTime())/1000.0+"s");
			log.setSTART_TIME(new Date());
			log.setBUSI_CODE("END");
			log.setBUSI_DESC("exception exit scene gen order");
			log.setDURATION((int)(dateEnd.getTime() - dateBegin.getTime()));
			AsynDataIns.insertPltCommonLog(log);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int handle(Object data){
		HashMap<String, List<HashMap<String, Object>>> orderMap = (HashMap<String, List<HashMap<String, Object>>>)data;
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION."+tenantId);
		for(String activityId:orderMap.keySet()){
			List<HashMap<String, Object>> orders = orderMap.get(activityId);
			String[] tanActivity = activityId.split(",");
			String mycatSql  = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = "+tanActivity[0]+"  */";
			HashMap<String, Object> activityInfo = mapper.getActivityInfo(tanActivity[0],tanActivity[1]);
			if(activityInfo==null){
				continue;
			}
			//是否为历史表 type
			String busiType = "2".equals(activityInfo.get("ACTIVITY_STATUS").toString())?"1":"3";
			//调用接口获取表名
			String orderNameUrl = AsynDataIns.getValueFromGlobal("GENORDER_ASSIGNED_URL");
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("busiType",busiType);
			params.put("activityId",tanActivity[1]);
			params.put("activitySeqId",activityInfo.get("REC_ID"));
			params.put("rows",orders.size());
			//因为场景营销目前只有短信渠道暂且按统一渠道处理 ！！！！！！！！！！！！
			//TODO
			params.put("channelId", orders.get(0).get("channelType"));
			params.put("tenantId",tenantId);
			// 查询映射关系
			String TABLE_NAME = mapper.queryOrderTable(params);
			if (org.apache.commons.lang.StringUtils.isBlank(TABLE_NAME)){
				 TABLE_NAME = HttpUtil.doPost(orderNameUrl, params);
			}
			// 新老工单表对应areaid对应area_no  marketing_words对应BUSINESS_RESERVE5
			StringBuilder insertSql = new StringBuilder("INSERT INTO "+TABLE_NAME+" (ACTIVITY_SEQ_ID,CHANNEL_ID,BUSINESS_RESERVE5,TENANT_ID,PHONE_NUMBER,BEGIN_DATE,CONTACT_CODE,AREA_NO,ORDER_STATUS,BUSINESS_RESERVE1,BUSINESS_RESERVE2,BUSINESS_RESERVE3,BUSINESS_RESERVE4 ) VALUES ");
			StringBuilder updateSql = new StringBuilder("UPDATE "+TABLE_NAME+" o,PLT_USER_LABEL_"+partFlag+" u SET o.USER_ID=u.USER_ID WHERE ");
			updateSql.append(" o.TENANT_ID='").append(tanActivity[0]).append("' AND u.TENANT_ID='").append(tanActivity[0]).append("' AND o.PHONE_NUMBER IN (");
			StringBuilder phoneNums = new StringBuilder();
			for(HashMap<String, Object> order:orders){
				String eventType = order.get("eventType")+"";
				String[] event=eventType.split("\\|");
//				String userId  = mapper.getUserId(order.get("telPhone")+"",partFlag,order.get("tenantId")+"");
				insertSql.append("(").append(activityInfo.get("REC_ID")).append(",'").append(order.get("channelType")).append("','").append(order.get("sendContent")).append("','").append(order.get("tenantId")).append("','").append(order.get("telPhone")).append("','").append(order.get("downKafkaTime")).append("','0','").append(order.get("areaId")).append("','5','").append(event[0]).append("','").append(event[1]).append("','").append(order.get("uniqueId")).append("', NOW() ),");
				phoneNums.append("'").append(order.get("telPhone")).append("',");
			}
			updateSql.append(phoneNums.substring(0,phoneNums.length()-1)).append(") AND o.PHONE_NUMBER=u.DEVICE_NUMBER ").append(" AND u.DATA_TYPE = '0' AND o.USER_ID IS NULL ");
//			AsynDataIns.executeDdlOnMysql(insertSql.substring(0,insertSql.length()-1), tanActivity[0]);
//			AsynDataIns.executeDdlOnMysql(updateSql.toString(), tanActivity[0]);

            mapper.executeScene(insertSql.substring(0, insertSql.length() - 1),mycatSql);
            mapper.executeScene(updateSql.toString(),mycatSql);
//			try {
//				logger.info(insertSql.substring(0,insertSql.length()-1));
//				logger.info(updateSql.toString());
//				Thread.sleep(orders.size()*2);
//				logger.info("execute insert and update scene order!");
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		return 0;
	}
}
