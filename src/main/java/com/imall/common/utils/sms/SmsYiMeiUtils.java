package com.imall.common.utils.sms;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.b2m.eucp.sdkhttp.YiMeiClient;

/**
 * @author jianxunji
 *
 */
public class SmsYiMeiUtils {
	public static YiMeiClient clientSms;
	public static YiMeiClient clientVoice;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SmsYiMeiUtils.class);
	
	/**
	 * @param softwareSerialNo
	 * @param password
	 * @param cellphoneNumbers 用逗号分隔的电话号码，最多500个
	 * @param smsText
	 * @param code
	 * @return
	 */
	public static String sendSms(String softwareSerialNo, String password, 
			List<String> cellphoneNumbers, String smsText, String code){
		try {
			if(cellphoneNumbers == null || cellphoneNumbers.size() == 0){
				return "-1";
			}
			
			String[] cellphoneNumbesObject = new String[cellphoneNumbers.size()];
			int index = 0;
			for(String c : cellphoneNumbers){
				cellphoneNumbesObject[index] = c;
				index ++;
			}
			int result = getClient(softwareSerialNo, password, null).sendSMS(cellphoneNumbesObject, smsText, "", 5);
			
			LOGGER.debug("send SMS to {}, result {}", new Object[]{cellphoneNumbers.toString(), String.valueOf(result)});
			return String.valueOf(result);
		} catch (Exception e) {
			LOGGER.error("error in sendSms", e);
		}
		return "-1";
	}
	
	/**
	 * @param softwareSerialNo
	 * @param password
	 * @param cellphoneNumbers 用逗号分隔的电话号码，最多500个
	 * @param smsText
	 * @param code
	 * @return
	 */
	public static String sendVoice(String softwareSerialNo, String password, 
			List<String> cellphoneNumbers, String smsText, String code){
		try {
			if(cellphoneNumbers == null || cellphoneNumbers.size() == 0){
				return "-1";
			}
			String[] cellphoneNumbersArray = new String[cellphoneNumbers.size()];
			int index = 0;
			for(String cellphone : cellphoneNumbers){
				cellphoneNumbersArray[index] = cellphone;
				index ++;
			}
			String result = getClient(softwareSerialNo, password, 1).sendVoice(cellphoneNumbersArray, 
					code, "", "GBK", 5, System.currentTimeMillis());
			
			LOGGER.debug("send SMS voice to {}, result {}", new Object[]{cellphoneNumbers.toString(), result});
			return result;
		} catch (Exception e) {
			LOGGER.error("error in sendVoice", e);
		}
		return "-1";
	}
	
	public synchronized static YiMeiClient getClient(String softwareSerialNo, String password, Integer i){
		if(i != null && i == 1){ // 语音
			if(clientVoice == null){
				try {
					clientVoice = new YiMeiClient(softwareSerialNo, password, i);
					clientVoice.init();
				} catch (Exception e) {
					LOGGER.error("error in getClient", e);
				}
			}
			return clientVoice;
		}else{
			if(clientSms == null){
				try {
					clientSms = new YiMeiClient(softwareSerialNo, password, i);
					clientSms.initSms();
				} catch (Exception e) {
					LOGGER.error("error in getClient", e);
				}
			}
			
			return clientSms;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
//		NameValuePair[] data = { new NameValuePair("Uid", "本站用户名"),
//		new NameValuePair("Key", "接口安全密码"),
//		new NameValuePair("smsMob", "手机号码"),
//		new NameValuePair("smsText", "短信内容") };
		
//		String key = "8SDK-EMY-6699-RHRLQ";
//		String password = "166434";
		String key = "EUCP-EMY-SMS1-0DQOA";
		String password = "358328";
//		String key = "EUCP-EMY-VOC1-27LKN";
//		String password = "BCADBA16A2CC30E7";
		String cellphoneNumber = "15011186215";
		List<String> cellphoneNumbers = new ArrayList<String>();
		cellphoneNumbers.add(cellphoneNumber);
		String code = "187613";
		String smsText = "【InsightApp】Insight验证码：" + code + "，两个小时之后过期，请妥善保存。";
		SmsYiMeiUtils.sendSms(key, password, cellphoneNumbers, smsText, code);
//		SmsYiMeiUtils.sendVoice(key, password, cellphoneNumbers, smsText, code);
	}
	
}