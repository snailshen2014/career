package com.bonc.busi.outer.mapper;

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
	
	
    @Select("select * from PLT_ACTIVITY_INFO where ACTIVITY_ID=#{ActivityId} and  TENANT_ID=#{TenantId}")
    public List<PltActivityInfo> retrievePltActivityInfoByActivityId(@Param("ActivityId")String ActivityId,
    		@Param("TenantId")String TenantId);
    
    // --- 更新活动状态信息 ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS=#{ACTIVITY_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void updatePltActivityInfo(PltActivityInfo data);
    
    // --- 失效活动 ---
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS=2 , END_DATE = now()  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void expirePltActivityInfo(PltActivityInfo data);
    
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
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS = 9 , BEFORE_SUSPEND_STATUS = #{ACTIVITY_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void suspendActivity(PltActivityInfo data);
    // --- 活动恢复 ---   
    @Update("update PLT_ACTIVITY_INFO set ACTIVITY_STATUS= #{BEFORE_SUSPEND_STATUS}  "
            + " where ACTIVITY_ID=#{ACTIVITY_ID} and TENANT_ID=#{TENANT_ID}")
    public void resumeActivity(PltActivityInfo data);
    
    // --- 查询IS SIM  ----

    @Select("select IS_SIM from UNICOM_D_MB_DS_ALL_LABEL_INFO "
    		+" where date_id=#{date_id} and  user_id=#{user_id}")
    public String selectChangeCard(@Param("date_id")String date_id,@Param("user_id")String user_id);
    

}
