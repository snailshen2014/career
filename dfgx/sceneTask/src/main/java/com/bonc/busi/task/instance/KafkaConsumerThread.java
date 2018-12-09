package com.bonc.busi.task.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.mapper.SceneMapper;
import com.bonc.busi.task.test.TestShareData;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumerThread implements Runnable {
	// 日志
	private static final Logger logger = Logger.getLogger(KafkaConsumerThread.class);
	// private PltCommonLog log = new PltCommonLog();
	private Date dateBegin = null;
	// 工具类
	private SceneMapper mapper = SpringUtil.getBean(SceneMapper.class);
	private BusiTools AsynDataIns = SpringUtil.getBean(BusiTools.class);
	// 测试,监控线程用
	private TestShareData testData = null;// 测试
	private String threadName = null;
	private Character pollThreadState = null;// 线程状态
	private Map<String, Long> handleDataNumMap = null;// 线程处理数据量(key:tenantid,activityid;value:datanum)
	// 初始化用
	private String topicName = null;
	private ConsumerConfig config = SpringUtil.getBean(ConsumerConfig.class);
	private ConsumerConnector consumer = null;
	private KafkaStream<byte[], byte[]> stream = null;
	private ConsumerIterator<byte[], byte[]> iterator = null;
	// 处理数据用
	private List<HashMap<String, Object>> clist = null;
	private int clistMaxNum = 400;

	public KafkaConsumerThread() {
	}

	public KafkaConsumerThread(TestShareData testData) {
		this.testData = testData;
	}

	@Override
	public void run() {
		// 初始化线程
		init();
		// 创建消费者连接
		connectToKafka();
		// 拉取数据到数据库
		pollDataTODB();
	}

	/**
	 * 初始化线程
	 */
	public void init() {
		// 初始化线程处理状态
		if (null == pollThreadState) {
			setPollThreadState('0');
		}
		// 初始化线程处理数据量
		if (null == getHandleDataNumMap()) {
			setHandleDataNumMap(new HashMap<String, Long>());
		}
		// 初始化线程名
		setThreadName(Thread.currentThread().getName());
		logger.info(threadName + ":线程启动");
	}

	/**
	 * 创建消费者连接
	 */
	public void connectToKafka() {
		logger.info("------------客户端初始化完毕开始-----------");
		topicName = AsynDataIns.getValueFromGlobal("KAFKA_TOPIC_NAME");
		// 测试
//		topicName = "sixthtopic";
		// 创建连接
		try {
			consumer = Consumer.createJavaConsumerConnector(config);
			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			topicCountMap.put(topicName, 1); // 一次从主题中获取一个数据
			Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer
					.createMessageStreams(topicCountMap);

			stream = messageStreams.get(topicName).get(0);// 获取每次接收到的这个数据
			dateBegin = new Date();
			logger.info("------------客户端初始化完毕-----------");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("exception for NoSuchElementException");
		}
	}

	/**
	 * 拉取数据
	 */
	@SuppressWarnings("unchecked")
	public void pollDataTODB() {
		try {
			logger.info("---------------进入拉取数据循环---------------");
			clist = new ArrayList<HashMap<String, Object>>();
			while (1 > 0) {
				Date dateEnd = new Date();
				try {
					setPollThreadState('2');// --- 取数据运行中 ---
					iterator = stream.iterator();
					while (iterator.hasNext()) {
						/*
						 * logger.info("----------------" + threadName +
						 * "：开始拉取第" + (testData.getPollnum() + 1) +
						 * "条信息-----------------");
						 */
						logger.info("-------开始拉取数据-------");
						String message = new String(iterator.next().message());
						logger.info("-------拉取数据成功-------");
						/*
						 * testData.setPollnum(testData.getPollnum() + 1);
						 * logger.info("----------------" + threadName + "：拉取第"
						 * + testData.getPollnum() + "条信息成功-----------------");
						 */
						HashMap<String, Object> order = JSON.parseObject(message, HashMap.class);
						clist.add(order);
						// 批量处理数据
						if (clist.size() >= clistMaxNum) {
							handleData();
						}
					}
				} catch (ConsumerTimeoutException e) {// 拉取数据超时，先将不足数量的数据先入库再重新拉取
					setPollThreadState('4');// --- 处理超时中 ---
					/*
					 * Socket client = null; try { client = new
					 * Socket("172.16.11.167", 2191); client = new
					 * Socket("172.16.11.167", 2192); client = new
					 * Socket("172.16.11.167", 2193); client.close(); } catch
					 * (Exception e2) { logger.info("zookeeper关闭"); try {
					 * Thread.sleep(12000); } catch (InterruptedException e1) {
					 * e1.printStackTrace(); } } try { client = new
					 * Socket("172.16.11.167", 9093); client = new
					 * Socket("172.16.11.167", 9094); client = new
					 * Socket("172.16.11.167", 9095); client.close(); } catch
					 * (Exception e2) { logger.info("kafka关闭"); try {
					 * Thread.sleep(12000); } catch (InterruptedException e1) {
					 * e1.printStackTrace(); } }
					 */
					logger.info("-------批量获取数据超时-------");
					if (clist != null && clist.size() != 0) {
						logger.info("---处理剩余数据---");
						handleData();
					} else {
						logger.info("---没有剩余数据---");
					}
					logger.info("---重新获取遍历器---");
				} catch (JSONException e) {
					logger.info("---参数格式有问题,入库失败 ---");
				} catch (NoSuchElementException e) {
					logger.info("---NoSuchElementException ---");
				} catch (Exception e) {
					e.printStackTrace();
					logger.info(
							"---gen exception exit! " + (dateEnd.getTime() - dateBegin.getTime()) / 1000.0 + "s---");
				}
			}
		} finally {
			end();
			pollThreadState = '9';// --- 线程死亡 ---
			logger.info("---scene gen exit!---");
		}
	}

	// 批量处理数据
	public void handleData() {
		logger.info("---" + clist.size() + "条数据入库中 ---");
		setPollThreadState('3');// --- 入库数据运行中 ---
		// 按租户 活动ID 分组
		HashMap<String, List<HashMap<String, Object>>> orderMap = new HashMap<String, List<HashMap<String, Object>>>();
		for (HashMap<String, Object> order : clist) {
			// 将制定的 工单按活动分组
			if (orderMap.get(order.get("tenantId") + "," + order.get("externalId")) == null) {
				List<HashMap<String, Object>> orders = new ArrayList<HashMap<String, Object>>();
				orders.add(order);
				orderMap.put(order.get("tenantId") + "," + order.get("externalId"), orders);
			} else {
				orderMap.get(order.get("tenantId") + "," + order.get("externalId")).add(order);
			}
		}
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		// 开始拼接sql语句入库
		for (String activityId : orderMap.keySet()) {
			logger.info("活动:" + activityId + "入库中");
			List<HashMap<String, Object>> orders = orderMap.get(activityId);
			String[] tanActivity = activityId.split(",");
			HashMap<String, Object> activityInfo = mapper.getActivityInfo(tanActivity[0], tanActivity[1]);
			if (activityInfo == null) {
				logger.info("---" + activityId + ":没有对应的活动，入库失败---");
				continue;
			}
			String TABLE_NAME = "2".equals(activityInfo.get("ACTIVITY_STATUS").toString())
					? "PLT_ORDER_INFO_SCENEMARKET_HIS" : "PLT_ORDER_INFO_SCENEMARKET";
			StringBuilder insertSql = new StringBuilder("INSERT INTO " + TABLE_NAME
					+ " (ACTIVITY_SEQ_ID,CHANNEL_ID,MARKETING_WORDS,TENANT_ID,PHONE_NUMBER,BEGIN_DATE,CONTACT_CODE,AREAID,ORDER_STATUS,RESERVE1,RESERVE2,RESERVE3,RESERVE4 ) VALUES ");
			StringBuilder updateSql = new StringBuilder(
					"UPDATE " + TABLE_NAME + " o,PLT_USER_LABEL u SET o.USER_ID=u.USER_ID WHERE ");
			updateSql.append(" o.TENANT_ID='").append(tanActivity[0]).append("' AND u.TENANT_ID='")
					.append(tanActivity[0]).append("' AND o.PHONE_NUMBER IN (");
			StringBuilder phoneNums = new StringBuilder();
			for (HashMap<String, Object> order : orders) {
				String eventType = order.get("eventType") + "";
				String[] event = eventType.split("\\|");
				insertSql.append("(").append(activityInfo.get("REC_ID")).append(",'").append(order.get("channelType"))
						.append("','").append(order.get("sendContent")).append("','").append(order.get("tenantId"))
						.append("','").append(order.get("telPhone")).append("','").append(order.get("downKafkaTime"))
						.append("','0','").append(order.get("areaId")).append("','5','").append(event[0]).append("','")
						.append(event[1]).append("','").append(order.get("uniqueId")).append("', NOW() ),");
				phoneNums.append("'").append(order.get("telPhone")).append("',");
			}
			updateSql.append(phoneNums.substring(0, phoneNums.length() - 1))
					.append(") AND o.PHONE_NUMBER=u.DEVICE_NUMBER AND u.PARTITION_FLAG=").append(partFlag)
					.append(" AND u.USER_TYPE = '0' AND o.USER_ID IS NULL ");
			mapper.executeScene(insertSql.substring(0, insertSql.length() - 1));
			mapper.executeScene(updateSql.toString());
			logger.info("活动:" + activityId + "入库成功");
			// 记录每个线程的处理数据总量
			if (null == getHandleDataNumMap().get(activityId)) {
				getHandleDataNumMap().put(activityId, 0L);
			}
			getHandleDataNumMap().put(activityId,
					getHandleDataNumMap().get(activityId) + orderMap.get(activityId).size());
		}
		// 测试，查看耗时
		/*
		 * testData.setHandleNUM(testData.getHandleNUM()+clist.size()); if
		 * (testData.getHandleNUM() == TestShareData.MESSAGENUM) {
		 * logger.info("----------拉取入库" + TestShareData.MESSAGENUM + "条数据共用" +
		 * (new Date().getTime() - dateBegin.getTime()) / 1000.0 +
		 * "s----------"); }
		 */
		clist.clear();
		logger.info("---数据入库结束 ---");
		logger.info("处理数据总量:" + getHandleDataNumMap().entrySet());

	}

	// 关闭客户端
	public int end() { // shutdown consumer
		if (null != consumer)
			consumer.shutdown();
		Date dateEnd = new Date();
		logger.info("--- scene gen order exit! " + (dateEnd.getTime() - dateBegin.getTime()) / 1000.0 + "s");
		return 0;
	}

	public synchronized Character getPollThreadState() {
		return pollThreadState;
	}

	public synchronized void setPollThreadState(Character pollThreadState) {
		this.pollThreadState = pollThreadState;
	}

	public synchronized String getThreadName() {
		return threadName;
	}

	public synchronized void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public synchronized Map<String, Long> getHandleDataNumMap() {
		return handleDataNumMap;
	}

	public synchronized void setHandleDataNumMap(Map<String, Long> handleDataNumMap) {
		this.handleDataNumMap = handleDataNumMap;
	}
}
