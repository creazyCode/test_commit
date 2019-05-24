package com.imall.common.service.impl;

import java.util.Date;

import org.junit.Before;

import com.imall.common.domain.Email;
import com.imall.common.utils.TimeUtils;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:action-servlet.xml" })
public class SpringMailServiceImplTest {
	
	private SpringMailServiceImpl mailService;
	
	@Before
	public void init(){
		mailService = new SpringMailServiceImpl();
	}
	
	public void testDoSendEmail() {
		String content = "尊敬的{0}，您好：<br/>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;请点击后面链接重置您的密码：<a href=\"{1}\">修改密码</a>。<br/><br/><br/>" +
				"您之所以收到这封邮件是由于您在微加中选择了重置密码服务，如果您没有进行此操作，请忽略这封邮件。<br/><br/>{2}<br/>最重视您的，<br/>微加服务团队";
		content = content.replace("{0}", "worldfriend");
		content = content.replace("{2}", TimeUtils.getTimeString(new Date(System.currentTimeMillis()), "yyyy-MM-dd"));
		Email email = new Email();
		email.setSubject("【微加】重置密码服务");
		email.addAddress("571528477@qq.com");
		email.addAddress("15011186215@163.com");
		email.setContent(content);
		
//		mailService.doSendEmail(email);
	}

}
