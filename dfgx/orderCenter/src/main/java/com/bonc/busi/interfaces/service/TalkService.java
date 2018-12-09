package com.bonc.busi.interfaces.service;

import java.util.HashMap;
import java.util.List;


public interface TalkService {
	/**
	 * 将 talk 中的话术变量进行替换
	 * @param talk 源变量字符串
	 * @param user 用户信息
	 *  userId 用户ID
	 *  phoneNumber 手机号码
	 *  ...... 用户全量非实时信息
	 * @return 变量替换后的结果
	 */
	String exchangeTalkVal(HashMap<String, String> talk);
	
	HashMap<String, String> exchangeTalkVals(List<HashMap<String, String>> req);

}
