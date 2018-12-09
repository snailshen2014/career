package com.bonc.service;

import java.util.List;

/**
 * Created by MQZ on 2017/6/7.
 */
public interface ServiceControl {

    /*
     * 工单生成
     */
    public boolean generateOrder(String tenantId , List<String> activityList);

}
