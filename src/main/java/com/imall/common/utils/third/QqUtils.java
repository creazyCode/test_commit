package com.imall.common.utils.third;

import com.imall.common.utils.HttpClientUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QqUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(QqUtils.class);

	public static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=APP_ID&redirect_uri=REDIRECT_URI&scope=SCOPE&state=STATE";
	public static final String GET_ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=APP_ID&client_secret=APP_SECRET&code=CODE&state=STATE&redirect_uri=REDIRECT_URI";

	public static String getAuthorizeUrl(String appId, String state, String redirectUri, boolean isFromMobile){
	    String scope = "get_user_info";
        String url = AUTHORIZE_URL.replace("APP_ID", appId)
                .replace("REDIRECT_URI", redirectUri)
                .replace("SCOPE", scope)
                .replace("STATE", state);
        if(isFromMobile){
            url += "&display=mobile";
        }
        return url;
	}

	public static String getAccessToken(String appId, String appSecret, String code, String state, String redirectUri){
	    String accessToken = null;
        String url = GET_ACCESS_TOKEN_URL.replace("APP_ID", appId)
                .replace("APP_SECRET", appSecret)
                .replace("CODE", code)
                .replace("STATE", state)
                .replace("REDIRECT_URI", redirectUri);
        String response = HttpClientUtils.get(url, null, null);
        if(!StringUtils.isBlank(response)){
            Matcher matcher = Pattern.compile("access_token\\s*=\\s*(\\w+)&").matcher(response);
            if(matcher.find()){
                accessToken = matcher.group(1);
            }
        }
        return accessToken;
	}

}
