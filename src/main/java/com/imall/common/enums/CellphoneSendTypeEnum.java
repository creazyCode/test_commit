package com.imall.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jianxunji
 */
public enum CellphoneSendTypeEnum {
	MESSAGE_CODE(1, "短信"),
	VOICE_CODE(2, "语音"),
	;

	private Integer code;
	private String description;
	
	private static Map<Integer, CellphoneSendTypeEnum> map;

	private CellphoneSendTypeEnum(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static CellphoneSendTypeEnum getByCode(Integer code) {
		if(map == null){
			map = new HashMap<Integer, CellphoneSendTypeEnum>();
			CellphoneSendTypeEnum[] enumObjects = values();
			for (CellphoneSendTypeEnum enumObject : enumObjects) {
				map.put(enumObject.getCode(), enumObject);
			}
		}
		return map.get(code);
	}
}
