package com.bonc.busi.divide.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;
import java.util.Map;

import com.bonc.busi.divide.model.RulePreReq;
import com.bonc.utils.StringUtil;

public class DivideSelectGen {
	
//	public static void main(String[] args) {
//		BEGIN();
//		SELECT("*");
//		FROM("PLT_ORDER_INFO");
//		WHERE("TENANT_ID=#{tenantId}");
//		WHERE("(ORG_PATH LIKE '/root/213/sa/' OR ORG_PATH=/root/213/sa)");
//		System.out.println(SQL());
//	}
	
	/**
	 * 1 查询工单账期 筛选条件
	 * @param pama
	 * @return
	 */
	private void getDivideWhere(HashMap<String, Object> pama){
    	//租戶約束,
		WHERE("a.TENANT_ID=#{tenantId}");
		WHERE("s.TENANT_ID=#{tenantId}");
		WHERE("(s.ORG_PATH LIKE '"+pama.get("orgPath")+"/%' OR s.ORG_PATH=#{orgPath})");
		WHERE("a.ACTIVITY_STATUS IN ('1','8','9')");
    	//活动名称约束
    	if(!StringUtil.validateStr(pama.get("activityName"))){
    		WHERE("a.ACTIVITY_NAME like '%"+pama.get("activityName")+"%'");
    	}
    	//工单生失效时间约束
    	if(!StringUtil.validateStr(pama.get("orderBeginDateStart"))){
    		WHERE("a.ORDER_BEGIN_DATE>=#{orderBeginDateStart}");
    	}
    	if(!StringUtil.validateStr(pama.get("orderBeginDateEnd"))){
    		WHERE("a.ORDER_BEGIN_DATE<=#{orderBeginDateEnd}");
    	}
    	if(!StringUtil.validateStr(pama.get("orderEndDateStart"))){
    		WHERE("a.ORDER_END_DATE>=#{orderEndDateStart}");
    	}
    	if(!StringUtil.validateStr(pama.get("orderEndDateEnd"))){
    		WHERE("a.ORDER_END_DATE<=#{orderEndDateEnd}");
    	}
    	//TODO 落实 活动行政级别约束字段
    	if(!StringUtil.validateStr(pama.get("activityAdminLevel"))){
    		WHERE("a.ORG_LEVEL=#{activityAdminLevel}");
    	}
    	//约束活动类型
    	if(!StringUtil.validateStr(pama.get("activityThemeId"))){
    		WHERE("a.ACTIVITY_THEMEID=#{activityThemeId}");
    	}
    	//TODO 落实 活动优先级字段 
    	if(!StringUtil.validateStr(pama.get("activityLevel"))){
    		WHERE("a.ACTIVITY_LEVEL=#{activityLevel}");
    	}
    	WHERE("s.ACTIVITY_SEQ_ID=a.REC_ID");
	}
	
	/**
	 * 查询工单账期 总数
	 * @param pama
	 * @return
	 */
	public String divideActivityCount(HashMap<String, Object> pama){
    	BEGIN();
    	SELECT(" COUNT(*) ");
        FROM("PLT_ORDER_STATISTIC s, PLT_ACTIVITY_INFO a " );
        getDivideWhere(pama);
        GROUP_BY(" s.ACTIVITY_SEQ_ID ");
        //设置分页参数
        return SQL();
	}
	
