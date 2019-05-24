package com.imall.common.utils;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.imall.common.utils.TimeUtils;

public class ObjectUtilsTest {
	
	@Test
	public void getValidToken() {
		//this token for 123456: MWMwY2QyOE1RPT0zOWFlMTBhZGMzOTQ5YmE1OWFiYmU1NmUwNTdmMjBmODgzZWI2NjY2ZjYzNDNkNDI3NjA2ZDNiY2VNVE0yTmpBeE5qZzNNVFUyTWc9PQ==
		//token for md5 password: MWMwY2QyOE1RPT0zOWExNGUxYjYwMGIxZmQ1NzlmNDc0MzNiODhlOGQ4NTI5MWI2NjY2ZjYzNDNkNDI3NjA2ZDNiY2VNVE0yTmpBNE1qWTVOVGsyTXc9PQ==
		Long uid = 1L;
		String password = "123456";
//		String password = "e10adc3949ba59abbe56e057f20f883e";
		Long loginTime = System.currentTimeMillis();
		String hex = ObjectUtils.encodeToken(uid, password, loginTime);
		System.out.println(hex);

		String lang = "zh_CN_#Hans";
		if(lang.contains("_#")){
			lang = lang.substring(0, lang.indexOf("_#"));
		}
		Assert.assertEquals("zh_CN", lang);
	}
	
	@Test
	public void testGetUuidFromJid() {
		String jid = "wg_123456@conference.imalljoy.com";
		String uuid = ObjectUtils.getUuidFromJid(jid);
		Assert.assertEquals("123456", uuid);
		
		jid = "w_1123456@imalljoy.com";
		uuid = ObjectUtils.getUuidFromJid(jid);
		Assert.assertEquals("1123456", uuid);
		
		jid = "w_device_1223456@imalljoy.com";
		uuid = ObjectUtils.getUuidFromJid(jid);
		Assert.assertEquals("1223456", uuid);
	}
	
	@Test
	public void testEncodeAndDecodeToken() {
		new TimeUtils();
		new StringUtils();
		Long uid = 1L;
		String password = "testdorwssap";
		Long loginTime = System.currentTimeMillis();
		String hex = ObjectUtils.encodeToken(uid, password, loginTime);
		
		Object[] userInfo = ObjectUtils.decodeToken(hex);
		Assert.assertNotNull(userInfo);
		Assert.assertEquals(uid, userInfo[0]);
		Assert.assertEquals(ObjectUtils.getMD5Hex(password), userInfo[1]);
		Assert.assertEquals(loginTime, userInfo[2]);
	}
	
	@Test
	public void testEncodeAndDecodeToken2() {
		new TimeUtils();
		new StringUtils();
		Long uid = 2L;
		String password = "testdorwssap123";
		Long loginTime = System.currentTimeMillis();
		String hex = ObjectUtils.encodeToken2(uid, password, loginTime);
		
		Object[] userInfo = ObjectUtils.decodeToken2(hex);
		Assert.assertNotNull(userInfo);
		Assert.assertEquals(uid, userInfo[0]);
		Assert.assertEquals(ObjectUtils.getMD5Hex(password), userInfo[1]);
		Assert.assertEquals(loginTime, userInfo[2]);
	}
	
	@Test
	public void testEncodeAndDecodeTokenWithException() {
		String hex = "MWMwY2QayOE1USXpOQT09MzalhMzgwMTUzMDM0ODExYWFhaNmUsdfzMTYyMWYyY2QzNTk1YjViNjY2NmY2MzQzZaDQyNzYwNamQzYmNlTVRNMk5ETTJOVE13TlRNeE1RPT0=";
//		String hex = "MWMwY2QyOE1UWTNPRGM9MzlhOTZlNzkyMTg5NjVlYjcyYzkyYTU0OWRkNWEzMzAxMTJiNjY2NmY2MzQzZDQyNzYwNmQzYmNlTVRRMk16STBNVFkxTWpBd01BPT0=";
		Object[] userInfo = ObjectUtils.decodeToken(hex);
		Assert.assertNull(userInfo);
		
		hex = "213adas=";
		userInfo = ObjectUtils.decodeToken(hex);
		Assert.assertNull(userInfo);
	}
	
