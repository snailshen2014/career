package com.bonc.busi.orderschedule.service;

import java.sql.SQLException;
import java.util.List;

import com.bonc.busi.orderschedule.bo.ActivityDailySummary;
import com.bonc.busi.orderschedule.bo.PltActivityInfo;
import com.bonc.common.base.JsonResult;

public interface OrderService {

	

	/** xcloud datasource test
	 * @param 
	 * @return
	 */	
	void TestXcloud();
	
	/** oracle datasource test
	 * @param 
	 * @return
	 */	
	void TestOracle();
	
	/** request activity info
	 * @param url
	 * @return json string
	 */
	String getActivityInfo(String activity_url );
	
	/** business deal function
	 * @param url
	 * @return 
	 */
	void  doActivity(String activity_url,String tenantId );
	
	/** insert activity table in mysql
	 * @param activity
	 * @return 0:success;-1 error
	 */
	void commitActivityInfo(PltActivityInfo activity );
	
	/** get user group infomation from oracle by user group id.
	 * @param user group id
	 * @return 0:success;-1 error
	 */
	String getUserGroupInfo(String id );
	
	/** create order records on xcloud
	 * @param sql
	 * @return 0:success;-1 error
	 * @throws SQLException 
	 */
	void   genOrderRec(String sql ) throws SQLException;
	
	/** insert activity summmary table in mysql
	 * @param activity
	 * @return 0:success;-1 error
	 */
	Integer commitActivitySummmaryInfo(ActivityDailySummary summary );
	

	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * @param tenantId
	 * @return List<Integer>
	 */
	List<Integer> getInvalidActivitySeqId(String tenantId);
	
	/*
	 * move invalid order records to his table
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	
	public void  moveInvalidOrderRecords(PltActivityInfo act);
	/*
	 * delete  invalid order records from plt_order_info
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	
	public void  deleteInvalidOrderRecords(PltActivityInfo act);
	/*
	 * update invalid order records,set invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	
	public void  updateInvalidOrderRecords(PltActivityInfo act) ;
	
	/*
	 * update activity_info table set activity_status='2' when order recores invalid
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	public void  updateActvityInfoInvalid(PltActivityInfo act);
	/*
	 * get the last finished activity rec_id by activity_id
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * @return 
	 */
	public Integer  getLastActivityRecId(PltActivityInfo act);
	public JsonResult startSceneMarketActivity(String response);
	
}
