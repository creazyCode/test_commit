package com.imall.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * the wrapper of the email
 * 
 * @author jjx
 * 
 */
public class Email implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2639347174971068922L;
	private List<String> addressList = new ArrayList<String>();
	private String subject;
	private String content;
	private boolean htmlFormat = true;

	public boolean addAddress(String address){
		this.addressList.add(address);
		return true;
	}
	
	public String[] getAddresses() {
		String[] strs = new String[this.addressList.size()];
		int i = 0;
		for(String str : this.addressList){
			strs[i ++] = str;
		}
		return strs;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHtmlFormat() {
		return htmlFormat;
	}

	public void setHtmlFormat(boolean htmlFormat) {
		this.htmlFormat = htmlFormat;
	}

}
