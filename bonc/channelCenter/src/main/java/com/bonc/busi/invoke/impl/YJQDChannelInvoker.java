package com.bonc.busi.invoke.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.busi.ftpInfo.mapper.FTPInfoMapper;
import com.bonc.busi.ftpInfo.po.FTPInfo;
import com.bonc.busi.invoke.ChannelInvoker;
import com.bonc.busi.invoke.IDataSendService;
import com.bonc.busi.sendField.mapper.SendFieldMapper;
import com.bonc.common.utils.ChannelEnum;
import com.bonc.common.utils.FileNameUtils;
import com.bonc.common.utils.WebServiceUtils;
import com.bonc.utils.PropertiesUtil;

/**
 * 一级渠道 数据下发
 * 
 * @author sky
 * @param <T>
 *
 */
@Component(value = "yJQDChannelInvoker")
public class YJQDChannelInvoker implements ChannelInvoker {

	Logger loger = Logger.getLogger(YJQDChannelInvoker.class);
	
	@Value("${dataSending.webService.url}")
	private String dataSendingWebServiceUrl;

	@Autowired
	FTPInfoMapper fTPInfoMapper;
	@Autowired
	SendFieldMapper sendFieldMapper;

	@Override
	public String invoke(Object object) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//数据下发开关控制
		String sending = PropertiesUtil.getWebService("dataSending.sending");
		if(sending!= null && "off".equals(sending)){
			resultMap.put("success",true);
			resultMap.put("message", "数据下发成功！");
			return JSON.toJSONString(resultMap);
		}
		Map<String, Object> dataMap = (Map<String, Object>) object;
		String  activityType = dataMap.get("activityType")!=null ?dataMap.get("activityType").toString():"";
		// 生成的xml数据文件 推送文件名称
		String sendConfigFileName = "";
		// 下发数据 文件名称
		String sendTableFileName = "";
		/*sendTableFileName = FileNameUtils.createFileName("D", "CLPS", "0103", "F", "A", null,
				dataMap.get("usergroupId").toString(), 12);
		StringBuffer sb = new StringBuffer(sendTableFileName);
		sendConfigFileName = sb.replace(18, 22, "0104").toString();*/
		
		if("3".equals(activityType)){
			sendTableFileName =  FileNameUtils.createFileName("C", "CLPS", "0101", "F", "A", null,
					dataMap.get("activityId").toString(), 8);
			StringBuffer sb = new StringBuffer(sendTableFileName);
			sendConfigFileName = sb.replace(14, 18, "0102").toString();
			
		}else{
			sendTableFileName =  FileNameUtils.createFileName("D", "CLPS", "0101", "F", "A", null, 
					dataMap.get("activityId").toString(), 8);
			StringBuffer sb = new StringBuffer(sendTableFileName);
			sendConfigFileName = sb.replace(14, 18, "0102").toString();
		}
		
		
		// 查询FTP信息
		FTPInfo ftpInfo = fTPInfoMapper.findFTPInfoByProvId(
				dataMap.get("provId").toString(),dataMap.get("tenantId").toString());

		String confRes = invokeWebService(sendConfigFileName, dataMap, ftpInfo, "conf");
		String dataRes = invokeWebService(sendTableFileName, dataMap, ftpInfo, "data");

		JSONObject confResInfo = JSON.parseObject(confRes);
		JSONObject dataResInfo = JSON.parseObject(dataRes);
		boolean confSendFlag = confResInfo.getBoolean("success");
		boolean dataSendFlag = dataResInfo.getBoolean("success");
		

