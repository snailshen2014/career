package com.bonc.busi.send.service.impl;

import java.util.HashMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.send.model.sms.SmsReq;
import com.bonc.busi.send.model.sms.SmsStatistics;
import com.bonc.utils.HttpUtil;
import com.bonc.utils.IContants;

/**
 * 获取短信发送情况（统计）
 * @author Administrator
 *
 */
public class DxThread{
	private SmsStatistics smss;
	private String url;
	private Long count;
	private DxServiceImpl dxService;
	public DxThread(SmsStatistics smss,String url,Long count,DxServiceImpl dxService){
		this.smss = smss;
		this.url = url;
		this.count = count;
		this.dxService = dxService;
	}
    public void run() {
    	System.out.println("------进入短信统计刷新-----------------------");
		SmsReq sr = sendDx();
		if(sr!=null&&sr.getSendAllNum()!=null&&sr.getSendAllNum()>0){
			HashMap<String, Object> sms = new HashMap<String, Object>();
			sms.put("externalId", smss.getExternalId());
			sms.put("SEND_NUM", sr.getSendAllNum());
			sms.put("SEND_SUC_NUM", sr.getSendSucNum());
			sms.put("SEND_ERR_NUM", sr.getErrNum());
			sms.put("tenantId", smss.getTenantId());
			dxService.refleshSmss(sms);
			if(sr.getSendSucNum()!=null&&sr.getSendAllNum()<=sr.getSendSucNum()){
				sms.put("IS_FINISH", "1");
				dxService.updateSmss(sms);
			}
		}
		IContants.SMS_STATC_TASK = "0";
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 短信批量发送 最多不超过100条
	 * @param reqs
	 * @return
	 */
	private SmsReq sendDx() {
		SmsReq bean = new SmsReq();

		String jsonString = JSON.toJSONString(getSmss());

		String resp = HttpUtil.sendPost(url, jsonString);
		JSONArray jsonArray = JSON.parseArray(resp);
		JSONObject jsonObject=(JSONObject) jsonArray.get(0);

		Integer returnflag = jsonObject.getInteger("returnflag");
		
		if(returnflag!=null&&returnflag==1){
			bean.setMsg("业务号不存在！");
			bean.setFlag(false);
		}else{
			bean.setFlag(true);
			bean.setSendAllNum(jsonObject.getLong("allSmsNum"));
			bean.setSendSucNum(jsonObject.getLong("sendSucNum"));
			bean.setErrNum(jsonObject.getLong("errNum"));
			
			bean.setSendErrNum(jsonObject.getLong("sendErrNum"));
			bean.setFormatErrNum(jsonObject.getLong("formatErrNum"));
			bean.setFormatSucNum(jsonObject.getLong("formatSucNum"));
		}
		return bean;
	}
	public SmsStatistics getSmss() {
		return smss;
	}
	public void setSmss(SmsStatistics smss) {
		this.smss = smss;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public DxServiceImpl getDxService() {
		return dxService;
	}
	public void setDxService(DxServiceImpl dxService) {
		this.dxService = dxService;
	}
	
}
