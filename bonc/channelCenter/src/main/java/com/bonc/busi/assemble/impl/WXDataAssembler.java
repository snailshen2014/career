package com.bonc.busi.assemble.impl;

import org.springframework.stereotype.Component;

import com.bonc.busi.assemble.DataAssembler;
import com.bonc.common.utils.ChannelEnum;

/**
 * 微信渠道  数据组装实现类
 * 
 * @author sky
 *
 */
@Component(value="wXDataAssembler")
public class WXDataAssembler implements DataAssembler {

	@Override
	public Object assembleData(Object object) throws Exception{
		// TODO Auto-generated method stub
		 return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.WX.getCode().equals(channelId);
	}
}