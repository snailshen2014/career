package com.bonc.busi.task.base;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.instance.KafkaConsumerThread;
/*import com.bonc.busi.task.test.TestShareData;*/
import com.bonc.busi.task.mapper.SceneMapper;

public class SceneManage {
	private final static Logger logger = LoggerFactory.getLogger(SceneManage.class);
	private PltCommonLog log = new PltCommonLog();
	
	private ExecutorService pool = Global.getExecutorService();// --- 得到线程池变量
	private	int	maxRemaindMinutes = 1440;   // --- 缺省时间180分钟 ---
	
	// 工具类
	private SceneMapper mapper = SpringUtil.getBean(SceneMapper.class);
	private BusiTools AsynDataIns = SpringUtil.getBean(BusiTools.class);
	
	// --- 构造函数 ---
	public SceneManage() {
	}

	public int getMaxRemaindMinutes() {
		return maxRemaindMinutes;
	}

	public void setMaxRemaindMinutes(int maxRemaindMinutes) {
		this.maxRemaindMinutes = maxRemaindMinutes;
	}
	
	/*
	 * 执行方法
	 */
	public void execute() {
		KafkaConsumerThread consumer = null;
		/*TestShareData t = new TestShareData();*/
		try {
			// 创建1个消费者客户端线程拉取数据到数据库
			/*consumer = new KafkaConsumerThread(t);*/
			consumer = new KafkaConsumerThread();
			pool.execute(consumer);;
		} catch (Exception e) {
			logger.info("拉取信息---异常结束");
			// --- 将SQL异常往上抛，其它异常本地截获 ---
			if (e.getCause() instanceof SQLException) {
				logger.info("sql error happend:{}", e.getMessage());
				throw e;
			} else {
				e.printStackTrace();
			}
		}
		// 侦听业务处理线程遍历KafkaConsumerThread
		logger.info(
				"-------线程状态:0.刚开启线程 1.连接上kafka 2.拉取数据中 3.数据入库中 4.处理超时中 9.线程死亡,连接断开;线程处理数据:[租户id,活动id=处理总量]-------");
		int startTimeMinutes = 0;
		while (1 > 0) {
			// 获取线程状态
			String busiCode = "";
			String busiDesc = "";
			String busiItem = "";
			String dataNum = "";
			Set<Entry<String, Long>> set = consumer.getHandleDataNumMap().entrySet();
			//定时清理存数据量的map集合
			if(startTimeMinutes>=maxRemaindMinutes){
				startTimeMinutes = 0;
				synchronized (consumer.getHandleDataNumMap()) {
					Iterator<Entry<String, Long>> it = set.iterator();
					while(it.hasNext()){
						Entry<String, Long> entry = it.next();
						String[] tanActivity = entry.getKey().split(",");
						if(mapper.getActivityInfo(tanActivity[0], tanActivity[1]).get("ORI_STATE") != "9"){
							it.remove();
						}
					}
				}
			}
			// 不入库的日志
			if (set == null) {
				dataNum = "NULL";
			} else {
				dataNum = set.toString();
			}
			logger.info(consumer.getThreadName() + ":线程状态:{};处理数据量:{}", consumer.getPollThreadState(), dataNum);
			// 入库的日志
			switch (consumer.getPollThreadState()) {
			case '0':
				busiCode = "START";
				busiDesc = "线程创建成功";
				break;
			case '1':
				busiCode = "CONNECT";
				busiDesc = "连接kafka成功";
				break;
			case '2':
				busiCode = "POLL";
				busiDesc = "拉取数据中";
				break;
			case '3':
				busiCode = "INTOSTORAGE";
				busiDesc = "数据入库中";
				break;
			case '4':
				busiCode = "TIMEOUT";
				busiDesc = "从kafka获取数据超时";
				break;
			case '9':
				busiCode = "END";
				busiDesc = "线程死亡，关闭连接";
				break;
			default:
				break;
			}
			busiItem = consumer.getThreadName() + ":" + dataNum;
			// 日志入库
			log.setSTART_TIME(new Date());
			log.setLOG_TYPE("73");
			log.setSPONSOR("yangjingxiong");
			log.setBUSI_CODE(busiCode);
			log.setBUSI_DESC(busiDesc);
			log.setBUSI_ITEM_1(busiItem);
			AsynDataIns.insertPltCommonLog(log);
			// 重启死亡线程
			if (consumer.getPollThreadState() == '9') {
				logger.info("---重启死亡线程---");
				/*consumer = new KafkaConsumerThread(t);*/
				consumer = new KafkaConsumerThread();
				pool.execute(consumer);
			}
			try {
				startTimeMinutes += 2;
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
