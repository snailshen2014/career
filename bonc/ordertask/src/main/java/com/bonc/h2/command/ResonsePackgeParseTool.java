package com.bonc.h2.command;

import java.util.Date;
import java.util.List;

import com.bonc.h2.pojo.ResponseResult;
import com.bonc.h2.pojo.UserAccountInfoBean;
import com.bonc.h2.pojo.UserFlowInfoBean;
import com.bonc.h2.pojo.UserStatusInfoBean;

/**
 * 应答包的解析； 包含密码验证、 积分查询（两种方式）、 呼转设置 通过h2接口协议实现的外围接口的包解析。
 * 
 * @author quechao createDate 2009-11-04
 * 
 */
public class ResonsePackgeParseTool {

	public static final char PACKAGE_END_FLAG = 0x1a;// (char)0x1a为结束标志
	public static final String TABLE = "" + (char) 0x09;// 数据字段间分隔符
	public static final String ENTER = "" + (char) 0x0d + (char) 0x0a;// 数据字段间分隔符
	public static final String USER_ACCONT = "GREALFEE";//实时话费，实时余额
	public static final String USER_STATUS = "GUSERSTATE";//用户状态
	public static final String USER_FLOW = "GFLUX";
	

	public static ResponseResult Analyse(List<byte[]> data) throws Exception {

		if (!data.isEmpty()) {
			ResponseResult result = new ResponseResult();
			result.setCreateTime(new Date());
			byte[] Datapackage = data.get(0);
			ResponseHead responseHead = new ResponseHead(Datapackage);
			responseHead.AnalyseHead();
			result.setDeciceNumber(responseHead.getDeviceNumber().trim());
			result.setSequenceNum(responseHead.getNumber().trim());
			result.setOperateNumber(responseHead.getOperateNumber().trim());
			result.setPlace(responseHead.getPlace().trim());
			result.setFlag(responseHead.getFlag().trim());
			
			if(USER_ACCONT.equals(responseHead.getServiceType().trim())){
				result.setServiceType(USER_ACCONT);
				result.setErrorCode(responseHead.getErrorCode().trim());
				result.setData(AnalyseUserAccountInfoResponse(data));
			}else if(USER_STATUS.equals(responseHead.getServiceType().trim())){
				result.setServiceType(USER_STATUS);
				result.setErrorCode(responseHead.getErrorCode().trim());
				result.setData(AnalyseUserStatusInfoResponse(data));
			}else if(USER_FLOW.equals(responseHead.getServiceType().trim())){
				result.setServiceType(USER_FLOW);
				result.setErrorCode(responseHead.getErrorCode().trim());
				result.setData(AnalyseUserFlowInfoResponse(data,responseHead.getDeviceNumberType().trim()));
			}else {
				result.setErrorCode(responseHead.getErrorCode());
			}
			return result;
		}
		return null;
	}

	
	private static Object AnalyseUserAccountInfoResponse(List<byte[]> data) throws Exception{
		UserAccountInfoBean userInfo=new UserAccountInfoBean();
		byte[] Datapackage = data.get(0);
		try {
			String dataBody=new String(Datapackage, 102,
					Datapackage.length - 1 - 102, "GBK");
			String[] splitdata = dataBody.split(ResonsePackgeParseTool.TABLE);
			/*for(int i=0;i<splitdata.length;i++){
				System.out.println("返回值"+splitdata[i]);
			}*/
			if(splitdata.length>1){
				userInfo.setDate(splitdata[0].trim());
				userInfo.setFee(splitdata[1].trim());
				userInfo.setBalance(splitdata[4].trim());
			}
			//userInfo.setCreditNums(splitdata[2].trim());
			return userInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Object AnalyseUserStatusInfoResponse(List<byte[]> data) throws Exception{
		UserStatusInfoBean userStatusInfo = new UserStatusInfoBean();
		byte[] Datapackage = data.get(0);
		try {
			String dataBody=new String(Datapackage, 102,
					Datapackage.length - 1 - 102, "GBK");
			System.out.println("---"+dataBody);
			String[] splitdata = dataBody.split(ResonsePackgeParseTool.TABLE);
			/*for(int i=0;i<splitdata.length;i++){
				System.out.println("返回值"+splitdata[i]);
			}*/
			userStatusInfo.setServiceType(splitdata[0].trim());
			//userInfo.setCreditNums(splitdata[2].trim());
			return userStatusInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Object AnalyseUserFlowInfoResponse(List<byte[]> data,String deviceNumberType) throws Exception{
		UserFlowInfoBean userFlowInfo = new UserFlowInfoBean();
		byte[] Datapackage = data.get(0);
		try {
			String dataBody=new String(Datapackage, 102,
					Datapackage.length - 1 - 102, "GBK");
			System.out.println("---"+dataBody);
			String[] splitdata = dataBody.split(ResonsePackgeParseTool.TABLE);
			/*
			for(int i=0;i<splitdata.length;i++){
				System.out.println("返回值>"+i+":"+splitdata[i]);
			}
			*/
			System.out.println("查询流量类型》》》》》"+deviceNumberType);
			if(splitdata.length>1){
				userFlowInfo.setPackage_last_flow(Integer.valueOf(splitdata[5]));
				if("T".equals(deviceNumberType)){
					userFlowInfo.setBefore_last_flow(Integer.valueOf(splitdata[9]));
				}
			}
			return userFlowInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}