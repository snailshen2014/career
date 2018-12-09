package com.bonc.busi.send.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.bonc.busi.send.bo.PltChannelOrderList;
import com.bonc.busi.send.model.ChannelModel;
import com.bonc.busi.send.model.QueryRange;

public interface SendMapper {

	@UpdateProvider(method="updateOrderDo",type=SendUpdateGen.class)
	public void updateOrderDo(ChannelModel model);

	/**
	 * @已废弃 短信下发工单的时候需要考虑:活动状态、工单状态、下发工单状态、租户、渠道ID  这些信息
	 * @param rangePLT_ORDER_INFO_SMS
	 * @return
	 */
	@Select("select c.PHONE_NUMBER,c.ID,c.ORDER_CONTENT "
			+ " from PLT_ORDER_INFO o,PLT_ACTIVITY_INFO a,PLT_CHANNEL_ORDER_LIST c where"
			+ " c.ORDER_SERIAL_ID=o.REC_ID and c.ACTIVITY_SEQ_ID=a.REC_ID "
			+ " and o.ORDER_STATUS<>'6' and a.ACTIVITY_STATUS='1' and c.ORDER_STATUS='0' "
			+ " and c.CHANNEL_ID=#{channelId} and c.TENANT_ID=#{provId} "
			+ " order by c.ID limit #{size} ")
	public ArrayList<PltChannelOrderList> findOrderPage(QueryRange range);

	@UpdateProvider(method="updateDxOrder",type=SendUpdateGen.class)
	public Integer updateDxOrder(QueryRange range);

	/**
	 * 查询所有可发送活动序列号的ID
	 * @param provId
	 * @return
	 */
	@Select("SELECT ACTIVITY_SEQ_ID recId FROM PLT_ORDER_INFO WHERE TENANT_ID=#{provId} "
			+ " AND ORDER_STATUS='5' AND CHANNEL_STATUS=0 GROUP BY ACTIVITY_SEQ_ID ")
	public List<HashMap<String, Object>> findSendActivity(String provId);

	
	/**
	 * TODO 判断活动是否有效　查询一个渠道下的有效活动列表
	 * 周期性活动才控制开始时间和结束时间
	 * @param activity
	 * @return
	 *   AND (d.END_TIME>NOW() AND d.START_TIME<NOW() or a.ORDER_GEN_RULE='3'
	 */
	@Select("SELECT a.ACTIVITY_LEVEL,a.FILTER_BLACKUSERLIST,a.FILTER_WHITEUSERLIST,a.ORDER_GEN_RULE,s.ACTIVITY_SEQ_ID,d.* "
			+ "FROM PLT_ACTIVITY_CHANNEL_DETAIL d "
			+ "inner join  PLT_ACTIVITY_INFO a on a.TENANT_ID=d.TENANT_ID AND a.ACTIVITY_ID=d.ACTIVITY_ID "
			+ "inner join  PLT_ACTIVITY_CHANNEL_STATUS s on s.ACTIVITY_SEQ_ID=a.REC_ID AND s.CHANNEL_ID=#{channelId} "
			+ "WHERE a.TENANT_ID=#{tenantId} AND s.STATUS=#{status} AND d.CHANN_ID=#{channelId} and a.ACTIVITY_STATUS in('1','8','9')")
	public List<HashMap<String, Object>> findActivityDetail(HashMap<String, Object> activity);
	
	/**
	 * 获取活动短信总数
	 * @param activity
	 * @return
	 */
	@Select("SELECT count(1) FROM PLT_ORDER_INFO_SMS sms " 
			+ " inner join PLT_USER_LABEL u on u.USER_ID=sms.USER_ID " 
			+ " WHERE sms.TENANT_ID=#{tenantId} AND sms.CHANNEL_ID=#{channelId} AND sms.ACTIVITY_SEQ_ID=#{activitySeqId} "
			+ " AND sms.ORDER_STATUS=#{orderStatus}")
	public Long selectCount(HashMap<String, Object> activity);
	/**
	 * 每次从短信表去取某个活动的５０００条数据
	 * @param activity
	 * @return
	 */
	@Select("SELECT sms.TENANT_ID,sms.PHONE_NUMBER,sms.REC_ID ${userColum} FROM PLT_ORDER_INFO_SMS sms " 
			+ "inner join PLT_USER_LABEL u on u.USER_ID=sms.USER_ID " 
			+ "WHERE sms.TENANT_ID=#{tenantId} AND sms.CHANNEL_ID=#{channelId} AND sms.ACTIVITY_SEQ_ID=#{activitySqlId} "
			+ "AND sms.ORDER_STATUS=#{orderStatus} AND sms.CHANNEL_STATUS=#{channelStatus} and sms.PREPARE_SEND_STATUS=#{prepareSendStatus} LIMIT 5000")
	public List<HashMap<String, Object>> findDxOrderList(HashMap<String, Object> activity);
	
