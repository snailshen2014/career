package com.bonc.busi.varsion.monitor;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.varsion.VersionSync;

/**
 * a XCLOUD monitor
 * 
 * when service is start load all XCLOUD code in memory
 * when XCLOUD code changed,sync will be called ,
 * through read configure FIELD_VALUE in PLT_STATIC_CODE TABLE_NAME='ALL_VERSIONS' AND FIELD_KEY='xcloudSync'
 * get a String by TABLE1,TABLE2,....
 * and read one by one table ,reload in memory
 * @author gaoYang
 *
 */

@Service("xcloudSync")
public class XcloudSync extends VersionSync{
	
	@Override
	public void sync(String tenantId,String content) {
		super.load(tenantId,content);
	}

	/**
	 * default load PLT_XCLOUD_TABLE
	 */
	@Override
	public HashMap<String, HashMap<String, CodeReq>> initload(String tenantId,String content) {
		// INIT XLOUND CODE 
		return super.defalutLoad(tenantId, content, 2);
	}

	@Override
	public String getName() {
		return "xcloudSync";
	}
	
}
