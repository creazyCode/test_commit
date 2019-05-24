package com.imall.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.validator.internal.constraintvalidators.hv.LuhnCheckValidator;
import org.junit.Before;
import org.junit.Test;

public class DeviceUtilsTest {
	
	@Before
	public void init(){
		
	}
	
	@Test
	public void testGetRandomAndroidDevice() {
		AndroidDevice device;
		for(int i = 0; i < 1000; i ++){
			device = DeviceUtils.getRandomAndroidDevice("");
//			System.out.println(device.getBrandModel());
			this.assertNotNull(device);
			Assert.assertTrue(!device.getBrandModel().contains("unknow"));
		}
		for(int i = 0; i < 1000; i ++){
			device = DeviceUtils.getRandomAndroidDevice("");
//			System.out.println(device.getBrandModel());
			this.assertNotNull(device);
			Assert.assertTrue(!device.getBrandModel().contains("unknow"));
		}
		for(int i = 0; i < 1000; i ++){
			Set<String> excludeBrands = new HashSet<String>();
			excludeBrands.add(DeviceConstants.BRAND_NAME_SAMSUNG);
			device = DeviceUtils.getRandomAndroidDevice(excludeBrands);
//			System.out.println(device.getBrandModel());
			this.assertNotNull(device);
			Assert.assertTrue(!device.getBrandModel().contains("unknow"));
			Assert.assertTrue(!device.getBrandModel().contains(DeviceConstants.BRAND_NAME_SAMSUNG));
		}
		for(int i = 0; i < 1000; i ++){
			device = DeviceUtils.getRandomAndroidDevice(DeviceConstants.BRAND_NAME_OPPO);
//			System.out.println(device.getBrandModel());
			this.assertNotNull(device);
			Assert.assertTrue(!device.getBrandModel().contains("unknow"));
			Assert.assertTrue(!device.getBrandModel().contains(DeviceConstants.BRAND_NAME_OPPO));
		}
	}
	
	@Test
	public void testGetRandomAndroidDeviceWithInfoRandom() {
		List<AndroidDevice> devices = new ArrayList<AndroidDevice>();
		AndroidDevice device;
		for(int i = 0; i < 1000; i ++){
			device = DeviceUtils.getRandomAndroidDevice("");
//			System.out.println(device.getBrandModel());
			this.assertNotNull(device);
			Assert.assertTrue(!device.getBrandModel().contains("unknow"));
			devices.add(device);
		}
		for(AndroidDevice temp1 : devices){
			for(AndroidDevice temp2 : devices){
				if(temp1 == temp2){
					continue;
				}
				Assert.assertTrue(!temp1.getBuildString().toLowerCase().equals(temp2.getBuildString().toLowerCase()));
				Assert.assertTrue(!temp1.getImei().toLowerCase().equals(temp2.getImei().toLowerCase()));
				Assert.assertTrue(!temp1.getMac().toLowerCase().equals(temp2.getMac().toLowerCase()));
//				Assert.assertTrue(!temp1.getVersion1().equals(temp2.getVersion1()));
//				Assert.assertTrue(!temp1.getVersion2().equals(temp2.getVersion2()));
//				Assert.assertTrue(!temp1.getApiLevel().equals(temp2.getApiLevel()));
			}
		}
	}
	
	private void assertNotNull(AndroidDevice device){
		Assert.assertNotNull(device);
		Assert.assertNotNull(device.getBrand());
		Assert.assertNotNull(device.getModel());
		Assert.assertNotNull(device.getBuildString());
		Assert.assertNotNull(device.getImei());
		Assert.assertNotNull(device.getMac());
		Assert.assertNotNull(device.getVersion1());
		Assert.assertNotNull(device.getVersion2());
		Assert.assertNotNull(device.getApiLevel());
	}
	
	@Test
	public void testGenerateRandomAndroidBuildString() {
		for(int i = 0; i < 1000; i ++){
			String build = DeviceUtils.generateRandomAndroidBuildString();
//			System.out.println(build);
		}
	}

	@Test
	public void testGenerateRandomAndroidVersion() {
		for(int i = 0; i < 1000; i ++){
			String androidVersion = DeviceUtils.generateRandomAndroidVersion();
//			System.out.println(androidVersion);
		}
	}
	
	@Test
	public void testGenerateRandomMac() {
		for(int i = 0; i < 1000; i ++){
			String mac = DeviceUtils.generateRandomMac();
//			System.out.println(mac);
		}
	}
	
	@Test
	public void testGetAndroidAPILevel() {
		for(int i = 0; i < 1000; i ++){
			String androidVersion = DeviceUtils.generateRandomAndroidVersion();
			Integer apiLevel = DeviceUtils.getAndroidAPILevel(androidVersion);
//			System.out.println(androidVersion + ": " + apiLevel);
			Assert.assertNotNull(apiLevel);
		}
	}
	
	@Test
	public void testGenerateRandomAndroidAPILevel() {
		for(int i = 0; i < 1000; i ++){
			int apiLevel = DeviceUtils.generateRandomAndroidAPILevel();
//			System.out.println(apiLevel);
		}
	}
	
	@Test
	public void testGenerateRandomIEMI1() {
		for(int i = 0; i < 1000; i ++){
			String imei = DeviceUtils.generateRandomIEMI();
			List<Integer> digits = new ArrayList<Integer>();
			char checkDigit = 0;
			for(int index = 0; index < imei.length(); index ++){
				if(index == imei.length() - 1){
					checkDigit = imei.charAt(index);
				}else{
					digits.add(Integer.valueOf(imei.substring(index, index + 1)));
				}
			}
			Assert.assertTrue(new LuhnCheckValidator().isCheckDigitValid(digits, checkDigit));
//			Assert.assertTrue(new LuhnValidator(2).passesLuhnTest(imei));
		}
	}
	
	@Test
	public void testGenerateRandomIEMI2() {
		for(int i = 0; i < 10000; i ++){
			String imei = "86880702" + ObjectUtils.generateRandomIntegerCode(6);
//			System.out.println(imei + Luhn.get(imei));
		}
	}
}
