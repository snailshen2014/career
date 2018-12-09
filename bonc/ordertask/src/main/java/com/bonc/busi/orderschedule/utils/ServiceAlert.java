package com.bonc.busi.orderschedule.utils;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.utils.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by MQZ on 2017/12/26.
 */
@Service
public class ServiceAlert {
    private static final Logger log = LoggerFactory.getLogger(ServiceAlert.class);

    @Autowired
    private BusiTools AsynDataIns;


    /**  预警接口 发送短信接口
     *  @param sendContent 发送内容
     * @param tenantId 租户id
     * @return  成功 or  失败
     */
    public boolean smsAlert(String sendContent,String tenantId,String exceptionId){
        /**
         * exceptionId
         * 000001 资源化配超时或者错误
         * 000002 执行出库行云SQL失败
         * 000003 活动工单生产时mysql出库失败（也可能工单侧FTP故障）
         * 000004 活动工单生产时从FTP下载失败(工单侧FTP服务故障)
         * 000010 该账期下的用户资料同步数据还没有准备好，行云的数据量是0
         * 000011 该账期下的用户资料同步从行云获取数据失败
         * 000012 该账期下的用户资料同步从ftp获取数据失败
         * 000013 该账期下的用户资料同步向数据库导入数据失败
         * 999999 工单侧其他错误
         *
         */
        String alertUrl = AsynDataIns.getValueFromGlobal("SERVICE_SMSALERT_URL");
//        String alertUrl ="http://clyxys.cop.local:8081/smsinterface/sms/serviceAlert ";
        if (StringUtils.isBlank(alertUrl)) return false;
        String commTxt = "工单模块，预警信息：";
        HashMap<String, String> params = new HashMap<>();
        params.put("sendContent",commTxt+sendContent);
        params.put("appId","003");
        params.put("tenant_id",tenantId);
        params.put("exceptionId",exceptionId);
        String resultJson = HttpUtil.sendPost(alertUrl, JSON.toJSONString(params));
        log.info("[ordertask] 短信预警接口返回信息：{}",resultJson);
        return true;
    }

}
