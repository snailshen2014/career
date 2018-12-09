package com.bonc.busi.scene.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.common.base.JsonResult;

public interface SceneService {
	//场景营销查询成功数量
	HashMap<String, Object> querySuccessNum(HashMap<String, Object> req);
	
	//场景能力插入数据
	JsonResult addSenceRecordBatch(HashMap<Object, Object> request);
	//场景能力查询状态
	JsonResult queryScencePowerStatus(HashMap<Object, String> request);

	//存储场景营销活动信息
	public JsonResult startSceneMarketActivity(String response);

	/**
	 * 查询工单服务 （场景营销移植）
	 * @param req
	 * @return
	 */
	public List<HashMap<String, Object>> queryActivityOrderInfo(HashMap<String,Object> req);
}
