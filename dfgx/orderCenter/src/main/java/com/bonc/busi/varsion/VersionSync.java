package com.bonc.busi.varsion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

/**
 * 
 * we define a timeTask @VersionServiceImpl to monitor all version
 * this version define in table plt_static_code named all_version
 * in memory also exist a version list 
 * 
 * timeTask compare memory version and with MYSQL version
 * if not equals will call a service to deal it 
 * 
 * @author gaoYang
 *
 */
@Component("versionSync")
public abstract class VersionSync {
	
	public static final String ALL_VERSIONS = "ALL_VERSIONS";
	
	@Autowired
	protected BusiTools BusiTools;
	
	@Autowired
	protected CodeService codeService;
	
	
	/**
	 * when service start , this method while called,we supported MULTI-tenant
	 * @param tenantId
	 */
	public void load(String tenantId,String content){
		HashMap<String, HashMap<String, CodeReq>> data=initload(tenantId,content);
		
		// get default data
		if(data==null&&!StringUtil.validateStr(content)){
			data = defalutLoad(tenantId,content,1);
		}
		
		//if data is null ,no need load
		if(data==null||data.isEmpty()){
			return;
		}
		
		//load new data
		for(String fieldName:data.keySet()){
			CodeUtil.reload(tenantId, fieldName, data.get(fieldName));
		}
	}
	
	/**
	 * when version is change ,this method while trigger
	 * @param tenantId	
	 * @param name change content in MYSQL 
	 */
	public abstract void sync(String tenantId,String content);
	
	/**
	 * get register name
	 * @param tenantId
	 * @return
	 */
	public abstract String getName();

	/**
	 * when service start , load your code to memory block
	 * if you want load by our,you have package a right Map Or  
	 * @param tenantId
	 * @param content
	 */
	public abstract HashMap<String, HashMap<String, CodeReq>> initload(String tenantId,String content);
	
	/**
	 * usually we load table by register content
	 * 
	 * @param tenantId
	 * @param content
	 * @param table 1:PLT_STATIC_CODE,2:PLT_XCLOUD_TABLE
	 * @return
	 */
	protected HashMap<String, HashMap<String, CodeReq>> defalutLoad(String tenantId,String content,int table){
		// INIT XLOUND CODE 
		HashMap<String, String> pama = new HashMap<String, String>();
		pama.put("tenantId", tenantId);
		
		HashMap<String, HashMap<String, CodeReq>> data = new HashMap<String, HashMap<String,CodeReq>>();
		if(StringUtil.validateStr(content)){
			return data;
		}
		String[] tables = content.split(IContants.CO_SPLIT);
		for(String tableName:tables){
			HashMap<String, CodeReq> codeReqs = new HashMap<String, CodeReq>();
			pama.put("tableName", tableName);
			List<CodeReq> codes = new ArrayList<CodeReq>();
			switch (table) {
			case 1:
				codes = codeService.getCodes(pama);
				break;
			case 2:
				codes = codeService.getXcloudTable(pama);
				break;
			}
			for(CodeReq code:codes){
				codeReqs.put(code.getFieldKey(), code);
			}
			data.put(tableName, codeReqs);
		}
		
		return data;
	}
	
	/**
	 * you can register this content to MYSQL ,if this monitor not exit in MYSQL,
	 * if you not Override this method ,we while write ''.
	 * when you change you version ,you can get it in method sync 
	 * @param tenantId
	 * @return
	 */
	@Deprecated
	protected String getMonitorContent(String tenantId){
		CodeReq req = new CodeReq(tenantId,ALL_VERSIONS,getName());
		String content = codeService.getCodeValue(req);
		if(null==content){
			registerMonitor(tenantId, getName(), "");
			content="";
		}
		return content;
	}
	
	@Deprecated
	private void registerMonitor(String tenantId,String name,String content){
		StringBuilder sql = new StringBuilder("INSERT INTO PLT_STATIC_CODE(TENANT_ID, TABLE_NAME, FIELD_KEY, FIELD_VALUE, LOAD_DATE) ");
		sql.append(" SELECT '").append(tenantId).append("','ALL_VERSIONS','").append(name).append("','").append(content).append("',NOW() FROM DUAL WHERE ");
		sql.append(" NOT EXISTS (SELECT TABLE_NAME,FIELD_KEY FROM PLT_STATIC_CODE WHERE TABLE_NAME='ALL_VERSIONS' AND FIELD_KEY='").append(name).append("')");
		
		BusiTools.executeDdlOnMysql(sql.toString(), tenantId);
	}
}
