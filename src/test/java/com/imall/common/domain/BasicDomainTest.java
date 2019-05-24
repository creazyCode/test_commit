package com.imall.common.domain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.imall.common.domain.BasicDomain;

/**
 * Test Basic Domain VO.
 * 
 * @author jianxun.ji
 * 
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:action-servlet.xml" })
public class BasicDomainTest {

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test hash code.
	 *
	 */
	@Test
	public void testHashCode() {
		Long id = 100L;
		BasicDomain basicDomain1 = new BasicDomain();
		basicDomain1.setUid(id);
		
		BasicDomain basicDomain2 = new BasicDomain();
		
		Assert.assertEquals(basicDomain1.hashCode(), id.hashCode());
		Assert.assertEquals(basicDomain2.hashCode(), Long.valueOf(0).hashCode());
	}

	/**
	 * Test equals object.
	 *
	 */
	@Test
	public void testEqualsObject() {
		BasicDomain basicDomain1 = new BasicDomain();
		basicDomain1.setUid(100L);

		BasicDomain basicDomain2 = new BasicDomain();
		basicDomain2.setUid(100L);

		BasicDomain basicDomain3 = new BasicDomain();
		basicDomain3.setUid(110L);

		BasicDomain basicDomain4 = null;

//		BasicDomain basicDomain5 = new User();
//		basicDomain5.setUid(100L);

		Assert.assertTrue(basicDomain1.equals(basicDomain2));
		Assert.assertFalse(basicDomain1.equals(basicDomain3));
		Assert.assertFalse(basicDomain1.equals(basicDomain3));
		Assert.assertFalse(basicDomain2.equals(basicDomain4));
//		Assert.assertFalse(basicDomain1.equals(basicDomain5));
	}
	
	@Test
	public void testJingdu(){
		System.out.println(100000000L * 10000000);
		System.out.println(100000000 * 10000000L);
		System.out.println(100000000 * 10000000);
		
		int i = 100000000 * 10000000;
		System.out.println(i);
		
		long j = 100000000 * 10000000;
		System.out.println(j);
		
		j = 100000000L * 10000000;
		System.out.println(j);
	}
}
