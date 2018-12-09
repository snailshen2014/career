package com.bonc.channelapi.hbase.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 校验日期时间工具类
 * 
 * @author caiqiang
 * @version 2016年8月3日
 * @see DateVerificationUtil
 * @since
 */
public class DateVerificationUtil {
    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(DateVerificationUtil.class);

    /**
     * Description:验证小时输入的格式
     * 
     * @param hour
     * @return boolean
     * @see
     */
    public static boolean verificationHour(String hour) {

        Integer hourI = Integer.parseInt(hour);
        if (hourI >= 0 && hourI <= 24) {
            return true;
        }

        return false;
    }

    /**
     * Description:验证日期输入
     * 
     * @param startDateStr
     * @param endDateStr
     * @param startHour
     * @param endHour
     * @return boolean
     * @see
     */
    public static boolean verificationDate(String startDateStr, String endDateStr,
                                           String startHour, String endHour) {

        if (!correctDate(startDateStr) && !correctDate(endDateStr)) {
            LOG.error("please input correct date");
            return false;
        }

        if (verificationHour(startHour) && verificationHour(endHour)) {
            return true;
        }
        return false;
    }

    /**
     * Description:验证日期输入的格式
     * 
     * @param date
     * @return boolean
     * @see
     */
    public static boolean correctDate(String date) {

        Pattern p = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\-\\s]?((((0?"
                                    + "[13578])|(1[02]))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))"
                                    + "|(((0?[469])|(11))[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|"
                                    + "(0?2[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12"
                                    + "35679])|([13579][01345789]))[\\-\\-\\s]?((((0?[13578])|(1[02]))"
                                    + "[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))"
                                    + "[\\-\\-\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\-\\s]?((0?["
                                    + "1-9])|(1[0-9])|(2[0-8]))))))");

        return p.matcher(date).matches();
    }

    /**
     * Description: 查询[start,end)中的月份
     * 
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     *             List<String>
     * @see
     */
    public static List<String> getMonthBetween(String startDate, String endDate)
        throws ParseException {

        startDate = startDate.substring(0, 6);
        endDate = endDate.substring(0, 6);

        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");// 格式化为年月日

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(startDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(endDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    // /**
    // * Description: 默认取当前月份前一个月的时间
    // *
    // * @param startTime
    // * @param endTime
    // * @param monthBetween
    // * @throws ParseException
    // * void
    // * @see
    // */
    // public static void defaultMonthBetween(String startTime, String endTime,
    // List<String> monthBetween)
    // throws ParseException {
    //
    // }

    /**
     * Description:比较两个时间的大小
     * 
     * @param data1
     * @param data2
     * @return boolean
     * @throws ParseException
     * @see
     */
    public static boolean compareDate(String startTime, String timeFromDB, String endTime)
        throws ParseException {

        LOG.info("startTime >>>>  " + startTime + "  endTime  >>>>  " + endTime
                 + "  timeFromDB  >>>>  " + timeFromDB);

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date startDate = df.parse(startTime);

        Date dateFromDB = df.parse(timeFromDB);
        Date endDate = df.parse(endTime);
        if (dateFromDB.getTime() >= startDate.getTime()
            && endDate.getTime() >= dateFromDB.getTime()) {
            return true;
        }
        else
            return false;
    }

    /**
     * Description:获取startTime endTime 相同的部分
     * 
     * @param startTime
     * @param endTime
     * @return String
     * @see
     */
    public static String getEqualTime(String startTime, String endTime) {
        String time = null;
        for (int i = 4; i < 14; i = i + 2) {
            if (startTime.substring(0, i).equals(endTime.substring(0, i))) {
                time = startTime.substring(0, i);
            }
        }
        return time;
    }

    /**
     * Description: 获取默认的时间
     *
     * @see
     */
    public static String defaultTime() {

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()).substring(0, 8);
    }
}
