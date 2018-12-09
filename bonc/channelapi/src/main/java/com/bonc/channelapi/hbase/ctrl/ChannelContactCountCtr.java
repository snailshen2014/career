package com.bonc.channelapi.hbase.ctrl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.BaseConstant;
import com.bonc.channelapi.hbase.constant.HbaseConstant;
import com.bonc.channelapi.hbase.entity.JsonResult;
import com.bonc.channelapi.hbase.entity.QueryContent;
import com.bonc.channelapi.hbase.util.DateVerificationUtil;
import com.bonc.channelapi.hbase.util.ValidateRequestUtil;

@RestController
@RequestMapping("/v1")
public class ChannelContactCountCtr {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(ChannelContactCountCtr.class);

	/**
	 * Description: 查询咨询类总数
	 * 
	 * @param phone
	 * @param startTime
	 * @param endTime
	 */
	@RequestMapping(value = "/channelContactCount", method = RequestMethod.GET)
	public Object method5(HttpServletRequest request,
			@RequestParam(required = true) String phone,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false) String businessCode) {
		if (!ValidateRequestUtil.validate(request)) {
			return new JsonResult(BaseConstant.CODE_TOKEN_ILLEGAL,
					BaseConstant.MSG_TOKEN_ILLEGAL);
		}

		try {
			JSONObject returnObject = new JSONObject();
			returnObject.put(
					"count",
					singleCount(new ArrayList<String>(), phone, businessCode,
							startTime, endTime));

			return JSON.toJSONString(returnObject);
		} catch (ParseException e) {
			LOG.error("报错信息：" + e.getMessage());
			return new JsonResult(BaseConstant.CODE_DATA_NONEXIST,
					BaseConstant.MSG_DATA_NONEXIST);
		} catch (Throwable e) {
			LOG.error("报错信息：", e);
			return new JsonResult(BaseConstant.CODE_ERROR,
					BaseConstant.MSG_ERROR);
		}

	}

	/**
	 * Description:hbase批量查询数据
	 * 
	 * @param smsList
	 * @return
	 * @throws Exception
	 *             String
	 * @see
	 */
	@RequestMapping(value = "/channelContactCountBatch", method = { RequestMethod.POST })
	@ResponseBody
	public Object smsMarketingBatch(
			@RequestBody List<QueryContent> queryContents) {
		JSONArray returnObjects = new JSONArray();
		if (queryContents == null || queryContents.size() <= 0) {
			return new JsonResult(BaseConstant.CODE_PARAM_ERROR,
					BaseConstant.MSG_PARAM_ERROR);
		} else {

			long start = System.currentTimeMillis();

			for (QueryContent queryContent : queryContents) {
				List<String> monthBetween = new ArrayList<String>();
				JSONObject returnObject = new JSONObject();
				String phone = queryContent.getPhone();
				if (!StringUtils.isEmpty(phone)) {

					returnObject.put("phone", phone);

					try {
						returnObject.put(
								"count",
								singleCount(monthBetween,
										queryContent.getStartTime(),
										queryContent.getEndTime(),
										queryContent.getBusinessCode(), phone));
					} catch (Exception e) {
						LOG.error("报错信息：" + e.getMessage());
						return new JsonResult(BaseConstant.CODE_DATA_NONEXIST,
								BaseConstant.MSG_DATA_NONEXIST);
					}

					returnObjects.add(returnObject);
				}

			}
			LOG.info(" 批量写入的时间   " + (System.currentTimeMillis() - start));
		}
		return returnObjects;
	}

	/**
	 * Description:hbase单次查询数据
	 * 
	 * @param monthBetween
	 * @param phone
	 * @param businessCode
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	private long singleCount(List<String> monthBetween, String phone,
			String businessCode, String startTime, String endTime)
			throws TableNotFoundException, IOException, Exception {
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

			monthBetween.add(startTime.substring(0, 6));
			monthBetween.add(endTime.substring(0, 6));
		} else {
			monthBetween = DateVerificationUtil.getMonthBetween(startTime,
					endTime);
		}

		long count = 0l;
		count = getContactCount(phone, businessCode, startTime, endTime);
		LOG.info("查询接口的结果 phone [" + phone + "]" + " | businessCode - "
				+ businessCode + " - " + "开始时间:" + startTime + "| 结束时间: "
				+ endTime + "|查询结果: " + count);

		return count;
	}

	/**
	 * Description:查询contact表
	 * 
	 * @param phone
	 * @param businessCode
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public Long getContactCount(String phone, String businessCode,
			String startTime, String endTime) throws TableNotFoundException,
			IOException, Exception {

		long count = 0l;
		try {

			long startT = System.currentTimeMillis();
			if (!StringUtils.isEmpty(businessCode)) {

				byte[] startBC = HbaseOperateClient.buildRowkey(businessCode,
						HbaseConstant.MARKETING_DATA_TYPE, phone, startTime);
				byte[] endBC = HbaseOperateClient.buildRowkey(businessCode,
						HbaseConstant.MARKETING_DATA_TYPE, phone, endTime, "2");

				ResultScanner rsBC = HbaseOperateClient.getResultScanner(
						startBC, endBC, HbaseConstant.TABLE_QDXT_CONTACT_PHONE);

				for (Result result : rsBC) {
					System.out.println(result.isEmpty());
					count++;
				}
				LOG.info("BC 查询时间 time ["
						+ (System.currentTimeMillis() - startT) + "]");
			} else {
				byte[] start = HbaseOperateClient.buildRowkey(phone,
						HbaseConstant.MARKETING_DATA_TYPE, startTime);

				byte[] end = HbaseOperateClient.buildRowkey(phone,
						HbaseConstant.MARKETING_DATA_TYPE, endTime, "3");
				ResultScanner resultScanner = HbaseOperateClient
						.getResultScanner(start, end,
								HbaseConstant.TABLE_QDXT_CONTACT);
				for (Result result : resultScanner) {
					System.out.println(result.isEmpty());
					count++;
				}
			}
		} catch (Exception e) {
			LOG.error("error message", e.getMessage());
		}
		return count;
	}
	/**
	 * Description:查询营销类总数
	 * 
	 * @param monthBetween
	 * @param phone
	 * @param prefixTableName
	 * @return
	 * @throws TableNotFoundException
	 * @throws IOException
	 *             Long
	 * @see
	 * 
	 *      public Long getPromoteCount(List<String> monthBetween, String phone,
	 *      String prefixTableName) throws TableNotFoundException, IOException,
	 *      Exception {
	 * 
	 *      Long count = 0l;
	 * 
	 *      for (String monthStr : monthBetween) { String tableName =
	 *      prefixTableName + monthStr;
	 * 
	 *      byte[] start = Bytes.add( Bytes.toBytes((short) (phone.hashCode() &
	 *      0x7fff)), Bytes.toBytes(phone), Bytes.toBytes(monthStr + "01"));
	 *      byte[] end = Bytes.add( Bytes.toBytes((short) (phone.hashCode() &
	 *      0x7fff)), Bytes.toBytes(phone), Bytes.toBytes(monthStr + "31"));
	 *      ResultScanner resultScanner = HbaseOperateClient.getResultScanner(
	 *      start, end, tableName); try { for (Result result : resultScanner) {
	 *      count++; } } catch (Exception e) { LOG.error("报错信息" +
	 *      e.getMessage()); continue; } } return count; }
	 */

}