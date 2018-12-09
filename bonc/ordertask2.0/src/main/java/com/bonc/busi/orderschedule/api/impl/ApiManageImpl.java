package com.bonc.busi.orderschedule.api.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelPo;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.MulyDivideReq;
import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.bo.ResourceRsp;
import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.FtpTools;
import com.bonc.busi.task.base.SftpUtils;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

@Service("ApiManage")
public class ApiManageImpl implements ApiManage {
    private static final Logger logger = LoggerFactory.getLogger(ApiManageImpl.class);

    @Autowired
    private BusiTools busiTools;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static  final  ThreadLocal<Integer> activitySeqId = new ThreadLocal<Integer>();
    private static  final  ThreadLocal<String> activityId = new ThreadLocal<String>();
    private static  final  ThreadLocal<String> tenantId= new ThreadLocal<String>();
    public Integer getActivitySeqId() {
        return activitySeqId.get();
    }

    public void genActivitySeqId() {
        activitySeqId.set(busiTools.getActivitySeqId());
    }
    private String getActivityId() {
        return activityId.get();
    }

    private void setActivityId(String id) {
        activityId.set(id);
    }
    private String getTenantId() {
        return tenantId.get();
    }

    private void setTenantId(String id) {
    	tenantId.set(id);
    }
    @Override
    public List<String> getActivityList(String tenantId) {
        String activityListUrl = SystemCommonConfigManager.getSysCommonCfgValue("ACTIVITY_INFO");
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        // 判断服务类型 电信或者联通
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        if ("1".equals(type)){
            List<String> activityList =  new ArrayList<String>(); // 保存活动信息ACTIVITY_ID的列表
            reqMap.put("tenant_id", tenantId);                          // 租户Id
            String resp = HttpUtil.sendPost(activityListUrl, JSON.toJSONString(reqMap));
            Map datamap1 = JSON.parseObject(resp, Map.class);
            String data = (String) datamap1.get("data");
            if (data != null) {
                try {
                    Map<String,Map> datamap = JSON.parseObject(data, Map.class);
                    List<Map> list = (List<Map>) datamap.get("activityList");
                    for (Map mapTemp : list) {
                        String activityId = (String) mapTemp.get("ACTIVITY_ID");
                        activityList.add(activityId);
				    /*电信项目-活动执行开始-日志记录结束*/
                    }
                } catch (Exception ex) {
                    resp = ex.getMessage();
                }
            }
            return activityList;
        } //---电信

        //---联通
        reqMap.put("tenantId", tenantId);                          // 租户Id
        String activityListInfo = HttpUtil.doGet(activityListUrl, reqMap);
        if (activityListInfo == null || activityListInfo.equals("") || activityListInfo.equals("ERROR")) {
            logger.error("[OrderTask2.0] getActivityList respond error..................!!!");
        }
        return JSON.parseArray(activityListInfo, String.class);
    }

    @Override
    public String getMaxdate(String tenantId) {
        //先判断账期类型  联通或电信
        String monthTimeUrl = SystemCommonConfigManager.getSysCommonCfgValue("GET_MONTH_TIME");
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        // 1 是电信  目的是合并电信版本 但因为接口一致所以做区分
        if ("1".equals(type)){
            Map<String, Object> params  = new HashMap<String, Object>();
            params.put("1", "1");
            params.put("tenant_id", tenantId);
            Map<String,Object> requestMap = new HashMap<String,Object>();
            requestMap.put("req", JSON.toJSONString(params));
            String sendPost = HttpUtil.doGet(monthTimeUrl, requestMap);
            Map<String, Object> resultMap = JSON.parseObject(sendPost, Map.class);
            String monthTime = (String) resultMap.get("MAX_DATE");
            return monthTime;
        }
        String cubeId = SystemCommonConfigManager.getSysCommonCfgValue("GET_CUBE_ID");
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("tenantId", tenantId);
        reqMap.put("cubeId", cubeId);
        String monthTime = HttpUtil.doGet(monthTimeUrl, reqMap);
        if (monthTime == null || monthTime.equals("") || monthTime.equals("ERROR")) {
            logger.error("[OrderTask2.0] getMaxdate respond error..................!!!");
            return "Call_ERROR";
        }
        // 根据接口返回值处理结果
        monthTime = monthTime.substring(monthTime.indexOf(",") + 1, monthTime.length());
        return monthTime;
    }

