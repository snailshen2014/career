package com.bonc.controller;


import com.alibaba.fastjson.JSON;
import com.bonc.busi.backpage.BackPageService;
import com.bonc.busi.backpage.bo.ActivityPo;
import com.bonc.busi.backpage.bo.ActivityStatistics;
import com.bonc.busi.backpage.bo.ComparatorActivity;
import com.bonc.busi.backpage.bo.CreateTenantBo;
import com.bonc.busi.backpage.bo.StaticsticCompator;
import com.bonc.busi.sys.entity.ActivityStatus;
import com.bonc.utils.HttpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by MQZ on 2017/8/16.
 */
@Controller
@RequestMapping("/back")
public class BackPageController {
    @Autowired
    BackPageService service;

    @RequestMapping(value="/register")
    public String register(HttpServletRequest request){
    	//登陆时选择的租户
        String tenantId = request.getParameter("tenant_id");
        //登陆时的密码
        String pass = request.getParameter("pass");
        //登陆时选择的租户类别：新租户 or 已有租户
        String isNewTenant = request.getParameter("isNewTenant");
        if(pass!=null && pass.equals("ordertask")){
            request.getSession().setAttribute("tenantId",tenantId);
            request.getSession().setAttribute("isNewTenant", isNewTenant);
            return "/back/index";
        }
        return "/back/main";
    }

    @RequestMapping(value = "/main")
    public String main(HttpServletRequest request) {
    	request.getSession().invalidate();  //跳转到登录页时，要先清除上次登录时记录的Session信息
        return "/back/main";
    }

    /**
     * 页面跳转
     *
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        return "/back/index";
    }

    @RequestMapping(value = "/cfgConfig")
    public String cfgConfig() {
        return "/back/cfgConfig";
    }

    @RequestMapping(value = "/monitor")
    public String monitor() {
        return "/back/ordergeneratemonitor";
    }
    
    @RequestMapping(value = "/statistics")
    public String statistics(){
    	return "/back/statistics";
    }

    @RequestMapping(value = "/tableUsed")
    public String tableUsed() {
        return "/back/tableUsed";
    }

    @RequestMapping(value = "/addTenant")
    public String addTenant() {
        return "/back/addTenant";
    }

    @RequestMapping(value = "/genXSql")
    public String genXSql() {
        return "/back/genXSql";
    }


    @RequestMapping(value = "tableUsedList", method = {RequestMethod.GET})
    @ResponseBody
    public Object tableUsedList(String tanantId) {
        System.out.println(tanantId);
        HashMap<String, Object> req = new HashMap<>();
        try {
            return JSON.toJSONString(service.getUsedTableList(tanantId));
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            resp.put("rows", new ArrayList<Integer>());
            resp.put("total", 0);
            return resp;
        }
    }
    
    /**
     * 对工单表的容量进行扩容,调用一次工单表的容量会增加5000000
     */
    @RequestMapping(value="/addTableCapacity", method = RequestMethod.GET)
    @ResponseBody
    public Object addTableCapacity(HttpServletRequest request){
    	String tenantId = request.getParameter("tenantId");
    	Map<String, String> resultData = new HashMap<String,String>();
    	service.addTableCapacity(tenantId);
    	resultData.put("code", "0000");
    	return resultData;
    }


    @RequestMapping(value = "getTenantId", method = {RequestMethod.GET})
    @ResponseBody
    public Object getTenantId() {
        try {
            return JSON.toJSONString(service.getValidTenantInfo());
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            return resp;
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/activityList")
    @ResponseBody
    public String getActivityList(HttpServletRequest req) {
        List<ActivityPo> list = new ArrayList<ActivityPo>();
        HashMap<String, Object> resp = new HashMap<String, Object>();
        String tenantId = req.getParameter("tenantId");
        System.out.println("活动列表 租户Id ：" + tenantId);
        if (tenantId != null) {
            String activityId = req.getParameter("activityId");
            list = service.getActivityList(tenantId, activityId);
            Collections.sort(list, new ComparatorActivity());
        }
        resp.put("total", list.size());
        resp.put("rows", list);
        String jsonStr = JSON.toJSONString(resp);
        System.out.println(jsonStr);
        return JSON.toJSONString(resp);
    }

    @RequestMapping(value = "/orderGenerateStep")
    @ResponseBody
    public String getActivityList1(HttpServletRequest req) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        List<Map<String, Object>> step = new ArrayList<Map<String, Object>>();
        String tenantId = req.getParameter("tenantId");
        if (tenantId != null) {
            if (req.getParameter("activityId") != null && !req.getParameter("activityId").trim().equals("")) {
                String activityId = req.getParameter("activityId");
                System.out.println("请求的活动Id:" + activityId);
                step = service.getActivityOrderGenerateStep(activityId, tenantId);
            }
        }
        resp.put("total", step.size());
        resp.put("rows", step);
        String jsonStr = JSON.toJSONString(resp);
        System.out.println(jsonStr);
        return JSON.toJSONString(resp);
    }

    /**
     * 重跑活动工单
     *
     * @return
     */
    @RequestMapping(value = "recycleActivityOrder")
    public void recycleActivityOrder(HttpServletRequest req, HttpServletResponse res) {
        String activityId = req.getParameter("activityId");
        String tenantId = req.getParameter("tenantId");
        if (tenantId != null && !tenantId.equals("") && activityId != null && !activityId.trim().equals("")) {
            service.recycleActivityOrder(activityId, tenantId);
        }
        try {
            res.sendRedirect("monitor");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "sysCfg", method = {RequestMethod.GET})
    @ResponseBody
    public Object sysCfg() {
        HashMap<String, Object> req = new HashMap<>();
        try {
            return JSON.toJSONString(service.getSysCfg());
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            resp.put("rows", new ArrayList<Integer>());
            resp.put("total", 0);
            return resp;
        }
    }

    @RequestMapping(value = "delCfgRow", method = {RequestMethod.GET})
    @ResponseBody
    public Object delCfgRow(String key) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            service.delCfgRow(key);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }


    @RequestMapping(value = "insertOrUpdateCfg", method = {RequestMethod.POST})
    @ResponseBody
    public Object insertOrUpdateCfg(@RequestBody String requst) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            Map cfgMap = JSON.parseObject(requst, Map.class);
            service.insertOrUpdateCfg(cfgMap);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }

