package entpack.utils;

import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 日期工具类
 */
public class DateUtil {

    public static String formatDate() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 返回字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 解析日期
     *
     * @param strDate 日期字符串
     * @return 返回日期或null
     */
    public static Date parse(String strDate) {
        return parse(strDate, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化日期
     *
     * @param date    日期
     * @param pattern 格式表达式
     * @return 返回字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 解析日期
     *
     * @param strDate 日期字符串
     * @param pattern 格式表达式
     * @return 返回日期或null
     */
    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parse(String strDate, String pattern, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取系统当前毫秒数
     *
     * @return 返回长整型
     */
    public static long getNowTime() {
        return System.currentTimeMillis();
    }

    /**
     * 得到几天前或几天后的date
     *
     * @param day 负数代表几天前，正数代表几天后
     * @return 返回日期
     */
    public static Date getDateByDays(int day) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        long res = now + (new BigDecimal(day).multiply(new BigDecimal(24 * 60 * 60 * 1000)).longValue());
        calendar.setTimeInMillis(res);

        return calendar.getTime();
    }

    /**
     * 判断当前日期是否在一个有效期内
     *
     * @param start 开始日期
     * @param end   截止日期
     * @return 返回布尔值
     */
    public static boolean between(Date start, Date end) {
        Date now = new Date();
        if (start == null || end == null) {
            return false;
        } else if (now.after(start) && now.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到当前时间精确到秒的字符串
     *
     * @return 返回字符串
     */
    public static String dateStr() {
        return formatDate(new Date(), "yyyyMMddHHmmss");
    }

    /**
     * 得到本月的第一天
     *
     * @return 返回字符串
     */
    public static String getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));

        return formatDate(calendar.getTime(), "yyyy-MM-dd");
    }

    /**
     * 得到本月的最后一天
     *
     * @return 返回字符串
     */
    public static String getMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMaximum(Calendar.DAY_OF_MONTH));
        return formatDate(calendar.getTime(), "yyyy-MM-dd");
    }

    /**
     * 获取某年某月的最后一天
     *
     * @param year  int 年份
     * @param month int 月份
     * @return int 某年某月的最后一天
     */
    private int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        // 某年某月的最后一天
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 统计两个日期之间包含的天数。
     *
     * @param date1
     * @param date2
     * @return 返回时间
     */
    public static int getDayDiff(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new InvalidParameterException("date1 and date2 cannot be null!");
        }
        long millSecondsInOneDay = 24 * 60 * 60 * 1000;
        return (int) ((date1.getTime() - date2.getTime()) / millSecondsInOneDay);
    }

    /**
     * 获得当天0点时间
     *
     * @return 返回时间
     */
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();


    }

    /**
     * 获得昨天0点时间
     *
     * @return 返回时间
     */
    public static Date getYesterdaymorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    /**
     * 获得当天近7天时间
     *
     * @return 返回时间
     */
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    /**
     * 获得当天24点时间
     *
     * @return 返回时间
     */
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得本周一0点时间
     *
     * @return 返回时间
     */
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获得本周日24点时间
     *
     * @return 返回时间
     */
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    /**
     * 获得本月第一天0点时间
     *
     * @return 返回时间
     */
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天24点时间
     *
     * @return 返回时间
     */
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    /**
     * 上月初0点时间
     *
     * @return 返回时间
     */
    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthmorning());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * 当前季度的结束时间，即2012-01-01 00:00:00
     *
     * @return 返回时间
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return 返回时间
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }

    /**
     * 本年开始点时间
     *
     * @return 返回时间
     */
    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), 1, 1, 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    /**
     * 本年结束点时间
     *
     * @return 返回时间
     */
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    /**
     * 上年开始点时间
     *
     * @return 返回时间
     */
    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    public static String formatDateISO(Date d) {
        return formatDateISO(d, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    public static String formatDateISO(Date d, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        String nowAsISO = formatter.format(d);
        return nowAsISO;
    }

    public static String formatDateISO(Date d, String pattern, TimeZone timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(timeZone);
        String nowAsISO = formatter.format(d);
        return nowAsISO;
    }


    public static Date getDateFromISO(String str) {
        return parse(str, "yyyy-MM-dd'T'HH:mm:ssXXX");
    }

    public static Date getDateFromISO(String str, String pattern) {
        return parse(str, pattern);
    }

    public static void main(String[] args) throws ParseException {

        System.out.println("t:" + DateUtil.formatDate(getTimesmorning()));
//

        System.out.println(new Timestamp(1576218563004L));

        Date betTime = DateUtil.parse("2019-10-09T19:35:36.07", "yyyy-MM-dd'T'HH:mm:ss.SS");

        System.out.println(DateUtil.formatDate(betTime));
        System.out.println(formatDateISO(new Date()));

//        DateTimeFormatter isoformat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        System.out.println(new DateTime().toString(isoformat));
//        DateTime dt= isoformat.parseDateTime("2019-06-18T15:53:00.498-07:00");
//
//        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dt.toString(format));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        System.out.println(formatter.format(new Date()));

        Date date = formatter.parse("2019-07-18T20:38:40.347+08:00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = sdf.format(date);
        System.out.println(sDate);

        System.out.println(DateUtil.formatDateISO(date));
        System.out.println(DateUtil.formatDate(date));

        String str = DateUtil.formatDate(DateUtil.getDateFromISO("2023-01-04T21:16:39+06:00"));
        System.out.println(str);
    }

    /**
     * 添加或减小时间
     *
     * @param field
     * @param amount
     * @return
     */
    public static Date addHour(Date nowTime, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
//        System.out.println(nowTime);
        calendar.add(field, amount); //减填负数
        nowTime = calendar.getTime();
        return nowTime;
    }

    public static Date setHour(Date nowTime, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.set(field, amount); //减填负数
        nowTime = calendar.getTime();
        return nowTime;
    }

    public static Date setHour(Date nowTime, int hour, int minute,int second,int millisecond) {
        nowTime = setHour(nowTime, Calendar.HOUR_OF_DAY, hour);
        nowTime = setHour(nowTime, Calendar.MINUTE, minute);
        nowTime = setHour(nowTime, Calendar.SECOND, second);
        nowTime = setHour(nowTime, Calendar.MILLISECOND, millisecond);
        return nowTime;
    }

    public static Date setHour(Date nowTime, int minute,int second,int millisecond) {
        nowTime = setHour(nowTime, Calendar.MINUTE, minute);
        nowTime = setHour(nowTime, Calendar.SECOND, second);
        nowTime = setHour(nowTime, Calendar.MILLISECOND, millisecond);
        return nowTime;
    }

    /**
     * 添加或减小时间
     *
     * @param field
     * @param amount
     * @return
     */
    public static Date addHour(int field, int amount) {

        return addHour(new Date(), field, amount);
    }


    /**
     * String(yyyy-MM-dd HH:mm:ss) 转 Date
     *
     * @param time
     * @return
     * @throws ParseException
     */
    // String date = "2010/05/04 12:34:23";
    public static Date stringToDate(String time) {

        Date date = new Date();
        // 注意format的格式要与日期String的格式相匹配
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = dateFormat.parse(time);
            System.out.println(date.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    //add by ufo

    /**
     * Date转为String(yyyy-MM-dd HH:mm:ss)
     *
     * @param time
     * @return
     */
    public static String dateToString(Date time) {
        String dateStr = "";
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
        try {
            dateStr = dateFormat.format(time);
            System.out.println(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * String(yyyy-MM-dd HH:mm:ss)转10位时间戳
     *
     * @param time
     * @return
     */
    public static Integer stringToTimestamp(String time) {

        int times = 0;
        try {
            times = (int) ((Timestamp.valueOf(time).getTime()) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (times == 0) {
            System.out.println("String转10位时间戳失败");
        }
        return times;

    }

    /**
     * 10位int型的时间戳转换为String(yyyy-MM-dd HH:mm:ss)
     *
     * @param time
     * @return
     */
    public static String timestampToString(Integer time) {
        //int转long时，先进行转型再进行计算，否则会是计算结束后在转型
        long temp = (long) time * 1000;
        Timestamp ts = new Timestamp(temp);
        String tsStr = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //方法一
            tsStr = dateFormat.format(ts);
            System.out.println(tsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tsStr;
    }

    /**
     * 10位时间戳转Date
     *
     * @param time
     * @return
     */
    public static Date timestampToDate(long time) {
        long temp = time * 1000;
        Timestamp ts = new Timestamp(temp);
        return ts;
    }

    /**
     * 14位时间戳转Date
     * @param time
     * @return
     */
    public static Date mstimestampToDate(long time) {
        long temp = time;
        Timestamp ts = new Timestamp(temp);
        return ts;
    }

    /**
     * Date类型转换为10位时间戳
     *
     * @param time
     * @return
     */
    public static Integer dateToTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());

        return (int) ((ts.getTime()) / 1000);
    }

    /**
     * Date类型转换为14位时间戳
     * @param time
     * @return
     */
    public static Long dateToMsTimestamp(Date time) {
        Timestamp ts = new Timestamp(time.getTime());

        return (long) ts.getTime();
    }
}
