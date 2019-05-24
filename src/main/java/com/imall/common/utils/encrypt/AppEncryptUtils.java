package com.imall.common.utils.encrypt;

import com.imall.common.utils.ObjectUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Encypt utils
 */
public class AppEncryptUtils {
	private static final String PARAMETER_SIGN_KEY_NAME = "s";
	
	private static final String AES_KEY_PASSWORD = "yXr8mbeEaT9EQ3SwnLwnOH1vUi7Ez6prMORCOuwqi4g1hVeJgbqkaDh9fIIjAI40";
	
	private static final String AES_IV = "BDC15E1365E4430A";
	private static final byte[] AES_IV_BYTES = AES_IV.getBytes();
	
	private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT+KUq1Dg7BHEF18R2vaKeh9HoJIYzX1OWl1StaiEVrjA4cTHnZHWMf6sBZO9nR+qsNTOBU6qp1JU5m6OjtxWQ3ZiQUbCCdT/Y4PMQX60GMzHBe5wds5FiBisCpjsHB8uzwXg8pm9hHoBwuwMyAlX6922QuTnzWlmHwGpTrnaypwIDAQAB";
	private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJP4pSrUODsEcQXXxHa9op6H0egkhjNfU5aXVK1qIRWuMDhxMedkdYx/qwFk72dH6qw1M4FTqqnUlTmbo6O3FZDdmJBRsIJ1P9jg8xBfrQYzMcF7nB2zkWIGKwKmOwcHy7PBeDymb2EegHC7AzICVfr3bZC5OfNaWYfAalOudrKnAgMBAAECgYATRBuVQP51SLXCkf5li3lh9MMy25aRl8bVY6zFy3y1SvHCV5Ks0xqvCWxliRPWublnZKHpI9CjvicT0hzzUwI+oGTJdeFQuUjqPpCEbhphzEwyWTNVoXus0I/KhhgxfC8qMgrK0XmCb/Y1SUeSFMytqaiURdkDi+rDzbQ4GoCAMQJBANqpR+HTKOhPhgRPtr2/src3zgMsAD71kHQx4Oc737492rCjMt74CGrWHRYMu2xZ7ybuPT3bA/iJar/G9ogpMi8CQQCtPSudpzWmsMuaKaKyX5LTjMmTsEM7gdvS0UmmrJpUmuMUdnLdgdl7kuNs5RTrw96+GXG11aNNGlCHyvDxmkEJAkEAqbqS4HjfHQx1c61IpRtDJzL7B71E55oHqhn5gKQaNfqfYVMcrrSLHBrdcvspFJ7uoyJ0iSoTd/16zigrv19y4wJAOIX34CV5bNzMYY1qJzblyh5qesSM+zKAjcV5eWdhp28t3jRTgBBo7Ffd2l7YN6Cd4Xqdzk1ZTcPjOJsviDTjOQJASMr1/NerIDw3iiZ8hudrApEU6r9nHUOV7OVgdhhmr7iXN54m3KU5N+f/vh7oSVcWvf3IO4eW3PC/Eli2iv4qHg==";
	
	private static final byte[] PUBLIC_KEY_BYTES = Base64.decodeBase64(PUBLIC_KEY.getBytes());
	private static final byte[] PRIVATE_KEY_BYTES = Base64.decodeBase64(PRIVATE_KEY.getBytes());
	