    @RequestMapping(value = "initTenantData", method = {RequestMethod.POST})
    @ResponseBody
    public Object initTenantData(@RequestBody String requst) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            CreateTenantBo cfg = JSON.parseObject(requst, CreateTenantBo.class);
            service.initTenantData(cfg);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }

    /**
     * localhost:17001/ordercontrol/back/simpleInitTenantData
     *      {
             "tenantName": "测试",
             "tenantId": "uni081",
             "provId": "081"
            }
     * @return
     */
    @RequestMapping(value = "simpleInitTenantData", method = {RequestMethod.POST})
    @ResponseBody
    public Object simpleInitTenantData(@RequestBody String requst) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            CreateTenantBo cfg = JSON.parseObject(requst, CreateTenantBo.class);
            service.simpleInitTenantData(cfg);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }

    @RequestMapping(value = "XSqlSelect", method = {RequestMethod.GET})
    @ResponseBody
    public Object XSqlSelect(String tenantId) {
        HashMap<String, Object> req = new HashMap<>();
        try {
            return JSON.toJSONString(service.XSqlSelect(tenantId));
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            resp.put("rows", new ArrayList<Integer>());
            resp.put("total", 0);
            return resp;
        }
    }

    @RequestMapping(value = "XSqlTable", method = {RequestMethod.GET})
    @ResponseBody
    public Object XSqlTable(String tenantId) {
        HashMap<String, Object> req = new HashMap<>();
        try {
            return JSON.toJSONString(service.XSqlTable(tenantId));
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            resp.put("rows", new ArrayList<Integer>());
            resp.put("total", 0);
            return resp;
        }
    }

    @RequestMapping(value = "XSqlWhere", method = {RequestMethod.GET})
    @ResponseBody
    public Object XSqlWhere(String tenantId) {
        HashMap<String, Object> req = new HashMap<>();
        try {
            return JSON.toJSONString(service.XSqlWhere(tenantId));
        } catch (Exception e) {
            HashMap<String, Object> resp = new HashMap<String, Object>();
            e.printStackTrace();
            resp.put("rows", new ArrayList<Integer>());
            resp.put("total", 0);
            return resp;
        }
    }

    @RequestMapping(value = "delSelectRow", method = {RequestMethod.GET})
    @ResponseBody
    public Object delSelectRow(String key ,String tenantId) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            service.delSelectRow(key,tenantId);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }

    @RequestMapping(value = "delTableRow", method = {RequestMethod.GET})
    @ResponseBody
    public Object delTableRow( String key ,String tenantId) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            service.delTableRow(key,tenantId);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }

}

    @RequestMapping(value = "delWhereRow", method = {RequestMethod.GET})
    @ResponseBody
    public Object delWhereRow( String key ,String tenantId) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        try {
            service.delWhereRow(key,tenantId);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }

    @RequestMapping(value = "insertOrUpdateXSQL", method = {RequestMethod.POST})
    @ResponseBody
    public Object insertOrUpdateXSQL(@RequestBody String req) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        Map map = JSON.parseObject(req, Map.class);
        service.insertOrUpdateXSQL(map);
        try {
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }
    
    @RequestMapping(value = "stopActivity", method = {RequestMethod.GET})
    @ResponseBody
    public Object stopActivity(HttpServletRequest req) {
        HashMap<String, Object> resp = new HashMap<String, Object>();
        String tenantId = req.getParameter("tenantId"); 
        String activityId = req.getParameter("activityId");
        String serviceURL = req.getParameter("serviceURL");
        try {
        	service.stopActivity(tenantId, activityId,serviceURL);
            resp.put("code", "0");
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", "1");
            return resp;
        }
    }
    
    @RequestMapping(value = "/activityStatistics/{tenantId}" , method = RequestMethod.GET)
    @ResponseBody
    public Object activityStatisticsInfo(@PathVariable("tenantId") String tenantId){
    	System.out.println("---------------------" + tenantId);
    	List<ActivityStatistics> list = new ArrayList<ActivityStatistics>();
    	list = service.getActivityStatisticsList(tenantId);
    	return JSON.toJSONString(list);
    }
}
