package com.bonc.busi.varsion.monitor;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.varsion.VersionService;
import com.bonc.busi.varsion.VersionSync;

/**
 * 
 * this service exist to monitor other monitors is new 
 * 
 * sometimes we want add a new code table,like commonSync_10 ,but it not bind with a service of commonSync
 * we define it to monitor all monitors , if you add a new configure in plt_static_code
 * you need update sysSync version,this monitor will be called ,and load the new monitor
 * and bind it version to commonSync_10
 * 
 * @author gaoYang
 *
 */
@Service("sysSync")
public class SysSync extends VersionSync{
	
	@Autowired
	private VersionService version;

	public void sync(String tenantId, String content) {
		super.load(tenantId,content);
	}

	/**
	 * default load
	 */
	public HashMap<String, HashMap<String, CodeReq>> initload(String tenantId,String content) {
		HashMap<String, String> pama = new HashMap<String, String>();
		pama.put("tenantId", tenantId);
		pama.put("tableName", content);
		
		List<CodeReq> monitors = codeService.getCodes(pama);
		for(CodeReq monitor:monitors){
			version.register(tenantId, monitor.getFieldKey(), null);
		}
		
		return null;
	}

	@Override
	public String getName() {
		return "sysSync";
	}

}