    @Override
    public String getActivityDetail(String activityId, String tenantId, Integer activitySeqId) {
        //设置全局租户id 和活动id
        this.setActivityId(activityId);
        this.setTenantId(tenantId);

        PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
//		PltActivityExecuteLogIns.setACTIVITY_SEQ_ID(ActivitySeqId);
        exeLog.setCHANNEL_ID("0");
        exeLog.setTENANT_ID(tenantId);
        exeLog.setBUSI_CODE(1004);
        exeLog.setBEGIN_DATE(new Date());
        exeLog.setPROCESS_STATUS(0);
        exeLog.setACTIVITY_ID(activityId);
        exeLog.setACTIVITY_SEQ_ID(activitySeqId);
        LogToDb.recordActivityExecuteLog(exeLog, 0);
        String activityDetailUrl = SystemCommonConfigManager.getSysCommonCfgValue("ACTIVITY_INFO_DETAIL");
        String activityDetailInfo = "";
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        if ("1".equals(type)){
            HashMap<String, String> submap = new HashMap<String, String>();
            //由于电信组使用的框架导致在调用活动详情服务接口时必须把参数按照调用的服务接口参数再封装一次
            Map<String,Object> activityDetailRequestMap = new HashMap<String,Object>();
            //submap.put("activityId", act);
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
            return "Call_ERROR";
        }
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(activityDetailInfo);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return activityDetailInfo;
//        //测试读取文本文件内容
//        String Json = null;
//        try {
//            Json = FileUtils.readFileToString(new File("/opt/1202.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return Json;

    }

