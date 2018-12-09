package com.bonc.busi.orderschedule.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeFun {
	/*
	 * get last day time of the month from special day
	 * 
	 * @param special day Date
	 * 
	 * @return last day time
	 */
	public static String getLastDayOfMonth(Date oneday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar ca = Calendar.getInstance();
		ca.setTime(oneday);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return format.format(ca.getTime());

	}

	public static String getSysDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	public static String getCurrentTime(String formater) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(formater);// 可以方便地修改日期格式
		return dateFormat.format(now);
	}
}
