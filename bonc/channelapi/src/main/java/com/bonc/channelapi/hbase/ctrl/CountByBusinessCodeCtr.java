package com.bonc.channelapi.hbase.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.channelapi.hbase.client.HBaseClient;
import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.HbaseConstant;
import com.bonc.channelapi.hbase.util.DateVerificationUtil;

@RestController
@RequestMapping("/v1")
public class CountByBusinessCodeCtr {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(CountByBusinessCodeCtr.class);

	/**
	 * Description:根据业务编码和时间段返回总数
	 * 
	 * @param request
	 * @param startTime
	 * @param endTime
	 * @param businessCode
	 * @throws TableNotFoundException
	 * @throws IOException
	 * @throws Exception
	 *             void
	 * @see
	 */
	@RequestMapping(value = "/countByBusinessCode", method = RequestMethod.GET)
	public Object scanByBussinesCode(HttpServletRequest request,
			@RequestParam(required = false) boolean deduplication,
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = true) String businessCode) {
		String time = null;
		if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {

			time = DateVerificationUtil.defaultTime();
			startTime = time + "000000";
			endTime = time + "235959";
		}

		byte[] start = HbaseOperateClient.buildRowkey(businessCode,
				HbaseConstant.MARKETING_DATA_TYPE, startTime);
		byte[] end = HbaseOperateClient.buildRowkey(businessCode,
				HbaseConstant.MARKETING_DATA_TYPE, endTime, "z");

		Scan scan = new Scan(start, end);
		scan.setSmall(true);

		int count = 0;

		List<String> phones = new ArrayList<String>();
		try {
			HTableInterface table = HBaseClient.getHConnection().getTable(
					HbaseConstant.TABLE_QDXT_CONTACT_BUSINESSCODE);
			ResultScanner scanner = table.getScanner(scan);
			for (Result result : scanner) {

				if (deduplication) {
					String phone = Bytes.toString(result.getValue(
							Bytes.toBytes("f"), Bytes.toBytes("phone")));
					if (!phones.contains(phone)) {
						phones.add(phone);
						count++;
					}
				} else {

					count++;
				}
			}
			scanner.close();
			table.close();
		} catch (IOException e) {
			LOG.error("Api16Controller.scanByBussinesCode", e);
		} catch (Exception e) {
			LOG.error("Api16Controller.scanByBussinesCode", e);
		}
		JSONObject returnObject = new JSONObject();
		returnObject.put("count", count);
		String result = JSON.toJSONString(returnObject);
		return result;
	}
}
