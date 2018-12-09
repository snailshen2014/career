package com.bonc.busi.divide.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.bonc.busi.divide.model.DivideArupBean;
import com.bonc.busi.divide.model.RulePreReq;

public interface ManuDivideMapper {

	/**
	 * @USE 1.1 获取当前组织机构 账期活动 总数
	 * @param request
	 * @return
	 */
	@SelectProvider(method="divideActivityCount",type=DivideSelectGen.class)
	List<Integer> divideActivityCount(HashMap<String, Object> request);
	
	/**
	 * @USE 1.2
	 * @param pama
	 * @return
	 */
	@SelectProvider(method="divideActivityList",type=DivideSelectGen.class)
	List<HashMap<String, Object>> divideActivityList(HashMap<String, Object> request);

	@Select("SELECT PATH FROM PLT_USER_ORG_INFO WHERE TENANT_ID=#{tenantId} AND AREA_CODE=#{areaNo} AND CITY_ID=-1 LIMIT 1")
	String getAreaPath(HashMap<String, Object> req);
//	
	@Select("SELECT PATH FROM PLT_USER_ORG_INFO WHERE TENANT_ID=#{tenantId} AND (ORGINFO_ID='' OR ORGINFO_ID IS NULL) LIMIT 1 ")
	String getRootPath(HashMap<String, Object> pama);
	
	/**
	 * @USE 2.1 获取下级组织列表
	 * @param request
	 * @return
	 */
//	@Select("SELECT PATH FROM PLT_USER_ORG_INFO WHERE TENANT_ID=#{tenantId} "
//			+ " AND ORGINFO_ID IN (SELECT ID FROM PLT_USER_ORG_INFO WHERE TENANT_ID=#{tenantId} ${PARENT_PATH} )"
//			+ " AND PATH LIKE '${orgPath}%' ${CITY_ID} ${AREA_NO}")
	@Select("SELECT DISTINCT CONCAT(#{parentPath},SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,#{parentPath},-1),'/',2)) NEXT_PATH "
			+ "FROM PLT_ORDER_INFO WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activityId} AND ORG_PATH<>#{parentPath} "
			+ "AND SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',3),'/',-1)=#{areaNo} ${CITY_IDS} ")
	List<String> getOrgList(HashMap<String, Object> request);
	
	/**
	 * @USE 2.2
	 * @param request
	 * @return
	 */
	@Select("SELECT '${subOrgPath}' ORG_PATH,"
			+ " SUM(s.VALID_NUMS) ALL_COUNT,"	//总工单数
			+ " SUM(IF(ORG_PATH=#{subOrgPath},s.VALID_NUMS,0)) ACCEPT_COUNT,"	//总工单数
			+ " IFNULL(SUM(IF(ORG_PATH=#{subOrgPath},s.VALID_NUMS-s.ITEM0,0)),0) DO_COUNT,"	//已执行工单数
			+ " IFNULL(SUM(IF(ORG_PATH=#{subOrgPath},s.ITEM0,0)),0) DIVIDE_ENABLE,"	//可调配工单数
			+ " IFNULL(SUM(IF(IS_EXE=0,s.ITEM0,0)),0) NO_DIVIDE_ALL,"	//未分配到人工单数
			+ " IFNULL(SUM(IF(ORG_PATH=#{subOrgPath} AND IS_EXE=0,s.ITEM0,0)),0) NO_DIVIDE_CUR,"	//本级未分配到人工单数
			+ " IF(SUM(s.ITEM0)=SUM(IF(ORG_PATH=#{subOrgPath},s.ITEM0,0)),'1','0') STATE"	//是否可以下钻标识
			+ " FROM PLT_ORDER_STATISTIC s "
			+ " WHERE s.TENANT_ID=#{tenantId} "
			+ " AND s.ACTIVITY_SEQ_ID=#{activityId}"
			+ " AND (s.ORG_PATH=#{subOrgPath} OR s.ORG_PATH LIKE '${subOrgPath}/%' ) ")
	HashMap<String, Object> dividedOrderList(HashMap<String, Object> request);

	
	@Select("SELECT COUNT(1) FROM PLT_ORDER_STATISTIC s WHERE s.TENANT_ID=#{tenantId} "
			+ "AND s.ACTIVITY_SEQ_ID=#{activityId} ${orgRange} ${loginIds} ")
	Integer countUserDivide(HashMap<String, Object> request);

