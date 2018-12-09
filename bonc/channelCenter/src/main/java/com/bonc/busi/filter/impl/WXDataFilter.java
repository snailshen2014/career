package com.bonc.busi.filter.impl;

import org.springframework.stereotype.Component;

import com.bonc.busi.filter.DataFilter;
import com.bonc.common.utils.ChannelEnum;

/**
 * 微信渠道数据过滤实现类
 * @author sky
 *
 */
@Component(value="wXDataFilter")
public class WXDataFilter implements DataFilter {

	@Override
	public Object filterData(Object object)  throws Exception{
		// TODO Auto-generated method stub
		 return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.WX.getCode().equals(channelId);
	}
}