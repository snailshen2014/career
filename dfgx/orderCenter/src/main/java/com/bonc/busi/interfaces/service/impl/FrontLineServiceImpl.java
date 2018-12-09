package com.bonc.busi.interfaces.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.interfaces.mapper.FrontLineMapper;
import com.bonc.busi.interfaces.mapper.UserExtMapper;
import com.bonc.busi.interfaces.model.frontline.ContactHistoryResp;
import com.bonc.busi.interfaces.model.frontline.OrderQueryReq;
import com.bonc.busi.interfaces.service.FrontLineService;
import com.bonc.busi.interfaces.service.TalkService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.DateUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("frontLineService")
public class FrontLineServiceImpl implements FrontLineService{
	
	private static final Logger log = Logger.getLogger(FrontLineServiceImpl.class);
	
	@Autowired
	private FrontLineMapper frontLineMapper;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private UserExtMapper userMapper;
	
	@Autowired
	private TalkService talkService;
	
	@Autowired
	private BusiTools  AsynDataIns;
	
	@Override
	public HashMap<String, Object> taskAll(HashMap<String, Object> req) {
		return frontLineMapper.sumOrdersStatistic(req);
	}
	
	@Override
	public HashMap<String, Object> activityStatistic(HashMap<String, Object> req) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		List<String> total = frontLineMapper.countActivityStatistic(req);
		resp.put("total", total.size());
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		if(total.size()>0){
			items = frontLineMapper.findActivityStatistic(req);
		}
		resp.put("items", items);
		return resp;
	}
	
	@Override
	public HashMap<String, Object> custManagerStatistic(HashMap<String, Object> req) {
		List<HashMap<String, Object>> all  = frontLineMapper.custManagerStatistic(req);
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("items", all);
		return resp;
	}

	/**
	 * exchange pama
	 * @param req
	 */
	private void exchangePama(OrderQueryReq req){
		HashMap<String, String> pama =req.getPama();
		if(null!=pama){
			HashMap<String, String> reqPama = new HashMap<String, String>();
			String serviceType="0".equals(req.getServiceType())?"MB.":"KD.";
			
			HashMap<String, String> pamaFileds = CodeUtil.getCodeMap(req.getTenantId(),IContants.ORDER_QUERY_PAMA);
			String value = null;
			for(String key:pama.keySet()){
				value = pamaFileds.get(serviceType+key);
				if(!StringUtil.validateStr(value)&&pama.get(key).contains(key)){
					reqPama.put(key, pama.get(key).replace(key,value));
				}else{
					log.error("not support this query pama :{'"+key+"':'"+pama.get(key)+"'}");
				}
			}
			req.setPama(reqPama);
		}
	}
	
	/**
	 * this interface user code contains ORDER_QUERY_FIELD_MAP get all need exchange pama fields
	 * MB_ORDER_QUERY_RETURN_FIELDS,MB_ORDER_QUERY_RETURN_FIELDS get mb or kd need query fields and 
	 * use ORDER_QUERY_RETURN_FIELDS get order need query fields
	 * use USER_EXT_SELECT query which modify user info need override
	 * use ORDER_QUERY_ITEMS_CODE get which query fields need change code.
	 * 
	 * so a total of seven code tables
	 * 
	 * roleType: support mulit-orgPath
	 * isVaild:  support valid or invalid activity query
	 */
	@Override
	public HashMap<String, Object> orderQuery(OrderQueryReq req) {
		// activityId must not null
		if(StringUtil.validateStr(req.getActivityId())){
			if(req.getPama().containsKey("telPhone")&&req.getPama().containsKey("phoneNum")){
				throw new BoncExpection(IContants.CODE_FAIL,"activityId is empty phoneNum must not empty !");
			}
		}
		
		//no isVaild set vaild order
		if(StringUtil.validateStr(req.getIsVaild())){
			req.setIsVaild("1");
		}
		
		// get valid partation find user
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		req.setPartFlag(partFlag);
		
		// through code get which fields need query,because diff user field will same interface field,so need split MB and KD
		String queryField=null;
		// serviceType 0:MB 1:KD
		if("0".equals(req.getServiceType())){
			queryField = IContants.ORDER_QUERY_FIELDS_MB;
		}else if("1".equals(req.getServiceType())){
			queryField = IContants.ORDER_QUERY_FIELDS_KD;
		}else{
			throw new BoncExpection(IContants.CODE_FAIL,"error serviceType value——>>>>"+req.getServiceType());
		}
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		//get all activitySeq
		List<HashMap<String, Object>> activitySeqIds = frontLineMapper.getActivitySeqIds(req);
		if(null==activitySeqIds||activitySeqIds.size()==0){
			resp.put("total", 0);
			resp.put("items", new ArrayList<HashMap<String, Object>>());
			return resp;
		}
		StringBuilder activityIds= new StringBuilder();
		// cache activitySeqInfo
		HashMap<Long, HashMap<String, Object>> activitys = new HashMap<Long, HashMap<String, Object>>();
		for(HashMap<String, Object> recId:activitySeqIds){
			activitys.put((Long)recId.get("ACTIVITY_ID"), recId);
			activityIds.append(recId.get("ACTIVITY_ID")).append(IContants.SPACE).append(IContants.CO_SPLIT);
		}
		req.setActivitySeqs(activityIds.toString().substring(0,activityIds.length()-1));
		
		//exchange pama with fields
		exchangePama(req);
		
		//if list operation not do count
		if(!"list".equals(req.getType())){
			Integer count = 0;
			long begin = System.currentTimeMillis();
			if("0".equals(req.getServiceType())){
				count = frontLineMapper.countOrdersQueryMB(req);
			}else {
				count = frontLineMapper.countOrdersQueryKD(req);
			}
			long end = System.currentTimeMillis();
			resp.put("total", count);
			log.info(req.getActivityId()+" orderQuery count lose :"+(end-begin)/1000.0+"s");
		}
		
		//load order query field
		HashMap<String, String> codeTable = CodeUtil.getCodeMap(req.getTenantId(),IContants.ORDER_QUERY_FIELDS);
		List<String> values =  new ArrayList<String>();
		if(null!=codeTable){
			for(String key:codeTable.keySet()){				
				values.add(codeTable.get(key));
			}
		}
		//load MB or KD query field
		codeTable =CodeUtil.getCodeMap(req.getTenantId(),queryField);
		for(String key:codeTable.keySet()){				
			values.add(codeTable.get(key));
		}
		req.setQueryFields(values);
		
		
		//if count operation not do items query
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		if(!"count".equals(req.getType())){
			long start = System.currentTimeMillis();
			if("0".equals(req.getServiceType())){
				items = frontLineMapper.findOrdersQueryMB(req);
			}else{
				items = frontLineMapper.findOrdersQueryKD(req);
			}
			long end = System.currentTimeMillis();
			log.info(req.getActivityId()+"orderQuery items lose :"+(end-start)/1000.0+"s");
			if(null==items||items.size()==0){
				resp.put("total", 0);
				resp.put("items", new ArrayList<HashMap<String, Object>>());
				return resp;
			}
		}

		// load app modify fields default: TELPHONE CONTACT_TELEPHONE_NO,CUST_NAME,ADDRESS KD_DETAIL_INSTALL_ADDRESS,
		String userExtsql = CodeUtil.getValue(req.getTenantId(),IContants.USER_EXT_SELECT).get(IContants.SELECT_FIELD).getFieldValue();
		for(HashMap<String, Object> item:items){
			//put activity info 
			if(null!=item.get("ACTIVITY_ID")){
				item.putAll(activitys.get(""+item.get("ACTIVITY_ID")));
			}
			//get modify user info 
			List<HashMap<String, Object>> userMap = userMapper.getUserExtInfo(req.getTenantId(),userExtsql,item.get("USER_ID"));
			if(null!=userMap&&userMap.size()>0){
				item.putAll(userMap.get(0));
			}
		}
		
		//exchange code table
		HashMap<String, String> itemsCodeTable = CodeUtil.getCodeMap(req.getTenantId(),IContants.ORDER_QUERY_ITEMS_CODE);
		for(String field:itemsCodeTable.keySet()){
			codeTable = CodeUtil.getCodeMap(req.getTenantId(),itemsCodeTable.get(field));
			for(HashMap<String, Object> item:items){
				try {
					item.put(field, codeTable.get(item.get(field)));
				} catch (Exception e) {
					log.warn("not exist this code! "+field);
				}
			}
		}
		
		resp.put("items", items);
		return resp;
	}
	
	/**
	 * query a user's all valid order
	 * 
	 * RESERVE5: strategy subdivision,according to user label judge send which sms content
	 */
	@Override
	public HashMap<String, Object> userQuery(HashMap<String, String> req) {
		// get user info
		List<HashMap<String, Object>> activityList = frontLineMapper.findUserOrder(req);
		//query result
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		//talk change req pama
		HashMap<String, String> talk = new HashMap<String, String>();
		talk.put("tenantId", req.get("tenantId"));
		for(HashMap<String, Object> item : activityList){
			req.put("activitySeqId", item.get("ACTIVITY_ID")+"");
			//query activity info not exist this activity jump it, else put in return info
			HashMap<String, Object> activityInfo=frontLineMapper.getActivityInfo(req);
			if(null==activityInfo){
				continue;
			}
			item.putAll(activityInfo);
			
			//get activity success info,if exist ,put it in return info
			HashMap<String, Object> successInfo = frontLineMapper.getSuccessInfo(req);
			if(null!=successInfo){
				item.putAll(successInfo);
			}
			
			//get activity product list, an activity have 0 or more activitys
			List<HashMap<String, Object>> products = frontLineMapper.findProductList(req);
			item.put("PRODUCT_LIST", products);
			
			//we first judge if exist resreve5 ,if exists ,use reserve5 as sms_words,or use default 
			if(null!=item.get("RESERVE5")&&!"".equals(item.get("RESERVE5"))){
				item.put("SMS_WORDS", item.get("RESERVE5"));
			}
			
			//exchange market_words
			talk.put("userId", item.get("USER_ID")+"");
			talk.put("talkWork", item.get("MARKET_WORDS")+"");
			item.put("MARKET_WORDS", talkService.exchangeTalkVal(talk));
			items.add(item);
		}
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("items", items);
		return resp;
	}


	/**
	 * @deprecated this method is not used
	 */
	@Override
	public ContactHistoryResp contactHistory(HashMap<String, Object> req) {
		ContactHistoryResp resp = new ContactHistoryResp();
		//根据查询时间计算出月表的数量
		try {
			//查询所有的月份
			List<String> monthList = DateUtil.getMonthList(req.get("contactDate")+"",(String)req.get("contactDateEnd"), DateUtil.DateFomart.EN_DATETIME);
			List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
			
			for (int m=0,size=monthList.size()-1;m<size;m++){
				req.put("contactDate",monthList.get(m+1));
				req.put("contactDateEnd", monthList.get(m));
				long start = System.currentTimeMillis();
				items.addAll(frontLineMapper.contactHistory(req));
				long end = System.currentTimeMillis();
				log.info("接触历史查询sql耗时——>"+(end-start)/1000.0+"s");
			}
			
			HashMap<String, HashMap<String, Object>> activityMap = new HashMap<String, HashMap<String,Object>>();
			StringBuilder recIds = new StringBuilder("(");
			for(HashMap<String, Object> item:items){
				recIds.append(item.get("ACTIVITY_SEQ_ID")).append(",");
//				activityMap.put(""+item.get("ACTIVITY_SEQ_ID"),item);
			}
			if(items.size()>0){
				recIds.append(items.get(0).get("ACTIVITY_SEQ_ID")).append(")");
				List<HashMap<String, Object>> activityNames = frontLineMapper.getContactActivity(req.get("tenantId")+"",recIds.toString());
				for(HashMap<String, Object> activity:activityNames){
					activityMap.put(""+activity.get("REC_ID"), activity);
				}
				for(HashMap<String, Object> item:items){
					item.put("ACTIVITY_NAME", activityMap.get(item.get("ACTIVITY_SEQ_ID")).get("ACTIVITY_NAME"));
				}
			}
			resp.setItems(items);
			return resp;
		} catch (ParseException e) {
			log.error("接触时间格式错误！");
			e.printStackTrace();
			throw new BoncExpection(IContants.CODE_FAIL,"接触时间格式错误！");
		} catch(Exception e){
			log.error("接触历史查询异常！");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * custName
	 * userId
	 * sex
	 * contactNum
	 * address
	 * remark
	 * ext1
	 * ext2
	 * ext3
	 */
	@Override
	@Transactional
	public void modifyUserInfo(HashMap<String, Object> map) {
		// query if exists this user modify info
		List<HashMap<String, Object>> userExtList = userMapper.getUserExtInfo(map.get("tenantId")+"",null,map.get("userId"));
		boolean flag = null==userExtList||userExtList.size()==0;
		
		// get userInfo from userinfo or userextinfo
		HashMap<String, Object> userInfo = new HashMap<String, Object>();
		if(flag){
			String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
			map.put("partFlag", partFlag);
			List<HashMap<String, Object>> userList =  userMapper.getUserInfo(map);
			if(null==userList||userList.size()==0){
				throw new BoncExpection(IContants.CODE_FAIL,"not exists this user userId is : "+map.get("userId"));
			}else{
				userInfo = userList.get(0);
			}
		}else{
			userInfo = userExtList.get(0);
		}
		
		// put interface pama in modify userinfo
		HashMap<String, CodeReq> fields=CodeUtil.getValue(map.get("tenantId")+"", "USER_MODIFY_FIELDS");
		for(String field: fields.keySet()){
			//null + "" lead　"null" String bug
			Object value = map.get(fields.get(field).getFieldValue());
			if(null==value||"".equals(value)){
				continue;
			}else{
				userInfo.put(fields.get(field).getFieldValue(), value+"");
			}
		}
		
		//update or insert userinfo
		if(flag){
			userMapper.addUserExtInfo(userInfo);
		}else{
			userMapper.updateUserExtInfo(userInfo);
		}
	}
}
