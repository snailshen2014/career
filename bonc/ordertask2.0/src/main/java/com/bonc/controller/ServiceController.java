package com.bonc.controller;

import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;
import com.bonc.busi.orderschedule.routing.OrderTableManager;
import com.bonc.busi.task.bo.SysCommonCfg;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.base.JsonResult;
import com.bonc.service.ServiceControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import javax.websocket.server.PathParam;

/**
 * Created by MQZ on 2017/6/7.
 */
@RestController
@RequestMapping("/service")
public class ServiceController {
    private final static Logger log = LoggerFactory.getLogger(ServiceController.class);
    @Autowired
    private ServiceControl  serviceControl;
    
    @Autowired
    private BaseMapper mapper;

    /*
     *   工单生成服务
	 */
    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
    public JsonResult generateOrder(@RequestBody Map<String, Object> request) {
        List<String> ActivityIdList = (List<String>) request.get("ActivityIdList");
        String TenantId = (String) request.get("TENANT_ID");
        boolean flag = serviceControl.generateOrder(TenantId, ActivityIdList);
        JsonResult	JsonResultIns = new JsonResult();
        if(flag){
            JsonResultIns.setCode("000000");
            JsonResultIns.setMessage("sucess");
        }
        else{
            JsonResultIns.setCode("000001");
            JsonResultIns.setMessage("failed");
        }
        return JsonResultIns;
    }

    /**
     * 工单表分配接口
     */
    @RequestMapping(value = "/getAssignedTable", method = RequestMethod.POST)
    public String getAssignedTable(@RequestParam("activityId") String activityId,@RequestParam("rows") Integer rows,
                                 @RequestParam("activitySeqId") int activitySeqId,@RequestParam("busiType") Integer busiType,
                                 @RequestParam("channelId") String channelId,@RequestParam("tenantId") String tenantId){
        String orderTableName = OrderTableManager.getAssignedTable(activityId,
                tenantId, channelId, busiType, rows, activitySeqId);
        return orderTableName;
    }
    
    /**
     * 更改sys_common表后，调用该服务刷新sysCommonCfgMap,避免重启应用
     * @return
     */
    @RequestMapping(value = "refreshSysCommonCfg")
	public JsonResult refreshSysCommonCfg() {
		JsonResult JsonResultIns = new JsonResult();
		List<SysCommonCfg> sysCommonCfgList = mapper.getAllSysCommonCfg();
		try {
			for (SysCommonCfg cfg : sysCommonCfgList) {
				SystemCommonConfigManager.sysCommonCfgMap.put(cfg.getCFG_KEY(), cfg.getCFG_VALUE());
				JsonResultIns.setCode("000000");
				JsonResultIns.setMessage("sucess");
			}
		} catch (Exception ex) {
			JsonResultIns.setCode("000001");
			JsonResultIns.setMessage(ex.getMessage());
		}
		return JsonResultIns;
	}
}
