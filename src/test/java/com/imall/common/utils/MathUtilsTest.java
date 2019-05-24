package com.imall.common.utils;


import junit.framework.Assert;

import org.junit.Test;

public class MathUtilsTest {
	
	@Test
	public void testKeep4Decimal() {
		Double doubleValue = 12867.12345;
		Assert.assertEquals(12867.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 12867.1234999;
		Assert.assertEquals(12867.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 12867.1234111;
		Assert.assertEquals(12867.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 12867.12;
		Assert.assertEquals(12867.12, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 0.12;
		Assert.assertEquals(0.12, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 0.1234999;
		Assert.assertEquals(0.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 0.1234544;
		Assert.assertEquals(0.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 1.1234999;
		Assert.assertEquals(1.1234, MathUtils.keep4Decimal(doubleValue));
		
		doubleValue = 1.1234544;
		Assert.assertEquals(1.1234, MathUtils.keep4Decimal(doubleValue));
	}
}
