package com.imall.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.imall.common.enums.CellphoneSendTypeEnum;

/**
 * the wrapper of the sms
 * 
 * @author jianxunji
 * 
 */
public class Sms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3605192935397776762L;
	private String cellphone;
	private List<String> cellphones = new ArrayList<String>();
	private String content;
	private String code;//验证码
	
	//表示是短信还是语音消息
	private Integer type = CellphoneSendTypeEnum.MESSAGE_CODE.getCode();
	
	public Sms() {
		super();
	}

	public Sms(String cellphone, String content) {
		super();
		this.cellphone = cellphone;
		this.content = content;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public List<String> getCellphones() {
		return cellphones;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public void processCellphones(){
		if(!StringUtils.isBlank(this.getCellphone())){
			if(this.getCellphones() != null && this.getCellphones().size() == 0){
				this.getCellphones().add(this.getCellphone());
			}
		}
	}
}
