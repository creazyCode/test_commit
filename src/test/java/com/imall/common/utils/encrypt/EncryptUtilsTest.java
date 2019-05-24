package com.imall.common.utils.encrypt;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.imall.common.utils.ObjectUtils;

import junit.framework.Assert;

public class EncryptUtilsTest {
	@Test
	public void testSign(){
		String expected = "54ae178acf7845145b91d921c3f50c6899a88511";
		String text = "123abc";
		String key = "1a2b3c";
		String hash1 = ObjectUtils.bytesToHexString(EncryptUtils.hash(text.getBytes(), key.getBytes()));
		String hash2 = ObjectUtils.bytesToHexString(EncryptUtils.hash(text.getBytes(), (key + 1).getBytes()));
		String hash3 = ObjectUtils.bytesToHexString(EncryptUtils.hash((text + 2).getBytes(), key.getBytes()));
		Assert.assertEquals(expected, hash1);
		Assert.assertFalse(hash1.equals(hash2));
		Assert.assertFalse(hash1.equals(hash3));
		Assert.assertFalse(hash2.equals(hash3));
	}
	
	@Test
	public void testRSA() throws Exception{
		KeyPairGenerator keyPairGen = null;  
        try {  
            keyPairGen = KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        // 初始化密钥对生成器，密钥大小为96-1024位  
        keyPairGen.initialize(1024,new SecureRandom());  
        // 生成一个密钥对，保存在keyPair中  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        // 得到私钥  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        // 得到公钥  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        
        System.out.println(Base64.encodeBase64String(publicKey.getEncoded()));
        System.out.println(Base64.encodeBase64String(privateKey.getEncoded()));
        
        String text = "你1a大2b3c,.-=`s<>好";
        byte[] encoded = EncryptUtils.encryptByRSA(text.getBytes("UTF-8"), publicKey.getEncoded());
        String encodedBase64 = Base64.encodeBase64String(encoded);
        
        System.out.println(encodedBase64);
        
        byte[] decoded = EncryptUtils.decryptByRSA(encoded, privateKey.getEncoded());
        Assert.assertEquals(text, new String(decoded, "UTF-8"));
	}
}