	/**
	 * 利用HmacSHA1的摘要算法
	 * 
	 * @param data 需要摘要的字符串
	 * @param key
	 * @return 摘要以后的bytes的hex字符串，小写字母
	 */
	public static String hash(String data, String key) {
		if(StringUtils.isBlank(data) || StringUtils.isBlank(key)){
			return null;
		}
		try {
			return ObjectUtils.bytesToHexString(EncryptUtils.hash(data.getBytes("UTF-8"), key.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加密用户的登录密码
	 * 
	 * @param password 原始密码
	 * @return 加密以后的二进制的16进制字符串，小写字母
	 */
	public static String encryptPasswordByAES(String password) {
		if(StringUtils.isBlank(password)){
			return null;
		}
		try {
			return ObjectUtils.bytesToHexString(EncryptUtils.encryptByAES(password.getBytes("UTF-8"), AES_KEY_PASSWORD.substring(0, 16).getBytes("UTF-8"), null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解密用户的登录密码
	 * 
	 * @param encryptedPassword 加密以后的二进制的16进制字符串
	 * @return 原始密码
	 */
	public static String decryptPasswordByAES(String encryptedPassword) {
		if(StringUtils.isBlank(encryptedPassword)){
			return null;
		}
		try {
			return new String(EncryptUtils.decryptByAES(ObjectUtils.hexStringToBytes(encryptedPassword), AES_KEY_PASSWORD.substring(0, 16).getBytes("UTF-8"), null), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 利用AES加密信息
	 * 
	 * @param data 需要加密的字符串
	 * @param key .bytes的长度必须为16
	 * @return 加密以后的bytes的base64
	 */
	public static String encryptByAES(String data, String key) {
		if(StringUtils.isBlank(data)){
			return null;
		}
		try {
			return new String(Base64.encodeBase64(EncryptUtils.encryptByAES(data.getBytes("UTF-8"), key.getBytes("UTF-8"), AES_IV_BYTES)), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 利用AES解密信息
	 * 
	 * @param data 加密以后的bytes的base64
	 * @param key .bytes的长度必须为16
	 * @return 解密以后的字符串
	 */
	public static String decryptByAES(String data, String key) {
		if(StringUtils.isBlank(data)){
			return null;
		}
		try {
			return new String(EncryptUtils.decryptByAES(Base64.decodeBase64(data.getBytes("UTF-8")), key.getBytes("UTF-8"), AES_IV_BYTES), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 利用RSA加密信息，data不能大于117个bytes
	 * 
	 * @param data 需要加密的字符串
	 * @return 加密以后的bytes的base64
	 */
	public static String encryptByRSA(String data) {
		if(StringUtils.isBlank(data)){
			return null;
		}
		try {
			return new String(Base64.encodeBase64(EncryptUtils.encryptByRSA(data.getBytes("UTF-8"), PUBLIC_KEY_BYTES)), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 利用RSA解密信息
	 * 
	 * @param data 加密以后的bytes的base64
	 * @return 解密以后的字符串
	 */
	public static String decryptByRSA(String data) {
		if(StringUtils.isBlank(data)){
			return null;
		}
		try {
			byte[] decryptedBytes = EncryptUtils.decryptByRSA(Base64.decodeBase64(data.getBytes("UTF-8")), PRIVATE_KEY_BYTES);
			if(decryptedBytes != null){
				return new String(decryptedBytes, "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获得数字签名，算法 = RSA(Hash(data, key))
	 * 
	 * @param data 原值
	 * @param key hash密码
	 * @return
	 */
	public static String sign(String data, String key){
		String signed = null;
		if(data != null && !StringUtils.isBlank(key)){
			signed = encryptByRSA(hash(data, key));
		}
		return signed;
	}
	
	/**
	 * 检查一个数字签名是否正确，signed = RSA(Hash(data, key))
	 * 
	 * @param data 原值
	 * @param signed 数字签名
	 * @param key hash密码
	 * @return
	 */
	public static boolean verifySign(String data, String signed, String key){
		boolean success = false;
		if(data != null && !StringUtils.isBlank(signed) && !StringUtils.isBlank(key)){
			String givenHash = decryptByRSA(signed);
			if(!StringUtils.isBlank(givenHash)){
				String expectedHash = hash(data, key);
				if(!StringUtils.isBlank(expectedHash) && givenHash.toLowerCase().equals(expectedHash)){
					success = true;
				}
			}
		}
		return success;
	}
	
	/**
	 * 校验参数是否正确，假如parameters为空返回false
	 * 
	 * @param key hash的密码
	 * @param parameters
	 * @param signedParameters
	 * @return
	 */
	public static boolean verifyParameters(String key, Map<String, String> parameters, String signedParameters){
		boolean success = false;
		if(parameters != null && !parameters.isEmpty()){
			TreeMap<String, Object> map = new TreeMap<String, Object>();
			Set<String> keys = parameters.keySet();
			String sign = signedParameters;
			for(String parameterKey : keys){
				if(PARAMETER_SIGN_KEY_NAME.equals(parameterKey)){
					if(parameters.get(parameterKey) != null){
						sign = parameters.get(parameterKey);
					}
				}else{
					map.put(parameterKey, parameters.get(parameterKey));
				}
			}
			if(!StringUtils.isBlank(sign)){
				StringBuilder parametersString = new StringBuilder();
				keys = map.keySet();
				for(String parameterKey : keys){
					parametersString.append(parameterKey);
					parametersString.append("=");
					String value = parameters.get(parameterKey);
					if(!StringUtils.isBlank(value)){
						parametersString.append(value);
					}
				}
				success = AppEncryptUtils.verifySign(parametersString.toString(), sign, key);
			}
		}else{
			//没有参数的时候，认为校验成功
			success = true;
		}
		return success;
	}
}
