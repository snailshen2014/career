package com.bonc.busi.sys.base;
/*
 * @desc:应用初始化
 * @author:曾定勇
 * @time:2016-12-15
 */

import	java.util.Date;
//import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bonc.busi.sys.service.SysFunction;
import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.mapper.SysMapper;




@Component
public class AppInit implements CommandLineRunner{
	
	
	@Autowired	 
	@Qualifier("SysFunctionImpl")
	private SysFunction	SysFunctionIns;
	@Autowired	
	private	SysMapper	SysMapperIns;
	

	
	@Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        // --- 纪录一下系统启动 ---
        SysLog			SysLogIns = new SysLog();
        // --- 执行单实例检查 --
        if(SysFunctionIns.singleInstaceControl("ORDERCONTROLCENTERSINGLEINSTANCECHECKFLAG", 
        		"ORDERCONTROLCENTERFLAG", "ORDERCONTROLFLAG")  == false){
        	System.out.println("已经有其它实例启动,系统退出");
        	 SysLogIns.setAPP_NAME("ORDERCONTROL-"+AppInit.class);
             SysLogIns.setLOG_TIME(new Date());
             SysLogIns.setLOG_MESSAGE("系统启动失败,已经有实例在运行");
             SysFunctionIns.saveSysLog(SysLogIns);
        	System.exit(0);
        }
        
        Global.init();
        
        // --- 纪录一下系统启动 ---
        SysLogIns.setAPP_NAME("ORDERCONTROL-"+AppInit.class);
        SysLogIns.setLOG_TIME(new Date());
        SysLogIns.setLOG_MESSAGE("系统启动");
        SysFunctionIns.saveSysLog(SysLogIns);
        
        SysLogIns = null;
        

    }
}
