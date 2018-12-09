package com.bonc.busi.count.service;

import java.util.List;


import com.bonc.busi.count.model.SendCount;

public interface MessageSendService {

	List<SendCount> findCountList(SendCount sendCount);

	String findCountTotal(SendCount sendCount);

}