		if (confSendFlag && dataSendFlag) {
			resultMap.put("success", true);
			resultMap.put("message", "数据下发成功！");
		} else {

			StringBuilder error = new StringBuilder();
			error.append("活动目标用户信息表下发");
			error.append(dataSendFlag ? "成功" : "失败");
			error.append(",[");
			error.append(dataResInfo.get("message") != null ? dataResInfo.get("message").toString() : "");
			error.append("]");
			error.append(" ");
			error.append("配置信息表下发");
			error.append(confSendFlag ? "成功" : "失败");
			error.append(",[");
			error.append(confResInfo.get("message") != null ? confResInfo.get("message").toString() : "");
			error.append("]");

			resultMap.put("success", false);
			resultMap.put("message", "数据下发失败！");
			resultMap.put("error", error.toString());
			resultMap.put("errorcode", "20001");

		}
		loger.info("info:" + JSON.toJSONString(resultMap));
		return JSON.toJSONString(resultMap);
	}

	/**
	 * 数据下发服务调用
	 * 
	 * @param sendFileName
	 * @param dataMap
	 * @param ftpInfo
	 * @return
	 * @throws Exception
	 */
	private String invokeWebService(String sendFileName, Map dataMap, FTPInfo ftpInfo, String type)
			throws Exception {
		HashMap<String, Object> jsonParamMap = new HashMap<String, Object>();
		String confSql = createSql(type, dataMap);
		if("conf".equals(type))
			assmebleParam(sendFileName, confSql, ftpInfo, dataMap, jsonParamMap,true);
		else if("data".equals(type))
			assmebleParam(sendFileName, confSql, ftpInfo, dataMap, jsonParamMap,false);
		// 将参数转换成JSON串
		String jsonParam = JSON.toJSONString(jsonParamMap);
		System.out.println("jsonParam==========" + jsonParam);
		String url = dataSendingWebServiceUrl;
		// 调用WEBSERVICE
		String res = WebServiceUtils.sendWebService(jsonParam, url, IDataSendService.class, "genAndSend");
		return res;
	}

	

	/**
	 * 组装webservice 调用参数
	 * 
	 * @param fileName
	 * @param sql
	 * @param ftpInfo
	 * @param dataMap
	 * @param jsonParamMap
	 */
	private void assmebleParam(String fileName, String sql, FTPInfo ftpInfo, Map dataMap,
			HashMap<String, Object> jsonParamMap,boolean isBigText) {
		// FTP信息JSON串
		Map ftp = new HashMap();
		jsonParamMap.put("ftpInfo", ftpInfo);
		// 是否删除文件
		jsonParamMap.put("delSource", false);
		// 是否包含表头
		jsonParamMap.put("hasHead", false);
		// 文件名称
		jsonParamMap.put("fileName", fileName);
		// 账期
		String month = dataMap.get("dealMonth").toString();
		jsonParamMap.put("monthId", month);
		// 省分
		String provId = dataMap.get("provId").toString();
		jsonParamMap.put("provId", ("-1".equals(provId) ? "000" : provId));
		// 执行SQL
		jsonParamMap.put("sqlSource", sql);
		// 数据库类型
		jsonParamMap.put("tools", "mysql");
		// 是否是大文本字段
		jsonParamMap.put("isBigText", isBigText);

	}
	
	/**
	 * 创建查询sql
	 * 
	 * @param type
	 * @param dataMap
	 * @return
	 */
	private String createSql(String type, Map dataMap) {
		String sql = "";
		if ("conf".equals(type)) {
			sql = PropertiesUtil.getWebService("dataSending.confSql");
			sql = sql.replace("#activityId#", dataMap.get("activityId").toString()).
					replace("#tenantId#", dataMap.get("tenantId").toString()).
					replace("#activitySeqId#", dataMap.get("recId").toString()).
					replace("#dealMonth#", dataMap.get("dealMonth").toString());

		} else if ("data".equals(type)) {
			String colmunSql = "";
			List<Map> colmunList = sendFieldMapper.findSendField(dataMap.get("tenantId").toString());
			for (Map c : colmunList) {
				//colmunSql += c.get("COLUMN_NAME") + " " + c.get("ALIAS") + ",";
				if("ACCT_FEE".equals(c.get("ALIAS"))){
					colmunSql += c.get("ALIAS") + "/100 "+c.get("ALIAS") +",";
				}else{
					colmunSql += c.get("ALIAS") + ",";
				}
				
			}
			colmunSql = colmunSql.substring(0, colmunSql.length() - 1);
			//sql = "SELECT " + colmunSql + " FROM ("+PropertiesUtil.getWebService("dataSending.dataSql")+" ) T";
			sql = "SELECT " + colmunSql +" "+PropertiesUtil.getWebService("dataSending.dataSql")+" ";
			sql = sql.replace("#activityId#", dataMap.get("activityId").toString()).
					replace("#tenantId#", dataMap.get("tenantId").toString()).
					replace("#dealMonth#", dataMap.get("dealMonth").toString()).
					replace("#recId#", dataMap.get("recId").toString());
					
		/*	sql = "SELECT DISTINCT " + colmunSql + " FROM (SELECT ORD.USER_ID,"
					+ "ORD.PHONE_NUMBER,ORD.SERVICE_TYPE,ACT.ACTIVITY_ID,ACT.GROUP_ID,"
					+ "ORD.PROV_ID,ORD.CITY_ID,ORD.PAY_MODE,"
					+ "ORD.PRODUCT_CLASS,ORD.AGREEMENT_TYPE,ORD.AGREEMENT_EXPIRE_TIME,"
					+ "ORD.ACCT_FEE,ORD.WENDING_FLAG,ORD.ELECCHANNEL_FLAG "
					+ " FROM  PLT_ORDER_INFO ORD,PLT_ACTIVITY_INFO ACT WHERE "
					+ "ORD.ACTIVITY_SEQ_ID = ACT.REC_ID "
					+ "AND ACT.ACTIVITY_ID='" + dataMap.get("activityId")
					+ "' AND ACT.TENANT_ID = '"+dataMap.get("tenantId")
					+ "' AND ORD.DEAL_MONTH='" + dataMap.get("dealMonth") 
					+ "' AND (ORD.CHANNEL_ID ='1' OR ORD.CHANNEL_ID ='2' OR ORD.CHANNEL_ID ='9')) T";*/
		}
		return sql;
	}
	@Override
	public Boolean supports(String channelId) {

		return ChannelEnum.YJQD.getCode().equals(channelId);
	}

}