	/**
	 * 1、查询工单账期 分页列表
	 * @param pama
	 * @return
	 */
	public String divideActivityList(HashMap<String, Object> pama){
		BEGIN();
		SELECT("a.ACTIVITY_NAME");
		SELECT("a.REC_ID ACTIVITY_ID");
		SELECT("a.ACTIVITY_ID OLD_ACTIVITY_ID");
		SELECT("a.ACTIVITY_THEMEID");
		SELECT("a.ORG_LEVEL ACTIVITY_ADMIN_LEVEL");
		SELECT("a.CREATE_DATE");
		SELECT("a.ACTIVITY_THEME");
		SELECT("DATE_FORMAT(a.ORDER_BEGIN_DATE,'%Y-%m-%d %H:%i:%s') BEGIN_DATE");
		SELECT("DATE_FORMAT(a.ORDER_END_DATE,'%Y-%m-%d %H:%i:%s') END_DATE");
		SELECT("IFNULL(a.CREATOR_ORG_PATH,'--') CREATOR_ORG_PATH");//活动归属的组织机构路径
		SELECT("SUM(s.VALID_NUMS) ALL_ORDERS");//总共单量
		SELECT("SUM(s.ITEM0) NO_EXE_NUM");//未执行的工单数
		SELECT("SUM(s.VALID_NUMS)-SUM(s.ITEM0) EXE_NUM");//已执行的工单数
		SELECT("SUM(IF(s.ORG_PATH<>#{orgPath},s.VALID_NUMS,0)) DIVIDE_NUM");//全部可分配到执行人工单数
		SELECT("SUM(IF(s.ORG_PATH=#{orgPath},s.VALID_NUMS,0)) NO_DIVIDE");//当前人可分配工单数
		FROM("PLT_ORDER_STATISTIC s, PLT_ACTIVITY_INFO a " );
		getDivideWhere(pama);
        GROUP_BY(" s.ACTIVITY_SEQ_ID");
        ORDER_BY(" a.ACTIVITY_LEVEL ");//根据活动优先级排序 高在前
        ORDER_BY(" a.ORG_LEVEL ");//根据地域级别排序	地域级别高在前
        ORDER_BY(" BEGIN_DATE DESC ");
        ORDER_BY(" NO_DIVIDE DESC ");//根据地域级别排序	地域级别高在前
        //设置分页参数
        String LIMIT = " LIMIT "+(Integer.parseInt(pama.get("pageNum")+"")-1)*Integer.parseInt(pama.get("pageSize")+"")+" , "+pama.get("pageSize");
        return SQL()+LIMIT;
	}
	
