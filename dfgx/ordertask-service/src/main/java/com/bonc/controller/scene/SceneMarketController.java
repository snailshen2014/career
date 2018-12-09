package com.bonc.controller.scene;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.service.OrderActivityService;
import com.bonc.busi.scene.service.SceneService;
import com.bonc.common.base.JsonResult;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/scenemarket")
public class SceneMarketController {
    private final static Logger log = LoggerFactory.getLogger(SceneMarketController.class);

    @Autowired
    private OrderActivityService OrderActivityServiceIns;
    @Autowired
    private SceneService sceneService;

    @RequestMapping(value = "/startActivity" , method = RequestMethod.POST)
    public JsonResult startActivity(@RequestBody String request ) {
        log.info("活动添加接口请求数据——>" + request);
        JsonResult JsonResultIns = new JsonResult();
        if (null == request) {
            JsonResultIns.setCode("2");
            JsonResultIns.setMessage("场景营销活动数据不能为空！——>>>>请求格式为：req=JSON串,注意特殊字符可能会URL转码开始生成");
            return JsonResultIns;
        }
        JsonResultIns = sceneService.startSceneMarketActivity(request);
        return JsonResultIns;
    }

    @RequestMapping(value = "/queryOrderInfo" , method = RequestMethod.POST)
    public Object queryOrderInfo(@RequestBody HashMap<String, Object> req) {
        log.info("场景营销工单查询接口请求参数——>" + JSON.toJSONString(req));
        JsonResult JsonResultIns = new JsonResult();
        if (null == req) {
            JsonResultIns.setCode("1");
            JsonResultIns.setMessage("场景营销请求参数不能为空！");
            return JsonResultIns;

        }
        HashMap<String, Object> resp = new HashMap<String, Object>();
        List<String> fields = new ArrayList<String>();
        fields.add("phoneNum");
        fields.add("contactDateStart");
        fields.add("contactDateEnd");
//		fields.add("envType");
		fields.add("channelId");
        fields.add("tenantId");
        fields.add("activityId");
        // --参数判断，是否必传，是否选传--
        for (String field : fields) {
            if (StringUtil.validateStr(req.get(field))) {
                resp.put("code", IContants.CODE_FAIL);
                resp.put("msg", field + " is empty");
                return resp;
            }
        }
        //--查询，Map-->json.toString(Map)--
        try {
            long start = System.currentTimeMillis();
            List<HashMap<String, Object>> result = OrderActivityServiceIns.queryActivityOrderInfo(req);
            long end = System.currentTimeMillis();
            log.info("场景营销工单查询接口总耗时——>>>>" + (end - start) / 1000.0 + "s");
            return JSON.toJSONString(result);
        } catch (BoncExpection e) {
            resp.put("code", IContants.BUSI_ERROR_CODE);
            resp.put("msg", e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", IContants.SYSTEM_ERROR_CODE);
            resp.put("msg", e.getMessage());
        }
        return resp;
    }


    @RequestMapping(value = "/querySuccessNum" , method = RequestMethod.POST)
    public Object querySuccessNum(@RequestBody HashMap<String, Object> req) {
        log.info("场景成功工单数查询请求参数——>" + JSON.toJSONString(req));
        JsonResult JsonResultIns = new JsonResult();
        if (null == req) {
            JsonResultIns.setCode("1");
            JsonResultIns.setMessage("场景营销请求参数不能为空！");
            return JsonResultIns;

        }
        HashMap<String, Object> resp = new HashMap<String, Object>();
        List<String> fields = new ArrayList<String>();
        fields.add("activityId");
//		fields.add("contactDateStart");
//		fields.add("contactDateEnd");
        fields.add("tenantId");
        // --参数判断，是否必传，是否选传--
        for (String field : fields) {
            if (StringUtil.validateStr(req.get(field))) {
                resp.put("code", IContants.CODE_FAIL);
                resp.put("msg", field + " is empty!");
                return resp;
            }
        }
        //--查询，Map-->json.toString(Map)--
        try {
            long start = System.currentTimeMillis();
            HashMap<String, Object> result = sceneService.querySuccessNum(req);
            long end = System.currentTimeMillis();
            log.info("场景营销成功工单数查询接口总耗时——>>>>" + (end - start) / 1000.0 + "s");
            return JSON.toJSONString(result);
        } catch (BoncExpection e) {
//            resp.put("code", IContants.BUSI_ERROR_CODE);
//            resp.put("msg", e.getMsg());
            return "{\"successUserNum\":0,\"userNum\":0,\"orderNum\":0,\"sendOrderNum\":0,\"succesOrderNum\":0,\"sendUserNum\":0}";
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("code", IContants.SYSTEM_ERROR_CODE);
            resp.put("msg", e.getMessage());
        }
        return resp;
    }


}
