package com.imall.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imall.common.enums.HolidayTypeEnum;

public class TimeUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);
	
	public static Date getDate(String value, String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return new Date(sdf.parse(value).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Timestamp getTimestamp(String value, String format){
		if(StringUtils.isBlank(value) || StringUtils.isBlank(format)){
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return new Timestamp(sdf.parse(value).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Timestamp getTimestamp(String value){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP_FORMAT);
			return new Timestamp(sdf.parse(value).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @description 计算两个日期间的秒数差，超过days天忽略
	 * @method getSecondsDiff
	 * @param [startTime, endTime, days]
	 * @return
	 * @time 2018/9/29
	 * @author tianxiang@insightchain.io
	 */
	public static long getMiniutesDiff(Timestamp startTime, Timestamp endTime){
		return (long) Math.floor((endTime.getTime() - startTime.getTime()) / CommonConstants.MINIUTE_M_S);
	}
	public static Timestamp getGMTTimestamp(String gmtTimeString){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP_GMT_FORMAT, Locale.US);
			return new Timestamp(sdf.parse(gmtTimeString).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public static Timestamp getUTCTimestamp(String gmtTimeString){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP_UTC_FORMAT, Locale.US);
            return new Timestamp(sdf.parse(gmtTimeString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static String getTimeString(Date time, String format){
		if(time == null){
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getGMTTimeString(Date time){
		if(time == null){
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP_GMT_FORMAT, Locale.US);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getTimeString(Date time){
		if(time == null){
			return null;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.TIME_STAMP_FORMAT);
			return sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 得到两天之间的天数差
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getDaysDiff(Long startTime, Long endTime){
		Timestamp startTimestamp = TimeUtils.getDayStartTimestamp(new Timestamp(startTime));
		Timestamp endTimestamp = TimeUtils.getDayStartTimestamp(new Timestamp(endTime));
		return (int) Math.floor((endTimestamp.getTime() - startTimestamp.getTime()) / CommonConstants.DAY_M_S);
	}
	
	public static int getAgeFromBirthday(Date birthday){
		Calendar mycalendar = Calendar.getInstance();// 获取现在时间
		int thisYear = mycalendar.get(Calendar.YEAR);// 获取年份
		mycalendar.setTime(birthday);
		int birthdayYear = mycalendar.get(Calendar.YEAR);// 获取年份
		return thisYear - birthdayYear;
	}
	
	public static Timestamp getBirthdayYearFromAge(int age){
		Calendar mycalendar = Calendar.getInstance();// 获取现在时间
		int thisYear = mycalendar.get(Calendar.YEAR);// 获取年份
		return getTimestamp(Integer.toString(thisYear - age), "yyyy");
	}
	
	/**
	 * 判断date是否是今天
	 * 
	 * @param date
	 * @return true为今天，false为今天之前
	 */
	public static boolean isToday(Date date){
		return isSameDay(date, new Timestamp(System.currentTimeMillis()));
	}
	
	/**
	 * 是否为同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2){
		if(date1 == null || date2 == null){
			return false;
		}
		date1 = TimeUtils.getDayStartTimestamp(new Timestamp(date1.getTime()));
		date2 = TimeUtils.getDayStartTimestamp(new Timestamp(date2.getTime()));
		return date1.getTime() == date2.getTime();
	}

	/**
	 * 比较两个日期是否在同一个小时之内
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameHour(Date date1, Date date2){
		if(date1 == null || date2 == null){
			return false;
		}
		String date1Str = TimeUtils.getTimeString(date1, CommonConstants.TIME_STAMP_FORMAT_HOUR);
		String date2Str = TimeUtils.getTimeString(date2, CommonConstants.TIME_STAMP_FORMAT_HOUR);
		return date1Str.equals(date2Str);
	}

	/**
	 * 比较两个日期是否在同一个小时之内
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameMinute(Date date1, Date date2){
		if(date1 == null || date2 == null){
			return false;
		}
		String date1Str = TimeUtils.getTimeString(date1, CommonConstants.TIME_STAMP_FORMAT_MINUTE);
		String date2Str = TimeUtils.getTimeString(date2, CommonConstants.TIME_STAMP_FORMAT_MINUTE);
		return date1Str.equals(date2Str);
	}
	
	/**
	 * 得到daytime那天一开始的timestamp
	 * 
	 * @param dayTime
	 * @return
	 */
	public static Timestamp getDayStartTimestamp(Date dayTime){
		String dayTimeStr = getTimeString(dayTime);
		String dayStr = dayTimeStr.split(" ")[0];
		return getTimestamp(dayStr + " 00:00:00");
	}
	
	/**
	 * 得到daytime那天一结束的timestamp
	 * 
	 * @param dayTime
	 * @return
	 */
	public static Timestamp getDayEndTimestamp(Timestamp dayTime){
		String dayTimeStr = getTimeString(dayTime);
		String dayStr = dayTimeStr.split(" ")[0];
		return getTimestamp(dayStr + " 23:59:59");
	}
	
	/**
	 * 得到dayTime那天21:59:59的timestamp
	 * 
	 * @param dayTime
	 * @return
	 */
	public static Timestamp getDay22Timestamp(Timestamp dayTime){
		String dayTimeStr = getTimeString(dayTime);
		String dayStr = dayTimeStr.split(" ")[0];
		return getTimestamp(dayStr + " 21:59:59");
	}
	
	/**
	 * 把dayTime的day和givenDayTime的hour、mins、senconds组合起来
	 * 
	 * @param dayTime
	 * @param givenDayTime
	 * @return
	 */
	public static Timestamp getGivenDayTimestamp(Date dayTime, Date givenDayTime){
		String dayTimeStr = getTimeString(dayTime);
		String dayStr = dayTimeStr.split(" ")[0];
		
		String giveDayTimeStr = getTimeString(givenDayTime);
		String giveTimeStr = giveDayTimeStr.split(" ")[1];
		
		return getTimestamp(dayStr + " " + giveTimeStr);
	}
	
	/**
	 * 获得某一天的随机时间
	 * 
	 * @param timeDay
	 * @param excludeNight 是否不包括晚上
	 * @param canAfterNow 是否可以在当前时间之后
	 * @return
	 */
	public static Timestamp getRandomTimestamp(Timestamp timeDay, boolean excludeNight, boolean canAfterNow){
		Timestamp timestamp = null;
		try {
			String dayTimeStr = getTimeString(timeDay);
			String dayStr = dayTimeStr.split(" ")[0];
			StringBuilder sb = new StringBuilder();
			int toHour = 23;
			Calendar nowCal = Calendar.getInstance();
			nowCal.setTime(new Timestamp(System.currentTimeMillis()));
			int nowHour = nowCal.get(Calendar.HOUR_OF_DAY);
			int nowMinute = nowCal.get(Calendar.MINUTE);
			int nowSecond = nowCal.get(Calendar.SECOND);
			if(!canAfterNow && isToday(timeDay)){
				toHour = nowHour - 1;
			}
			if(toHour < 0){
				toHour = 0;
			}
			if(excludeNight){
				if(toHour <= 6){
					toHour = 7;
				}
				sb.append(generate2IntString(6, toHour));
			}else{
				sb.append(generate2IntString(toHour));
			}
			sb.append(":");
			sb.append(generate2IntString(59));
			sb.append(":");
			sb.append(generate2IntString(59));
			timestamp = getTimestamp(dayStr + " " + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	/**
	 * 获得某几天内的随机时间，去除晚上的时间
	 * 
	 * @param fromDay
	 * @param toDay
	 * @param excludeNight
	 * @param canAfterNow
	 * @return
	 */
	public static Timestamp getRandomTimestamp(Timestamp fromDay, 
			Timestamp toDay, boolean excludeNight, boolean canAfterNow){
		Timestamp timestamp = null;
		try {
			Timestamp nextNDay = null;
			if(fromDay == null && toDay != null){
				nextNDay = toDay;
			}else if(fromDay != null && toDay == null){
				nextNDay = fromDay;
			}else if(fromDay != null && toDay != null){
				Calendar fromCal = Calendar.getInstance();
				fromCal.setTime(fromDay);
				
				Calendar toCal = Calendar.getInstance();
				toCal.setTime(toDay);
				
				int diff = getDaysDiff(fromDay.getTime(), toDay.getTime());
				int random = 0;
				if(diff > 0){
					if(canAfterNow){
						random = RandomUtils.nextInt(diff + 1);
					}else{
						//防止random为某一天一开始的情况，这样产生的时间就肯定会大于当前时间了
						Calendar nowCal = Calendar.getInstance();
						nowCal.setTime(new Timestamp(System.currentTimeMillis()));
						int nowHour = nowCal.get(Calendar.HOUR_OF_DAY);
						if(nowHour <= 7){
							random = RandomUtils.nextInt(diff);
						}
					}
				}
				nextNDay = getNextNDay(fromDay, random);
			}
			if(nextNDay != null){
				timestamp = getRandomTimestamp(nextNDay, excludeNight, canAfterNow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timestamp;
	}
	
	/**
	 * 生成两个时间之间的随机时间
	 * 
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public static Timestamp getRandomTimestamp(Timestamp fromTime, Timestamp toTime){
		if(fromTime == null || toTime == null){
			return null;
		}
		return new Timestamp(ObjectUtils.getRandom(fromTime.getTime(), toTime.getTime()));
	}
	
	/**
	 * 获得第n天以后的时间
	 * 
	 * @param fromDay
	 * @param n
	 * @return
	 */
	public static Timestamp getNextNDay(Timestamp fromDay, int n){
		return new Timestamp(fromDay.getTime() + n * CommonConstants.DAY_M_S);
	}
	
	/**
	 * @param minValue 最小值，可以等于
	 * @param maxValue 最大值，可以等于
	 * @return
	 */
	private static String generate2IntString(int minValue, int maxValue){
		int random = ObjectUtils.getRandom(minValue, maxValue);
		StringBuilder sb = new StringBuilder();
		if (random < 10) {
			sb.append(0);
		}
		sb.append(random);
		return sb.toString();
	}
	
	/**
	 * @param maxValue 最大值，可以等于
	 * @return
	 */
	private static String generate2IntString(int maxValue){
		int random = RandomUtils.nextInt(maxValue + 1);
		StringBuilder sb = new StringBuilder();
		if (random < 10) {
			sb.append(0);
		}
		sb.append(random);
		return sb.toString();
	}
	
	/**
	 * 得到距离最近的minutes分钟的timestamp，之后的时间
	 * 
	 * @param time
	 * @param minutes
	 * @return
	 */
	public static Timestamp getMinsTimestampAfter(Timestamp time, int minutes){
		if(time == null){
			time = new Timestamp(System.currentTimeMillis());
		}
		Timestamp currentTimestamp = time;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTimestamp);
		int min = cal.get(Calendar.MINUTE);
		
		String dayTimeStr = TimeUtils.getTimeString(currentTimestamp);
		String[] strs = dayTimeStr.split(" ");
		
		int currentMins = min;
		if(min % minutes != 0){
			for(int i = 0; i < minutes; i ++){
				if(((min + i) % minutes) == 0){
					currentMins = min + i;
				}
			}
		}
		String replace = "";
		String replaced = "";
		if(min < 10){
			replace = "0" + min;
			replaced = "0" + currentMins;
		}else{
			replace = String.valueOf(min);
			replaced = String.valueOf(currentMins);
		}
		replace = ":" + replace + ":";
		replaced = ":" + replaced + ":";
		
		String hours = strs[1].replace(replace, replaced);
		String str = strs[0] + " " + hours.substring(0, hours.indexOf(replaced) + 4) + "00";
		return getTimestamp(str);
	}

	/**
	 * 得到距离最近的minutes分钟的timestamp，之前的时间
	 *
	 * @param time
	 * @param minutes
	 * @return
	 */
	public static Timestamp getMinsTimestamp(Timestamp time, int minutes){
		if(time == null){
			time = new Timestamp(System.currentTimeMillis());
		}
		Timestamp currentTimestamp = time;

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTimestamp);
		int min = cal.get(Calendar.MINUTE);

		String dayTimeStr = TimeUtils.getTimeString(currentTimestamp);
		String[] strs = dayTimeStr.split(" ");

		int currentMins = min;
		if(min % minutes != 0){
			for(int i = 0; i < minutes; i ++){
				if(((min - i) % minutes) == 0){
					currentMins = min - i;
				}
			}
		}
		String replace = "";
		String replaced = "";
		if(min < 10){
			replace = "0" + min;
			replaced = "0" + currentMins;
		}else{
			replace = String.valueOf(min);
			replaced = String.valueOf(currentMins);
		}
		replace = ":" + replace + ":";
		replaced = ":" + replaced + ":";

		String hours = strs[1].replace(replace, replaced);
		String str = strs[0] + " " + hours.substring(0, hours.indexOf(replaced) + 4) + "00";
		return getTimestamp(str);
	}
	
	/**
	 * 得到距离最近的五分钟的timestamp，之前的时间
	 * 
	 * @param time
	 * @return
	 */
	public static Timestamp get5MinsTimestamp(Timestamp time){
		return getMinsTimestamp(time, 5);
	}
		
	/**
	 * 获取当前日期是星期几，星期日是1，然后到星期六是7
	 * 
	 * @param day
	 * @return 当前日期是星期几
	 */
	public static int getWeekIndexOfDate(Date day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(day);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
     * 获取当前日期是星期几<br>
     * 
     * @param day
     * @return 当前日期是星期几
     */
    public static String getWeekStringOfDate(Date day) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[getWeekIndexOfDate(day) - 1];
    }
    
    /**
	  * 获得用户在某个小时的活跃度比例
	  * 
	 * @param hour
	 * @return
	 */
	public static double getHourRatioForUserActive(int hour){
		 double ratio = 1.0;
		 Map<Integer, Double> hourRatios = getHourRatiosForUserActive();
		 Set<Integer> set = hourRatios.keySet();
		 for(Integer h : set){
			 if(h == hour){
				 ratio = hourRatios.get(h);
				 break;
			 }
		 }
		 return ratio;
	 }
	 
	 /**
	  * 获得用户在每个小时的活跃度比例
	  * 
	 * @return
	 */
	public static Map<Integer, Double> getHourRatiosForUserActive(){
		 Map<Integer, Double> hourRatios = new LinkedHashMap<Integer, Double>();
		 hourRatios.put(0, 0.4);
		 hourRatios.put(1, 0.1);
		 hourRatios.put(2, 0.01);
		 hourRatios.put(3, 0.01);
		 hourRatios.put(4, 0.01);
		 hourRatios.put(5, 0.1);
		 hourRatios.put(6, 0.1);
		 hourRatios.put(7, 0.5);
		 hourRatios.put(8, 0.6);
		 hourRatios.put(9, 0.8);
		 hourRatios.put(10, 1.0);
		 hourRatios.put(11, 1.2);
		 hourRatios.put(12, 1.2);
		 hourRatios.put(13, 1.1);
		 hourRatios.put(14, 0.9);
		 hourRatios.put(15, 0.9);
		 hourRatios.put(16, 1.0);
		 hourRatios.put(17, 1.0);
		 hourRatios.put(18, 1.2);
		 hourRatios.put(19, 1.3);
		 hourRatios.put(20, 1.3);
		 hourRatios.put(21, 1.3);
		 hourRatios.put(22, 1.0);
		 hourRatios.put(23, 0.6);
		 return hourRatios;
	 }
	
	/**
	 * 生成某一天的随机时间，根据用户活跃的时间来
	 * 
	 * @param day
	 * @return
	 */
	public static Timestamp getRandomTimeByUserActive(Timestamp day){
		Map<Integer, Double> hourTotalRatios = new LinkedHashMap<Integer, Double>();
		double total = 0;
		Map<Integer, Double> hourRatios = getHourRatiosForUserActive();
		Set<Integer> set = hourRatios.keySet();
		for(Integer h : set){
			total += hourRatios.get(h);
			hourTotalRatios.put(h, total);
		}
		double random = ObjectUtils.getRandom(0, total);
		set = hourTotalRatios.keySet();
		int hour = 10;
		for(Integer h : set){
			if(random <= hourTotalRatios.get(h)){
				hour = h;
				break;
			}
		}
		int minute = ObjectUtils.getRandom(0, 59);
		int seconds = ObjectUtils.getRandom(0, 59);
		String timeString = getTimeString(day, CommonConstants.DATE_FORMAT);
		timeString += " ";
		if(hour < 10){
			timeString += "0" + hour;
		}else{
			timeString += hour;
		}
		timeString += ":";
		if(minute < 10){
			timeString += "0" + minute;
		}else{
			timeString += minute;
		}
		timeString += ":";
		if(seconds < 10){
			timeString += "0" + seconds;
		}else{
			timeString += seconds;
		}
		return getTimestamp(timeString);
	}
	
	/**
	 * 得到上个月的最大天数
	 * 
	 * @param time
	 * @return
	 */
	public static int getMaxDayOfLastMonth(Date time){
    	return getMaxDayOfMonth(time, -1);
	}
	
	/**
	 * 一种格式换成另一种格式
	 * @author xy_wu
	 *
	 * @param startTime
	 * @param startFormat
	 * @param endFormat
	 * @return
	 */
	public static Timestamp getTimeByStartFormatToEndFormat(String startTime, String startFormat, String endFormat){
		SimpleDateFormat end = new SimpleDateFormat(endFormat);
		SimpleDateFormat start = new SimpleDateFormat(startFormat, Locale.ENGLISH);
		try {
			startTime = end.format(start.parse(startTime));
		} catch (Exception e) {
		}
		
		return getTimestamp(startTime, endFormat);
	}
	
	
	
	
	/**
	 * @param time
	 * @param monthDiff 和当前月份的差
	 * @return
	 */
	public static int getMaxDayOfMonth(Date time, int monthDiff){
		Calendar calendar = Calendar.getInstance(Locale.CHINESE);
		calendar.setTime(time);
		calendar.add(Calendar.MONTH, monthDiff);
		int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return day;
	}
	
	public static List<List<String>> LEGAL_HOLIDAS = new ArrayList<List<String>>();
	public static List<String> LEGAL_NOT_HOLIDAS = new ArrayList<String>();
	
	static{
		List<String> days = new ArrayList<String>();
		days.add("2016-01-01");
		days.add("2016-01-02");
		days.add("2016-01-03");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-02-07");
		days.add("2016-02-08");
		days.add("2016-02-09");
		days.add("2016-02-10");
		days.add("2016-02-11");
		days.add("2016-02-12");
		days.add("2016-02-13");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-04-02");
		days.add("2016-04-03");
		days.add("2016-04-04");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-04-30");
		days.add("2016-05-01");
		days.add("2016-05-02");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-06-09");
		days.add("2016-06-10");
		days.add("2016-06-11");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-09-15");
		days.add("2016-09-16");
		days.add("2016-09-17");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-10-01");
		days.add("2016-10-02");
		days.add("2016-10-03");
		days.add("2016-10-04");
		days.add("2016-10-05");
		days.add("2016-10-06");
		days.add("2016-10-07");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2016-12-31");
		days.add("2017-01-01");
		days.add("2017-01-02");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2017-01-27");
		days.add("2017-01-28");
		days.add("2017-01-29");
		days.add("2017-01-30");
		days.add("2017-01-31");
		days.add("2017-02-01");
		days.add("2017-02-02");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2017-04-02");
		days.add("2017-04-03");
		days.add("2017-04-04");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2017-04-29");
		days.add("2017-04-30");
		days.add("2017-05-01");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2017-05-28");
		days.add("2017-05-29");
		days.add("2017-05-30");
		LEGAL_HOLIDAS.add(days);
		
		days = new ArrayList<String>();
		days.add("2017-10-01");
		days.add("2017-10-02");
		days.add("2017-10-03");
		days.add("2017-10-04");
		days.add("2017-10-05");
		days.add("2017-10-06");
		days.add("2017-10-07");
		days.add("2017-10-08");
		LEGAL_HOLIDAS.add(days);

		days = new ArrayList<String>();
		days.add("2017-12-30");
		days.add("2017-12-31");
		days.add("2018-01-01");
		LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-02-15");
        days.add("2018-02-16");
        days.add("2018-02-17");
        days.add("2018-02-18");
        days.add("2018-02-19");
        days.add("2018-02-20");
        days.add("2018-02-21");
        LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-04-05");
        days.add("2018-04-06");
        days.add("2018-04-07");
        LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-04-29");
        days.add("2018-04-30");
        days.add("2018-05-01");
        LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-06-16");
        days.add("2018-06-17");
        days.add("2018-06-18");
        LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-09-22");
        days.add("2018-09-23");
        days.add("2018-09-24");
        LEGAL_HOLIDAS.add(days);

        days = new ArrayList<String>();
        days.add("2018-10-01");
        days.add("2018-10-02");
        days.add("2018-10-03");
        days.add("2018-10-04");
        days.add("2018-10-05");
        days.add("2018-10-06");
        days.add("2018-10-07");
        LEGAL_HOLIDAS.add(days);


		//非假期
		LEGAL_NOT_HOLIDAS.add("2016-02-06");
		LEGAL_NOT_HOLIDAS.add("2016-02-14");
		LEGAL_NOT_HOLIDAS.add("2016-06-12");
		LEGAL_NOT_HOLIDAS.add("2016-09-18");
		LEGAL_NOT_HOLIDAS.add("2016-10-08");
		LEGAL_NOT_HOLIDAS.add("2016-10-09");

		LEGAL_NOT_HOLIDAS.add("2017-01-22");
		LEGAL_NOT_HOLIDAS.add("2017-02-04");
		LEGAL_NOT_HOLIDAS.add("2017-04-01");
		LEGAL_NOT_HOLIDAS.add("2017-05-27");
		LEGAL_NOT_HOLIDAS.add("2017-09-30");

		LEGAL_NOT_HOLIDAS.add("2018-02-11");
		LEGAL_NOT_HOLIDAS.add("2018-02-24");
		LEGAL_NOT_HOLIDAS.add("2018-04-08");
		LEGAL_NOT_HOLIDAS.add("2018-04-28");
		LEGAL_NOT_HOLIDAS.add("2018-09-29");
		LEGAL_NOT_HOLIDAS.add("2018-09-30");
	}
	
	/**
	 * 暂时不考虑寒假和暑假
	 * 
	 * @param time
	 * @return
	 * 
	 */
	public static HolidayTypeEnum getHolidayType(Date time){
		HolidayTypeEnum type = HolidayTypeEnum.NOT_HOLIDAY;
		Calendar calendar = Calendar.getInstance(Locale.CHINESE);
		calendar.setTime(time);
		int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK);
		if(!isContainsByLegalNotHoliday(time)){
			type = getLegalHolidayType(time);
			if(type == HolidayTypeEnum.NOT_HOLIDAY){
				if(weekOfDay == Calendar.SATURDAY){
					type = HolidayTypeEnum.HOLIDAY_FIRST;
				}else if(weekOfDay == Calendar.SUNDAY){
					type = HolidayTypeEnum.HOLIDAY_LAST;
				}
			}
		}
		return type;
	}
	
	/**
	 * 是否被包含在国家额外的法定假日中
	 * 
	 * @param time
	 * @return
	 */
	private static HolidayTypeEnum getLegalHolidayType(Date time){
		String timeString = TimeUtils.getTimeString(time, CommonConstants.DATE_FORMAT);
		boolean contains = false;
		HolidayTypeEnum type = HolidayTypeEnum.NOT_HOLIDAY;
		for(List<String> holidays : LEGAL_HOLIDAS){
			int index = holidays.indexOf(timeString);
			if(index >= 0){
				if(index == 0){
					type = HolidayTypeEnum.HOLIDAY_FIRST;
				}else if(index == (holidays.size() - 1)){
					type = HolidayTypeEnum.HOLIDAY_LAST;
				}else{
					type = HolidayTypeEnum.HOLIDAY_CENTER;
				}
				break;
			}
		}
		return type;
	}
	
	/**
	 * 是否被包含在国家额外的非法定假日中
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isContainsByLegalNotHoliday(Date time){
		return LEGAL_NOT_HOLIDAS.contains(TimeUtils.getTimeString(time, CommonConstants.DATE_FORMAT));
	}
	
	public static void main(String[] args) {
		Timestamp now = get5MinsTimestamp(null);
		System.out.println(TimeUtils.getTimeString(now));
		
		now = get5MinsTimestamp(TimeUtils.getTimestamp("2015-02-02 18:01:01"));
		System.out.println(TimeUtils.getTimeString(now));
		
		now = get5MinsTimestamp(TimeUtils.getTimestamp("2015-02-02 18:02:01"));
		System.out.println(TimeUtils.getTimeString(now));

		now = get5MinsTimestamp(TimeUtils.getTimestamp("2015-02-02 18:04:01"));
		System.out.println(TimeUtils.getTimeString(now));
		
		now = get5MinsTimestamp(TimeUtils.getTimestamp("2015-02-02 18:05:01"));
		System.out.println(TimeUtils.getTimeString(now));
		
		now = new Timestamp(1466067062396L);
		String timeString = TimeUtils.getTimeString(now, "dd-MM-yyyy HH:mm:ssz");
		System.out.println(timeString);
		
		now = new Timestamp(1466067054578L);
		timeString = TimeUtils.getTimeString(now, "dd-MM-yyyy HH:mm:ssz");
		System.out.println(timeString);
	}
	
	
	
	
	/**
	 * date 字符串类型的时间； i表示前几天或者后几天
	 * 
	 * @author xy_wu
	 *
	 * @param date
	 * @param i
	 * @return
	 */
	public static String getNextOrLastDay(String date, Integer i) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date parse = null;
		try {
			parse = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse);
		calendar.add(Calendar.DAY_OF_MONTH, i);
		parse = calendar.getTime();
		return sdf.format(parse);
	}

	/**
	 * 特定时间的前后几个小时的时间
	 * 
	 * @author xy_wu
	 *
	 * @param date
	 * @param i
	 * @return
	 */
	public static Timestamp getNextOrLastHour(Date date, Integer i) {
		String dateStr = date.toString();
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.HOUR_OF_DAY, i);
		Timestamp timestamp = new Timestamp(cal.getTime().getTime());
		return timestamp;
	}

	/**
	 * 获取当前时间的 前后几个小时的整点时间
	 * 
	 * @author xy_wu
	 *
	 * @param date
	 * @param i
	 * @return
	 */
	public static Timestamp getHourStartTimestamp(Date date, Integer i) {

		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) + i);
		date = ca.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(date);

		return Timestamp.valueOf(format);

	}

	/**
	 * 是否满18岁
	 * @author xy_wu
	 *
	 * @param idCard
	 * @return
	 */
	public static boolean isAduil(String idCard) {
		
		boolean aduil = true;
		int year = Integer.parseInt(idCard.substring(6, 10)) + 18;
		int birthDate = Integer.parseInt(year + idCard.substring(10, 14));
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		int now = Integer.parseInt(simpleDateFormat.format(new Date()));
		if(birthDate > now){
			aduil = false;
		}
		
		return aduil;
	}

	/**
	 * @description 获取当天0点0分0秒
	 * @method getBeginTime
	 * @param [time]
	 * @return
	 * @time 2019/1/3
	 * @author tianxiang@insightchain.io
	 */
	public static Timestamp getBeginTime(Timestamp time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}

	/**
	 * @description获取当天23点59分59秒
	 * @method getLastTime
	 * @param [time]
	 * @return
	 * @time 2019/1/3
	 * @author tianxiang@insightchain.io
	 */
	public static Timestamp getLastTime(Timestamp time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return new Timestamp(calendar.getTimeInMillis());
	}

}
