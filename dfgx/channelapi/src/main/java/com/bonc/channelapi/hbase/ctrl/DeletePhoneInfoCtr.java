package com.bonc.channelapi.hbase.ctrl;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.BaseConstant;
import com.bonc.channelapi.hbase.constant.HbaseConstant;
import com.bonc.channelapi.hbase.entity.JsonResult;

@RestController
@RequestMapping("/v1")
public class DeletePhoneInfoCtr {

	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(DeletePhoneInfoCtr.class);

	/**
	 * Description: 删除该手机号下的所有信息
	 * 
	 * @param phone
	 * @return Object
	 * @see
	 */
	@RequestMapping(value = "/deletePhoneInfo", method = RequestMethod.GET)
	private Object delete(String phone) {

		byte[] start = HbaseOperateClient.buildRowkey(phone,
				HbaseConstant.MARKETING_DATA_TYPE);
		byte[] end = HbaseOperateClient.buildRowkey(phone,
				HbaseConstant.MARKETING_DATA_TYPE, "z");
		try {

			ResultScanner resultScanner = HbaseOperateClient.getResultScanner(
					start, end, HbaseConstant.TABLE_QDXT_CONTACT);

			for (Result r : resultScanner) {
				byte[] value = r.getValue(Bytes.toBytes("f"),
						Bytes.toBytes("pushTime"));
				byte[] rowkey = Bytes.add(
						Bytes.toBytes((short) phone.hashCode() & 0x7fff),
						Bytes.toBytes(phone),
						Bytes.toBytes(HbaseConstant.MARKETING_DATA_TYPE));
				rowkey = Bytes.add(rowkey, value);
				HbaseOperateClient.delete(HbaseConstant.TABLE_QDXT_CONTACT,
						rowkey);
			}
			HbaseOperateClient.delete(HbaseConstant.TABLE_QDXT_CONTACT, end);
		} catch (IOException e) {
			LOG.info("delete error  ", e);
			return new JsonResult("4004", "删除未成功");
		} catch (Exception e) {
			LOG.error("错误信息" + e.getMessage());
			return new JsonResult(BaseConstant.CODE_ERROR,
					BaseConstant.MSG_ERROR);
		}

		return new JsonResult("200", "删除成功");
	}
}
