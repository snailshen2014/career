package com.bonc.busi.code.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.mapper.CodeMapper;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.IContants;

@Service("codeService")
public class CodeServiceImpl implements CodeService {

	private static final Logger log = Logger.getLogger(CodeServiceImpl.class);
	
	@Autowired
	private CodeMapper mapper;

	@Override
	/**
	 * tenantId 租户
	 * fieldName 类型
	 * fieldKey key
	 */
	public String getValue(CodeReq req) {
		if (null == req.getFieldKey() || "".equals(req.getFieldKey().trim())) {
			return req.getFieldKey();
		}
		//key=uni076.KD.USER_STATUS.2
		String key= req.getTenantId()+IContants.DO_SPLIT+ (req.getType()==null?"":(req.getType() +IContants.DO_SPLIT)) + req.getFieldName() + IContants.DO_SPLIT
				+ req.getFieldKey();
		//fieldName=KD.USER_STATUS
		req.setFieldName((req.getType()==null?"":(req.getType() +IContants.DO_SPLIT))+ req.getFieldName());
		String value = CodeUtil.getValue(key);

		if(null != value) {
			return value;
		}

		// 先从mysql取数据
		HashMap<String, Object> codeMap = mapper.getLocalCode(req);
		
		if (null != codeMap&&"1".equals(codeMap.get("IS_LOAD")+"")) {
			log.info("从MYSQL中加载码表"+req.getFieldName()+" IS_LOAD="+codeMap.get("IS_LOAD"));
			if(null!=codeMap.get("FIELD_VALUE")){
				value = codeMap.get("FIELD_VALUE")+"";
				CodeUtil.setValue(key, value);
				return value;
			}
			return req.getFieldKey();
		}

		//先从内存中去码表SQL
		String fieldTable = req.getTenantId()+IContants.DO_SPLIT+ req.getFieldName();
		String table = CodeUtil.getTable(fieldTable);
		if (null == table||"".equals(table)) {
			//一次加载所有行云码表ＳＱＬ　从MYSQL 配置中取码表配置SQL
			req.setFieldName(IContants.XCLOUD_TABLE);
			List<HashMap<String, String>> xcloudSql = mapper.getCodeTable(req);
			for(HashMap<String, String> map:xcloudSql){
				CodeUtil.setTable(req.getTenantId()+IContants.DO_SPLIT+map.get("FIELD_KEY"), map.get("FIELD_VALUE"));
			}
			table = CodeUtil.getTable(fieldTable);
			if(null==table||"".equals(table)){
				log.error("未配置行云码表——＞＞＞＞"+req.getFieldName());
				
				return req.getFieldKey();
			}else{
				req.setTable(table);
			}
		}
		req.setTable(table);

		// 从本地未找到码表，去行云去取
		try {
			List<String> values = getXcloud(req);
			if(null!=values&&values.size()>0){
				value = values.get(0);
			}else {
				if(codeMap==null){
					mapper.addCode(req);
				}else{
					mapper.updateCode(req);
				}
			}
		} catch (Exception e) {
			// 先入库　再放入内存
			if(codeMap==null){
				mapper.addCode(req);
			}else{
				mapper.updateCode(req);
			}
			log.error("行云码表配置错误——>>>>"+JSON.toJSONString(table));
			return req.getFieldKey();
		}
		 
		if(null!=value){
			req.setFieldValue(value);
			CodeUtil.setValue(key, value);
		}else{
			return req.getFieldKey();
		}
		
		// 先入库　再放入内存
		if(codeMap==null){
			mapper.addCode(req);
		}else{
			mapper.updateCode(req);
		}
		return value;
	}

	public List<String> getXcloud(CodeReq req) {
		try {
			return mapper.getXcloudCode(req);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<HashMap<String, String>> getCodeTable(CodeReq req) {
		return mapper.getCodeTable(req);
	}
	
	@Override
	public String getCodeValue(CodeReq req) {
		return mapper.getCodeValue(req);
	}

	public String getInterfacePama(CodeReq reqCode) {
		String key = reqCode.getTenantId()+IContants.DO_SPLIT+reqCode.getFieldName()+IContants.DO_SPLIT+reqCode.getFieldKey();
		String value = CodeUtil.getFieldMap(key);
		if(null!=value){
			return value;
		}else{
			//如果找不到重新刷一遍　接口参数
			List<HashMap<String, String>> intefacePama = getCodeTable(reqCode);
			for(HashMap<String, String> pama:intefacePama){
				String pamaKey = reqCode.getTenantId()+IContants.DO_SPLIT+reqCode.getFieldName()+IContants.DO_SPLIT+pama.get(IContants.FIELD_KEY);
				CodeUtil.setFieldMap(pamaKey, pama.get(IContants.FIELD_VALUE));
			}
			return CodeUtil.getFieldMap(key);
		}
	}
	
	@Override
	public void deleteSame() {
		mapper.deleteSame();
	}

	@Override
	public HashMap<String, String> getValue(String tenantId,String tableName) {
		if(null==tenantId||"".equals(tenantId)){
			log.error(" tenantId must not empty!");
			return null;
		}
		if(null==tableName||"".equals(tableName)){
			log.error(" tableName must not empty!");
			return null;
		}
		HashMap<String, String> codeTable = CodeUtil.getCodeTable(tenantId,tableName);
		//从码表缓存一个类型 如果是空 从mysql中加载
		if(codeTable==null){
			codeTable = new HashMap<String, String>();
			CodeReq req = new CodeReq(tenantId,tableName);
			List<HashMap<String, String>> _codeTable = mapper.getCodeTable(req);
			if(_codeTable==null||_codeTable.isEmpty()){
				log.info("load codeTable is empty!");
				return null;
			}
			//将查询出的_codeTable数据移入 codeTable
			for(HashMap<String, String> map:_codeTable){
				codeTable.put(map.get(IContants.FIELD_KEY), map.get(IContants.FIELD_VALUE));
			}
			//将结果如内存
			CodeUtil.setCodeTable(tenantId, tableName, codeTable);
		}
		return codeTable;
	}

	@Override
	public String getValue(String tenantId, String tableName, String key) {
		if(null==tenantId||"".equals(tenantId)){
			log.error(" tenantId must not empty!");
			return null;
		}
		if(null==tableName||"".equals(tableName)){
			log.error(" tableName must not empty!");
			return null;
		}
		if(null==key||"".equals(key)){
			log.error(" key must not empty!");
			return null;
		}
		HashMap<String, String> codeTable = getValue(tenantId,tableName);
		//从码表缓存一个类型 如果是空 从mysql中加载
		if(codeTable==null){
			log.warn("not have this codeTable!——>>>>" +tenantId+IContants.DO_SPLIT+tableName);
			return null;
		}
		String _key = tenantId+IContants.DO_SPLIT+tableName+IContants.DO_SPLIT+key;
		String _value = codeTable.get(key);
		if(null!=_value){
			return codeTable.get(key);
		}else{
			log.warn("codeTable don't have this key!——>>>>" + _key);
			return null;
		}
	}
}
