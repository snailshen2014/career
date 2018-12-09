package com.bonc.busi.backpage.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.busi.sys.dao.SyscommcfgDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.backpage.BackPageService;
import com.bonc.busi.backpage.bo.ActivityPo;
import com.bonc.busi.backpage.bo.ActivityStatistics;
import com.bonc.busi.backpage.bo.CreateTenantBo;
import com.bonc.busi.backpage.bo.OrderStatus;
import com.bonc.busi.backpage.bo.StaticsticCompator;
import com.bonc.busi.backpage.mapper.BackPageMapper;
import com.bonc.busi.backpage.mapper.BaseFunc;
import com.bonc.busi.sys.entity.ActivityStatus;
import com.bonc.utils.HttpUtil;


/**
 * Created by MQZ on 2017/8/17.
 */

@Service("BackPageService")
public class BackPageServiceImpl implements BackPageService {
    private final static Logger log = LoggerFactory.getLogger(BackPageServiceImpl.class);
    @Autowired
    BackPageMapper mapper;
    @Autowired
    BaseFunc baseFunc;
    @Autowired
    SyscommcfgDao SyscommcfgDao;

    String LocalPath = BackPageService.class.getClassLoader().getResource("").getPath()+"/initSql/";
//    String LocalPath = System.getProperty("user.dir") + "\\initSql\\";
    String StructureSql = LocalPath + "StructureSql.sql";
    String DianxinData = LocalPath + "dianxinData.sql";
    String LiantongData = LocalPath + "liantongData.sql";
    String DianxinywData = LocalPath + "dianxinywData.sql";
    String LiantongywData = LocalPath + "liantongywData.sql";
    String BaseAppendDynamic = LocalPath + "baseAppendDynamic.sql";
    String BaseAppendStatic = LocalPath + "baseAppendStatic.sql";
    String BaseFullStatic = LocalPath + "baseFullStatic.sql";
    String BaseUrl = LocalPath + "baseUrl.sql";

    @Override
    public Object getUsedTableList(String tenantId) {
        HashMap<String, Object> resp = new HashMap<>();
        resp.put("total", 55);
        resp.put("rows", mapper.getUsedTableList(tenantId));
        return resp;
    }

    @Override
    public List<Map<String, Object>> getValidTenantInfo() {
        return mapper.getValidTenantInfo();
    }

    @Override
    public List<HashMap> getSysCfg() {
        return mapper.getSysCfg();
    }

    @Override
    public void delCfgRow(String key) {
        mapper.delCfgRow(key);
    }

    @Override
    public void insertOrUpdateCfg(Map cfgMap) {
        String key = (String) cfgMap.get("CFG_KEY");
        // 先去cfg查key 如果没有插入 如果有更新
        Integer count = mapper.getSysCfgByKey(key);
        if (count != null && count > 0) {
            mapper.updateCfg(cfgMap);
        } else {
            mapper.insertCfg(cfgMap);
        }
    }

    @Override
    public void simpleInitTenantData(CreateTenantBo cfg) {
        // 默认属性
        cfg.setIsFullInit("false");
        cfg.setIsYW("false");
        cfg.setIsLiantong("true");
        cfg.setIsStructure("true");
        cfg.setIsliantongData("true");
        cfg.setIsdianxinywData("false");
        cfg.setIsdianxinData("false");
        cfg.setIsliantongywData("false");
        cfg.setMYSQL_PASS("TODO");
        cfg.setMYSQL_USER("TODO");
        cfg.setMYSQL_Url("TODO");
        cfg.setXCloud_USER("TODO");
        cfg.setXCloud_Url("TODO");
        cfg.setXCloud_PASS("TODO");
        cfg.setFTP_Url("TODO");
        cfg.setFTP_Port("21");
        cfg.setFTP_PASS("TODO");
        cfg.setFTP_USER("TODO");
        initTenantData(cfg);

    }

