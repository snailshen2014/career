package com.bonc.busi.filter.impl;

import org.springframework.stereotype.Component;

import com.bonc.busi.filter.DataFilter;
import com.bonc.common.utils.ChannelEnum;

/**
 * 网厅渠道数据过滤  实现类
 * 
 * @author sky
 *
 */
@Component(value="wTDataFilter")
public class WTDataFilter implements DataFilter {

	@Override
	public Object filterData(Object object)  throws Exception{
		// TODO Auto-generated method stub
		 return null;
	}

	@Override
	public Boolean supports(String channelId) {
		
		return ChannelEnum.WT.getCode().equals(channelId);
	}
}