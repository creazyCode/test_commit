package com.imall.common.utils.sms;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sms512688Utils {
	public static final Logger LOGGER = LoggerFactory.getLogger(Sms512688Utils.class);
	
	/**
	 * @param corpId
	 * @param password
	 * @param cellphoneNumbers 用逗号分隔的电话号码，最多500个
	 * @param smsText
	 * @return
	 */
	public static String sendSms(String corpId, String password, List<String> cellphoneNumbers, String smsText){
		try {
			if(cellphoneNumbers == null || cellphoneNumbers.size() == 0){
				return "-1";
			}
			StringBuilder phoneNumberString = new StringBuilder("");
			for(String cellphone : cellphoneNumbers){
				phoneNumberString.append(cellphone);
				phoneNumberString.append(",");
			}
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod("http://www.512688.com/ws/BatchSend.aspx");
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
			String pns = phoneNumberString.substring(0, phoneNumberString.length() - 1);
			NameValuePair[] data = { 
						new NameValuePair("CorpID", corpId),
						new NameValuePair("Pwd", password),
						new NameValuePair("Mobile", pns),
						new NameValuePair("Content", smsText)
					};
			post.setRequestBody(data);
			client.executeMethod(post);
			Header[] headers = post.getResponseHeaders();
			int statusCode = post.getStatusCode();
			String result = new String(post.getResponseBodyAsString().getBytes("utf8"));
			LOGGER.debug("send SMS to {}, result {}", new Object[]{pns, result});
			post.releaseConnection();
			return result;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "-1";
	}
	
	public static void main(String[] args) throws Exception {
//		NameValuePair[] data = { new NameValuePair("Uid", "本站用户名"),
//		new NameValuePair("Key", "接口安全密码"),
//		new NameValuePair("smsMob", "手机号码"),
//		new NameValuePair("smsText", "短信内容") };
		
		String uid = "reinsch";
		String key = "ee3bef11139ad73184dd";
		String cellphoneNumber = "18610133586";
		String smsText = "验证码：123456，10分钟以后过期，请勿告诉他人，感谢您使用【味来】服务。";
		
//		Sms512688Utils.sendSms(uid, key, cellphoneNumber, smsText);
	}

}