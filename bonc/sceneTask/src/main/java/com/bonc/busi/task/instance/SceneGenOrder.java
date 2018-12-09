package com.bonc.busi.task.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.ParallelFunc;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.SceneMapper;
import com.bonc.utils.DateUtil;
import com.bonc.utils.DateUtil.DateFomart;

public class SceneGenOrder extends ParallelFunc{
	private static final Logger logger = Logger.getLogger(SceneGenOrder.class);
	private static int POLLNUM = 0;
	private static long start = 0;
	private static final int MESSAGENUM = 1000;
	
	private ConsumerConfig config = SpringUtil.getBean(ConsumerConfig.class);
	private SceneMapper mapper = SpringUtil.getBean(SceneMapper.class);
	private BusiTools  AsynDataIns = SpringUtil.getBean(BusiTools.class);
	
	private	PltCommonLog log = new PltCommonLog();
	private ConsumerConnector consumer = null;
	private KafkaStream<byte[], byte[]> stream = null;
	private ConsumerIterator<byte[], byte[]> iterator = null;
	private Date dateBegin = null;

	
	private static String today = null;
	
	@Override
	public	int	begin(){
		
		/*String topicName = AsynDataIns.getValueFromGlobal("KAFKA_TOPIC_NAME");*/
		
		//测试
		String topicName = "sixthtopic";
		
		dateBegin = new Date();
		// --- 得到序列号 ---
		int SerialId = AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		log.setLOG_TYPE("72");
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
			logger.info("------------客户端初始化完毕-----------");
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
				logger.info("----------------处理第"+POLLNUM+"条信息完成-----------------");
				logger.info("----------------到此共用"+(System.currentTimeMillis()-start)+"毫秒-----------------");
				
				if(POLLNUM==MESSAGENUM){
					long end = System.currentTimeMillis();
					long interval = end - start;
					logger.info("---------------处理"+MESSAGENUM+"条数据共用"+interval+"毫秒-------------------");
				}
			}// --- while ---
		//--iCurHandleThreadNum;
		}finally{
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
					}
					buffer.put(obj);
					logger.info("----------------拉取第"+POLLNUM+"条信息成功-----------------");
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
			logger.info("----------------开始拉取第"+(++POLLNUM)+"条信息-----------------");
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
			while (true) {
				String message = new String(iterator.next().message());
				if(POLLNUM==1){
					start = System.currentTimeMillis();
					logger.info("-----------------计时开始-------------------");
				}
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
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		for(String activityId:orderMap.keySet()){
			List<HashMap<String, Object>> orders = orderMap.get(activityId);
			String[] tanActivity = activityId.split(",");
			HashMap<String, Object> activityInfo = mapper.getActivityInfo(tanActivity[0],tanActivity[1]);
			if(activityInfo==null){
				continue;
			}
			String TABLE_NAME = "2".equals(activityInfo.get("ACTIVITY_STATUS").toString())?"PLT_ORDER_INFO_SCENEMARKET_HIS":"PLT_ORDER_INFO_SCENEMARKET";
			StringBuilder insertSql = new StringBuilder("INSERT INTO "+TABLE_NAME+" (ACTIVITY_SEQ_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,PHONE_NUMBER,BEGIN_DATE,CONTACT_CODE,AREAID,ORDER_STATUS,RESERVE1,RESERVE2,RESERVE3,RESERVE4 ) VALUES ");
			StringBuilder updateSql = new StringBuilder("UPDATE "+TABLE_NAME+" o,PLT_USER_LABEL u SET o.USER_ID=u.USER_ID WHERE ");
			updateSql.append(" o.TENANT_ID='").append(tanActivity[0]).append("' AND u.TENANT_ID='").append(tanActivity[0]).append("' AND o.PHONE_NUMBER IN (");
			StringBuilder phoneNums = new StringBuilder();
			for(HashMap<String, Object> order:orders){
				String eventType = order.get("eventType")+"";
				String[] event=eventType.split("\\|");
//				String userId  = mapper.getUserId(order.get("telPhone")+"",partFlag,order.get("tenantId")+"");
				insertSql.append("(").append(activityInfo.get("REC_ID")).append(",'").append(order.get("channelType")).append("','").append(order.get("sendContent")).append("','").append(order.get("tenantId")).append("','").append(order.get("telPhone")).append("','").append(order.get("downKafkaTime")).append("','0','").append(order.get("areaId")).append("','5','").append(event[0]).append("','").append(event[1]).append("','").append(order.get("uniqueId")).append("', NOW() ),");
				phoneNums.append("'").append(order.get("telPhone")).append("',");
			}
			updateSql.append(phoneNums.substring(0,phoneNums.length()-1)).append(") AND o.PHONE_NUMBER=u.DEVICE_NUMBER AND u.PARTITION_FLAG=").append(partFlag).append(" AND u.USER_TYPE = '0' AND o.USER_ID IS NULL ");
			mapper.executeScene(insertSql.substring(0,insertSql.length()-1));
			mapper.executeScene(updateSql.toString());
//			AsynDataIns.executeDdlOnMysql(insertSql.substring(0,insertSql.length()-1), tanActivity[0]);
//			AsynDataIns.executeDdlOnMysql(updateSql.toString(), tanActivity[0]);
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
