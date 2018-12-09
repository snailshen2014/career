package com.bonc.busi.backpage;

import java.util.List;
import java.util.Map;

public interface AsynPageService {
	
	List<Map<String,Object>> getAsynUserLabel(String tenantId);
	
	List<Map<String,Object>> getAsynUserLabelParam(String tenantId,int begin ,int end);
	
	int getAsynUserLabelTotal(String tenantId);

	

}
