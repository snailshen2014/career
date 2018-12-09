package com.bonc.busi.track.mapper;
import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY;
import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

import java.util.HashMap;

import com.bonc.utils.IContants;

public class TrackProvider {

	private Boolean validateStr(Object req){
		return null==req||"".equals(req+"");
	}
	
	public String getActivitySet(HashMap<String, Object> req){
		BEGIN();
		//查询出可能有的批次，以及批次可能所在表的标识
		SELECT("REC_ID,ACTIVITY_STATUS,DATE_FORMAT(ORDER_BEGIN_DATE,'%Y%m') ORDER_BEGIN_DATE,"
				+ " DATE_FORMAT(ORDER_END_DATE,'%Y%m') ORDER_END_DATE,ORDER_END_DATE>CURDATE() MAX_END");
		FROM("PLT_ACTIVITY_INFO");
		WHERE("TENANT_ID=#{tenantId}");
		if(!validateStr(req.get("activityId"))){
			WHERE("ACTIVITY_ID=#{activityId}");
		}
		if(!validateStr(req.get("activitySeqId"))){
			WHERE("REC_ID=#{activitySeqId}");
		}
		if(!validateStr(req.get("beginDateStart"))){
			WHERE("ORDER_BEGIN_DATE>=#{beginDateStart}");
		}
		if(!validateStr(req.get("beginDateEnd"))){
			WHERE("ORDER_END_DATE<=#{beginDateEnd}");
		}
		ORDER_BY("ORDER_BEGIN_DATE DESC");
		return SQL();
	}
	
	public String getDxActivitySet(HashMap<String, Object> req){
		BEGIN();
		//查询出可能有的批次，以及批次可能所在表的标识
		SELECT("a.REC_ID,a.ACTIVITY_STATUS,DATE_FORMAT(a.ORDER_BEGIN_DATE,'%Y%m') ORDER_BEGIN_DATE,"
				+ " DATE_FORMAT(a.ORDER_END_DATE,'%Y%m') ORDER_END_DATE,a.ORDER_END_DATE>CURDATE() MAX_END,"
				+ " s.STATUS");
		FROM("PLT_ACTIVITY_INFO a,PLT_ACTIVITY_CHANNEL_STATUS s");
		WHERE("a.TENANT_ID=#{tenantId}");
		WHERE("s.TENANT_ID=#{tenantId}");
		WHERE("a.REC_ID=s.ACTIVITY_SEQ_ID");
		WHERE("s.CHANNEL_ID=#{channelId}");
		if(!validateStr(req.get("activityId"))){
			WHERE("a.ACTIVITY_ID=#{activityId}");
		}
		if(!validateStr(req.get("activitySeqId"))){
			WHERE("a.REC_ID=#{activitySeqId}");
		}
		if(!validateStr(req.get("beginDateStart"))){
			WHERE("a.ORDER_BEGIN_DATE>=#{beginDateStart}");
		}
		if(!validateStr(req.get("beginDateEnd"))){
			WHERE("a.ORDER_END_DATE<=#{beginDateEnd}");
		}
		ORDER_BY("a.ORDER_BEGIN_DATE DESC");
		return SQL();
	}
	
