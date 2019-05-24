package com.imall.common.service.impl;

import com.imall.common.domain.Email;
import com.imall.common.service.IMailService;
import com.imall.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

//@Service("mailServiceImpl")
public class SpringMailServiceImpl implements IMailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMailServiceImpl.class.getName());

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    //	@Autowired
//	@Qualifier("mailSender")
    private JavaMailSender mailSender;

    private String sender;

    private String senderName;

    @Value("${developers.email}")
    private String developersEmail;

    @Value("${auditors.email}")
    private String auditorsEmail;

    @Override
    public void sendEmailAsync(final Email email) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sendEmail(email);
            }
        });
    }

    @Override
    public boolean sendEmail(final Email email) {
        boolean success = false;
        if (email.getAddresses() == null || email.getAddresses().length == 0) {
            return success;
        }
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mime, true, "utf-8");
//			helper.setFrom(sender);// 发件人
            helper.setFrom(sender, senderName);
            helper.setTo(email.getAddresses());// 收件人
//			helper.setReplyTo(sender);// 回复到
            helper.setSubject(email.getSubject());// 邮件主题
            helper.setText(email.getContent(), email.isHtmlFormat());// true表示设定html格式
            mailSender.send(mime);
            success = true;
        } catch (Exception e) {
            LOGGER.error("send mail failed: " + JsonUtils.fromObjectToJson(email), e);
        }
        return success;
    }

    /**
     * @description 给内部员工发送系统邮件-sync
     * @method sendSystemEmailAsync
     * @param [subject, content, isToDev, isToAudit, address]
     * @return
     * @time 2019/1/10
     * @author tianxiang@insightchain.io
     */
    public void sendSystemEmailAsync(String subject, String content, Boolean isToDev, Boolean isToAudit, String[] address ) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sendSystemEmail(subject,content,isToDev,isToAudit,address);
            }
        });
    }

    /**
     * @description 给内部员工发送系统邮件
     * @method sendSystemEmail
     * @param [subject, content, isToDev, isToAudit, address]
     * @return
     * @time 2018/12/3
     * @author tianxiang@insightchain.io
     */
    @Override
    public boolean sendSystemEmail(String subject, String content, Boolean isToDev, Boolean isToAudit, String[] address ){
        Boolean success = Boolean.FALSE;
        Email email = new Email();
        email.setSubject(subject);
        email.setContent(content);
        if(isToDev){
            email.addAddress(developersEmail);
        }
        if(isToAudit){
            email.addAddress(auditorsEmail);
        }
        if(null != address && address.length > 0){
            for(String toAddress : address){
                email.addAddress(toAddress);
            }
        }
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mime, true, "utf-8");
            helper.setFrom(sender, senderName);
            helper.setTo(email.getAddresses()); // 收件人
            helper.setSubject(email.getSubject()); // 邮件主题
            helper.setText(email.getContent(), email.isHtmlFormat());// true表示设定html格式
            mailSender.send(mime);
            success = true;
        } catch (Exception e) {
            LOGGER.error("send mail failed: " + JsonUtils.fromObjectToJson(email), e);
        }
        return success;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
