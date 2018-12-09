package com.bonc.busi.code.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bonc.busi.code.mapper.CodeMapper;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.Constants;

@Service("codeService")
public class CodeServiceImpl implements CodeService {

	private static final Logger log = Logger.getLogger(CodeServiceImpl.class);
	
	@Autowired
	private CodeMapper mapper;

	@Override
	public String getValue(CodeReq req) {
		if (null == req.getFieldKey() || "".equals(req.getFieldKey().trim())) {
			return req.getFieldKey();
		}
		//1.从内存中取
		//key=uni076.KD.USER_STATUS.2
		String key= req.getTenantId()+Constants.DO_SPLIT+ (req.getType()==null?"":(req.getType() +Constants.DO_SPLIT)) + req.getFieldName() + Constants.DO_SPLIT
				+ req.getFieldKey();
		String value = CodeUtil.getValue(key);
		if(null != value) {
			return value;
		}
		
		//2.从MySQL中取
		//fieldName=KD.USER_STATUS
		/*req.setFieldName((req.getType()==null?"":(req.getType() +Constants.DO_SPLIT))+ req.getFieldName());*/
		HashMap<String, Object> codeMap = mapper.getLocalCode(req);
		if (null != codeMap && "1".equals(codeMap.get("IS_LOAD")+"")) {
			/*log.info("从MYSQL中加载码表"+req.getFieldName()+" IS_LOAD="+codeMap.get("IS_LOAD"));*/
			if(null != codeMap.get("FIELD_VALUE")){
				value = codeMap.get("FIELD_VALUE")+"";
				CodeUtil.setValue(key, value);
				return value;
			}
			return req.getFieldKey();
		}

		//3.从MySQL中取码表SQL
		String table = CodeUtil.getTable(req.getTenantId()+Constants.DO_SPLIT+ req.getFieldName());
		if (null == table||"".equals(table)) {
			//从MySQL中加载所有码表SQL
			CodeReq codeReq = new CodeReq();
			codeReq.setFieldName(Constants.XCLOUD_TABLE);
			codeReq.setTenantId(req.getTenantId());
			List<HashMap<String, String>> xcloudSql = mapper.getCodeTable(codeReq);
			for(HashMap<String, String> map:xcloudSql){
				CodeUtil.setTable(req.getTenantId()+Constants.DO_SPLIT+map.get("FIELD_KEY"), map.get("FIELD_VALUE"));
			}
			table = CodeUtil.getTable(req.getTenantId()+Constants.DO_SPLIT+req.getFieldName());
			if(null==table||"".equals(table)){
				log.error("MySQL中未配置行云码表"+req.getFieldName()+"=======================》");
				return req.getFieldKey();
			}
		}
		//设置码表SQL
		req.setTable(table);
		
		
		//4.去行云去数据
		try {
            /*long start = System.currentTimeMillis();*/
			/*long end = System.currentTimeMillis();
			log.info("行云查找码表耗时："+(end-start)/1000.0+"s");*/
			List<String> values = getXcloud(req);
			if (null != values && values.size() > 0) {
				value = values.get(0);
			} else {
				value = null;
			} 
		} catch (Exception e) {
			log.error("行云码表:"+req.getTable()+"配置错误=================》");
			value = null;
		}
		req.setFieldValue(value);
	    //更新内存
		CodeUtil.setValue(key, value);
		//更新MySQL码表
		if(codeMap==null){
			mapper.addCode(req);
		}else{
			mapper.updateCode(req);
		}
        if(null != value){
        	return value;
        }
        return req.getFieldKey();
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

	/*public String getInterfacePama(CodeReq reqCode) {
		String key = reqCode.getTenantId()+Constants.DO_SPLIT+reqCode.getFieldName()+Constants.DO_SPLIT+reqCode.getFieldKey();
		String value = CodeUtil.getFieldMap(key);
		if(null!=value){
			return value;
		}else{
			//如果找不到重新刷一遍　接口参数
			List<HashMap<String, String>> intefacePama = getCodeTable(reqCode);
			for(HashMap<String, String> pama:intefacePama){
				String pamaKey = reqCode.getTenantId()+Constants.DO_SPLIT+reqCode.getFieldName()+Constants.DO_SPLIT+pama.get(Constants.FIELD_KEY);
				CodeUtil.setFieldMap(pamaKey, pama.get(Constants.FIELD_VALUE));
			}
			return CodeUtil.getFieldMap(key);
		}
	}*/
}
