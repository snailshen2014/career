package com.bonc.busi.varsion.monitor;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.varsion.VersionSync;

/**
 * 
 * it is a common monitor , to load all application monitor 
 * it can define many monitor by commonSync_1-commonSync_any
 * this monitor one service is commonSync
 * 
 * when any commonSync_any'version changed ,it will be reload
 * 
 * @author gaoYang
 *
 */
@Service("commonSync")
public class CommonSync extends VersionSync{

	public void sync(String tenantId, String content) {
		super.load(tenantId,content);
	}

	/**
	 * default load
	 */
	public HashMap<String, HashMap<String, CodeReq>> initload(String tenantId,String content) {
		return null;
	}

	@Override
	public String getName() {
		return "commonSync";
	}

}