	public String countContactTrack(HashMap<String, Object> req){
		String channelId = req.get("channelId")+"";
		BEGIN();
		SELECT("COUNT(1)");
		//根据生失效状态判断表名称
		FROM(req.get("tableName"+req.get("activityStatus"))+"");
		WHERE("TENANT_ID=#{tenantId}");
		WHERE("ACTIVITY_SEQ_ID IN "+("1".equals(req.get("activityStatus"))?req.get("effectRec"):req.get("invalidRec")));
		//默认查询状态是5的工单明细
		if(null==req.get("orderStatus")){
			req.put("orderStatus", 5);
		}
		WHERE("ORDER_STATUS=#{orderStatus}");
		WHERE("CHANNEL_ID=#{channelId}");
		if(!validateStr(req.get("contactDateStart"))){
			WHERE("CONTACT_DATE>=#{contactDateStart}");
		}
		if(!validateStr(req.get("contactDateEnd"))){
			WHERE("CONTACT_DATE<=#{contactDateEnd}");
		}
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		if(!validateStr(req.get("contactCode"))){
			Integer contactCode= (Integer)req.get("contactCode");
			if(contactCode==2){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,102,103,104,121)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=2");
				}else if(IContants.TC_CHANNEL_1.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.TC_CHANNEL_2.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0201,0202)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (1101,1102,1100,1000)");
				}
			}
			if(contactCode==0){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (201,202,203,204,0)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE <> 2");
				}else if(IContants.TC_CHANNEL_1.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.TC_CHANNEL_2.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0203,0)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}
			}
		}
		return SQL();
	}
	
	public String listContactTrack(HashMap<String, Object> req){
		BEGIN();
		SELECT("ACTIVITY_SEQ_ID,ORG_PATH,CHANNEL_ID,PHONE_NUMBER,CONTACT_CODE,"
				+ "DATE_FORMAT(CONTACT_DATE,'%Y-%m-%d %H:%i:%s') CONTACT_DATE,'工单内容' CONTACT_CONENT,PROV_ID,AREA_NO,CITYID,"
				+ "DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE");
		FROM(req.get("tableName"+req.get("activityStatus"))+"");
		WHERE("TENANT_ID=#{tenantId}");
		WHERE("ACTIVITY_SEQ_ID IN "+("1".equals(req.get("activityStatus"))?req.get("effectRec"):req.get("invalidRec")));
		//默认查询状态是5的工单明细
		if(null==req.get("orderStatus")){
			req.put("orderStatus", 5);
		}
		WHERE("CHANNEL_ID=#{channelId}");
		WHERE("ORDER_STATUS=#{orderStatus}");
		if(!validateStr(req.get("contactDateStart"))){
			WHERE("CONTACT_DATE>=#{contactDateStart}");
		}
		if(!validateStr(req.get("contactDateEnd"))){
			WHERE("CONTACT_DATE<=#{contactDateEnd}");
		}
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		if(!validateStr(req.get("contactCode"))){
			String channelId = req.get("channelId")+"";
			Integer contactCode= (Integer)req.get("contactCode");
			if(contactCode==2){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,102,103,104,121)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=2");
				}else if(IContants.TC_CHANNEL_1.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.TC_CHANNEL_2.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0201,0202)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (1101,1102,1100,1000)");
				}
			}
			if(contactCode==0){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (201,202,203,204,0)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE <> 2");
				}else if(IContants.TC_CHANNEL_1.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.TC_CHANNEL_2.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0203,0)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}
			}
		}
		String SQL = SQL()+" LIMIT "+((Integer)req.get("pageSize"))*((Integer)req.get("pageNum")-1)+","+req.get("pageSize");
		return SQL;
	}
	
	public String countOrderRecord(HashMap<String, Object> req){
		BEGIN();
		SELECT("COUNT(1)");
		FROM(req.get("tableName")+"");
		WHERE("TENANT_ID=#{tenantId}");
		WHERE("ACTIVITY_SEQ_ID=#{activitySeqId}");
		WHERE("CHANNEL_ID=#{channelId}");
		
		if(!validateStr(req.get("beginDateStart"))){
			WHERE("BEGIN_DATE>=#{beginDateStart}");
		}
		if(!validateStr(req.get("beginDateEnd"))){
			WHERE("BEGIN_DATE<=#{beginDateEnd}");
		}
		
		if(!validateStr(req.get("updateDateStart"))){
			WHERE("LAST_UPDATE_TIME>=#{updateDateStart}");
		}
		if(!validateStr(req.get("updateDateEnd"))){
			WHERE("LAST_UPDATE_TIME<=#{updateDateEnd}");
		}
		
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		
		if(!validateStr(req.get("contactCode"))){
			String channelId = req.get("channelId")+"";
			Integer contactCode= (Integer)req.get("contactCode");
			if(contactCode==2){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,102,103,104,121)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=2");
				}else if(IContants.TC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0201,0202)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (1101,1102,1100,1000)");
				}
			}
			if(contactCode==0){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (201,202,203,204,0)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0,1,3)");
				}else if(IContants.TC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0203,0)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}
			}
		}
		return SQL();
	}
	
	public String listOrderRecord(HashMap<String, Object> req){
		BEGIN();
		SELECT("DATE_FORMAT(END_DATE,'%Y-%m-%d %H:%i:%s') END_DATE,"
				+ " DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') BEGIN_DATE,"
				+ " CHANNEL_ID,ORG_PATH,PHONE_NUMBER,"
				+ " DATE_FORMAT(LAST_UPDATE_TIME,'%Y-%m-%d %H:%i:%s') LAST_UPDATE_TIME,'新增' UPDATE_TYPE,'工单内容' UPDATE_CONTENT,PROV_ID,AREA_NO,CITYID");
		FROM(req.get("tableName")+"");
		WHERE("TENANT_ID=#{tenantId}");
		WHERE("ACTIVITY_SEQ_ID=#{activitySeqId}");
		WHERE("CHANNEL_ID=#{channelId}");
		
		if(!validateStr(req.get("beginDateStart"))){
			WHERE("BEGIN_DATE>=#{beginDateStart}");
		}
		if(!validateStr(req.get("beginDateEnd"))){
			WHERE("END_DATE<=#{beginDateEnd}");
		}
		
		if(!validateStr(req.get("updateDateStart"))){
			WHERE("LAST_UPDATE_TIME>=#{updateDateStart}");
		}
		if(!validateStr(req.get("updateDateEnd"))){
			WHERE("LAST_UPDATE_TIME<=#{updateDateEnd}");
		}
		
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		
		if(!validateStr(req.get("contactCode"))){
			String channelId = req.get("channelId")+"";
			Integer contactCode= (Integer)req.get("contactCode");
			if(contactCode==2){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,102,103,104,121)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=2");
				}else if(IContants.TC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (101,121)");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0201,0202)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (1101,1102,1100,1000)");
				}
			}
			if(contactCode==0){
				if(IContants.YX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (201,202,203,204,0)");
				}else if(IContants.DX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0,1,3)");
				}else if(IContants.TC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}else if(IContants.ST_CHANNEL.equals(channelId)||IContants.WT_CHANNEL.equals(channelId)||IContants.WSC_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE IN (0203,0)");
				}else if(IContants.WX_CHANNEL.equals(channelId)){
					WHERE("CONTACT_CODE=0");
				}
			}
		}
		String LIMIT = "LIMIT "+((Integer)req.get("pageNum")-1)*(Integer)req.get("pageSize")+","+req.get("pageSize");
		return SQL()+LIMIT;
	}
	
	public String getChannelNums(HashMap<String, Object> req){
		BEGIN();
		SELECT(" a.ACTIVITY_ID,s.CHANNEL_ID,s.TENANT_ID,"
			+ " SUM(s.ALL_COUNT)+SUM(s.VALID_NUM) ALL_COUNT," //初始工单数
			+ " SUM(s.ALL_COUNT) FILTER_COUNT, " // 过滤掉的工单数
			+ " SUM(s.FILTER2_COUNT) BLACK_FILTER_COUNT,"// 黑名单过滤数
			+ " SUM(s.FILTER3_COUNT) SUCCESS_FILTER_COUNT,"// 成功过滤数
			+ " SUM(s.FILTER0_COUNT)+SUM(s.FILTER1_COUNT) RULE_FILTER_COUNT,"// 规则过滤数
			+ " SUM(s.VALID_NUM) VALID_COUNT ");
		FROM(" PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a ");
		WHERE(" s.TENANT_ID=#{tenantId} ");
		WHERE(" a.TENANT_ID=#{tenantId} ");
		if(!validateStr(req.get("activityId"))){
			WHERE("a.ACTIVITY_ID=#{activityId}");
		}
		if(!validateStr(req.get("activitySeqId"))){
			WHERE("a.REC_ID=#{activitySeqId}");
		}
		WHERE(" a.REC_ID=s.ACTIVITY_SEQ_ID ");
		GROUP_BY(" s.CHANNEL_ID ");	
		return SQL();
	}
	
	
	public String countupdatehistory(HashMap<String, Object> req){
		BEGIN();
		SELECT(" COUNT(1) ");
		FROM(" PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a ");
		WHERE(" s.TENANT_ID=#{tenantId} ");
		WHERE(" a.TENANT_ID=#{tenantId} ");
		if(!validateStr(req.get("activityId"))){
			WHERE("a.ACTIVITY_ID=#{activityId}");
		}
		if(!validateStr(req.get("activitySeqId"))){
			WHERE("a.REC_ID=#{activitySeqId}");
		}
		WHERE(" a.REC_ID=s.ACTIVITY_SEQ_ID ");
		WHERE(" s.CHANNEL_ID=#{channelId} ");
		return SQL();
	}
	
	public String updatehistory(HashMap<String, Object> req){
		BEGIN();
		SELECT(" '新增工单' UPDATE_TYPE,s.VALID_NUM UPDATE_NUM,s.CHANNEL_ID,s.ACTIVITY_SEQ_ID, "
			+ " a.ORDER_BEGIN_DATE UPDATE_DATE,a.ORDER_BEGIN_DATE VALIDED_DATE,a.ACTIVITY_STATUS ");
		FROM(" PLT_ORDER_DETAIL_COUNT s,PLT_ACTIVITY_INFO a ");
		WHERE(" s.TENANT_ID=#{tenantId} ");
		WHERE(" a.TENANT_ID=#{tenantId} ");
		if(!validateStr(req.get("activityId"))){
			WHERE("a.ACTIVITY_ID=#{activityId}");
		}
		if(!validateStr(req.get("activitySeqId"))){
			WHERE("a.REC_ID=#{activitySeqId}");
		}
		WHERE(" a.REC_ID=s.ACTIVITY_SEQ_ID ");
		WHERE(" s.CHANNEL_ID=#{channelId} ");
		String LIMIT = "LIMIT "+((Integer)req.get("pageNum")-1)*(Integer)req.get("pageSize")+","+req.get("pageSize");
		return SQL()+LIMIT;
	}
	
	public String countBlack(HashMap<String, Object> req){
		
		BEGIN();
		SELECT(" COUNT(1) ");
		FROM(" PLT_ORDER_INFO_BLACK ");
		WHERE(" TENANT_ID=#{tenantId} ");
		WHERE(" ACTIVITY_SEQ_ID IN "+req.get("recIds"));
		WHERE(" CHANNEL_ID=#{channelId} ");
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		return SQL();
	}
	
	public String listBlack(HashMap<String, Object> req){
		
		BEGIN();
		SELECT(" PROV_ID,AREA_NO,CITYID,CHANNEL_ID,PHONE_NUMBER,DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE ");
		FROM(" PLT_ORDER_INFO_BLACK ");
		WHERE(" TENANT_ID=#{tenantId} ");
		WHERE(" ACTIVITY_SEQ_ID IN "+req.get("recIds"));
		WHERE(" CHANNEL_ID=#{channelId} ");
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		String LIMIT = "LIMIT "+((Integer)req.get("pageNum")-1)*(Integer)req.get("pageSize")+","+req.get("pageSize");
		return SQL()+LIMIT;
	}
	
	 public String countRuleFilter(HashMap<String, Object> req){
		
		BEGIN();
		SELECT(" COUNT(1) ");
		FROM(req.get("tableName")+"");
		
		WHERE(" TENANT_ID=#{tenantId} ");
		WHERE(" ACTIVITY_SEQ_ID IN "+req.get("recIds"));
		WHERE(" CHANNEL_ID=#{channelId} ");
		WHERE(" ORDER_STATUS NOT IN (6,5) ");
		
		if(!validateStr(req.get("phoneNumber"))){
			req.put("phoneNumber", req.get("phoneNumber")+"%");
			WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
		}
		if(!validateStr(req.get("cityId"))){
			WHERE(" CITYID = #{cityId} ");
		}
		if(!validateStr(req.get("areaNo"))){
			WHERE(" AREA_NO = #{areaNo} ");
		}
		if(!validateStr(req.get("provId"))){
			WHERE(" PROV_ID = #{provId} ");
		}
		return SQL();
	}
	 
	 public String getFilterList(HashMap<String, Object> req){
			
			BEGIN();
			SELECT(" PROV_ID,AREA_NO,CITYID,CHANNEL_ID,PHONE_NUMBER,DATE_FORMAT(BEGIN_DATE,'%Y-%m-%d %H:%i:%s') ORDER_BEGIN_DATE ");
			FROM(req.get("tableName")+"");			

			WHERE(" TENANT_ID=#{tenantId} ");
			WHERE(" ACTIVITY_SEQ_ID IN "+req.get("recIds"));
			WHERE(" CHANNEL_ID=#{channelId} ");
			WHERE(" ORDER_STATUS NOT IN (6,5) ");
			
			if(!validateStr(req.get("phoneNumber"))){
				req.put("phoneNumber", req.get("phoneNumber")+"%");
				WHERE("PHONE_NUMBER LIKE #{phoneNumber}");
			}
			if(!validateStr(req.get("cityId"))){
				WHERE(" CITYID = #{cityId} ");
			}
			if(!validateStr(req.get("areaNo"))){
				WHERE(" AREA_NO = #{areaNo} ");
			}
			if(!validateStr(req.get("provId"))){
				WHERE(" PROV_ID = #{provId} ");
			}
			
			String LIMIT = "LIMIT "+((Integer)req.get("pageNum")-1)*(Integer)req.get("pageSize")+","+req.get("pageSize");
			return SQL()+LIMIT;
	}
	 
	public static void main(String[] args) {
		HashMap<String, Object> req = new HashMap<String, Object>();
		req.put("tableName", "123");
		req.put("recIds", "123");
		req.put("tenantId", "123");
		req.put("recchannelIdIds", "123");
		req.put("pageNum", 123);
		req.put("pageSize", 123);
		System.out.println(new TrackProvider().getFilterList(req));
	}
}
