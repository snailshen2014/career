package com.bonc.busi.scene.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.scene.mapper.SceneMapper;
import com.bonc.busi.scene.service.SceneService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("sceneService")
public class SceneServiceImpl implements SceneService{

	@Autowired
	private SceneMapper mapper;
	
	@Override
	public HashMap<String, Object> querySuccessNum(HashMap<String, Object> req) {
		//先查询活动信息
		List<Object> recIds = mapper.queryActivitySeq(req);
		if(recIds.size()<=0){
			throw new BoncExpection(IContants.CODE_FAIL," activity not exists! ");
		}
		
		String recSql = "";
		for(int i=0 ,max = recIds.size()-1;i<=max;i++){
			recSql = (recSql+recIds.get(i)+(i==max?"":","));
		}
		req.put("recSql", recSql);
		
		if(!StringUtil.validateStr(req.get("contactDateStart"))){
			req.put("contactDateStartSql", " AND BEGIN_DATE>=#{contactDateStart} ");
		}
		if(!StringUtil.validateStr(req.get("contactDateEnd"))){
			req.put("contactDateEndSql", " AND BEGIN_DATE<=#{contactDateEnd} ");
		}
		
		HashMap<String, Object> resp = mapper.querySuccessNum(req);
		return resp;
	}
}
