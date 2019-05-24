package com.imall.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeviceUtils {

	/**
	 * 生成随机的IMEI
	 * 
	 * @return
	 */
	public static String generateRandomSN(){
		String sn = ObjectUtils.generateRandomCode(12);
		sn = sn.toUpperCase();
		return sn;
	}
	
	/**
	 * 生成随机的IMEI
	 * 
	 * @return
	 */
	public static String generateRandomIEMI(){
		String imeiWithoutChecksum = "86" + ObjectUtils.generateRandomIntegerCode(12);
		String imei = imeiWithoutChecksum + Luhn.get(imeiWithoutChecksum);
		return imei;
	}
	
	/**
	 * 生成随机的mac地址
	 * 
	 * @return
	 */
	public static String generateRandomMac(){
		StringBuilder mac = new StringBuilder();
		String[] str = {"1","2","3","4","5","6","7","8","9","0","a","b","c","d","e","f"};
		for(int i = 1; i < 13; i ++){
			mac.append(str[ObjectUtils.getRandom(0, 15)]);
			if(i > 0 && i % 2 == 0 && i != 12){
				mac.append(":");
			}
		}
		return mac.toString();
	}
	
	/**
	 * 生成随机的IP地址
	 * 
	 * @return
	 */
	public static String generateRandomIP(){
		StringBuilder ip = new StringBuilder();
		ip.append(ObjectUtils.getRandom(1, 254));
		ip.append(".");
		ip.append(ObjectUtils.getRandom(0, 254));
		ip.append(".");
		ip.append(ObjectUtils.getRandom(0, 254));
		ip.append(".");
		ip.append(ObjectUtils.getRandom(0, 254));
		return ip.toString();
	}
	
	/**
	 * 生成随机的android build string
	 * 
	 * @return
	 */
	public static String generateRandomAndroidBuildString(){
		return ObjectUtils.generateRandomCode(6).toUpperCase();
	}
	
	/**
	 * 生成随机的android api level
	 * 
	 * @return
	 */
	public static int generateRandomAndroidAPILevel(){
		List<Integer> apiLevels = new ArrayList<Integer>(DeviceConstants.ANDROID_VERSIONS.values());
		return apiLevels.get(ObjectUtils.getRandom(0, apiLevels.size() - 1));
	}
	
	/**
	 * 获得android api level
	 * 
	 * @return
	 */
	public static int getAndroidAPILevel(String androidVersion){
		int apiLevel = generateRandomAndroidAPILevel();
		if(androidVersion != null){
			Integer temp = DeviceConstants.ANDROID_VERSIONS.get(androidVersion);
			if(temp != null){
				apiLevel = temp;
			}
		}
		return apiLevel;
	}
	
	/**
	 * 生成随机的android 手机型号
	 * 
	 * @return
	 */
	public static String generateRandomAndroidVersion(){
		Set<String> versions = DeviceConstants.ANDROID_VERSIONS.keySet();
		List<String> versionsList = new ArrayList<String>(versions);
		return versionsList.get(ObjectUtils.getRandom(0, versionsList.size() - 1));
	}
	
	/**
	 * 生成随机的android 手机型号
	 * 
	 * @return
	 */
	public static String generateRandomAndroidVersionBak(){
		int[] firstVersions = {4, 5, 6};
		int[] sencondVersions = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		int[] threadVersions = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		StringBuilder sb = new StringBuilder();
		sb.append(firstVersions[ObjectUtils.getRandom(0, firstVersions.length - 1)]);
		sb.append(".");
		sb.append(sencondVersions[ObjectUtils.getRandom(0, sencondVersions.length - 1)]);
		sb.append(".");
		sb.append(threadVersions[ObjectUtils.getRandom(0, threadVersions.length - 1)]);
		return sb.toString();
	}
	
	public static AndroidDevice getSomeBrandAndroidDevice(String brand){
		AndroidDevice device = null;
		if(brand != null){
			List<AndroidDevice> devices = DeviceConstants.BRAND_DEVICES.get(brand);
			if(devices != null && devices.size() > 0){
				device = devices.get(ObjectUtils.getRandom(0, devices.size() - 1));
				device = new AndroidDevice(device.getBrand(), device.getModel());
				device.generateInfo();
			}
		}
		if(device == null){
			device = getRandomAndroidDevice("");
		}
		return device;
	}
	
	public static AndroidDevice getRandomAndroidDevice(String excludeBrand){
		Set<String> excludeBrands = null;
		if(excludeBrand != null){
			excludeBrands = new HashSet<String>();
			excludeBrands.add(excludeBrand);
		}
		return getRandomAndroidDevice(excludeBrands);
	}
	
	public static AndroidDevice getRandomAndroidDevice(Set<String> excludeBrands){
		Map<String, Integer> existBrandShare = new LinkedHashMap<String, Integer>();
		Set<String> allBrands = DeviceConstants.BRAND_MARKET_SHARE.keySet();
		int total = 0;
		AndroidDevice device = null;
		AndroidDevice temp = null;
		for(String brand : allBrands){
			if(excludeBrands != null && excludeBrands.contains(brand)){
				continue;
			}
			List<AndroidDevice> devices = DeviceConstants.BRAND_DEVICES.get(brand);
			Integer share = DeviceConstants.BRAND_MARKET_SHARE.get(brand);
			if(devices != null && devices.size() > 0 && share != null && share > 0){
				total += share;
				existBrandShare.put(brand, total);
			}
		}
		int random = ObjectUtils.getRandom(0, total);
		allBrands = existBrandShare.keySet();
		for(String brand : allBrands){
			int share = existBrandShare.get(brand);
			if(random <= share){
				List<AndroidDevice> devices = DeviceConstants.BRAND_DEVICES.get(brand);
				if(devices.size() == 1){
					temp = devices.get(0);
				}else{
					temp = devices.get(ObjectUtils.getRandom(0, devices.size() - 1));
				}
				break;
			}
		}
		if(temp == null){
			temp = DeviceConstants.BRAND_DEVICES.get(DeviceConstants.BRAND_NAME_VIVO).get(1);
		}
		device = new AndroidDevice(temp.getBrand(), temp.getModel());
		device.generateInfo();
		return device;
	}
	
	public static Set<String> getRandomInstalledApps(int number){
		Set<String> randomInstalledApps = new HashSet<String>();
		int allAppsSize = DeviceConstants.APP_PACKAGE_NAMES.size();
		if(number >= allAppsSize){
			randomInstalledApps.addAll(DeviceConstants.APP_PACKAGE_NAMES);
		}else{
			for(int i = 0; i < number; i ++){
				for(int j = 0; j < 1000; j ++){
					int random = ObjectUtils.getRandom(0, allAppsSize - 1);
					String packageName = DeviceConstants.APP_PACKAGE_NAMES.get(random);
					if(!randomInstalledApps.contains(packageName)){
						randomInstalledApps.add(packageName);
						break;
					}
				}
			}
		}
		return randomInstalledApps;
	}

	public static String getIOSDevice(String deviceSystemInfo){
		return DeviceConstants.IOS_DEVICE_SYSTEMS.get(deviceSystemInfo);
	}
}
