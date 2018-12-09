package com.bonc.busi.sys.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.sys.entity.PltActivityInfo;
import com.bonc.busi.sys.mapper.ActivityInfoMapper;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.mapper.TelecomOrderMonitorMapper;
import com.bonc.busi.sys.service.TelecomOrderMonitorService;
import com.bonc.utils.HttpUtil;

/**
 * @author Administrator
 *
 */
@Service
public class TelecomOrderMonitorServiceImpl implements TelecomOrderMonitorService {
	
	private static final Logger logger = LoggerFactory.getLogger(TelecomOrderMonitorServiceImpl.class);
	
	@Autowired
	TelecomOrderMonitorMapper mapper;
	
	@Autowired
	SysMapper sysMapper;
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
	@Autowired
    private ActivityInfoMapper activityInfoMapper;
	
	
	private final String ACTIVITY_INFO_URL_KEY = "ACTIVITY_INFO";
	
	private List<String> activityIds = new  ArrayList<String>();

	@Override
	public Map<String, Object> queryOrderStateById(String tenantId) {
		Map<String, Object> map = new HashMap<String,Object>();
		activityIds = getNeedRunActivityIds(tenantId); //可以跑的所有活动列表
		//查询当前时间正在跑的活动
		String  runningActivityId = mapper.queryRunningActivity(tenantId);
		//保存待跑的活动
		List<String> waitingActivityIds = new ArrayList<String>();
		//保存已经跑完的活动的信息(活动Id,工单数,最近工单生成时间)
		List<Map<String, Object>> finishedActivityInfo = new ArrayList<Map<String, Object>>();
		activityIds.remove(runningActivityId);//从列表里移除正在跑的活动	
		waitingActivityIds = this.queryWaitingActivityIds(activityIds);
		finishedActivityInfo = this.queryFinishedActivityInfo(activityIds,tenantId);
		map.put("running", runningActivityId !=null ? runningActivityId:"");
		map.put("waiting", waitingActivityIds);
		map.put("finished", finishedActivityInfo);
		return map;
	}
	
	/**
	 * 可跑活动去掉正在跑的活动之后就是待跑活动
	 * @return
	 */
	public List<String> queryWaitingActivityIds(List<String> activityIds){
		return activityIds;
	}
	
	/**
	 * 已经跑完的活动的信息
	 * @return
	 */
	public List<Map<String, Object>> queryFinishedActivityInfo(List<String> activityIds,String tenantId){
		List<Map<String, Object>> finishedActivityInfo = new ArrayList<Map<String, Object>>();
		if(activityIds !=null && activityIds.size()>0){
			//activityIds 转换成('','','')的形式
			String actIds = transForm(activityIds);
			List<Map<String, Object>> activityMapList = mapper.queryActivityInfoMap(actIds,tenantId);
			for(Map<String, Object> map : activityMapList){
				String activityId = (String) map.get("ACTIVITY_ID");
				int    activitySeqId = 0;
				try{
					activitySeqId = ((Long)map.get("REC_ID")).intValue();
				}catch(Exception ex){
					activitySeqId = (int) map.get("REC_ID");
				}
				Timestamp lastOrderCreateTime = (Timestamp) map.get("LAST_ORDER_CREATE_TIME");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = sdf.format(lastOrderCreateTime);
				//根据批次查询工单数：查询plt_activity_process_log表
				int orderCount = mapper.queryOrderCount(tenantId,activitySeqId,activityId);
				Map<String, Object> info = new HashMap<String,Object>();
				info.put("activityId", activityId);
				info.put("orderCount", orderCount);
				info.put("lastOrderGenTime", time);
				finishedActivityInfo.add(info);
			}
		}
		return finishedActivityInfo;
	}
	
