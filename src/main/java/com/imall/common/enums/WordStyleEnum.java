package com.imall.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jianxunji
 */
public enum WordStyleEnum {
	HORIZONTAL(1, "横向"),
	VERTICAL(2, "纵向"),
	;
	
	private Integer code;
	private String description;
	
	private static Map<Integer, WordStyleEnum> map;

	private WordStyleEnum(Integer code, String description) {
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
	
	public static WordStyleEnum getByCode(Integer code) {
		if(map == null){
			map = new HashMap<Integer, WordStyleEnum>();
			WordStyleEnum[] enumObjects = values();
			for (WordStyleEnum enumObject : enumObjects) {
				map.put(enumObject.getCode(), enumObject);
			}
		}
		return map.get(code);
	}
}
