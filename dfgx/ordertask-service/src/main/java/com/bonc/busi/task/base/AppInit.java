package com.bonc.busi.task.base;
/*
 * @desc:应用初始化
 * @author:曾定勇
 * @time:2016-12-15
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppInit implements CommandLineRunner{
	
	@Autowired
	private SysVars sysVars;
	
	@Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        Global.init();
        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据完成<<<<<<<<<<<<<");
    }
}
