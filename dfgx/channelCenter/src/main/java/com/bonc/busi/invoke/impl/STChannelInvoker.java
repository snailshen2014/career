package com.bonc.busi.invoke.impl;

import org.springframework.stereotype.Component;

import com.bonc.busi.invoke.ChannelInvoker;
import com.bonc.common.utils.ChannelEnum;

/**
 * 手厅渠道   数据下发
 * @author sky
 *
 */
@Component(value="sTChannelInvoker")
public class STChannelInvoker implements ChannelInvoker {

	@Override
	public String invoke(Object object) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.ST.getCode().equals(channelId);
	}
	
	
}