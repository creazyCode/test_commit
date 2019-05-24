package com.imall.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jianxunji
 */
public enum HolidayTypeEnum {
	NOT_HOLIDAY(11, "不是holiday"),
//	HOLIDAY_PREVIOUS_DAY(12, "holiday前一天"),
//	HOLIDAY_NEXT_DAY(13, "holiday后一天"),
	
	HOLIDAY_FIRST(21, "holiday开始第一天"),
	HOLIDAY_CENTER(22, "holiday中间"),
	HOLIDAY_LAST(23, "holiday最后一天"),
	;
	
	private Integer code;
	private String description;
	
	private static Map<Integer, HolidayTypeEnum> map;

	private HolidayTypeEnum(Integer code, String description) {
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
	
	public static HolidayTypeEnum getByCode(Integer code) {
		if(map == null){
			map = new HashMap<Integer, HolidayTypeEnum>();
			HolidayTypeEnum[] enumObjects = values();
			for (HolidayTypeEnum enumObject : enumObjects) {
				map.put(enumObject.getCode(), enumObject);
			}
		}
		return map.get(code);
	}
}
