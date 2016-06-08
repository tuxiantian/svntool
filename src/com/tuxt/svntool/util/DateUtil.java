package com.tuxt.svntool.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 关于日期的操作建议使用该类中的方法。当该类中的方法不能满足需求或使用不便时，再在类的后面添加自己的方法。
 * @author tuxiantian@163.com
 * @since 2015年12月17日上午10:43:03
 */
public final class DateUtil {
	/** Private Constructor **/
	private DateUtil() {
	}

	/** 日期格式 **/
	public interface DATE_PATTERN {
		String HHMMSS = "HHmmss";
		String HH_MM_SS = "HH:mm:ss";
		String YYYYMMDD = "yyyyMMdd";
		String YYYY_MM_DD = "yyyy-MM-dd";
		String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
		String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
		String YNR="YYYY年MM月dd日";
	}

	/**
	 * 将Date类型转换成String类型
	 * 
	 * @param date
	 *            Date对象
	 * @return 形如:"yyyy-MM-dd HH:mm:ss"
	 */
	public static String date2String(Date date) {
		return date2String(date, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
	}

	/**
	 * 将Date按格式转化成String
	 * 
	 * @param date
	 *            Date对象
	 * @param pattern
	 *            日期格式
	 * @return String
	 */
	public static String date2String(Date date, String pattern) {
		if (date == null || pattern == null) {
			throw new IllegalArgumentException("Date and Pattern must not be null");
		}
		return new SimpleDateFormat(pattern).format(date);
	}
	/**
	 * 将date转为pattern格式的字符串
	 * @param date
	 * @param pattern 日期格式<br>
	 * 				     建议使用DateUtil中DATE_PATTERN里面定义的值
	 * @return
	 */
	public static String date2String(String date,String pattern){
		if (date == null || pattern == null) {
			throw new IllegalArgumentException("Date and Pattern must not be null");
		}
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		try {
			return sdf.format(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将字符串转换成日期
	 * 
	 * @param date
	 *            日期字符串
	 * @param pattern
	 *            格式
	 * @return
	 */
	public static Date formatDate(String date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
		}
		return new Date();
	}
	/**
	 * 将String类型转换成Date类型.使用格式yyyy-MM-dd HH:mm:ss进行转换。
	 * 
	 * @param date   日期字符串
	 * @return Date对象
	 */
	public static Date string2Date(String date) {
		if (date == null) {
			throw new IllegalArgumentException("date must not be null");
		}
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	/**
	 * 将String类型转换成Date类型
	 * 
	 * @param date    日期字符串
	 * @param pattern 日期格式
	 * @return
	 */
	public static Date string2Date(String date,String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 获取该日期的年份和月份
	 * 传入yyyy-MM-dd格式的字符串返回yyyyMM格式的字符串
	 * @author jiaotd
	 * @since 2015年7月25日 下午4:02:28
	 * @return
	 */
	public static String getYearAndMonth(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = GregorianCalendar.getInstance();
		try {
		calendar.setTime(sdf.parse(str));
		} catch (ParseException e) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(calendar.get(Calendar.YEAR));
		if((calendar.get(Calendar.MONTH) + 1) < 10 ){
			sb.append("0").append((calendar.get(Calendar.MONTH) + 1));
		}else{
			sb.append((calendar.get(Calendar.MONTH) + 1));
		}
		return sb.toString();
	}
	/**
	 * 获取该日期的上一个月的年份和月份
	 * 传入yyyy-MM-dd格式的字符串返回yyyyMM格式的字符串
	 * <pre>
	 * DateUtil.getLastYearAndMonth("2015-12-16 19:22:21")="201511"
	 * </pre>
	 * @param str
	 * @return
	 */
	public static String getLastYearAndMonth(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = GregorianCalendar.getInstance();
		try {
		calendar.setTime(sdf.parse(str));
		} catch (ParseException e) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		
		//如果上一个月小于10
		if((calendar.get(Calendar.MONTH)) < 10 ){
			//如果上一个月是0
			if (calendar.get(Calendar.MONTH)==0) {
				return sb.append(calendar.get(Calendar.YEAR)-1).append("12").toString();
			}else {
				sb.append(calendar.get(Calendar.YEAR));
				sb.append("0").append((calendar.get(Calendar.MONTH)));
			}
		}else{
			sb.append(calendar.get(Calendar.YEAR));
			sb.append((calendar.get(Calendar.MONTH)));
		}
		return sb.toString();
	}
	
	
	/**
	 * 判断传入的时间是否比当前日大
	 * @author jiaotd
	 * @since 2015年7月26日 下午2:24:55
	 * @param date(yyyy-MM-ss)
	 * @return
	 */
	public static boolean isBeforeToday(String str){
		Date date = string2Date(str,DATE_PATTERN.YYYY_MM_DD);
		Date today = new Date();
		today = string2Date(date2String(today,DATE_PATTERN.YYYY_MM_DD), DATE_PATTERN.YYYY_MM_DD);
		return date.before(today);
	}
	
	/**
	 * 得到2个日期之间的月份
	 * <pre>
	 * DateUtil.getMonthsBetween(DateUtil.string2Date("2015-12-16 14:16:20"), DateUtil.string2Date("2015-09-16 14:16:20"), "MM")=[09, 10, 11, 12]
	 * </pre>
	 * @param minDate
	 * @param maxDate
	 * @param format 日期的格式
	 * @return <code>List&lt;String&gt;</code>2个日期之间的月份的集合
	 */
	public static List<String> getMonthsBetween(Date minDate, Date maxDate,String format) {
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (minDate.after(maxDate)) {
			Date tmp = minDate;
			minDate = new Date(maxDate.getTime());
			maxDate = new Date(tmp.getTime());
		}
		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();
		min.setTime(minDate);
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
		max.setTime(maxDate);
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}
		return result;
	}
	
	/**
	 * 获得两个日期之间的所有日期
	 * @param minStr
	 * @param maxStr
	 * @param dateFormat 日期返回值的格式
	 * @return
	 */
	public static List<String> getAllDays(String minStr, String maxStr, String dateFormat) {
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		if (minStr.length() > 10) {
			minStr = minStr.substring(0, 10);
		}
		if (maxStr.length() > 10) {
			maxStr = maxStr.substring(0, 10);
		}
		Date minDate = DateUtil.string2Date(minStr, "yyyy-MM-dd");
		Date maxDate = DateUtil.string2Date(maxStr, "yyyy-MM-dd");
		if (minDate.after(maxDate)) {
			Date tmp = minDate;
			minDate = new Date(maxDate.getTime());
			maxDate = new Date(tmp.getTime());
		}
		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();
		min.setTime(minDate);
		max.setTime(maxDate);
		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.DAY_OF_MONTH, 1);
		}
		result.add(sdf.format(max.getTime()));
		return result;
	}
	
	public enum TimeUnit{
		day,hour,min,sec;
	}
	/**
	 * 获取两个日期之间的差值，以TimeUnit为单位，有天、小时、分钟、秒几个单位。
	 * 要使用DateUtil类里面的枚举TimeUnit作为单位
	 * <pre>
	 * DateUtil.getDistanceTimes("2015-12-16 19:22:21", "2015-12-16 19:22:31", TimeUnit.sec)=10
	 * </pre>
	 * @param str1
	 * @param str2
	 * @param unit DateUtil类里面的枚举TimeUnit
	 * @return
	 */
	public static long getDistanceTimes(String str1, String str2,TimeUnit unit) {
		if (unit==null) {
			  throw new IllegalArgumentException("The TimeUnit must not be null");
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			switch (unit) {
			case day:
				day = diff / (24 * 60 * 60 * 1000);
				return day;
			case hour:
				hour = (diff / (60 * 60 * 1000) - day * 24);
				return hour;
			case min:
				min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
				return min;
			case sec:
				sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
				return sec;
			default:
				break;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	/**
	 * 是否小于当前系统时间几分钟
	 * @throws ParseException 
	 */
	public static boolean isBeforeSomeminuteNow(String compareTime,int someminute) throws ParseException{
		return isBeforeNminutes(compareTime,someminute,new Date());
	}
	/**
	 * 是否在被比较时间之后
	 * @param compareDate 比较的时间
	 * @param minutes 相差分钟
	 * @param date 被比较的时间
	 * @return
	 * @throws ParseException 
	 */
	public static boolean isBeforeNminutes(String compareDate,int minutes,Date date) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
		Date d1 = format.parse(compareDate);
		return (date.getTime()-d1.getTime())/(60*1000)>=minutes;
	}
	/**
	 * 将形如"yyyyMMddHHmmss"的数字字符串转为"yyyy-MM-dd HH:mm:ss"格式的日期字符串
	 * @param number
	 * @return
	 */
	public static String number2DateStr(String number) {
		if (StringUtil.isEmpty(number)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date date = df.parse(number);
			return date2String(date, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
		} catch (Exception e) {
		}
		if (number.length() >= 8) {
			return number.substring(0, 8);
		}
		return null;
	}
	/**
	 * 将日期字符串转为“yyyyMMddHHmmss”格式的数字字符串
	 * @param dateStr
	 * @return
	 */
	public static String dateStr2Number(String dateStr) {
		if (StringUtil.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
		try {
			Date date = df.parse(dateStr);
			return date2String(date, "yyyyMMddHHmmss");
		} catch (Exception e) {
		}
		return dateStr.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
	}
	/**
	 * 判断date是否是它所在月份的最后一天
	 * @param date
	 * @return
	 */
	public static boolean isLastDayOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();  
		  
		// 设置日期为本月最大日期  
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); 
		DateFormat format=new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
		
		try {
			if (format.parse(date2String(calendar.getTime())).compareTo(format.parse(date2String(date)))==0) {
				return true;
			}else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断date是否是它所在月份的第一天
	 * @param date
	 * @return
	 */
	public static boolean isFirstDayOfMonth(Date date){
		Calendar calendar = Calendar.getInstance();  
		  
		// 设置日期为本月最小日期  
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE)); 
		DateFormat format=new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD);
		
		try {
			if (format.parse(date2String(calendar.getTime())).compareTo(format.parse(date2String(date)))==0) {
				return true;
			}else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 以TimeUnit为单位对该日期进行加减。若是减，add为负值
	 * @param date
	 * @param add
	 * @param unit DateUti类中的枚举TimeUnit
	 * @return
	 */
	public static Date getAddDateByTimeUnit(Date date, int add,TimeUnit unit) {
		if (unit==null) {
			throw new IllegalArgumentException("The TimeUnit must not be null");
		}
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setLenient(true);
		if (add == 0) {
			return c.getTime();
		}
		switch (unit) {
		case day:
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+add);
			return c.getTime();
		case hour:
			c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR)+add);
			return c.getTime();
		case min:
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)+add);
			return c.getTime();
		case sec:
			c.set(Calendar.SECOND, c.get(Calendar.SECOND)+add);
			return c.getTime();
		default:
			return date;
		}
	}
	
