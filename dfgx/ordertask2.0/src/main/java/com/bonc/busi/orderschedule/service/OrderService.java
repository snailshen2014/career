package com.bonc.busi.orderschedule.service;

import java.util.List;
import com.bonc.busi.orderschedule.bo.*;

/*
 * @Desc: order producer service,the entry of the scheduled task
 * @Author: shenyanjun@bonc.com.cn
 * @Time: 2017-04-12 for productization add
 */
public interface OrderService {

	/**
	 * business deal function
	 * 
	 * @param url
	 * @return
	 */
	void doActivity(String activity_url, String tenantId);

	/**
	 * insert activity table in mysql
	 * 
	 * @param activity
	 * @return 0:success;-1 error
	 */
	void commitActivityInfo(PltActivityInfo activity);

	/**
	 * get user group infomation from oracle by user group id.
	 * 
	 * @param user
	 *            group id
	 * @return 0:success;-1 error
	 */
	String getUserGroupInfo(String id);

	/*
	 * get invalid order activity_seq_id from plt_order_info table
	 * 
	 * @param tenantId
	 * 
	 * @return List<Integer>
	 */
	List<PltActivityInfo> getInvalidActivitySeqId(String tenantId);

	/*  
	 * DEPRECATED
	 * move invalid order records to his table
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void moveInvalidOrderRecords(PltActivityInfo act);
	
	/*
	 * DEPRECATED
	 * delete invalid order records from plt_order_info
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void deleteInvalidOrderRecords(PltActivityInfo act);
	
	/*
	 * DEPRECATED
	 * update invalid order records,set
	 * invalid_date=sysdate,input_date=sysdate,order_status='6'
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */

	public void updateInvalidOrderRecords(PltActivityInfo act);

	/*
	 * update activity_info table set activity_status='2' when order recores
	 * invalid
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
	public void updateActvityInfoInvalid(PltActivityInfo act);

	/*
	 * DEPRECATED
	 * get the last finished activity rec_id by activity_id
	 * 
	 * @param PltActivityInfo just using rec_id,tenant_id
	 * 
	 * @return
	 */
	public Integer getLastActivityRecId(PltActivityInfo act);

	/*
	 * get last log record time
	 * 
	 * @param
	 * 
	 * @return max time
	 */

	public String getLastLogTime();

}
