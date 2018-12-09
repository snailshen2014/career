package com.bonc.controller;

import java.util.HashMap;
import java.util.Map;

import com.bonc.busi.backpage.bo.CreateTenantBo;
import com.bonc.common.utils.BoncExpection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bonc.busi.backpage.StartTenantService;
import com.bonc.busi.backpage.mapper.BackPageMapper;

/**
 * @desc:一键开租户处理
 * @author: lizhen
 * @data: 2017年12月28日
 */
@RestController
@RequestMapping("/Starttenant")
public class StartTenantController {
    @Autowired
    BackPageMapper mapper;

    @Autowired
    StartTenantService starttenantservice;
    private final static Logger log = LoggerFactory.getLogger(StartTenantController.class);

    @RequestMapping(value = "/inittenant")
    public Map<String, Object> inittenant(@RequestBody HashMap<String, Object> tenant) {
        Map<String, Object> starttenant = new HashMap<String, Object>();
        Map<String, Object> startmessage = new HashMap<String, Object>();
        try{
            // 装配json为CreateTenantBo对象
            CreateTenantBo tenantBo = starttenantservice.assembleJsonToBo(tenant);
            //先将报文插入日志表中
            if (starttenantservice.insertmessage(tenant)) {
                //第一步：初始化主机配置
                if (starttenantservice.inithostControl(tenant)) {
                    //第二步：初始化路由流程
                    if (starttenantservice.initadsparaControl(tenant)) {
                    	//第三步：表配置流程初始化
                        if (starttenantservice.inittableinfoControl(tenant)) {
                             //第四步：初始化表结构
                             if (starttenantservice.initTableStructure(tenantBo,tenant)) {
                                //第五步：初始化基础数据
                                if (starttenantservice.initTableData(tenantBo)) {
                                    //第六步：插入租户表记录
                                    starttenantservice.initTenantRecord(tenantBo);
                                    starttenant.put("success", "true");
                                    starttenant.put("message", "一键开租户成功");
                                }else {
                                    starttenant.put("success", "false");
                                    startmessage.put("errorcode", "5");
                                    startmessage.put("errormsg", "第五步：初始化基础数据 失败");
                                    starttenant.put("message", startmessage);
                                }
                            } else {
                                starttenant.put("success", "false");
                                startmessage.put("errorcode", "4");
                                startmessage.put("errormsg", "第四步：初始化表结构 失败");
                                starttenant.put("message", startmessage);
                            }
                        } else {
                            starttenant.put("success", "false");
                            startmessage.put("errorcode", "3");
                            startmessage.put("errormsg", "第三步：表配置流程初始化 失败");
                            starttenant.put("message", startmessage);
                        }
                    } else {
                        starttenant.put("success", "false");
                        startmessage.put("errorcode", "2");
                        startmessage.put("errormsg", "第二步：初始化路由流程 失败");
                        starttenant.put("message", startmessage);
                    }
                } else {
                    starttenant.put("success", "false");
                    startmessage.put("errorcode", "1");
                    startmessage.put("errormsg", "第一步：初始化主机配置 失败");
                    starttenant.put("message", startmessage);
                }
            }
        }catch (BoncExpection boncExpection){
            boncExpection.printStackTrace();
            starttenant.put("success", "false");
            startmessage.put("errorcode", boncExpection.getCode());
            startmessage.put("errormsg", boncExpection.getMsg());
            starttenant.put("message", startmessage);
        }catch (Exception e){
            e.printStackTrace();
            starttenant.put("success", "false");
            startmessage.put("errorcode", "-1");
            startmessage.put("errormsg", e.getMessage());
            starttenant.put("message",startmessage);
        }
        return starttenant;
    }
}
