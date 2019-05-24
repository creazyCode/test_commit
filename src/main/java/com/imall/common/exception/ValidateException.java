package com.imall.common.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * This exception is thrown when the parameter is error
 * 
 * @date 2012.12.20
 * @author jianxun.ji
 * 
 */
public class ValidateException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8765138248481434488L;
	private int errorCode;
	private Set<ConstraintViolation<Object>> constraintViolations;
	private String[] str;

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public ValidateException(int errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param constraintViolations
	 */
	public ValidateException(int errorCode, String errorMessage,
			Set<ConstraintViolation<Object>> constraintViolations) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.constraintViolations = constraintViolations;
	}
	
	public ValidateException(int errorCode, String errorMessage,
			Set<ConstraintViolation<Object>> constraintViolations, String... str) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.constraintViolations = constraintViolations;
		this.str = str;
	}

	public String[] getStr() {
		return str;
	}

	public void setStr(String[] str) {
		this.str = str;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public Set<ConstraintViolation<Object>> getConstraintViolations() {
		return constraintViolations;
	}

	public void setConstraintViolations(
			Set<ConstraintViolation<Object>> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}
}
