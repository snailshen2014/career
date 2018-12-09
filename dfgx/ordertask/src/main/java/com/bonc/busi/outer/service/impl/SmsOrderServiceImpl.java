package com.bonc.busi.outer.service.impl;

import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.outer.mapper.SmsOrderMapper;
import com.bonc.busi.outer.service.SmsOrderService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.Global;
import com.bonc.busi.task.mapper.BaseMapper;

@Service
public class SmsOrderServiceImpl implements SmsOrderService {
	
	private final static Logger log= LoggerFactory.getLogger(SmsOrderServiceImpl.class);
	
	@Autowired
	SmsOrderMapper mapper;
	
	@Autowired
	BaseMapper baseMapper;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BusiTools BusiToolsIns;

	/**
	 * 启动单独的线程完成移短信工单的操作，并记录结果
	 */
	@Override
	public void moveSmsOrderToHis(String activityId, int activitySeqId, String tenantId, String channelId,
			String hisTableName) {
		ExecutorService  excutor = Global.getExecutorService();
		SmsOrderMoveToHisTask task = new SmsOrderMoveToHisTask();
	    String moveOrderTemplate = baseMapper.getValueFromSysCommCfg("ACTIVITYSTATUS.MOVE");
	    String deleteOrderTemplate = baseMapper.getValueFromSysCommCfg("ACTIVITYSTATUS.DELETE");
	    
	    if(StringUtils.isBlank(moveOrderTemplate)||StringUtils.isBlank(deleteOrderTemplate)){
	    	log.error("ACTIVITYSTATUS.MOVE或ACTIVITYSTATUS.DELETE 没有在sys_common_cfg表里设置");
	    	return;
	    }
	    
		task.setActivityId(activityId);
		task.setActivitySeqId(activitySeqId);
		task.setTenantId(tenantId);
		task.setChannelId(channelId);
		task.setHisTableName(hisTableName);
		task.setMapper(mapper);
		task.setBaseMapper(baseMapper);
		task.setMoveOrderTemplate(moveOrderTemplate);
		task.setDeletOrderTemplate(deleteOrderTemplate);
		task.setJdbcTemplate(jdbcTemplate);
		task.setBusiToolsIns(BusiToolsIns);
		mapper.insertSmsOrderMoveRecordOrignStatus(activityId, activitySeqId, tenantId, channelId, "0");
		excutor.execute(task);
	}

}
