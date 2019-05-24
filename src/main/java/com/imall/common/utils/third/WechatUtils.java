package com.imall.common.utils.third;

import com.imall.common.utils.HttpClientUtils;
import com.imall.common.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WechatUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(WechatUtils.class);

	public static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=APP_ID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
	public static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APP_ID&secret=APP_SECRET&code=CODE&grant_type=authorization_code";

	public static String getAuthorizeUrl(String appId, String redirectUri, String state){
        return AUTHORIZE_URL.replace("APP_ID", appId)
                .replace("REDIRECT_URI", redirectUri)
                .replace("STATE", state);
	}

	public static String[] getAccessTokenAndUserId(String appId, String appSecret, String redirectUri, String code){
        String[] returns = null;
	    String accessToken = null;
	    String userId = null;
        String url = GET_ACCESS_TOKEN_URL.replace("APP_ID", appId)
                .replace("APP_SECRET", appSecret)
                .replace("CODE", code);
        String response = HttpClientUtils.get(url, null, null);
        if(!StringUtils.isBlank(response)){
            Map<String, Object> responseMap = JsonUtils.fromJsonToMap(response);
            if(responseMap != null){
                accessToken = (String) responseMap.get("access_token");
                userId = (String) responseMap.get("uid");
            }
        }
        if(!StringUtils.isBlank(accessToken) && !StringUtils.isBlank(userId)){
            returns = new String[2];
            returns[0] = accessToken;
            returns[0] = userId;
        }
        return returns;
	}

}
