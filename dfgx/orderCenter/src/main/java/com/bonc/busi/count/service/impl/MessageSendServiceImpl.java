package com.bonc.busi.count.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.count.mapper.MessageSendMapper;
import com.bonc.busi.count.model.SendCount;
import com.bonc.busi.count.service.MessageSendService;



@Service
public class MessageSendServiceImpl implements MessageSendService{
	@Autowired
	private MessageSendMapper messageSendMapper;
	@Override
	public List<SendCount> findCountList(SendCount sendCount) {
		List<SendCount> list =  messageSendMapper.findCountList(sendCount);
		return list;
	}
	@Override
	public String findCountTotal(SendCount sendCount) {
		return  messageSendMapper.findCountTotal(sendCount);
	}
}