	/**
	 * 批量置短信为预发送状态,每次５０００条数据
	 * @param activity
	 * @return
	 */
	@Select("UPDATE PLT_ORDER_INFO_SMS SET PREPARE_SEND_STATUS=#{prepareSendStatus} WHERE TENANT_ID=#{tenantId} " 
			+ "AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{activitySqlId} AND ORDER_STATUS=#{orderStatus} "
			+ "AND CHANNEL_STATUS=#{channelStatus} and (PREPARE_SEND_STATUS<>'2' or PREPARE_SEND_STATUS is null) LIMIT 5000")
	public void updateDxOrderList(HashMap<String, Object> activity);

	/**
	 * 将微信状态置为互斥
	 * @param wxMutex
	 */
	@Update("UPDATE PLT_ORDER_INFO SET CHANNEL_STATUS='403' WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} "
			+ " AND CHANNEL_STATUS=#{channelStatus} AND PHONE_NUMBER=#{PHONE_NUMBER} ") 
	public void modifyWxStatus(HashMap<String, Object> wxMutex);
	
	/**
	 * 批量将微信状态置为互斥
	 * @param wxMutex
	 */
	@Update("UPDATE PLT_ORDER_INFO SET CHANNEL_STATUS='403' WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} "
			+ " AND CHANNEL_STATUS=#{channelStatus} AND PHONE_NUMBER in(select PHONE_NUMBER from PLT_ORDER_INFO_SMS where PREPARE_SEND_STATUS=#{PREPARE_SEND_STATUS} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} )") 
	public void updateWxStatus(HashMap<String, Object> wxMutex);

	/**
	 * 修改短信的状态
	 * @param hashMap
	 */
	@Update("UPDATE PLT_ORDER_INFO_SMS SET CHANNEL_STATUS='2' WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID}") 
	public void modifyDxStatus(HashMap<String, Object> hashMap);
	/**
	 * 批量修改短信的状态
	 * @param hashMap
	 */
	@Update("UPDATE PLT_ORDER_INFO_SMS SET CHANNEL_STATUS='2',PREPARE_SEND_STATUS='2',CONTACT_CODE='2',CONTACT_DATE=NOW() WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activitySqlId} AND PREPARE_SEND_STATUS=#{prepareSendStatus}") 
	public void modifyPlDxStatus(HashMap<String, Object> hashMap);

	/**
	 * 修改短信的活动为失效
	 * @param activity
	 */
	@Update("UPDATE PLT_ORDER_INFO_SMS SET CHANNEL_STATUS='5' WHERE TENANT_ID=#{tenantId} AND PREPARE_SEND_STATUS='1' AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{activitySqlId} ") 
	public void modifyDxLose(HashMap<String, Object> activity);

	/**
	 * 修改其他渠道的活动为失效
	 * @param reqMap
	 */
	@Update("UPDATE PLT_ORDER_INFO SET CHANNEL_STATUS=#{channelStatus} WHERE TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} ") 
	public void modifyChannelStatus(HashMap<String, Object> reqMap);
	
	/**
	 * 修改短信渠道是否发送短信
	 * @param reqMap
	 */
	@Update("UPDATE PLT_ACTIVITY_CHANNEL_STATUS SET STATUS=#{status} WHERE  TENANT_ID=#{tenantId} AND CHANNEL_ID=#{channelId} and ACTIVITY_SEQ_ID=#{activitySqlId} ") 
	public void modifyDuanxinStatus(HashMap<String, Object> reqMap);

	/**
	 * 文件发送统计
	 * @param sms
	 */
	@Insert("INSERT INTO PLT_ORDER_STATISTIC_SEND (TENANT_ID,EXTERNAL_ID,ACTIVITY_SEQ_ID,CHANNEL_ID,SEND_ALL_COUNT,SEND_ALL_NUM,IS_FINISH,SEND_DATE) "
			+ " VALUES (#{tenantId},#{externalId},#{activitySeqId},#{channelId},#{sendAllCount},#{sendAllNum},0,NOW())")
	public void addSmsStatistic(HashMap<String, Object> sms);
	