    @Override
    public String getGroupId() {
        //添加返回结果
        String respond = "";
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID("0");
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1000);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
        LogToDb.recordActivityExecuteLog(exeLog, 0);
        String userGroupUrl = SystemCommonConfigManager.getSysCommonCfgValue("GET_USER_GROUP_SQL");
        // 从json工厂过去groupId
        String userGroupId = ActivityJsonFactory.getUserGroupId();
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        // 1 是电信  目的是合并电信版本 但因为接口一致所以做区分
        if ("1".equals(type)){
            //使用服务接口的方式获取用户群sql
            Map<String,Object> map = new HashMap<String,Object>();
            Map<String,Object> requestParamMap = new HashMap<String,Object>();
            map.put("userId", "101");
            map.put("cust_group_id", userGroupId);
            //appCode参数可以随便设置
            map.put("appCode", "000");
            requestParamMap.put("cust_group_id", JSON.toJSON(map));
            //获取用户群sql的服务
            //服务返回的是json格式的数据："{\"C002\":\" CUNLIANG2.CUST_LBL_MB_HLJ.YM0193 = '1'  AND  CUNLIANG2.CUST_LBL_MB_HLJ.YM0051 = '1'  AND  CUNLIANG2.CUST_LBL_MB_HLJ.YD0079 <= 100 \",\"C001\":\"1493082071550\"}"
            String jsonStr = HttpUtil.doPost(userGroupUrl, requestParamMap);
            logger.info("客户群返回的sql{}",jsonStr);
            //由于返回的json数据还带了双引号""以及反斜杠\，需要对该json数据处理,否则使用JSON转对象时有问题
//            jsonStr = jsonStr.substring(1, jsonStr.lastIndexOf("\""));
//            jsonStr = jsonStr.replace("\\", "");
//            if(jsonStr.equals("\"-1\"")){
//                logger.error("调用用户群sql服务错误 ");
//                //return null;
//                //如果返回的结果是-1，表明服务调用异常.
//            }
            try {
                String temp =  jsonStr.substring(1, jsonStr.lastIndexOf("\""));
                temp = temp.replace("\\", "");
                Map userGroupMap = JSON.parseObject(temp, Map.class);
                logger.info("电信调用用户群服务返回的Map数据：" + userGroupMap.get("C002"));
                return  (String)userGroupMap.get("C002");
            }catch (Exception e){
                JSONObject obj = JSONObject.parseObject(jsonStr);
                logger.info("电信调用用户群服务返回的Map数据：" + obj.get("C002"));
                return obj.getString("C002");
            }
        }else {
            HashMap<String, Object> reqMap = new HashMap<String, Object>();
            reqMap.put("ciWaId", userGroupId);
            reqMap.put("tenantId", ActivityJsonFactory.tenantId());
            respond = HttpUtil.doGet(userGroupUrl, reqMap);
            if (respond == null || respond.equals("") || respond.equals("ERROR")) {
                logger.error("[OrderTask2.0] getGroupId respond error..................!!!");
                return "Call_ERROR";
            }
            // 根据接口返回值处理结果
            Map map = JSON.parseObject(respond, Map.class);
            respond = (String) map.get("sql");
        }
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(userGroupId);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return respond;
    }

    @Override
    public String getRuleTypestmpTable(String channelId, ChannelPo channelPo) {
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
    	exeLog.setCHANNEL_ID(channelId);
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1002);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
    	LogToDb.recordActivityExecuteLog(exeLog, 0);
    	
        // 通知资源划配生成临时表
        String resourceRep = callToResource(channelId, channelPo);
        ResourceRsp res = JSON.parseObject(resourceRep, ResourceRsp.class);
        String resourceTable = res.getTemp_table();
        String businessId = res.getDraw_business_id();
        // 循环调用资源化配状态 监听状态为3时返回临时表名称
        int delay = 0;
        while (true) {
            String rspStatus = callToResourceStatus(businessId);
            if (null== rspStatus || rspStatus.contains("ERROR")) {
                return "ERROR";
            }
            System.out.println("[OrderTask2.0] "+this.getTenantId() +"  活动id："+this.getActivityId() +
                    "批次id:"+this.getActivitySeqId() +"渠道id:"+channelId+" 资源划配状态为" + rspStatus);
            if (rspStatus.contains("3"))
                break;
            else
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            // only wait max time 20 minute
            if (delay++ >= 120 || rspStatus.contains("4")) {
                logger.error("[OrderTask2.0] get Ressource Status timeout or state is 4 need re-run");
                BoncExpection boncExpection = new BoncExpection();
                boncExpection.setMsg("callRessourceError");
                throw boncExpection;
            }
        }
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(resourceRep);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return resourceTable;
    }

    private String callToResource(String channelId, ChannelPo cp) {
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID(channelId);
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1001);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
    	LogToDb.recordActivityExecuteLog(exeLog, 0);

    	String activityMulyDivideUrl = SystemCommonConfigManager.getSysCommonCfgValue("ACTIVITY_MULT_DIVIDE");
        String groupId = getGroupId();
        MulyDivideReq mulyDivideReq = new MulyDivideReq();
        mulyDivideReq.setRule_type_id(cp.getOrderIssuedRule());
        String ruleOrgPath = "";
        if ("5".equals(channelId)) {
            String  orgPath =  ActivityJsonFactory.getActivityProvPo().getFrontlineChannelPo().getRuleOrgPath();
            if (null != orgPath){
                ruleOrgPath =orgPath;
            }
        }
        if ("14".equals(channelId)) {
            String  orgPath =  ActivityJsonFactory.getActivityProvPo().getChannelTelePhone().getRuleOrgPath();
            if (null != orgPath){
                ruleOrgPath =orgPath;
            }
        }
        mulyDivideReq.setRuleOrgPath(ruleOrgPath);
        mulyDivideReq.setRule_sql(groupId);
        mulyDivideReq.setTenant_id(ActivityJsonFactory.tenantId());
        mulyDivideReq.setTarget_id(channelId);
        mulyDivideReq.setBusiness_id(ActivityJsonFactory.getActivityId());
        //判断是否为异网 如果是异网添加netflag :'1'
        if ("1".equals(SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_IS_YW"))){
            mulyDivideReq.setNetflag("1");
        }
        logger.info("资源划配请求："+  JSON.toJSONString(mulyDivideReq));
        String resourceRep = requestResource(activityMulyDivideUrl, JSON.toJSONString(mulyDivideReq));
        if (resourceRep == null || resourceRep.equals("") || resourceRep.equals("ERROR")) {
            logger.error("[OrderTask2.0] callToResource respond error..................!!!");
            return "Call_ERROR";
        }
        logger.info("[OrderTask2.0] resource's respond=" + resourceRep);
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(JSON.toJSONString(mulyDivideReq));
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return resourceRep;
    }

    private String callToResourceStatus(String businessId) {
        String activityMulyDivideStatusUrl = SystemCommonConfigManager.getSysCommonCfgValue("ACTIVITY_DIVIDE_STATUS");
        String multiRequest = "{\"";
        multiRequest += "draw_business_id\":";
        multiRequest += "\"";
        multiRequest += businessId;
        multiRequest += "\"}";
        String resourceStatus = requestResource(activityMulyDivideStatusUrl, multiRequest);
        if (resourceStatus == null || resourceStatus.equals("") || resourceStatus.equals("ERROR")|| resourceStatus.equals("Error")) {
            logger.error("[OrderTask2.0] callToResourceStatus respond error..................!!!");
            return "Call_ERROR";
        }
        return resourceStatus;
    }

    private static String requestResource(String url, String json) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        StringBuilder buf = new StringBuilder();
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");

            StringEntity inputEntity = new StringEntity(json, "utf-8");
            inputEntity.setContentType("application/json");
            inputEntity.setContentEncoding("utf-8");
            httpPost.setEntity(inputEntity);

            httpResponse = httpClient.execute(httpPost);
            // System.out.println("RequestResource httpResponse =" +
            // httpResponse);
