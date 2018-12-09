package com.bonc.service.impl;

import com.bonc.busi.orderschedule.GenOrderIns;
import com.bonc.busi.task.base.Global;
import com.bonc.service.ServiceControl;
import com.bonc.service.func.EntryThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MQZ on 2017/6/7.
 */
@Service("ServiceControl")
public class ServiceControlImpl implements ServiceControl {

    private final static Logger log= LoggerFactory.getLogger(ServiceControlImpl.class);
    @Autowired private GenOrderIns GenOrderIns;

    @Override
    public boolean generateOrder(String tenantId, List<String> activityList) {

        Map<String, Object > tenantIdAndActivityList = new HashMap<String, Object>();
        tenantIdAndActivityList.put("tenantId", tenantId);
        tenantIdAndActivityList.put("activityList", activityList);
        Thread ThreadIns = new EntryThread(GenOrderIns, tenantIdAndActivityList);
        // --- 执行 ---
        Global.getExecutorService().execute(ThreadIns);
        return true;

    }
}
