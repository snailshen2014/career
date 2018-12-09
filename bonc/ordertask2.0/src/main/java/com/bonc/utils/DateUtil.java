package com.bonc.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @作者 高阳
 * @时间 2016-9-14
 * @描述 与时间相关的操作，当前时间，格式转换，获取特定时间点等
 */
public class DateUtil {
	private static final Logger logger = Logger.getLogger(DateUtil.class);
	
	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static String STATISTIC_DATE = null;

	/**
	 * @描述 定义各种常用的时间格式
	 */
	public static class DateFomart{
		public static String CN_DATETIME="yyyy年MM月dd日 HH时:mm分:ss秒";
		public static String CN_DATE="yyyy年MM月dd日";
		public static String CN_TIME="HH时:mm分:ss秒";
		
		public static String EN_DATETIME="yyyy-MM-dd HH:mm:ss";
		public static String EN_DATE="yyyy-MM-dd";
		public static String EN_TIME="HH:mm:ss";
		
		public static String DATETIME="yyyyMMddHHmmss";
		public static String DATETIMEHM="yyyyMMddHHmmssSSS";
		public static String DATE="yyyyMMdd";
		public static String MONTH="yyyyMM";
		
		public static String EN_MINUTE="yyyyMMddHHmm";
	}
	
	/**
	 * @描述 获取与当前时间相关的时间信息
	 */
	public static class CurrentDate{
		/**
		 * @param dateFomart DateFomart中的时间格式
		 * @return 返回 yyyyMMdd 格式的时间
		 */
		public static String currentDateFomart(String fomart) {
			return new SimpleDateFormat(fomart).format(new Date());
		}
		
		/**
		 * @return 返回当前毫秒数
		 */
		public static long getNowDateTime() {
			return (new Date()).getTime();
		}
	}

	/**
	 * @描述 获取特定日期的时间信息
	 */
	public static class SpecialDate{
		/**
		 * 获取某年第一天日期
		 * @param year 年份
		 * @return Date
		 */
		public static String getCurrYearFirst(int year) {
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(Calendar.YEAR, year);
			Date currYearFirst = calendar.getTime();
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currYearFirst);
		}