	public static void main(String[] args) throws ParseException {
		//System.out.println(DateUtil.getYearAndMonth("2015-5-6 3:4:3"));
		String str="2015-12-31";
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		/*System.out.println(DateUtil.getYearAndMonth(date2String(new Date())));
		System.out.println(DateUtil.getLastYearAndMonth(date2String(new Date())));
		System.out.println(DateUtil.getLastYearAndMonth(str));*/
		System.out.println("2015-12-31:"+DateUtil.isFirstDayOfMonth(format.parse("2015-11-01")));
		//System.out.println("2015-12-31:"+DateUtil.isLastDayOfMonth2(format.parse("2015-12-31")));
		/*System.out.println("2015-12-30:"+DateUtil.isLastDayOfMonth2(format.parse("2015-12-30")));
		System.out.println("2015-2-28:"+DateUtil.isLastDayOfMonth2(format.parse("2015-2-28")));
		System.out.println("2015-2-27:"+DateUtil.isLastDayOfMonth2(format.parse("2015-2-27")));
		System.out.println("2015-9-30:"+DateUtil.isLastDayOfMonth2(format.parse("2015-9-30")));
		System.out.println("2015-9-29:"+DateUtil.isLastDayOfMonth2(format.parse("2015-9-29")));
		System.out.println("2000-2-29:"+DateUtil.isLastDayOfMonth2(format.parse("2000-2-29")));
		System.out.println("2000-2-28:"+DateUtil.isLastDayOfMonth2(format.parse("2000-2-28")));*/
	}
}
