package com.imall.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jianxunji
 */
public enum TextAlignEnum {
	LEFT(1, "left"),
	CENTER(2, "center"),
	RIGHT(3, "right"),
	TOP(4, "top"),
	BOTTOM(5, "bottom"),
	;

	private Integer code;
	private String description;
	
	private static Map<Integer, TextAlignEnum> map;

	private TextAlignEnum(Integer code, String description) {
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
	
	public static TextAlignEnum getByCode(Integer code) {
		if(map == null){
			map = new HashMap<Integer, TextAlignEnum>();
			TextAlignEnum[] enumObjects = values();
			for (TextAlignEnum enumObject : enumObjects) {
				map.put(enumObject.getCode(), enumObject);
			}
		}
		return map.get(code);
	}
}
