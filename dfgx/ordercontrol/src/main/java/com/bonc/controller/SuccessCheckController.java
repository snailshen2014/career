package com.bonc.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.backpage.SuccessPageService;

@Controller
@RequestMapping("/success")
public class SuccessCheckController {

	@Autowired
	SuccessPageService successPageService;

	@RequestMapping(value = "/successFilter")
	public String successFilter() {
		return "/back/successFilter";
	}
	@RequestMapping(value = "/successCheck")
	public String successCheck() {
		return "/back/successCheck";
	}


	/*@RequestMapping(value = "/filterindex")
	public String successFilterMain(HttpServletRequest request) {
		return "/back/successFilterNew";
	}*/

/*	@RequestMapping(value = "filter", method = { RequestMethod.GET })
	@ResponseBody
	public Object getCurrentSucessFilterlog(@Param("tenantId") String tenantId, @Param("pageSize") String pageSize,
			@Param("pageNumber") String pageNumber) {

		System.out.println("tenantId--" + tenantId + " pageSizeTemp== " + pageSize + " pageNumber== " + pageNumber);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		Integer.valueOf(pageSize);
		int currentPage = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(pageSize);
		;

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		String month = df.format(new Date()).substring(4, 6);

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df1.format(new Date()) + " 00:00:00";

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tenantId", tenantId);
		param.put("month", month);
		param.put("flag", "91");
		param.put("currentPage", currentPage);
		param.put("pageNum", Integer.valueOf(pageSize));
		param.put("date", date);
		try {
			int successLogNum = 0;
			successLogNum = successPageService.getSuccessLogNum(param);
			List<HashMap> list = new ArrayList<HashMap>();
			list = successPageService.getCurrentSucesslog(param);

			resp.put("total", successLogNum);
			resp.put("rows", list);
			String jsonStr = JSON.toJSONString(resp);
			System.out.println(jsonStr);
			return JSON.toJSONString(resp);
		} catch (Exception e) {

			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp.toString();
		}

	}*/

	@RequestMapping(value = "activityfilter", method = { RequestMethod.GET })
	@ResponseBody
	public Object getActivitySuccess(@Param("tenantId") String tenantId, @Param("pageSize") String pageSize,
			@Param("pageNumber") String pageNumber, @Param("activityId") String activityId) {

		System.out.println("tenantId--" + tenantId + " pageSizeTemp== " + pageSize + " pageNumber== " + pageNumber
				+ "activityId=" + activityId);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		int successLogNum = 0;
		int currentPage = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(pageSize);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tenantId", tenantId);
		param.put("currentPage", currentPage);
		param.put("pageNum", Integer.valueOf(pageSize));
		param.put("activityId", activityId);
		try {
			successLogNum = successPageService.getActivitySuccessNum(param);
			list = successPageService.getActivitySuccessByPage(param);

			resp.put("total", successLogNum);
			resp.put("rows", list);
			String jsonStr = JSON.toJSONString(resp);
			System.out.println(jsonStr);
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp.toString();
		}

	}

	@RequestMapping(value = "activityfilterdetail", method = { RequestMethod.GET })
	@ResponseBody
	public Object getSuccessFilter(String activitySeqId, String beginTime, String tenantId) {

		System.out.println("tenantId--" + tenantId + " activitySeqId== " + activitySeqId + " beginTime== " + beginTime);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tenantId", tenantId);
		param.put("activitySeqId", activitySeqId);
		param.put("month", beginTime.substring(5, 7));
		param.put("flag", "91");
		try {
			list = successPageService.getActivitysuccessDetail(param);
			resp.put("total", list.size());
			resp.put("rows", list);
			String jsonStr = JSON.toJSONString(resp);
			System.out.println(jsonStr);
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp.toString();
		}

	}

	@RequestMapping(value = "successcheckdetail", method = { RequestMethod.GET })
	@ResponseBody
	public Object getSuccessCheckDetail(String dateId, String beginTime, String endTime, String tenantId,String pageSize, String pageNumber) {

		System.out.println(
				"tenantId--" + tenantId + " dateId== " + dateId + " beginTime== " + beginTime + "endTime==" + endTime);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		int successLogNum = 0;
		
		int currentPage = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(pageSize);
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("tenantId", tenantId);
		param.put("month", beginTime.substring(5, 7));
		param.put("flag", "01");
		param.put("dateId", dateId);
		param.put("beginTime", beginTime);
		param.put("endTime", endTime);
		param.put("currentPage", currentPage);
		param.put("pageNum", Integer.valueOf(pageSize));
		try {
			successLogNum = successPageService.getSuccessCheckNum(param);
			list = successPageService.getSuccessCheckDetail(param);
			resp.put("total", successLogNum);
			resp.put("rows", list);
			String jsonStr = JSON.toJSONString(resp);
			System.out.println("getSuccessCheckDetail=="+jsonStr);
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp.toString();
		}

	}

	@RequestMapping(value = "successcheck", method = { RequestMethod.GET })
	@ResponseBody
	public Object getSuccessCheck(@Param("pageSize") String pageSize, @Param("pageNumber") String pageNumber,
			String tenantId, String dateId) {

		System.out.println("tenantId--" + tenantId + " pageSize== " + pageSize + " pageNumber== " + pageNumber
				+ "dateId=" + dateId);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		int successLogNum = 0;
		int currentPage = (Integer.valueOf(pageNumber) - 1) * Integer.valueOf(pageSize);
		List<HashMap<String, Object>> dateIdlist = new ArrayList<HashMap<String, Object>>();

		try {
			// --得到账期--
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("tenantId", tenantId);
			param.put("dateId", dateId);
			param.put("currentPage", currentPage);
			param.put("pageNum", Integer.valueOf(pageSize));
			dateIdlist = successPageService.getDateId(param);
			//--得到成功标准数量--
			successLogNum = successPageService.getDateIdNum(param);

			List<HashMap<String, Object>> checklist = new ArrayList<HashMap<String, Object>>();
			for (Map<String, Object> dateIdTemp : dateIdlist) {
				String dateIdOne = (String) dateIdTemp.get("DATEID");
				String month = ((String) dateIdTemp.get("START_TIME")).substring(4, 6);
				param.put("dateId", dateIdOne);
				param.put("month", month);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = successPageService.getCheckOverview(param);
				if(map==null){
					HashMap<String, Object> mapNew = new HashMap<String, Object>();
					mapNew.put("DATEID", param.get("dateId"));
					checklist.add(mapNew);
				}else{
					checklist.add(map);
				}
				
			}

			resp.put("total", successLogNum);
			resp.put("rows", checklist);
			String jsonStr = JSON.toJSONString(resp);
			System.out.println(jsonStr);
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("rows", new ArrayList<Integer>());
			resp.put("total", 0);
			return resp.toString();
		}

	}

}
