package com.imall.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import com.imall.common.domain.Sms;
import com.imall.common.service.ISmsService;

/**
 * @author jason
 *
 */
public abstract class BaseSmsServiceImpl implements ISmsService {
	protected boolean isAsync = true;
	@Autowired
	@Qualifier("taskExecutor")
	protected TaskExecutor taskExecutor;
	
	@Override
	public boolean sendSms(final Sms sms) {
		if(sms == null){
			return true;
		}
		sms.processCellphones();
		boolean successful = false;
		if(this.isAsync()){
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					doSendSms(sms);
				}
			});
			successful = true;
		}else{
			successful = doSendSms(sms);
		}
		return successful;
	}
	
	
	public abstract boolean doSendSms(Sms sms);
	
	@Override
	public boolean sendVoice(final Sms sms) {
		if(sms == null){
			return true;
		}
		sms.processCellphones();
		boolean successful = false;
		if(this.isAsync()){
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					doSendVoice(sms);
				}
			});
			successful = true;
		}else{
			successful = doSendVoice(sms);
		}
		return successful;
	}
	
	public abstract boolean doSendVoice(Sms sms);
	
	@Override
	public boolean isAsync() {
		return isAsync;
	}
	
	@Override
	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}
}
