package com.bonc.busi.interfaces.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.bonc.busi.interfaces.model.frontline.OrderQueryReq;

public interface FrontLineMapper {

	/**
	 * @USE 1.3
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="sumOrdersStatistic")
	HashMap<String, Object> sumOrdersStatistic(HashMap<String, Object> req);
	
	/**
	 * @USE 2.1工单查询(移动)　,已经对活动状态进行约束
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="countOrdersQueryMB")
	Integer countOrdersQueryMB(OrderQueryReq req);
	
	/**
	 * @USE 2.2
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="findOrdersQueryMB")
	List<HashMap<String, Object>> findOrdersQueryMB(OrderQueryReq req);
	
	/**
	 * @USE 2.3 工单查询(宽带)　,已经对活动状态进行约束
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="countOrdersQueryKD")
	Integer countOrdersQueryKD(OrderQueryReq req);
	
	/**
	 * @USE 2.4
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="findOrdersQueryKD")
	List<HashMap<String, Object>> findOrdersQueryKD(OrderQueryReq req);

	
	
	/**
	 * @USE 3 执行信息接口　TODO 暂时未添加　　　　活动状态限制
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="custManagerStatistic")
	List<HashMap<String, Object>> custManagerStatistic(HashMap<String, Object> req);

	
	
	/**
	 * @USE 4.1用户工单查询,已经对活动状态进行约束
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="findUserOrder")
	List<HashMap<String, Object>> findUserOrder(HashMap<String, String> req);

	/**
	 * 查询用户的活动信息
	 */
	@Select(  "	SELECT a.ACTIVITY_ID OLD_ACTIVITY_ID,a.ACTIVITY_NAME,a.ACTIVITY_LEVEL,a.GROUP_ID,a.GROUP_NAME,a.ACTIVITY_DESC,"
			+ " a.USERGROUP_FILTERCON, c.FILTER_CON,a.STRATEGY_DESC POLICY_DES,c.MARKET_WORDS,c.SMS_WORDS"
			+ " FROM PLT_ACTIVITY_INFO a,PLT_ACTIVITY_CHANNEL_DETAIL c "
			+ " WHERE a.REC_ID=#{activitySeqId} AND a.TENANT_ID=#{tenantId} "
			+ " AND c.TENANT_ID=#{tenantId} AND c.ACTIVITY_SEQ_ID=#{activitySeqId} "
			+ " AND c.CHANN_ID=#{channelId} AND a.ACTIVITY_STATUS IN ('1','8','9') ")
	HashMap<String, Object> getActivityInfo(HashMap<String, String> req);

	/**
	 * 查询用户的活动信息
	 */
	@Select(  "	SELECT s.SUCESSNAME SUCCESS_NAME,s.SUCESSTYPE SUCCESS_TYPE,s.SUCESSPOINTS AWARD_NUM,"
			+ " s.SUCESSREWARD AWARD_DESC,s.SUCESSCONDITIONE SUCESS_CONDITIONE FROM PLT_ACTIVITY_SUCESS_CFG s "
			+ " WHERE s.TENANT_ID=#{tenantId} AND s.ACTIVITY_SEQ_ID=#{activitySeqId} ")
	HashMap<String, Object> getSuccessInfo(HashMap<String, String> req);
	
	/**
	 * @USE 4.2活动产品工单查询
	 * @param req
	 * @return
	 */
	@Select("SELECT p.PRODUCTNAME PRODUCT_NAME,p.PRODUCTCODE PRODUCT_CODE,"
			+ " p.ORD PRODUCT_ORD,p.PRODUCTDISTRICT PRODUCTD_ISTRICT "
			+ " FROM PLT_ACTIVITY_PRODUCT_LIST p "
			+ " WHERE p.TENANT_ID=#{tenantId} AND p.ACTIVITY_SEQ_ID=#{activitySeqId} ")
	List<HashMap<String, Object>> findProductList(HashMap<String, String> req); 
	
	/**
	 * @USE 4.3
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="contactHistory")
	List<HashMap<String,Object>> contactHistory(HashMap<String, Object> req);

	
	/**
	 * @USE 5.1
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="findActivityStatistic")
	List<HashMap<String, Object>> findActivityStatistic(HashMap<String, Object> req);

	/**
	 * @USE 5.2
	 * @param req
	 * @return
	 */
	@SelectProvider(type=FrontLineProvider.class,method="countActivityStatistic")
	List<String> countActivityStatistic(HashMap<String, Object> req);

	
	@Select("SELECT * FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_ID=#{activityId} ORDER BY LAST_ORDER_CREATE_TIME DESC")
	List<HashMap<String, Object>> findActivityStatus(OrderQueryReq req);

	@SelectProvider(type=FrontLineProvider.class,method="getActivitySeqIds")
	List<HashMap<String, Object>> getActivitySeqIds(OrderQueryReq req);

	@Select("SELECT REC_ID,ACTIVITY_NAME FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{tenantId} AND REC_ID IN ${req}")
	List<HashMap<String, Object>> getContactActivity(@Param("tenantId") String tenantId,@Param("req") String req);


}
