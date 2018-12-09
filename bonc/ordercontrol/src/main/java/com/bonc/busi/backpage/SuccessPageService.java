package com.bonc.busi.backpage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SuccessPageService {
//public List<HashMap> getCurrentSucesslog(String month,String flag,String tenantId,int currentPage,int pageNum);
	public List<HashMap> getCurrentSucesslog(Map<String, Object> param);
	//
	public int getSuccessLogNum(Map<String, Object> param);
	//--分页展示曾跑过成功标准的活动--
	public List<HashMap<String,Object>> getActivitySuccessByPage(Map<String, Object> param);
	//--统计含有成功标准活动的数量--
	public int getActivitySuccessNum(Map<String, Object> param);
	//--展示该活动的成功标准过滤详情--
	public  List<HashMap<String,Object>> getActivitysuccessDetail(Map<String, Object> param);
	//--事后成功标准检查得到所有账期--
	public List<HashMap<String,Object>> getDateId(Map<String, Object> param);
	//--根据账期获得成功标准检查总览--
	public HashMap<String,Object> getCheckOverview(Map<String, Object> param);
	//--得到成功标准账期数量--
	public int getDateIdNum(Map<String, Object> param);
	
	//--展示该活动的成功标准检查详情--
		public  List<HashMap<String,Object>> getSuccessCheckDetail(Map<String, Object> param);
		
	//--得到成功标准详情数量--
	public int getSuccessCheckNum(Map<String, Object> param);
	
}
