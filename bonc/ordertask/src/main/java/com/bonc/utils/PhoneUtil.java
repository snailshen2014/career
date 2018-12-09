package com.bonc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PhoneUtil {
	
	/**
	 * 路由尾号规则
	 * @param num 手机号
	 * @return 尾号 （非数字返回0）
	 */
	public static String routePhoneNumTail(String num){
		String tailNum;
		if (num==null || num.trim().equals("")){
			return "ERROR 参数不合法";
		}
		tailNum = num.substring(num.length()-1);
		tailNum = Character.isDigit(tailNum.charAt(0))?tailNum:"0";
		return tailNum;
	}
	
	
	/**
	 * 路由月份
	 * @return 当前月份
	 */
	public static String routeMonth(){
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		String currentMonth = date.format(new Date()).substring(5, 7); //获取当前月份
		return currentMonth;
	}

	/**
	 * 路由月份和手机尾号
	 *  * @param num 手机号
	 * @return 月份+尾号 （非数字返回0）
	 */
	public static String routeMonthAndPhoneNumTail(String num){
	   String tailNum = routePhoneNumTail(num);
	   String curMonth = routeMonth();
	   return curMonth+tailNum;
	}
	
	public static void main(String[] args) {
		String month = routeMonth();
		
		System.out.println(month);
	}
}