	@Test
	public void testIsEmail(){
		String email = "test";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "12345";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "test12345";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "1@1";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "1@1.";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "t@a";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "t@a.";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
//		email = "test@1.1";
//		Assert.assertFalse(ObjectUtils.isEmail(email));
		
//		email = "1@1.1.2";
//		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@163.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@126.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@sina.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@qq.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@sina.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@sina.com.cn";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@gmail.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@sohu.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "test@foxmail.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "te.st@foxmail.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "te-st@foxmail.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "te_st@foxmail.com";
		Assert.assertTrue(ObjectUtils.isEmail(email));
		
		email = "te@st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te%st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te你st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te`st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te~st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te!st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te#st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te^st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te&st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te*st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te(st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te)st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te=st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te+st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te|st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te\\st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te/st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te,st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te<st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te>st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te;st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te:st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te[st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te]st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te{st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te}st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te，st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te；st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te：st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
		
		email = "te st@foxmail.com";
		Assert.assertFalse(ObjectUtils.isEmail(email));
	}
	
	@Test
	public void testGetChecksum() {
		String all;
		List<String> list = new ArrayList<String>();
		all = "11010030313647453200a7db74523200";
		list.add(all);
		
		all = "110100303136474532000d9ccda3c100";
		list.add(all);
		
		all = "11010030363447453200069ea3829100";
		list.add(all);
		
		all = "150100525831344d4201b29bc2c2b28b";
		list.add(all);
		
		all = "11010030333247453400cde8edcf22bf";
		list.add(all);
		
		String checksum = "";
		for(String temp : list){
			checksum = ObjectUtils.getChecksum(temp.substring(0, temp.length() - 2));
			System.out.println(checksum);
//			Assert.assertEquals(checksum, temp.substring(temp.length() - 2));
		}
	}

	@Test
	public void testBytesToHexString() throws UnsupportedEncodingException {
		String hex = "1a2b3c";
		byte[] bytes = {26, 43, 60};
		String hex1 = ObjectUtils.bytesToHexString(bytes);
		String hex2 = ObjectUtils.bytesToHexStringBak(bytes);
		Assert.assertEquals(hex, hex1);
		Assert.assertEquals(hex, hex2);
	}
	
	@Test
	public void testHexStringToBytes() throws UnsupportedEncodingException {
		String hex = "f17beb3dc216bfb93503b4026b53b13eb3e596f717cd58427e393c8b05f3dfb06df2d3de1300d9ba65fbdfbdca074fa57be84b825564605a6ef04946265ac05a688dbe355ea535edd1714019032d68362ab058293af2199d809fcc785353927f3ae6f0cc88bfbcb1c668deae68ad3b310060ee769a169fcf97b0cea531672996a56542076c3b6c4cca2e904e3e007f03e287be848d53b2a918ca87a2551f2ba4c36731e19f59eeb66ca87c54ed895cbc2bdef6ecda851ba2a965ed8dcf19dab5b905530a594fdc803523f5e4f8a7c2690311c8a9003902a7a69d16e76e993650fd7e4caaa6636780e87b0a765b72db12a41570d8298f204b293c5ae6c0ffaafd3e4025b178b2da9a07b499f5c05edda903040ed3ed2f1500bb2026752610927f5496d92bc542078bc11b1fb454cac7041bc553f7827ec95ed5dd7b1fbcb18b1b01ec78aeab8b72e927083de572c7ae2055a81b78e65ed8faa5c9b157bc386b249e93c2ac55d88ad27044e49afa844451c020730d432db13b758a8646cee4101973cc2e7d2fe6eadbdf3e245105c431e83471a43b93505b59f6c3eb2337a51521fdac05662ba087134d673e60779991c53111d24627553058058c600de37c72dbf4242321f96ec7585b4db200d644ec7f5071a39f6f360b8706cca3789e34c89a26e2cca9c2fd3d4ada349b9fc160aafff57d74a04cad800b836f92445577ba0cbac5cd3717eb5283e0cc842a831bec01bb0770a758c7471899f3ac5937535b56e1ca50908b91beb45d12b97dac547376dabc0af1d0aa9f05ac2ca15df230100c93080742ed390e4a24a01571222e584b3fd4de4136cab34c256b1eaab603146cc2e0abb648bfeea26d7bd18588c09a82550c63530a2843ffd9b6e1edc95f89ba8673b34c25a5abd86ba5c23e50f55a36b49666e56484ca3e16eb70538ba4519b3b93682c48e094c2366b162e028a481b63ca336b3ebe332465930a58ee7e243317e672a8d9d73e1f5259b9fb654f5065ed7b08bb15ec699050b9c0f92645d359835830f76edf30fa6b88ab65036cb9573082a7dbf5f0242e79ddbcf9cc450937d1011897d8b03e69730ee2132ac9f1f682f8d331ef5a014596de0de738cd9d7365e3654b81b84548e06638c013a84b94f364182d707fe846c26c92b406f55c9ae7ff377e5588ba077f068eee5f70d63bc819caaa63d596bf0bacc4fd2d839abda291dd6437ad2a74c196457453e4aec021519ade9b466502dbe78d8248e055a6cf7be173b020e39524cbea13948af6671117631ffb6a047078241ec15b7bebcd742ff251ef11419d036f27c0a0db02ed";
		byte[] bytes = ObjectUtils.hexStringToBytes(hex);
		byte[] bytes2 = ObjectUtils.hexStringToBytesBak(hex);
		for(int index = 0; index < bytes.length; index ++){
			Assert.assertEquals(bytes[index], bytes2[index]);
		}
	}
	
