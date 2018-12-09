package com.bonc.channelapi.hbase.ctrl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.channelapi.hbase.client.HBaseClient;
import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.BaseConstant;
import com.bonc.channelapi.hbase.constant.HbaseConstant;
import com.bonc.channelapi.hbase.entity.JsonResult;
import com.bonc.channelapi.hbase.entity.SMSInfo;
import com.bonc.channelapi.hbase.util.ValidateRequestUtil;

@RestController
@RequestMapping("/v1")
public class smsMarketingCtr {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(smsMarketingCtr.class);

	@RequestMapping(value = "/smsMarketing", method = RequestMethod.POST)
	public Object method5(HttpServletRequest request,
			@RequestParam(required = false) String channelType,
			@RequestParam(required = true) String phone,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = true) String pushTime,
			@RequestParam(required = false) String pushResult,
			@RequestParam(required = false) String activityId,
			@RequestParam(required = false) String smsContent) {

		if (!ValidateRequestUtil.validate(request)) {
			return new JsonResult(BaseConstant.CODE_TOKEN_ILLEGAL,
					BaseConstant.MSG_TOKEN_ILLEGAL);
		}
		try {
			put(HbaseConstant.TABLE_QDXT_CONTACT,
					new Put(HbaseOperateClient.buildRowkey(phone,
							HbaseConstant.MARKETING_DATA_TYPE, pushTime)),
					channelType, phone, eventType, pushTime, pushResult,
					activityId, smsContent);

			if (!StringUtils.isEmpty(activityId)) {

				// 插入qdxt_contact_businesscode
				put(HbaseConstant.TABLE_QDXT_CONTACT_BUSINESSCODE,
						new Put(HbaseOperateClient.buildRowkey(activityId,
								HbaseConstant.MARKETING_DATA_TYPE, pushTime,
								phone)), channelType, phone, eventType,
						pushTime, pushResult, activityId, smsContent);

				// 插入qdxt_contact_phone
				put(HbaseConstant.TABLE_QDXT_CONTACT_PHONE,
						new Put(HbaseOperateClient.buildRowkey(activityId,
								HbaseConstant.MARKETING_DATA_TYPE, phone,
								pushTime)), channelType, phone, eventType,
						pushTime, pushResult, activityId, smsContent);
			}
		} catch (IOException e) {
			LOG.error("错误信息" + e.getMessage());
			return new JsonResult(BaseConstant.CODE_ERROR,
					BaseConstant.MSG_ERROR);
		} catch (Exception e) {
			LOG.error("错误信息" + e.getMessage());
			return new JsonResult(BaseConstant.CODE_ERROR,
					BaseConstant.MSG_ERROR);
		}
		return new JsonResult("200", "put data to hbase success");
	}

	/**
	 * Description:hbase批量写入数据
	 * 
	 * @param smsList
	 * @return
	 * @throws Exception
	 *             String
	 * @see
	 */
	@RequestMapping(value = "/smsMarketingBatch", method = { RequestMethod.POST })
	@ResponseBody
	public Object smsMarketingBatch(@RequestBody List<SMSInfo> smsList)
			throws Exception {
		if (smsList == null || smsList.size() <= 0) {
			return new JsonResult(BaseConstant.CODE_PARAM_ERROR,
					BaseConstant.MSG_PARAM_ERROR);
		} else {

			long start = System.currentTimeMillis();

			for (SMSInfo sms : smsList) {
				put(HbaseConstant.TABLE_QDXT_CONTACT,
						new Put(HbaseOperateClient.buildRowkey(sms.getPhone(),
								HbaseConstant.MARKETING_DATA_TYPE,
								sms.getPushTime())), sms.getChannelType(),
						sms.getPhone(), sms.getEventType(), sms.getPushTime(),
						sms.getPushResult(), sms.getActivityId(),
						sms.getSmsContent());
			}
			LOG.info(" 批量写入的时间   " + (System.currentTimeMillis() - start));
		}
		return new JsonResult("200", "put data to hbase success");
	}

	/**
	 * Description:qdxt_contact_businesscode表put
	 * 
	 * @param channelType
	 * @param phone
	 * @param eventType
	 * @param pushTime
	 * @param pushResult
	 * @param activityId
	 * @param smsContent
	 * @throws IOException
	 * @throws Exception
	 *             void
	 * @see
	 */
	private void put(String hTableName, Put put, String... sms)
			throws IOException, Exception {

		put.add(Bytes.toBytes("f"), Bytes.toBytes("phone"),
				Bytes.toBytes(sms[1]));
		put.add(Bytes.toBytes("f"), Bytes.toBytes("pushTime"),
				Bytes.toBytes(sms[3]));

		sms[0] = (StringUtils.isEmpty(sms[0])) ? "" : sms[0];
		put.add(Bytes.toBytes("f"), Bytes.toBytes("channelType"),
				Bytes.toBytes(sms[0]));

		sms[2] = (StringUtils.isEmpty(sms[2])) ? "" : sms[2];
		put.add(Bytes.toBytes("f"), Bytes.toBytes("eventType"),
				Bytes.toBytes(sms[2]));

		sms[4] = (StringUtils.isEmpty(sms[4])) ? "" : sms[4];
		put.add(Bytes.toBytes("f"), Bytes.toBytes("pushResult"),
				Bytes.toBytes(sms[4]));

		sms[5] = (StringUtils.isEmpty(sms[5])) ? "" : sms[5];
		put.add(Bytes.toBytes("f"), Bytes.toBytes("activityId"),
				Bytes.toBytes(sms[5]));

		sms[6] = (StringUtils.isEmpty(sms[6])) ? "" : sms[6];
		put.add(Bytes.toBytes("f"), Bytes.toBytes("smsContent"),
				Bytes.toBytes(sms[6]));
		put.add(Bytes.toBytes("f"), Bytes.toBytes("source_type"),
				Bytes.toBytes("INTERFACE_CJYX"));

		HTableInterface table = HBaseClient.getHConnection().getTable(
				hTableName);

		table.put(put);
		table.close();
	}

	/**
	 * LOG.info("插入即查询 ：phone:" + phone + "|" + "table:" +
	 * HbaseConstant.TABLE_QDXT_CONTACT + "|" + "pushTime" + pushTime + "|" +
	 * "activityId - " + activityId + " - " + singleCount(new
	 * ArrayList<String>(), phone, activityId, HbaseConstant.TABLE_QDXT_CONTACT,
	 * pushTime, pushTime) + " | put time" + (System.currentTimeMillis() -
	 * startT));
	 */

}