		/**
		 * 获取某年最后一天日期
		 * 
		 * @param year 年份
		 * @return Date
		 */
		public static String getCurrYearLast(int year) {
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(Calendar.YEAR, year);
			calendar.roll(Calendar.DAY_OF_YEAR, -1);
			Date currYearLast = calendar.getTime();
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currYearLast);
		}
		/**
		 * 获取某年某月第一天日期
		 * 
		 * @param year
		 *            年
		 * @param month
		 *            月份
		 * @param format
		 *            显示格式
		 * @return
		 * @author liangmeng
		 */
		public static String getCurrMonthFirst(int year, int month, String format) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
			return new SimpleDateFormat(format).format(cal.getTime());
		}

		/**
		 * 获取某年某月最后一天日期
		 * 
		 * @param year
		 *            年
		 * @param month
		 *            月份
		 * @param format
		 *            显示格式
		 * @return
		 * @author liangmeng
		 */
		public static String getCurrMonthLast(int year, int month, String format) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, value);
			return new SimpleDateFormat(format).format(cal.getTime());
		}
	}
	
	public static class FomartDate{
		/**
		 * 转换为固定格式字符串
		 * 
		 * @param date
		 *            时间
		 * @param format
		 *            格式
		 * @return
		 * @author moubd
		 */
		public static String toFormat(Timestamp date, String format) {
	        if(format==null || null==date){
	        	return "";
	        }else{
	        	return new SimpleDateFormat(format).format(date);
	        }
		}

		/**
		 * 转换为指定格式字符串
		 * @return 
		 */
		public static String toFormat(Date date, String format) {
	        if(format==null || null==date){
	        	return "";
	        }else{
	        	return new SimpleDateFormat(format).format(date);
	        }
		}
		
		/**
		 *如果传入的时间dates不满足from 的时间格式，则不对其进行转换，原值返回
		 * @param dates 时间字符串
		 * @param from 转换前的时间格式
		 * @param to 转换后的时间格式
		 * @return  转换后的时间字符串
		 * @throws ParseException 
		 */
		public static String toFormat(String dates, String from, String to) throws ParseException {
			DateFormat format1 = new SimpleDateFormat(from);
			DateFormat format2 = new SimpleDateFormat(to);
			Date date = format1.parse(dates);
			return  format2.format(date);
		}
		
//		public static Long toFormat(String dates, String format) {
//			if(null==dates||"".equals(dates)){
//				return null;
//			}
//			DateFormat dateFormat = new SimpleDateFormat(format);
//			Long date;
//			try {
//				date = (Long) dateFormat.parse(dates);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return date;
//		}
	}
	
	/**
	 * 得到本日的前几个月时间 如果number=2当日为2007-9-1 00:00:00,那么获得2007-7-1 00:00:00
	 * 
	 * @param number
	 * @return
	 */
	public static String getDateBeforeMonth(int number) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -number);
		return new SimpleDateFormat(DateUtil.DateFomart.EN_DATETIME).format(cal.getTime());
	}
	
	public static String getDateBeforeSecond(String fomart,String time,int number) {
		Calendar cal = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat(null==fomart?DateUtil.DateFomart.EN_DATETIME:fomart);
		try {
			cal.setTime(df.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.SECOND, -number);
		return new SimpleDateFormat(null==fomart?DateUtil.DateFomart.EN_DATETIME:fomart).format(cal.getTime());
	}

	/**
	 * 获取当前月向前X个月的list
	 * 
	 * @param count 向前推的月份数量
	 * @return
	 * @author liangmeng
	 */
	public static List<Map<String, Object>> getPerMonth(int count) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			Map<String, Object> map = new HashMap<String, Object>();
			int year = cal.get(Calendar.YEAR);
			int month = (cal.get(Calendar.MONTH) + 1);
			map.put("formatStr", year + "年" + month + "月");
			map.put("year", year);
			map.put("month", month);
			map.put("yyyyMM", String.valueOf(year) + (month < 10 ? "0" + month : month));
			list.add(map);
		}
		return list;
	}

	
	/**
	 * 获取从上个月开始  长度为count的 list
	 * 
	 * @param count
	 *            向前推的月份数量
	 * @return
	 * @author yangzh
	 */
	public static List<Map<String, Object>> getPerMonth1(int count) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < count; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			Map<String, Object> map = new HashMap<String, Object>();
			int year = cal.get(Calendar.YEAR);
			int month = (cal.get(Calendar.MONTH));
			map.put("formatStr", year + "年" + month + "月");
			map.put("year", year);
			map.put("month", month);
			map.put("yyyyMM", String.valueOf(year) + (month < 10 ? "0" + month : month));
			list.add(map);
		}
		return list;
	}
	
	/**
     * 获取当前月向前X个月的list
     * @param count 向前推的月份数量
     * @return 
     * @author liangmeng
     */
    public static List<Map<String,Object>> getMonthFromPerMonth(int count){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for(int i=1;i<count+1;i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -i);
            Map<String,Object> map = new HashMap<String, Object>();
            int year = cal.get(Calendar.YEAR);
            int month = (cal.get(Calendar.MONTH)+1);
            map.put("formatStr",  year+ "年" + month+"月");
            map.put("year", year);
            map.put("month", month);
            map.put("yyyyMM", String.valueOf(year) + (month<10? "0"+month : month));
            list.add(map);
        }
        return list;
    }
    
    /**
     * 获取当前月向前X个月的list(1日算做上个月)
     * @param count 向前推的月份数量
     * @return 
     * @author liangmeng
     */
    public static List<Map<String,Object>> getMonthFromDay2(int count){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for(int i=1;i<count+1;i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -i);
            Map<String,Object> map = new HashMap<String, Object>();
            int year = cal.get(Calendar.YEAR);
            int day = (cal.get(Calendar.DAY_OF_MONTH));
            //System.out.println("day=========>"+day);
            int month = (cal.get(Calendar.MONTH)+1);
            //System.out.println("month-before:"+month);
            /*如果当前是1日，则算做上个月*/
            if(day==1){
                cal.add(Calendar.MONTH, -1);
            }
            month = (cal.get(Calendar.MONTH)+1);
            //System.out.println("month-after:"+month);
            map.put("formatStr",  year+ "年" + month+"月");
            map.put("year", year);
            map.put("month", month);
            map.put("yyyyMM", String.valueOf(year) + (month<10? "0"+month : month));
            list.add(map);
        }
        return list;
    }

	/**
	 * 传入日期的下月下月的第一天
	 */
	@SuppressWarnings("deprecation")
	public static String nextMonthFirtDay(Date date) {
//		Calendar calendar2Calendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1900+date.getYear());
		calendar.set(Calendar.MONTH, date.getMonth()+1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return simpleDateFormat.format(calendar.getTime());
	}
	/**
	 * 传入日期的最后一天
	 */
	@SuppressWarnings("deprecation")
	public static String thisMonthLastDay(Date date) {
//		Calendar calendar2Calendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1900+date.getYear());
		calendar.set(Calendar.MONTH, date.getMonth()+1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		return simpleDateFormat.format(calendar.getTime());
	}
	/**
	 * 传入日期是否为当前月的最后一天
	 */
	public static boolean isThisMonthLastDay(Date date) {
		return simpleDateFormat.format(date.getTime()).equals(thisMonthLastDay(new Date()));
	}
	/**
	 * 传入日期是否为下月的第一天
	 */
	public static boolean isNextMonthFirstDay(Date date) {
		return simpleDateFormat.format(date.getTime()).equals(nextMonthFirtDay(new Date()));
	}
	/**
     * 获取近期几个月的list集合
     * @param number  要获取的月个数   formart 指定格式 默认:"yyyy年M月"
     * @return
     * @author 李明顶
     *
     * Date: 2014年4月2日 <br>
     */
    public static List<String> getLatelyMonthForNum(final int number,final String ...formart ){
        String formatTem = "yyyy年MM月";
        if(formart!=null&&formart.length==1){
            formatTem = formart[0];
        }
        List<String>  list = new ArrayList<String>();
        int currenMouth = Calendar.getInstance().get(Calendar.MONTH);//当前月-1
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat(formatTem);
        for(int i=0;i<number;i++){ 
            Calendar c=Calendar.getInstance();
            c.add(Calendar.MONTH, -1);//今天的时间月份-1支持1月的上月
            c.set(Calendar.MONTH,currenMouth-i);
            list.add(simpleDateFormat.format(c.getTime()).toString());
        }
        return list;
    }
    /**
     * 根据查询月获取查询当月的起止日期      可以指定月参数格式    和    起止日期格式
     * @param date   月参数格式默认:yyyy年M月     formart:起止日期格式默认:yyyy年M月dd日
     * @return 查询当月的起止日期字符串
     * @author 李明顶
     *
     * Date: 2014年4月3日 <br>
     */
    @SuppressWarnings("deprecation")
    public static String getStartAndEndDate(final String date,final String ... formart){
        String dateFormat = "yyyy年MM月";//默认格式
        String startAndEndDateFormart = "yyyy年MM月dd日";//默认起止日期格式
        if(formart!=null&&formart.length==1){
            dateFormat = formart[0];
        }
        if(formart!=null&&formart.length==2){
            startAndEndDateFormart = formart[1];
        }
        
        //是否是当月
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String nowDate = simpleDateFormat.format(new Date()).toString();
        if(nowDate.equals(date)){
           String startDate = nowDate+"01日";
           String endDate = new SimpleDateFormat(startAndEndDateFormart).format(new Date()).toString();//默认当前月
            return startDate+"至"+endDate;       
        } 
        //起始日期
        String startDate = date+"01日";  
        //截止日期 
        Date endDateD = null;
       try {
           endDateD = simpleDateFormat.parse(date);
       } catch (ParseException e) {
       } 
		int m = endDateD.getMonth();  
        int y = endDateD.getYear();  
        Date firstDay = new Date(y,m+1,1) ;  
        int min = 24*60*60*1000;    
        Date end = new Date(firstDay.getTime()-min);
        String endDate = new SimpleDateFormat(startAndEndDateFormart).format(end);
        return startDate+"至"+endDate;
    }
 
    /**
     * 获取当月起向前几个月list，格式yyyyMM
     * @param count
     * @return
     */
    public static List<String> getYearMonth(int count) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -i);
            int year = cal.get(Calendar.YEAR);
            int month = (cal.get(Calendar.MONTH) + 1);
            list.add(String.valueOf(year) + (month < 10 ? "0" + month : month));
        }
        return list;
    }
    
    public static String getLong(String fomart,String start,String end){
    	try {
    		DateFormat df = new SimpleDateFormat(null==fomart?DateUtil.DateFomart.EN_DATETIME:fomart);
    		Long startLong = df.parse(start).getTime()/1000;
    		Long endLong = df.parse(end).getTime()/1000;
			return (endLong-startLong)+"秒";
		} catch (ParseException e) {
			logger.error("计算时长失败，日期格式错误！fomart="+fomart+"&&start="+start+"&&end="+end);
			return null;
		}catch (Exception e){
			logger.error("计算时长失败！fomart="+fomart+"&&start="+start+"&&end="+end);
			return null;
		}
    }
    
    public static String secToTime(Integer time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
    
    public static String getStatisticDate(){
		Calendar ncalendar = Calendar.getInstance();
		ncalendar.set(Calendar.HOUR_OF_DAY, 6);
		ncalendar.set(Calendar.MINUTE, 30);
		ncalendar.set(Calendar.SECOND, 0);
		String STATISTIC_DATE = new SimpleDateFormat(DateFomart.EN_DATETIME).format(ncalendar.getTimeInMillis());
		return STATISTIC_DATE;
    };
    
    public static String getLastStatisticDate(){
		Calendar ncalendar = Calendar.getInstance();
		ncalendar.set(Calendar.DAY_OF_MONTH, ncalendar.get(Calendar.DAY_OF_MONTH));
		if(new Date().getTime()<ncalendar.getTimeInMillis()){
			ncalendar.set(Calendar.DAY_OF_MONTH, ncalendar.get(Calendar.DAY_OF_MONTH)-1);
		}
		ncalendar.set(Calendar.HOUR_OF_DAY, 6);
		ncalendar.set(Calendar.MINUTE, 30);
		ncalendar.set(Calendar.SECOND, 0);
		STATISTIC_DATE = new SimpleDateFormat(DateFomart.EN_DATETIME).format(ncalendar.getTimeInMillis());
    	return STATISTIC_DATE;
    };
    
    public static String getCurMonth(){
    	Calendar ncalendar = Calendar.getInstance();
//    	ncalendar.set(Calendar.MONTH, 8);
		int month = ncalendar.get(Calendar.MONTH)+1;
		return month<10?"0"+month:""+month;
    }
    
    public static List<String> getMonthList(String start,String format) throws ParseException{
    	List<String> monthList = new ArrayList<String>();
    	SimpleDateFormat monthf=new SimpleDateFormat(DateFomart.MONTH);
    	int count = countMonths(start,CurrentDate.currentDateFomart(format),format);
    	monthList.add(start);
    	Calendar calendar=Calendar.getInstance();
    	calendar.setTime(monthf.parse(start.substring(0,6)));
    	while (count-->0) {
    		calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)+1);
    		monthList.add(FomartDate.toFormat(calendar.getTime(), DateFomart.MONTH));
		}
    	Collections.reverse(monthList);
    	return monthList;
    }
    
    public static List<String> getMonthList(int times) throws ParseException{
    	List<String> monthList = new ArrayList<String>();
    	Calendar calendar=Calendar.getInstance();
//    	calendar.set(Calendar.MONTH, Calendar.MONTH);
    	while (times-->0) {
    		monthList.add(FomartDate.toFormat(calendar.getTime(), DateFomart.MONTH));
    		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
		}
    	return monthList;
    }
    
    public static int countMonths(String start,String end,String format) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Calendar cStart=Calendar.getInstance();
        Calendar cEnd=Calendar.getInstance();
        
        cStart.setTime(sdf.parse(start));
        cEnd.setTime(sdf.parse(end));
        
        int year =cEnd.get(Calendar.YEAR)-cStart.get(Calendar.YEAR);
        
        //开始日期若小月结束日期
        if(year<0){
            year=-year;
            return year*12+cStart.get(Calendar.MONTH)-cEnd.get(Calendar.MONTH);
        }
        return year*12+cEnd.get(Calendar.MONTH)-cStart.get(Calendar.MONTH);
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
    
    public static Timestamp getDate(String datetime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new java.sql.Timestamp(parsed.getTime());

	}
    
    public static void main(String[] args) throws ParseException {
//		System.out.println(getLong(DateFomart.EN_DATETIME,"2015-06-08 12:11:15","2015-06-08 12:41:55"));
//		System.out.println(getStatisticDate());
//		System.out.println(getLastStatisticDate());
//    	System.out.println(getCurMonth());
//    	List<String> months = getMonthList(5);
//    	Collections.reverse(months);
//    	for(String month:months){
//    		System.out.println(month);
//    	}
    	System.out.println(getLastStatisticDate());
	}
}