	/**
	 * 查询出基层单元 A活动下的划分，根据归属B组织机构路径  ，下可调配工单列表 
	 * 查询出 工单信息，调配中属于工单冻结状态，此时一线人员不可执行该工单
	 * @param pama
	 * @return
	 */
	public String dispatchOrderCount(HashMap<String, Object> pama){
    	BEGIN();
    	//如果选则全部工单就把REC_ID给出来
    	if("1".equals(pama.get("checkAll"))){
    		SELECT(" o.REC_ID ");
    	}else{
    		SELECT(" COUNT(1) ");
    	}
		
    	//如果不存在用户信息，就不对用户表进行关联
		boolean falg = "1".equals(pama.get("userFlag"));
		if(falg){
			FROM("PLT_ORDER_INFO o, PLT_USER_LABEL u " );
		}else{
			FROM("PLT_ORDER_INFO o" );
		}
		
		//活动SEQ_ID约束
		WHERE("o.ACTIVITY_SEQ_ID=#{activityId}");
		
		StringBuilder orgSql = new StringBuilder("( 1=0 ");
		@SuppressWarnings("unchecked")
		Map<String,String> receives = (Map<String, String>) pama.get("receives");
		for(String path:receives.keySet()){
			//如果角色是人
			if("1".equals(receives.get(path))){
				String[] paths = path.split(",");
				orgSql.append("OR ( o.ORG_PATH='").append(paths[0]).append("' AND o.WENDING_FLAG='").append(paths[1]).append("')");
			}else{
				orgSql.append("OR ( o.ORG_PATH='").append(path).append("' AND o.WENDING_FLAG IS NULL )");
			}
		}
		orgSql.append(" ) ");
		WHERE(orgSql.toString());
		WHERE("(o.ORG_PATH LIKE '"+pama.get("orgPath")+"/%' OR o.ORG_PATH=#{orgPath}) ");
		
		if(falg){
			WHERE("u.PARTITION_FLAG=#{partFlag}");
			WHERE("o.USER_ID=u.USER_ID ");
			WHERE("u.TENANT_ID=#{tenantId}");
		}
		
		WHERE("o.CHANNEL_ID=#{channelId}");
		WHERE("o.ORDER_STATUS='5'");
		WHERE("o.CONTACT_CODE ='0'");
		WHERE("o.TENANT_ID=#{tenantId}");
		//责任人 占位字段
		if(!StringUtil.validateStr(pama.get("phoneNum"))){
			WHERE("o.PHONE_NUMBER LIKE '"+pama.get("phoneNum")+"%'");
		}
		
		if(falg){
			if(!StringUtil.validateStr(pama.get("userStatus"))){
				WHERE("u.USER_STATUS=#{userStatus}");
			}
			if(!StringUtil.validateStr(pama.get("rentFeeStart"))){
				WHERE("u.MB_ARPU>=#{rentFeeStart}");
			}
			if(!StringUtil.validateStr(pama.get("rentFeeEnd"))){
				WHERE("u.MB_ARPU<=#{rentFeeEnd}");
			}
			if(!StringUtil.validateStr(pama.get("onlineLongStart"))){
				WHERE("u.MB_ONLINE_DUR/1>=#{onlineLongStart}");
			}
			if(!StringUtil.validateStr(pama.get("onlineLongEnd"))){
				WHERE("u.MB_ONLINE_DUR/1<=#{onlineLongEnd}");
			}
			if(!StringUtil.validateStr(pama.get("areaNo"))){
				WHERE("u.AREA_ID=#{areaNo}");
			}
			if(!StringUtil.validateStr(pama.get("cityId"))){
				WHERE("u.CITY_ID=#{cityId}");
			}
			if(!StringUtil.validateStr(pama.get("towns"))){
				WHERE("u.TOWNS=#{towns}");
			}
			if(!StringUtil.validateStr(pama.get("netChannel"))){
				WHERE("u.MB_NETIN_CHANNEL=#{netChannel}");
			}
		}
        return SQL();
	}
	
