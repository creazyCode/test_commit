package com.imall.common.utils.encrypt;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.imall.common.exception.UnexpectedException;
import com.imall.common.utils.ObjectUtils;
import com.imall.common.utils.encrypt.EncryptUtils.HashType;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encypt utils
 */
public class EncryptUtils {
	/**
	 * Define a hash type enumeration for strong-typing
	 */
	public enum HashType {
		MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA512("SHA-512");

		private String algorithm;

		HashType(String algorithm) {
			this.algorithm = algorithm;
		}

		@Override
		public String toString() {
			return this.algorithm;
		}
	}

//	private static final HashType DEFAULT_HASH_TYPE = HashType.MD5;

//	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	/**
	 * hash a data with HmacSHA1
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] hash(byte[] data, byte[] key) {
		if (data == null || key.length == 0) {
			return null;
		}
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
			mac.init(signingKey);
			return mac.doFinal(data);
		} catch (Exception ex) {
			throw new UnexpectedException(ex);
		}
	}
	
	/**
	 * Create a text hash using specific hashing algorithm
	 * 
	 * @param text
	 *            The text
	 * @param hashType
	 *            The hashing algorithm
	 * @return The hash string
	 */
	public static String hash(String text, HashType hashType) {
		if (text == null || hashType == null) {
			return null;
		}
		try {
			MessageDigest m = MessageDigest.getInstance(hashType.toString());
			byte[] out = m.digest(text.getBytes());
			return ObjectUtils.bytesToHexString(out);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Encrypt a String with the AES encryption standard.
	 * 
	 * @param data
	 *            The String to encrypt
	 * @param key
	 *            The key used to encrypt, must length = 16 or null
	 * @param iv
	 * @return An hexadecimal encrypted string
	 */
	public static byte[] encryptByAES(byte[] data, byte[] key, byte[] iv) {
		if (data == null || key == null) {
			return null;
		}
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = null;
			if(iv != null){
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			}else{
				cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			}
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new UnexpectedException(ex);
		}
	}
	
	/**
	 * Decrypt a String with the AES encryption standard.
	 * 
	 * @param data
	 *            An hexadecimal encrypted string
	 * @param key
	 *            The key used to encrypt, must length = 16 or null
	 * @param iv
	 * @return The decrypted String
	 */
	public static byte[] decryptByAES(byte[] data, byte[] key, byte[] iv) {
		if (data == null || key == null) {
			return null;
		}
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			
			Cipher cipher = null;
			if(iv != null){
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
			}else{
				cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			}
			
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new UnexpectedException(ex);
		}
	}
	
	/**
	 * 使用RSA进行加密
	 * 
	 * @param data 不能大于117个bytes
	 * @param publicKey
	 * @return
	 */
	public static byte[] encryptByRSA(byte[] data, byte[] publicKey) {
		if (data == null || publicKey == null) {
			return null;
		}
		try {
			KeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory instance = KeyFactory.getInstance("RSA");
            Cipher instance2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance2.init(Cipher.ENCRYPT_MODE, instance.generatePublic(keySpec));
            return instance2.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 使用RSA进行解密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	public static byte[] decryptByRSA(byte[] data, byte[] privateKey) {
		if (data == null || privateKey == null) {
			return null;
		}
        try {
            KeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory instance = KeyFactory.getInstance("RSA");
            Cipher instance2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance2.init(Cipher.DECRYPT_MODE, instance.generatePrivate(keySpec));
            return instance2.doFinal(data);
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        return null;
    }
	
//	public static String decrypt(String content, String key) {
//		if (content == null) {
//			return null;
//		}
//		try {
//			 Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
////			Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
//			cipher.init(2, getPrivateKey(ALGORITHM, key));
//			return new String(crypt(Base64.decode(content, 0), cipher)).trim();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public static String encrypt(String content, String key) {
//		if (Security.getProvider("BC") == null) {
//            Security.addProvider(new BouncyCastleProvider());
//        }
//		try {
////			 Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//			 Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",new BouncyCastleProvider());
//
////			Cipher cipher = Cipher.getInstance("RSA");
//			cipher.init(1, getPublicKeyFromX509(ALGORITHM, key));
//			return Base64.encodeToString(
//					crypt(content.getBytes("UTF-8"), cipher), 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

}
