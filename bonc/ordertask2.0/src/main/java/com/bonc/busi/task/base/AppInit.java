package com.bonc.busi.task.base;
/*
 * @desc:应用初始化
 * @author:曾定勇
 * @time:2016-12-15
 */

import com.bonc.busi.task.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class AppInit implements CommandLineRunner{
	@Autowired BaseMapper baseMapper;
	@Autowired BusiTools busiTools;
	private static final  String ASYNUSER_FLAG = "ASYNUSER.RUN.FLAG.";
	private static final  String ORDERRUN_FLAG = "ORDER_RUNNING_";
	private static final  String ORDER_DONE = "2002";

	@Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作<<<<<<<<<<<<<");
        Global.init();
        checkCfgFlag();
    }

	private void checkCfgFlag() {
		List<String> tenantIds = baseMapper.getValidTenantId();
		for (String tenantId : tenantIds){
			// 修改所有租户同步用户标识为FALSE
			baseMapper.updateSysCommonCfg(ASYNUSER_FLAG+tenantId,"FALSE");
			String orderRunFlag = busiTools.getValueFromGlobal(ORDERRUN_FLAG + tenantId);
			if (null!=orderRunFlag && "1".equals(orderRunFlag)){
				Map<String, Object> activityExeMap = busiTools.getLastExeActivity(tenantId);
				if (ORDER_DONE!=activityExeMap.get("BUSI_CODE")){
					int deleteNum = busiTools.deleteActivityInfo((Integer) activityExeMap.get("ACTIVITY_SEQ_ID"), tenantId);
					if (deleteNum > 0 ){
						System.out.println(">>>>>>>>>>>>>>>租户："+tenantId+"  上次服务中断异常已重跑标志位已重置 "
								+(String)activityExeMap.get("ACTIVITY_ID")+"<<<<<<<<<<<<<");
					}
					baseMapper.updateSysCommonCfg(ORDERRUN_FLAG+tenantId,"0");
				}
			}
		}
	}

}
