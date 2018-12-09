package com.bonc.busi.assemble.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bonc.busi.assemble.DataAssembler;
import com.bonc.busi.entity.ActivityBo;
import com.bonc.busi.entity.CostPo;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.common.utils.FreemarkerUtil;

/**
 * 沃视窗渠道数据组装实现类
 * @author sky
 *
 */
@Component(value="wSCDataAssembler")
public class WSCDataAssembler implements DataAssembler {

	@Override
	public Object assembleData(Object object) throws Exception{
		ActivityBo bo = new ActivityBo();
    	StringWriter sw = new StringWriter();
    	bo.getPo().setProvId("-1");
    	bo.getPo().setCityId("1");
    	bo.getPo().setSplitType("");
    	bo.getPo().setActivityId("111");
    	bo.getPo().setActivityName("测试");
    	bo.getPo().setStartDate(new Date());
    	bo.getPo().setEndDate(new Date());
    	bo.setProvIds("1,2,3");
    	bo.getPo().setUserGroupId("test");
    	bo.getPo().setUserGroupName("test");
    	bo.getPo().setCycleInfo("day");
    	CostPo c = new CostPo();
    	c.setCostRule("aaa");
    	bo.setCostInfo(c);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("bo", bo);
    	map.put("status", "0");
		FreemarkerUtil.print("com/bonc/busi/template","activityConfig-template.ftl", map,sw);
		 return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.WSC.getCode().equals(channelId);
	}
}