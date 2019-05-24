package com.imall.common.utils.sms;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class SmsChineseUtils {
	
	public static boolean sendSms(String uid, String key, String cellphoneNumber, String smsText){
		try {
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod("http://utf8.sms.webchinese.cn");
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf8");// 在头文件中设置转码
			NameValuePair[] data = { new NameValuePair("Uid", uid),
					new NameValuePair("Key", key),
					new NameValuePair("smsMob", cellphoneNumber),
					new NameValuePair("smsText", smsText) };
			post.setRequestBody(data);
			client.executeMethod(post);
			Header[] headers = post.getResponseHeaders();
			int statusCode = post.getStatusCode();
			System.out.println("statusCode:" + statusCode);
			for (Header h : headers) {
				System.out.println(h.toString());
			}
			String result = new String(post.getResponseBodyAsString().getBytes("utf8"));
			System.out.println(result);
			post.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
//		NameValuePair[] data = { new NameValuePair("Uid", "本站用户名"),
//		new NameValuePair("Key", "接口安全密码"),
//		new NameValuePair("smsMob", "手机号码"),
//		new NameValuePair("smsText", "短信内容") };
		
		String uid = "yanghui320";
		String key = "b592ef4e73a3c523f36f";
		//String cellphoneNumber = "13911015488";
		//String cellphoneNumber = "13581856018";
		String cellphoneNumber = "15901124203";
		String smsText = "验证码：123456，10分钟以后过期，请勿告诉他人，感谢您使用服务。";
		
		SmsChineseUtils.sendSms(uid, key, cellphoneNumber, smsText);
	}

}