	@Select("SELECT s.ORG_PATH,s.LOGIN_ID,"
			+ " IFNULL(s.VALID_NUMS,0) ACCEPT_COUNT,"	//总工单数
			+ " IFNULL(s.VALID_NUMS-s.ITEM0,0) DO_COUNT,"	//已执行工单数
			+ " IFNULL(s.ITEM0 ,0) DIVIDE_ENABLE," //可调配工单数
			+ " IS_EXE" //是否到人的标识
			+ " FROM PLT_ORDER_STATISTIC s "
			+ " WHERE s.TENANT_ID=#{tenantId} "
			+ " AND s.ACTIVITY_SEQ_ID=#{activityId}"
			+ " ${orgRange} ${loginIds} "
			+ " LIMIT ${start},${size}")	//根据pageSize,pageNum 获取分页参数
	List<HashMap<String, Object>> listUserDivide(HashMap<String, Object> request);
	
	
	/**
	 * @USE 3.1
	 * @param request
	 * @return
	 */
	@SelectProvider(method="dispatchOrderCount",type= DivideSelectGen.class)
	Integer dispatchOrderCount(HashMap<String, Object> request);

	/**
	 * @USE 3.2
	 * @param request
	 * @return
	 */
	@SelectProvider(method="dispatchOrderCount",type=DivideSelectGen.class)
	List<String> dispatchOrderIds(HashMap<String, Object> request);
	
	/**
	 * @USE 3.2
	 * @param request
	 * @return
	 */
	@SelectProvider(method="dispatchOrderList",type=DivideSelectGen.class)
	List<HashMap<String, Object>> dispatchOrderList(HashMap<String, Object> request);
	
	/**
	 * @USE 3.3
	 * @param tenantId
	 * @param activityId
	 * @return
	 */
	@Select("SELECT ACTIVITY_ID ,ACTIVITY_NAME,"
			+ " DATE_FORMAT(ORDER_BEGIN_DATE,'%Y-%m-%d %H:%i:%s') BEGIN_DATE,"
			+ " DATE_FORMAT(ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') END_DATE "
			+ " FROM PLT_ACTIVITY_INFO WHERE TENANT_ID=#{TENANT_ID} AND REC_ID=#{ACTIVITY_ID}")
	HashMap<String, Object> getActivityInfoMap(@Param("TENANT_ID") String tenantId,
			@Param("ACTIVITY_ID") String activityId);
	
	/**
	 * @USE 4
	 * @param orderMap
	 * 
	 * WENDING_FLAG 存储 归属人的LOGIN_ID
	 */
	@Update("UPDATE PLT_ORDER_INFO SET ORG_PATH=#{orgPath},WENDING_FLAG=#{loginId},RESERVE3=#{isExe} WHERE TENANT_ID=#{tenantId} AND REC_ID IN (${recIds}) AND CONTACT_CODE='0' ")
	void dispathOrder(HashMap<String, Object> orderMap);
	
	/**
	 * @USE 5.2
	 * @param request
	 * @return
	 */
	@SelectProvider(method="belongPreDivide",type=DivideSelectGen.class)
	List<HashMap<String, Object>> belongPreDivide(RulePreReq request);

	/**
	 * @USE 5.3/7.1
	 * @param request
	 * @return
	 */
	@Insert("INSERT INTO PLT_DIVIDE_LOG (TENANT_ID,ORG_PATH,RECEIVE_PATH,LOGIN_ID,REC_ID,DIVIDE_TYPE,DIVIDE_DATE,IS_DIVIDE,DIVIDE_NUM)"
			+ " values (#{TENANT_ID},#{ORG_PATH},#{acceptPath},#{loginId},#{REC_ID},#{DIVIDE_TYPE},#{DIVIDE_DATE},#{IS_DIVIDE},#{divideNum}) ")
	Integer insertDivideLog(HashMap<String, Object> request);
	
	
	/**
	 * @USE 8.1
	 * @param rep
	 */
	@Update("UPDATE PLT_DIVIDE_LOG SET IS_DIVIDE='1' WHERE TENANT_ID=#{tenantId} AND REC_ID=#{recId} ")
	void changeDivideLog(HashMap<String, Object> rep);

	@Select("SELECT REC_ID recId,IFNULL(CONVERT(MB_ARPU,DECIMAL(10,2)),0) arup FROM PLT_ORDER_INFO o "
			+ " WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activityId} "
			+ " ${orderOrgSql} AND ORDER_STATUS='5' AND CONTACT_CODE='0' ORDER BY arup DESC ")
	List<DivideArupBean> getOrdersArup(RulePreReq request);

	@Select("SELECT DISTINCT AREA_NO FROM PLT_ORDER_STATISTIC WHERE TENANT_ID=#{tenantId} AND ACTIVITY_SEQ_ID=#{activityId} ")
	List<HashMap<String, String>> getAreaList(HashMap<String, Object> request);

	@Select("SELECT * FROM PLT_ORG_LOGIN_INFO WHERE TENANT_ID=#{tenantId} AND ORG_PATH=#{orgPath} ")
	HashMap<String, Object> getEndLoginId(@Param("tenantId")String tenantId, @Param("orgPath")String path);


}
