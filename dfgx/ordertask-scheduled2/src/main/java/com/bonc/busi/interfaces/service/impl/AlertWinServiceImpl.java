package com.bonc.busi.interfaces.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.interfaces.mapper.AlertWinMapper;
import com.bonc.busi.interfaces.service.AlertWinService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.IContants;

@Transactional
@Service("alertWinService")
public class AlertWinServiceImpl implements AlertWinService{
	
	@Autowired
	private AlertWinMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	@Override
	public void updateLimitNum() {
		List<Map<String, Object>> tenantIds = BusiTools.getValidTenantInfo();
		for(Map<String, Object> map:tenantIds){
			map.put("CHANNEL_ID", IContants.TC_CHANNEL_1);
			mapper.updateLimitNum(map);
			map.put("CHANNEL_ID", IContants.TC_CHANNEL_2);
			mapper.updateLimitNum(map);
		}
	}

}
