package com.bonc.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	
	/**
	 * 将当前时间转化为指定的格式
	 */
	public static String formatTime(String format){
		
		Date time=new Date();
		DateFormat date=new SimpleDateFormat(format);
		String result = date.format(time);
		return result;
		
	}
	
	public static String getCurrentTime(String formater) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(formater);// 可以方便地修改日期格式
		return dateFormat.format(now);
	}
    
    public static Timestamp getDateTime(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new java.sql.Timestamp(parsed.getTime());
	}

}
