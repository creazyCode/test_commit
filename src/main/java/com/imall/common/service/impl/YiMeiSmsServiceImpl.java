package com.imall.common.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.imall.common.domain.Sms;
import com.imall.common.utils.sms.SmsYiMeiUtils;

public class YiMeiSmsServiceImpl extends BaseSmsServiceImpl {

	public static final Logger log = LoggerFactory.getLogger(YiMeiSmsServiceImpl.class);

	@Value("${yimei.serial.number}")
	private String yiMeiSerialNumber;
	@Value("${yimei.password}")
	private String yiMeiPassword;
	
	@Value("${yimei.serial.number.voice}")
	private String yiMeiSerialNumberVoice;
	@Value("${yimei.password.voice}")
	private String yiMeiPasswordVoice;
	
	@Override
	public boolean doSendSms(Sms sms) {
		SmsYiMeiUtils.sendSms(yiMeiSerialNumber, yiMeiPassword, 
				sms.getCellphones(), sms.getContent(), sms.getCode());
		return true;
	}

	@Override
	public boolean doSendVoice(Sms sms) {
		SmsYiMeiUtils.sendVoice(yiMeiSerialNumberVoice, yiMeiPasswordVoice, 
				sms.getCellphones(), sms.getContent(), sms.getCode());
		return true;
	}
	
	public String getYiMeiSerialNumber() {
		return yiMeiSerialNumber;
	}

	public void setYiMeiSerialNumber(String yiMeiSerialNumber) {
		this.yiMeiSerialNumber = yiMeiSerialNumber;
	}

	public String getYiMeiPassword() {
		return yiMeiPassword;
	}

	public void setYiMeiPassword(String yiMeiPassword) {
		this.yiMeiPassword = yiMeiPassword;
	}

	public String getYiMeiSerialNumberVoice() {
		return yiMeiSerialNumberVoice;
	}

	public void setYiMeiSerialNumberVoice(String yiMeiSerialNumberVoice) {
		this.yiMeiSerialNumberVoice = yiMeiSerialNumberVoice;
	}

	public String getYiMeiPasswordVoice() {
		return yiMeiPasswordVoice;
	}

	public void setYiMeiPasswordVoice(String yiMeiPasswordVoice) {
		this.yiMeiPasswordVoice = yiMeiPasswordVoice;
	}
}