//            System.out.println("[OrderTask2.0] RequestResource httpResponse status: =" + httpResponse.getStatusLine());
            String output = "";
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader((httpResponse.getEntity().getContent())));

            while ((output = bufferedReader.readLine()) != null) {
                buf.append(output);
            }
            // System.out.println(buf);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[OrderTask2.0] RequestResource cat IOException:" + httpResponse.getStatusLine());

        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buf.toString();
    }

    @Override
    public int downXCloudLoadFile(String fileName) {
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID("0");
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1006);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
    	LogToDb.recordActivityExecuteLog(exeLog, 0);
    	
        //获取ftp信息
        String FtpSrvIp = SystemCommonConfigManager.getSysCommonCfgValue("HDFSSRV.IP."+ getTenantId());
        String FtpUser = SystemCommonConfigManager.getSysCommonCfgValue("HDFSSRV.USER."+ getTenantId());
        String FtpPassword = SystemCommonConfigManager.getSysCommonCfgValue("HDFSSRV.PASSWORD."+ getTenantId());
        String FtpPort = SystemCommonConfigManager.getSysCommonCfgValue("HDFSSRV.PORT."+ getTenantId());
        String ftpRemote = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_REMOTEPATH");
        String ftpLocal = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_LOCALPATH");
        //转换字符串
        ftpRemote = ftpRemote.replaceFirst("HDFS:", "");
        int downRtn = -1;
        String transType = SystemCommonConfigManager.getSysCommonCfgValue("ORDER.TRANS.TYPE." + getTenantId());
        String ftpRtn;
        if ("SFTP".equals(transType)){
            SftpUtils sftpUtils = new SftpUtils();
            ftpRtn = sftpUtils.downloadXcloudFile(FtpSrvIp, FtpUser, FtpPassword, Integer.parseInt(FtpPort),
                    ftpRemote + fileName + ".csv", ftpLocal + fileName + ".csv", true);
        }else {
            ftpRtn = FtpTools.downloadXcloudFile(FtpSrvIp, FtpUser, FtpPassword, Integer.parseInt(FtpPort),
                    ftpRemote + fileName + ".csv", ftpLocal + fileName + ".csv", true);
        }
        if ("000000".equals(ftpRtn))
            downRtn = 0;
        else
            logger.error("Down load xcloudFile error" + ftpRtn);
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(ftpRtn);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return downRtn;
    }

    @Override
  /*
     * 在行云上执行DDL语句
	 */
    public JsonResult execDdlOnXcloud(String sqlDdl,String tenantId) {
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
    	exeLog.setCHANNEL_ID("0");
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1005);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
    	LogToDb.recordActivityExecuteLog(exeLog, 0);
    	
        JsonResult JsonResultIns = new JsonResult();
        Date begin = new Date();
        Connection connection = null;
        Statement statement = null;
        boolean bReturn = true;
        try {
            Class.forName(SystemCommonConfigManager.getSysCommonCfgValue("DS.XCLOUD.DRIVER"));
            connection = DriverManager.getConnection(SystemCommonConfigManager.getSysCommonCfgValue("DS.XCLOUD.URL."+tenantId),
                    SystemCommonConfigManager.getSysCommonCfgValue("DS.XCLOUD.USER."+tenantId),
                    SystemCommonConfigManager.getSysCommonCfgValue("DS.XCLOUD.PASSWORD."+tenantId));
            statement = connection.createStatement();
            System.out.println("++++++++++++++++++++++++++++++++行云出库开始执行+++++++++++++++++++++++++++++++");
            statement.execute(sqlDdl);
            JsonResultIns.setCode("000000");
            JsonResultIns.setMessage("sucess");
            System.out.println("++++++++++++++++++++++++++++++++行云出库执行成功+++++++++++++++++++++++++++++++");
        } catch (Exception e) {
            logger.info("在行云上执行命令报错 !!!");
            JsonResultIns.setCode("000001");
            JsonResultIns.setMessage(e.toString());
            e.printStackTrace();
            bReturn = false;
//            throw e;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }

        }
        // --- 如果是从行云出库,则需要延时以供行云改文件名 ---
        if (sqlDdl.toUpperCase().indexOf("EXPORT") != -1)
            if (bReturn) {
                // --- 行云要花时间改文件名 ---
                Date end = new Date();
                long dur = (end.getTime() - begin.getTime()) / 1000 / 10;
                System.out.println("+++++++++++++++++++++++++++++++等待行云改名时间："+dur +"+++++++++++++++++++++++++++++++");
                if (dur < 40) dur = 40;
                try {
                    Thread.sleep(dur * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        System.out.println("++++++++++++++++++++++++++++++++等待结束+++++++++++++++++++++++++++++++++");
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(sqlDdl);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return JsonResultIns;
    }


    /*
     * 倒入MYSQL
	 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean loadDataInMysql(String sqlData, String tenantId) {
    	PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID("0");
    	exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
    	exeLog.setTENANT_ID(this.getTenantId());
    	exeLog.setBUSI_CODE(1007);
    	exeLog.setBEGIN_DATE(new Date());
    	exeLog.setPROCESS_STATUS(0);
    	exeLog.setACTIVITY_ID(this.getActivityId());
    	LogToDb.recordActivityExecuteLog(exeLog, 0);
    	
        //Date				begin = new Date();
        Connection connection = null;
        Statement statement = null;
        boolean bReturn = true;
        try {
            Class.forName(SystemCommonConfigManager.getSysCommonCfgValue("DS.MYSQL.DRIVER"));
            connection = DriverManager.getConnection(SystemCommonConfigManager.getSysCommonCfgValue("DS.MYSQL.URL." + tenantId),
                    SystemCommonConfigManager.getSysCommonCfgValue("DS.MYSQL.USER." + tenantId),
                    SystemCommonConfigManager.getSysCommonCfgValue("DS.MYSQL.PASSWORD." + tenantId));
            //connection = DriverManager.getConnection("jdbc:mysql://10.162.2.119:31699/henan0" , "orderrun",
            //		"orderrun");
            statement = connection.createStatement();
            logger.info("sql:{}" + sqlData);
            statement.execute(sqlData);
        } catch (Exception e) {
            logger.info("入MYSQL数据库出错 !!!");
            e.printStackTrace();
            bReturn = false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(sqlData);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return bReturn;
    }

    /**
     * 调用成功标准过滤
     * @param TenantId  租户id
     * @param ActivitySeqId     活动批次号
     * @param activityid  活动id
     * @return
     */
    @Override
    public String orderFilterSucess(String TenantId, Integer ActivitySeqId, String activityid) {
        //先判断是否为异网
        String isYw = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_IS_YW");
        if ("1".equals(isYw)){
            return "YW";
        }
        String ordersucessfilter_url = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_SUCESSFILTER_URL");
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("TenantId", TenantId);
        reqMap.put("ActivitySeqId", ActivitySeqId);
        reqMap.put("ActivityId" ,activityid);
        String  result= HttpUtil.doGet(ordersucessfilter_url, reqMap);
        return result;
    }


    /**
     * 当一个批次工单生成完成之后调用  需要调用这个接口
     * @param activityId 活动id
     * @param tenantId   租户id
     * @param activitySeqId 活动批次
     * @param updateFlag: 是否是更新统计数据的标识，当传了该参数就表示该调用是为了更新统计表的数,不是初次统计，该参数的值可以为任何值,例如true
     */
    @Override
    public void channelInitHandle(String activityId, String tenantId, String activitySeqId,String updateFlag) {
        String initHandle_url = SystemCommonConfigManager.getSysCommonCfgValue("COCHANNEL_INIT_URL");
        HashMap<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("tenantId", tenantId);
        reqMap.put("activityId", activityId);
        reqMap.put("activitySeqId", activitySeqId);
        reqMap.put("updateFlag", updateFlag);
        String respons = HttpUtil.sendPost(initHandle_url, JSON.toJSONString(reqMap));
        logger.info("调用统计请求地址："+ initHandle_url + "调用参数为：" +JSON.toJSONString(reqMap));
    }

    /**
     * 弹窗渠道添加手机索引
     */
    @Override
    public String popwinaddindex(String TenantId, String OrderTableName, int ActivitySeqid, String activityId, String channelId) {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("TenantId", ActivityJsonFactory.tenantId());
        param.put("ActivitySeqId", ActivitySeqid);
        param.put("OrderTableName", OrderTableName);
        param.put("ActivityId", activityId);
        param.put("ChannelId", channelId);
        String popwinaddindex_url = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_POPIN_ADDINDEX");
        String response = HttpUtil.doGet(popwinaddindex_url, param);
        logger.info("调用添加渠道手机索引请求地址："+ popwinaddindex_url + "调用参数为：" +JSON.toJSONString(param));
        return  response;
    }

    @Override
    public JsonResult execDdlSql(String xCloudSql) {
        PltActivityExecuteLog	exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID("0");
        exeLog.setACTIVITY_SEQ_ID(this.getActivitySeqId());
        exeLog.setTENANT_ID(this.getTenantId());
        exeLog.setBUSI_CODE(1005);
        exeLog.setBEGIN_DATE(new Date());
        exeLog.setPROCESS_STATUS(0);
        exeLog.setACTIVITY_ID(this.getActivityId());
        LogToDb.recordActivityExecuteLog(exeLog, 0);
        JsonResult JsonResultIns = new JsonResult();
        Date begin = new Date();
        try {
            jdbcTemplate.execute(xCloudSql);
            JsonResultIns.setCode("000000");
            JsonResultIns.setMessage("sucess");
        } catch (Exception e) {
            logger.info("在行云上执行命令报错 !!!");
            JsonResultIns.setCode("000001");
            JsonResultIns.setMessage(e.toString());
            e.printStackTrace();
        }
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        exeLog.setBUSI_ITEM(xCloudSql);
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return JsonResultIns;
    }

    @Override
    public void callAllotOrder(HashMap<String, Object> params) {
        Map<String,Object> allotOrderRequestMap = new HashMap<String,Object>();
        String  allotOrderUrl  = SystemCommonConfigManager.getSysCommonCfgValue("GET_ALLOTORDER_URL");
//        String  allotOrderUrl = "http://172.16.14.12:8090/eframeone/allotcenter/allot/allotOrder";
        if (StringUtils.isBlank(allotOrderUrl)) return;
        allotOrderRequestMap.put("req",JSON.toJSONString(params));
        String respons = HttpUtil.doPost(allotOrderUrl, allotOrderRequestMap);
        logger.info("调用电信调配 ：{}    调用参数为：{}" ,allotOrderUrl,JSON.toJSONString(params));
    }


    @Override
    public List<String> getEnableChannel(String activityId, List<String> orgChannelIds) {
        HashMap<String, Object> jsonResult = new HashMap<String, Object>();
        String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
        //判断电信和联通系统 只处理特殊渠道
        if ("1".equals(type)){
            return specialChannel(orgChannelIds);
        }
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("tenantId", ActivityJsonFactory.tenantId());
        param.put("activityId", ActivityJsonFactory.getActivityId());
        String enableChannel_url = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_CHECK_ENABLECHANNEL");
        String enableChannel_result = HttpUtil.doGet(enableChannel_url, param);
        jsonResult = (HashMap<String, Object>) JSON.parseObject(enableChannel_result, Map.class);
        List<String> ennbleList = (List<String>) jsonResult.get("result");
        //得到相同元素
        ennbleList = getSameId(ennbleList, orgChannelIds);
        //处理特殊渠道（弹窗子渠道）
        ennbleList = specialChannel(ennbleList);
        return ennbleList;
    }


    /**
     * 获取两个List的相同元素
     *
     * @param list1
     * @param list2
     * @return
     */
    private static List<String> getSameId(List<String> list1, List<String> list2) {
        List<String> same = new ArrayList<String>();
        for (String str : list1) {
            if (list2.contains(str)) {
                same.add(str);
            }
        }
        return same;
    }

    /**
     * 处理特殊渠道（弹窗子渠道）
     * @param ennbleList
     * @return
     */
    private List<String> specialChannel(List<String> ennbleList) {
        List<String> resultList = new ArrayList<String>();
        for (String str : ennbleList){
            resultList.add(str);
        }
        if (ennbleList.contains("8")){
            resultList.remove("8");
            //如果包含渠道8则 添加进子渠道
            if (null == ActivityJsonFactory.getPopWinList()){
                return resultList;
            }
            for (ChannelGroupPopupPo po : ActivityJsonFactory.getPopWinList()){
                String subChannelId = po.getChannelId();
                subChannelId += po.getBusinessHall();
                resultList.add(subChannelId);
            }
        }
        return  resultList;
    }

//    public static void main(String[] args) {
//        SftpUtils sftpUtils = new SftpUtils();
//        String s = sftpUtils.downloadXcloudFile("172.16.91.235", "test", "test", 24, "/test/activityJson.txt", "E:\\activityJson.csv", true);
//        System.out.println(s);
//    }
}
