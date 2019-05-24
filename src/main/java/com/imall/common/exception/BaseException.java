package com.imall.common.exception;

/**
 * This is the base exception class
 * 
 * @author jianxun.ji
 * 
 */
public abstract class BaseException extends RuntimeException {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -4236543133123967544L;
	
	 /**
    * The default constructor.
    */
   public BaseException() {
       super();
   }


   /**
    * Constructor
    * @param message 
    * @param cause 
    */
   public BaseException(String message, Throwable cause) {
       super(message, cause);
   }


   /**
    * Constructor
    * @param message 
    */
   public BaseException(String message) {
       super(message);
   }


   /**
    * Constructor
    * @param cause 
    */
   public BaseException(Throwable cause) {
       super(cause);
   }


   /**
    * @return The exception error code.
    */
   public abstract int getErrorCode();


   /**
    * @return The textual message description of exception.
    */
   public abstract String getErrorText();

   public static String getInfo(Exception ex) {
		boolean flag = true;
		StringBuilder sb = new StringBuilder();
		sb.append(ex.getMessage());
		for (StackTraceElement element: ex.getStackTrace()) {
			if (element.getClassName().startsWith("com.imall")) {
				if (flag) {
					sb.append("\t" + element.getFileName() + ":" + element.getLineNumber() + "," + element.getMethodName());
					flag = false;
				}else {
					sb.append("\n" + element.getFileName() + ":" + element.getLineNumber() + "," + element.getMethodName());
				}
			}
		}
		return sb.toString();
	}
}
