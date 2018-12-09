package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.activity.*;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.ActivityProcessLog;
import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.orderschedule.bo.SpecialFilter;
import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.files.OrderFileMannager;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.log.LogToDb;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.orderschedule.mapping.ConstantValue;
import com.bonc.busi.orderschedule.mapping.OrgRangeValue;
import com.bonc.busi.orderschedule.mapping.SqlMapping;
import com.bonc.busi.orderschedule.mapping.Value;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChannelFunc {
    private final static Logger logdb = LoggerFactory.getLogger(ChannelFunc.class);


    private ApiManage apiManage = SpringUtil.getApplicationContext().getBean(ApiManage.class);
    private OrderMapper ordermapper = SpringUtil.getApplicationContext().getBean(OrderMapper.class);


    private String filename;
    private String recomendInfo = "";
    private String smsTemplate = "";
    private String productId = "";
    private String productName = "";
    private String filterName = "";
    private String parameterType = "";
    private String org_range = "";

    // 需要将 Activity对象变成 ThreadLocal对象
    protected static final ThreadLocal<Activity> actAttribute = new ThreadLocal<>();
    private static final String ORDER_TABLE = "PLT_ORDER_INFO_TEMP";

    //构造方法
    public ChannelFunc(final Activity act) {
        actAttribute.set(act);
    }



    public void setRecomendInfoAndSmsTemplate() {
        //子类选择性实现方法
    }

    public void setRecomendInfo(String recomendInfo) {
        this.recomendInfo = recomendInfo;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setOrg_range(String org_range) {
        this.org_range = org_range;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * 记录活动详情信息(子类需要重写)
     */
    public int recordActivityDetail() {
        return 0;
    }

    /**
     * 生成渠道工单  (主流程)
     * po类
     * 日志序列号
     */
    public boolean genChannelOrders(ChannelPo channelPo) {
        // 得到资源化配临时表名称
        String tmpTableName = getTmpTableName(actAttribute.get().getChannelId(), channelPo);
        if (tmpTableName.contains("ERROR") || "".equals(tmpTableName) || null==tmpTableName) {
            logdb.error("资源划配调用失败");
            return false;
        }
        //记录工单生成时间到PLT_ACTIVITY_PROCESS_LOG--开始
        ActivityProcessLog log = new ActivityProcessLog();
        log.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
        log.setACTIVITY_SEQ_ID(apiManage.getActivitySeqId());
        log.setCHANNEL_ID(actAttribute.get().getChannelId());
        log.setTENANT_ID(actAttribute.get().getTenantId());
        log.setSTATUS(0);
        log.setORDER_BEGIN_DATE(TimeUtil.getDateTime(TimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
        ordermapper.InsertActivityProcessLog(log);
        //记录工单生成时间到PLT_ACTIVITY_PROCESS_LOG--结束
        // 生成文件名
        genFileName();
        // 初始化orgpath 和RecomendInfo SmsTemplate
        setOrg_range(ActivityJsonFactory.getOrgPath());
        // 子类初始化RecomendInfo SmsTemplate
        setRecomendInfoAndSmsTemplate();
        // 生成行云SQL
        String xCloudSql = genXCloudSql(tmpTableName, channelPo.getFilterConditionSql());
        // 执行行云SQL
        System.out.println("[OrderTask2.0] " + actAttribute.get().getTenantId() + "  批次id：" + actAttribute.get().getActivitySeqId() +
                "渠道id:" + actAttribute.get().getChannelId() + " 行云sql:" + xCloudSql);
        JsonResult JsonResultIns = apiManage.execDdlOnXcloud(xCloudSql, actAttribute.get().getTenantId());
        // 2017-09-26 改为通过mycat来路由行云 执行sql
//        JsonResult JsonResultIns  = apiManage.execDdlSql(xCloudSql);
        if (!"000000".equalsIgnoreCase(JsonResultIns.getCode())) {
            logdb.error("[OrderCenter] Generate order file error on xcloud.");
            return false;
        } else {
            logdb.info("[OrderCenter]  RUN_XCLOUD_SQL_END");
        }

        // 下载行云文件到服务中
        int isFinishDownLoad = apiManage.downXCloudLoadFile(getFileName());
        if (isFinishDownLoad == -1) {
            BoncExpection boncExpection = new BoncExpection();
            boncExpection.setMsg("执行行云异常");
            throw boncExpection;
        }
        // 拆分文件
        List<String> fileLists = splitOriginFile();
        // 循环文件列表入mysql库的临时表
        for (String fileName : fileLists) {
            if (loadDataToTempTable(fileName)) {
                logdb.info("[OrderCenter]  load data mysql ok");
            } else {
                logdb.info("[OrderCenter]  load data mysql error");
                return false;
            }
        }
        // 记录活动详情信息(子类需要重写)
        PltActivityExecuteLog exeLog = new PltActivityExecuteLog();
        exeLog.setCHANNEL_ID("0");
        exeLog.setACTIVITY_SEQ_ID(apiManage.getActivitySeqId());
        exeLog.setTENANT_ID(actAttribute.get().getTenantId());
        exeLog.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
        exeLog.setCHANNEL_ID(actAttribute.get().getChannelId());
        exeLog.setBEGIN_DATE(new Date());
        exeLog.setPROCESS_STATUS(0);
        exeLog.setBUSI_CODE(1015);
        LogToDb.recordActivityExecuteLog(exeLog, 0);
        recordActivityDetail();
        exeLog.setPROCESS_STATUS(1);
        exeLog.setEND_DATE(new Date());
        LogToDb.recordActivityExecuteLog(exeLog, 1);
        return true;
    }
    /*
     * 创建临时表
     */

    private String getTmpTableName(String channelId, ChannelPo channelPo) {
        return apiManage.getRuleTypestmpTable(channelId, channelPo);
    }

    /**
     * 生成行云sql
     */
    private String genXCloudSql(String tmpTableName, String specialCondition) {
        String selSql = getSelectSql(tmpTableName, specialCondition);
        StringBuilder sb = new StringBuilder();
        /*!mycat:sql=select * FROM XCLOUD_TTTTTENANT_ID*/
//        sb.append("/*!mycat:sql=select * FROM XCLOUD_").append(actAttribute.get().getTenantId()).append("*/ ");
        sb.append("export ");
        sb.append(selSql);
        sb.append("ATTRIBUTE(LOCATION('");
        sb.append(SystemCommonConfigManager.getSysCommonCfgValue("ORDER_REMOTEPATH"));
        sb.append(getFileName());
        sb.append(".csv')");
        sb.append(" SEPARATOR('|'))");
        return sb.toString();
    }

    /**
     * 入mysql库
     *
     * @param fileName
     */
    private boolean loadDataToTempTable(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("LOAD DATA   local INFILE  '");
        sb.append(fileName);
        sb.append("' IGNORE into table ");
        sb.append(ORDER_TABLE);
        sb.append(" fields  terminated by \'|\' LINES TERMINATED BY \'\\n\' ");
        sb.append(getOrderColumnsSql());
        logdb.info("[OrderTask2.0] Load data sql:" + sb.toString());
        //call api to load data
        if (apiManage.loadDataInMysql(sb.toString(), actAttribute.get().getTenantId())) {
            logdb.info("[OrderTask2.0] Load data file:" + fileName + " finished status ok.");
            //delete local file
            OrderFileMannager.deleteFile(fileName);
            return true;
        } else {
            logdb.error("[OrderTask2.0] Load data file:" + fileName + " finished status error.");
            //delete local file
            OrderFileMannager.deleteFile(fileName);
            return false;
        }
    }

    /*
     * 生成文件名称
     */
    private void genFileName() {
        String name = "order_tmp_";
        name += ActivityJsonFactory.tenantId() + "_";
        Format format = new SimpleDateFormat("yyyyMMddHHmmss");
        name += format.format(new Date());
        this.filename = name;
    }

    /*
     * 获取文件名称
     */
    private String getFileName() {
        return filename;
    }

    /*
     * get case when sql
     *
     * @param: List<ChannelSpecialFilterPo>
     *
     * @param: int 0:get recommend sql;1:get msm template sql
     *
     * @return: sql
     */
    protected String getSpecialFilterMap(List<ChannelSpecialFilterPo> specialFilterList, int type) {
        // init tree map
        Map<String, SpecialFilter> filterMap = new TreeMap<String, SpecialFilter>();
        for (int i = 0; i < specialFilterList.size(); i++) {
            ChannelSpecialFilterPo p = specialFilterList.get(i);
            SpecialFilter filter = new SpecialFilter();
            filter.setSql(p.getFilterConditionSql());
            if (type == 0){
                if (p.getRecommenedInfo() == null) return "";
                filter.setRecommend(p.getRecommenedInfo());
            }
            if (type == 1){
                if (p.getMsmTemplate() == null) return "";
                filter.setSmsTemplate(p.getMsmTemplate());
            }
            if (type == 2) {
                if (p.getProductId() == null) return "";
                filter.setProductId(p.getProductId());
            }
            if (type == 3) {
                if (p.getProductName() == null) return "";
                filter.setProductName(p.getProductName());
            }
            if (type == 4) {
                if (p.getFilterName() == null) return "";
                filter.setFilterName(p.getFilterName());
            }
            if (type == 5) {
                if (p.getParameterType() == null) return "";
                filter.setParameterType(p.getParameterType());
            }
            filterMap.put(p.getOrd(), filter);
        }
        // generate case sql
        StringBuilder caseWhenSql = new StringBuilder();
        String tempCon = "";

        if (filterMap.size() == 1) {
            // no condition sql so return recommend info and sms template
            String ord = "0";
            Set<String> ordSet = filterMap.keySet();
            Iterator<String> iterator = ordSet.iterator();
            while (iterator.hasNext()) {
                ord = iterator.next();
            }
            //问题修改，由于报文里传的ord不是"0"时, filterMap.get("0")拿不到数据。
            if (type == 0)
                caseWhenSql.append(filterMap.get(ord).getRecommend());
            if (type == 1)
                caseWhenSql.append(filterMap.get(ord).getSmsTemplate());
            if (type == 2) {
                caseWhenSql.append(filterMap.get(ord).getProductId());
            }
            if (type == 3) {
                caseWhenSql.append(filterMap.get(ord).getProductName());
            }
            if (type == 4){
                caseWhenSql.append(filterMap.get(ord).getFilterName() );
            }
            if (type == 5){
                caseWhenSql.append(filterMap.get(ord).getParameterType() );
            }
        } else {
            int i = 0;
            String and = " and ";
            caseWhenSql.append("(case");
            for (Map.Entry<String, SpecialFilter> entry : filterMap.entrySet()) {
                if (++i == 1)
                    continue;
                // caseWhenSql.append
                caseWhenSql.append(" when " + tempCon);
                if (!tempCon.equals(""))
                    caseWhenSql.append(and);
                caseWhenSql.append(entry.getValue().getSql());
                caseWhenSql.append(" then ");
                // join value
                caseWhenSql.append("'");
                if (type == 0)
                    caseWhenSql.append(entry.getValue().getRecommend());
                if (type == 1)
                    caseWhenSql.append(entry.getValue().getSmsTemplate());
                if (type == 2)
                    caseWhenSql.append(entry.getValue().getProductId());
                if (type == 3)
                    caseWhenSql.append(entry.getValue().getProductName());
                if (type == 4)
                    caseWhenSql.append(entry.getValue().getFilterName());
                if (type == 5)
                    caseWhenSql.append(entry.getValue().getParameterType());
                caseWhenSql.append("'");

//                tempCon += getReverseCondition(entry.getValue().getSql(), ++j);
            }
            // join else sql
            caseWhenSql.append(" else ");
            caseWhenSql.append("'");
            if (type == 0)
                caseWhenSql.append(filterMap.get("0").getRecommend());
            if (type == 1)
                caseWhenSql.append(filterMap.get("0").getSmsTemplate());
            if (type == 2)
                caseWhenSql.append(filterMap.get("0").getProductId());
            if (type == 3)
                caseWhenSql.append(filterMap.get("0").getProductName());
            if (type == 4)
                caseWhenSql.append(filterMap.get("0").getFilterName());
            if (type == 5)
                caseWhenSql.append(filterMap.get("0").getParameterType());
            caseWhenSql.append("'");
            caseWhenSql.append(" end) ");

        }
        return caseWhenSql.toString();

    }

    private List<String> splitOriginFile() {
        int activitySeqId = apiManage.getActivitySeqId();
        String ftpLocal = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_LOCALPATH");
        String splitNum = SystemCommonConfigManager.getSysCommonCfgValue("ORDER_SPLIT_NUM");
        String origFileName = ftpLocal + getFileName() + ".csv";
        Integer filterBlackUserFlag = ordermapper.getFilterBlackUserFlag(activitySeqId, ActivityJsonFactory.tenantId());
        List<String> fileList = OrderFileMannager.splitFile(origFileName, Integer.parseInt(splitNum), filterBlackUserFlag != null && filterBlackUserFlag == 1);
        OrderFileMannager.deleteFile(origFileName);
        return fileList;
    }

    /**
     * construct value object
     *
     * @param v
     * @return
     */
    private Value getConstantValueObject(Object v) {
        ConstantValue value = new ConstantValue();
        value.setValue(v);
        return value;
    }

    /**
     * get xcloud select sql
     *
     * @param tmpTableName
     * @param specialCondition
     * @return
     */
    private String getSelectSql(String tmpTableName, String specialCondition) {
        String mark = "'";
        SqlMapping sqlMap = new SqlMapping(actAttribute.get().getTenantId());
        //need set value handel for counting elements
        //ACTIVITY_SEQ_ID
        sqlMap.setElementValue(1, getConstantValueObject(actAttribute.get().getActivitySeqId()), 0);
        //CHANNEL_ID
        sqlMap.setElementValue(2, getConstantValueObject(mark + actAttribute.get().getChannelId() + mark), 0);
        //ORDER_STATUS
        sqlMap.setElementValue(20, getConstantValueObject(0), 0);
        //CHANNEL_STATUS
        sqlMap.setElementValue(21, getConstantValueObject(0), 0);
        //BEGIN_DATE
        sqlMap.setElementValue(25, getConstantValueObject(mark + actAttribute.get().getOrderBeginDate() + mark), 0);
        //END_DATE
        sqlMap.setElementValue(26, getConstantValueObject(mark + actAttribute.get().getOrderEndDate() + mark), 0);
        //USERLABEL_RESERVE1
        sqlMap.setElementValue(39, getConstantValueObject(formateSpecial(this.recomendInfo)), 0);
        //USERLABEL_RESERVE2
        sqlMap.setElementValue(40, getConstantValueObject(formateSpecial(this.smsTemplate)), 0);
        //BUSINESS_RESERVE46
        sqlMap.setElementValue(133, getConstantValueObject(formateSpecial(this.filterName)), 0);
        //BUSINESS_RESERVE48
        sqlMap.setElementValue(135, getConstantValueObject(formateSpecial(this.productId)), 0);
        //BUSINESS_RESERVE49
        sqlMap.setElementValue(136, getConstantValueObject(formateSpecial(this.productName)), 0);
        //BUSINESS_RESERVE44
        sqlMap.setElementValue(131, getConstantValueObject(formateSpecial(this.parameterType)), 0);
        //table
        //${AssignResult}
        sqlMap.setElementValue(2, getConstantValueObject(tmpTableName), 1);

        //conditions
        String dateId = apiManage.getMaxdate(ActivityJsonFactory.tenantId());
        sqlMap.setElementValue(3, getConstantValueObject(dateId), 2);

        //5 ,6 need judge by business feature
        if (this.org_range != null && !this.org_range.equals("")) {
            String elementValue = sqlMap.getElementValue(4, 2);
            OrgRangeValue range = new OrgRangeValue(this.org_range, elementValue);
            sqlMap.setElementValue(4, range, 2);
        } else {
            sqlMap.closeElement(4, 2);
        }
        if (specialCondition != null && !specialCondition.equals("")) {
            specialCondition = specialCondition.replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
            specialCondition = specialCondition.replaceAll("CUST_LABEL", "a");
            sqlMap.setElementValue(5, getConstantValueObject("("+specialCondition+")"), 2);
        } else {
            sqlMap.closeElement(5, 2);
        }
        return sqlMap.toSelect();
    }

    /**
     * get order table's columns sql "(c1,c2,c3...)
     *
     */
    private String getOrderColumnsSql() {
        SqlMapping sqlMap = new SqlMapping(actAttribute.get().getTenantId());
        return sqlMap.toColumns();
    }

    private String formateSpecial(String str) {
        if (str.indexOf("case") == -1) {
            return "'" + str + "'";
        } else {
            if (str.indexOf("UNICOM_D_MB_DS_ALL_LABEL_INFO") != -1) {
                str = str.replace("UNICOM_D_MB_DS_ALL_LABEL_INFO", "a");
            }
        }
        return str;

    }


}
