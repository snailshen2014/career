package com.bonc.channelapi.hbase.ctrl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.HbaseConstant;

@RestController
@RequestMapping("/v1")
public class ContactDeatailCtr {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ContactDeatailCtr.class);

	/**
	 * Description: 接触类详情
	 * 
	 * @param phone
	 * @param startTime
	 * @param endTime
	 * @param pageNum
	 * @param pageSize
	 */
	@RequestMapping(value = "/contactData", method = RequestMethod.GET)
	public Object method0(HttpServletRequest request,
			@RequestParam(required = true) String phone,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false) String businessCode) {

		if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			Calendar startDate = Calendar.getInstance();
			int startYear = startDate.get(Calendar.YEAR);
			int startMonth = startDate.get(Calendar.MONTH);
			int startDay = startDate.get(Calendar.DATE);

			startDate.set(startYear, startMonth, startDay);

			endTime = sdf.format(startDate.getTime());
			startDate.add(Calendar.MONTH, -1);
			startTime = sdf.format(startDate.getTime());

		}
		byte[] start = HbaseOperateClient.buildRowkey(phone,
				HbaseConstant.MARKETING_DATA_TYPE, startTime);

		byte[] end = HbaseOperateClient.buildRowkey(phone,
				HbaseConstant.MARKETING_DATA_TYPE, endTime, "3");

		// List<Map<String, Object>> values = new ArrayList<Map<String,
		// Object>>();
		try {
			JSONArray returnArrayResult = new JSONArray();
			ResultScanner resultScanner = HbaseOperateClient.getResultScanner(
					start, end, HbaseConstant.TABLE_QDXT_CONTACT);

			for (Result result : resultScanner) {
				Map<String, Object> columnMap = new HashMap<String, Object>();

				columnMap.put("phone", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("phone"))));

				columnMap.put("pushTime", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("pushTime"))));

				columnMap.put("channelType", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("channelType"))));

				columnMap.put("eventType", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("eventType"))));

				columnMap.put("pushResult", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("pushResult"))));

				columnMap.put("activityId", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("activityId"))));

				columnMap.put("smsContent", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("smsContent"))));

				columnMap.put("source_type", Bytes.toString(result.getValue(
						Bytes.toBytes("f"), Bytes.toBytes("source_type"))));

				returnArrayResult.add(columnMap);
			}

			return returnArrayResult;

		} catch (Exception e) {
			LOG.error("error message", e.getMessage());
		}

		return null;
	}
}