	/**
	 * @param activityIds2
	 * @return
	 */
	private String transForm(List<String> activityIds) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = "'a'";
		if(activityIds != null && activityIds.size()>0){
			for(String activityId :activityIds){
				stringBuffer.append("'");
				stringBuffer.append(activityId);
				stringBuffer.append("'");
				stringBuffer.append(",");		
			}
		}
		if(stringBuffer.length()>2){
			temp = stringBuffer.substring(0, stringBuffer.length()-1);
		}
		return "("+ temp + ")"; 
	}

	/**
	 * 查询服务，获取所有的活动列表
	 * @return
	 */
	public List<String> getAllActivityId(String tenantId){
		List<String> activityIds = new ArrayList<String>();
		String url = sysMapper.getSystemValueByKey(ACTIVITY_INFO_URL_KEY);
		if (StringUtils.isNotBlank(url)) {
			HashMap<String, Object> reqMap = new HashMap<String, Object>();
			// 判断服务类型 电信或者联通
			String type = sysMapper.getSystemValueByKey("SERVICE_PROVIDER_TYPE");
			if (type != null && type.trim().equals("1")) {
				reqMap.put("tenant_id", tenantId);
				String resp = HttpUtil.sendPost(url, JSON.toJSONString(reqMap));
				Map datamap1 = JSON.parseObject(resp, Map.class);
				String data = (String) datamap1.get("data");
				if (data != null) {
					try {
						Map<String, Map> datamap = JSON.parseObject(data, Map.class);
						List<Map> list = (List<Map>) datamap.get("activityList");
						if (list != null && list.size() > 0) {
							for (Map mapTemp : list) {
								String activityId = (String) mapTemp.get("ACTIVITY_ID");
								activityIds.add(activityId);
							}
						}
					} catch (Exception ex) {
						resp = ex.getMessage();
						System.out.println("调用服务获取活动列表出错了: " + resp);
					}
				}
			}	
		}
		return activityIds;
	}
	
	/**
	 * 获取所有的可以跑的活动列表
	 * @param activityIds
	 * @return
	 */
	public List<String> getNeedRunActivityIds(String tenantId){
		List<String> needRunActivityIds = new ArrayList<String>();
		List<String> activityIds = getAllActivityId(tenantId);
		if(activityIds !=null && activityIds.size()>0){
			for(String activityId : activityIds){
				boolean needRun = isNeedRun(activityId,tenantId);
				 if(needRun){
					 needRunActivityIds.add(activityId);
				 }
			}
			 
		}
		return needRunActivityIds;
	}

	/**
	 * 判断活动是否需要跑工单
	 * @param activityId
	 * @param tenantId
	 * @return
	 */
	private boolean isNeedRun(String activityId, String tenantId) {
		boolean needRun = true;
		//根据活动Id获取活动报文
		String activityDetailUrl = sysMapper.getSystemValueByKey("ACTIVITY_INFO_DETAIL");
		String activityDetailInfo = "";
		if (StringUtils.isNotBlank(activityDetailUrl)) {
			String type = sysMapper.getSystemValueByKey("SERVICE_PROVIDER_TYPE");
			if(type !=null && type.trim().equals("1")){
				HashMap<String, String> submap = new HashMap<String, String>();
				// 由于电信组使用的框架导致在调用活动详情服务接口时必须把参数按照调用的服务接口参数再封装一次
				Map<String, Object> activityDetailRequestMap = new HashMap<String, Object>();
				// submap.put("activityId", act);
				submap.put("activity_id", activityId);
				submap.put("tenant_id", tenantId);
				activityDetailRequestMap.put("req", JSON.toJSON(submap));
				activityDetailInfo = HttpUtil.doPost(activityDetailUrl, activityDetailRequestMap);
			}else {
	            HashMap<String, Object> reqMap = new HashMap<String, Object>();
	            reqMap.put("tenantId", tenantId);
	            reqMap.put("activityId", activityId);
	            activityDetailInfo = HttpUtil.doGet(activityDetailUrl, reqMap);
	        }
			if (activityDetailInfo == null || activityDetailInfo.equals("") || activityDetailInfo.equals("ERROR")) {
				logger.error("[OrderCenter] getActivityDetail respond error..................!!!");
				return false;
			}
			JSONObject activityObj = JSONObject.parseObject(activityDetailInfo);
			String activityState = activityObj.getString("state");//获取活动的状态
			String activityType = activityObj.getString("activityType");
			String activityEndDate = activityObj.getString("endDate");
			String orderAppointDateStr = activityObj.getString("orderAppointDate");
			Integer orderAppointDate = 1;
			if(StringUtils.isNotBlank(orderAppointDateStr)){
				orderAppointDate = Integer.valueOf(orderAppointDateStr);
			}
			if (activityState !=null && !activityState.equals("05")) {
				logger.info("[Ordertask2.0] Activity id=" + activityId + ":activity status!=05 no need run.");
	            return false;
	        }
	        if (isActivityNeedRun(activityId, tenantId, Integer.parseInt(activityType), activityEndDate,orderAppointDate) == -1) {
	            return false;
	        }
	        return true;	
		}
		return false;
	}

	/**
     * judge is activity need running by activityCycle
     *
     * @param activity                 id
     * @param tenant_id
     * @param flag:activity            cycle
     * @param activityEndDate:activity end date
     * @return -1:no run,0:run
     */
    private int isActivityNeedRun(String activity, String tenant_id, int flag, String activityEndDate,Integer orderAppointDate) {
        // 1、月，2、日，3、一次性",
        PltActivityInfo act = new PltActivityInfo();
        act.setACTIVITY_ID(activity);
        act.setTENANT_ID(tenant_id);
        String create_time = activityInfoMapper.isActivityRun(act);
        // new activity
        if (create_time == null) {
            return 0;
        }
        // when activity expire ,the activity can not run
        if (activityEndDate != null) {
            String now = getCurrentTime("yyyy-MM-dd");
            if (activityEndDate.compareTo(now) < 0) {
                return -1;
            }
        }
        int rtn = -1;
        switch (flag) {
            case 3:// one month one time
                String actmonth = create_time.substring(0, 6);
//                String nowmonth = getCurrentTime("yyyyMM");
                String nowdate = getCurrentTime("yyyyMMdd");
                String nowmonth = nowdate.substring(0, 6);
                //周期性判断账期
//                if(!judgeDateIdIsChanged(activity,tenant_id)) return -1;
                if (null != orderAppointDate  && orderAppointDate>0 && orderAppointDate<32){
                    Integer day = new Integer(nowdate.substring(6,8));
                    if (!actmonth.equals(nowmonth)  && day >= orderAppointDate)
                        rtn = 0;
                    else{
                    	break;
                    }
                }else {
                    if (!actmonth.equals(nowmonth))
                        rtn = 0;
                }
                break;
            case 1:// day
                String actday = create_time.substring(0, 8);
                String nowday = getCurrentTime("yyyyMMdd");
                //周期性判断账期
//                if(!judgeDateIdIsChanged(activity,tenant_id)) return -1;
                if (!actday.equals(nowday))
                    rtn = 0;
                break;
            case 2:// only one time
                rtn = -1;
                break;
            default:
                return -1;
        }

//        System.out.println("[Ordertask2.0] Activity id=" + activity + ",teantid=" + tenant_id + "activityType=" + flag + ",return=" + rtn);
        return rtn;
    }
    
    public String getCurrentTime(String formater) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(formater);// 可以方便地修改日期格式
        return dateFormat.format(now);
    }
}
