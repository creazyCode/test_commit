package com.imall.common.sensitive.dfa;

/**
 * Created by frankie on 2016/11/7.
 */


public class MinorityChecker {

    public static boolean hasMinority(String content) {
        if (content != null && ! content.trim().equals("")) {
            for (int i = 0; i < content.length(); i++) {
                char charAt = content.charAt(i);
                if (charAt >= 0x0600 && charAt <= 0x06FF) {
                    return true;
                } else if (charAt >= 0x0F00 && charAt <= 0x0FFF) {
                    return true;
                } else if (charAt >= 0x0400	&& charAt <= 0x04FF) {
                    return true;
                }
            }
        }
        return false;
    }
}
