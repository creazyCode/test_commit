package com.imall.common.service;

import com.imall.common.domain.Sms;

/**
 * @author jianxunji
 *
 */
public interface ISmsService {
	/**
	 * @param sms
	 * @return
	 */
	public boolean sendSms(Sms sms);
	
	/**
	 * @param sms
	 * @return
	 */
	public boolean sendVoice(Sms sms);
	
	/**
	 * @return
	 */
	public boolean isAsync();

	/**
	 * @param isAsync
	 */
	public void setAsync(boolean isAsync);
}
