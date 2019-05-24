package com.imall.common.exception;

public class UnexpectedException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5756700734084381080L;

	public UnexpectedException() {
		super();
	}
	
	public UnexpectedException(Throwable cause) {
		super(cause);
	}
	
	@Override
	public int getErrorCode() {
		return 10000;
	}

	@Override
	public String getErrorText() {
		return "Unexpected Exception";
	}
}
