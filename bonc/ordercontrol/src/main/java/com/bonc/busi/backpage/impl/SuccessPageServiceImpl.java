package com.bonc.busi.backpage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.backpage.SuccessPageService;
import com.bonc.busi.backpage.bo.OrderStatus;
import com.bonc.busi.backpage.mapper.SuccessCheckPageMapper;

import com.bonc.busi.sys.dao.SyscommcfgDao;
@Service
public class SuccessPageServiceImpl implements SuccessPageService {
	@Autowired SuccessCheckPageMapper SuccessCheckPageMapperIns;
	@Autowired SyscommcfgDao SyscommcfgDao;
	@Override
	//public List<HashMap> getCurrentSucesslog(String month,String flag,String tenantId,int currentPage,int pageNum) {
		
		//return SuccessCheckPageMapperIns.getCurrentSucesslog( month, flag, tenantId, currentPage, pageNum);
	//}
	public List<HashMap> getCurrentSucesslog(Map<String, Object> param) {
		
		return SuccessCheckPageMapperIns.getCurrentSucesslog((String)param.get("month"),(String)param.get("flag"),(String)param.get("tenantId"),(String)param.get("date"),(Integer)param.get("currentPage"),(Integer)param.get("pageNum"));
	}
	public int getSuccessLogNum(Map<String, Object> param){
		int successLogNum = 0;
		successLogNum = SuccessCheckPageMapperIns.getSuccessLogNum((String)param.get("month"),(String)param.get("flag"),(String)param.get("tenantId"),(String)param.get("date"));
		return successLogNum;
	}
	
	//--分页展示曾跑过成功标准的活动--
	@Override
	public List<HashMap<String,Object>> getActivitySuccessByPage(Map<String, Object> param){
		List<HashMap<String,Object>> activityList = new ArrayList<HashMap<String,Object>>();
		activityList = SuccessCheckPageMapperIns.getActivitySuccessByPage(param);
		String runFlag =  SyscommcfgDao.query("ORDERCHECK.FILTER.RUNFLG."+param.get("tenantId"));
		for(HashMap<String,Object> activity:activityList){
			String orderStatus = OrderStatus.success.toString();
			if(activity.get("END_DATE")!=null && ((String) activity.get("DESC")).trim().equals("成功标准过滤")){
				activity.put("FILTER_STATUS", orderStatus);
			}else if(activity.get("END_DATE")!=null && ((String) activity.get("DESC")).trim().equals("调用事前成功检查出错")){
				orderStatus = OrderStatus.failed.toString();
				activity.put("FILTER_STATUS", orderStatus);
			}else if (activity.get("END_DATE")==null && runFlag.equals("TURE")){
				orderStatus =  OrderStatus.running.toString();
				activity.put("FILTER_STATUS", orderStatus);
			}else{
				activity.put("FILTER_STATUS", null);
			}
		}
		return activityList;
	}
	//--统计含有成功标准活动的数量--
	@Override
	public int getActivitySuccessNum(Map<String, Object> param){
		int activityNum = 0;
		activityNum = SuccessCheckPageMapperIns.getActivitySuccessNum(param);
		return activityNum;
	}
	//--展示该活动的成功标准过滤详情--
	@Override
		public  List<HashMap<String,Object>> getActivitysuccessDetail(Map<String, Object> param){
		List<HashMap<String,Object>> activityList = new ArrayList<HashMap<String,Object>>();
		activityList = SuccessCheckPageMapperIns.getActivitysuccessDetail(param);
		return activityList;
		}
	//--事后成功标准检查得到所有账期--
	@Override
		public List<HashMap<String,Object>> getDateId(Map<String, Object> param){
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		list = SuccessCheckPageMapperIns.getDateId(param);
		return list;
	}
	
	//--根据账期获得成功标准检查总览--
	@Override
		public HashMap<String,Object> getCheckOverview(Map<String, Object> param){
		HashMap<String,Object> map =new  HashMap<String,Object>();
		String orderStatus = OrderStatus.success.toString();
		map = SuccessCheckPageMapperIns.getCheckOverview(param);
		String curRunFlag = SyscommcfgDao.query("ORDERCHECK.SUCESS.RUNFLG." + param.get("tenantId"));
		if(map!=null && curRunFlag.equals("FALSE")){
			//--跑完的成功检查检查是否出错--
			Map<String, Object> ifErrorParam = new HashMap<String, Object>();
			ifErrorParam.putAll(map);
			ifErrorParam.put("month", param.get("month"));
			int count = SuccessCheckPageMapperIns.getCheckOverviewError(ifErrorParam);
			if(count > 0){
				orderStatus =  OrderStatus.failed.toString();
				map.put("CHECK_STATUS", orderStatus);
			}else if(count == 0){
				map.put("CHECK_STATUS", orderStatus);
			}
			
			//--账期重跑--
		}else if(map!=null && curRunFlag.equals("TURE")){
			map = SuccessCheckPageMapperIns.getCheckOverviewIng(param);
			orderStatus =  OrderStatus.running.toString();
			map.put("CHECK_STATUS", orderStatus);
			//--新的账期开始--
		}else if((map == null||map.size() == 0)&&curRunFlag.equals("TURE")){
			map = SuccessCheckPageMapperIns.getCheckOverviewIng(param);
			if(map!=null){
				orderStatus =  OrderStatus.running.toString();
				map.put("CHECK_STATUS", orderStatus);
			}
			
		}
		
		return map;
	}
	
	//--得到成功标准账期数量--
	@Override
		public int getDateIdNum(Map<String, Object> param){
		int dateIdNum = 0;
		dateIdNum = SuccessCheckPageMapperIns.getDateIdNum(param);
		return dateIdNum;
	}
	
	//--展示该活动的成功标准检查详情--
	@Override
			public  List<HashMap<String,Object>> getSuccessCheckDetail(Map<String, Object> param){
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		list = SuccessCheckPageMapperIns.getSuccessCheckDetail(param);
		return list;
	}
			
		//--得到成功标准详情数量--
	@Override
		public int getSuccessCheckNum(Map<String, Object> param){
		int detailNum = 0;
		detailNum = SuccessCheckPageMapperIns.getSuccessCheckNum(param);
		return detailNum;
	}
}
