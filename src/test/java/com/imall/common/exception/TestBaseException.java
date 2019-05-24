package com.imall.common.exception;

import org.junit.Test;

import com.imall.common.exception.BaseException;

public class TestBaseException {
	@Test
	public void testBaseException() {
		new BaseException(){
			private static final long serialVersionUID = -7518358657981659350L;

			@Override
			public int getErrorCode() {
				return 0;
			}

			@Override
			public String getErrorText() {
				return null;
			}
		};
	}

	@Test
	public void testBaseExceptionStringThrowable() {
		new BaseException("test", new NullPointerException("test error")){
			private static final long serialVersionUID = -7518358657981659350L;

			@Override
			public int getErrorCode() {
				return 0;
			}

			@Override
			public String getErrorText() {
				return null;
			}
		};
	}

	@Test
	public void testBaseExceptionString() {
		new BaseException("test"){
			private static final long serialVersionUID = -7518358657981659350L;

			@Override
			public int getErrorCode() {
				return 0;
			}

			@Override
			public String getErrorText() {
				return null;
			}
		};
	}

	@Test
	public void testBaseExceptionThrowable() {
		new BaseException(new NullPointerException("test error")){
			private static final long serialVersionUID = -7518358657981659350L;

			@Override
			public int getErrorCode() {
				return 0;
			}

			@Override
			public String getErrorText() {
				return null;
			}
		};
	}
}
