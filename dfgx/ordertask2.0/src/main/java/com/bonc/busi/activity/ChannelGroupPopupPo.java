package com.bonc.busi.activity;

import java.util.List;

public class ChannelGroupPopupPo  implements ChannelPo{

	private String activity_Id;
	private String tenant_id;
	private String channelId;	//渠道Id
	private String businessHall; //营业厅编码
	private String businessHallName; //营业厅名称
	private String validate;	//是否需要生效
	private String numberLimit;		//当日弹出次数限制
	private String target;			//目标
	private String content;		//话术信息
	private String channelName;  //营业厅名称
	private String filterCondition;  //筛选条件
	private String filterConditionSql;   	//筛选条件对应sql
	private List<ChannelSpecialFilterPo> channelSpecialList;		//客户细分列表

	/**
	 * 工单下发规则
	 */
	private String orderIssuedRule;
	/**
	 * 短信话术
	 * @return
	 */
	private String smsSendWords;

	public String getSmsSendWords() {
		return smsSendWords;
	}
	public void setSmsSendWords(String smsSendWords) {
		this.smsSendWords = smsSendWords;
	}
	public String getOrderIssuedRule() {
		return orderIssuedRule;
	}
	public void setOrderIssuedRule(String orderIssuedRule) {
		this.orderIssuedRule = orderIssuedRule;
	}
	public String getBusinessHallName() {
		return businessHallName;
	}
	public void setBusinessHallName(String businessHallName) {
		this.businessHallName = businessHallName;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getBusinessHall() {
		return businessHall;
	}
	public void setBusinessHall(String businessHall) {
		this.businessHall = businessHall;
	}
	public String getFilterCondition() {
		return filterCondition;
	}
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}
	public String getFilterConditionSql() {
		return filterConditionSql;
	}
	public void setFilterConditionSql(String filterConditionSql) {
		this.filterConditionSql = filterConditionSql;
	}
	public String getActivity_Id() {
		return activity_Id;
	}
	public void setActivity_Id(String activity_Id) {
		this.activity_Id = activity_Id;
	}
	public String getTenant_id() {
		return tenant_id;
	}
	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}
	public String getValidate() {
		return validate;
	}
	public void setValidate(String validate) {
		this.validate = validate;
	}
	public String getNumberLimit() {
		return numberLimit;
	}
	public void setNumberLimit(String numberLimit) {
		this.numberLimit = numberLimit;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	//for ACTIVITY_SEQ_ID
	private Integer ACTIVITY_SEQ_ID;
	public void setACTIVITY_SEQ_ID(Integer id) {
		this.ACTIVITY_SEQ_ID = id;
	}
	public Integer getACTIVITY_SEQ_ID(){
		return this.ACTIVITY_SEQ_ID;
	}
	public List<ChannelSpecialFilterPo> getChannelSpecialList() {
		return channelSpecialList;
	}
	public void setChannelSpecialList(
			List<ChannelSpecialFilterPo> channelSpecialList) {
		this.channelSpecialList = channelSpecialList;
	}
}
