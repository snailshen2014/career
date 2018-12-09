package com.bonc.busi.task.base;

import com.bonc.busi.task.mapper.BaseMapper;
import kafka.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.instance.SceneGenOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Properties;

public class KafkaInit implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(UpdateOrderUserInfo.class);

    private String tenantId;

    KafkaInit(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(30 * 1000l);
            log.info(this.tenantId + "  begin start kafka task!");
            SceneGenOrder sceneGenOrder = new SceneGenOrder();
            sceneGenOrder.setMaxQueueSize(10000);
            sceneGenOrder.setTenantId(this.tenantId);
            ParallelManage ParallelManageIns = new ParallelManage(sceneGenOrder, 1);
            ParallelManageIns.execute();
            log.info(this.tenantId + "load kafka success!");
        } catch (Exception e) {
            log.info("load kafka error!" + e.getMessage());
        }

    }

}
