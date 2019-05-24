package com.imall.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test Case Utils.
 * This class is only for unit test.
 * If you delete this class. it can cause some unexpected fail unit testes.
 * 
 * @author jianxunji
 */
public class TestCaseUtils {
	/** logger */
	public static final Logger LOGGER = LoggerFactory.getLogger(TestCaseUtils.class);

	/**
	 * inject the object in map to injectObject by @Autowired, to make sure that
	 * the attribute name in the injectObject is equals the key in the map
	 * 
	 * @param injectObject
	 * @param map
	 */
	public static void injectAutowiredObjectByName(Object injectObject, Map<String, Object> map) {
		
		if (map == null || map.isEmpty()) {
			return;
		}
		Field[] declaredFields = injectObject.getClass().getDeclaredFields();
		if(declaredFields == null || declaredFields.length == 0){
			return;
		}
		
		for (Field field : declaredFields) {
			if(!field.isAnnotationPresent(Autowired.class)){
				continue;
			}
			for (Map.Entry<String,Object> entry : map.entrySet()) {
				if (entry.getKey().equals(field.getName())) {
					boolean isAccessible = field.isAccessible();
					try {
						field.setAccessible(true);
						if (entry.getValue() != null) {
							field.set(injectObject, entry.getValue());
						}
					} catch (IllegalAccessException e) {
						
						LOGGER.error("property {} is illegal access exception, {}", new Object[] { field.getName(), e.getMessage() });
					} finally {
						field.setAccessible(isAccessible);
					}
				}
			}
		}
	}

	/**
	 * inject the object in map to injectObject by @Autowired and type
	 * 
	 * @param injectObject
	 * @param daos
	 */
	public static void injectAutowiredObjectByType(Object injectObject, Object[] daos) {
		
		if(daos == null || daos.length == 0){
			return;
		}
		Field[] declaredFields = injectObject.getClass().getDeclaredFields();
		if(declaredFields == null || declaredFields.length == 0){
			return;
		}
		Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
		for (Object o : daos) {
			map.put(o.getClass(), o);
		}
		
//		Set<Class<?>> set;
		for (Field field : declaredFields) {
			if(!field.isAnnotationPresent(Autowired.class)){
				continue;
			}
			
//			set = map.keySet();
//			for (Class<?> clazz : set) {
//				if (isType(clazz, field.getType())) {
//					boolean isAccessible = field.isAccessible();
//					try {
//						field.setAccessible(true);
////						if (field.get(injectObject) == null) {
//						if (map.get(clazz) != null) {
//							field.set(injectObject, map.get(clazz));
//						}
//					} catch (IllegalAccessException e) {
//						
//						LOGGER.error("property {} is illegal access exception, {}", new Object[] { field.getName(), e.getMessage() });
//					} finally {
//						field.setAccessible(isAccessible);
//					}
//				}
//			}
			for (Map.Entry<Class<?>,Object> entry : map.entrySet()) {
				if (ObjectUtils.isType((Class<?>)entry.getKey(), field.getType())) {
					boolean isAccessible = field.isAccessible();
					try {
						field.setAccessible(true);
//						if (field.get(injectObject) == null) {
						if (entry.getValue() != null) {
							field.set(injectObject, entry.getValue());
						}
					} catch (IllegalAccessException e) {
						
						LOGGER.error("property {} is illegal access exception, {}", new Object[] { field.getName(), e.getMessage() });
					} finally {
						field.setAccessible(isAccessible);
					}
				}
			}
		}
	}
	
	
}
