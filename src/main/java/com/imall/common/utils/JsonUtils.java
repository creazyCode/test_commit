package com.imall.common.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jianxunji
 */
public class JsonUtils {
	/** logger **/
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

	public static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * <br/>convert json string to java object
	 * 
	 * @param json json string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromJsonToMap(String json){
		if(StringUtils.isBlank(json)){
			return null;
		}
		try {
			return MAPPER.readValue(json, Map.class);
		} catch (JsonParseException e) {
			LOGGER.error("JsonParseException: ", e);
		} catch (JsonMappingException e) {
			LOGGER.error("JsonMappingException: ", e);
		} catch (IOException e) {
			LOGGER.error("IOException: ", e);
		}
		return null;
	}
	
	/**
	 * <br/>convert json string to java object
	 * 
	 * @param json json string
	 * @param valueType object'class
	 * @return
	 */
	public static <T> T fromJsonToObject(String json, Class<T> valueType){
		try {
			return (T)MAPPER.readValue(json, valueType);
		} catch (Exception e) {
			LOGGER.error("fromJsonToObject Exception: ", e);
		}
		return null;
	}
	
	/**
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> List<T> fromJsonToList(String json, Class<T> valueType){
		try {
			JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, valueType);
//			return MAPPER.readValue(json, new TypeReference<List<T>>(){});
			return MAPPER.readValue(json, javaType);
		} catch (Exception e) {
			LOGGER.error("fromJsonToList Exception: ", e);
		}
		return null;
	}

	/**
	 * <br/>java object convert to json string
	 * 
	 * @param object java object
	 * @return
	 */
	public static String fromObjectToJson(Object object) {
		try {
			if(object == null){
				return null;
			}
			return MAPPER.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			LOGGER.error("JsonGenerationException: ", e);
		} catch (JsonMappingException e) {
			LOGGER.error("JsonMappingException: ", e);
		} catch (IOException e) {
			LOGGER.error("IOException: ", e);
		}
		return null;
	}

	/**
	 * <br/>java object convert to json string
	 * 
	 * @param object java object who want to convert to json string
	 * @param filterName filter name
	 * @param properties filter fields
	 * @return
	 */
	public static String fromObjectToJson(Object object, String filterName, Set<String> properties) {
		FilterProvider filters = new SimpleFilterProvider().addFilter(filterName, SimpleBeanPropertyFilter.serializeAllExcept(properties));
		try {
			return MAPPER.filteredWriter(filters).writeValueAsString(object);
		} catch (JsonGenerationException e) {
			LOGGER.error("JsonGenerationException: ", e);
		} catch (JsonMappingException e) {
			LOGGER.error("JsonMappingException: ", e);
		} catch (IOException e) {
			LOGGER.error("IOException: ", e);
		}
		return null;
	}

	/**
	 * if can log this object array by json
	 * 
	 * @param objects
	 */
	public static boolean canLogObjectArrayJson(Object[] objects){
		boolean b = true;
		if(objects != null && objects.length > 0){
			for (Object object : objects) {
				if(!canLogObjectJson(object)){
					return false;
				}
			}
		}
		return b;
	}

	/**
	 * if can log this object by json
	 * 
	 * @param object
	 */
	public static boolean canLogObjectJson(Object object){
		boolean b = true;
		if (object == null || !(object instanceof Serializable)) {
			b = false;
		} else if (object.getClass().getName().contains("org.springframework")
				|| object.getClass().getName().contains("org.mybatis")
				|| object.getClass().getName().contains("org.apache")
				|| object.getClass().getName().contains("$Proxy")) {
			b = false;
		}
		return b;
	}
	
	public static void main(String[] args) {
		Map<String, Object> map = fromJsonToMap("{\"userId\": 12321}");
		Object userId = map.get("userId");
		System.out.println(map);
	}
}
