package com.bonc.busi.track.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.busi.code.service.CodeService;
import com.bonc.busi.track.mapper.TrackMapper;
import com.bonc.busi.track.service.TrackService;
import com.bonc.common.utils.BoncExpection;
import com.bonc.utils.CodeUtil;
import com.bonc.utils.DateUtil;
import com.bonc.utils.IContants;

@Service("trackService")
public class TrackServiceImpl implements TrackService{
	
	@Autowired
	private TrackMapper mapper;
	
	@Autowired
	private CodeService codeService;

	@Override
	public Object getContactTrack(HashMap<String, Object> req) {
		//int 类型验证
		if(!(req.get("pageSize") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageSize is not int");
		}
		if(!(req.get("pageNum") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageNum is not int");
		}
		if(null!=req.get("contactCode") &&!(req.get("contactCode") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"contactCode is not int");
		}
		
		String channelId = req.get("channelId")+"";
		if(IContants.DX_CHANNEL.equals(channelId)){
			return getDxContactTrack(req);
		}
		
		//第1步：查询活动所有的批次,并拼接批次ID,短信渠道特殊处理
		List<HashMap<String, Object>> activitySeq = mapper.getActivitySet(req);
		
		//失效批次号
		StringBuilder invalidRec = new StringBuilder("(");
		Boolean invalid = false;
		//生效批次号
		StringBuilder effectRec = new StringBuilder("(");
		Boolean effect = false;
		
		//查询出一个活动，
		HashMap<String, HashMap<String, Object>> activitys = new HashMap<String, HashMap<String,Object>>();
		for(HashMap<String, Object> item:activitySeq){
			if("2".equals(item.get("ACTIVITY_STATUS")+"")){
				invalidRec.append(item.get("REC_ID")).append(",");
				invalid = true;
			}else{
				effectRec.append(item.get("REC_ID")).append(",");
				effect = true;
			}
			activitys.put(item.get("REC_ID")+"", item);
		}
		if(invalid){
			req.put("invalidRec", invalidRec.substring(0,invalidRec.length()-1)+")");
		}
		if(effect){
			req.put("effectRec", effectRec.substring(0,effectRec.length()-1)+")");
		}
		
		//根据查询条件确定工单所在的表
		Long effectCount = 0l;
		Long invalidCount=0l;
	
		//第２步：根据渠道定位需要操作的表名称
		HashMap<String, String> codeTable= CodeUtil.getCodeMap(req.get("tenantId")+"", IContants.CHANNEL_ORDER_TABLE);
		String tableName = codeTable.get(req.get("channelId"));
		if(null==tableName){
			throw new BoncExpection(IContants.CODE_FAIL,"错误的渠道编号！"+req.get("channelId"));
		}
		String ignore = IContants.YX_CHANNEL.equals(channelId)?" IGNORE INDEX (IDX_ORG_PATH) ":"";
		
		req.put("tableName1", tableName+ignore);
		req.put("tableName0", tableName+"_HIS"+ignore);
		
		//第３步：根据约束条件计算出　失效和有效的工单数，当且仅当该活动存在有效或者无效的批次，才会对工单表或者历史表进行查询,如果短信渠道需要用到批次发送的月份
		//短信有效批次也从无效里面查询
		if(effect){
			req.put("activityStatus", "1");
			effectCount = mapper.countContactTrack(req);
		}
		if(invalid){
			req.put("activityStatus", "0");
			invalidCount = mapper.countContactTrack(req);
		}
		
		Integer pageSize = (Integer) req.get("pageSize");
		Integer pageNum = (Integer) req.get("pageNum");
		
		Long total = effectCount+invalidCount;
		//第４部：根据分页情况，判断待查询页数据可能所在的表，只根据可能存在的表中获取待查询工单
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		if(pageSize*pageNum<=effectCount&&effectCount>0){
			//只需要查询有效工单
			req.put("activityStatus", "1");
			items = mapper.listContactTrack(req);
		}else if(pageSize*pageNum>effectCount&&(pageNum-1)*pageSize<effectCount){
			req.put("activityStatus", "1");
			items = mapper.listContactTrack(req); 
			if(invalidCount>0){
				Long num = pageSize*pageNum-effectCount;
				//重新计算　pageSize pageNum
				req.put("pageNum", 1);
				req.put("pageSize", num.intValue());
				req.put("activityStatus", "0");
				items.addAll(mapper.listContactTrack(req));
			}
		}else{
			if(invalidCount>0){
				Long num = pageNum-effectCount/pageSize;
				//重新计算　pageSize pageNum
				req.put("activityStatus", "0");
				req.put("pageNum", num.intValue());
				items = mapper.listContactTrack(req);
			}
		}
		//第５步：对查询的数据列表数据进行处理
		exchangeItems(req.get("tenantId")+"",items,activitys);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", total);
		resp.put("items", items);
		return resp;
	}

	private void exchangeItems(String tenantId,List<HashMap<String, Object>> items,HashMap<String, HashMap<String, Object>> activitys) {
		
		HashMap<String, String> contactCode = CodeUtil.getCodeMap(tenantId, IContants.CONTACT_CODE);
//		HashMap<String, String> contactCode = codeService.getValue(tenantId, IContants.CONTACT_CODE);
		for(HashMap<String, Object> item:items){
			item.put("CONTACT_CODE",contactCode.get(item.get("CONTACT_CODE")));
//			item.put("CHANNEL_ID", );
		}
	}

	private Object getDxContactTrack(HashMap<String,Object> req) {
		List<HashMap<String, Object>> activitySeq = mapper.getDxActivitySet(req);
		
		//获取近三个月的月名称
		List<String> lastedMonth = DateUtil.getLatelyMonthForNum(3, DateUtil.DateFomart.MONTH);
		
		//初始化成空串
		HashMap<String, StringBuilder> monthRecs= new HashMap<String, StringBuilder>();
		for(String month:lastedMonth){
			monthRecs.put(month, new StringBuilder("("));
		}
		
		//通过逻辑锁定　每个月份可能包含的批次号
		StringBuilder recIds = new StringBuilder("("); 
		
		Boolean flag=false;
		//查询出一个活动，
		HashMap<String, HashMap<String, Object>> activitys = new HashMap<String, HashMap<String,Object>>();
		for(HashMap<String, Object> item:activitySeq){
			String beginDate = item.get("ORDER_BEGIN_DATE")+"";
			if("1".equals(item.get("STATUS")+"")){
				if(null!=monthRecs.get(beginDate)){
					monthRecs.get(beginDate).append(item.get("REC_ID")).append(",");
				}
			}else{
				recIds.append(item.get("REC_ID")).append(",");
				flag = true;
			}
			
			activitys.put(item.get("REC_ID")+"", item);
		}
		//合成短信工单表的批次ID
		req.put("effectRec", recIds.substring(0,recIds.length()-1)+")");
		
		for(String month:lastedMonth){
			if("(".equals(monthRecs.get(month).toString())){
				monthRecs.put(month, null);
			}else{
				monthRecs.put(month, new StringBuilder(monthRecs.get(month).substring(0,monthRecs.get(month).length()-1)+")"));
			}
		}
		
		//第２步：根据渠道定位需要操作的表名称
		HashMap<String, String> codeTable= CodeUtil.getCodeMap(req.get("tenantId")+"", IContants.CHANNEL_ORDER_TABLE);
		String tableName = codeTable.get(req.get("channelId"));
		if(null==tableName){
			throw new BoncExpection(IContants.CODE_FAIL,"错误的渠道编号！"+req.get("channelId"));
		}

		HashMap<String, Long> monthCount = new HashMap<String, Long>();
		Long total = 0l;
		
		Long effectCount=0l;
		
		if(flag){
			req.put("activityStatus", "1");
			req.put("tableName1", tableName);
			effectCount = mapper.countContactTrack(req);
			total+=effectCount;
		}
		
		//短信的都视为无效工单
		req.put("activityStatus", "0");
		HashMap<String, String> tableMap = new HashMap<String, String>(); 
		for(String month:lastedMonth){
			if(monthRecs.get(month)!=null){
				req.put("invalidRec", monthRecs.get(month).toString());
				String _tableName = tableName+"_HIS_"+month.substring(4,6);
				tableMap.put(month, _tableName);
				req.put("tableName0", _tableName);
				Long count = mapper.countContactTrack(req);
				monthCount.put(month, count);
				total+=count;
			}
		}
		
		Integer pageSize = (Integer)req.get("pageSize");
		Integer pageNum = (Integer)req.get("pageNum");
		
		Integer end = pageSize*pageNum;
		Integer start = pageSize*(pageNum-1);
		
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		
		Long tableCount = 0l;
		if(start<effectCount){
			tableCount+=effectCount;
			req.put("activityStatus", "1");
			req.put("tableName0", tableName);
			items = mapper.listContactTrack(req);
		}
		
		req.put("activityStatus", "0");
		for(String month:lastedMonth){
			req.put("tableName0", tableMap.get(month));
			if(monthCount.get(month)!=null){
				//始终是新表的第一页
				if(start<tableCount&&end>tableCount){
					Long num = end-tableCount;
					req.put("pageNum", 1);
					req.put("pageSize", num.intValue());
					items.addAll(mapper.listContactTrack(req));
				}
				//多数情况下应出于这一种情况
				if(start>=tableCount&&start<monthCount.get(month)+tableCount){
					Integer curPageNum=(int) (tableCount/pageSize);
					req.put("pageNum", (Integer)req.get("pageNum")-curPageNum);
					items.addAll(mapper.listContactTrack(req));
				}
				tableCount+=monthCount.get(month);//累计 
			}
		}
		
		exchangeItems(req.get("tenantId")+"", items, activitys);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", total);
		resp.put("items", items);
		return resp;
	}

	public static void main(String[] args) {
		String minMonth = "201607";
		System.out.println(minMonth.compareTo("201608"));
		
		System.out.println(new StringBuilder("").toString().equals(new StringBuilder("").toString()));
	}

	@Override
	public Object getActivityChannelNum(HashMap<String, Object> req) {
		List<HashMap<String, Object>> items = mapper.getChannelNums(req);
		for(HashMap<String, Object> item:items){
			item.put("TENANT_ID", req.get("tenantId"));
			HashMap<String, Object> contactNums = mapper.getContactNums(item);
			item.putAll(contactNums);
			if(Integer.parseInt(item.get("UN_CONTACT_COUNT")+"")<0){
				item.put("UN_CONTACT_COUNT", 0);
			}
		}
		return items;
	}

	@Override
	public Object updateHistory(HashMap<String, Object> req) {
		
		if(!(req.get("pageSize") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageSize is not int");
		}
		if(!(req.get("pageNum") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageNum is not int");
		}
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		Integer count = mapper.countupdatehistory(req);
//		String limit = (Integer)req.get("pageSize")*((Integer)req.get("pageNum")-1)+","+req.get("pageSize");
//		req.put("limit", limit);
		List<HashMap<String, Object>> items = mapper.updatehistory(req);
		resp.put("total", count);
		resp.put("items", items);
		return resp;
	}

	@Override
	public Object getOrderRecord(HashMap<String, Object> req) {
		//int 类型验证
		if(!(req.get("pageSize") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageSize is not int");
		}
		if(!(req.get("pageNum") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageNum is not int");
		}
		if(null!=req.get("contactCode") &&!(req.get("contactCode") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"contactCode is not int");
		}
		
		String channelId = req.get("channelId")+"";
		
		HashMap<String, String> codeTable= CodeUtil.getCodeMap(req.get("tenantId")+"", IContants.CHANNEL_ORDER_TABLE);
		String tableName = codeTable.get(channelId);
		if(null==tableName){
			throw new BoncExpection(IContants.CODE_FAIL,"错误的渠道编号！"+req.get("channelId"));
		}
		
//		String month = 
		//看看短信发没发完
		if(IContants.DX_CHANNEL.equals(channelId)){
			HashMap<String, Object> status=mapper.getDxOrderStatus(req);
			if("0".equals(status.get("STATUS")+"")){
				req.put("tableName", tableName);
			}else{
				status = mapper.getRecStatus(req);
				req.put("tableName", tableName+"_HIS_"+(status.get("SEND_MONTH")+"").substring(4,6));
			}
		}else{
			HashMap<String, Object> status = mapper.getRecStatus(req);
			if("2".equals(status.get("ACTIVITY_STATUS")+"")){
				req.put("tableName", tableName+"_HIS");
			}else{
				req.put("tableName", tableName);
			}
		}
		
		Long count = mapper.countOrderRecord(req);
		List<HashMap<String, Object>> items = mapper.listOrderRecord(req);
		
		HashMap<String, String> channelDesc= CodeUtil.getCodeMap(req.get("tenantId")+"", IContants.DIM_CHANNEL_DESC);
		for(HashMap<String, Object> item: items){
			item.put("CHANNEL_DESC", channelDesc.get(channelId));
		}
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", count);
		resp.put("items", items);
		return resp;
	}

	@Override
	public Object countDetail(HashMap<String, Object> req) {
		//查询活动的批次
		List<HashMap<String, Object>> activitys = mapper.findActivitySeqs(req);
		
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		for(HashMap<String, Object> activity:activitys){
			req.put("activitySeqId", activity.get("REC_ID"));
			List<HashMap<String, Object>> countDateils = mapper.countDetails(req);
			
			for(HashMap<String, Object> detail:countDateils){
				detail.put("LAST_ORDER_CREATE_TIME", activity.get("LAST_ORDER_CREATE_TIME"));
			}
			items.addAll(countDateils);
		}
		
		return items;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object filterList(HashMap<String, Object> req) {
		//int 类型验证
		if(!(req.get("pageSize") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageSize is not int");
		}
		if(!(req.get("pageNum") instanceof Integer)){
			throw new BoncExpection(IContants.CODE_FAIL,"pageNum is not int");
		}
		
		HashMap<String, Object> resp = null;
		
		//1 是黑名单过滤数
		if("1".equals(req.get("type"))){
			resp = (HashMap<String, Object>)blackFilterList(req);
		//2是规则过滤数
		}else if("2".equals(req.get("type"))){
			resp = (HashMap<String, Object>)ruleFilterList(req);
		//3是成功过滤数
		}else if("3".equals(req.get("type"))){
			resp = (HashMap<String, Object>)successFilterList(req);
		}else {
			throw new BoncExpection(IContants.CODE_FAIL,"error req's pama type");
		}
		
		List<HashMap<String, Object>> items = (ArrayList<HashMap<String,Object>>)resp.get("items");
		for(HashMap<String, Object> item:items){
			String tenantId = (String)req.get("tenantId");
			item.put("PROV_ID", CodeUtil.getValue(tenantId, "PROV_ID",item.get("PROV_ID")+""));
			item.put("AREA_NO", CodeUtil.getValue(tenantId, "DIM_REF_AREA_NO",item.get("AREA_NO")+""));
			item.put("CITYID", CodeUtil.getValue(tenantId, "DIM_REF_CITY_ID",item.get("CITYID")+""));
			item.put("CHANNEL_DESC", CodeUtil.getValue(tenantId, "DIM_CHANNEL_DESC",item.get("CHANNEL_ID")+""));
		}
		
		return resp;
	}

	private Object ruleFilterList(HashMap<String, Object> req) {
		//第1步：查询活动所有的批次,并拼接批次ID,短信渠道特殊处理
		List<HashMap<String, Object>> activitySeq = mapper.getActivitySet(req);
				
				
		//只有短信渠道历史分月，其他历史不分月
		String channelId = req.get("channelId")+"";
		//不同批次生成时候所在的月份
		List<String> findTable = new ArrayList<String>();
		HashMap<String, Integer> tableCount = new HashMap<String, Integer>();
		Integer count = 0;
		
		//存储每张表里面所包含的批次号
		HashMap<String, String> recMaps = new HashMap<String, String>();
		
		HashMap<String, String> codeTable= CodeUtil.getCodeMap(req.get("tenantId")+"", IContants.CHANNEL_ORDER_TABLE);
		
		for(HashMap<String, Object> activity:activitySeq){
			String tableName = null;
			if(IContants.DX_CHANNEL.equals(channelId)&&null!=activity.get("ORDER_BEGIN_DATE")){
				String ORDER_BEGIN_DATE = activity.get("ORDER_BEGIN_DATE")+"";
				tableName = codeTable.get(channelId)+"_HIS_"+ORDER_BEGIN_DATE.substring(4,6);
			}else{
				tableName = codeTable.get(channelId)+"_HIS";
			}
			
			if(null!=tableName&&!findTable.contains(tableName)){
				findTable.add(tableName);
			}
			
			if(null==recMaps.get(tableName)){
				recMaps.put(tableName, "("+activity.get("REC_ID")+",");
			}else{
				recMaps.put(tableName, recMaps.get(tableName)+activity.get("REC_ID")+",");
			}
		}
		
		//处理最后一个分号
		for(String tableName:findTable){
			recMaps.put(tableName, recMaps.get(tableName).substring(0,recMaps.get(tableName).length()-1)+")");
		}
		
		//查询每个表中过滤掉的用户数
		for(String tableName : findTable){
			req.put("tableName", tableName);
			req.put("recIds", recMaps.get(tableName));
			Integer num = mapper.countRuleFilter(req);
			tableCount.put(tableName, num);
			count+=num;
		}
		
		//
		Integer max = 0;
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String,Object>>();
		
		//获取原始分页信息
		Integer pageSize= (Integer)req.get("pageSize");
		Integer pageNum= (Integer)req.get("pageNum");
		
		for(String tableName : findTable){
			req.put("pageNum", pageNum);
			req.put("pageSize", pageSize);
			req.put("pageStart", pageSize*(pageNum-1));
			
			max+=tableCount.get(tableName);
			
			//获取上界限
			Integer a = pageSize*pageNum;
			Integer b = pageSize*(pageNum-1);
			//
			if(max>=a){
				req.put("tableName", tableName);
				req.put("recIds", recMaps.get(tableName));
				List<HashMap<String, Object>> result = mapper.getFilterList(req);
				items.addAll(result);
				break;
			}
			if(b<max&&max<a){
				List<HashMap<String, Object>> result = mapper.getFilterList(req);
				items.addAll(result);
				pageNum = 1;
				pageSize = pageSize-b+max;
			}
			if(b>=max){
				List<HashMap<String, Object>> result = mapper.getFilterList(req);
				items.addAll(result);
				pageNum = pageNum - (tableCount.get(tableName))/pageSize + 1;
			}
		}
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", count);
		resp.put("items", items);
		return resp;
	}

	private Object successFilterList(HashMap<String, Object> req) {
		req.put("orderStatus", 6);
		return getContactTrack(req);
	}

	private Object blackFilterList(HashMap<String, Object> req) {
		//第1步：查询活动所有的批次,并拼接批次ID,短信渠道特殊处理
		List<HashMap<String, Object>> activitySeq = mapper.getActivitySet(req);
		
		//生效批次号
		StringBuilder effectRec = new StringBuilder("(");
		//查询出一个活动，
		HashMap<String, HashMap<String, Object>> activitys = new HashMap<String, HashMap<String,Object>>();
		for(HashMap<String, Object> item:activitySeq){
			effectRec.append(item.get("REC_ID")).append(",");
			activitys.put(item.get("REC_ID")+"", item);
		}
		req.put("recIds", effectRec.substring(0,effectRec.length()-1)+")");
		Integer count  = mapper.countBlack(req);
		
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("total", count);
		
//		req.put("pageStart", (Integer)req.get("pageSize")*(((Integer)(req.get("pageNum")))-1));
		List<HashMap<String, Object>> items = mapper.listBlack(req);
		resp.put("items", items);
		return resp;
	}
}
