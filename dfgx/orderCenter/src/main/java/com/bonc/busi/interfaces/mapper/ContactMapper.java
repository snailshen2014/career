package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

public interface ContactMapper {
	/**
	 * @USE 1
	 * @param map
	 * @return
	 */
	@Select("SELECT *,IF(CONTACT_DATE>CURDATE(),1,0) TODAY_CONTACT FROM ${tableName} WHERE TENANT_ID=#{tenantId} AND  REC_ID=#{recId}")
	HashMap<String, Object> findOrderInfo(HashMap<String, Object> map);
	
	@Insert("INSERT INTO ${logTable} (TENANT_ID,CHANNEL_ID,ACTIVITY_SEQ_ID,ORDER_REC_ID,SERVICE_TYPE,ORG_PATH,USER_ID,PHONE_NUMBER,CONTACT_DATE,CONTACT_CODE,CONTACT_CONTENT,CONTACT_TYPE,IMG_PATH,LOGIN_ID,LOGIN_NAME,EXT0,EXT1,EXT2,EXT3,EXT4,EXT5) "
			+ " VALUES (#{tenantId},#{channelId},#{ACTIVITY_SEQ_ID},#{recId},#{serviceType},#{orgPath},#{USER_ID},#{PHONE_NUMBER},#{contactDate},#{contactCode},#{contactMsg},#{contactType},#{imgPath},#{loginId},#{loginName},#{ext0},#{ext1},#{ext2},#{ext3},#{ext4},#{ext5})")
	void insertContact(HashMap<String, Object> map);

	@UpdateProvider(method="contactStatistc",type=ContactProvider.class)
	void contactStatistc(HashMap<String, Object> map);

	//TODO 查询活动信息，用于判断是否互斥
	@Select("SELECT a.*,d.* FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_CHANNEL_DETAIL d WHERE a.TENANT_ID=#{tenantId} "
			+ " AND a.REC_ID=#{ACTIVITY_SEQ_ID} AND a.TENANT_ID=d.TENANT_ID AND a.REC_ID=d.ACTIVITY_SEQ_ID AND d.CHANN_ID=#{channelId}")
	HashMap<String, Object> findActivity(HashMap<String, Object> map);

	//TODO 更新互斥工单
	@Update("UPDATE PLT_ORDER_INFO SET CHANNEL_STATUS=#{mutexStatus} WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND USER_ID=#{USER_ID} AND CHANNEL_ID=#{mutexChannel} ")
	void changeMutex(HashMap<String, Object> map);

	//TODO 修改工单信息，如果接触编码已经接触过了不进行更新 
	@Update("UPDATE ${tableName} "
			+ " SET CONTACT_DATE=#{contactDate}, "
			+ " CONTACT_TYPE=#{contactType}, "
			+ " CONTACT_CODE=#{contactCode}, "
			+ " RESERVE2=#{contactMsg},"
			+ " EXE_PATH=#{exeId},"
			+ " CHNL_TYPE4=#{loginId},"
			+ " CHANNEL_STATUS=#{CHANNEL_STATUS} "
			+ " WHERE TENANT_ID=#{tenantId} "
			+ " AND REC_ID=#{recId} "
			+ " AND CHANNEL_STATUS=0 ")
	Integer updateOrderInfo(HashMap<String, Object> map);

	/**
	 * 修改批次完成情况
	 * @param statistic
	 */
	@UpdateProvider(method="updateActivityProc",type=ContactProvider.class)
	void updateActivityProc(HashMap<String, Object> statistic);
}
