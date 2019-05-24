package com.imall.common.utils;

import java.text.DecimalFormat;

public class MathUtils {
	/**
	 * 计算经纬度距离
	 * @param lat1
	 * @param lat2
	 * @param lon1
	 * @param lon2
	 * @return 单位千米
	 */
	public static double getDistance(double lat1, double lat2, double lon1, double lon2) { 
        double R = 6371; 
        double distance = 0.0; 
        double dLat = (lat2 - lat1) * Math.PI / 180; 
        double dLon = (lon2 - lon1) * Math.PI / 180; 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) 
                + Math.cos(lat1 * Math.PI / 180) 
                * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) 
                * Math.sin(dLon / 2); 
        distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R; 
        return distance; 
    }
	
	/**
	 * seed1获得8进制+9，+seed2获得8进制+9，最后用时间戳补齐
	 * 
	 * @param digitsNumber
	 * @param seed1
	 * @param seed1DigitsNumber
	 * @param seed2
	 * @param seed2DigitsNumber
	 * @return
	 */
	public static String generateRandomNumberString(int digitsNumber, 
			Long seed1, int seed1DigitsNumber, 
			Long seed2, int seed2DigitsNumber){
		StringBuilder sb = new StringBuilder();
		sb.append(getDigitsString(seed1, seed1DigitsNumber));
		sb.append("9");
		sb.append(getDigitsString(seed2, seed2DigitsNumber));
		sb.append("9");
		String timeString = String.valueOf(System.currentTimeMillis());
		sb.append(timeString.substring(
				timeString.length() - (digitsNumber - seed1DigitsNumber - seed2DigitsNumber - 2), 
				timeString.length()));
		return sb.toString();
	}
	
	/**
	 * @param seed
	 * @param seedDigitsNumber
	 * @return
	 */
	private static String getDigitsString(Long seed, int seedDigitsNumber){
		String octalString1 = Long.toOctalString(seed);
		if(octalString1.length() > seedDigitsNumber){
			octalString1 = octalString1.substring(octalString1.length() - seedDigitsNumber, 
					octalString1.length());
		}else if(octalString1.length() < seedDigitsNumber){
			octalString1 = ObjectUtils.appendZeros(octalString1, seedDigitsNumber);
		}
		return octalString1;
	}
	
	/**
	 * @param val
	 * @param precision
	 * @return
	 */
	public static Double roundDouble(double val, int precision) {
		Double ret = null;
		try {
			double factor = Math.pow(10, precision);
			ret = Math.floor(val * factor + 0.5) / factor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 保留 4位小数
	 * @author xy_wu
	 *
	 * @param doubleValue
	 * @return
	 */
	public static Double keep4Decimal(double doubleValue) {
        // 保留5位小数
        DecimalFormat df = new DecimalFormat("0.00000");
        String result = df.format(doubleValue);
        // 获取小数 . 号第一次出现的位置
        int index = result.indexOf(".");
        // 字符串截断
        String substring = result.substring(0, index + 5);
        Double newDouble = Double.valueOf(substring);
        if(newDouble > doubleValue){
        	newDouble = newDouble - 0.0001;
        }
        return newDouble;
    }
	
	public static Double keep2Decimal(double doubleValue) {
		// 保留3位小数
		DecimalFormat df = new DecimalFormat("0.000");
		String result = df.format(doubleValue);
		// 获取小数 . 号第一次出现的位置
		int index = result.indexOf(".");
		// 字符串截断
		String substring = result.substring(0, index + 3);
		Double newDouble = Double.valueOf(substring);
		if(newDouble > doubleValue){
			newDouble = newDouble - 0.01;
		}
		return newDouble;
	}
}
