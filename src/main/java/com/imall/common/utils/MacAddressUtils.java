package com.imall.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class MacAddressUtils {
	/**
	 * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,linux 等.
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * 获取unix网卡的mac地址. 非windows的系统默认调用本方法获取. 如果有特殊系统请继续扩充新的取mac地址方法.
	 * 
	 * @return mac地址
	 */
	public static String getUnixMACAddress() {
		String mac = null;
		try {
			mac = getMac("ifconfig eno1");
			if(mac == null){
				mac = getMac("ifconfig eth0");
				if(mac == null){
					mac = getMac("ifconfig en1");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return mac;
	}
	
	private static String getMac(String command) throws IOException{
		String mac = null;
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader bufferedReader = null;
		try{
			// 显示信息中包含有mac地址信息
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				// 寻找标示字符串[hwaddr]
				index = line.toLowerCase().indexOf("ether");
				if (index >= 0) {// 找到了
					// 取出mac地址并去除2边空格
					mac = line.substring(index + "ether".length() + 1).trim();
					if(mac != null && mac.contains(" ")){
						mac = mac.substring(0, mac.indexOf(" "));
					}
					break;
				}
			}
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
		return mac;
	}

	/**
	 * 获取widnows网卡的mac地址.
	 * 
	 * @return mac地址
	 */
	public static String getWindowsMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// windows下的命令，显示信息中包含有mac地址信息
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				// 寻找标示字符串[physical
				index = line.toLowerCase().indexOf("physical address");
				if (index >= 0) {// 找到了
					index = line.indexOf(":");// 寻找":"的位置
					if (index >= 0) {
						// 取出mac地址并去除2边空格
						mac = line.substring(index + 1).trim();
					}
					break;
				}
			}
		} catch (IOException e) {
//			System.out.println("widnows方式未获取到网卡地址");
		} finally {
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
		return mac;
	}

	/**
	 * windows 7 专用 获取MAC地址
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getWindows7MACAddress() {
		StringBuffer sb = new StringBuffer();
		try {
			// 获取本地IP对象
			InetAddress ia = InetAddress.getLocalHost();
			// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
			byte[] mac = NetworkInterface.getByInetAddress(ia)
					.getHardwareAddress();
			// 下面代码是把mac地址拼装成String
			for (int i = 0; i < mac.length; i++) {
				// mac[i] & 0xFF 是为了把byte转化为正整数
				String s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
		} catch (Exception ex) {
//			System.out.println("windows 7方式未获取到网卡地址");
		}
		return sb.toString();
	}

	/**
	 * 获取MAC地址
	 * 
	 * @throws Exception
	 */
	public static String getMACAddress() {
		// unix
		String mac = getUnixMACAddress();
		// windows7
		if (isNull(mac)) {
			mac = getWindows7MACAddress();
		}
		// windows
		if (isNull(mac)) {
			mac = getWindowsMACAddress();
		}

		if (!isNull(mac)) {
			mac = mac.replace("-", "").replaceAll(":", "");
			return mac.toLowerCase();
		}else{
			return null;
		}
	}

	public static boolean isNull(Object strData) {
		if (strData == null || String.valueOf(strData).trim().equals("")) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(getMACAddress());
	}
}