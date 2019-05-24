package com.imall.common.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;

public class
TimeUtilsTest {
	
	@Test
	public void testIsToday(){
		Timestamp date = new Timestamp(System.currentTimeMillis());
		Assert.assertTrue(TimeUtils.isToday(date));
		
		date = TimeUtils.getTimestamp("2016-07-20 00:00:00");
		Assert.assertFalse(TimeUtils.isToday(date));
	}
	
	@Test
	public void testIsSameDay(){
		Timestamp date1 = new Timestamp(System.currentTimeMillis());
		Timestamp date2 = new Timestamp(System.currentTimeMillis());
		Assert.assertTrue(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 00:00:00");
		date2 = TimeUtils.getTimestamp("2016-07-20 00:00:00");
		Assert.assertTrue(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 02:40:30");
		date2 = TimeUtils.getTimestamp("2016-07-20 03:02:50");
		Assert.assertTrue(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 02:40:30");
		date2 = TimeUtils.getTimestamp("2016-07-20 02:40:30");
		Assert.assertTrue(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 00:00:00");
		date2 = TimeUtils.getTimestamp("2016-07-21 00:00:00");
		Assert.assertFalse(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 02:40:30");
		date2 = TimeUtils.getTimestamp("2016-07-21 03:02:50");
		Assert.assertFalse(TimeUtils.isSameDay(date1, date2));
		
		date1 = TimeUtils.getTimestamp("2016-07-20 02:40:30");
		date2 = TimeUtils.getTimestamp("2017-07-21 03:02:50");
		Assert.assertFalse(TimeUtils.isSameDay(date1, date2));
	}

	@Test
	public void testGetAgeFromBirthday(){
		Calendar mycalendar = Calendar.getInstance();// 获取现在时间
		int thisYear = mycalendar.get(Calendar.YEAR);// 获取年份
		
		Timestamp birthday = TimeUtils.getTimestamp("1984-01-01 00:01:01", CommonConstants.TIME_STAMP_FORMAT);
		int age = TimeUtils.getAgeFromBirthday(birthday);
		Assert.assertEquals(thisYear - 1984, age);
		
		birthday = TimeUtils.getTimestamp("1984-12-31 23:59:59", CommonConstants.TIME_STAMP_FORMAT);
		age = TimeUtils.getAgeFromBirthday(birthday);
		Assert.assertEquals(thisYear - 1984, age);
	}
	
	@Test
	public void testGetBirthdayYearFromAge(){
		Timestamp birthdayYear = TimeUtils.getBirthdayYearFromAge(29);
		Calendar mycalendar = Calendar.getInstance();// 获取现在时间
		int thisYear = mycalendar.get(Calendar.YEAR);// 获取年份
		
		Timestamp time = TimeUtils.getTimestamp(Integer.toString(thisYear - 29), "yyyy");
		Assert.assertEquals(time.getTime(), birthdayYear.getTime());
	}
	
	@Test
	public void testGetRandomTimestamp(){
		Timestamp fromDay = null;
		Timestamp toDay = null;
		Timestamp random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
		Assert.assertNull(random);
		
		for(int i = 0; i < 1000; i ++){
			fromDay = TimeUtils.getTimestamp("2016-02-10 01:29:53");
			toDay = null;
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getTimestamp("2016-02-10 00:00:00").getTime());
			Assert.assertTrue(random.getTime() < TimeUtils.getTimestamp("2016-02-11 00:00:00").getTime());
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = null;
			toDay = TimeUtils.getTimestamp("2016-02-10 01:29:53");
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getTimestamp("2016-02-10 00:00:00").getTime());
			Assert.assertTrue(random.getTime() < TimeUtils.getTimestamp("2016-02-11 00:00:00").getTime());
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = TimeUtils.getTimestamp("2016-02-10 01:29:53");
			toDay = TimeUtils.getTimestamp("2016-02-10 12:31:48");
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getTimestamp("2016-02-10 00:00:00").getTime());
			Assert.assertTrue(random.getTime() < TimeUtils.getTimestamp("2016-02-21 00:00:00").getTime());
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = TimeUtils.getTimestamp("2016-02-10 01:29:53");
			toDay = fromDay;
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getTimestamp("2016-02-10 00:00:00").getTime());
			Assert.assertTrue(random.getTime() < TimeUtils.getTimestamp("2016-02-21 00:00:00").getTime());
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = new Timestamp(System.currentTimeMillis() - 2 * CommonConstants.DAY_M_S);
			toDay = new Timestamp(System.currentTimeMillis());
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, false, false);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getDayStartTimestamp(fromDay).getTime());
			Assert.assertTrue(random.getTime() < (toDay.getTime() + CommonConstants.HOUR_M_S));
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = new Timestamp(System.currentTimeMillis() - 2 * CommonConstants.DAY_M_S);
			toDay = new Timestamp(System.currentTimeMillis());
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, false);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getDayStartTimestamp(fromDay).getTime());
			Assert.assertTrue(random.getTime() <= toDay.getTime());
		}
		
		for(int i = 0; i < 1000; i ++){
			fromDay = new Timestamp(System.currentTimeMillis() - 2 * CommonConstants.DAY_M_S);
			toDay = new Timestamp(System.currentTimeMillis());
			random = TimeUtils.getRandomTimestamp(fromDay, toDay, true, true);
//			System.out.println(TimeUtils.getTimeString(random));
			Assert.assertTrue(random.getTime() >= TimeUtils.getDayStartTimestamp(fromDay).getTime());
			Assert.assertTrue(random.getTime() <= TimeUtils.getGivenDayTimestamp(toDay, TimeUtils.getTimestamp("2016-12-10 23:59:59")).getTime());
		}
	}
	
	@Test
	public void testGetRandomTimeByUserActive(){
		for(int i = 0; i < 1000; i ++){
			Timestamp time1 = TimeUtils.getTimestamp("1984-09-02 00:01:01");
			Timestamp time2 = TimeUtils.getRandomTimeByUserActive(time1);
//			System.out.println(TimeUtils.getTimeString(time2));
			Assert.assertTrue(time2.getTime() >= TimeUtils.getDayStartTimestamp(time1).getTime());
			Assert.assertTrue(time2.getTime() < TimeUtils.getDayEndTimestamp(time1).getTime());
		}
	}
	
	@Test
	public void getCalendar(){
		for(int i = 1; i < 31; i ++){
			Calendar calendar = Calendar.getInstance();
			Timestamp timestamp = TimeUtils.getTimestamp("1984-08-" + (i < 10 ? "0" + i : i) + " 00:01:01");
			calendar.setTime(timestamp);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			Assert.assertEquals(i, day);
		}
	}
	
	@Test
	public void testGetGMTTimeString(){
		String expectedTimeString = "Wed, 18 Jan 2017 07:26:06 GMT";
		Timestamp time = TimeUtils.getTimestamp("2017-01-18 15:26:06");
		String timeString = TimeUtils.getGMTTimeString(time);
		Assert.assertEquals(expectedTimeString, timeString);
	}
	
	@Test
	public void testGetGMTTimestamp(){
		String timeString = "Wed, 18 Jan 2017 07:26:06 GMT";
		Timestamp time1 = TimeUtils.getTimestamp("2017-01-18 15:26:06");
		Timestamp time2 = TimeUtils.getGMTTimestamp(timeString);
		Assert.assertEquals(time1, time2);
	}

	@Test
	public void testGetUTCTimestamp(){
		String timeString = "May 31, 2018 10:41 AM UTC";
		Timestamp time1 = TimeUtils.getTimestamp("2018-05-31 18:41:00");
		Timestamp time2 = TimeUtils.getUTCTimestamp(timeString);
		Assert.assertEquals(time1, time2);

        timeString = "May 31, 2018 10:41 PM UTC";
        time1 = TimeUtils.getTimestamp("2018-06-01 06:41:00");
        time2 = TimeUtils.getUTCTimestamp(timeString);
        Assert.assertEquals(time1, time2);
	}

	@Test
	public void testGetTimestamp1(){
		String timeString = "Jul-03-2018 12:51:50 PM";
		String format = "MMM-dd-yyyy hh:mm:ss a";
		Timestamp time = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            time = new Timestamp(sdf.parse(timeString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
		Assert.assertNotNull(time);
	}
	
	@Test
	public void testGetMaxDayOfLastMonth(){
		Timestamp time = TimeUtils.getTimestamp("1984-09-02 00:01:01");
		Assert.assertEquals(31, TimeUtils.getMaxDayOfLastMonth(time));
		
		time = TimeUtils.getTimestamp("1984-01-02 00:01:01");
		Assert.assertEquals(31, TimeUtils.getMaxDayOfLastMonth(time));
		
		time = TimeUtils.getTimestamp("1984-03-02 00:01:01");
		Assert.assertEquals(29, TimeUtils.getMaxDayOfLastMonth(time));
		
		time = TimeUtils.getTimestamp("1985-03-02 00:01:01");
		Assert.assertEquals(28, TimeUtils.getMaxDayOfLastMonth(time));
		
		time = TimeUtils.getTimestamp("1984-05-02 00:01:01");
		Assert.assertEquals(30, TimeUtils.getMaxDayOfLastMonth(time));
	}
	
	@Test
	public void testGetDaysDiff(){
		Timestamp time1 = TimeUtils.getTimestamp("1984-09-02 00:00:00");
		Timestamp time2 = TimeUtils.getTimestamp("1984-09-02 11:59:01");
		int diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(0, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-02 12:10:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(0, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-02 23:59:59");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(0, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-03 00:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(1, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-03 12:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(1, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-03 23:59:59");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(1, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-04 00:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(2, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-04 11:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(2, diff);
		
		time2 = TimeUtils.getTimestamp("1984-09-04 23:10:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(2, diff);
		
		time2 = TimeUtils.getTimestamp("1984-10-04 00:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(32, diff);
		
		time2 = TimeUtils.getTimestamp("1984-10-04 11:00:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(32, diff);
		
		time2 = TimeUtils.getTimestamp("1984-10-04 23:10:01");
		diff = TimeUtils.getDaysDiff(time1.getTime(), time2.getTime());
		Assert.assertEquals(32, diff);
	}
	
	@Test
	public void testGetTimestamp(){
		System.out.println(TimeUtils.getTimestamp("2017-06-08 07:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-08 08:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-08 19:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-08 20:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-08 12:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-08 13:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-09 07:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-09 08:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-09 19:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-09 20:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-09 12:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-09 13:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-10 07:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-10 08:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-10 19:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-10 20:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-10 12:59:59").getTime());
		System.out.println(TimeUtils.getTimestamp("2017-06-10 13:00:00").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-05-08 07:59:59").getTime());
		
		System.out.println(TimeUtils.getTimestamp("2017-06-30 13:10:12").getTime());
	}
	
	@Test
	public void testGetWeekIndexOfDate(){
		Timestamp time = TimeUtils.getTimestamp("2017-08-14 01:02:12");
		Assert.assertEquals(2, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-15 01:02:12");
		Assert.assertEquals(3, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-16 01:02:12");
		Assert.assertEquals(4, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-17 01:02:12");
		Assert.assertEquals(5, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-18 01:02:12");
		Assert.assertEquals(6, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-19 01:02:12");
		Assert.assertEquals(7, TimeUtils.getWeekIndexOfDate(time));
		
		time = TimeUtils.getTimestamp("2017-08-20 01:02:12");
		Assert.assertEquals(1, TimeUtils.getWeekIndexOfDate(time));
	}
}
