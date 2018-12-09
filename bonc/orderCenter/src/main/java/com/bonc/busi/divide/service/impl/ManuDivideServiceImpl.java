package com.bonc.busi.divide.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.code.model.CodeReq;
import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.divide.mapper.ManuDivideMapper;
import com.bonc.busi.divide.model.DispatchReq;
import com.bonc.busi.divide.model.DivideArupBean;
import com.bonc.busi.divide.model.DividedResp;
import com.bonc.busi.divide.model.RulePreReq;
import com.bonc.busi.divide.service.ManuDivideService;
import com.bonc.busi.statistic.service.StatisticService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.IContants;
import com.bonc.utils.StringUtil;

@Service("menuDivideService")
public class ManuDivideServiceImpl implements ManuDivideService{

	private static final Logger logger = Logger.getLogger(ManuDivideServiceImpl.class);
	@Autowired
	private BusiTools  AsynDataIns;
	@Autowired
	private ManuDivideMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	@Autowired
	private StatisticService statisticService;
	
	@Autowired
	private CodeService codeService;
	
	@Override
	public HashMap<String, Object> divideActivityList(HashMap<String, Object> request) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		//1、获取活动账期总数据
		List<Integer> counts = mapper.divideActivityCount(request);
		//2、获取当页码活动账期列表
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		if(counts.size()>0){
			items = mapper.divideActivityList(request);
		}
		//3、封装返回参数
		resp.put("total", counts.size());
		resp.put("items", items);
		logger.info("活动划分请求列表="+JSON.toJSONString(resp));
		return resp;
	}

	@Override
	public DividedResp dividedCellCount(HashMap<String, Object> pama) {
		//为避免跨省份调配 必须传areaNo 进行限制
		if(StringUtil.validateStr(pama.get("areaNo"))){
			throw new BoncExpection(IContants.CODE_FAIL,"areaNo is empty!");
		}
		DividedResp dividedResp = new DividedResp();
		String parentPath=pama.get("nextOrgPath")+"";
		
		//支持单地市和多区县的查询
		if(!StringUtil.validateStr(pama.get("cityIds"))){
			String path=mapper.getAreaPath(pama);
			if(path.startsWith(parentPath+"/")){
				parentPath=path;
			}
			pama.put("CITY_IDS", " AND SUBSTRING_INDEX(SUBSTRING_INDEX(ORG_PATH,'/',4),'/',-1) IN ('"+(pama.get("cityIds")+"").replace(",", "','").replace(" ", "")+"')");
		}
		
		//如果是省份人员 设置默认的path
		if(StringUtil.validateStr(parentPath)){
			String path=mapper.getRootPath(pama);
			if(path.startsWith(parentPath+"/")){
				parentPath=path;
			}
		}
		
		pama.put("parentPath", parentPath);
		
		//1、查询 下级组织机构 总数
		List<String> orgLists = mapper.getOrgList(pama);
		
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		CodeReq codeReq = new CodeReq();
		codeReq.setTenantId(pama.get("tenantId")+"");
		int count = 0;
		for(String org:orgLists){
			pama.put("subOrgPath", org);
			HashMap<String, Object> item = mapper.dividedOrderList(pama);
			if(null!=item&&null!=item.get("ACCEPT_COUNT")){
				items.add(item);
				count++;
			}
		}
		dividedResp.setTotal(count);
		dividedResp.setItems(items);
		return dividedResp;
	}


	@Override
	public HashMap<String, Object> userDivideList(HashMap<String, Object> request) {
		//重新修改组织机构路径
		if(!StringUtil.validateStr(request.get("orgRange"))){
			request.put("orgRange"," AND s.ORG_PATH IN "+("('"+request.get("orgRange")+"')").replace(",", "','").replace(" ", ""));
		}else{
			request.put("orgRange"," AND s.ORG_PATH=#{orgPath} ");
		}
		if(!StringUtil.validateStr(request.get("loginIds"))){
			request.put("loginIds"," AND s.LOGIN_ID IN "+("('"+request.get("loginIds")+"')").replace(",", "','").replace(" ", ""));
		}
		
		//设置分页参数
		Integer pageSize = Integer.parseInt(request.get("pageSize")+"");
		Integer pageNum = Integer.parseInt(request.get("pageNum")+"");
		request.put("start", (pageNum-1)*pageSize);
		request.put("size", pageSize);
		
		Integer count = mapper.countUserDivide(request);
		List<HashMap<String, Object>> items = mapper.listUserDivide(request);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", count);
		resp.put("items", items);
		return resp;
	}

	
	/**
	 * 查询 当前活动、 归属自己组织机构路径下 可调配的工单列表，未执行的
	 */
	@Override
	public Object dispatchOrderList(HashMap<String, Object> request) {
		
		String partFlag = AsynDataIns.getValueFromGlobal("ASYNUSER.MYSQL.EFFECTIVE_PARTITION");
		request.put("partFlag",partFlag);
		
		//判断是否包含用户信息，如果不包含查询条件里面不关联用户表
		if(!StringUtil.validateStr(request.get("userStatus"))||
				!StringUtil.validateStr(request.get("rentFeeStart"))||
				!StringUtil.validateStr(request.get("rentFeeEnd"))||
				!StringUtil.validateStr(request.get("onlineLongStart"))||
				!StringUtil.validateStr(request.get("onlineLongEnd"))||
				!StringUtil.validateStr(request.get("areaNo"))||
				!StringUtil.validateStr(request.get("cityId"))||
				!StringUtil.validateStr(request.get("towns"))||
				!StringUtil.validateStr(request.get("netChannel"))){
			request.put("userFlag", "1");
		}
		
		//将接受者再转化成Map
		@SuppressWarnings("unchecked")
		Map<String,String> receives = JSON.parseObject(request.get("orderOrgs")+"",Map.class);
		request.put("receives", receives);
		
		//如果查询全部工单直接返回结果工单
		if("1".equals(request.get("checkAll"))){
			List<String> recIdList = mapper.dispatchOrderIds(request);
			if(null==recIdList){
				return new ArrayList<String>();
			}else{
				return recIdList;
			}
		}
		
		Integer total = mapper.dispatchOrderCount(request);
		String tenantId=request.get("tenantId")+"";
		//2、查询可调配工单分页列表，未执行的
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		if(null!=total&&total>0){
			items = mapper.dispatchOrderList(request);
			HashMap<String, Object> activityInfo = mapper.getActivityInfoMap(request.get("tenantId")+"", ""+request.get("activityId"));
			HashMap<String, String> codes=CodeUtil.getCodeMap(tenantId, "PLT_USER_LABEL");
			for(HashMap<String, Object> item:items){
				item.putAll(activityInfo);
				item.put("USER_STATUS", CodeUtil.getValue(tenantId, codes.get("USER_STATUS"), ""+item.get("USER_STATUS")));
				item.put("AREA_NO", CodeUtil.getValue(tenantId, codes.get("AREA_ID"), ""+item.get("AREA_NO")));
				item.put("CITY_ID", CodeUtil.getValue(tenantId, codes.get("CITY_ID"), ""+item.get("CITY_ID")));
				item.put("NET_CHANNEL", CodeUtil.getValue(tenantId, codes.get("MB_NETIN_CHANNEL"), ""+item.get("NET_CHANNEL")));
				item.put("TOWNS", CodeUtil.getValue(tenantId, codes.get("TOWNS"), ""+item.get("TOWNS")));
			}
		}
		//3、设置工单返回列表
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", total);
		resp.put("items", items);
		return resp;
	}

	@Override
	public void dispatchOrder(DispatchReq request) {
		String uuid = StringUtil.getUUID();
		HashMap<String, Object> dispatchMap = new HashMap<String, Object>();
		dispatchMap.put("tenantId", request.getTenantId());
		dispatchMap.put("activitySeqId", request.getActivityId());
		for(String path:request.getOrderMap().keySet()){
			
			//如果分配的是人,path=orgPath,loginId,否则的化path=orgPath
			if("1".equals(request.getIsExe().get(path))){
				String[] orgInfo = path.split(",");
				dispatchMap.put("orgPath", orgInfo[0]);
				dispatchMap.put("loginId", orgInfo[1]);
			}else{
				//集团客户分配改造
				HashMap<String, Object> endPath = mapper.getEndLoginId(request.getTenantId(),path);
				if(null==endPath||endPath.get("LOGIN_ID")==null){
					dispatchMap.put("orgPath", path);
				}else{
					dispatchMap.put("orgPath", endPath.get("ORG_PATH"));
					dispatchMap.put("loginId", endPath.get("LOGIN_ID"));
					request.getIsExe().put(path,"1");
				}
			}
			
			dispatchMap.put("isExe", request.getIsExe().get(path));
			dispatchMap.put("recIds", request.getOrderMap().get(path));
			insertDetailRecord(dispatchMap,request.getOrgPath(),uuid);
			mapper.dispathOrder(dispatchMap);
		}
		dispatchMap.put("orgPath", request.getOrgPath());
		statisticService.reStatisticOrg(dispatchMap);
	}

	private void insertDetailRecord(HashMap<String, Object> map,String orgPath,String uuid) {
		//插入记录
		StringBuilder sql= new StringBuilder();
		sql.append("INSERT INTO PLT_DIVIDE_ORDER(REC_ID,RECEIVE_PATH,LOGIN_ID,TENANT_ID,ORDER_ID,IS_EXE) VALUES ");
		String[] recIds=(map.get("recIds")+"").split(",");
		for(String recId: recIds){
			String loginId = null==map.get("loginId")?"NULL":("'"+map.get("loginId")+"'");
			sql.append("('").append(uuid).append("','").append(map.get("orgPath")).append("',").append(loginId).append(",'").append(map.get("tenantId")).append("',").append(recId).append(",'").append(map.get("isExe")).append("'),");
		}
		BusiTools.executeDdlOnMysql(sql.substring(0,sql.length()-1), map.get("tenantId")+"");
		
		//插入LOG
		HashMap<String, Object> log = new HashMap<String, Object>();
		log.put("TENANT_ID", map.get("tenantId"));
		log.put("ORG_PATH", orgPath);
		log.put("acceptPath", map.get("orgPath"));
		log.put("loginId", map.get("loginId"));
		log.put("REC_ID", uuid);
		log.put("DIVIDE_TYPE", recIds.length);
		log.put("DIVIDE_DATE", new Date());
		log.put("IS_DIVIDE", "1");
		log.put("divideNum", recIds.length);
		mapper.insertDivideLog(log);
	}
	
	//根据工单归属组织路径 获取 约束条件 
	private String getOrderSql(RulePreReq request){
		StringBuilder sql = new StringBuilder();
		//组织路径大的范围不能超过操作人的全权限
		sql.append(" AND (o.ORG_PATH LIKE '").append(request.getOrgPath()).append("/%' OR o.ORG_PATH='").append(request.getOrgPath()).append("') ");
		//工单归属的组织列表
		HashMap<String, String> orderOrg = request.getOrderOrgs(); 
		sql.append(" AND ( 1=0 ");
		for(String org:orderOrg.keySet()){
			if("1".equals(orderOrg.get(org))){
				String[] path = org.split(",");
				sql.append(" OR (o.ORG_PATH='").append(path[0]).append("' AND o.WENDING_FLAG='").append(path[1]).append("' ) ");
			}else{
				sql.append(" OR (o.ORG_PATH='").append(org).append("' AND o.WENDING_FLAG IS NULL ) ");
			}
		}
		sql.append(" ) ");
		return sql.toString();
	}

	@Override
	@Transactional
	public Object rulePreDivide(RulePreReq request) {
		//生成并设置划分序列号
		String uuid = StringUtil.getUUID();
		HashMap<String, String> orgMap = request.getReceiveOrgs();
		if(null==orgMap||orgMap.isEmpty()){
			throw new BoncExpection(IContants.CODE_FAIL,"reveiveOrgs is empty!");
		}
		
		//获取选择的人的
		int total = getOrderNum(request);
		if(0==total){
			throw new BoncExpection(IContants.CODE_FAIL,"this orgPath not have order!");
		}
		
		//设置工单归属组织路径设置工单约束SQL
		request.setOrderOrgSql(getOrderSql(request));
		
		Integer type = request.getRuleType();
		
		List<HashMap<String, Object>> divideResult = new ArrayList<HashMap<String,Object>>();
		//按规则
		if(type.equals(-1)){
			//按收入归属进行分配
			divideResult=belongDivide(request,uuid);
		}else if(type.equals(-2)){
			//平均分
			divideResult=averageDivide(request,uuid,total);
		}else if(type>0){
			//数值分
			divideResult=numDivide(request,uuid,total);
		}else if(type.equals(-3)){
			//arpu值平均分
			divideResult=arpuDivide(request,uuid,total);
		}
		
		//末级组织到人改造
		endLoginId(request.getTenantId(),uuid);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("items", divideResult);
		int totalDivideNum=0;
		for(HashMap<String, Object> item:divideResult){
			totalDivideNum+=(Integer)item.get("divideNum");
		}
		resp.put("total", total);
		resp.put("dividedNum", totalDivideNum);
		resp.put("unDivided", total-totalDivideNum);
		resp.put("recId", uuid);
		resp.put("dividedRate", (totalDivideNum*100.0/total+"00000").substring(0,5)+"%");
		return resp;
	}
	
	private void endLoginId(String tenantId, String uuid) {
		StringBuilder sql=new StringBuilder("UPDATE PLT_DIVIDE_ORDER d,PLT_ORG_LOGIN_INFO l ");
		sql.append("SET d.LOGIN_ID=l.LOGIN_ID,d.IS_EXE=1 ");
		sql.append("WHERE d.TENANT_ID='"+tenantId+"' AND l.TENANT_ID='"+tenantId+"' AND d.RECEIVE_PATH=l.ORG_PATH AND d.IS_EXE=0 AND d.REC_ID='"+uuid+"' ");
		AsynDataIns.executeDdlOnMysql(sql.toString(), tenantId);
	}

	private int getOrderNum(RulePreReq request) {
		HashMap<String, Object> req = new HashMap<String, Object>();
		req.put("checkAll", "0");
		req.put("userFlag", "0");
		req.put("activityId", request.getActivityId());
		req.put("receives", request.getOrderOrgs());
		req.put("orgPath", request.getOrgPath());
		req.put("channelId", request.getChannelId());
		req.put("tenantId", request.getTenantId());
		
		return mapper.dispatchOrderCount(req);
	}
	
	private List<HashMap<String, Object>> arpuDivide(RulePreReq request, String uuid, int total) {
		//提取接受者
		Set<String> orgSet = request.getReceiveOrgs().keySet();
		ArrayList<HashMap<String, Object>> detailDivides = new ArrayList<HashMap<String,Object>>();
		//定义一群接受者
		List<DivideArupBean> orgReceive = new ArrayList<DivideArupBean>();
		for(String org: orgSet){
			DivideArupBean bean = new DivideArupBean();
			bean.setOrgPath(org);
			bean.setArup(0.0);
			bean.setIsExe(request.getReceiveOrgs().get(org));
			bean.setRecIds(new ArrayList<Integer>());
			orgReceive.add(bean);
		}
		
		//获取待分配工单 排好序
		List<DivideArupBean> orders = mapper.getOrdersArup(request);
		
		//i 是绝对下标
		for(int i=0,orderNum=orders.size();i<orderNum;i++){
			//排序　周期轮回
			if(i%orgSet.size()==0){
				sortReceive(orgReceive);
			}
			// 获取接收者对象
			DivideArupBean thisReceive = orgReceive.get(i%orgSet.size());
			thisReceive.setArup(thisReceive.getArup()+orders.get(i).getArup());//设置arup和，作为排序使用
			thisReceive.getRecIds().add(orders.get(i).getRecId());//将对应的权重赋给改组织机构
		}
		
		for(DivideArupBean bean:orgReceive){
			HashMap<String, Object> divideObj = new HashMap<String, Object>();
			divideObj.put("divideNum", bean.getRecIds().size());
			if("1".equals(request.getReceiveOrgs().get(bean.getOrgPath()))){
				String[] path=bean.getOrgPath().split(",");
				divideObj.put("acceptPath", path[0]);
				divideObj.put("loginId", path[1]);
			}else{
				divideObj.put("acceptPath", bean.getOrgPath());
			}
			addDivideLog(request, uuid, divideObj);
			detailDivides.add(divideObj);
		}
		BusiTools.executeDdlOnMysql(getArupDivideSql(orgReceive,uuid,request.getTenantId()),request.getTenantId());
		
		return detailDivides;
	}

	//按数值分配
	private List<HashMap<String, Object>> numDivide(RulePreReq request, String uuid, int total) {
		//提取接受者
		Set<String> orgSet = request.getReceiveOrgs().keySet();
		ArrayList<HashMap<String, Object>> detailDivides = new ArrayList<HashMap<String,Object>>();
		int i=0;	//页码
		for(String org:orgSet){
			HashMap<String, Object> divideObj = new HashMap<String, Object>();
			int divideNum = 0;
			if(total>=request.getRuleType()){
				divideNum = request.getRuleType();
			}else{
				divideNum = total;
			}
			total-=divideNum;
			if("1".equals(request.getReceiveOrgs().get(org))){
				String[] path = org.split(",");
				divideObj.put("acceptPath", path[0]);
				divideObj.put("loginId", path[1]);
			}else{
				divideObj.put("acceptPath", org);
			}
			divideObj.put("divideNum", divideNum);
			detailDivides.add(divideObj);
			addDivideLog(request, uuid, divideObj);
			BusiTools.executeDdlOnMysql(getEvgSql(request,org,uuid,request.getRuleType()*i,divideNum),request.getTenantId());
			i++;
			if(total<=0){
				break;
			}
		}
		return detailDivides;
	}

	//平均分处理逻辑
	private List<HashMap<String, Object>> averageDivide(RulePreReq request, String uuid,int total) {
		//提取待分配组织列表
		Set<String> orgSet = request.getReceiveOrgs().keySet();
		int count = orgSet.size();
		int avg = total/count;
		int mod = total%count;
		
		List<HashMap<String, Object>> detailDivides = new ArrayList<HashMap<String,Object>>();
		int i=0;
		int start=0;	//分页分起始位置
		for(String org:orgSet){
			int divideNum = 0;//工单接受的数量
			if(i<mod){
				divideNum = avg+1;	//余数个 平均数+1
			}else{
				divideNum = avg;	//总数键余数个 平均数
			}
			i++;
			//如果组织机构分配的工单数是0,将不继续执行
			if(divideNum<=0){
				continue;
			}
			HashMap<String, Object> divideObj = new HashMap<String, Object>();
			if("1".equals(request.getReceiveOrgs().get(org))){
				String[] path = org.split(",");
				divideObj.put("acceptPath", path[0]);
				divideObj.put("loginId", path[1]);
			}else{
				divideObj.put("acceptPath", org);
			}
			divideObj.put("divideNum",divideNum);
			addDivideLog(request, uuid, divideObj);
			detailDivides.add(divideObj);
			BusiTools.executeDdlOnMysql(getEvgSql(request,org,uuid,start,divideNum),request.getTenantId());
			start+=divideNum;
		}
		return detailDivides;
	}

	//按规则预分配
	private List<HashMap<String, Object>> belongDivide(RulePreReq request, String uuid) {
		List<HashMap<String, Object>> belongs = mapper.belongPreDivide(request);
		for(HashMap<String, Object> detailDivide : belongs){
			addDivideLog(request,uuid,detailDivide);
		}
		BusiTools.executeDdlOnMysql(getBelongSql(request,uuid),request.getTenantId());
		return belongs;
	}
	
	private void addDivideLog(RulePreReq request,String uuid,HashMap<String, Object> divideObj ){
		HashMap<String, Object> log = new HashMap<String, Object>();
		log.put("DIVIDE_DATE", new Date());
		log.put("IS_DIVIDE", "0");
		log.put("TENANT_ID", request.getTenantId());
		log.put("DIVIDE_TYPE", request.getRuleType());
		log.put("ORG_PATH", request.getOrgPath());
		log.put("REC_ID", uuid);
		log.putAll(divideObj);
		
		mapper.insertDivideLog(log);
	}

	private String getArupDivideSql(List<DivideArupBean> orgReceive,String uuid,String tenantId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PLT_DIVIDE_ORDER(REC_ID,RECEIVE_PATH,LOGIN_ID,TENANT_ID,ORDER_ID,IS_EXE) VALUES ");
		for(DivideArupBean bean:orgReceive){
			String[] orgPath = bean.getOrgPath().split(",");
			String loginId = "1".equals(bean.getIsExe())?("'"+orgPath[1]+"'"):" NULL ";
			for(Integer recId:bean.getRecIds()){
				sql.append("('").append(uuid).append("','").append(orgPath[0]).append("',").append(loginId).append(",'").append(tenantId).append("',").append(recId).append(", '").append(bean.getIsExe()).append("'),");
			}
		}
		return sql.toString().substring(0,sql.length()-1);
	}

	private void sortReceive(List<DivideArupBean> orgReceive) {
		Collections.sort(orgReceive,new Comparator<DivideArupBean>() {
			@Override
			public int compare(DivideArupBean lBean, DivideArupBean rBean) {
				if(lBean.getArup()==rBean.getArup()){
					return 0;
				}else if(lBean.getArup()>rBean.getArup()){
					return 1;
				}else{
					return -1;
				}
			}
		});
		
	}

	private String getEvgSql(RulePreReq request,String receive, String uuid,int start,int size) {
		String isExe = request.getReceiveOrgs().get(receive);
		String[] orgPath = receive.split(",");
		String loginId = "1".equals(isExe)?("'"+orgPath[1]+"'"):" NULL ";
		StringBuilder sql = new StringBuilder("INSERT INTO PLT_DIVIDE_ORDER(REC_ID,RECEIVE_PATH,LOGIN_ID,TENANT_ID,ORDER_ID,IS_EXE) ");
		sql.append(" SELECT '").append(uuid).append("' REC_ID,'").append(orgPath[0]).append("' RECEIVE_PATH,").append(loginId).append(" LOGIN_ID,o.TENANT_ID,o.REC_ID ORDER_ID,'").append(isExe).append("' IS_EXE FROM PLT_ORDER_INFO o WHERE ");
		sql.append(" o.TENANT_ID='").append(request.getTenantId()).append("' ");
		sql.append(" AND o.ACTIVITY_SEQ_ID=").append(request.getActivityId()).append(request.getOrderOrgSql());
		sql.append(" AND o.CONTACT_CODE='0' AND o.ORDER_STATUS='5' AND o.CHANNEL_ID='5'  ");
		sql.append("LIMIT "+start+","+size);
		logger.info(sql.toString());
		return sql.toString();
	}

	/**
	 * 有调动
	 * @param request
	 * @param uuid
	 * @return
	 */
	private String getBelongSql(RulePreReq request,String uuid) {
		String isExe = null;
		//组装分配Id
		StringBuilder orgPaths = new StringBuilder();
		for(String org:request.getReceiveOrgs().keySet()){
			orgPaths.append("'").append(org).append("',");
			if(null==isExe){
				isExe = request.getReceiveOrgs().get(org);
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PLT_DIVIDE_ORDER(REC_ID,RECEIVE_PATH,TENANT_ID,ORDER_ID,IS_EXE ) ");
		sql.append("SELECT '"+uuid+"' REC_ID,o.USER_PATH RECEIVE_PATH,o.TENANT_ID,o.REC_ID ORDER_ID,'"+isExe+"' IS_EXE ");
		sql.append("FROM PLT_ORDER_INFO o WHERE o.TENANT_ID='"+request.getTenantId()+"' ");
		sql.append("AND o.ORG_PATH='"+request.getOrgPath()+"' AND o.CHANNEL_ID='5' AND o.ORDER_STATUS=5 ");
		sql.append("AND o.CHANNEL_ID=5 AND o.ACTIVITY_SEQ_ID='"+request.getActivityId()+"' AND o.USER_PATH IN ("+orgPaths.substring(0, orgPaths.length()-1)+") ");
		return sql.toString();
	}
 

	private String getUpdateSql(HashMap<String, Object> request) {
		return "UPDATE PLT_ORDER_INFO o,PLT_DIVIDE_ORDER d SET o.ORG_PATH=d.RECEIVE_PATH,o.WENDING_FLAG=d.LOGIN_ID,o.MANUAL_PATH='"+request.get("orgPath")+"',o.RESERVE3=d.IS_EXE "
				+ " WHERE d.TENANT_ID='"+request.get("tenantId")+"' AND d.REC_ID='"+request.get("recId")+"'  AND d.TENANT_ID=o.TENANT_ID AND o.REC_ID=d.ORDER_ID";
	}
	
	/**
	 * recId
	 * tenantId
	 * orgPath
	 * activityId
	 * channelId
	 */
	@Override
	public void confirmDivide(HashMap<String, Object> request) {
		logger.info("确认划分请求参数="+JSON.toJSONString(request));
		//1、先更新状态
		mapper.changeDivideLog(request);
		//2、更新工单组织机构
		BusiTools.executeDdlOnMysql(getUpdateSql(request),request.get("tenantId")+"");
		
		//3.重新统计改组织机构路径
		request.put("activitySeqId", request.get("activityId"));
		statisticService.reStatisticOrg(request);
	}

	@Override
	public List<HashMap<String, String>> getAreaList(
			HashMap<String, Object> request) {
		List<HashMap<String, String>> areas = mapper.getAreaList(request);
		for(HashMap<String, String> areaNo:areas){
			areaNo.put("AREA_NAME", CodeUtil.getValue(request.get("tenantId")+"", "DIM_REF_AREA_NO", ""+areaNo.get("AREA_NO")));
		}
		return areas;
	}

}
