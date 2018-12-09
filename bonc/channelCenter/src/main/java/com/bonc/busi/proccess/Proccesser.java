package com.bonc.busi.proccess;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.bonc.busi.assemble.DataAssembler;
import com.bonc.busi.filter.DataFilter;
import com.bonc.busi.invoke.ChannelInvoker;
import com.bonc.common.utils.ChannelEnum;

/**
 * 业务处理 解析数据、过滤数据、组装数据、下发渠道、反馈结果
 * 
 * @author sky
 *
 */
@Component
public class Proccesser {
	/**
	 * 处理分发业务
	 * 
	 * @param object
	 * @return
	 */
	public String proccess(String channelId, DataFilter dataFilter, DataAssembler dataAssembler,
			ChannelInvoker channelInvoker, Object object) throws Exception{

		Assert.notNull(channelId, "channelId must not be null");
		Assert.notNull(channelInvoker, "channelInvoker must not be null");
		String result = null;
		
			if (!dataFilter.supports(channelId)) {
				throw new IllegalArgumentException(
						"DataFilter [" + dataFilter.getClass() + "] does not support to [" + ChannelEnum.getName(channelId) + "]");
			}
			 Object data1 = dataFilter.filterData(object);
			if (!dataAssembler.supports(channelId)) {
				throw new IllegalArgumentException(
						"DataAssembler [" + dataAssembler.getClass() + "] does not support to [" + ChannelEnum.getName(channelId) + "]");
			}
			Object data2 = dataAssembler.assembleData(data1);
			if (!channelInvoker.supports(channelId)) {
				throw new IllegalArgumentException(
						"ChannelInvoker [" + channelInvoker.getClass() + "] does not support to [" + ChannelEnum.getName(channelId) + "]");
			}
			result = channelInvoker.invoke(data2);
			
		return result;
	}

}