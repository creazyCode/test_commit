package com.imall.common.service;

import com.imall.common.domain.Email;

public interface IMailService {
	/**
	 * 同步发送邮件
	 *
	 * @param email
	 * @return
	 */
	public boolean sendEmail(Email email);

	/**
	 * 异步发送邮件
	 *
	 * @param email
	 */
	public void sendEmailAsync(Email email);

	/**
	 * @description 给内部员工发送系统邮件
	 * @method sendSystemEmail
	 * @param [subject, content, isToDev, isToAudit, address]
	 * @return
	 * @time 2018/12/3
	 * @author tianxiang@insightchain.io
	 */
	public boolean sendSystemEmail(String subject, String content, Boolean isToDev, Boolean isToAudit, String[] address );

	/**
	 * @description 给内部员工发送系统邮件-sync
	 * @method sendSystemEmailAsync
	 * @param [subject, content, isToDev, isToAudit, address]
	 * @return
	 * @time 2019/1/10
	 * @author tianxiang@insightchain.io
	 */
	void sendSystemEmailAsync(String subject, String content, Boolean isToDev, Boolean isToAudit, String[] address );
}
