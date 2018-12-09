package com.bonc.busi.orderschedule.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.bonc.busi.orderschedule.bo.OrderTablesAssignRecord;

/**
 * order table assign ruler mapper interface
 * @author shenyanjun@bonc.com.cn
 * 
 */
public interface OrderTableRouting {
	/**
	 *  get assigned table name by activity's id from assign records,if exists  table and no reach limit amount use it
	 *  else get new table name form routing table
	 *  @param activity's id
	 *  @param tenant id
	 *  @return table name
	 */
	@Select ("select TABLE_NAME from PLT_ORDER_TABLES_ASSIGN_RECORD_INFO where activity_id=#{actId} and CHANNEL_ID=#{channelId} and BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}"
			 + " order by ASSIGN_DATE DESC limit 1")
	public String getActivityAssignedTable(@Param("actId") String actId,@Param("channelId") String channelId,@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 * record  one activity assigned table info
	 *   @param record
	 *   @return 
	 */
	@Insert("insert into PLT_ORDER_TABLES_ASSIGN_RECORD_INFO (ACTIVITY_SEQ_ID,ACTIVITY_ID,CHANNEL_ID,TABLE_NAME,ASSIGN_DATE,TENANT_ID,BUSI_TYPE)"
			+ " values (#{ACTIVITY_SEQ_ID},#{ACTIVITY_ID},#{CHANNEL_ID},#{TABLE_NAME},#{ASSIGN_DATE},#{TENANT_ID},#{BUSI_TYPE}) ")
	public void recordActivityAssignedTableInfo(OrderTablesAssignRecord record);
	
	/**
	 *  get new table
	 *  @param busi type;0:order using,1:order filter using.
	 *  @param tenId
	 *  @return table name
	 */
	@Select ("select concat(TABLE_PREFIX,cast(CURRENT_SEQ_NUMBER as CHAR)) from PLT_ORDER_TABLES_ROUTING_INFO where BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public String getRoutingTable(@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  get max capacity of the system table
	 *  @param busi type
	 *  @param tenId
	 *  @return MAX_SEQ_NUMBER
	 */
	@Select ("select  MAX_SEQ_NUMBER from PLT_ORDER_TABLES_ROUTING_INFO where BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public Integer getMaxAssignedTableNumber(@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  get  current assigned table number,if current_seq_number reach max, resize?
	 *  @param busi type
	 *  @param tenId
	 * @return 
	 *  @return MAX_SEQ_NUMBER,if no data return null
	 */
	@Select ("select  CURRENT_SEQ_NUMBER from PLT_ORDER_TABLES_ROUTING_INFO where BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public Integer  getCurrentAssignedTableNumber(@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  increase current table index
	 *  @param busi type
	 *  @param tenId
	 *  @return MAX_SEQ_NUMBER
	 */
	@Update("update PLT_ORDER_TABLES_ROUTING_INFO set CURRENT_SEQ_NUMBER= CURRENT_SEQ_NUMBER +1 where " 
			+ " BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}") 
	public void setTableRoutingCurrentIndex(@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  get total limit number of the table
	 *  @param busi type
	 *  @param tenId
	 *  @param tname,table's name
	 *  @return 
	 */
	@Select ("select  TOTAL_LIMIT from PLT_ORDER_TABLES_USING_INFO where TABLE_NAME=#{tname} and USING_BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public Integer getTableTotalLimit(@Param("tname") String tname,@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  get table using number
	 *  @param busi type
	 *  @param tenId
	 *  @param tname,table's name
	 *  @return 
	 */
	@Select ("select  CURRENT_AMOUNT from PLT_ORDER_TABLES_USING_INFO where TABLE_NAME=#{tname} and USING_BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public Integer getTableUsingNumber(@Param("tname") String tname,@Param("busiType") int busiType,@Param("tenId") String tenId);

	/**
	 *  get max remain capacity table form using table
	 *  @param busi type
	 *  @param tenId
	 *  @return 
	 */
	@Select ("select  TABLE_NAME from PLT_ORDER_TABLES_USING_INFO  where USING_BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId} order by CURRENT_AMOUNT asc limit 1;")
	public String getMaxRemainCapacityTable(@Param("busiType") int busiType,@Param("tenId") String tenId);

	
	/**
	 *  update table using status.0:no use;1:use
	 *  @param status
	 *  @param busi type
	 *  @param tenId
	 *  @param tname,table's name
	 *  @return 
	 */
	@Update("update PLT_ORDER_TABLES_USING_INFO set USING_STATUS=${status} where TABLE_NAME=#{tname} and USING_BUSI_TYPE=#{busiType} and TENANT_ID=#{tenId}")
	public void setTableUsingStatus(@Param("status") int status,@Param("tname") String tname,@Param("busiType") int busiType,@Param("tenId") String tenId);
	
	/**
	 *  increase decrease table using numbers
	 *  @param params
	 *  @return 
	 */
	@UpdateProvider(type = SqlWorker.class, method = "setTableCapacity")
	public void setTableCapacity(Map<String,Object> params);

	/**
	 * 删除PLT_ORDER_TABLES_ASSIGN_RECORD_INFO记录
	 * @param rec
	 */
	@Delete("DELETE FROM PLT_ORDER_TABLES_ASSIGN_RECORD_INFO WHERE "
			+ " ACTIVITY_SEQ_ID=#{ACTIVITY_SEQ_ID} AND ACTIVITY_ID=#{ACTIVITY_ID}  AND CHANNEL_ID=#{CHANNEL_ID}  "
			+ " AND TABLE_NAME=#{TABLE_NAME}  AND  BUSI_TYPE=#{BUSI_TYPE} AND TENANT_ID=#{TENANT_ID} ")
	public void deleteActivityAssignedTableInfo(OrderTablesAssignRecord rec);
}