	@Test
	public void testIsASCII(){
		String str = "1234567890fd你jasi,.{}1&*12";
		boolean isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(!isASCII);
		
		str = "1234567890fdjasi,.{}1&*12";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(isASCII);
		
		str = null;
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(isASCII);
		
		str = "";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(isASCII);
		
		str = "你好么哈哈哈？";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(!isASCII);
		
		str = "？";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(!isASCII);
		
		str = ",./<>?!@#$%^&*:\";'{}[]'";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(isASCII);
		
		str = ",./<>?!@！#$%^&*:\";'{}[]'";
		isASCII = ObjectUtils.isASCII(str);
		Assert.assertTrue(!isASCII);
	}
	
	@Test
	public void testContainStringNumber(){
		String str = " 这都是 什么    世 道";
		int number = ObjectUtils.containStringNumber(str, " ");
		Assert.assertEquals(7, number);
		
		str = " 这都是 什么    世 道 ";
		number = ObjectUtils.containStringNumber(str, " ");
		Assert.assertEquals(8, number);
		
		str = "这都是 什么   世 道 ";
		number = ObjectUtils.containStringNumber(str, " ");
		Assert.assertEquals(6, number);
		
		str = "这都是 什么   世 道 ";
		number = ObjectUtils.containStringNumber(str, "什");
		Assert.assertEquals(1, number);
	}
	
