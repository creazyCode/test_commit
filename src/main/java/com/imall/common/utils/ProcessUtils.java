package com.imall.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);
	
	/**
	 * 获得当前线程名称
	 * 
	 * @return
	 */
	public static String getCurrentThreadName(){
		return Thread.currentThread().getName();
	}
	
	/**
	 * 获得当前pid
	 * 
	 * @return
	 */
	public static String getCurrentPid(){
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		return name.split("@")[0];
	}
	
	/**
	 * 判断某一个pid是否存在
	 * 
	 * @param pid
	 * @return
	 */
	public static boolean checkPidExist(String pid){
		BufferedReader bufferedReader = null;
		Process process = null;
		try{
			process = Runtime.getRuntime().exec("ps -ef | grep " + pid);
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = 0;
			while ((line = bufferedReader.readLine()) != null) {
				index ++;
			}
			if(index > 1){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return false;
	}
	
	public static boolean killPid(String pid){
		try{
			Runtime.getRuntime().exec("kill -9 " + pid);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
