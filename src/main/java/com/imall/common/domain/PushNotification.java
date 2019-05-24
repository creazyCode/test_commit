package com.imall.common.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.imall.common.utils.ObjectUtils;

/**
 * 
 * @author jjx
 * 
 */
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@com.fasterxml.jackson.databind.annotation.JsonSerialize(include=com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL)
public class PushNotification implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6044525172267587581L;
	
	private String uid;//要发送的设备的uid，这个用作XMPP server识别发给谁内容
	private String deviceToken;
	private String title;
	private String content;
	private Integer type = 11;
	private Boolean showPage = Boolean.TRUE;
	private Map<String, Object> customFields = new HashMap<String, Object>();
	
	private Integer deviceType;
	
	public PushNotification() {
		super();
	}
	
	public PushNotification(String deviceToken, String content) {
		super();
		this.deviceToken = deviceToken;
		this.content = content;
	}
	
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Map<String, Object> getCustomFields() {
		return customFields;
	}

	public Boolean getShowPage() {
		return showPage;
	}

	public void setShowPage(Boolean showPage) {
		this.showPage = showPage;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void initUid(String userUuid, String deviceUuid){
		this.setUid(ObjectUtils.generateChatId(userUuid, deviceUuid, false));
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
}