	/**
	 * 查询短信发送统计
	 * @param sms
	 */
	@Select("SELECT * FROM PLT_ORDER_STATISTIC_SEND WHERE ACTIVITY_SEQ_ID=#{activitySeqId} AND CHANNEL_ID=#{channelId} and TENANT_ID=#{tenantId}")
	public List<HashMap<String, Object>> findSmsStatistic(HashMap<String, Object> sms);
	
	/**
	 * 查询短信发送统计
	 * @param sms
	 */
	@Select("SELECT * FROM PLT_ORDER_STATISTIC_SEND WHERE TENANT_ID=#{tenantId} and CHANNEL_ID=#{channelId} and IS_FINISH=#{IS_FINISH}")
	public List<HashMap<String, Object>> findSmsNoSend(HashMap<String, Object> sms);
	
	/**
	 * 删除短信发送统计
	 * @param sms
	 */
	@Select("DELETE FROM PLT_ORDER_STATISTIC_SEND WHERE ACTIVITY_SEQ_ID=#{activityId} and TENANT_ID=#{tenantId}")
	public void delSmsStatistic(HashMap<String, Object> sms);
	
	/**
	 * 修改发送统计
	 * @param sms
	 */
	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET SEND_NUM=#{SEND_NUM},SEND_SUC_NUM=#{SEND_SUC_NUM},SEND_ERR_NUM=#{SEND_ERR_NUM} WHERE EXTERNAL_ID=#{externalId} and TENANT_ID=#{tenantId}")
	public void updateSmsStatistic(HashMap<String, Object> sms);
	
	/**
	 * 修改发送统计记录状态
	 * @param sms
	 */
	@Update("UPDATE PLT_ORDER_STATISTIC_SEND SET IS_FINISH=#{IS_FINISH} WHERE EXTERNAL_ID=#{externalId} and TENANT_ID=#{tenantId}")
	public void updateSmsStatisticFlag(HashMap<String, Object> sms);
	
	/**
	 * 修改发送统计记录状态
	 * @param sms
	 */
	@Select("SELECT * FROM PLT_ORDER_STATISTIC_SEND   WHERE EXTERNAL_ID=#{externalId} and TENANT_ID=#{tenantId}  and channel_id= #{channelId}")
	public 	 List<HashMap<String, Object>>  findOneSmsStatistic(HashMap<String, Object> sms);
	
	/**
	 * 将已完成短信工单放入历史表
	 * @param sms
	 */
	@Insert("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID ='uni076'*/ insert into PLT_ORDER_INFO_SMS_HIS select * from PLT_ORDER_INFO_SMS a " +
			" where a.ACTIVITY_SEQ_ID=#{activitySqlId}  and TENANT_ID=#{tenantId}")
	public void smsToHistory(HashMap<String, Object> sms);
	
	/**
	 * 将已完成短信从工单表中删除
	 * @param sms
	 */
	@Insert("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID ='uni076'*/delete from PLT_ORDER_INFO_SMS where ACTIVITY_SEQ_ID=#{activitySqlId} and TENANT_ID=#{tenantId}")
	public void delSms(HashMap<String, Object> sms);
	
	/**
	 * 查询租户
	 * @return
	 */
	@Select(" select * from TENANT_INFO where STATE='1'")
	public List<HashMap<String, Object>> findTenant();
	
	/**
	 * 话术变量
	 * @return
	 */
	@Select(" SELECT * FROM PLT_STATIC_CODE WHERE TABLE_NAME in(${TABLE_NAME}) and TENANT_ID=#{tenantId}")
	public List<HashMap<String, Object>> findTalkList(HashMap<String, Object> reqMap);
	/**
	 * 修改pl_order_info_sms 的CHANNEL_STATUS为短信发送失败(404)  这个值最终会记录到历史表中
	 * @return
	 */
	@Update("UPDATE PLT_ORDER_INFO_SMS SET CHANNEL_STATUS=#{channel_status} WHERE TENANT_ID=#{tenantId} " 
			+ "AND CHANNEL_ID=#{channelId} AND ACTIVITY_SEQ_ID=#{activitySqlId}  "
			+ " and PHONE_NUMBER in (${phoneStr})")
	public void modifyOrderInfoSmsStatus(HashMap<String, Object> smsMap);
}
