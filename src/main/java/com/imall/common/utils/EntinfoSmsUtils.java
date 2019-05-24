package com.imall.common.utils;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jason
 *
 */
public class EntinfoSmsUtils{
	public static final Logger LOGGER = LoggerFactory.getLogger(EntinfoSmsUtils.class);
	
	/**
	 * @param sn
	 * @param pwd
	 * @param cellphone
	 * @param smsText
	 * @return
	 */
	public static boolean sendSms(String sn, String pwd, String cellphone, String smsText){
    	try {
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod("http://sdk105.entinfo.cn/z_mdsmssend.aspx");
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
			NameValuePair[] data = { 
					new NameValuePair("sn", sn),
					new NameValuePair("pwd", ObjectUtils.getMD5HexForSms(pwd)),
					new NameValuePair("mobile", cellphone),
					new NameValuePair("content", smsText) };
			post.setRequestBody(data);
			client.executeMethod(post);
			Header[] headers = post.getResponseHeaders();
			int statusCode = post.getStatusCode();
			String result = new String(post.getResponseBodyAsString().getBytes(
					"utf8"));
			post.releaseConnection();
			LOGGER.debug("send sms to {}, {}, {}", new Object[]{cellphone, smsText, result});
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
    }
}
