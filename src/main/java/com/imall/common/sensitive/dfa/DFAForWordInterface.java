package com.imall.common.sensitive.dfa;

import java.io.UnsupportedEncodingException;

/**
 * Created by frankie on 2016/10/9.
 */
public interface DFAForWordInterface {
    String searchKeyword(String text) throws UnsupportedEncodingException;
}
