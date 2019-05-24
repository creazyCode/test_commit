package com.imall.common.utils;

import org.slf4j.Logger;

import com.imall.common.exception.BaseException;

public class LogUtils {
    /*public static final void clientError(Integer id, String errors) {
        if (errors == null || id == null || errors.endsWith("{}")) {
            return;
        }
        if (logService != null && !useLocalLog) {
            logService.log(WARN, id, "CLIERR", errors);
        } else {
            cerrLog.warn("u{} - error : {}", id, errors);
        }
    }*/
    
    public static final void logError(Logger logger, Long userId, Exception ex){
    	StringBuilder sb = new StringBuilder();
    	sb.append("u: ");
    	if(userId != null){
    		sb.append(userId);
    	}else{
    		sb.append(0);
    	}
        sb.append(" -");
        if(ex != null){
        	sb.append(BaseException.getInfo(ex));
        }
        StackTraceElement[] stacks = new Throwable().getStackTrace();

        for (int i = 1; i < stacks.length; ++i) {
            if (!stacks[i].getClassName().startsWith(LogUtils.class.getName())) {
                sb.append("\t" + stacks[i].getFileName() + ":" + stacks[i].getLineNumber() + "," + stacks[i].getMethodName());
                break;
            }
        }
        logger.error(sb.toString());
    }
    
    /*public static final void logPlatformError(int userId, Exception ex){
    	StringBuilder sb = new StringBuilder();
    	sb.append("u");
        sb.append(userId);
        sb.append(" ");
        if(ex != null){
        	sb.append(BaseException.getInfo(ex));
        }
        StackTraceElement[] stacks = new Throwable().getStackTrace();

        for (int i = 1; i < stacks.length; ++i) {
            if (!stacks[i].getClassName().startsWith(LogUtils.class.getName())) {
                sb.append("\t" + stacks[i].getFileName() + ":" + stacks[i].getLineNumber());
                break;
            }
        }
		platFormErrorLog.error(sb.toString());
    }*/
}
