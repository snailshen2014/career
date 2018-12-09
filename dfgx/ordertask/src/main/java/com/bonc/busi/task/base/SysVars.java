package com.bonc.busi.task.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.IContants;

@Component("sysVars")
public class SysVars {
	private static HashMap<String, String> vars=new HashMap<String, String>();
	
	public static String getSysVar(String key) {
		return vars.get(key);
	}
	
	public static String getTenantId() {
		return getSysVar("TENANT_ID");
	}

	
	public void initSysVars(){
		Map<String,String> envMap = System.getenv();
		String tenantId = envMap.get("TENANT_ID");
		if(StringUtils.isNotNull(tenantId)){
			SysVars.vars.put("TENANT_ID", tenantId);
		}else{
			throw new BoncExpection(IContants.BUSI_ERROR_CODE,"tenantId load error!");
		}
		
		String STOP_TASK = envMap.get("STOP_TASK");
		if("1".equals(STOP_TASK)){
			SysVars.vars.put("TASK_FLAG", "1");
		}else{
			SysVars.vars.put("TASK_FLAG", "0");
		}
	}
	
}



