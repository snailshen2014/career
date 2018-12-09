package com.bonc.busi.varsion.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.varsion.VersionService;
import com.bonc.busi.varsion.VersionSync;
import com.bonc.utils.StringUtil;

@Service("version")
public class VersionServiceImpl implements VersionService{
	private final static Logger log = LoggerFactory.getLogger(VersionServiceImpl.class);
	
	/**
	 * out key tenantId,inner key is busiName in Version
	 */
	private static HashMap<String, HashMap<String, String>> versions = new HashMap<String, HashMap<String,String>>();
	
	/**
	 * get service by this map ,key is Version name
	 */
	private static HashMap<String, VersionSync> sync = new HashMap<String, VersionSync>();
	
	private static final String ALL_VERSIONS = "ALL_VERSIONS";
	
	@Autowired
	private BusiTools BusiTools;
	
	@Autowired
	private CodeService codeService;
	
	@Override
	public void init() {
		
		//get valid tenant
		List<Map<String, Object>> tenants = BusiTools.getValidTenantInfo();
		for(Map<String, Object> tenant:tenants){
			String tenantId = tenant.get("TENANT_ID")+"";
			HashMap<String, String> pama = new HashMap<String, String>();
			pama.put("tenantId", tenantId);
			pama.put("tableName", ALL_VERSIONS);
			
			// get monitor version in MYSQL ,by configure ALL_VERSIONS
			List<CodeReq> codeVersion = codeService.getCodes(pama);
			for(CodeReq code:codeVersion){
				try {
					//1, INIT MYSQL version CFG ,we config service and monitor NO ,split by _ 
					register(tenantId, code.getFieldKey(),code.getLoadDate());
					VersionSync vSync=sync.get(code.getFieldKey());
					if(null==vSync){
						log.warn("register service fail! "+code.getFieldKey());
						continue;
					}
					
					// load data
					vSync.load(tenantId,code.getFieldValue());
					log.info("load Service success! "+sync.get(code.getFieldKey()).getName());
				} catch (Exception e) {
					log.error("init version monitor error! "+code.getFieldKey()+"_"+tenantId);
				}
			}
		}
		
		log.info("versions init finish! "+JSON.toJSONString(versions));
	}
	
	@Override
	public void scan() {
		//scan valid tenantId
		List<Map<String, Object>> tenants = BusiTools.getValidTenantInfo();
		for(Map<String, Object> tenant:tenants){
			
			HashMap<String, String> tenantVersion = new HashMap<String, String>();
			
			String tenantId = tenant.get("TENANT_ID")+"";
			HashMap<String, String> pama = new HashMap<String, String>();
			pama.put("tenantId", tenantId);
			pama.put("tableName", ALL_VERSIONS);
			List<CodeReq> versionList = codeService.getCodes(pama);
			
			for(CodeReq code:versionList){
				// get key
				String key = code.getFieldKey();
				
				// get lasted version
				String version = code.getLoadDate();
				
				if(null==versions.get(tenantId)){
					log.warn("this tenantId has not mointor! "+tenantId);
					continue;
				}
				
				// if some data no have version ,it was a dirty data. step it
				if(null==version){
					continue;
				}
				
				//compare lasted version with memory version judge sync
				// compareTo method request parameter must not null, so use equals
				if(!version.equals(versions.get(tenantId).get(key))){
					// when exist dirty data, like no need CFG ,service of course not exist. so we step it
					if(null==sync.get(key)){
						continue;
					}
					//is service exist ,to notice customer change content
					sync.get(key).sync(tenantId,code.getFieldValue());
					
					//load new version
					tenantVersion.put(key, version);
				}
			}
			
			//put new version in memory
			addVersion(tenantId, tenantVersion);
		}
	}

	@Override
	public void changeVersion(String tenantId,String busiCode,String content) {
		CodeReq req = new CodeReq(tenantId,ALL_VERSIONS,busiCode);
		req.setFieldValue(StringUtil.validateStr(req.getFieldValue())?"":" FIELD_VALUE='"+req.getFieldValue()+"', ");
		codeService.updateCode(req);
	}

	@Override
	public HashMap<String, String> getVersion(String tenantId, String busiName) {
		HashMap<String, String> req = new HashMap<String, String>();
		req.put("tenantId", tenantId);
		req.put("tableName", ALL_VERSIONS);
		req.put("fieldKey", busiName);
		List<CodeReq> codes = codeService.getCodes(req);
		HashMap<String, String> resp=new HashMap<String, String>();
		for(CodeReq code:codes){
			resp.put("content", code.getFieldValue());
			resp.put("sqlVersion", code.getLoadDate());
			resp.put("memversion", versions.get(tenantId).get(busiName));
		}
		return resp;
	}
	
	/**
	 * bind a service with a new version to monitor
	 */
	@Override
	public void register(String tenantId,String busiName,String version){
		if(null==sync.get(busiName)){
			//  we configure service and monitor NO ,split by _
			String[] name = busiName.split("_");
			VersionSync syncV=(VersionSync)SpringUtil.getBean(name[0]);
			addService(busiName,syncV);
			
			HashMap<String, String> inrcVersion=new HashMap<String, String>();
			inrcVersion.put(busiName, version);
			addVersion(tenantId, inrcVersion);
		}
	}
	
	/**
	 * only this method can bind service
	 * @param busiName
	 * @param _sync
	 */
	private synchronized void addService(String busiName, VersionSync _sync) {
		if(null==sync.get(busiName)){
			sync.put(busiName, _sync);
		}
	}
	

	/**
	 * only this method can change memory version
	 * @param tenantId
	 * @param tenantVersion
	 */
	private synchronized void addVersion(String tenantId,HashMap<String, String> inrcVersion){
		if(null==versions.get(tenantId)){
			//load new tenantId version
			versions.put(tenantId,inrcVersion);
		}else{
			//load tenant new version
			versions.get(tenantId).putAll(inrcVersion);
			inrcVersion=null;
		}
	}

}
