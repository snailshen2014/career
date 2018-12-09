package com.bonc.busi.outer.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.outer.model.PltActivityInfo;

public interface PltActivityInfoDao {
	
	//@Select("#{sql}")
	//public List<Map>   selectList(@Param("sql")String  sql);
	
	@Select("select * from PLT_ACTIVITY_INFO")
    public List<PltActivityInfo> retrieveAllPltActivityInfos();
	
	
    @Select("select * from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{ActivityId} and TENANT_ID=#{TenantId} ORDER BY LAST_ORDER_CREATE_TIME")
    public List<PltActivityInfo> retrievePltActivityInfoByActivityId(@Param("ActivityId")String ActivityId,
    		@Param("TenantId")String TenantId);
    
    // --- 更新活动状态信息 ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS=#{ACTIVITY_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void updatePltActivityInfo(PltActivityInfo data);
    
    // --- 失效活动 ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS=2,ORI_STATE=8,END_DATE=now(),ORDER_END_DATE=now()"
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void expirePltActivityInfo(PltActivityInfo data);
    
    @Select("select * from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{ActivityId} and TENANT_ID=#{TenantId} ORDER BY LAST_ORDER_CREATE_TIME")
    public List<PltActivityInfo> activityInfoByActivityId(@Param("ActivityId")String ActivityId,@Param("TenantId")String TenantId);
    
    // --- 执行批处理,更新工单为取消状态 ---
    @Update("update PLT_ORDER_INFO set ORDER_STAUTS = 3  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void expireOrder(PltActivityInfo data);
    
    
    // --- 执行批处理,将工单移入历史 ---
    @Insert("insert into PLT_ORDER_INFO_HIS select * from PLT_ORDER_INFO"  +
      "  where  ACTIVITY_SEQ_ID =#{ActivitySeqId} and TENANT_ID=#{TenantId}")
    public int moveOrderHis(@Param("ActivitySeqId") int ActivitySeqId,
    		@Param("TenantId")String TenantId);
    
    // --- 删除工单（待执行) ---
 // --- 执行批处理,将工单移入历史 ---
    @Delete("delete  PLT_ORDER_INFO "  +
      "  where  ACTIVITY_SEQ_ID =#{ActivitySeqId} and TENANT_ID=#{TenantId}")
    public int deleteOrder(@Param("ActivitySeqId") int ActivitySeqId,
    		@Param("TenantId")String TenantId);
    
    // --- 活动挂起  ---    
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS = 9,ORI_STATE=11 , BEFORE_SUSPEND_STATUS = #{ACTIVITY_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void suspendActivity(PltActivityInfo data);
    // --- 活动恢复 ---   
    @Update("update PLT_ACTIVITY_INFO set ORI_STATE=9 ,ACTIVITY_STATUS= #{BEFORE_SUSPEND_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void resumeActivity(PltActivityInfo data);
    
    // --- 查询IS SIM  ----

    @Select("select IS_SIM from UNICOM_D_MB_DS_ALL_LABEL_INFO "
    		+" where date_id=#{date_id} and  user_id=#{user_id}")
    public String selectChangeCard(@Param("date_id")String date_id,@Param("user_id")String user_id);
    
    // --- 查询最大和最小  ---
    @Select("SELECT MIN(REC_ID) AS MINID  FROM PLT_ORDER_INFO "
    		+" WHERE ACTIVITY_SEQ_ID =#{activitySeqId}")
    public long selectOrderId(@Param("activitySeqId")int activitySeqId);
    
    
    // --- 场景营销 工单查询---
    @Select("SELECT PHONE_NUMBER,DATE_FORMAT(CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE "
    		+ " FROM PLT_ORDER_INFO_SCENEMARKET  "
    		+ " WHERE  PHONE_NUMBER = #{phoneNum} "
    		+ " AND TENANT_ID = #{tenantId} "
    		+ " AND BEGIN_DATE>=#{contactDateStart} "
    		+ " AND BEGIN_DATE<=#{contactDateEnd} "
    		+ " AND CHANNEL_STATUS = '3' "
    		+ " ${envTypeAnd} ${eventIdAnd} ${channelIdAnd} ${activitySeqId} "
    		
    		)
    public List<HashMap<String,Object>> selectActivityOrder(HashMap<String, Object> req);
    
    
    //--- 场景营销根据activityId查
    @Select("SELECT REC_ID FROM plt_activity_info WHERE ACTIVITY_ID = #{activityId}")
    public List<HashMap<String, Object>> selectActivityId(@Param("activityId") Object object);
}
