package com.imall.common.wechat;

import com.imall.common.utils.encrypt.EncryptUtils;
import com.imall.common.utils.encrypt.EncryptUtils.HashType;

/**
 * @author jason
 *
 */
public class JsShare {
	private String nonceStr;
	private String jsApiTicket;
	//当前时间，单位秒
	private Long timestamp;
	private String url;
	
	private String signature;
	
	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getJsApiTicket() {
		return jsApiTicket;
	}

	public void setJsApiTicket(String jsApiTicket) {
		this.jsApiTicket = jsApiTicket;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSignature() {
		return signature;
	}

	public void generateSignature() {
		StringBuilder sb = new StringBuilder();
		sb.append("jsapi_ticket=");
		sb.append(this.getJsApiTicket());
		sb.append("&");
		
		sb.append("noncestr=");
		sb.append(this.getNonceStr());
		sb.append("&");
		
		sb.append("timestamp=");
		sb.append(this.getTimestamp());
		sb.append("&");
		
		sb.append("url=");
		sb.append(this.getUrl());
		
		this.signature = EncryptUtils.hash(sb.toString(), HashType.SHA1);
	}
}