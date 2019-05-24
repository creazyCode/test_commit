package com.imall.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.sf.json.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class IPUtil {

	/**
	 * 
	 * 此方法描述的是：
	 * 
	 * 地址：http://ip.taobao.com/ 提供的服务包括： 1.
	 * 根据用户提供的IP地址，快速查询出该IP地址所在的地理信息和地理相关的信息，包括国家、省、市和运营商。 2.
	 * 用户可以根据自己所在的位置和使用的IP地址更新我们的服务内容。 优势： 1. 提供国家、省、市、县、运营商全方位信息，信息维度广，格式规范。 2.
	 * 提供完善的统计分析报表，省准确度超过99.8%，市准确度超过96.8%，数据质量有保障。 接口说明 1. 请求接口（GET）：
	 * http://ip.taobao.com/service/getIpInfo.php?ip=[ip地址字串] 2. 响应信息：
	 * （json格式的）国家 、省（自治区或直辖市）、市（县）、运营商 3. 返回数据格式：
	 * {"code":0,"data":{"ip":"210.75.225.254"
	 * ,"country":"\u4e2d\u56fd","area":"\u534e\u5317",
	 * "region":"\u5317\u4eac\u5e02"
	 * ,"city":"\u5317\u4eac\u5e02","county":"","isp":"\u7535\u4fe1",
	 * "country_id"
	 * :"86","area_id":"100000","region_id":"110000","city_id":"110000",
	 * "county_id":"-1","isp_id":"100017"}} 其中code的值的含义为，0：成功，1：失败。
	 * @author: yanq
	 * @version: 2014-3-6 下午8:47:27
	 */
	public static String getCityName(String ip) {
		String cityName = "北京";
		String[] ipStr = ip.trim().split(",");
		if(ipStr != null && ipStr.length > 0){
			ip = ipStr[0];
		}
		String jsonUrl = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
		JSONObject json = cityNameFromUrl(jsonUrl);
		if (json != null && json.getInt("code") == 0) {
			String data = json.getString("data");
			JSONObject dataMap = JSONObject.fromObject(data);
			if (dataMap != null && dataMap.getString("country") != null
					&& dataMap.getString("country").contains("中国")) {
				cityName = dataMap.getString("city");
			}
		}
		return cityName;
	}

	public static String getCityNameOld(String ip) {
		String cityName = "";

		org.jsoup.nodes.Document doc = null;
		Connection con = null;

		// 连接 www.ip138.com 网页
		con = Jsoup.connect("http://wap.ip138.com/ip.asp?ip=" + ip + "")
				.timeout(10000);

		try {
			doc = con.get();

			// 获得包含本机ip的文本串：您的IP是：[xxx.xxx.xxx.xxx] 来自：中国
			org.jsoup.select.Elements els = doc.body().select("b");
			for (org.jsoup.nodes.Element el : els) {
				cityName = el.text();
			}

			// 从文本串过滤出ip，用正则表达式将非数字和.替换成空串""
			cityName = cityName.replaceAll("查询结果：", "");
			String[] str = cityName.split(" ");
			if (str != null && str.length > 0) {
				cityName = str[0];
				int index = cityName.indexOf("省");
				if (index != -1) {
					cityName = cityName.substring(index + 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			cityName = "北京";
			return cityName;
		}
		if (cityName.contains("本机")) {
			cityName = "北京";
		} else if ("".equals(cityName.trim())) {
			cityName = "北京";
		} else if ("无效的IP地址".equals(cityName.trim())) {
			cityName = "北京";
		}

		return cityName;
	}

	/**
	 * 得到本机的外网ip，出现异常时返回空串""
	 * 
	 * @return
	 */
	public static String getPublicIP() {
		String ip = "";

		org.jsoup.nodes.Document doc = null;
		Connection con = null;

		// 连接 www.ip138.com 网页
		con = Jsoup.connect("http://iframe.ip138.com/ic.asp").timeout(10000);

		try {
			doc = con.get();

			// 获得包含本机ip的文本串：您的IP是：[xxx.xxx.xxx.xxx] 来自：中国
			org.jsoup.select.Elements els = doc.body().select("center");
			for (org.jsoup.nodes.Element el : els) {
				ip = el.text();
			}

			// 从文本串过滤出ip，用正则表达式将非数字和.替换成空串""
			ip = ip.replaceAll("[^0-9.]", "");
		} catch (IOException e) {
			e.printStackTrace();
			return ip;
		}

		return ip;
	}

	public static JSONObject cityNameFromUrl(String jsonUrl) {
		URL url;
		String inputline = "";
		String info = "";
		JSONObject jsonob = null;
		try {
			url = new URL(jsonUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			InputStreamReader inStream = new InputStreamReader(
					conn.getInputStream(), "UTF-8");
			BufferedReader buffer = new BufferedReader(inStream);
			while ((inputline = buffer.readLine()) != null) {
				info += inputline;
			}
			jsonob = JSONObject.fromObject(info);
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonob;
	}

	/**
	 * @description 判断ip是否在某个网段内
	 * 		判断两个IP地址是不是在同一个网段，就将它们的IP地址分别与子网掩码做与运算，得到的结果一网络号，如果网络号相同，就在同一子网，否则，不在同一子网。
	 * @method isInRange
	 * @param [network, mask]
	 * @return
	 * @time 2019/4/28
	 * @author tianxiang@insightchain.io
	 */
	public static boolean isInRange(String network, String mask) {
		String[] networkips = network.split("\\.");
		int ipAddr = (Integer.parseInt(networkips[0]) << 24)
				| (Integer.parseInt(networkips[1]) << 16)
				| (Integer.parseInt(networkips[2]) << 8)
				| Integer.parseInt(networkips[3]);
		int type = Integer.parseInt(mask.replaceAll(".*/", ""));
		int mask1 = 0xFFFFFFFF << (32 - type);
		String maskIp = mask.replaceAll("/.*", "");
		String[] maskIps = maskIp.split("\\.");
		int cidrIpAddr = (Integer.parseInt(maskIps[0]) << 24)
				| (Integer.parseInt(maskIps[1]) << 16)
				| (Integer.parseInt(maskIps[2]) << 8)
				| Integer.parseInt(maskIps[3]);

		return (ipAddr & mask1) == (cidrIpAddr & mask1);
	}

	public static void main(String args[]){
		System.out.println(isInRange("100.121.146.58","100.64.0.0/10"));
		System.out.println(isInRange("100.121.21.226","100.64.0.0/10"));
		System.out.println(isInRange("100.121.146.57","100.64.0.0/10"));
		System.out.println(isInRange("100.121.145.225","100.64.0.0/10"));
	}
}
