package com.bonc.utils;

import java.text.DateFormat;
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

}
