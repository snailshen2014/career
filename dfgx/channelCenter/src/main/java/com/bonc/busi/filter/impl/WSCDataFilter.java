package com.bonc.busi.filter.impl;

import org.springframework.stereotype.Component;

import com.bonc.busi.filter.DataFilter;
import com.bonc.common.utils.ChannelEnum;

/**
 * 沃视窗渠道数据过滤  实现类
 * 
 * @author sky
 *
 */
@Component(value="wSCDataFilter")
public class WSCDataFilter implements DataFilter {

	@Override
	public Object filterData(Object object)  throws Exception{
		// TODO Auto-generated method stub
		 return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.WSC.getCode().equals(channelId);
	}
}