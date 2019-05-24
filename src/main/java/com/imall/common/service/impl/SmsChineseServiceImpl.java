package com.imall.common.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.imall.common.domain.Sms;
import com.imall.common.utils.sms.SmsChineseUtils;

public class SmsChineseServiceImpl extends BaseSmsServiceImpl {

	public static final Logger log = LoggerFactory.getLogger(SmsChineseServiceImpl.class);

	@Value("${sms.chinese.uid}")
	private String smsChineseUid;
	@Value("${sms.chinese.key}")
	private String smsChineseKey;
	
	@Override
	public boolean doSendSms(Sms sms) {
		SmsChineseUtils.sendSms(smsChineseUid, smsChineseKey, 
				sms.getCellphone(), sms.getContent());
		return true;
	}

	@Override
	public boolean doSendVoice(Sms sms) {
		return false;
	}
	
	public String getSmsChineseUid() {
		return smsChineseUid;
	}

	public void setSmsChineseUid(String smsChineseUid) {
		this.smsChineseUid = smsChineseUid;
	}

	public String getSmsChineseKey() {
		return smsChineseKey;
	}

	public void setSmsChineseKey(String smsChineseKey) {
		this.smsChineseKey = smsChineseKey;
	}
}