	public String dispatchOrderList(HashMap<String, Object> pama){
		BEGIN();
		SELECT("o.REC_ID");
		//如果是checkAll 仅仅选择所有的工单
		SELECT("o.ACTIVITY_SEQ_ID ACTIVITY_ID");
		SELECT("o.RESERVE3 IS_EXE");
		SELECT("u.USER_STATUS");
		SELECT("o.PHONE_NUMBER PHONE_NUMBER");
		SELECT("u.MB_ARPU MB_ARPU");
		SELECT("u.MB_ONLINE_DUR ONLINE_LONG");
		SELECT("o.ORG_PATH S_ORG_PATH");//原始的ORG_PATH
		SELECT("IF(o.RESERVE3='1',o.WENDING_FLAG,'') LOGIN_ID");//工单归属的LOGIN_ID
		SELECT("CONCAT(o.ORG_PATH,IF(o.RESERVE3='1',',',''),IF(o.RESERVE3='1',o.WENDING_FLAG,'')) ORG_PATH");
		SELECT("u.AREA_ID AREA_NO");
		SELECT("u.CITY_ID");
		SELECT("u.TOWNS");
		SELECT("u.MB_NETIN_CHANNEL NET_CHANNEL");
		
		FROM("PLT_ORDER_INFO o, PLT_USER_LABEL u " );
		//活动ID约束
		WHERE("o.ACTIVITY_SEQ_ID=#{activityId}");
		
		StringBuilder orgSql = new StringBuilder("( 1=0 ");
		@SuppressWarnings("unchecked")
		Map<String,String> receives = (Map<String, String>)pama.get("receives");
		for(String path:receives.keySet()){
			//如果角色是人
			if("1".equals(receives.get(path))){
				String[] paths = path.split(",");
				orgSql.append("OR ( o.ORG_PATH='").append(paths[0]).append("' AND o.WENDING_FLAG='").append(paths[1]).append("')");
			}else{
				orgSql.append("OR ( o.ORG_PATH='").append(path).append("' AND o.WENDING_FLAG IS NULL )");
			}
		}
		orgSql.append(" ) ");
		WHERE(orgSql.toString());
		WHERE("(o.ORG_PATH LIKE '"+pama.get("orgPath")+"/%' OR o.ORG_PATH=#{orgPath}) ");
		WHERE("u.PARTITION_FLAG=#{partFlag}");
		WHERE("o.USER_ID=u.USER_ID ");
		WHERE("o.CHANNEL_ID=#{channelId}");
		WHERE("o.ORDER_STATUS='5'");
		WHERE("o.CONTACT_CODE ='0'");
		WHERE("o.TENANT_ID=#{tenantId}");
		WHERE("u.TENANT_ID=#{tenantId}");
		//责任人 占位字段
		if(!StringUtil.validateStr(pama.get("phoneNum"))){
			WHERE("o.PHONE_NUMBER LIKE '"+pama.get("phoneNum")+"%'");
		}
		if(!StringUtil.validateStr(pama.get("userStatus"))){
			WHERE("u.USER_STATUS=#{userStatus}");
		}
		if(!StringUtil.validateStr(pama.get("rentFeeStart"))){
			WHERE("u.MB_ARPU>=#{rentFeeStart}");
		}
		if(!StringUtil.validateStr(pama.get("rentFeeEnd"))){
			WHERE("u.MB_ARPU<=#{rentFeeEnd}");
		}
		if(!StringUtil.validateStr(pama.get("onlineLongStart"))){
			WHERE("u.MB_ONLINE_DUR/1>=#{onlineLongStart}");
		}
		if(!StringUtil.validateStr(pama.get("onlineLongEnd"))){
			WHERE("u.MB_ONLINE_DUR/1<=#{onlineLongEnd}");
		}
		if(!StringUtil.validateStr(pama.get("areaNo"))){
			WHERE("u.AREA_ID=#{areaNo}");
		}
		if(!StringUtil.validateStr(pama.get("cityId"))){
			WHERE("u.CITY_ID=#{cityId}");
		}
		if(!StringUtil.validateStr(pama.get("towns"))){
			WHERE("u.TOWNS=#{towns}");
		}
		if(!StringUtil.validateStr(pama.get("netChannel"))){
			WHERE("u.MB_NETIN_CHANNEL=#{netChannel}");
		}
        String LIMIT="";
    	 //设置分页参数
        Integer pageSize = Integer.parseInt(pama.get("pageSize")+"");
        Integer pageNum = Integer.parseInt(pama.get("pageNum")+"");
        LIMIT = " LIMIT "+(pageNum-1)*pageSize+" , "+pageSize;
        return SQL()+LIMIT;
	}
	
	
	public String belongPreDivide(RulePreReq pama){
		StringBuilder orgPaths = new StringBuilder();
		for(String org:pama.getReceiveOrgs().keySet()){
			orgPaths.append("'").append(org).append("',");
		}
		BEGIN();
		SELECT("o.USER_PATH acceptPath");	//归属人
		SELECT("COUNT(*) divideNum");		//分配的工单数
        FROM("PLT_ORDER_INFO o ");
        WHERE("o.TENANT_ID=#{tenantId}");
        WHERE("o.ORG_PATH=#{orgPath}");
        WHERE("o.ACTIVITY_SEQ_ID=#{activityId}");
        WHERE("o.ORDER_STATUS='5'");
        WHERE("o.CONTACT_CODE='0'");	//如果工单上的工单归属执行者上面的话理论上肯定是 0
        WHERE("o.USER_PATH IN (" + orgPaths.substring(0, orgPaths.length()-1)+") ");
        GROUP_BY("o.USER_PATH");
        return SQL();
	}
	
}
