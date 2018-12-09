package com.bonc.busi.backpage.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.backpage.AsynPageService;
import com.bonc.busi.backpage.mapper.AsynPageMapper;

@Service("AsynPageService")
public class AsynPageServiceImpl implements AsynPageService {
	@Autowired  AsynPageMapper mapper;
	
	public List<Map<String,Object>> getAsynUserLabel(String tenantId) {
        return mapper.getAsynUserLabel(tenantId);
	}   

	public List<Map<String,Object>> getAsynUserLabelParam(String tenantId,int begin,int end) {
        return mapper.getAsynUserLabelParam(tenantId,begin,end );
  
	}
	
	
	public int getAsynUserLabelTotal(String tenantId) {
        return mapper.getAsynUserLabelTotal(tenantId);
  
	}


}
