package com.bonc.busi.varsion.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.varsion.VersionSync;
import com.bonc.busi.varsion.service.VersionServiceImpl;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.JsonUtil;

/**
 * 
 * some talk words contains some variable ,so need replace it,
 * but we don't know what word have in content(talk words),
 * so we need read a interface to get a word list ,and load it to memory,the interface is provide by activity side.
 * 
 * @param extInfos: store how to access variable corresponding fields 
 * @param talkName: store get a key in memory .to get this list
 * @author gaoYang
 *
 */
@Service("talkSync")
public class TalkSync extends VersionSync {

	private final static Logger log = LoggerFactory.getLogger(VersionServiceImpl.class);

	private static String url = null;
	
	private static final String talkName="DIM_TALK_VARIABLE";
	
	public static final String getTalkName(){
		return talkName;
	}
	
	// store all tenant  structure user query fields, when query userInfo user it
	public static final HashMap<String, String> extInfos=new HashMap<String, String>();
	
	
	public static final String getExtInfo(String tenantId){
		return extInfos.get(tenantId);
	}
	
	private static synchronized void putExtInfo(String tenantId,String extInfo){
		extInfos.put(tenantId, extInfo);
	}

	@Override
	public void sync(String tenantId, String content) {
		super.load(tenantId, content);
	}

	@Override
	public String getName() {
		return "talkSync";
	}

	@Override
	public HashMap<String, HashMap<String, CodeReq>> initload(String tenantId,String content) {
		if (null == url) {
			url = BusiTools.getValueFromGlobal("ACT_HUA_SHU");
			// test URL
//			url = "http://10.245.2.222:8080/activityInter/activity/actHuaShu";
			log.info("reload huashu url=" + url);
		}

		Map<String, Object> aclMap = new HashMap<String, Object>();
		aclMap.put("tenantId", "uni076");
		String aclList = HttpUtil.doGet(url, aclMap);

		log.info("load talk content=" + aclList);

		JSONObject obj = JSONObject.parseObject(aclList);
		if (!"1".equals(obj.getString("resultCode"))) {
			throw new BoncExpection(IContants.BUSI_ERROR_CODE, "load talk val fail!");
		}
		List<Map<String, String>> items=JsonUtil.getListMapStr(obj.getString("result"));
		
		HashMap<String, HashMap<String, CodeReq>> codes=new HashMap<String, HashMap<String,CodeReq>>();
		HashMap<String, CodeReq> code=new HashMap<String, CodeReq>();
		StringBuilder extUserInfo = new StringBuilder();
		for(Map<String, String> item:items){
			if("1".equals(item.get("valid"))&&"01".equals(item.get("varType"))){
				CodeReq codeReq = new CodeReq(tenantId,item.get("realName"),item.get("content"));
				codeReq.setFieldValue(item.get("mysqlName").substring(item.get("mysqlName").lastIndexOf(" ")+1).replace("u.", ""));
				code.put(item.get("content"), codeReq);
				
				//structure userInfo query result fields
				extUserInfo.append(item.get("mysqlName")).append(IContants.CO_SPLIT);
			}
		}
		putExtInfo(tenantId, extUserInfo.toString());
		codes.put(talkName, code);
		return codes;
	}
}
