package com.bonc.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: TimeUtil
 * @Description: TimeUtil
 * @author: LiJinfeng
 * @date: 2016年12月9日 下午3:32:45
 */

public class TimeUtil {
	
	/**
	 * @Title: formatTime
	 * @Description: 将当前时间转化为指定的格式
	 * @return: String
	 * @param format
	 * @return
	 * @throws: 
	 */
	public static String formatSystemTime(String format){
		
		if(StringUtils.isBlank(format)){
			return null;
		}
		Date time=new Date();
		DateFormat date=new SimpleDateFormat(format);
		String result = date.format(time);
		return result;
		
	}
	
    /**
     * @Title: string2String
     * @Description: 将指定格式的时间字符串转化为另一种格式的时间字符串
     * @return: String
     * @param beforeFormat
     * @param date
     * @param afterFormat
     * @return
     * @throws: 
     */
    public static String string2String(String beforeFormat,String date,String afterFormat){
	    
	   if(StringUtils.isBlank(date) || StringUtils.isBlank(beforeFormat) || StringUtils.isBlank(afterFormat)){
			return null;
	   }
	   Date string2Date = TimeUtil.String2Date(date,beforeFormat);
	   String date2String = TimeUtil.date2String(string2Date, afterFormat);
	   return date2String;
	   
	}
	
    /**
     * @Title: String2Date
     * @Description: 将指定格式的时间字符串转化为Date对象
     * @return: Date
     * @param date
     * @param format
     * @return
     * @throws: 
     */
    public static Date String2Date(String date,String format){
    	
    	if(StringUtils.isBlank(date) || StringUtils.isBlank(format)){
    		return null;
    	}
    	DateFormat dateFormat = new SimpleDateFormat(format);
    	Date result = null;
		try {
			result = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
		
	}
    
    /**
     * @Title: date2String
     * @Description: 将Date对象转化为指定格式的时间字符串
     * @return: String
     * @param date
     * @param format
     * @return
     * @throws: 
     */
    public static String date2String(Date date,String format){
		
    	if(date == null || StringUtils.isBlank(format)){
    		return null;
    	}
		DateFormat dateFormat=new SimpleDateFormat(format);
		String result = dateFormat.format(date);
		return result;
		
	}
    
    /**
     * @Title: getStartTimeOfYesterday
     * @Description: 得到昨天开始时间
     * @return: String
     * @param format
     * @return
     * @throws: 
     */
    public static String getStartTimeOfYesterday(String format){
    	
    	if(StringUtils.isBlank(format)){
			return null;
		}
    	Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date=calendar.getTime();
		return new SimpleDateFormat(format).format(date);
    	
    }
    
    /**
     * @Title: getStartTimeOfTaday
     * @Description: 得到今天开始时间
     * @return: String
     * @param format
     * @return
     * @throws: 
     */
    public static String getStartTimeOfTaday(String format){
    	
    	if(StringUtils.isBlank(format)){
			return null;
		}
      	Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date=calendar.getTime();
		return new SimpleDateFormat(format).format(date);
     	
    }
    
    /**
     * @Title: getStartTimeOfYesterday
     * @Description: 得到昨天开始时间
     * @return: String
     * @param format
     * @return
     * @throws: 
     */
    public static Date getStartTimeOfYesterday(){
    	
    	Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
    	
    }
    
    /**
     * @Title: getStartTimeOfTaday
     * @Description: 得到今天开始时间
     * @return: String
     * @param format
     * @return
     * @throws: 
     */
    public static Date getStartTimeOfTaday(){
    	
      	Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
     	
    }
   
    /**
     * @Title: main
     * @Description: 测试类
     * @return: void
     * @param args
     * @throws: 
     */
    public static void main(String[] args) {
		/*System.out.println(TimeUtil.getStartTimeOfYesterday("yyyyMMddHHmmss"));
		System.out.println(TimeUtil.getStartTimeOfTaday(""));*/
    	String date = "20161201";
    	String string2String = TimeUtil.string2String("yyyyMMdd", "20170105", "yyyy年MM月dd日");
    	System.out.println(string2String);
    	
	}

}
