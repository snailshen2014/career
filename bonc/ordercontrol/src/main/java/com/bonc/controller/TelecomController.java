package com.bonc.controller;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.service.SysFunction;
import com.bonc.common.base.BDIJsonResult;
import com.bonc.common.base.JsonResult;
import com.bonc.utils.HttpUtil;
import oracle.jdbc.proxy.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by MQZ on 2017/6/29.
 */
@RestController
@RequestMapping("/telecomcontroller")
public class TelecomController {
    private final static Logger log = LoggerFactory.getLogger(TelecomController.class);
    @Autowired
    private SysFunction SysFunctionIns;

    /**
     * 生成工单
     */
    @RequestMapping(value="/genOrder")
    @Post
    public String genOrder(@RequestBody String requestParam){
        log.info("获得的JSON请求参数是：" + requestParam);
		/* 获得的请求参数应该是如下的格式：
         * {
         *    "activityList": [
         *  {
         *    "ACTIVITY_ID": "a1e28cc5b85347acb761af6d80fb62c7","EXEC_ACCOUNT":"20170408"
         *  },
         *  {
         *    "ACTIVITY_ID": "ee5a89d80def41a88b325ba597501c8c","EXEC_ACCOUNT":"20170408"
         *  },
         *  {
         *    "ACTIVITY_ID": "c232b0e7be974666b99add45002e8d01","EXEC_ACCOUNT":"20170408"
         *  }
         * ],
         *   "tenant_id": "tenant_system"
         *  }

		 */
        BDIJsonResult JsonResultIns = new BDIJsonResult();
        JsonResultIns.setStatus("1");//默认成功
        String message = "successed";
        List<String> activityList =  new ArrayList<String>(); // 保存活动信息ACTIVITY_ID的列表
        String tenantId = null;                              // 租户Id
        try {
            if (requestParam != null) {
                Map datamap = JSON.parseObject(requestParam, Map.class);
                tenantId = (String) datamap.get("tenant_id"); // 获得租户Id
                String activityListStr = datamap.get("activityList").toString();
                List<Map> list = JSON.parseArray(activityListStr, Map.class);
                for (Map mapTemp : list) {
                    String activityId = (String) mapTemp.get("ACTIVITY_ID");
                    activityList.add(activityId);
                        /*电信项目-活动执行开始-日志记录结束*/
                }
            }

        if (activityList.size() > 0 && tenantId != null) {
            log.info("TelecomController() 生成工单操作开始");
            try {
                SysLog SysLogIns = new SysLog();
                SysLogIns.setAPP_NAME("ORDERCONTROL-"+CommController.class.getName()+"-startGenOrder");
                SysLogIns.setTENANT_ID(tenantId);
                SysLogIns.setLOG_TIME(new Date());
                SysLogIns.setLOG_MESSAGE("收到电信生成工单启动请求");
                // 排查 No operations allowed after connection closed. 错误
//                SysFunctionIns.saveSysLog(SysLogIns);
                // --- 调用service执行对应的功能 ---
                JsonResultIns=  SysFunctionIns.startTelecomGenOrder(tenantId,activityList, '1');
                SysLogIns.setLOG_TIME(new Date());
                SysLogIns.setLOG_MESSAGE(JsonResultIns.getMessage());
                SysLogIns.setBUSI_ITEM_1(JsonResultIns.getStatus());
                SysLogIns.setBUSI_ITEM_2("生成电信工单调度结束");
//                SysFunctionIns.saveSysLog(SysLogIns);
            } catch (Exception ex) {
                message = ex.getMessage();
                log.error(">>>>>>>>>>请求工单生成服务发生错误：" + ex.getMessage());
                ex.printStackTrace();
            }
            log.info("ActivityController() 生成工单操作结束");
        }
        } catch (Exception ex) {
            message = ex.getMessage();
        }
        //返回的结果
        return JSON.toJSONString(JsonResultIns);
    }
}
