package com.imall.common.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.imall.common.domain.Sms;
import com.imall.common.service.ISmsService;

/**
 * 用于群发短信
 * 
 * @author jianxunji
 *
 */
public class Sms512688ServiceImpl extends BaseSmsServiceImpl {

	public static final Logger log = LoggerFactory.getLogger(Sms512688ServiceImpl.class);
	@Autowired
	@Qualifier("smsChineseServiceImpl")
	private ISmsService smsChineseService;
	
	@Value("${sms.512688.corpId}")
	private String smsCorpId;
	@Value("${sms.512688.password}")
	private String smsPassword;
	
	@Override
	public boolean doSendSms(Sms sms) {
		smsChineseService.setAsync(this.isAsync());
		if(sms != null && (sms.getCellphones() == null || sms.getCellphones().size() == 0)){
			smsChineseService.sendSms(sms);
		}else if(sms != null && sms.getCellphones() != null && sms.getCellphones().size() > 0){
			for(String cellphone : sms.getCellphones()){
				Sms sendSms = new Sms();
				sendSms.setCellphone(cellphone);
				sendSms.setContent(sms.getContent());
				
				smsChineseService.sendSms(sendSms);
			}
		}
		return true;
	}

	@Override
	public boolean doSendVoice(Sms sms) {
		return false;
	}

	public String getSmsCorpId() {
		return smsCorpId;
	}

	public void setSmsCorpId(String smsCorpId) {
		this.smsCorpId = smsCorpId;
	}

	public String getSmsPassword() {
		return smsPassword;
	}

	public void setSmsPassword(String smsPassword) {
		this.smsPassword = smsPassword;
	}
}
