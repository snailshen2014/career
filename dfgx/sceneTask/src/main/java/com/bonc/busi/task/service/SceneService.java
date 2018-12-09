package com.bonc.busi.task.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.common.base.JsonResult;

public interface SceneService {
	void dealfailsms();

	// 场景营销查询成功数量
	HashMap<String, Object> querySuccessNum(HashMap<String, Object> req);

	// 场景能力插入数据
	JsonResult addSenceRecordBatch(HashMap<Object, Object> request);

	// 场景能力查询状态
	JsonResult queryScencePowerStatus(HashMap<Object, String> request);

	// 场景营销查询接触数量
	List<HashMap<String, Object>> queryHandleNum(HashMap<String, Object> req);
}
