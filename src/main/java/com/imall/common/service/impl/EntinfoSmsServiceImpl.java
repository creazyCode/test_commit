package com.imall.common.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.imall.common.domain.Sms;
import com.imall.common.utils.EntinfoSmsUtils;

public class EntinfoSmsServiceImpl extends BaseSmsServiceImpl {

	public static final Logger log = LoggerFactory.getLogger(EntinfoSmsServiceImpl.class);

	@Value("${entinfo.sn}")
	private String entinfoSn;
	@Value("${entinfo.pwd}")
	private String entinfoPwd;
	
	@Override
	public boolean doSendSms(Sms sms) {
		EntinfoSmsUtils.sendSms(entinfoSn, entinfoPwd, 
				sms.getCellphone(), sms.getContent());
		return true;
	}

	@Override
	public boolean doSendVoice(Sms sms) {
		return false;
	}
	
	public String getEntinfoSn() {
		return entinfoSn;
	}

	public void setEntinfoSn(String entinfoSn) {
		this.entinfoSn = entinfoSn;
	}
}
