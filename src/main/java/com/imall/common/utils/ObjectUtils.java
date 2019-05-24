package com.imall.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imall.common.domain.BasicDomain;

public class ObjectUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);
	
//	public static final String TOKEN_BASE64_RAW = "d-u.y12,";
//	public static final String TOKEN_BASE64_RESULT = Base64.encodeBase64String(TOKEN_BASE64_RAW.getBytes());
	
	public static final String TOKEN_MD5_RAW = "ab1,387.re_";
	public static final String TOKEN_MD5_RESULT = getMD5Hex(TOKEN_MD5_RAW);
	public static final String TOKEN_MD5_PART_1 = TOKEN_MD5_RESULT.substring(0, 7);
	public static final String TOKEN_MD5_PART_2 = TOKEN_MD5_RESULT.substring(7, 10);
	public static final String TOKEN_MD5_PART_3 = TOKEN_MD5_RESULT.substring(10);
	
	public static final String SPLITER_ARRAY = ";";
	public static final String SPLITER_PROPERTY = ",";
	
	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
	
	/**
	 * Is the clazz1 equals clazz2 or the subclass of clazz2.
	 * 
	 * @param clazz1
	 * @param clazz2
	 * @return
	 */
	public static boolean isType(Class<?> clazz1, Class<?> clazz2){
		
		if(clazz1 == null || clazz2 == null){
			return false;
		}
		if(clazz1.equals(clazz2)){
			return true;
		}else{
			Class<?>[] interfaces = clazz1.getInterfaces();
			if(interfaces != null){
				for (Class<?> class1 : interfaces) {
					if(isType(class1, clazz2)){
						return true;
					}
				}
			}
			if(isType(clazz1.getSuperclass(), clazz2)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Check primitive wrapped clazz.
	 * 
	 * @param clazz
	 * @return
	 */
	private static boolean isPrimitiveWrapped(Class<?> clazz){
		return clazz.equals(Character.class) || clazz.equals(Byte.class) ||
				clazz.equals(Short.class) || clazz.equals(Integer.class) ||
				clazz.equals(Long.class) || clazz.equals(Float.class) ||
				clazz.equals(Double.class) || clazz.equals(Boolean.class);
	}

	/**
	 * Check two same object taste.
	 * 
	 * @param expected
	 * @param result
	 * @return
	 */
	private static boolean isTwoSameObjectTaste(Object expected, Object result) {
		if (expected == null && result == null) {
			return true;
		}
		
		if (expected == null || result == null) {
			return false;
		}
		
		if (!expected.getClass().equals(result.getClass())) {
			return false;
		}
		return true;
	}

	/**
	 * Check two same field.
	 * 
	 * @param field
	 * @param expected
	 * @param result
	 * @return
	 *
	 * @throws IllegalAccessException
	 */
	private static boolean isTwoSameField(Field field, Object expected, Object result) throws IllegalAccessException {
		
		if(field.get(expected) != null && field.get(result) == null) {
			LOGGER.error(
					"property {} in Class {} is not equals, expected is {} and result is {}",
					new Object[] { field.getName(),
							expected.getClass().getName(),
							field.get(expected),
							field.get(result) });
			return false;
		
		} else if(field.get(result) != null && field.get(expected) == null) {
			LOGGER.error(
					"property {} in Class {} is not equals, expected is {} and result is {}",
					new Object[] { field.getName(),
							expected.getClass().getName(),
							field.get(expected),
							field.get(result) });
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Check two same extended object.
	 * 
	 * @param field
	 * @param expected
	 * @param result
	 * @param ignoredProperties
	 * @return
	 *
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("deprecation")
	private static boolean isTwoSameExtObject(Field field, Object expected, Object result, String[] ignoredProperties) throws IllegalAccessException {
		//when field.get(object2) != null && field.get(object1) != null go the following code
		if(isType(field.getType(), Collection.class)){
			Object obj = null;
			if(((Collection<?>)field.get(expected)).size() > 0){
				obj = expected;
			}else if(((Collection<?>)field.get(result)).size() > 0){
				obj = result;
			}
			if(obj != null){
				if(((List<?>)field.get(obj)).get(0) instanceof BasicDomain){
					@SuppressWarnings("unchecked")
					boolean b = isTwoCollectionDeeplyEquals((List<Object>)field.get(expected),
							(List<Object>)field.get(result), ignoredProperties, false);
					if(!b){
						LOGGER.error(
								"property {} in Class {} is not equals, expected is {} and result is {}",
								new Object[] { field.getName(),
										expected.getClass().getName(),
										JsonUtils.fromObjectToJson(field.get(expected)),
										JsonUtils.fromObjectToJson(field.get(result))});
						return false;
					}
				}
			}
		}else if(isType(field.getType(), BasicDomain.class)){//递归比较子对象
			return ObjectUtils.isTwoObjectDeeplyEquals(field.get(expected), field.get(result), ignoredProperties);
		}else if(field.getType().equals(BigDecimal.class)){
			BigDecimal bd1 = (BigDecimal)field.get(expected);
			BigDecimal bd2 = (BigDecimal)field.get(result);
			if (bd1.compareTo(bd2) != 0) {
				LOGGER.error(
						"property {} in Class {} is not equals, expected is {} and result is {}",
						new Object[] { field.getName(),
								expected.getClass().getName(),
								field.get(expected),
								field.get(result) });
				return false;
			}
		}else if(field.getType().equals(Time.class)){
			Time time1 = (Time) field.get(expected);
			Time time2 = (Time) field.get(result);
			if(time1.getHours() != time2.getHours() || time1.getMinutes() != time2.getMinutes()
					|| time1.getSeconds() != time2.getSeconds()){
				LOGGER.error(
						"property {} in Class {} is not equals, expected is {} and result is {}",
						new Object[] { field.getName(),
								expected.getClass().getName(),
								field.get(expected),
								field.get(result) });
				return false;
			}
		}else if(field.getType().equals(Float.class)){
			if (!(Math.abs((Float)field.get(result) - (Float)field.get(expected)) < 0.01f)) {
				LOGGER.error(
						"property {} in Class {} is not equals, expected is {} and result is {}",
						new Object[] { field.getName(),
								expected.getClass().getName(),
								field.get(expected),
								field.get(result) });
				return false;
			}
		}else if(field.getType().equals(Double.class)){
			if (!(Math.abs((Double)field.get(result) - (Double)field.get(expected)) < 0.01f)) {
				LOGGER.error(
						"property {} in Class {} is not equals, expected is {} and result is {}",
						new Object[] { field.getName(),
								expected.getClass().getName(),
								field.get(expected),
								field.get(result) });
				return false;
			}
		}else{
			if (!field.get(expected).equals(field.get(result))){
				LOGGER.error(
						"property {} in Class {} is not equals, expected is {} and result is {}",
						new Object[] { field.getName(),
								expected.getClass().getName(),
								field.get(expected),
								field.get(result)});
				return false;
			}
		}
		return true;
	}

	/**
	 * Compare the private properties of the object, excluding properties:
	 * serialVersionUID.
	 * 
	 * @param expected
	 * @param result
	 * @param ignoredProperties
	 */
	public static boolean isTwoObjectDeeplyEquals(Object expected, Object result, String[] ignoredProperties) {
		if (!isTwoSameObjectTaste(expected, result)) {
			return false;
		}
		
		if((expected.getClass().isPrimitive() || expected.getClass().equals(String.class) || isPrimitiveWrapped(expected.getClass())) && expected.equals(result)){
			return true;
		}
		Set<String> commonIngoredProperties = new HashSet<String>();
		commonIngoredProperties.add("serialVersionUID");

		List<Field> allFields = getAllFields(expected.getClass());
		List<String> ignoredPropertiesList = new ArrayList<String>();
		if(ignoredProperties != null){
			ignoredPropertiesList = Arrays.asList(ignoredProperties);
		}
		for (Field field : allFields) {
			if (!ignoredPropertiesList.contains(field.getName())
					&& !commonIngoredProperties.contains(field.getName())) {
				boolean isAccessible = field.isAccessible();
				try {
					field.setAccessible(true);
					if (field.get(expected) == null && field.get(result) == null) {
						continue;
					}
					if (!isTwoSameField(field, expected, result)) {
						return false;
					}
					if (!isTwoSameExtObject(field, expected, result, ignoredProperties)) {
						return false;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					LOGGER.error("property {} is illegal access exception, {}", new Object[] { field.getName(), e.getMessage() });
				} finally {
					field.setAccessible(isAccessible);
				}
			}
		}
		return true;
	}

	/**
	 * Compare the private properties of every equals object, excluding properties:
	 * serialVersionUID.
	 * 
	 * @param expected
	 * @param result
	 * @param ignoredProperties
	 * @param withOrder if order must equals too
	 * @return
	 */
	public static boolean isTwoCollectionDeeplyEquals(List<?> expected, List<?> result, String[] ignoredProperties, boolean withOrder) {
		if (expected == null && result == null) {
			return true;
		}
		
		if (expected == null || result == null) {
			LOGGER.error("the two collections are not equal, expected is {} and result is {}",
					new Object[] { expected, result });
			return false;
		}
		
		if (expected.size() != result.size()) {
			LOGGER.error("the two collections size are not equal, expected is {} and result is {}",
					new Object[] { expected.size(), result.size() });
			return false;
		}
		List<Object> resultCopy = new ArrayList<Object>();
		for (Object object : result) {
			resultCopy.add(object);
		}
		if(withOrder){
			Object object1;
			Object object2;
			for (int i = 0; i < expected.size(); i++) {
				object1 = expected.get(i);
				object2 = result.get(i);
				boolean b = isTwoObjectDeeplyEquals(object1, object2, ignoredProperties);
				if(!b){
					return false;
				}
			}
		}else{
			for (Object object1 : expected) {
				if(!result.contains(object1)){
					return false;
				}
				boolean b = false;
				Iterator<Object> expectedIter = resultCopy.iterator();
				while(expectedIter.hasNext()){
					Object object2 = expectedIter.next();
					if (object1.equals(object2)) {	// If the data in the list id is null how to do? The judgment equal conditions not correct
						b = isTwoObjectDeeplyEquals(object1, object2, ignoredProperties);
						if(b){
							expectedIter.remove();
							break;
						}
					}
				}
				if(!b){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Get all the fields of the Class including the fields in the parent,
	 * parent's parent and so on...
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		
		if (clazz == null) {
			return new ArrayList<Field>();
		}
		List<Field> allFields = new ArrayList<Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		if (declaredFields != null && declaredFields.length > 0) {
			for (Field field : declaredFields) {
				allFields.add(field);
			}
		}
		allFields.addAll(getAllFields(clazz.getSuperclass()));
		return allFields;
	}
	
	public static boolean isTwoObjectEquals(Object object1,Object object2){
		if(object1 == null && object2 == null){
			return true;
		}else if(object1 != null && object2 != null){
			return object1.equals(object2);
		}else{
			return false;
		}
	}
	
	public static String getMD5Hex(byte[] data){
		return DigestUtils.md5Hex(data);
	}
	
	public static String getMD5Hex(String target){
		return DigestUtils.md5Hex(target);
	}
	
	public static String getMD5HexForSms(String target){
		String resultStr = "";
		try {
			byte[] temp = target.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(temp);
			// resultStr = new String(md5.digest());
			byte[] b = md5.digest();
			for (int i = 0; i < b.length; i++) {
				char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
						'9', 'A', 'B', 'C', 'D', 'E', 'F'};
				char[] ob = new char[2];
				ob[0] = digit[(b[i] >>> 4) & 0X0F];
				ob[1] = digit[b[i] & 0X0F];
				resultStr += new String(ob);
			}
			return resultStr;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据用户登陆信息获得token值
	 * 
	 * @param uid
	 * @param password
	 * @param loginTime
	 * @return token
	 */
	public static String encodeToken(Long uid, String password, Long loginTime){
		//generate token: Base64(TOKEN_MD5_PART_1 + Base64(uid) + TOKEN_MD5_PART_2 + MD5(password) + TOKEN_MD5_PART_3 + Base64(loginTime))
		return new String(Base64.encodeBase64(new StringBuilder(TOKEN_MD5_PART_1)
				.append(new String(Base64.encodeBase64(uid.toString().getBytes())))
				.append(TOKEN_MD5_PART_2)
				.append(ObjectUtils.getMD5Hex(password))
				.append(TOKEN_MD5_PART_3)
				.append(new String(Base64.encodeBase64(loginTime.toString().getBytes()))).toString().getBytes()));
    }
	
	/**
	 * 根据用户登陆信息获得token值
	 * 
	 * @param uid
	 * @param password
	 * @param loginTime
	 * @return token
	 */
	public static String encodeToken2(Long uid, String password, Long loginTime){
		//重新想一个算法？可以根据uid的各个位置之和来做点什么？？？
		//generate token: Base64(TOKEN_MD5_PART_1 + Base64(uid) + TOKEN_MD5_PART_2 + MD5(password) + TOKEN_MD5_PART_3 + Base64(loginTime))
		return new String(Base64.encodeBase64(new StringBuilder(TOKEN_MD5_PART_1.toUpperCase())
				.append(new String(Base64.encodeBase64(uid.toString().getBytes())))
				.append(TOKEN_MD5_PART_2.toUpperCase())
				.append(ObjectUtils.getMD5Hex(password).toUpperCase())
				.append(TOKEN_MD5_PART_3.toUpperCase())
				.append(new String(Base64.encodeBase64(loginTime.toString().getBytes()))).toString().getBytes()));
	}
	
	/**
	 * 跟据token获得用户信息
	 * 
	 * @param token
	 * @return 返回的数组中包括uid, MD5(password), loginTime 
	 */
	public static Object[] decodeToken(String token){
		if(StringUtils.isBlank(token)){
			return null;
		}
		Object[] userInfo = null;
		try {
			userInfo = new Object[3];
			String temp = new String(Base64.decodeBase64(token.getBytes()));
			int index1 = temp.indexOf(TOKEN_MD5_PART_1);
			int index2 = temp.indexOf(TOKEN_MD5_PART_2);
			int index3 = temp.indexOf(TOKEN_MD5_PART_3);
			String uidBase64 = temp.substring(index1 + TOKEN_MD5_PART_1.length(), index2);
			userInfo[0] = Long.valueOf(new String(Base64.decodeBase64(uidBase64.getBytes())));
			userInfo[1] = temp.substring(index2 + TOKEN_MD5_PART_2.length(), index3);
			String loginTimeBase64 = temp.substring(index3 + TOKEN_MD5_PART_3.length());
			userInfo[2] = Long.valueOf(new String(Base64.decodeBase64(loginTimeBase64.getBytes())));
		} catch (Exception e) {
			LOGGER.error("decode token {} error: {}", new Object[]{token, e.getMessage()});
			e.printStackTrace();
			userInfo = null;
		}
		return userInfo;
    }
	
	/**
	 * 跟据token获得用户信息
	 * 
	 * @param token
	 * @return 返回的数组中包括uid, MD5(password), loginTime 
	 */
	public static Object[] decodeToken2(String token){
		if(StringUtils.isBlank(token)){
			return null;
		}
		Object[] userInfo = null;
		try {
			userInfo = new Object[3];
			String temp = new String(Base64.decodeBase64(token.getBytes()));
			int index1 = temp.indexOf(TOKEN_MD5_PART_1.toUpperCase());
			int index2 = temp.indexOf(TOKEN_MD5_PART_2.toUpperCase());
			int index3 = temp.indexOf(TOKEN_MD5_PART_3.toUpperCase());
			String uidBase64 = temp.substring(index1 + TOKEN_MD5_PART_1.length(), index2);
			userInfo[0] = Long.valueOf(new String(Base64.decodeBase64(uidBase64.getBytes())));
			userInfo[1] = temp.substring(index2 + TOKEN_MD5_PART_2.length(), index3).toLowerCase();
			String loginTimeBase64 = temp.substring(index3 + TOKEN_MD5_PART_3.length());
			userInfo[2] = Long.valueOf(new String(Base64.decodeBase64(loginTimeBase64.getBytes())));
		} catch (Exception e) {
			LOGGER.error("decode token {} error: {}", new Object[]{token, e.getMessage()});
			e.printStackTrace();
			userInfo = null;
		}
		return userInfo;
	}
	
	/**
	 * check if the given string is in email format. including @ and the email name can not include special character.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
        Pattern pattern = Pattern.compile("[\\w\\.\\-\\_]+@([\\w\\-]+\\.)+[\\w\\-]+");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
	
	/**
	 * @param str
	 * @return
	 */
	public static boolean isNumberic(String str){
		if(str != null && !"".equals(str.trim()) && str.matches("[0-9]+")){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断一个字符串是否都由ASCII字符组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isASCII(String str){
		if(StringUtils.isBlank(str)){
			return true;
		}
		for(int i = 0; i < str.length(); i ++){
			char c = str.charAt(i);
			int cInt = c;
			if(cInt < 0 || cInt > 127){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 生成数字的随机字符串
	 * 
	 * @param codeLength
	 * @return
	 */
	public static String generateRandomIntegerCode(int codeLength){
		if(codeLength <= 0){
			return "";
		}
		StringBuilder code = new StringBuilder();
		for(int i = 0; i < codeLength; i ++){
			code.append(RandomUtils.nextInt(10));
		}
		return code.toString();
	}

	/**
	 * 随机生成16进制的字符串
	 * 
	 * @param codeLength
	 * @return
	 */
	public static String generateRandom16BitCode(int codeLength){
		if(codeLength <= 0){
			return "";
		}
		String str = "abcdef0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < codeLength; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	
	/**
	 * 生成数字和字母的随机字符串，不带大写字母
	 * 
	 * @param codeLength
	 * @return
	 */
	public static String generateRandomCode(int codeLength){
		String str = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < codeLength; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	
	/**
	 * 生成数字和字母的随机字符串,带大写字母
	 * 
	 * @param codeLength
	 * @return
	 */
	public static String generateRandomCodeWithCapital(int codeLength){
		if(codeLength <= 0){
			return "";
		}
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < codeLength; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}
	
	public static String appendZeros(String str, int number){
		if(str != null && str.length() < number){
			StringBuilder sb = new StringBuilder(str);
			int count = number - str.length();
			for(int i = 0; i < count; i ++){
				sb.append("0");
			}
			return sb.toString();
		}
		return str;
	}
	
	/**
	 * 
	 * @param length
	 * @param numberFlag 是否数字
	 * @return
	 */
	public static String createRandomCharacters(int length, boolean numberFlag) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890"
				: "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}
	
	public static String generateUUID(){
		String uuidString = UUID.randomUUID().toString();
		StringBuilder sb = new StringBuilder();
		sb.append(uuidString.substring(0,8));
		sb.append(uuidString.substring(9,13));
		sb.append(uuidString.substring(14,18));
		sb.append(uuidString.substring(19,23));
		sb.append(uuidString.substring(24));
		return sb.toString();
	}
	
	public static String generateShortUUID() {
	    StringBuffer shortBuffer = new StringBuffer();
	    String uuid = generateUUID();
	    for (int i = 0; i < 6; i++) {
	        String str = uuid.substring(i * 4, i * 4 + 4);
	        int x = Integer.parseInt(str, 16);
	        shortBuffer.append(chars[x % 0x3E]);
	    }
	    return shortBuffer.toString();
	 
	}
	
	/**
	 * 得到扩展名称，不包括.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtendName(String fileName){
		String extendName = null;
		if(fileName != null){
			int index = fileName.lastIndexOf(".");
			if(index >= 0){
				extendName = fileName.substring(index + 1).toLowerCase();
			}
		}
		return extendName;
	}
	
	public static String mapToString(Map<? extends Object, ? extends Object> map, int maxLength){
		StringBuilder sb = new StringBuilder("");
		if(map != null && map.size() > 0){
			try{
				Set<? extends Object> keySet = map.keySet();
				for(Object key : keySet){
					if(sb.length() >= maxLength){
						break;
					}
					sb.append(key);
					sb.append(":");
					sb.append(map.get(key).toString());
					sb.append(";");
				}
			}catch(Exception e){
				LOGGER.warn("ignored error: ", e);
			}
		}
		return sb.toString();
	}
	
	public static boolean charGBKCompare(byte[] str) {
		boolean bln = false;
		int i = str.length - 1;
		int j = 0;
		System.out.println("File.charCompare() 【" + str[i] + "】");
		if ((str[i] > 0 && str[i] < 0x40)) {
			System.out.println("File.charCompare() 【标点】【" + str[i] + "】");
		} else if ((str[i] < 0 && str[i] < -127) || str[i] > 64) {
			System.out.println("File.charCompare() 【低字节】【" + str[i] + "】");
		} else {
			while (str[i - 1] < 0 && str[i - 1] > -127) {
				System.out.println("File.charCompare() 【向前找】【" + str[i - 1]
						+ "】");
				j++;
				i--;
			}
			System.out.println("File.charCompare() 【找完】【" + str[i - 1] + "】["
					+ j + "]");
			if (j % 2 == 0)
				return bln = true;
		}
		return bln;
	}
	
	/**
	 * 这是一个猜测txt文件编码的方法，并不保证准确，原理是：优先认为是utf8: 谁包含中文问号谁的编码正确；如果utf8识别出来有换行符，但是gbk没有，则是utf8；如果使用utf8识别中文数量大于5认为是utf8，其他时候看utf8和gbk哪个识别的中文数量多
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String getFileEncoding(String fileName) throws Exception{  
		File file =  new File(fileName);
		String utf8Encoding = "UTF-8";
		String utf8Text = FileUtils.readFileToString(file, utf8Encoding);
		int utf8ContainsChineseNumber = containsChineseNumber(utf8Text);
		if(utf8Text.contains("？") || utf8Text.contains("，") 
				|| utf8Text.contains("。") || utf8Text.contains("；") || utf8Text.contains("！")
				|| utf8Text.contains("￥") || utf8Text.contains("左") || utf8Text.contains("右")
				|| utf8Text.contains("喜") || utf8Text.contains("欢") || utf8Text.contains("哪")
				|| utf8Text.contains("个") || utf8Text.contains("边") || utf8Text.contains("选")
				|| utf8Text.contains("的") || utf8Text.contains("好") || utf8Text.contains("啊")
				|| utf8Text.contains("呢") || utf8Text.contains("我") || utf8Text.contains("你")){
//		if(utf8Text.contains("？")){
			return utf8Encoding;
		}
		
		String gbkEncoding = "GBK";
		String gbkText = FileUtils.readFileToString(file, gbkEncoding);
		int gbkContainsChineseNumber = containsChineseNumber(gbkText);
		if(gbkText.contains("？") || gbkText.contains("，") 
				|| gbkText.contains("。") || gbkText.contains("；") || gbkText.contains("！")
				|| gbkText.contains("￥") || gbkText.contains("左") || gbkText.contains("右")
				|| gbkText.contains("喜") || gbkText.contains("欢") || gbkText.contains("哪")
				|| gbkText.contains("个") || gbkText.contains("边") || gbkText.contains("选")
				|| gbkText.contains("的") || gbkText.contains("好") || gbkText.contains("啊")
				|| gbkText.contains("呢") || gbkText.contains("我") || gbkText.contains("你")){
//		if(gbkText.contains("？")){
			return gbkEncoding;
		}
		
		String[] charsetsToBeTested = { utf8Encoding, gbkEncoding };
		CharsetDetector cd = new CharsetDetector();
		Charset charset = cd.detectCharset(file, charsetsToBeTested);
		if(charset != null && !StringUtils.isBlank(charset.name())){
			return charset.name();
		}
		
		if(utf8Text.contains("\n") && !gbkText.contains("\n")){
			return utf8Encoding;
		}
		if(gbkText.contains("\r\n")){
			return gbkEncoding;
		}
//		if(utf8ContainsChineseNumber < 5 && gbkContainsChineseNumber > utf8ContainsChineseNumber){
//			return gbkEncoding;
//		}
		return utf8Encoding;
	}
	
	public static int containsChineseNumber(String str) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		int number = 0;
		while (matcher.find()) {
			number ++;
		}
		return number;
	}
	
	public static String getEncoding(String str) {
		String encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		return "";
	}
	
	/**
	 * 生成from到to的随机数，两边都包括
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getRandom(int from, int to){
		if(from == to){
			return from;
		}
		int random = RandomUtils.nextInt(to - from + 1);
		return random + from;
	}

	/**
	 * 得到在center的上下variance的百分比浮动的随机数
	 * 
	 * @param center
	 * @param variance
	 * @return
	 */
	public static int getRandomByVariance(int center, float variance){
		return getRandom((int) Math.ceil(center * (1 - variance)), (int) Math.ceil(center * (1 + variance)));
	}
	
	/**
	 * 得到在center的上下variance的百分比浮动的随机数
	 * 
	 * @param center
	 * @param variance
	 * @return
	 */
	public static double getRandomByVariance(double center, float variance){
		return getRandom(center * (1 - variance), center * (1 + variance));
	}
	
	/**
	 * 生成from到to的随机数，两边都包括
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static long getRandom(long from, long to){
		if(from == to){
			return from;
		}
		return (long) getRandom(1.0 * from, 1.0 * to);
	}
	
	/**
	 * 生成from到to的随机数，两边都包括
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static double getRandom(double from, double to){
		if(from == to){
			return from;
		}
		return from + RandomUtils.nextFloat() * (to - from);
	}
	
	/**
	 * 获得一个可能性是否发生的随机状态
	 * 
	 * @param possibility
	 * @return
	 */
	public static boolean checkPossibility(double possibility) {
		if (possibility <= 0) {
			return false;
		}
		if (possibility >= 1.0) {
			return true;
		}
		double random = ObjectUtils.getRandom(0.0, 1.0);
		if (random <= possibility) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获得一个可能性是否发生的随机状态
	 * 
	 * @param possibility
	 * @return
	 */
	public static boolean checkPossibilityByNewsFlash(double possibility, double basicNumber, double number) {
		
		if (possibility <= 0) {
			return false;
		}
		
		if (possibility >= 1.0) {
			return true;
		}
		
		double random = ObjectUtils.getRandom(1.0, number);
		if (random <= basicNumber) {
			return true;
		}
		return false;
	}
	
	/**
	 * 得到随机的正负1
	 * @return
	 */
	public static int getRandomSymbol(){
		int random = getRandom(0, 1);
		return random == 0 ? 1 : -1;
	}
	
	/**
	 * 得到聊天服务器的jid和密码
	 * 
	 * @param userUuid
	 * @param deviceUuid
	 * @param isChatGroup
	 * @return
	 */
	public static String generateChatId(String userUuid, String deviceUuid, boolean isChatGroup){
		String chatId = null;
		if(!StringUtils.isBlank(userUuid)){
			if(isChatGroup){
//				chatId = "wg_" + uuid + "@" + CommonConstants.XMPP_GROUP_SERVICE_NAME;
				chatId = "wg_" + userUuid;
			}else{
//				chatId = "w_" + uuid + "@" + CommonConstants.XMPP_USER_SERVICE_NAME;
				chatId = "w_" + userUuid ;
			}
		}else{
//			chatId = "w_device_" + deviceUuid + "@" + CommonConstants.XMPP_USER_SERVICE_NAME;
			chatId = "w_device_" + deviceUuid;
		}
		return chatId;
	}
	
	/**
	 * 根据聊天服务器的jid获得uuid
	 * 
	 * @param jid
	 * @return
	 */
	public static String getUuidFromJid(String jid){
		String uuid = null;
		if(!StringUtils.isBlank(jid)){
			if(jid.contains("@")){
				uuid = jid.substring(0, jid.indexOf("@"));
				if(uuid.contains("w_device_")){
					uuid = uuid.substring(uuid.indexOf("w_device_") + "w_device_".length());
				}else if(uuid.contains("wg_")){
					uuid = uuid.substring(uuid.indexOf("wg_") + "wg_".length());
				}else if(jid.contains("w_")){
					uuid = uuid.substring(uuid.indexOf("w_") + "w_".length());
				}
			}
		}
		return uuid;
	}
	
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException, IOException {
//		System.out.println(ObjectUtils.isEmail("zwt_wt_0830@163.com"));
//		for(int i = 1; i < 7; i ++){
//	    	String fileName = "/Users/jianxunji/test_encoding/" + i + ".txt";
//	    	File file = new File(fileName);
//	    	System.out.println(FileUtils.readFileToString(file, "ISO-8859-1"));
//	    }
//	    for(int i = 1; i < 7; i ++){
//	    	String fileName = "/Users/jianxunji/test_encoding/" + i + ".txt";
//	    	UniversalDetector detector = new UniversalDetector(null);
//	        //开始给一部分数据，让学习一下啊，官方建议是1000个byte左右（当然这1000个byte你得包含中文之类的）
//	    	File file = new File(fileName);
//	    	byte[] bytes = FileUtils.readFileToByteArray(file);
//	        detector.handleData(bytes, 0, bytes.length);
//	        //识别结束必须调用这个方法
//	        detector.dataEnd();
//	        //神奇的时刻就在这个方法了，返回字符集编码。
//	    	System.out.println(detector.getDetectedCharset() == null ? null : detector.getDetectedCharset());
//	    	
//	    	detector.reset();
//	    	byte[] bytes1 = new byte[10000];
//	    	(new FileInputStream(file)).read(bytes1);
//	    	detector.handleData(bytes1, 0, bytes1.length);
//	    	//识别结束必须调用这个方法
//	    	detector.dataEnd();
//	    	//神奇的时刻就在这个方法了，返回字符集编码。
//	    	System.out.println(detector.getDetectedCharset() == null ? null : detector.getDetectedCharset());
//	    }
		
		byte[] bytes = ObjectUtils.hexStringToBytes("1f8b08000000000000009dbd6b935cd59926fa57147c9a891079d6fd12313111b6e976770cf2d0166d9f8939138e752da5559559546649c89fc0180ce68ec1607371bb0d6d77bb91a0b1311617479c9f72acac2a7d9abf709e77efdc79dda992878b10aa5db5d65eebbd3ccf7bcbfff93f1f48e383c141c9c3302d57077b313c7051888b0f08639d90ca32fdc0c50738fefde6d7bf76e17239ba364ce5818b0c7ff38b0f0456742dc546e3bce159719eb865c2dac46a0ea1d2835c692518131c3f540ff883f87f26f03c17cd0ffe5f17db1ddcb8314983f1a80cae4d1ea01fcd1567d65ba7687d867f67b75f3f7dfdb7b3cf7e387be7f67203c5b16ab4ccd2ab988cd4499b908a72aa32176b6a37608c6796338d9f3a6003d6fcc06edd6bc36be3c1f512a657cad1e0f0687c6d98cb51b383ad1398bdffaf271fbd3efbf0add90f7fbbd8418d554b6f4a943eb0c865d652d42275c9591763d4da11a866076bef1de3d5c1fe786fd29d2c2dac185e5c08d92dfcf078efd1f1e59cc251debc01111c8eb2c8920dd7aa9ae09d30d8736499e9c2d76e8099ede5c3281f8d8779f1e693c1b4ec97c32be3d18d072eca9e43387dfd0f67b7defd7ffeaf3b7ffed5c993b7360e838b648ad3512b2e0b33f41f1b4b96aa6ac1335bdd0de48476a3d776d35cc6e4201c4d0f8ef7a7c3ebc3511e5fdf7119cf3e33fbf8e5d9fbbfb8f3f9e7b39ffc6ab105a345e2c9b05078b1158bd59885cba9b2c270367a6d0b24e7e71e480afb6594432b149c73897f8437f37d9cbcf9c1eca56736ce01f290628008c8108df6a92ac54b0cd94212b34b614d28e4f626482826a9401bf6c3f1289168423e0f4aff515ca6271fa5af3f3c7f7ab111c881acd0a3820d24e38db7dc569620ac5c565ee5ea6918773fa7d1588a072e5a662f3e20add7c67b27557729fff6da9d2f7fba291456a82a4a112a541d228f9631564ccac5e712a45f1351764f23d1c8c7fef05ab91ef6f70fc321ce258d2b2cd074b8eb70eebefbdeecc7bf3a7df7cf273fbfbdb225a78be3c185908be24a69a98c94c6071354e172f37e36b5a695d3323d3ebc3efc01b41226b367e9fe273b392dd254cd39c9678a11ea9b1976a38b141102b37e337efb661e1bde180ee84ec60f5c74cc1a1c9c709e29239493739379faecc7b30f7e7cf6dcf2c55db45aba5c932b2e49964586bd3456a56aa465d9ceeda5a5e36058d50ecc9ab55cf8897afdeae070ff786f38ea3ff6bfbd7ef591f99717671e6c0a8249a9745151f80a03cfb183ecb9b16b86927bbb7de607e338dc2fd3f1787f3298dc984ccbc195b27f48b24e17c4bdb2ca31c337cfbe7b68a110d5c42879363ac8aa9db1de5b9d6381add4d9a76d0b251f8cc7c3fd2c18d75c30bb2d0725603b57c211045092fcae9d05bdc39ddbafddf9e29f165bb0aec019a590998fa654a3a00aceab9292284a4731bf03d82a69796bb207f8c103b776136172383c2a838383072e6a4dab3265a18e5cbbf9aa972ecdde7866d557669e4af12ec32a4a1f5db046c8ea4b894e58e3f85c0fe146f00565480f759faf0cb1f48bfbdddfbd75f6c49bf00fa7377f75faca338b758bc95c4bf8491e7d7541ebc803548fc550151dc39ab2ddc3062d743e1d8d0f9b3bedc529f7fe96f99e5480ca97a843565e70abb487e3563ad8ac6b36d1add924794f9bb4508949ba7278fd683c8241dab1b5d9cd5f9cbcf9e9ec8b27664f7f72b27233c6d6ca24a0132e48c36233666480f3b029456778dcb4d2f7dacd78349c8e8f9a1be246d13769d189c4b7caf4faf8e8eaa5ee990ec23807a756234b59400a59705a145f73e2597b5c5a67148400866a21cc80af09c6248cae1e8721ce62383d0ed07920411c329706484619ded9a3af5e9abdfdcf4b63549d0e58399724140f251aa18d65c10bc9a2f56cbeae97d67972d576c037d6bd313ebe7a3c20b442d69fd13b4ba7bcb5ce994ef9be78ebeed37f5cf1cccc399858c664095c55c03516bdd4421a9d9cb28b351dd4bd396a3b90db4a90c6fbfb61afec8004cf7f317bfbabc59232074dc01046b5b8e09380bdf18e2b5b83f730456b264793fcf78022fce6304c26fdc6f6e4851fcf5e7cf3ee6bcf2e750e8202d1e6267293522dd60b91b249d65a8ddfc54d07d7aed9bee4f8686f30191e4075866194ca607c5846adf10d87c3c102a8ea3e2442d0ad0fa6020f856a01caa1775a055393cc30ffa128e992cff3fd28cf0c301af6230724640272b62ede578ec3f5321c84c343ac74b54c0988e02a25a37b0010315cb14eda662fbe74e7b3dbebb441da8b0a320695684ec9e5083f1735aec5729e83e5702146081d65648a98106f4c31f88427c9b7648a618cf9b644b466e078b403865ca22f3f8a2fff974b8f3ef48dfff15f172703f963f03d8169681acb9ad5a033034883a766909afb05f0799c8e0fca683a391eeeb03e273ffbf1c9af7ebd58387a2d0d74405befacca3c021edae84a2e8281bbacc1b273b85bb787bdb01f1ebfa1fa49ccddcf5f3df9fd7b4b0b0c4f60390c704812875dacb64979c912aec0315f3725a21f8291bc5d0f574b3a9e4cc7077b65323d3eda7103e77e53a7ae2c7b1884c80b533c825815e52384035b6140bb72535d775d48797c5a8e46611f6b1c35a6a2df25bcffb3bb4ffdb645cc6737bf9abdffe315a862810415bcb5866730ce29932b648f7c95ca751d309de3153a1f7563ef70d2efbdffc7371fb9bcb4cc1274ad1aac05e17485fb60c9f61b2ca5420865ed0c9acbe1bd6770657ab07f6d58ae971de4e9ef1ebdf4f085935f7e70facef3a7bf7d7e76fbe5a578d802cc9054f1a5e658a590d6e8e88099320379598f2cf01de291c241390a87e1281cec1f1f847eb9f846f3d023f4d0c3cd431d5e55cc0ad882602b5062f6c0490e703d0330a598fcb91c6158f76f34b0e0b09474056a396d7c14f38a944328b00d65ba8338bbf5e1dd5f7e020138fdf2e672073e304042fc1d7506cf0a051e2a7ac8628564e43e1519340baced635a60c447d3ee4e1e7bec200cf7b11303a9120d6bd0ca42809c9adbcd7ff887bb3fbc797af3633a2e58572f1a61f40086f0d7d201c8612f2973170bc76902b604e6e961f29b4a3178d1461837fc662713070793a513e9d58936b4b0388654a4c321b31c5596d9d9aa01d9b008be29423dec267fdcf49fdb1c368faf8ff6c7214fda0d6cd1d83b9f3d7ff6e5972d8c9dfd7cc9648b563e4b0f68666a88023aa8528ed9d704f2289ddbd4c84d240b251c1d0e477b836b580500034b2b48af8347ea28dbecfddfdcf9e2cdd31ffee9ecd68f96512e991c8801f017ecb407612b4a5891712249421e64075a801c1df17237e02bfe9c56aed707c7877b4760bb74f38daf340c52286cc7d7befb8fddd73ba40444084a0169e74158e9250f5005916d2e56c06f6c4b1fdfd640e088c3fd320fafdd2baaf6bfbf78f6eeebcfcfbefcf87f7ff1dcd245e145c92982ab65e16111a08409bc159b08404ef63c1fb1307b6504265acad1c138eff00e7f337fe2d278e50894001187234b9e251db4941e079f39903a0f167e6bd345f61aa1c5cbab9d2fbf7ce104922c62d4b142c0f15639c04da69ca1008133beb6a0e9046c5bc762bc7a703c19a6fe771d5e80adb9f3a757969ea6680320a6732d159e25e187076819c8a233b03a656d59bb8e16bb70d577c650e8ff3e2a932be3e98e48cceb4f52c4eed71f9dbcf49b25290d25049600e1828b2c261399f7a09f0554046aae373d8d9893d29e4b1e1f8474d86af496a3f9efedd73acc1521429ac01f0f5a49101b93646522f0e8c079d610f9397c8b5ebd4e0ff7c77bfd2ffd1d88c0c3e3bd7f3cccd8e3f704e36ac0d440f815420257c75c8cc0423557973dee1d7a07cde625c575b8b14bc60e267b87c7932bfd5ba0274e3e7d1626f5e4a5dfde7de2c925ef0b42c4c0534826c1701b5378e121813682f9a558ce5b7a216d47e3eb139270edf1571ff2fcc3cb67bf791696f4e4e5d7ee7cfee9d2c131cf2a801f189e0623a8d5060a1483ff818c332eee5bbfe3fe314586a657f677a8f7d7bb071e5e017a2a0252c0bfa752a162569550c094b8c49f855ab738495fe4feb8b9d7a38628f4c4ca3ff9fcf4f35fce5efcf1e9eda5c47b86d7e32e549913640f185215e99d4e5a721589b7ae47a076b9332c9fc27e3ade0f0bbebf29f567f06037df5cf560b02d9e67a79d62bad61cb807f106b2923a159fc55adca38dc5ac6bfa7e8861703d4de8ae29a4079703efb520dbb3af6ecefe78f3ceede797aa0613662354a7128d3790235da0ee9cc74c8e45af04bb24d3db0bb611b6ebe1e8288ca637f09a7db4f3f4f53fcc3e7ce5ce9fdfbdf3e79bb31797b9008d1bcd8a4bd81807d25b403571ddda790ab9096b36edf7e6513724f8fbe31b83eb43522e4941566ea4271eb0086ae09d4f7ef4e7074fbef8dde9eb9f9fbcf2dee9bfbfdac439960100e5a1d0f0cc6038b166a81824de5605476e53757e7ee816685b7b8a61c88159b5eb6950c7d3f1e17e001707f17de0a2a1d03bb454036a0931dfc6d9a7808f1f2dd7cc1e2c16f0dd5a9e94a338a7c95ca4e4750597c9cb402f8028290378ade8f52519da311e4d03dce3ae90dbd997b7a0d6b3179e3e79f7a9bb3f7f65f6d9cdd94ffe7503d0d70adb97a2a9804bcc0593e0d04ae25e07254b156233fc7b1f74f31036fd689cca6432deb5b347427a64f948a7f80244aa9400ec05fb23954cd814087ee4060ec8b1bfc2fc775ba9c3fd721046e079bb7602f68d33da088d7aa76ac23e2051d84ac4af2c16700daf8b4dd9ebf31c6fa31d7172b8963dec73418f82e46ec5647cb459e05ff02966a2a0907fc9ccc28428afd6c0c6dcf5aedbbfbde3f1d5a341855e8234b52118fc0a1c012ab1508e679f39fdf075da97620c7246cf38ae025e2e1b970ba034f61b6b01c2830fca39d58e4968a5386f23f01b51bf8ed150ecdb8926edc8e036a4166ca9932011f849eea2905074f0f426002e2bc5db2b2eb218fc749e8b4fde03cf3a6e2acc0157cdd2dc4b67e9844829d6b56223d2399886ab657c3c1d95eb0078cc30b533ec09a63f7bf1674b2e014457acc185570e77abac77c5b9ccb980e68a6a3aa3c08450147a52300a6afbf227251d1f15a0a883f1682105b2b5665bb80fd237bbf9e9c93b2fae264c658e3243054db2123e580be1a2a0f807a3acbaed2c8563ce4a72093812d08b7e3548e5683a1c4da6309b3b9de2d9ad27effce95f66379f3bfbf5d31b364246209e825f842b32252d149c04248ad924bcc8624d1d7a247219ded98d42cf3e7d1adef8e4d97f03165d2e0c5209a320013d40387c851c7a674108b34f26e41e9fcc56969ebffd0ec5ff5afbd5160b2c232c456766609f8d4b0c703f281b15c32f1144d7d6b54cb933f7b4411b047ff0d8631dbfa53b3096e3ac18bd23b34a186117bad946466fbe0bc95517254496ab766fce09e0bf1aa5a4b41a341194d324c1a387eb4ca20b8b7a6ead2003690676434517c80cae6354d274786d48debb37fe323e80ec5e78284cc3567d092e2000181769adc42fbcfa081309f12e8c6919b69cc62e84da38b0349df44726cf9e7c1db773e7f63221837bd0262a1782d39001f0025e40c354015e96f8ede6ba9b76b95c0ba3bdb23f3c3e9046c3470d1622d2583420215858d9055cfe66f5e9a5c3842dccccf9cc716244be41f7b5855efa64c9482f4a5bb866d22f6572c3401c1f8e1af7b02b04374fd93ffdf19ddbbf5bbe3fa111180620c458852c22f9cc6a2d0e4e0a706e4b1fb6a07118e663d8a67094aec4f1e31018b8052c4cfa2134876df45d4eeae4b9e74fdeb97dfaf3af66b7ff65490c38cc740429e4d5da4a496b51c08c628aba7ac7161929230834535c7edd3412344ffbe3e34cb6b02fc274fbd50d2bc80d0e2527c9808e9c3295019c06e9294bcca107b2c7130ff8c6792f68f0d72693218ce088d2133b08d9dc5bb7372d018e533280895164178a0607890aabcac420279b8ed8ae599f6ee57981481f3e1e5eb8f3d9e777fff9ade5fd160f5d17145b8104391d19b42ac20a25c76ae25b91c54df95e96014c0f8f4aba52d2d51d65008f3e7254bed17e7d116c80770b3ac3784819b34a787797b01dc5811bccb9b46b258e384ffd6f48f446f8b032e5e1592b8321c755f21a4a816265196b0d31af870ff9f6bb76eb4da63bdef1f2df5f02e968ade9125c450f93c509e8560dac0337069c0b3252aa837f4d9babeee4d6215d3d3e84fdaac3a383fbc8786f3cdf799b00f1d61a8c5304d812e025577266c972f06f61eda63dbb87b7995c390e0765700432bd5746c3635c82230981410319b4bef32edfc603a7fff1d5e9937f58de3cee01cb6923387326c06e1527aa331ad81bc4bfcbbd6aa031e04fba093170eb41e485afeb346c706338dabb311eedc530fe5e9b13fc9ea1084e537102b1567a115b5d79f4c1f6d1070da931b4d4e21f7816677d1bfa5770cfd598229dccbe08232959cbb38d163089cd1d20c09974de2c22231bf667b56c6c9b0e346563cbc01fd8116848b570f6ba264d36c77b16bcac01a85cf60090ad05878f8dc703f09a4c32b09de4698ced8f5e3efbcd93b3975fbcfb2f2bb9624d80df42f5230f093e5e84e043a6da4e402133a7a706ae88e18c76071dbffef5fff68dfd71b334ebab1cbcfbe69feebef6cb93373fc5af64a7c07aa0efcddb47290aac2f78712d0a1c3d8402fd09a026d28321af195fd5baba75b9d82fd36b4b6fbf3f2c647b39854815093b736e017beefce995b3df3c837feefef3abcb30a4ca0677200bf8719025baa2945049abca8b97baab23f53035a0197d8867e5141ea51aa51dc1eed76fce5e7969c3f7100952d655975cac3e08d0612d59700a3619fb323dafdfcf87017ac7b0cc4d3ca877f5b7bf3a7deec7a75ffcececab5757a342d6a7e22cac5394e475a2612952491cafd1827de44d07b4cb585d1fd6e1a44c4101f6263b6cd5771ffedab7ba3417fc283c3cbedcc56a788512081f924f786d1209550b15d43232987a6930d5f955824d69dce09ea7f1d1cbad1480a4ae821f078b918cac865ca1099486c75b83dcd92a8a65e6bcd3b8b63fac651efbefea8fb07bd7a47bc045c14679e7abe8a9d94f7e7bf2c493b35f3f797afbb3a587d6c93a2b88893b2ebc0cd96940a20438148da85b1e1ac7c1c4c0f48747fff6d2b7431e8e7758a2d73fbdfbcbb572a46a38fc948b0002d9f05202f96558056d701ab56e61a14debd7bc7a4b492fdf2b30d104a6d75501af0de055e0a3aa10dec31a523822170b8528f0a4e701cf4dc0bff80d9c0568f2f561dea3a291de1cc15ff1fd9dd5f054640d6a2ca88e2902c7702a9fca5c0158b0f47f14d3ba122660c0e1a09faacc9e7eeece9f9ebffbcb3fccbe5ac65899613ea99074b6969b52805f23703187f3a37c82bcef30fa4a867427d878e8db972edcf9f3bb273f797ff6ccd3b39b7fda2cf64d54c7a54550ce531d9b01ea11a1260922efd35a6cedbcaa9223981198f2c3dde51ccb27ba781a5065a43a2e0dbaa800a5a1c4497adc4f51015fda8cec6de5ecc9859e9f2cdcf1681763572af128b8a955b15c7291b2e80a7a6f5d8e3025db0524fd017e789247a8b2edef4793e9d13138f478b4c3ab9cddfafdc95b2fddf9d39240415b4a850d87573712b720c1e03d65125d54019c6eed1aee51c4b2948873ec7a6bd33784c109a8b3f32e725b52cc06845269c17c85298996af577ef3fbd28fa6d036cf6b6a5ab07b1fa8b8f7bb3a257602075401708047538ea09d00c82615990146f8163fb8c7260fc3d1f00754b53f2ad3452126d786b27cde4bc34c0745bfb5f2408702bc4f0c9e98f99c0d6319caabe0850ab76047c1759992a60ad3fa26df3e90ebb477796bc3d174720818b28bf59f3cf7d3d98b1fcddeffd1e92bcf6c986070149b2a1912c90448923146e748b188aa2c546a4b87b67d6f3e82011b8cc6d31dfa73e7ab3f9f7ef8d57f3afbf2f3d39b4f9cdefe97ffbc42c35d2e02781b720249312ed0b1685fa4292a85b50a0bbbabece7d1e1e1ae42c9975f397d7fe9e8924845c3d9894a893767b5c956061d613e0d99f6ed08784fe1df5e20da4d05d3dbcb7df6d9c9b32fdff9ecc3d99f9f5a26a5006a83309565b06f0e5f81ff134947e1703f659111c34d3bab1ab4b759f5da5df370048eb14ff57ebde9f6277e7176ebdd956b8d3e6bcd2444dc270b4203e405e60f6aa355526ed3306fe6c23ae6f5d863d47f41812c2d9b16100a53c0afd8b9649f3dfdd5d9cd5b1bf0da8043690f89aa3a715d737659a42a148c93a97ad1760042a0199da41a782266b4c43acd69e24ae1f0b00d2d512d8b2516d113576a234a6d7469f6937fc56fa82af9a275906adf547fc26e9866550b9e689a28d22e6a73bd442a636b0afc19eb030ff328ef85cb4d67c285ef96f89de61b3a7bccbdb7d151e96d6a5075b60a9701afe46208266cfa04e92ffca7a59179301c1d18f59f7b0de27074783ccd8550d64edbfcd54f674f7fb0515d2805ac9c620cf40aa005b743703f53a10f6721faf500c5bdbb7216b1872ec97e9eaff8e9bba7cffd7ca3d84d98984250812ac800f0439440cc9059102015ac5a0b882f43ae9b21c7e634bed7b44f9d873cdb5339f9e48d26e1048cdfe4889c81cdb1161c4099a293c1b7066bb88631caa929c7e56b75875bdbc0d20d0fab47b00b1d5eec2d0e78fbb3935f3db3aa9f593a5eb2a3c639e1a2ce4c382b8d4b099e489c075d0a40c21867be740263f8384df972e0402fbd16aacb15ffcde5361bb9a8cb688f80b9868979295c8039b285070965d51a2e3328ee5cf5f00066a1ac9279faf18ad241abc9e38394ae77fd3af332240f09b28649c899edc2deb3d7fe6df6da1b9ba548ace498c190b82c26b32c60608acdc9829b6bd5554734f502cc095a5c6e7523c4fde16834ba3af841a1e81405808971c19aeacefddefdf72f4edf7867e907728836c5583cb7780e5a1a42ae26060367c4c332412fa520edd43b0d455b8d33bdbab31ee8d2a3ffed61fa5a17f7113508acad7370d67963619c80f6ab86697636acf7a1f4547f43baeaf188dbf96fdab735dc7109f9ed2ce1db3f3b7be1a96f96e9df1e2fbbb142cda5005ee088f1ee206b99490ab8786a8cc99dbac19c5a6f5ce368dda01f6c4caeeeea01bbfbf9ab674f7d3a7bfde3d3b75742cf358208e0622bf35e05896fa851c44a65dfb1c8b55a33d6834c3bb2720fd4b77ca4c3a19625450d1e2524ce638c8105213d0085f1b07a7f4db22d1d4fa7831f5c19de382e8b13803fb25c68126cad69cbe4129b7c7717766baa425a9f78f7eda5ce27466dca528b2432f321c800e0e50d95182a65d242ee18b870cb76f1b702d15fcf4997c7035559f65fc2d78ea7e30b0f0dc3fe4af7134e9c4756a8cc00d80a641b1a976de00a6a160d5bef59eeb135d7af4ca926260df19f746538a2ca6a4691000532012b0248b5d0f1174e5fffe5c9ef3f5fde854f323005b506bdd712b206078cd7d74ac2d675f56ed485e618d5dbc881dad0b5611c1e1cdf381e50ac71928e4a19b599660d0b65c86676e8eba517ee3efdc7b6ea70894432ec08e0a5979411f02646eeb983851116a8b3737bab990f3610eb1077116ba5b6edc6ddf757d443cf2fcfbfdc41bf44802f035e53cec3678be3d200f35c521b4a089bdabe16e81b0dca41d89b8cebf43ac84c033baf00fe514f0b1906a539ae72d9fbf9fffe536b5f5b0cba3c7e1ec088036550ab3706002651a40d279f339d43a7f75e72007f5205d79f573c4c6d2b66d75fd9277bf367beb7d18319b4a88e1b7062887b0120cc9a4518574272a0ccee3c57d7d45c8654a8876347da095ffd6ef3d5ceea80fc67eb5dcdc6289f6078320323af320550adb45e68d353eab808918cf6ef5d6c03fb7ee15b0f3fb295c94e99516d36f59d02ff192ab5d546876463547991cd9dbf734f267bb274ece1683a4cfb4432af531d9e6db601f8a2da5c611be4fefc27946178fff727efaec4993d8c4d2481b3810ac3847095624d8c1a416d179ba1de379a96d0f6bef5338f36a1dc8c0ac01ff79fc4a3ed1797d25f002c4ba834192101e40254c82a2b74d0e1280c5b8f06c86d68b5371eefed2fadeee406e84f0e87d3a6477f91e0176d04d73b98f2ae9eff9bcd771221ba796bf6e51bb3575e38f9f083251286212cd8797194f6a1226f6d00f538408fcd55baed502b23ff602872e27a81f0d572235d09e416fb7b7e6e3d73f7b50feefe74a54210e6b8c2e00373c84c7972300bce600fa1a866d1e9b1340cf701c6dbde17cac590486ff52577087425ec0ba22d393587c30f29e96315beaa5c2cfe57e03cd6e3562d43dc28196c6659cc4710881d41f7af9ec69a27bf7ae1eebbff4c19003031aacff2e0927415a1daa00da30c80aea9c03c8b028f48451f5653c1bdf13d05713ddd707387d717436d2758d0d7040388550deeb746261ca5e421092a08f73e26aacd95d1839cebf50c694f616c2efbfb83c934ec95410bfa079371c2267001781a741bb4532c7ab34fdfffeaceedf7979a51a42d0a042ca92a9254548848433c32cc64128b0c1ce570cd7c6c060440b807fff6687c63bcb68f0d25d99bd47ba9441bfa010d39796f59955daba48d44f884044f1eb84d59f11c5d5640505d49fe79fa30c2117c3f3c76389e52342c8d8f47d3ae36120e4b382000d7c50cbe55a6dfe81e5854a024c81d3433539966a4b21b0f8b0d179f9c703eae46c3e867620b6ac3585d29370e8601701cbf407028d8cc9907ca605d99e0e953cf9e3ef9bb4bcdd7bbab0032c07246c5a081424225d9755e536c1dfea95bb74904f2a6f4896d28c1a23ab1e13f8f3d0607ddb01f2dad0561ea38d83ffc037584d0a93939771030c52a670965633c82f50598486262d2496ab953f31009b5f5d2c2bdddf98008c3745fa1b7c1c90f57026fd2796f6da8d4f8610bb3b5c818250842e0063ac936957f67c038a4ab50829522c05e2bd0969ebdf0f47a19a094178df686999689cb1c8bc2e18335462fb3a4090711d6425728b36f1a99ee051326d7c68f0f0e8769dc1f027d045fb9f0e8a397578cb0c01d4b03e5171e84286599124c60212ba1d78682f4aed79cffa86db1c7dbefba817913fee5f6890e198a2a55c0c1f3eaa1a870025a38070f00b4a61deb194bc1d634ed60383d6e8bc37553f7a0289cb9e8fb3f79f2f9d3673f3ebbf5fb251cd139c2ac3a6b1495a563b5a079e4d41acda06069c13c745bb5c5b7186fcf2896c9707f47890cc4edecd9f7eefefabd93e7fe7d19eb8d200240c2343008cc2fc3e9d7ea43520a242cac15e6341350ee91a378fcc615fcdffecee2eb47e891bfeb1e59a4dd21df1aee01f4d0059c87a806f4009ace38b546df6fe07f3448076db67938f97e53094d7f1126e7d6e0749de9741e6c0824f0f48d17967b20226468701493340346441f15e1605f20e38be25bb0210dba446c08607c63264b770edfa13d3cb21f6e94a37e42b2119485758522a958bc4d3e830d67a81c5858d2146df7eb55173dad19ab751f4784bfe20e656fa65e6c8c69ca2001099445155761e90cd656ac541d6073c147d61354f7a8ec6ce24ba5d692a6d33299f6abba7ca88d329d7cf1fa6af89f295074ac9c3945fe9da3e24e507291420d0a28e8bc3d5cc726c6c7df1f068af895d1f071c23c34e0864200d66b6ca08bc19c7dfcc2d9c7cf9cbdf80b98717191b2da40bc6db0cd08cd4c4a1cbf2b2581fc57410059522d8c8e4d95c47c208574ad14ba0d47f7381cd8c170d04d44e01441c7ebd3781d263b1f3bfbe8e5d38f3f991bde379e99dd7e134fc2de322083b60a478ac2833785cc420209a1d350b8a698245c13f5d9fa263141dd434439bf3de0e0e6eb41a17b62f46541945e07246c01482eccc769ad23f40a4c465401d85029c0a8501cf7d107cd580c6c510adb5628490a0ab2817c90223c7c237440b4bd2d996faae4fbca948617da32f9c6e1c25f37edcf068e11ec0080085e8952ab21e8c05486a424037bd58525a9489ec9ae487ebd4669c121c3340def1d9ffe1a9ef8de6473d89e06cd4b0091b550085c33e5a92ed62766aa52ebb5117d1472b13eb5a31e84fd45bfca2e1efb68fbdc854bdd839dde085685b0a5326572014be2d150888a3a090117b6c218bd59f6c3f1750845b8361ceded6c58387de7c9ed714200a69119e0536f23207211261416599255d1083ebe7a1582f5f72bac40a54461a4798fee3d7b664f5ef8e19dcf3fdd464e8cba36a232d449156d88569105139c9ac45591e27e9113c551810c7687529ffb0f2a33fd6c99e487d5ab4965152862c82ba039bc4d8d30ac013efc7c81e8561e8d87935287653ff713a6934f7e75f21fcbfa285e59d6da6780649f29822829ae2f4436ce85c2cf2de06d0ebfee87c995fde1de95e93d15e1e4b9e749023e7c6d19bb041e9340c0e0c7a011503f0aed2418021d2a70fa564f5f5ff0881288d331d900d3db4fb86e256151a83f1302d55849a34b0656d24a1659a84cc0aa402d3a4e823691c1d72d3161f0689e70faa6dfdca66814491c8eee45d4669fbd72f2ec1f5b4dd8689a710aac2182a857c81ee8aa046c0c390340c12ec03edd7ff8e2607a7575fa63df7d7cfdf2238f764f3c54ae51a5e3123f674d4d0a81c401104646e5924ac552df02b8e3d6d0c37eb9803d28474d01c3a01ea67b9bc9f3bfab431bc0d7564a95585222fb44cd245109f84707af67cb7942333e3c1c2ffcabd254012a411294c0cfec426ef326c84e68ec45b0150517dcd88812e03120b80cdec2b89aa503d733c556e393a5a8b36c3a6a1465416c93d6d27db50678cf3c9f76b5c364ce13deb73e9fbdbd9cb5935de14e82cf665b78b544a858e52c1b504b9828b5663255bfc9bc713cfac17890dae9319ed2537a99f4bf73fbd58d9006a3e23d6aa506a1233016ab0806ee9b872000b7bb25ad8792b4c2c0074eae7bebe564b56622e8982aa0fa87923673a72e5c9a3fd185fa4d802218bcb2808372ae4089397017e0211541ad657d885fc8f5687bfafee0902694e08b8089b2999402be0047673b54c7a5ef46a574596c2f828035c26ace66b8c40853e114600afd710c5df840c15b5029af596ff51dd35cc72bf08e4b0b456c82064119e2e85ac3c4abc5485c69d87a8183e617b573960a2228b9a8597419f604cc32b980d7d0d17a9043ef2b8dbeecb025109d3692cf87b6f4938b764c6d9770918d3e1aa79481e1eb68e69dcf9eb8fbfacd8d848b07adaa3c97088ae1bd712950423d6b5385a7f8e22676eb2d260dc77938ae8fef983bf8cb4f4ede7876a39c80a2e192051dc1f00235325986fb2fd11467ea46aa85f78cd70cc7d3f108e86440d9ec83404566c65314956615d0885dbfe474b3773e5a6df7b6a0ecccc1f866955371de2422d770930562667de9a279345167ce2b0782faaff5a01536b17e025d40691eef6fbae425ec39cdece96c4f537473f2b38feebeb9ccb45136c5c94c695d612a96076e5214854acc15eb16b2686d4b6fc831f4b417b7c55cf731c2a5af9ecbb10c61743cc798b205c98b919a4a75e489c6f0adf735f4d4732d1a9b064765323e3e6a8a5cfab6404dcedfed2211dfa6a73a2b007fc8620d3c2b7c135063d4946ed7f097d517e5d776d0339f7999f2e86a5a546f84632e7ed44809dadfc45860e223f5177b7a75a85d31262b686faaba0233ae9692f4351e2d67f6d0bcd3719b65eb1baf319fdc73f9c6f2ad2d858a2964e64bf51acb01c1f09092aa8e463a996d8ce6fbc5ff60381a36e2af2db5e4c0eb51571e5ff42edefddd5bb3affeb8a10182e9a29985fb9791e3fd4046014a9caaf0c22294656fb3a41009155e5930493834d32bf8e735fb0c5ae6cf2e6e75fcfc1ffd14d61687755b74cee98ba3e3fdfd5ed7b41f2783722006bbaaf01e1ea7405ee96f2e892550f2401ec2c3d9465db9ad354a4a4a123a008b525b886d5524eb7030dc9f8649188d0f0255b2935fa269923487a3f30b7f8f271ebcdc3dd219e204bd874d74d45264584e409a4089546940d378bbec9fb1d234d5709b5df7c79369da1f8e0661386faef2f06bca7776f0e4e7b7cf9efaf2ecc99fc0182ca3ca3e665644701508a80004fa6c3dd07210c6519065114d371a0e7addf87fff10886bb447ae709f96a45a48691ce0b6c429cf57fdcb53effde5a94ffef2c37fffffde7afa2f3ffc8fbf3cf5d45f9e5a763649d043c6339595f100c2e0401945042c13441fbba64e439596ac49a312345eaf38dc8ef1dc5bdaeef17cab1a15de08f0137e19ea1958a5fa2e6e0af4aac1efdd9640f52de7db7287d3180df62acd5835ac69eb76860be11cef921a800334496b7d4e7e8dd447c3c11869dc51d0a1649140d86d155ad9ca577c0177cd49acc72eba61764d7ddd41995e19536bad6cea907176de003f2c9cd1cd5b777ffdde4a795d87420d055a0b0881e22679eabf6400d0c04a2e27eb56a79835d52e86d0089658dbc872327a0e5380ea49b9f7ccfe6636fa43f3471f593cda1d8b2d2a735321f000c6a618694bf68cf230341b48dd17956d1d646a5bdf7a496cd3f2d65beeac32dc02d891048dc6c9c7ea681090c855662088789e7b5c59ff3ee67df53ae80430066c262a590426924d2116e1a137cae59ccedbc1d6a4e579047e338630af8fedef75b23c4018e029ac8781a24e5513398402b0dbf9b5369afbcd7e5f3b1c510a78bcb306f53b87a387ba0716dd0a8c25090343e90721a385458599cebc6af0a4b85e117bbf33a19b02a0764ad48a17efd9d0b74b1c8fa738a6ef1e0da76bbe3c91ed3451802b02aa0273c79444f45e13a0b1653dc0d4d31335af52a76cdc0d1a52c258dffaa76f7f367b66999830d1645f403b405143a23e71e7a28b3267f289eb353a566c8397abc7c32bc76170fdf14c4ad1b027d81faba812b543cecd9096d9fbbf987df1441772ed5e39651fbca7bac4a88087356716ec89a61e1ae1bbc9644d7a4ae8462a7b6634b7b307efa19797806d5addec9d3ed88c3b8551885ac61c180b4e9ac4a1ae942d32f55ce3b011e79986fda66811c0bd694cdd0c809fbe740bde737901ce88224b334e077c9d97a269488882f31042754a61e01041b56d3b1400b80c67ccb4db7518f730127418ad99e83d8c4c1f16109404a304739431b204e1a88585980a635b4532bbec44984e43baf25714a73485c9a609bd4549de13cc85053029a193522595289885b9c6fed66ab3dd7c2206136b95f48cf597d197f954d4be9d34d3c540f45747d7698861080037093248ec92d54c176b75e060585b186e533f26c311b4a30ce3984239aaa98a50924baf173d42344c8d1246f329119c69a162938188b08adec90428a7c0eb63d4492ce62651fc8fcf87b2ac57263471f72639bfb7c8956d131998e7177e76f287e7cf6ebdb16282a40b5ce25d6902321c947185e3e033d73e84bade20dc44fcf5b6099a1c862690479d16c0db8aea29e68bd2d72179b3b73f5c01c95964c024d81cf868aad864a06e5e780b0fe1178845d2686ed3d64aee2850a66288497f62b0ad865866586c24762c14c712c964a0266066189da80bf0ca6a91de7cfc667f46b41b48df2f4cab5d39a982a3e7284c043d6239d850a8ccbd148042d2fa359fd3d395b36c702b540adaebea2eb75feb1499730dec5e2a15425bea732f9106f4797275617dc6d239ed6e9ba1d7733e0565edc98e946825c18f45102e7118d54a0508347609da009ebe3e8252af8769ae5e3b04b12a5478106a218ed7f856428e40fadc70de799a4b9766379f9b3dfddb8d4e2cc62cac6c11a0c778415b0a856b22b0710a90be4527561b9a6a16df2c45de6e933c9e94a33c6c9a35c3d18dfe7cca3fe2990b0fad3cd41d867531889c9d2e099c88075985a92287a47448d5df171a0561ef8aa27b43351fbdbc1a9d836b4d306409c6d414f051d8312e6b841c064be34ace4be57507b03fa7b9838a03d83509ebce674fd0a8a1177e76e7cb174fbfbcb939a70528908253a094cee13b6ba5f42a8f59173064fb578d3b0f436c2b8e41fa97a3a7a85b8d0a321c15d37795f92d5f9d7df0c99dcf57328cc22a1a8a4913390a74bf1a65091a9bc8c097bb01008deb75343b717b34db32ffddc0bf5d13f9db9960a76fb4c5905de80632280140356840ce820611d1cc8960354de8583742e7c0d025599a96b4633aecff7de1f45f6fdf7d73591a5432ab09acd4a91ab01e8079051281d7c7d544efd655925ddc8a9c4ec67be3e3f6131a9aeaf4e621d8329c176cef6212c92bef9c3eff66c75269d0386fab1082071fb1507ffcf42412f00fc54d21044083225151866c992a25fb44fb4911eb94bdf743a28ec2fea4ec98007ffaf24f4e5ffdc5c91f969f1202e706f0270180e1e3538e4c47aba3abc2f32a9c5c2f17ef433be0e95bba819d379f9ec215a73110f3ae022cffe5aba79fbfd3eac45206b58a0168cec1764420dee2930d44959dcd32ca2d5b40d1e3f5b9ef4b0e524693f1d195e3b8dac3dde7279ae7feee382ebd93ce2eab62e8e3ed3218313c30451413e070b541ac4dc2e8fbf43092bfcbc7b8804728d97f395cdbe114db997c6db67fa907c2e4a0144d01ac1c8c5c6748b84a4e189fb40a3d25717ac7fbaf70b01d02d0e8e13c81b731ad3702e2960229ce5a862235ee30385f72818588fadc4968ed84ce6e40b252a60febaef6e1e98b30801a2ca92988060566895219c6605d80855ae10c6a536b602ccd056a5a58a984c7d3445e0517b5e313bbdaaeb4a68e833860df3edae6b4d5520f15294f9171f6409e4e685012977ca1d02117496f89a1da1a4d465a506814219c64a124b7a3989190d02fca8f7658b7ed057cebe9b35bcb04069c612ac58a10c943a7002f0047d57c3485031b136bfe99b5098c8dd44918c7309eff877c00b5d67241e3edacec70769b423bf9e35bed28c48bdc37f50c6d089fd9c08102b3d13917666285e62a4eb9df60ca32956371084d64c26f60eed6273789c9a392c67ba3616b0a7ae3444d08152ef2e447cb4191a2501509131ee4478484333752430e6382e7d6d16f7ae89d08f1ea7054c7c73ba20fc3c371be70f7d72f9dfcd3174ba4d87c548f84b8791e69a405c7dd07903e50ad94d4fa27d3f474ea5d1986c3abf3ac3910f16ab336a78f5b60ca2fdac400cf5efce9d21b8017b77557054a65b404188e3ca88a43005234d494685d0d71d51b2872c54d0bf7c0ad47aef726e1603fc409ec11f5aad0f552b6d30312706d175db1dfbc1c0e2e7c1d04b51cddb8b0f91959a59a8cebc7a6032c71ce49d8aa8b2960c5c22dbbf6f1d39cb5b649a1efaafe3a9c4c219147ed209a1da5575db8ec83376968f0fa183500044ac2543807cb227061b09542253c115daa7c1bb3f15e8958b46bdfb34d7b6332a4c7a555a5327d345d65c205559852d0462f08b3d94dcf78fe8771dce76cce8dd12285aebf2467e8d321a2e7853eaa802aaff0e73ce4b5c8695fbbf8a84c4b9894764ae43c2cd236de6b483d48c9e23314bf7cf5e4ad9fdeb9fdea46a332356b4b9a176975b1cea8a252941009621180328b3234182c6098d641f70fd84ee11020b51c8e8fa8647e5e09d47721df681f7ca479f0e1f1ea872b7a9f8b3201d8a2844417833f0086a98e5b0e64cf37afe57e26ad503dda95a3b2cb677ef0dee96fdec2a1ac963ff0e8052539c12ab4f2057b0858b080513198dd3572b9bb84fe50dcbbc50e5fdfecae038df536009fd0df011c52626d20d718a96784d9f3d64d57c2f8075706c36f977668ae6e0644491276a8105b0b04ad8ed9012f9034bf1227ee34ae5d401b2183f02d85da483affd00c94d6f3c6f175dbf4d8f0a08992e2ca6ddb19a39c5da2e4779e3bfbc37b1408026da3b2a28eb972a6b22d199ea7666569a0650a2c579a531073cc5d7c14165bfba674deafc282d1a0c92f97d615354507ed47016965a583855e64777ffd5e33477b59d920b1a6041d087825c8b5a54282cc32034b0d8b0c0e6591846d3f41721d9b877465589ae96c87932b34a0a0eda2c1c21a567d110b9dbd7e6bf6d327ef7cf18ba564794df1c7a475545118ea54b726b9644251c026eb65248e3e0247efe06400a593f131a112b082e6b6452f189a7df9c6c65c34af2b0b507bc3698ebd4d54eda958a10f531055e7f57af145b0623d77050f4e9d4ebbc752b4957e4b09d3b0f6306e2c52289e66c225a643b69a22022cad57ae35107c3e03ee7ffdff49270193da7b0000");
		bytes = ObjectUtils.hexStringToBytes("1f8b08000000000000009dbd6b935cd59926fa57147c9a891079d6fd12313111b6e976770cf2d0166d9f8939138e752da5559559546649c89fc0180ce68ec1607371bb0d6d77bb91a0b1311617479c9f72acac2a7d9abf709e77efdc79dda992878b10aa5db5d65eebbd3ccf7bcbfff93f1f48e383c141c9c3302d57077b313c7051888b0f08639d90ca32fdc0c50738fefde6d7bf76e17239ba364ce5818b0c7ff38b0f0456742dc546e3bce159719eb865c2dac46a0ea1d2835c692518131c3f540ff883f87f26f03c17cd0ffe5f17db1ddcb8314983f1a80cae4d1ea01fcd1567d65ba7687d867f67b75f3f7dfdb7b3cf7e387be7f67203c5b16ab4ccd2ab988cd4499b908a72aa32176b6a37608c6796338d9f3a6003d6fcc06edd6bc36be3c1f512a657cad1e0f0687c6d98cb51b383ad1398bdffaf271fbd3efbf0add90f7fbbd8418d554b6f4a943eb0c865d652d42275c9591763d4da11a866076bef1de3d5c1fe786fd29d2c2dac185e5c08d92dfcf078efd1f1e59cc251debc01111c8eb2c8920dd7aa9ae09d30d8736499e9c2d76e8099ede5c3281f8d8779f1e693c1b4ec97c32be3d18d072eca9e43387dfd0f67b7defd7ffeaf3b7ffed5c993b7360e838b648ad3512b2e0b33f41f1b4b96aa6ac1335bdd0de48476a3d776d35cc6e4201c4d0f8ef7a7c3ebc3511e5fdf7119cf3e33fbf8e5d9fbbfb8f3f9e7b39ffc6ab105a345e2c9b05078b1158bd59885cba9b2c270367a6d0b24e7e71e480afb6594432b149c73897f8437f37d9cbcf9c1eca56736ce01f290628008c8108df6a92ac54b0cd94212b34b614d28e4f626482826a9401bf6c3f1289168423e0f4aff515ca6271fa5af3f3c7f7ab111c881acd0a3820d24e38db7dc569620ac5c565ee5ea6918773fa7d1588a072e5a662f3e20add7c67b27557729fff6da9d2f7fba291456a82a4a112a541d228f9631564ccac5e712a45f1351764f23d1c8c7fef05ab91ef6f70fc321ce258d2b2cd074b8eb70eebefbdeecc7bf3a7df7cf273fbfbdb225a78be3c185908be24a69a98c94c6071354e172f37e36b5a695d3323d3ebc3efc01b41226b367e9fe273b392dd254cd39c9678a11ea9b1976a38b141102b37e337efb661e1bde180ee84ec60f5c74cc1a1c9c709e29239493739379faecc7b30f7e7cf6dcf2c55db45aba5c932b2e49964586bd3456a56aa465d9ceeda5a5e36058d50ecc9ab55cf8897afdeae070ff786f38ea3ff6bfbd7ef591f99717671e6c0a8249a9745151f80a03cfb183ecb9b16b86927bbb7de607e338dc2fd3f1787f3298dc984ccbc195b27f48b24e17c4bdb2ca31c337cfbe7b68a110d5c42879363ac8aa9db1de5b9d6381add4d9a76d0b251f8cc7c3fd2c18d75c30bb2d0725603b57c211045092fcae9d05bdc39ddbafddf9e29f165bb0aec019a590998fa654a3a00aceab9292284a4731bf03d82a69796bb207f8c103b776136172383c2a838383072e6a4dab3265a18e5cbbf9aa972ecdde7866d557669e4af12ec32a4a1f5db046c8ea4b894e58e3f85c0fe146f00565480f759faf0cb1f48bfbdddfbd75f6c49bf00fa7377f75faca338b758bc95c4bf8491e7d7541ebc803548fc550151dc39ab2ddc3062d743e1d8d0f9b3bedc529f7fe96f99e5480ca97a843565e70abb487e3563ad8ac6b36d1add924794f9bb4508949ba7278fd683c8241dab1b5d9cd5f9cbcf9e9ec8b27664f7f72b27233c6d6ca24a0132e48c36233666480f3b029456778dcb4d2f7dacd78349c8e8f9a1be246d13769d189c4b7caf4faf8e8eaa5ee990ec23807a756234b59400a59705a145f73e2597b5c5a67148400866a21cc80af09c6248cae1e8721ce62383d0ed07920411c329706484619ded9a3af5e9abdfdcf4b63549d0e58399724140f251aa18d65c10bc9a2f56cbeae97d67972d576c037d6bd313ebe7a3c20b442d69fd13b4ba7bcb5ce994ef9be78ebeed37f5cf1cccc399858c664095c55c03516bdd4421a9d9cb28b351dd4bd396a3b90db4a90c6fbfb61afec8004cf7f317bfbabc59232074dc01046b5b8e09380bdf18e2b5b83f730456b264793fcf78022fce6304c26fdc6f6e4851fcf5e7cf3ee6bcf2e750e8202d1e6267293522dd60b91b249d65a8ddfc54d07d7aed9bee4f8686f30191e4075866194ca607c5846adf10d87c3c102a8ea3e2442d0ad0fa6020f856a01caa1775a055393cc30ffa128e992cff3fd28cf0c301af6230724640272b62ede578ec3f5321c84c343ac74b54c0988e02a25a37b0010315cb14eda662fbe74e7b3dbebb441da8b0a320695684ec9e5083f1735aec5729e83e5702146081d65648a98106f4c31f88427c9b7648a618cf9b644b466e078b403865ca22f3f8a2fff974b8f3ef48dfff15f172703f963f03d8169681acb9ad5a033034883a766909afb05f0799c8e0fca683a391eeeb03e273ffbf1c9af7ebd58387a2d0d74405befacca3c021edae84a2e8281bbacc1b273b85bb787bdb01f1ebfa1fa49ccddcf5f3df9fd7b4b0b0c4f60390c704812875dacb64979c912aec0315f3725a21f8291bc5d0f574b3a9e4cc7077b65323d3eda7103e77e53a7ae2c7b1884c80b533c825815e52384035b6140bb72535d775d48797c5a8e46611f6b1c35a6a2df25bcffb3bb4ffdb645cc6737bf9abdffe315a862810415bcb5866730ce29932b648f7c95ca751d309de3153a1f7563ef70d2efbdffc7371fb9bcb4cc1274ad1aac05e17485fb60c9f61b2ca5420865ed0c9acbe1bd6770657ab07f6d58ae971de4e9ef1ebdf4f085935f7e70facef3a7bf7d7e76fbe5a578d802cc9054f1a5e658a590d6e8e88099320379598f2cf01de291c241390a87e1281cec1f1f847eb9f846f3d023f4d0c3cd431d5e55cc0ad882602b5062f6c0490e703d0330a598fcb91c6158f76f34b0e0b09474056a396d7c14f38a944328b00d65ba8338bbf5e1dd5f7e020138fdf2e672073e304042fc1d7506cf0a051e2a7ac8628564e43e1519340baced635a60c447d3ee4e1e7bec200cf7b11303a9120d6bd0ca42809c9adbcd7ff887bb3fbc797af3633a2e58572f1a61f40086f0d7d201c8612f2973170bc76902b604e6e961f29b4a3178d1461837fc662713070793a513e9d58936b4b0388654a4c321b31c5596d9d9aa01d9b008be29423dec267fdcf49fdb1c368faf8ff6c7214fda0d6cd1d83b9f3d7ff6e5972d8c9dfd7cc9648b563e4b0f68666a88023aa8528ed9d704f2289ddbd4c84d240b251c1d0e477b836b580500034b2b48af8347ea28dbecfddfdcf9e2cdd31ffee9ecd68f96512e991c8801f017ecb407612b4a5891712249421e64075a801c1df17237e02bfe9c56aed707c7877b4760bb74f38daf340c52286cc7d7befb8fddd73ba40444084a0169e74158e9250f5005916d2e56c06f6c4b1fdfd640e088c3fd320fafdd2baaf6bfbf78f6eeebcfcfbefcf87f7ff1dcd245e145c92982ab65e16111a08409bc159b08404ef63c1fb1307b6504265acad1c138eff00e7f337fe2d278e50894001187234b9e251db4941e079f39903a0f167e6bd345f61aa1c5cbab9d2fbf7ce104922c62d4b142c0f15639c04da69ca1008133beb6a0e9046c5bc762bc7a703c19a6fe771d5e80adb9f3a757969ea6680320a6732d159e25e187076819c8a233b03a656d59bb8e16bb70d577c650e8ff3e2a932be3e98e48cceb4f52c4eed71f9dbcf49b25290d25049600e1828b2c261399f7a09f0554046aae373d8d9893d29e4b1e1f8474d86af496a3f9efedd73acc1521429ac01f0f5a49101b93646522f0e8c079d610f9397c8b5ebd4e0ff7c77bfd2ffd1d88c0c3e3bd7f3cccd8e3f704e36ac0d440f815420257c75c8cc0423557973dee1d7a07cde625c575b8b14bc60e267b87c7932bfd5ba0274e3e7d1626f5e4a5dfde7de2c925ef0b42c4c0534826c1701b5378e121813682f9a558ce5b7a216d47e3eb139270edf1571ff2fcc3cb67bf791696f4e4e5d7ee7cfee9d2c131cf2a801f189e0623a8d5060a1483ff818c332eee5bbfe3fe314586a657f677a8f7d7bb071e5e017a2a0252c0bfa752a162569550c094b8c49f855ab738495fe4feb8b9d7a38628f4c4ca3ff9fcf4f35fce5efcf1e9eda5c47b86d7e32e549913640f185215e99d4e5a721589b7ae47a076b9332c9fc27e3ade0f0bbebf29f567f06037df5cf560b02d9e67a79d62bad61cb807f106b2923a159fc55adca38dc5ac6bfa7e8861703d4de8ae29a4079703efb520dbb3af6ecefe78f3ceede797aa0613662354a7128d3790235da0ee9cc74c8e45af04bb24d3db0bb611b6ebe1e8288ca637f09a7db4f3f4f53fcc3e7ce5ce9fdfbdf3e79bb31797b9008d1bcd8a4bd81807d25b403571ddda790ab9096b36edf7e6513724f8fbe31b83eb43522e4941566ea4271eb0086ae09d4f7ef4e7074fbef8dde9eb9f9fbcf2dee9bfbfdac439960100e5a1d0f0cc6038b166a81824de5605476e53757e7ee816685b7b8a61c88159b5eb6950c7d3f1e17e001707f17de0a2a1d03bb454036a0931dfc6d9a7808f1f2dd7cc1e2c16f0dd5a9e94a338a7c95ca4e4750597c9cb402f8028290378ade8f52519da311e4d03dce3ae90dbd997b7a0d6b3179e3e79f7a9bb3f7f65f6d9cdd94ffe7503d0d70adb97a2a9804bcc0593e0d04ae25e07254b156233fc7b1f74f31036fd689cca6432deb5b347427a64f948a7f80244aa9400ec05fb23954cd814087ee4060ec8b1bfc2fc775ba9c3fd721046e079bb7602f68d33da088d7aa76ac23e2051d84ac4af2c16700daf8b4dd9ebf31c6fa31d7172b8963dec73418f82e46ec5647cb459e05ff02966a2a0907fc9ccc28428afd6c0c6dcf5aedbbfbde3f1d5a341855e8234b52118fc0a1c012ab1508e679f39fdf075da97620c7246cf38ae025e2e1b970ba034f61b6b01c2830fca39d58e4968a5386f23f01b51bf8ed150ecdb8926edc8e036a4166ca9932011f849eea2905074f0f426002e2bc5db2b2eb218fc749e8b4fde03cf3a6e2acc0157cdd2dc4b67e9844829d6b56223d2399886ab657c3c1d95eb0078cc30b533ec09a63f7bf1674b2e014457acc185570e77abac77c5b9ccb980e68a6a3aa3c08450147a52300a6afbf227251d1f15a0a883f1682105b2b5665bb80fd237bbf9e9c93b2fae264c658e3243054db2123e580be1a2a0f807a3acbaed2c8563ce4a72093812d08b7e3548e5683a1c4da6309b3b9de2d9ad27effce95f66379f3bfbf5d31b364246209e825f842b32252d149c04248ad924bcc8624d1d7a247219ded98d42cf3e7d1adef8e4d97f03165d2e0c5209a320013d40387c851c7a674108b34f26e41e9fcc56969ebffd0ec5ff5afbd5160b2c232c456766609f8d4b0c703f281b15c32f1144d7d6b54cb933f7b4411b047ff0d8631dbfa53b3096e3ac18bd23b34a186117bad946466fbe0bc95517254496ab766fce09e0bf1aa5a4b41a341194d324c1a387eb4ca20b8b7a6ead2003690676434517c80cae6354d274786d48debb37fe323e80ec5e78284cc3567d092e2000181769adc42fbcfa081309f12e8c6919b69cc62e84da38b0349df44726cf9e7c1db773e7f63221837bd0262a1782d39001f0025e40c354015e96f8ede6ba9b76b95c0ba3bdb23f3c3e9046c3470d1622d2583420215858d9055cfe66f5e9a5c3842dccccf9cc716244be41f7b5855efa64c9482f4a5bb866d22f6572c3401c1f8e1af7b02b04374fd93ffdf19ddbbf5bbe3fa111180620c458852c22f9cc6a2d0e4e0a706e4b1fb6a07118e663d8a67094aec4f1e31018b8052c4cfa2134876df45d4eeae4b9e74fdeb97dfaf3af66b7ff65490c38cc740429e4d5da4a496b51c08c628aba7ac7161929230834535c7edd3412344ffbe3e34cb6b02fc274fbd50d2bc80d0e2527c9808e9c3295019c06e9294bcca107b2c7130ff8c6792f68f0d72693218ce088d2133b08d9dc5bb7372d018e533280895164178a0607890aabcac420279b8ed8ae599f6ee57981481f3e1e5eb8f3d9e777fff9ade5fd160f5d17145b8104391d19b42ac20a25c76ae25b91c54df95e96014c0f8f4aba52d2d51d65008f3e7254bed17e7d116c80770b3ac3784819b34a787797b01dc5811bccb9b46b258e384ffd6f48f446f8b032e5e1592b8321c755f21a4a816265196b0d31af870ff9f6bb76eb4da63bdef1f2df5f02e968ade9125c450f93c509e8560dac0337069c0b3252aa837f4d9babeee4d6215d3d3e84fdaac3a383fbc8786f3cdf799b00f1d61a8c5304d812e025577266c972f06f61eda63dbb87b7995c390e0765700432bd5746c3635c82230981410319b4bef32edfc603a7fff1d5e9937f58de3cee01cb6923387326c06e1527aa331ad81bc4bfcbbd6aa031e04fba093170eb41e485afeb346c706338dabb311eedc530fe5e9b13fc9ea1084e537102b1567a115b5d79f4c1f6d1070da931b4d4e21f7816677d1bfa5770cfd598229dccbe08232959cbb38d163089cd1d20c09974de2c22231bf667b56c6c9b0e346563cbc01fd8116848b570f6ba264d36c77b16bcac01a85cf60090ad05878f8dc703f09a4c32b09de4698ced8f5e3efbcd93b3975fbcfb2f2bb9624d80df42f5230f093e5e84e043a6da4e402133a7a706ae88e18c76071dbffef5fff68dfd71b334ebab1cbcfbe69feebef6cb93373fc5af64a7c07aa0efcddb47290aac2f78712d0a1c3d8402fd09a026d28321af195fd5baba75b9d82fd36b4b6fbf3f2c647b39854815093b736e017beefce995b3df3c837feefef3abcb30a4ca0677200bf8719025baa2945049abca8b97baab23f53035a0197d8867e5141ea51aa51dc1eed76fce5e7969c3f7100952d655975cac3e08d0612d59700a3619fb323dafdfcf87017ac7b0cc4d3ca877f5b7bf3a7deec7a75ffcececab5757a342d6a7e22cac5394e475a2612952491cafd1827de44d07b4cb585d1fd6e1a44c4101f6263b6cd5771ffedab7ba3417fc283c3cbedcc56a788512081f924f786d1209550b15d43232987a6930d5f955824d69dce09ea7f1d1cbad1480a4ae821f078b918cac865ca1099486c75b83dcd92a8a65e6bcd3b8b63fac651efbefea8fb07bd7a47bc045c14679e7abe8a9d94f7e7bf2c493b35f3f797afbb3a587d6c93a2b88893b2ebc0cd96940a20438148da85b1e1ac7c1c4c0f48747fff6d2b7431e8e7758a2d73fbdfbcbb572a46a38fc948b0002d9f05202f96558056d701ab56e61a14debd7bc7a4b492fdf2b30d104a6d75501af0de055e0a3aa10dec31a523822170b8528f0a4e701cf4dc0bff80d9c0568f2f561dea3a291de1cc15ff1fd9dd5f054640d6a2ca88e2902c7702a9fca5c0158b0f47f14d3ba122660c0e1a09faacc9e7eeece9f9ebffbcb3fccbe5ac65899613ea99074b6969b52805f23703187f3a37c82bcef30fa4a867427d878e8db972edcf9f3bb273f797ff6ccd3b39b7fda2cf64d54c7a54550ce531d9b01ea11a1260922efd35a6cedbcaa9223981198f2c3dde51ccb27ba781a5065a43a2e0dbaa800a5a1c4497adc4f51015fda8cec6de5ecc9859e9f2cdcf1681763572af128b8a955b15c7291b2e80a7a6f5d8e3025db0524fd017e789247a8b2edef4793e9d13138f478b4c3ab9cddfafdc95b2fddf9d39240415b4a850d87573712b720c1e03d65125d54019c6eed1aee51c4b2948873ec7a6bd33784c109a8b3f32e725b52cc06845269c17c85298996af577ef3fbd28fa6d036cf6b6a5ab07b1fa8b8f7bb3a257602075401708047538ea09d00c82615990146f8163fb8c7260fc3d1f00754b53f2ad3452126d786b27cde4bc34c0745bfb5f2408702bc4f0c9e98f99c0d6319caabe0850ab76047c1759992a60ad3fa26df3e90ebb477796bc3d174720818b28bf59f3cf7d3d98b1fcddeffd1e92bcf6c986070149b2a1912c90448923146e748b188aa2c546a4b87b67d6f3e82011b8cc6d31dfa73e7ab3f9f7ef8d57f3afbf2f3d39b4f9cdefe97ffbc42c35d2e02781b720249312ed0b1685fa4292a85b50a0bbbabece7d1e1e1ae42c9975f397d7fe9e8924845c3d9894a893767b5c956061d613e0d99f6ed08784fe1df5e20da4d05d3dbcb7df6d9c9b32fdff9ecc3d99f9f5a26a5006a83309565b06f0e5f81ff134947e1703f659111c34d3bab1ab4b759f5da5df370048eb14ff57ebde9f6277e7176ebdd956b8d3e6bcd2444dc270b4203e405e60f6aa355526ed3306fe6c23ae6f5d863d47f41812c2d9b16100a53c0afd8b9649f3dfdd5d9cd5b1bf0da8043690f89aa3a715d737659a42a148c93a97ad1760042a0199da41a782266b4c43acd69e24ae1f0b00d2d512d8b2516d113576a234a6d7469f6937fc56fa82af9a275906adf547fc26e9866550b9e689a28d22e6a73bd442a636b0afc19eb030ff328ef85cb4d67c285ef96f89de61b3a7bccbdb7d151e96d6a5075b60a9701afe46208266cfa04e92ffca7a59179301c1d18f59f7b0de27074783ccd8550d64edbfcd54f674f7fb0515d2805ac9c620cf40aa005b743703f53a10f6721faf500c5bdbb7216b1872ec97e9eaff8e9bba7cffd7ca3d84d98984250812ac800f0439440cc9059102015ac5a0b882f43ae9b21c7e634bed7b44f9d873cdb5339f9e48d26e1048cdfe4889c81cdb1161c4099a293c1b7066bb88631caa929c7e56b75875bdbc0d20d0fab47b00b1d5eec2d0e78fbb3935f3db3aa9f593a5eb2a3c639e1a2ce4c382b8d4b099e489c075d0a40c21867be740263f8384df972e0402fbd16aacb15ffcde5361bb9a8cb688f80b9868979295c8039b285070965d51a2e3328ee5cf5f00066a1ac9279faf18ad241abc9e38394ae77fd3af332240f09b28649c899edc2deb3d7fe6df6da1b9ba548ace498c190b82c26b32c60608acdc9829b6bd5554734f502cc095a5c6e7523c4fde16834ba3af841a1e81405808971c19aeacefddefdf72f4edf7867e907728836c5583cb7780e5a1a42ae26060367c4c332412fa520edd43b0d455b8d33bdbab31ee8d2a3ffed61fa5a17f7113508acad7370d67963619c80f6ab86697636acf7a1f4547f43baeaf188dbf96fdab735dc7109f9ed2ce1db3f3b7be1a96f96e9df1e2fbbb142cda5005ee088f1ee206b99490ab8786a8cc99dbac19c5a6f5ce368dda01f6c4caeeeea01bbfbf9ab674f7d3a7bfde3d3b75742cf358208e0622bf35e05896fa851c44a65dfb1c8b55a33d6834c3bb2720fd4b77ca4c3a19625450d1e2524ce638c8105213d0085f1b07a7f4db22d1d4fa7831f5c19de382e8b13803fb25c68126cad69cbe4129b7c7717766baa425a9f78f7eda5ce27466dca528b2432f321c800e0e50d95182a65d242ee18b870cb76f1b702d15fcf4997c7035559f65fc2d78ea7e30b0f0dc3fe4af7134e9c4756a8cc00d80a641b1a976de00a6a160d5bef59eeb135d7af4ca926260df19f746538a2ca6a4691000532012b0248b5d0f1174e5fffe5c9ef3f5fde854f323005b506bdd712b206078cd7d74ac2d675f56ed485e618d5dbc881dad0b5611c1e1cdf381e50ac71928e4a19b599660d0b65c86676e8eba517ee3efdc7b6ea70894432ec08e0a5979411f02646eeb983851116a8b3737bab990f3610eb1077116ba5b6edc6ddf757d443cf2fcfbfdc41bf44802f035e53cec3678be3d200f35c521b4a089bdabe16e81b0dca41d89b8cebf43ac84c033baf00fe514f0b1906a539ae72d9fbf9fffe536b5f5b0cba3c7e1ec088036550ab3706002651a40d279f339d43a7f75e72007f5205d79f573c4c6d2b66d75fd9277bf367beb7d18319b4a88e1b7062887b0120cc9a4518574272a0ccee3c57d7d45c8654a8876347da095ffd6ef3d5ceea80fc67eb5dcdc6289f6078320323af320550adb45e68d353eab808918cf6ef5d6c03fb7ee15b0f3fb295c94e99516d36f59d02ff192ab5d546876463547991cd9dbf734f267bb274ece1683a4cfb4432af531d9e6db601f8a2da5c611be4fefc27946178fff727efaec4993d8c4d2481b3810ac3847095624d8c1a416d179ba1de379a96d0f6bef5338f36a1dc8c0ac01ff79fc4a3ed1797d25f002c4ba834192101e40254c82a2b74d0e1280c5b8f06c86d68b5371eefed2fadeee406e84f0e87d3a6477f91e0176d04d73b98f2ae9eff9bcd771221ba796bf6e51bb3575e38f9f083251286212cd8797194f6a1226f6d00f538408fcd55baed502b23ff602872e27a81f0d572235d09e416fb7b7e6e3d73f7b50feefe74a54210e6b8c2e00373c84c7972300bce600fa1a866d1e9b1340cf701c6dbde17cac590486ff52577087425ec0ba22d393587c30f29e96315beaa5c2cfe57e03cd6e3562d43dc28196c6659cc4710881d41f7af9ec69a27bf7ae1eebbff4c19003031aacff2e0927415a1daa00da30c80aea9c03c8b028f48451f5653c1bdf13d05713ddd707387d717436d2758d0d7040388550deeb746261ca5e421092a08f73e26aacd95d1839cebf50c694f616c2efbfb83c934ec95410bfa079371c2267001781a741bb4532c7ab34fdfffeaceedf7979a51a42d0a042ca92a9254548848433c32cc64128b0c1ce570cd7c6c060440b807fff6687c63bcb68f0d25d99bd47ba9441bfa010d39796f59955daba48d44f884044f1eb84d59f11c5d5640505d49fe79fa30c2117c3f3c76389e52342c8d8f47d3ae36120e4b382000d7c50cbe55a6dfe81e5854a024c81d3433539966a4b21b0f8b0d179f9c703eae46c3e867620b6ac3585d29370e8601701cbf407028d8cc9907ca605d99e0e953cf9e3ef9bb4bcdd7bbab0032c07246c5a081424225d9755e536c1dfea95bb74904f2a6f4896d28c1a23ab1e13f8f3d0607ddb01f2dad0561ea38d83ffc037584d0a93939771030c52a670965633c82f50598486262d2496ab953f31009b5f5d2c2bdddf98008c3745fa1b7c1c90f57026fd2796f6da8d4f8610bb3b5c818250842e0063ac936957f67c038a4ab50829522c05e2bd0969ebdf0f47a19a094178df686999689cb1c8bc2e18335462fb3a4090711d6425728b36f1a99ee051326d7c68f0f0e8769dc1f027d045fb9f0e8a397578cb0c01d4b03e5171e84286599124c60212ba1d78682f4aed79cffa86db1c7dbefba817913fee5f6890e198a2a55c0c1f3eaa1a870025a38070f00b4a61deb194bc1d634ed60383d6e8bc37553f7a0289cb9e8fb3f79f2f9d3673f3ebbf5fb251cd139c2ac3a6b1495a563b5a079e4d41acda06069c13c745bb5c5b7186fcf2896c9707f47890cc4edecd9f7eefefabd93e7fe7d19eb8d200240c2343008cc2fc3e9d7ea43520a242cac15e6341350ee91a378fcc615fcdffecee2eb47e891bfeb1e59a4dd21df1aee01f4d0059c87a806f4009ace38b546df6fe07f3448076db67938f97e53094d7f1126e7d6e0749de9741e6c0824f0f48d17967b20226468701493340346441f15e1605f20e38be25bb0210dba446c08607c63264b770edfa13d3cb21f6e94a37e42b2119485758522a958bc4d3e830d67a81c5858d2146df7eb55173dad19ab751f4784bfe20e656fa65e6c8c69ca2001099445155761e90cd656ac541d6073c147d61354f7a8ec6ce24ba5d692a6d33299f6abba7ca88d329d7cf1fa6af89f295074ac9c3945fe9da3e24e507291420d0a28e8bc3d5cc726c6c7df1f068af895d1f071c23c34e0864200d66b6ca08bc19c7dfcc2d9c7cf9cbdf80b98717191b2da40bc6db0cd08cd4c4a1cbf2b2581fc57410059522d8c8e4d95c47c208574ad14ba0d47f7381cd8c170d04d44e01441c7ebd3781d263b1f3bfbe8e5d38f3f991bde379e99dd7e134fc2de322083b60a478ac2833785cc420209a1d350b8a698245c13f5d9fa263141dd434439bf3de0e0e6eb41a17b62f46541945e07246c01482eccc769ad23f40a4c465401d85029c0a8501cf7d107cd580c6c510adb5628490a0ab2817c90223c7c237440b4bd2d996faae4fbca948617da32f9c6e1c25f37edcf068e11ec0080085e8952ab21e8c05486a424037bd58525a9489ec9ae487ebd4669c121c3340def1d9ffe1a9ef8de6473d89e06cd4b0091b550085c33e5a92ed62766aa52ebb5117d1472b13eb5a31e84fd45bfca2e1efb68fbdc854bdd839dde085685b0a5326572014be2d150888a3a090117b6c218bd59f6c3f1750845b8361ceded6c58387de7c9ed714200a69119e0536f23207211261416599255d1083ebe7a1582f5f72bac40a54461a4798fee3d7b664f5ef8e19dcf3fdd464e8cba36a232d449156d88569105139c9ac45591e27e9113c551810c7687529ffb0f2a33fd6c99e487d5ab4965152862c82ba039bc4d8d30ac013efc7c81e8561e8d87935287653ff713a6934f7e75f21fcbfa285e59d6da6780649f29822829ae2f4436ce85c2cf2de06d0ebfee87c995fde1de95e93d15e1e4b9e749023e7c6d19bb041e9340c0e0c7a011503f0aed2418021d2a70fa564f5f5ff0881288d331d900d3db4fb86e256151a83f1302d55849a34b0656d24a1659a84cc0aa402d3a4e823691c1d72d3161f0689e70faa6dfdca66814491c8eee45d4669fbd72f2ec1f5b4dd8689a710aac2182a857c81ee8aa046c0c390340c12ec03edd7ff8e2607a7575fa63df7d7cfdf2238f764f3c54ae51a5e3123f674d4d0a81c401104646e5924ac552df02b8e3d6d0c37eb9803d28474d01c3a01ea67b9bc9f3bfab431bc0d7564a95585222fb44cd245109f84707af67cb7942333e3c1c2ffcabd254012a411294c0cfec426ef326c84e68ec45b0150517dcd88812e03120b80cdec2b89aa503d733c556e393a5a8b36c3a6a1465416c93d6d27db50678cf3c9f76b5c364ce13deb73e9fbdbd9cb5935de14e82cf665b78b544a858e52c1b504b9828b5663255bfc9bc713cfac17890dae9319ed2537a99f4bf73fbd58d9006a3e23d6aa506a1233016ab0806ee9b872000b7bb25ad8792b4c2c0074eae7bebe564b56622e8982aa0fa87923673a72e5c9a3fd185fa4d802218bcb2808372ae4089397017e0211541ad657d885fc8f5687bfafee0902694e08b8089b2999402be0047673b54c7a5ef46a574596c2f828035c26ace66b8c40853e114600afd710c5df840c15b5029af596ff51dd35cc72bf08e4b0b456c82064119e2e85ac3c4abc5485c69d87a8183e617b573960a2228b9a8597419f604cc32b980d7d0d17a9043ef2b8dbeecb025109d3692cf87b6f4938b764c6d9770918d3e1aa79481e1eb68e69dcf9eb8fbfacd8d848b07adaa3c97088ae1bd712950423d6b5385a7f8e22676eb2d260dc77938ae8fef983bf8cb4f4ede7876a39c80a2e192051dc1f00235325986fb2fd11467ea46aa85f78cd70cc7d3f108e86440d9ec83404566c65314956615d0885dbfe474b3773e5a6df7b6a0ecccc1f866955371de2422d770930562667de9a279345167ce2b0782faaff5a01536b17e025d40691eef6fbae425ec39cdece96c4f537473f2b38feebeb9ccb45136c5c94c695d612a96076e5214854acc15eb16b2686d4b6fc831f4b417b7c55cf731c2a5af9ecbb10c61743cc798b205c98b919a4a75e489c6f0adf735f4d4732d1a9b064765323e3e6a8a5cfab6404dcedfed2211dfa6a73a2b007fc8620d3c2b7c135063d4946ed7f097d517e5d776d0339f7999f2e86a5a546f84632e7ed44809dadfc45860e223f5177b7a75a85d31262b686faaba0233ae9692f4351e2d67f6d0bcd3719b65eb1baf319fdc73f9c6f2ad2d858a2964e64bf51acb01c1f09092aa8e463a996d8ce6fbc5ff60381a36e2af2db5e4c0eb51571e5ff42edefddd5bb3affeb8a10182e9a29985fb9791e3fd4046014a9caaf0c22294656fb3a41009155e5930493834d32bf8e735fb0c5ae6cf2e6e75fcfc1ffd14d61687755b74cee98ba3e3fdfd5ed7b41f2783722006bbaaf01e1ea7405ee96f2e892550f2401ec2c3d9465db9ad354a4a4a123a008b525b886d5524eb7030dc9f8649188d0f0255b2935fa269923487a3f30b7f8f271ebcdc3dd219e204bd874d74d45264584e409a4089546940d378bbec9fb1d234d5709b5df7c79369da1f8e0661386faef2f06bca7776f0e4e7b7cf9efaf2ecc99fc0182ca3ca3e665644701508a80004fa6c3dd07210c6519065114d371a0e7addf87fff10886bb447ae709f96a45a48691ce0b6c429cf57fdcb53effde5a94ffef2c37fffffde7afa2f3ffc8fbf3cf5d45f9e5a763649d043c6339595f100c2e0401945042c13441fbba64e439596ac49a312345eaf38dc8ef1dc5bdaeef17cab1a15de08f0137e19ea1958a5fa2e6e0af4aac1efdd9640f52de7db7287d3180df62acd5835ac69eb76860be11cef921a800334496b7d4e7e8dd447c3c11869dc51d0a1649140d86d155ad9ca577c0177cd49acc72eba61764d7ddd41995e19536bad6cea907176de003f2c9cd1cd5b777ffdde4a795d87420d055a0b0881e22679eabf6400d0c04a2e27eb56a79835d52e86d0089658dbc872327a0e5380ea49b9f7ccfe6636fa43f3471f593cda1d8b2d2a735321f000c6a618694bf68cf230341b48dd17956d1d646a5bdf7a496cd3f2d65beeac32dc02d891048dc6c9c7ea681090c855662088789e7b5c59ff3ee67df53ae80430066c262a590426924d2116e1a137cae59ccedbc1d6a4e579047e338630af8fedef75b23c4018e029ac8781a24e5513398402b0dbf9b5369afbcd7e5f3b1c510a78bcb306f53b87a387ba0716dd0a8c25090343e90721a385458599cebc6af0a4b85e117bbf33a19b02a0764ad48a17efd9d0b74b1c8fa738a6ef1e0da76bbe3c91ed3451802b02aa0273c79444f45e13a0b1653dc0d4d31335af52a76cdc0d1a52c258dffaa76f7f367b66999830d1645f403b405143a23e71e7a28b3267f289eb353a566c8397abc7c32bc76170fdf14c4ad1b027d81faba812b543cecd9096d9fbbf987df1441772ed5e39651fbca7bac4a88087356716ec89a61e1ae1bbc9644d7a4ae8462a7b6634b7b307efa19797806d5addec9d3ed88c3b8551885ac61c180b4e9ac4a1ae942d32f55ce3b011e79986fda66811c0bd694cdd0c809fbe740bde737901ce88224b334e077c9d97a269488882f31042754a61e01041b56d3b1400b80c67ccb4db7518f730127418ad99e83d8c4c1f16109404a304739431b204e1a88585980a635b4532bbec44984e43baf25714a73485c9a609bd4549de13cc85053029a193522595289885b9c6fed66ab3dd7c2206136b95f48cf597d197f954d4be9d34d3c540f45747d7698861080037093248ec92d54c176b75e060585b186e533f26c311b4a30ce3984239aaa98a50924baf173d42344c8d1246f329119c69a162938188b08adec90428a7c0eb63d4492ce62651fc8fcf87b2ac57263471f72639bfb7c8956d131998e7177e76f287e7cf6ebdb16282a40b5ce25d6902321c947185e3e033d73e84bade20dc44fcf5b6099a1c862690479d16c0db8aea29e68bd2d72179b3b73f5c01c95964c024d81cf868aad864a06e5e780b0fe1178845d2686ed3d64aee2850a66288497f62b0ad865866586c24762c14c712c964a0266066189da80bf0ca6a91de7cfc667f46b41b48df2f4cab5d39a982a3e7284c043d6239d850a8ccbd148042d2fa359fd3d395b36c702b540adaebea2eb75feb1499730dec5e2a15425bea732f9106f4797275617dc6d239ed6e9ba1d7733e0565edc98e946825c18f45102e7118d54a0508347609da009ebe3e8252af8769ae5e3b04b12a5478106a218ed7f856428e40fadc70de799a4b9766379f9b3dfddb8d4e2cc62cac6c11a0c778415b0a856b22b0710a90be4527561b9a6a16df2c45de6e933c9e94a33c6c9a35c3d18dfe7cca3fe2990b0fad3cd41d867531889c9d2e099c88075985a92287a47448d5df171a0561ef8aa27b43351fbdbc1a9d836b4d306409c6d414f051d8312e6b841c064be34ace4be57507b03fa7b9838a03d83509ebce674fd0a8a1177e76e7cb174fbfbcb939a70528908253a094cee13b6ba5f42a8f59173064fb578d3b0f436c2b8e41fa97a3a7a85b8d0a321c15d37795f92d5f9d7df0c99dcf57328cc22a1a8a4913390a74bf1a65091a9bc8c097bb01008deb75343b717b34db32ffddc0bf5d13f9db9960a76fb4c5905de80632280140356840ce820611d1cc8960354de8583742e7c0d025599a96b4633aecff7de1f45f6fdf7d73591a5432ab09acd4a91ab01e8079051281d7c7d544efd655925ddc8a9c4ec67be3e3f6131a9aeaf4e621d8329c176cef6212c92bef9c3eff66c75269d0386fab1082071fb1507ffcf42412f00fc54d21044083225151866c992a25fb44fb4911eb94bdf743a28ec2fea4ec98007ffaf24f4e5ffdc5c91f969f1202e706f0270180e1e3538e4c47aba3abc2f32a9c5c2f17ef433be0e95bba819d379f9ec215a73110f3ae022cffe5aba79fbfd3eac45206b58a0168cec1764420dee2930d44959dcd32ca2d5b40d1e3f5b9ef4b0e524693f1d195e3b8dac3dde7279ae7feee382ebd93ce2eab62e8e3ed3218313c30451413e070b541ac4dc2e8fbf43092bfcbc7b8804728d97f395cdbe114db997c6db67fa907c2e4a0144d01ac1c8c5c6748b84a4e189fb40a3d25717ac7fbaf70b01d02d0e8e13c81b731ad3702e2960229ce5a862235ee30385f72818588fadc4968ed84ce6e40b252a60febaef6e1e98b30801a2ca92988060566895219c6605d80855ae10c6a536b602ccd056a5a58a984c7d3445e0517b5e313bbdaaeb4a68e833860df3edae6b4d5520f15294f9171f6409e4e685012977ca1d02117496f89a1da1a4d465a506814219c64a124b7a3989190d02fca8f7658b7ed057cebe9b35bcb04069c612ac58a10c943a7002f0047d57c3485031b136bfe99b5098c8dd44918c7309eff877c00b5d67241e3edacec70769b423bf9e35bed28c48bdc37f50c6d089fd9c08102b3d13917666285e62a4eb9df60ca32956371084d64c26f60eed6273789c9a392c67ba3616b0a7ae3444d08152ef2e447cb4191a2501509131ee4478484333752430e6382e7d6d16f7ae89d08f1ea7054c7c73ba20fc3c371be70f7d72f9dfcd3174ba4d87c548f84b8791e69a405c7dd07903e50ad94d4fa27d3f474ea5d1986c3abf3ac3910f16ab336a78f5b60ca2fdac400cf5efce9d21b8017b77557054a65b404188e3ca88a43005234d494685d0d71d51b2872c54d0bf7c0ad47aef726e1603fc409ec11f5aad0f552b6d30312706d175db1dfbc1c0e2e7c1d04b51cddb8b0f91959a59a8cebc7a6032c71ce49d8aa8b2960c5c22dbbf6f1d39cb5b649a1efaafe3a9c4c219147ed209a1da5575db8ec83376968f0fa183500044ac2543807cb227061b09542253c115daa7c1bb3f15e8958b46bdfb34d7b6332a4c7a555a5327d345d65c205559852d0462f08b3d94dcf78fe8771dce76cce8dd12285aebf2467e8d321a2e7853eaa802aaff0e73ce4b5c8695fbbf8a84c4b9894764ae43c2cd236de6b483d48c9e23314bf7cf5e4ad9fdeb9fdea46a332356b4b9a176975b1cea8a252941009621180328b3234182c6098d641f70fd84ee11020b51c8e8fa8647e5e09d47721df681f7ca479f0e1f1ea872b7a9f8b3201d8a2844417833f0086a98e5b0e64cf37afe57e26ad503dda95a3b2cb677ef0dee96fdec2a1ac963ff0e8052539c12ab4f2057b0858b080513198dd3572b9bb84fe50dcbbc50e5fdfecae038df536009fd0df011c52626d20d718a96784d9f3d64d57c2f8075706c36f977668ae6e0644491276a8105b0b04ad8ed9012f9034bf1227ee34ae5d401b2183f02d85da483affd00c94d6f3c6f175dbf4d8f0a08992e2ca6ddb19a39c5da2e4779e3bfbc37b1408026da3b2a28eb972a6b22d199ea7666569a0650a2c579a531073cc5d7c14165bfba674deafc282d1a0c92f97d615354507ed47016965a583855e64777ffd5e33477b59d920b1a6041d087825c8b5a54282cc32034b0d8b0c0e6591846d3f41721d9b877465589ae96c87932b34a0a0eda2c1c21a567d110b9dbd7e6bf6d327ef7cf18ba564794df1c7a475545118ea54b726b9644251c026eb65248e3e0247efe06400a593f131a112b082e6b6452f189a7df9c6c65c34af2b0b507bc3698ebd4d54eda958a10f531055e7f57af145b0623d77050f4e9d4ebbc752b4957e4b09d3b0f6306e2c52289e66c225a643b69a22022cad57ae35107c3e03ee7ffdff49270193da7b0000");
		String test = ObjectUtils.unzipToString(bytes);
		System.out.println(test);
		
		int total = 0;
		int n = 1000;
		for(int i = 0; i < n; i ++){
			int r = getRandom(4, 6);
			total += r;
			System.out.println(r);
		}
		System.out.println((total * 1.0) / (n * 1.0));
		
//		long now = System.currentTimeMillis();
//		System.out.println(now);
//		System.out.println(encodeToken(7L, "baobei520", now));
//		System.out.println(encodeToken(1163L, "654321", now));
//		
//		Object[] objects = decodeToken(encodeToken(7L, "123456", now));
//		System.out.println(objects[0]);
//		System.out.println(objects[1]);
//		System.out.println(objects[2]);
//		
//		objects = decodeToken(encodeToken(1163L, "654321", now));
//		System.out.println(objects[0]);
//		System.out.println(objects[1]);
//		System.out.println(objects[2]);
	}
	
	public static void randomList(final List<?> list){
		if(list == null || list.size() == 0){
			return;
		}
		final List<Float> priorities = new ArrayList<Float>();
		for(Object o : list){
			priorities.add(RandomUtils.nextFloat());
		}
		Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2) {
				int index1 = list.indexOf(object1);
				int index2 = list.indexOf(object2);
				return priorities.get(index1).compareTo(priorities.get(index2));
			}
		});
	}
	
	public static byte[] zipToBytes(String content){
		if(StringUtils.isBlank(content)){
			return null;
		}
		return zipToBytes(content.getBytes());
	}

	public static byte[] zipToBytes(byte[] bytes){
		if(bytes == null || bytes.length == 0){
			return null;
		}
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gZIPOutputStream.write(bytes);
			gZIPOutputStream.finish();
			gZIPOutputStream.close();
			byte[] toByteArray = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
			return toByteArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] unzipToBytes(byte[] bytes){
		GZIPInputStream gzipInputStream = null;
		ByteArrayOutputStream out = null;
		try{
			gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
			out = new ByteArrayOutputStream();

			// Transfer bytes from the compressed stream to the output stream
			byte[] buf = new byte[10000];
			int len;
			while ((len = gzipInputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			return out.toByteArray();
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			// Close the file and stream
			try {
				if (gzipInputStream != null){
					gzipInputStream.close();
				}
				if (out != null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String unzipToString(InputStream inputStream){
		ByteArrayOutputStream out = null;
		try{
//			GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
			out = new ByteArrayOutputStream();
			
			// Transfer bytes from the compressed stream to the output stream  
			byte[] buf = new byte[10000];  
			int len;  
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);  
			}
			byte[] bytes = out.toByteArray();
			return new String(bytes, "UTF-8");
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			// Close the file and stream
			try {
				if (inputStream != null){
					inputStream.close();
				}
				if (out != null){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String unzipToString(byte[] bytes){
		GZIPInputStream gzipInputStream = null;
		try{
			gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//			// Transfer bytes from the compressed stream to the output stream
//			byte[] buf = new byte[10000];
//			int len;
//			while ((len = gzipInputStream.read(buf)) > 0) {
//				out.write(buf, 0, len);
//			}
//			// Close the file and stream
//			gzipInputStream.close();
//			out.close();
//			bytes = out.toByteArray();
//			return new String(bytes, "UTF-8");
		}catch(IOException e){
			e.printStackTrace();
		}
		return unzipToString(gzipInputStream);
	}
	
	/**
	 * 生成16进制累加和校验码
	 *
	 * @param data 除去校验位的数据
	 * @return
	 */
	public static String getChecksum(String data) {
	    if (StringUtils.isBlank(data)) {
	        return "";
	    }
	    int total = 0;
	    int len = data.length();
	    int num = 0;
	    int oneTime = 2;
	    while (num < len) {
	        String s = data.substring(num, num + oneTime);
	        total += Integer.parseInt(s, 16);
	        num = num + oneTime;
	    }
	    /**
	     * 用256求余最大是255，即16进制的FF
	     */
	    int mod = total % 256;
	    String hex = Integer.toHexString(mod);
	    len = hex.length();
	    //如果不够校验位的长度，补0,这里用的是两位校验
	    if (len < 2) {
	        hex = "0" + hex;
	    }
	    return hex;
	}
	
	/**
	 * 16进制累加和校验
	 *
	 * @param data
	 *            除去校验位的数据
	 * @param sign
	 *            校验位的数据
	 * @return
	 */
	public static boolean checkChecksum(String data, String sign) {
		// String
		// sourceData="0100150aa303b101b201b301b404b504b601b904ba03be0140";
		// data="0100150aa303b101b201b301b404b504b601b904ba03be01";
		// sign="40";
		if (StringUtils.isBlank(data) || StringUtils.isBlank(sign)) {
			return false;
		}
		String checksum = getChecksum(data);
		System.out.println("=============" + checksum);
		if (checksum.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexStringBak(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytesBak(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
    /**
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

    /**
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	/** 
	  * Convert char to byte 
	  * @param c char 
	  * @return byte 
	  */  
	 private static byte charToByte(char c) {
	     return (byte) "0123456789ABCDEF".indexOf(c);  
	 }
	 
	 /**
	 * @param url
	 * @return
	 */
	public static String getHostFromUrl(String url){
		 String host = null;
		 if(!StringUtils.isBlank(url) && (url.contains("http://") || url.contains("https://"))){
			 if(url.contains("?")){
				 url = url.substring(0, url.indexOf("?"));
			 }
			 try {
				host = new URI(url).getHost();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		 }
		 return host;
	 }
	 
	 /**
	  * 获得临时文件目录
	  * 
	 * @return
	 */
	public static String getTempDir(){
		 return System.getProperty("java.io.tmpdir");
	 }
	 
	 /**
	  * 得到随机的省和经纬度，40%的概率在直辖市，剩下在其他省
	  * 
	 * @return
	 */
	public static Province getRandomProvince(){
		List<Province> allProvince = new ArrayList<Province>();
		Province province = new Province();
		
		province = new Province("甘肃省", 103.73, 36.03, 1.0);
		allProvince.add(province);
		
		province = new Province("青海省", 101.74, 36.56, 0.5);
		allProvince.add(province);
		
		province = new Province("四川省", 104.06, 30.67, 1.6);
		allProvince.add(province);
		
		province = new Province("河北省", 114.48, 38.03, 1.6);
		allProvince.add(province);
		
		province = new Province("云南省", 102.73, 25.04, 1.6);
		allProvince.add(province);
		
		province = new Province("贵州省", 106.71, 26.57, 1.0);
		allProvince.add(province);
		
		province = new Province("湖北省", 114.31, 30.52, 1.0);
		allProvince.add(province);
		
		province = new Province("河南省", 113.65, 34.76, 1.5);
		allProvince.add(province);
		
		province = new Province("山东省", 117.0, 36.65, 1.6);
		allProvince.add(province);
		
		province = new Province("江苏省", 118.78, 32.04, 3.0);
		allProvince.add(province);
		
		province = new Province("安徽省", 117.27, 31.86, 1.0);
		allProvince.add(province);
		
		province = new Province("浙江省", 120.19, 30.26, 3.0);
		allProvince.add(province);
		
		province = new Province("江西省", 115.89, 28.68, 1.5);
		allProvince.add(province);
		
		province = new Province("福建省", 119.3, 26.08, 1.8);
		allProvince.add(province);
		
		province = new Province("广东省", 113.23, 23.16, 3.0);
		allProvince.add(province);
		
		province = new Province("湖南省", 113.0, 28.21, 1.0);
		allProvince.add(province);
		
		province = new Province("海南省", 110.35, 20.02, 0.5);
		allProvince.add(province);
		
		province = new Province("辽宁省", 123.38, 41.8, 1.0);
		allProvince.add(province);
		
		province = new Province("吉林省", 125.35, 43.88, 1.0);
		allProvince.add(province);
		
		province = new Province("黑龙江省", 126.63, 45.75, 1.0);
		allProvince.add(province);
		
		province = new Province("山西省", 112.53, 37.87, 1.0);
		allProvince.add(province);
		
		province = new Province("陕西省", 108.95, 34.27, 1.0);
		allProvince.add(province);
//		provice = new Province("台湾省", 121.30, 25.03, 1.0);
		// 4直辖市
		province = new Province("北京市", 116.46, 39.92, 10.0);
		allProvince.add(province);
		
		province = new Province("上海市", 121.48, 31.22, 10.0);
		allProvince.add(province);

		province = new Province("重庆市", 106.54, 29.59, 1.0);
		allProvince.add(province);

		province = new Province("天津市", 117.2, 39.13, 1.0);
		allProvince.add(province);

		// 5自治区
		province = new Province("内蒙古自治区", 111.65, 40.82, 0.2);
		allProvince.add(province);

		province = new Province("广西壮族自治区", 108.33, 22.84, 1.0);
		allProvince.add(province);

		province = new Province("西藏自治区", 91.11, 29.97, 0.2);
		allProvince.add(province);

		province = new Province("宁夏回族自治区", 106.27, 38.47, 0.2);
		allProvince.add(province);

		province = new Province("新疆维吾尔自治区", 87.68, 43.77, 0.2);
		allProvince.add(province);
		
		// 2特别行政区
//		provice = new Province("香港省", 114.17, 22.28, 1.0);
//		provice = new Province("澳门省", 113.54, 22.19, 1.0);
		
		double sum = 0.0;
		for(Province p : allProvince){
			sum += p.getProbability();
			p.setTotalProbability(sum);
		}
		
		province = null;
		double random = getRandom(0, sum);
		for(Province p : allProvince){
			if(random < p.getTotalProbability()){
				province = p.clone();
				break;
			}
		}
		if(province != null){
			province.setLatitude(ObjectUtils.getRandomSymbol() * ObjectUtils.getRandom(0.0, 0.5) + province.getLatitude());
			province.setLongitude(ObjectUtils.getRandomSymbol() * ObjectUtils.getRandom(0.0, 0.5) + province.getLongitude());
		}
		return province;
	}
	
	public static class Province{
		private String name;
		private Double longitude;
		private Double latitude;
		private Double probability = 1.0;
		private Double totalProbability = 0.0;
		
		public Province() {
			super();
		}
		
		public Province(String name, Double longitude, 
				Double latitude, Double probability) {
			super();
			this.name = name;
			this.longitude = longitude;
			this.latitude = latitude;
			this.probability = probability;
		}
		
		public Province clone() {
			Province province = new Province();
			province.name = this.name;
			province.longitude = this.longitude;
			province.latitude = this.latitude;
			province.probability = this.probability;
			return province;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getProbability() {
			return probability;
		}

		public void setProbability(Double probability) {
			this.probability = probability;
		}

		public Double getTotalProbability() {
			return totalProbability;
		}

		public void setTotalProbability(Double totalProbability) {
			this.totalProbability = totalProbability;
		}
	}
	
	public static int containStringNumber(String str, String contains) {
		if(contains == null || "".equals(contains)){
			return 0;
		}
		int counter = 0;
		if (str.indexOf(contains) == -1) {
			return 0;
		} else if (str.indexOf(contains) != -1) {
			counter = containStringNumber(str.substring(str.indexOf(contains) + contains.length()), contains);
			counter ++;
			return counter;
		}
		return 0;
	}
	
	/**
	 * 等待多少毫秒
	 * 
	 * @param millis
	 */
	public static void waitForTime(long millis){
	    if(millis <= 0){
	        return;
        }
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 第一个version是否大于第二个
	 * 
	 * @param version
	 * @param comparedVersion
	 * @return
	 */
	public static boolean versionBigger(String version, String comparedVersion){
		int versionInteger = getVersionInteger(version);
		int comparedVersionInteger = getVersionInteger(comparedVersion);
		if(versionInteger > comparedVersionInteger){
			return true;
		}
		return false;
	}
	
	/**
	 * 第一个version是否大于等于第二个
	 * 
	 * @param version
	 * @param comparedVersion
	 * @return
	 */
	public static boolean versionBiggerOrEquals(String version, String comparedVersion){
		int versionInteger = getVersionInteger(version);
		int comparedVersionInteger = getVersionInteger(comparedVersion);
		if(versionInteger >= comparedVersionInteger){
			return true;
		}
		return false;
	}
	
	/**
	 * @param version
	 * @return
	 */
	public static int getVersionInteger(String version){
		int versionInteger = 0;
		if(!StringUtils.isBlank(version)){
			String[] vs = version.split("\\.");
			int i = vs.length - 1;
			for(String v : vs){
				versionInteger += Integer.valueOf(v) * Math.pow(1000, i);
				i --;
			}
		}
		return versionInteger;
	}
	
	/**
	 * @param request
	 * @return
	 */
	public static Map<String, String> getRequestParametersMap(HttpServletRequest request){
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		return params;
	}
	
	/**
	 * 获得一个排好序的string
	 * 
	 * @param map
	 */
	public static String getMapSortedString(Map<String, ? extends Object> map, String splitter){
		if(map == null){
			return null;
		}
		StringBuilder sortedString = new StringBuilder();
		if(splitter == null){
			splitter = "";
		}
		if(!(map instanceof TreeMap)){
			map = new TreeMap<String, Object>(map);
		}
		Set<String> keys = map.keySet();
		int index = 0;
		for(String parameterKey : keys){
			if(parameterKey != null){
				sortedString.append(parameterKey);
			}else{
				sortedString.append("");
			}
			sortedString.append("=");
			if(map.get(parameterKey) != null 
					&& !StringUtils.isBlank(map.get(parameterKey).toString())){
				sortedString.append(map.get(parameterKey));
			}
			if(index < (keys.size() - 1)){
				sortedString.append(splitter);
			}
			index ++;
		}
		return sortedString.toString();
	}
	
	public static List<String> getListFromString(String string, String splitter){
		if(StringUtils.isBlank(splitter)){
			splitter = SPLITER_ARRAY;
		}
		List<String> userUuids = new ArrayList<String>();
		if(!StringUtils.isBlank(string)){
			String[] strs = string.trim().split(splitter);
			for(String str : strs){
				if(!StringUtils.isBlank(str)){
					userUuids.add(str.trim());
				}
			}
		}
		return userUuids;
	}
	
	/**
	 * 删除连续出现的2个以上的blank
	 * 
	 * @param text
	 * @return
	 */
	public static String removeContinousBlank(String text){
		if(StringUtils.isBlank(text)){
			return text;
		}
		return text.replaceAll("\\s{1,}", " ");
	}

	/**
	 * @param list
	 * @param replacedIndex
	 * @param replacedValue
	 * @return
	 */
	public static <T> List<T> replaceList(List<T> list, int replacedIndex, T replacedValue){
		if(list == null || list.isEmpty()){
			return null;
		}
		if(replacedIndex < 0 || replacedIndex > list.size() - 1){
			return null;
		}
		List<T> newList = new ArrayList<T>();
		newList.addAll(list.subList(0, replacedIndex));
		newList.add(replacedValue);
		newList.addAll(list.subList(replacedIndex + 1, list.size()));
		return newList;
	}
	
	/**
	 * 是否包含blank
	 * 
	 * @param text
	 * @return
	 */
	@Deprecated
	public static boolean containsBlank(String text){
		//TODO 有bug，中文的时候会认为是空格
		if(StringUtils.isBlank(text)){
			return false;
		}
		Pattern pattern = Pattern.compile(".+?\\s*.+");
		return pattern.matcher(text).find();
	}
	
	/**
	 * 是否包含emoji，不是很准确
	 * 
	 * @param source
	 * @return
	 */
	@Deprecated
	public static boolean containsEmoji(String source) {
		int len = source.length();
		boolean isEmoji = false;
		for (int i = 0; i < len; i++) {
			char hs = source.charAt(i);
			if (0xd800 <= hs && hs <= 0xdbff) {
				if (source.length() > 1) {
					char ls = source.charAt(i + 1);
					int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
					if (0x1d000 <= uc && uc <= 0x1f77f) {
						return true;
					}
				}
			} else {
				// non surrogate
				if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
					return true;
				} else if (0x2B05 <= hs && hs <= 0x2b07) {
					return true;
				} else if (0x2934 <= hs && hs <= 0x2935) {
					return true;
				} else if (0x3297 <= hs && hs <= 0x3299) {
					return true;
				} else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
						|| hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
					return true;
				}
				if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
					char ls = source.charAt(i + 1);
					if (ls == 0x20e3) {
						return true;
					}
				}
			}
		}
		return isEmoji;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}
	
	/**
	 * 删除换行符
	 * 
	 * @param str
	 * @param replacement
	 * @return
	 */
	public static String removeEnter(String str, String replacement){
		if(str == null){
			return null;
		}
		if(replacement == null){
			replacement = "";
		}
		Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");  
		Matcher m = CRLF.matcher(str);
		if (m.find()) {  
			str = m.replaceAll(replacement);
		}
		return str;
	}
	
	/**
	 * 处理第三方给的用户名
	 * 
	 * @param formerName
	 * @return
	 */
	public static String parseThirdUserName(String formerName){
		String name = formerName;
		if(StringUtils.isBlank(name)){
			name = "Insight用户";
		}
		name = name.trim();
		if(name.length() > 15){
			name = name.substring(0, 15);
		}
		if(name.length() < 4){
			name = name + ObjectUtils.generateRandomIntegerCode(4 - name.length());
		}
		return name;
	}

	public static Set<String> getLocalIps(){
		Set<String> localIps = new HashSet<String>();
		Enumeration allNetInterfaces = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//				System.out.println(netInterface.getName());
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
//						System.out.println("本机的IP = " + ip.getHostAddress());
						localIps.add(ip.getHostAddress());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localIps;
	}

    public static String getUrlWithoutParameter(String url){
        String temp = null;
        if (!StringUtils.isBlank(url)){
            try {
                URL urlObject = new URL(url);
                temp = urlObject.getProtocol() + "://" + urlObject.getHost() + urlObject.getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    public static String getUrlParameters(String url){
        String temp = null;
        if (!StringUtils.isBlank(url)){
            try {
                URL urlObject = new URL(url);
                temp = urlObject.getQuery();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    /**
     * 得到这样的格式：http://www.makeweiyuan.com
     *
     * @param url
     * @return
     */
    public static String getDomain(String url){
        String temp = null;
        URL urlObject = null;
        try {
            urlObject = new URL(url);
            temp = getDomain(urlObject);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 得到这样的格式：http://www.makeweiyuan.com
     *
     * @param url
     * @return
     */
    public static String getDomain(URL url){
        String temp = null;
        try {
            temp = url.getProtocol() + "://" + url.getHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }


    /**
     * 判断是否是一个IP
     *
     * @param ip
     * @return
     */
    public static boolean isIp(String ip){
		boolean b = false;
		if (!StringUtils.isBlank(ip)){
            ip = ip.trim();
            if(ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){
                String s[] = ip.split("\\.");
                if(Integer.parseInt(s[0]) <= 255){
                    if(Integer.parseInt(s[1]) <= 255){
                        if(Integer.parseInt(s[2]) <= 255){
                            if(Integer.parseInt(s[3]) <= 255) {
                                b = true;
                            }
                        }
                    }
                }
            }
        }
		return b;
	}

	public static boolean isEquals(Object param1, Object param2){
    	if(param1 == null && param2 == null){
    		return true;
		}
		if(param1 == null || param2 == null){
    		return false;
		}
		return param1.equals(param2);
	}

    /**
     *
     * 把一个list分成几个list，个数平均分
     *
     * @param listNumber 一共分成几个list
     * @return
     */
    public static List<List> devideListByNumber(List list, int listNumber){
        if(list == null || list.size() == 0 || listNumber <= 0){
            return null;
        }
        List<List> listList = new ArrayList<List>();

        if(list.size() <= listNumber){
            listList.add(list);
        }else{
            int peiceNumber = (int) Math.floor((1.0D * list.size()) / (1.0D * listNumber));
            if(list.size() % listNumber != 0){
                peiceNumber = peiceNumber + 1;
            }
            for(int i = 0; i < listNumber; i ++){
                int fromIndex = peiceNumber * i;
                if(fromIndex >= list.size()){
                    break;
                }
                int toIndex = peiceNumber * (i + 1);
                if(toIndex > list.size()){
                    toIndex = list.size();
                }
                listList.add(list.subList(fromIndex, toIndex));
            }
        }
        return listList;
    }

	/**
	 *
	 * 把一个list按照给出的个数分为几个list
     *
	 * @param peiceNumber 每一份多少个
	 * @return
	 */
	public static List<List> devideList(List list, int peiceNumber){
		if(list == null || list.size() == 0 || peiceNumber <= 0){
			return null;
		}
		List<List> listList = new ArrayList<List>();
		if(list.size() <= peiceNumber){
			listList.add(list);
		}else{
			int listNumber;//把所有币分成多少份
			if(list.size() % peiceNumber == 0){
				listNumber = list.size() / peiceNumber;
			}else{
				listNumber = (int) Math.floor((1.0D * list.size()) / (1.0D * peiceNumber)) + 1;
			}
			for(int i = 0; i < listNumber; i ++){
				int fromIndex = peiceNumber * i;
				if(fromIndex >= list.size()){
					break;
				}
				int toIndex = peiceNumber * (i + 1);
				if(toIndex > list.size()){
					toIndex = list.size();
				}
				listList.add(list.subList(fromIndex, toIndex));
			}
		}
		return listList;
	}
	
	/** 
	 * 获取堆栈结构化信息
	 * @author xy_wu
	 *
	 * @param e
	 * @return
	 */
	public static String getExceptionStack(Exception e) {
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		String result = e.toString() + "\n";
		for (int index = stackTraceElements.length - 1; index >= 0; --index) {
			result += "at [" + stackTraceElements[index].getClassName() + ",";
			result += stackTraceElements[index].getFileName() + ",";
			result += stackTraceElements[index].getMethodName() + ",";
			result += stackTraceElements[index].getLineNumber() + "]\n";
		}
		return result;
	}
	
	/**
	 * 判断是不是图片格式 不是图片格式 true
	 * @author xy_wu
	 *
	 * @param fileExtention
	 * @return
	 */
	public static Boolean checkImageFile(String fileExtention) {
		if ((!"png".equalsIgnoreCase(fileExtention)) && (!"jpg".equalsIgnoreCase(fileExtention))
				&& (!"jpeg".equalsIgnoreCase(fileExtention)) && (!"gif".equalsIgnoreCase(fileExtention))
				&& (!"bmp".equalsIgnoreCase(fileExtention))) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是不是视频格式  不是视频格式 true
	 * @author xy_wu
	 *
	 * @param fileExtention
	 * @return
	 */
	public static Boolean checkVideoFile(String fileExtention) {
		if ((!"avi".equalsIgnoreCase(fileExtention)) && (!"mp4".equalsIgnoreCase(fileExtention))
				&& (!"flv".equalsIgnoreCase(fileExtention)) && (!"rmvb".equalsIgnoreCase(fileExtention))
				&& (!"rm".equalsIgnoreCase(fileExtention))) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 7－9－10－5－8－4－2－1－6－3－7－9－10－5－8－4－2。
	 * 1－0－X－9－8－7－6－5－4－3－2
	 * 校验 身份证是否正确
	 * @author xy_wu
	 *
	 * @param idCard
	 * @return
	 */
	public static Boolean checkIdCard(String idCard) {

		idCard = idCard.trim();
		int[] arr = new int[idCard.length()];
		for (int i = 0; i < idCard.length() - 1; i++) {
			arr[i] = Integer.parseInt(idCard.substring(i, i + 1));
		}

		String[] idCardNumber = idCard.split("");
		Integer sum = null;
		if (idCardNumber.length == 18) {
			sum = arr[0] * 7 + arr[1] * 9 + arr[2] * 10 + arr[3] * 5 + arr[4] * 8 + arr[5] * 4 + arr[6] * 2 + arr[7] * 1
					+ arr[8] * 6 + arr[9] * 3 + arr[10] * 7 + arr[11] * 9 + arr[12] * 10 + arr[13] * 5 + arr[14] * 8
					+ arr[15] * 4 + arr[16] * 2;
		}

		if (sum == null) {
			return false;
		} else {
			Integer remainder = sum % 11;
			String checkCode = null;

			switch (remainder) {
			case 0:
				checkCode = "1";
				break;
			case 1:
				checkCode = "0";
				break;
			case 2:
				checkCode = "x";
				break;
			case 3:
				checkCode = "9";
				break;
			case 4:
				checkCode = "8";
				break;
			case 5:
				checkCode = "7";
				break;
			case 6:
				checkCode = "6";
				break;
			case 7:
				checkCode = "5";
				break;
			case 8:
				checkCode = "4";
				break;
			case 9:
				checkCode = "3";
				break;
			case 10:
				checkCode = "2";
				break;
			default:
				checkCode = null;
				break;
			}

			if (idCardNumber[17].toLowerCase().equals(checkCode)) {
				return true;
			} else {
				return false;
			}

		}

	}
	
	public static Boolean checkPhoneNumber(String phoneNumber) {
		Boolean checkPhone = true;
		if (phoneNumber.equals("") || phoneNumber.length() != 11) {
			checkPhone = false;
		}
		String phoneNumberSub = phoneNumber.substring(0, 1);
		if (!phoneNumberSub.equals("1")) {
			checkPhone = false;
		}
		return checkPhone;
	}
}
