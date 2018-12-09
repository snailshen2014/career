package com.bonc.common.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.bonc.utils.PropertiesUtil;

public class FileNameUtils {

	   	private static Date preDate = null;
	    private static int indexNum = 0;
	    
	 /**
     * 生成下发文件名称，CD201507291812001CLPS0101CA201505
     * @param dateType M：月报，D：日报，H：小时，T：其它
     * @param chudianType “CLPS”：存量经营-省分触点管理 “CLGS”：存量经营-集团触点管理
     * @param interfaceNum (接口类型  基本信息：01办理信息：02接触信息：03其它应用：04接口序号)+(接口在“接口类型”中序号，如01)
     * @param operateType F：覆盖（默认），C：新增，D：删除
     * @param fileFlag 第一次上传的文件为“A”，重传文件为“B” 
     * @param fileTimeFlag 日报：YYMMDD，月报：YYYYMM，小时：HHMISS
     * @param activityId 活动id 如果为用户群则传入0
     * @param numLength 中间填充位数
     * @return
     */
    public static String createFileName(String dateType, String chudianType, String interfaceNum, String operateType,
    		String fileFlag, String fileTimeFlag, String activityId, int numLength){
    	Date now = new Date();
    	SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
    	synchronized(FileNameUtils.class){
    		if(preDate == null){
    			indexNum = 0;
    		}else if(preDate.getDay() < now.getDay()){
    			indexNum = 0;
    		}else{
    			indexNum++;
    		}
    		preDate = now;
        } 
    	
    	//正式用的是'C' 测试151用的是'T'
    	String firstChar = PropertiesUtil.getWebService("datasending.filename.first");
    	  	
    	StringBuilder sb = new StringBuilder(firstChar);
    	sb.append(dateType);
    	sb.append(chudianType);
    	sb.append(activityId == null ? "" : addZeroForNum('0', activityId, numLength, true));
    	sb.append(interfaceNum);
    	sb.append(operateType);
    	sb.append(fileFlag);
    	if(null != fileTimeFlag && !"".equals(fileTimeFlag)){//周期性活动
    		sb.append(fileTimeFlag);
    	}else{
    		sb.append(sf.format(now));
        	sb.append(String.format("%03d", indexNum));
    	}
    	return sb.toString();
    }
    
    /**
     * 格式化字符串，不够补位
     * @param c 要填充的字符    
     * @param str 要格式化的字符串   
     * @param strLength 填充后字符串的总长度    
     * @param isLeft  是否是左对齐，true为左对齐，false为右对齐
     * @return
     */
    public static String addZeroForNum(char c, String str, int strLength, boolean isLeft) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
              sb = new StringBuffer();
              if(isLeft){
            	  sb.append(c).append(str);// 左(前)补0
              }else{
            	  sb.append(str).append(c);//右(后)补0
              }
              str = sb.toString();
              strLen = str.length();
        }
        return str;
    }
    
    public static void main(String[] args) {
    	String r = addZeroForNum('0', "5642", 8, true);//flushLeft('0', 8L, "5642");
    	System.out.println(r);
    	
    	for(int i=0;i<4;i++){
	    	String sendFileName = FileNameUtils.createFileName("H", "CLPS", "012101", "D", "B", "2016-11-22", null, 0);
	    	System.out.println("-----------"+sendFileName);
    	}
	}
}