	@Test
	public void testVersionBiggerOrEquals(){
		Assert.assertFalse(ObjectUtils.versionBiggerOrEquals("3.7.9", "3.8.0"));
		Assert.assertFalse(ObjectUtils.versionBiggerOrEquals("3.7.7", "3.8.0"));
		Assert.assertFalse(ObjectUtils.versionBiggerOrEquals("3.7.17", "3.8.0"));
		
		Assert.assertFalse(ObjectUtils.versionBiggerOrEquals("3.17.0", "3.18.0"));

		Assert.assertFalse(ObjectUtils.versionBiggerOrEquals("3.17.17", "3.18.18"));
		
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.8.0", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.8.1", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.8.9", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.8.19", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.9.0", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.9.19", "3.8.0"));

		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.19.19", "3.18.18"));
		Assert.assertTrue(ObjectUtils.versionBiggerOrEquals("3.18.18", "3.18.18"));
	}
	
	@Test
	public void testVersionBigger(){
		Assert.assertFalse(ObjectUtils.versionBigger("3.7.9", "3.8.0"));
		Assert.assertFalse(ObjectUtils.versionBigger("3.7.7", "3.8.0"));
		Assert.assertFalse(ObjectUtils.versionBigger("3.7.17", "3.8.0"));
		
		Assert.assertFalse(ObjectUtils.versionBigger("3.17.0", "3.18.0"));
		
		Assert.assertFalse(ObjectUtils.versionBigger("3.17.17", "3.18.18"));
		
		Assert.assertFalse(ObjectUtils.versionBigger("3.8.0", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBigger("3.8.1", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBigger("3.8.9", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBigger("3.8.19", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBigger("3.9.0", "3.8.0"));
		Assert.assertTrue(ObjectUtils.versionBigger("3.9.19", "3.8.0"));
		
		Assert.assertTrue(ObjectUtils.versionBigger("3.19.19", "3.18.18"));
		Assert.assertFalse(ObjectUtils.versionBigger("3.18.18", "3.18.18"));
		Assert.assertFalse(ObjectUtils.versionBigger("3.18.19", "3.18.19"));
		Assert.assertFalse(ObjectUtils.versionBigger("3.9.19", "3.9.19"));
	}
	
	/*@Test
	public void testGenerateShortUUID(){
		Set<String> set = new HashSet<String>();
		for(int i = 0; i < 100000; i ++){
			String uuid = ObjectUtils.generateShortUUID();
			Assert.assertFalse(set.contains(uuid));
			set.add(uuid);
		}
		
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		for(String s : list){
			System.out.println(s);
		}
	}*/
	
	@Test
	public void testRemoveContinousBlank(){
		String expected = "你 好";
		String text = "你  好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		
		text = "你    好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你     好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你      好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你       好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你        好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你         好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你   \t      好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你\t好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你 \t好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你\t 好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你 \t 好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
		text = "你\t\t好";
		Assert.assertEquals(expected, ObjectUtils.removeContinousBlank(text));
	}
	
	@Test
	public void testContainsBlank(){
		Assert.assertTrue("a\tb".contains("\t"));
		Assert.assertFalse("atb".contains("\t"));
		Assert.assertFalse("atb".contains("\\t"));
		Assert.assertFalse("a\tb".contains("\\t"));
		Assert.assertTrue("a\\tb".contains("\\t"));
		boolean expected = true;
		String text = "你 好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你  好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你    好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你     好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你      好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你       好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你        好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		text = "你         好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你   \t      好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你\t好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你 \t好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你\t 好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你\t\t好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = " 你好";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
		text = "你好 ";
		Assert.assertEquals(expected, ObjectUtils.containsBlank(text));
		
//		text = "你好";
//		Assert.assertEquals(!expected, ObjectUtils.containsBlank(text));
//		
//		text = "nihao";
//		Assert.assertEquals(!expected, ObjectUtils.containsBlank(text));
//		
//		text = "你好😢";
//		Assert.assertEquals(!expected, ObjectUtils.containsBlank(text));
	}
	
	@Test
	public void testContainsEmoji(){
		String text = "你  好abcfdsafda1293012*()*!@^*#^>?<?>L:AD";
		Assert.assertFalse(ObjectUtils.containsEmoji(text));
		
		text = "😄";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "🇦🇹";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😭";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "☠";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😢";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "💀";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "👿";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😚";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
//		text = "🤗";
//		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "👕";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
//		text = "🤕";
//		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "👨‍👧‍👦";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "🙆🏽";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "🏽😉";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "👘";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "🏰";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😁";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😬";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "💦";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
		
		text = "😇";
		Assert.assertTrue(ObjectUtils.containsEmoji(text));
	}
	
	@Test
	public void testReplaceList(){
		List<String> list = new ArrayList<String>();
		list.add("4");
		list.add("3");
		list.add("2");
		list.add("1");
		List<String> newList = ObjectUtils.replaceList(list, 1, "33"); 
		Assert.assertEquals(4, newList.size());
		Assert.assertEquals("4", newList.get(0));
		Assert.assertEquals("33", newList.get(1));
		Assert.assertEquals("2", newList.get(2));
		Assert.assertEquals("1", newList.get(3));
		
		newList = ObjectUtils.replaceList(list, 0, "33"); 
		Assert.assertEquals(4, newList.size());
		Assert.assertEquals("33", newList.get(0));
		Assert.assertEquals("3", newList.get(1));
		Assert.assertEquals("2", newList.get(2));
		Assert.assertEquals("1", newList.get(3));
		
		newList = ObjectUtils.replaceList(list, 3, "33"); 
		Assert.assertEquals(4, newList.size());
		Assert.assertEquals("4", newList.get(0));
		Assert.assertEquals("3", newList.get(1));
		Assert.assertEquals("2", newList.get(2));
		Assert.assertEquals("33", newList.get(3));
	}
	
	@Test
	public void testRemoveEnter(){
		String expected = "你 好 ";
		String str = "\n你 好 ";
		String result = ObjectUtils.removeEnter(str, ""); 
		Assert.assertEquals(expected, result);
		
		str = "\r\n你 \n好\r\n ";
		result = ObjectUtils.removeEnter(str, ""); 
		Assert.assertEquals(expected, result);
		
		str = "\r\n你 \r\n好 \n";
		result = ObjectUtils.removeEnter(str, " "); 
		Assert.assertEquals(" 你  好  ", result);
	}
	
	@Test
	public void testOutputSQLForFeedVoteSharding() {
		boolean withDropTable = false;
		boolean withCreateTable = false;
		boolean withAddIndex = true;
		boolean withDeleteOldTable = true;
		boolean withExportSQLTable = false;
		Long minFeedVoteId = 1L;//第一次
		minFeedVoteId = 31700001L;//第二次
		minFeedVoteId = 33960001L;//第三次
//		minFeedVoteId = 33962436L;//第四次
		minFeedVoteId = 35960001L;//第四次
		Long maxFeedVoteId = 31700000L;//第一次
		maxFeedVoteId = 33960000L;//第二次
		maxFeedVoteId = 35960000L;//第三次
//		maxFeedVoteId = 35960000L;//第四次
		maxFeedVoteId = 359600000L;//第四次
		int shardingTableNumber = 100;
		String originalTableName = "w_feed_vote";
		originalTableName = "w_feed_vote_temp";
		String tableName = "w_feed_vote_";
		String template1 = "drop table if exists TABLE_NAME;\n";
		String template2 = "CREATE TABLE `TABLE_NAME` (\n" +
                "  `uid` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `created_time` timestamp NOT NULL DEFAULT '2000-01-01 00:00:00',\n" +
                "  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `user_id` int(11) DEFAULT NULL,\n" +
                "  `group_id` int(11) DEFAULT NULL,\n" +
                "  `feed_id` int(11) NOT NULL,\n" +
                "  `vote` int(11) NOT NULL DEFAULT '0' COMMENT '1 - left; 2 - right;',\n" +
                "  `request_from_type` int(11) NOT NULL DEFAULT '1' COMMENT '请求来自哪里',\n" +
                "  `device_type` int(11) NOT NULL DEFAULT '99' COMMENT 'Device类型',\n" +
                "  `device_ip` varchar(100) DEFAULT NULL,\n" +
                "  `cookie_uuid` varchar(100) DEFAULT NULL,\n" +
                "  `wechat_open_id` varchar(100) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`uid`),\n" +
                "  UNIQUE KEY `uid` (`uid`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";
		String template3 = "insert into TABLE_NAME(created_time, updated_time, user_id, group_id, feed_id, vote, request_from_type, device_type, device_ip, cookie_uuid, wechat_open_id)"
				+ " select created_time, updated_time, user_id, group_id, feed_id, vote, request_from_type, device_type, device_ip, cookie_uuid, wechat_open_id from " + originalTableName + " where uid >= "
				+ minFeedVoteId + " and uid <= " + maxFeedVoteId + " and user_id is null;\n";
		String template4 = "insert into TABLE_NAME(created_time, updated_time, user_id, group_id, feed_id, vote, request_from_type, device_type, device_ip, cookie_uuid, wechat_open_id)"
				+ " select a.created_time, a.updated_time, a.user_id, a.group_id, a.feed_id, a.vote, a.request_from_type, a.device_type, a.device_ip, a.cookie_uuid, a.wechat_open_id "
				+ "from " + originalTableName + " a inner join w_feed b on a.feed_id = b.uid and b.deleted = 35 where a.uid >= " + minFeedVoteId + " and a.uid <= " + maxFeedVoteId + " and a.user_id is not null;\n";
		String template5 = "insert into TABLE_NAME(created_time, updated_time, user_id, group_id, feed_id, vote, request_from_type, device_type, device_ip, cookie_uuid, wechat_open_id)"
				+ " select a.created_time, a.updated_time, a.user_id, a.group_id, a.feed_id, a.vote, a.request_from_type, a.device_type, a.device_ip, a.cookie_uuid, a.wechat_open_id"
				+ " from " + originalTableName + " a inner join w_feed b on a.feed_id = b.uid and b.deleted <> 35 where a.uid >= " + minFeedVoteId + " and a.uid <= " + maxFeedVoteId + 
				" and a.user_id is not null and a.user_id % TABLENUMBER = NUMBER;\n";
		String template6 = "alter table TABLE_NAME add UNIQUE index feed_index_UNIQUE(`user_id`,`feed_id`);\n" +
                			"alter table TABLE_NAME add index `feed_index` (`feed_id`,`created_time` desc);\n";
		String template7 = "mysqldump -uroot -proot imall_latest TABLENAME > feed_vote_final_DATE.sql";
		
		String dropTableSQL = "";
		String createTableSQL = "";
		String insertDataSQL = "";
		String addIndexSQL = "";
		
		String allTableNames = "";
		allTableNames += tableName + "null ";
		allTableNames += tableName + "other ";
		
		dropTableSQL += template1.replace("TABLE_NAME", tableName + "null");
		dropTableSQL += template1.replace("TABLE_NAME", tableName + "other");
		
		createTableSQL += template2.replace("TABLE_NAME", tableName + "null");
		createTableSQL += template2.replace("TABLE_NAME", tableName + "other");

		insertDataSQL += template3.replace("TABLE_NAME", tableName + "null");
		insertDataSQL += template4.replace("TABLE_NAME", tableName + "other");

		addIndexSQL += template6.replace("TABLE_NAME", tableName + "null");
		addIndexSQL += template6.replace("TABLE_NAME", tableName + "other");
		for(int i = 0; i < shardingTableNumber; i ++){
			String newTableName = tableName + String.valueOf(i % shardingTableNumber);
			allTableNames += newTableName + " ";
			dropTableSQL += template1.replace("TABLE_NAME", newTableName);
			createTableSQL += template2.replace("TABLE_NAME", newTableName);
			insertDataSQL += template5.replace("TABLE_NAME", newTableName)
					.replace("TABLENUMBER", String.valueOf(shardingTableNumber))
					.replace("NUMBER", String.valueOf(i));
			addIndexSQL += template6.replace("TABLE_NAME", newTableName);
		}
		if(withDropTable){
			System.out.println(dropTableSQL);
			System.out.println();
		}
		if(withCreateTable){
			System.out.println(createTableSQL);
			System.out.println();
		}
		System.out.println(insertDataSQL);
		System.out.println();
		if(withAddIndex){
			System.out.println(addIndexSQL);
			System.out.println();
		}
		if(withDeleteOldTable){
			System.out.println("rename table " + originalTableName + " to " + originalTableName + "_temp;");
		}
		if(withExportSQLTable){
			System.out.println(template7.replace("TABLENAME", allTableNames).replace("DATE", 
					TimeUtils.getTimeString(new Timestamp(System.currentTimeMillis()), CommonConstants.DATE_FORMAT)));
		}
	}
	
	@Test
	public void testOutputSQLForFeedVoteSharding2() {
		System.out.println("ALTER TABLE w_feed_vote_null ADD COLUMN `device_uuid` varchar(100) DEFAULT NULL;");
		System.out.println("ALTER TABLE w_feed_vote_null ADD COLUMN `mac` varchar(50) DEFAULT NULL;");
		System.out.println("ALTER TABLE w_feed_vote_other ADD COLUMN `device_uuid` varchar(100) DEFAULT NULL;");
		System.out.println("ALTER TABLE w_feed_vote_other ADD COLUMN `mac` varchar(50) DEFAULT NULL;");
		for(int i = 0; i < 100; i ++){
			System.out.println("ALTER TABLE w_feed_vote_" + i + " ADD COLUMN `device_uuid` varchar(100) DEFAULT NULL;");
			System.out.println("ALTER TABLE w_feed_vote_" + i + " ADD COLUMN `mac` varchar(50) DEFAULT NULL;");
		}
	}

	@Test
	public void testDevideList(){
		List<Integer> list = new ArrayList<>();

		int listCount = 1292;
		for(int i = 0; i < listCount; i ++){
			list.add(i);
		}

		int peiceNumber = 13;

		List listList = ObjectUtils.devideList(list, peiceNumber);
		Assert.assertEquals((listCount % peiceNumber) == 0 ? (listCount / peiceNumber) : (listCount / peiceNumber + 1), listList.size());

		peiceNumber = listCount;
		listList = ObjectUtils.devideList(list, peiceNumber);
		Assert.assertEquals((listCount % peiceNumber) == 0 ? (listCount / peiceNumber) : (listCount / peiceNumber + 1), listList.size());

		peiceNumber = listCount + 129;
		listList = ObjectUtils.devideList(list, peiceNumber);
		Assert.assertEquals((listCount % peiceNumber) == 0 ? (listCount / peiceNumber) : (listCount / peiceNumber + 1), listList.size());
	}
}
