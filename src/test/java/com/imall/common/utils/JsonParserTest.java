/**
 * Program Name  : TestJsonParser.java
 * Description  :  coupon platform 
 * @author : yh.zhai
 *
 * ***************************************************************
 *                P R O G R A M    H I S T O R Y
 * ***************************************************************
 * DATE    : PROGRAMMER   :  CONTENT
 * 2012-12-17 : yh.zhai  :  
 */
package com.imall.common.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.util.JSONPObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.ParseException;

import com.imall.common.exception.ValidateException;
import com.imall.common.utils.JsonUtils;

/**
 * @author yh.zhai
 *
 */
public class JsonParserTest {

	private TestJavaObject1 tjo1;
	private TestJavaObject2 tjo2;
	private TestJavaObject3 tjo3;
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Before
	public void init() {
		tjo1 = new TestJavaObject1();
		tjo1.setId(Long.valueOf("1"));
		tjo1.setName("testObject1");
		List<String> childs = new ArrayList<String>();
		childs.add("child1");
		childs.add("child2");
		childs.add("child3");
		tjo1.setChilds(childs);
		Map<String, String> childsRelationship = new HashMap<String, String>();
		childsRelationship.put("child1", "re1");
		childsRelationship.put("child2", "re2");
		childsRelationship.put("child3", "re3");
		tjo1.setChildsRelationship(childsRelationship);
		
		tjo2 = new TestJavaObject2();
		tjo1.setId(Long.valueOf("2"));
		tjo1.setName("testObject2");
		Timestamp createdTimestamp = new Timestamp(System.currentTimeMillis());
		tjo2.setCreatedTimestamp(createdTimestamp);
		
		tjo3 = new TestJavaObject3();
		tjo3.setId(Long.valueOf("1"));
		tjo3.setName("testObject1");
		List<String> _childs = new ArrayList<String>();
		_childs.add("child1");
		_childs.add("child2");
		_childs.add("child3");
		tjo3.setChilds(_childs);
		Map<String, String> _childsRelationship = new HashMap<String, String>();
		_childsRelationship.put("child1", "re1");
		_childsRelationship.put("child2", "re2");
		_childsRelationship.put("child3", "re3");
		tjo3.setChildsRelationship(_childsRelationship);
	}
	
	@Test
	public void testJsonParser(){
		new JsonUtils();
	}
	
	@Test
	public void testFromObjectToJson() {
		//String jsonStr = JsonParser.fromObjectToJson(tjo1);
		boolean b = true;
		List lst = new ArrayList();
		String jsonStr = JsonUtils.fromObjectToJson(lst);
		System.out.println(jsonStr);
		
		String jsonStr1 = JsonUtils.fromObjectToJson("");
		System.out.println(jsonStr1);
		
		JSONPObject jo;
		System.out.println(JsonUtils.fromJsonToObject("true", Boolean.class));

		Long serialVersionUID = 2777338044014576020L;
		Object o = serialVersionUID;
		System.out.println(isWrapClass(tjo1.getClass()));
		//JSONObject jsonObject = JSONObject.fromObject(json);
		
		System.out.println(this.getClass().getResource("/").getPath());
		
		System.out.println("178a7efc-752b-4e6b-aed0-420ae4c7e6c7.tar".substring(0, "178a7efc-752b-4e6b-aed0-420ae4c7e6c7.tar".lastIndexOf(".")));
	
		/*BufferedReader in;
		in = new BufferedReader(new FileReader("/tickets/26cbe84f-e190-3b01-b7b5-8dd01eba4d3b"));*/
		Pattern p1 = Pattern.compile("/tickets/.*");
		//String s = in.readLine();
		Matcher matcher = p1.matcher("/tickets");
		System.out.println(matcher.find());
	}
	
	/**
	 * @param clz
	 * @return
	 */
	public static boolean isWrapClass(Class<?> clz) {    
        try {    
           return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();   
        } catch (Exception e) {    
            return false;
        }    
    }    
	
	@Test
	public void testFromObjectToJsonFilter() {
		Set<String> properties = new HashSet<String>();
		properties.add("name");
		String jsonStr = JsonUtils.fromObjectToJson(tjo3, "testFilter", properties);
		System.out.println(jsonStr);
	}
	
	@Test
	public void testDate() throws java.text.ParseException{
		DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date date1 = new Date(1340335617681l);
		System.out.println("date1" + df.format(date1));
		Date date2 = new Date(1340335613805l);
		System.out.println("date2" + df.format(date2));
		
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
		
		format.parse("09:00 PM");
	}
	
	@Test
	public void testFromJsonToObject() {
		String jsonStr = JsonUtils.fromObjectToJson(tjo1);
		
		TestJavaObject1 obj1 = new TestJavaObject1();
		
		
		obj1 = JsonUtils.fromJsonToObject("true", TestJavaObject1.class);
		//System.out.println(obj1.toString());
		
		System.out.println(DigestUtils.md5Hex("110110"));
	}
	
	@Test
	public void testCanLogObjectArrayJson() {
		Assert.assertTrue(JsonUtils.canLogObjectArrayJson(null));
		Assert.assertTrue(JsonUtils.canLogObjectArrayJson(new Object[]{}));
		Assert.assertFalse(JsonUtils.canLogObjectArrayJson(new Object[]{new JsonParserTest()}));
//		Assert.assertTrue(JsonUtils.canLogObjectArrayJson(new Object[]{new CommonException("test")}));
	}
	
	@Test
	public void testCanLogObjectJson() {
		Assert.assertFalse(JsonUtils.canLogObjectJson(null));
		Assert.assertFalse(JsonUtils.canLogObjectJson(new JsonParserTest()));
//		Assert.assertTrue(JsonUtils.canLogObjectJson(new CommonException("test")));
		Assert.assertFalse(JsonUtils.canLogObjectJson(new ParseException(0, null)));
	}
	
	
	public static class TestJavaObject2{
        private Long id;
        private String name;
        private Timestamp createdTimestamp;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Timestamp getCreatedTimestamp() {
			return createdTimestamp;
		}
		public void setCreatedTimestamp(Timestamp createdTimestamp) {
			this.createdTimestamp = createdTimestamp;
		}
    }
	
	public static class TestJavaObject1{
        private Long id;
        private String name;
        private List<String> childs;
        private Map<String, String> childsRelationship;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}

		@JsonIgnore
		public boolean isId() {
			return false;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<String> getChilds() {
			return childs;
		}
		public void setChilds(List<String> childs) {
			this.childs = childs;
		}
		public Map<String, String> getChildsRelationship() {
			return childsRelationship;
		}
		public void setChildsRelationship(Map<String, String> childsRelationship) {
			this.childsRelationship = childsRelationship;
		}
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
    }
	
	@JsonFilter("testFilter")
	public static class TestJavaObject3{
        private Long id;
        private String name;
        private List<String> childs;
        private Map<String, String> childsRelationship;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<String> getChilds() {
			return childs;
		}
		public void setChilds(List<String> childs) {
			this.childs = childs;
		}
		public Map<String, String> getChildsRelationship() {
			return childsRelationship;
		}
		public void setChildsRelationship(Map<String, String> childsRelationship) {
			this.childsRelationship = childsRelationship;
		}
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
    }

}