    @Override
    public void initTenantData(CreateTenantBo cfg) {
        log.info("工单数据初始化开始参数为:{}", JSON.toJSON(cfg));
        String TenantId = cfg.getTenantId();
        // ----------业务库的表结构建立----------
        if ("true".equals(cfg.getIsStructure())) {
            String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, StructureSql);
            baseFunc.executeDdlOnMycat(tempSQLPath);
            baseFunc.deleteFile(tempSQLPath);
        }
        // ----------业务库的表静态数据建立----------
        if ("true".equals(cfg.getIsdianxinData())) {
            String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, DianxinData);
            baseFunc.executeDdlOnMycat(tempSQLPath);
            baseFunc.deleteFile(tempSQLPath);
        }
        if ("true".equals(cfg.getIsliantongData())) {
            String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, LiantongData);
            baseFunc.executeDdlOnMycat(tempSQLPath);
            baseFunc.deleteFile(tempSQLPath);
        }
        if ("true".equals(cfg.getIsdianxinywData())) {
            String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, DianxinywData);
            baseFunc.executeDdlOnMycat(tempSQLPath);
            baseFunc.deleteFile(tempSQLPath);
        }
        if ("true".equals(cfg.getIsliantongywData())) {
            String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, LiantongywData);
            baseFunc.executeDdlOnMycat(tempSQLPath);
            baseFunc.deleteFile(tempSQLPath);
        }
        // ----------base库 SYS_CFG表数据录入----------
        // ----------base库 全量静态数据----------
        if ("true".equals(cfg.getIsFullInit())) {
            baseFunc.executeDdlOnMycat(BaseFullStatic);
            String tempSQLPath1 = baseFunc.replaceSQLbyURL(TenantId, BaseUrl, cfg);
            baseFunc.executeDdlOnMycat(tempSQLPath1);
            baseFunc.deleteFile(tempSQLPath1);
            // ------------更新本网或者异网 联通或者电信
            if ("true".equals(cfg.getIsLiantong())) {
                Map map = new HashMap();
                map.put("CFG_KEY", "SERVICE_PROVIDER_TYPE");
                map.put("CFG_VALUE", "0");
                map.put("NOTE", "区分联通和电信 1为电信 0为联通");
                mapper.updateCfg(map);
            }
            if ("true".equals(cfg.getIsLiantong())) {
                Map map = new HashMap();
                map.put("CFG_KEY", "SERVICE_IS_YW");
                map.put("CFG_VALUE", "1");
                map.put("NOTE", "区分本网和异网 1为异网 0为本网");
                mapper.updateCfg(map);
            }
        }
        // ----------base库 追加静态数据----------
        String tempSQLPath = baseFunc.replaceSQLbyTenantId(TenantId, BaseAppendStatic);
        baseFunc.executeDdlOnMycat(tempSQLPath);
        baseFunc.deleteFile(tempSQLPath);
        // ----------base库 追加动态数据----------
        tempSQLPath = baseFunc.replaceSQLbyParams(TenantId, BaseAppendDynamic, cfg);
        baseFunc.executeDdlOnMycat(tempSQLPath);
        baseFunc.deleteFile(tempSQLPath);
        // --------- 在租户表添加一条记录 -------------
        mapper.addTenantRecord(cfg);
        log.info("工单数据初始化完毕");
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        List<String> activityList =  new ArrayList<String>(); // 保存活动信息ACTIVITY_ID的列表
        reqMap.put("tenant_id", "uni076");                          // 租户Id
        String resp = HttpUtil.sendPost("http://172.16.14.6:8088/epmservices/restful/activity/ActivityRuleList", JSON.toJSONString(reqMap));
        Map datamap1 = JSON.parseObject(resp, Map.class);
        String data = (String) datamap1.get("data");
        if (data != null) {
            try {
                Map<String,Map> datamap = JSON.parseObject(data, Map.class);
                List<Map> activityDianxinList = (List<Map>) datamap.get("activityList");
                for (Map mapTemp : activityDianxinList) {
                    String activityDianxinId = (String) mapTemp.get("ACTIVITY_ID");
                    activityList.add(activityDianxinId);
				    /*电信项目-活动执行开始-日志记录结束*/
                }
            } catch (Exception ex) {
                resp = ex.getMessage();
            }
        }
        String activityIds = activityList.toString();
        System.out.println(activityIds);

    }

    /**
     * 获取活动列表信息
     */
	@Override
	public List<ActivityPo> getActivityList(String tenantId, String activityId) {
		List<ActivityPo> list = new ArrayList<ActivityPo>();
		// 活动列表的URL activityId=43277&
		String activityIdsURL = mapper.getCfgValue("ACTIVITY_INFO");  
		activityIdsURL = activityIdsURL+"?tenantId="+tenantId;
		//String activityIdsURL = "http://clyxys.cop.local:8081/cactivityInter/activity/tempActIds?tenantId=" + tenantId;
		RestTemplate restTemplate = new RestTemplate();
        String type = SyscommcfgDao.query("SERVICE_PROVIDER_TYPE");
        String activityIds = null;
        if ("1".equals(type)) {
            HashMap<String, Object> reqMap = new HashMap<String, Object>();
            List<String> activityList =  new ArrayList<String>(); // 保存活动信息ACTIVITY_ID的列表
            reqMap.put("tenant_id", tenantId);                          // 租户Id
            String resp = HttpUtil.sendPost(activityIdsURL, JSON.toJSONString(reqMap));
            Map datamap1 = JSON.parseObject(resp, Map.class);
            String data = (String) datamap1.get("data");
            if (data != null) {
                try {
                    Map<String,Map> datamap = JSON.parseObject(data, Map.class);
                    List<Map> activityDianxinList = (List<Map>) datamap.get("activityList");
                    for (Map mapTemp : activityDianxinList) {
                        String activityDianxinId = (String) mapTemp.get("ACTIVITY_ID");
                        activityList.add(activityDianxinId);
				    /*电信项目-活动执行开始-日志记录结束*/
                    }
                } catch (Exception ex) {
                    resp = ex.getMessage();
                }
            }
            activityIds = JSON.toJSONString(activityList);
        }else {
             activityIds = restTemplate.getForObject(activityIdsURL, String.class);
        }
		if (activityId != null && !activityId.equals("")) { // 查询指定的活动
			ActivityPo activityPo = this.buildActivityPo(tenantId, activityId);
			if (activityPo != null) {
				activityPo.setActivityId(activityId);
				list.add(activityPo);
				return list;
			}
		}
		if (activityIds != null && !activityIds.equals("")) {
			List<String> activityIdList = JSON.parseArray(activityIds, String.class); // 获取到的活动列表	["43037","42923"]
			for (String actId : activityIdList) {
				ActivityPo activityPo = this.buildActivityPo(tenantId, actId);
				if (activityPo != null) {
					activityPo.setActivityId(actId);
					list.add(activityPo);
				}
			}
		}
		return list;
	}

	/**
	 * 查询活动的名称以及活动的工单生成状态：  生成成功|生成失败|生产中
	 * 查询方式： 根据活动Id去查询活动表中最后的批次，然后根据该批次去日志表里查询
	 * @param activityId 活动Id
	 * @return
	 */
	private Map<String, String> getActivityOrderStatus(String tenantId , String activityId){
		Map<String, String> resultMap = new HashMap<String,String>();
		Map<String, Object>  map = mapper.selectActivityLatestSeqIdAndName(tenantId,activityId);
		if(map != null && map.size() != 0 ){
			String orderStatus = OrderStatus.success.toString();
			String activityName = (String) map.get("ACTIVITY_NAME");  //活动名称
			Integer activitySeqId = ((Long) map.get("REC_ID")).intValue();
			List<Integer> busiCode = mapper.selectBusiCode(activityId,activitySeqId,tenantId);
			if(busiCode.contains(0)){  //如果包含0,说明工单生成成功
				orderStatus = OrderStatus.success.toString();
			} else if(busiCode.contains(2001) || busiCode.contains(2003) || busiCode.contains(2004) || busiCode.contains(2005) || busiCode.contains(2010)) {
				orderStatus = OrderStatus.failed.toString();
			} else {
				orderStatus = OrderStatus.running.toString();
			}
			resultMap.put("activityName", activityName);
			resultMap.put("orderStatus", orderStatus);
		}
		return resultMap;
	}

	/**
	 * 构建符合要求的ActivityPo
	 * @param tenantId
	 * @param activityId
	 * @return
	 */
	private ActivityPo buildActivityPo(String tenantId, String activityId) {
		ActivityPo activityPo = null;
		 Map<String, String> map = this.getActivityOrderStatus(tenantId, activityId);
		 if(map != null && map.size() != 0) {
			   activityPo = new ActivityPo();
			   activityPo.setActivityName(map.get("activityName"));
			   activityPo.setOrderStatus(map.get("orderStatus"));
		 }
		return activityPo;
	}

	/**
	 * 得到工单的执行步骤
	 */
	@Override
	public List<Map<String, Object>> getActivityOrderGenerateStep(String activityId, String tenantId) {
		Map<String, Object> map = mapper.selectActivityLatestSeqIdAndName(tenantId, activityId);
		List<Map<String, Object>> orderGenerateSteps = new ArrayList<Map<String, Object>>();
		if ( map != null && map.get("REC_ID") != null) {
			Integer activitySeqId = ((Long) map.get("REC_ID")).intValue(); // 活动的最新批次
			// 拼出类似的Sql: /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE
			// TENANT_ID ='uni076'*/
			String myCatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID =" + "'" + tenantId + "'"
					+ "*/";
			Map<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("myCatSql", myCatSql);
			requestMap.put("activityId", activityId);
			requestMap.put("activitySeqId", activitySeqId);
			requestMap.put("tenantId", tenantId);
			orderGenerateSteps = mapper.selectActivityOrderGenerateSteps(requestMap);
		}
		return orderGenerateSteps;
	}

	/**
	 * 重跑指定的活动
	 * 把指定的活动的相关信息删除后，活动就会重跑： 删除条件： 活动Id + 活动最新的批次  (活动表，process_log表,detail表,execute_interface表)
	 */
	@Override
	public void recycleActivityOrder(String activityId, String tenantId) {
		Map<String, Object> map = mapper.selectActivityLatestSeqIdAndName(tenantId, activityId);
		if (map != null && map.get("REC_ID") != null) {
			Integer activitySeqId = ((Long) map.get("REC_ID")).intValue(); // 活动的最新批次
			Map<String, Object> delMap = new HashMap<String, Object>();
			delMap.put("activityId", activityId);
			delMap.put("activitySeqId", activitySeqId);
			delMap.put("tenantId", tenantId);
			// 删除活动表里的信息
			delMap.put("tableName", "PLT_ACTIVITY_INFO");
			mapper.deleteActivityInfo(delMap);

			// 删除PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE记录
			delMap.put("tableName", "PLT_ACTIVITY_CHANNEL_EXECUTE_INTERFACE");
			mapper.deleteExecuteInterface(delMap);

			// 删除PLT_ACTIVITY_CHANNEL_DETAIL记录
			delMap.put("tableName", "PLT_ACTIVITY_CHANNEL_DETAIL");
			mapper.deleteActivityInfo(delMap);

			// 删除PLT_ACTIVITY_PROCESS_LOG记录
			delMap.put("tableName", "PLT_ACTIVITY_PROCESS_LOG");
			mapper.deleteActivityInfo(delMap);
		}
	}
	
    @Override
    public List<HashMap> XSqlSelect(String tenantId) {
        return mapper.XSqlSelect(tenantId);
    }

    @Override
    public List<HashMap> XSqlTable(String tenantId) {
        return mapper.XSqlTable(tenantId);
    }

    @Override
    public List<HashMap> XSqlWhere(String tenantId) {
        return mapper.XSqlWhere(tenantId);
    }

    @Override
    public void delSelectRow(String key, String tenantId) {
        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */";
        mycatSql = mycatSql.replace("TTTTTENANT_ID", tenantId);
        mapper.delSelectRow(key, mycatSql);
    }

    @Override
    public void delTableRow(String key, String tenantId) {
        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */";
        mycatSql = mycatSql.replace("TTTTTENANT_ID", tenantId);
        mapper.delTableRow(key, mycatSql);
    }

    @Override
    public void delWhereRow(String req, String tenantId) {
        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */";
        mycatSql = mycatSql.replace("TTTTTENANT_ID", tenantId);
        mapper.delWhereRow(req, mycatSql);
    }

    @Override
    public void insertOrUpdateXSQL(Map map) {
        String TENANT_ID = (String) map.get("TENANT_ID");
        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */";
        mycatSql = mycatSql.replace("TTTTTENANT_ID", TENANT_ID);
        map.put("mycatSql", mycatSql);
        String ORDER_COLUMN_SEQ = (String) map.get("ORDER_COLUMN_SEQ");
        String TABLE_SEQ = (String) map.get("TABLE_SEQ");
        String CON_SEQ = (String) map.get("CON_SEQ");
        if (ORDER_COLUMN_SEQ != null && !ORDER_COLUMN_SEQ.equals("")) {
            Integer count = mapper.getSelectByKey(ORDER_COLUMN_SEQ);
            if (count != null && count > 0) {
                mapper.updateSelect(map);
            } else {
                mapper.insertSelect(map);
            }
        }
        if (TABLE_SEQ != null && !TABLE_SEQ.equals("")) {
            Integer count = mapper.getTableByKey(TABLE_SEQ);
            if (count != null && count > 0) {
                mapper.updateTable(map);
            } else {
                mapper.insertTable(map);
            }
        }
        if (CON_SEQ != null && !CON_SEQ.equals("")) {
            Integer count = mapper.getWhereByKey(CON_SEQ);
            if (count != null && count > 0) {
                mapper.updateWhere(map);
            } else {
                mapper.insertWhere(map);
            }
        }
    }

    /**
     * 对工单表的容量进行扩容
     */
	@Override
	public void addTableCapacity(String tenantId) {
		mapper.addTableCapacity(tenantId);
	}

	@Override
	public void stopActivity(String tenantId, String activityId,String serviceURL) {
		ActivityStatus activityStatus = new ActivityStatus();
        activityStatus.setTenant_Id(tenantId);
        activityStatus.setActivityId(activityId);
        activityStatus.setActivityStatus("2");
        log.info("停止活动的服务地址： " + serviceURL);
        log.info("停止的活动的Id ： " + activityId);
        String url = serviceURL;
		HttpUtil.sendPost(url, JSON.toJSONString(activityStatus));
	}

	@Override
	public List<ActivityStatistics> getActivityStatisticsList(String tenantId) {
		List<ActivityStatistics>  list = new ArrayList<ActivityStatistics>();

//		ActivityStatistics activityStatistics1 = new ActivityStatistics();
//    	ActivityStatistics activityStatistics2 = new ActivityStatistics();
//    	ActivityStatistics activityStatistics3 = new ActivityStatistics();
//    	
//    	activityStatistics1.setDate("2017-08-18");
//    	activityStatistics1.setTotal(10);
//    	activityStatistics1.setSuccess(8);
//    	activityStatistics1.setFail(2);
//    	
//    	activityStatistics2.setDate("2017-10-17");
//    	activityStatistics2.setTotal(11);
//    	activityStatistics2.setSuccess(9);
//    	activityStatistics2.setFail(2);
//    	
//    	activityStatistics3.setDate("2017-10-16");
//    	activityStatistics3.setTotal(20);
//    	activityStatistics3.setSuccess(10);
//    	activityStatistics3.setFail(1);
//    	
//    	list.add(activityStatistics1);
//    	list.add(activityStatistics2);
//    	list.add(activityStatistics3);
    	
    	for(int i= 0; i<7;i++){   //查询一周的活动总数/失败数
    		 Calendar calendar = new GregorianCalendar();
    		 Date date=new Date();//当前日期
    		 calendar.setTime(date);
    		 //calendar.set(2017,8,2);
    		 calendar.add(calendar.DATE,-i);
    		 date=calendar.getTime(); 
    		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	     String dateString = formatter.format(date);
    	     System.out.println(dateString);
    	     //活动表里查询日期等于dateString的活动的数目
    	     String failedActivityCondition = "ACTIVITY_STATUS='0'";
    	     int totalActivityNum = mapper.queryTotalActivityNum(dateString,tenantId);
    	     int failActivityNum = 0 ;
    	     if(totalActivityNum > 0) {    //当天跑过活动
    	    	 failActivityNum = mapper.queryFailedActivityNum(dateString,tenantId);
    	     }
    	     ActivityStatistics activityStatistics = new ActivityStatistics();
    	     activityStatistics.setDate(dateString);
    	     activityStatistics.setTotal(totalActivityNum);
    	     activityStatistics.setFail(failActivityNum);
    	     activityStatistics.setSuccess(totalActivityNum-failActivityNum);
    	     list.add(activityStatistics); 
    	}
    	Collections.sort(list, new StaticsticCompator());
		return list;
	}
	
	@Override
	public List<String> tablename(String tenantId, String mysqlschemaname) {
	        String mycatSql = "/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */";
	        mycatSql = mycatSql.replace("TTTTTENANT_ID", tenantId);
	        List<String> tablename=mapper.tablename(mycatSql,mysqlschemaname);
	        return tablename;
	}

}
