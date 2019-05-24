package com.imall.common.utils.third;

import com.imall.common.utils.HttpClientUtils;
import com.imall.common.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WeiboUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(WeiboUtils.class);

	public static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize?client_id=CLIENT_ID&response_type=code&redirect_uri=REDIRECT_URI";
	public static final String GET_ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token?client_id=CLIENT_ID&client_secret=CLIENT_SECRET&grant_type=authorization_code&redirect_uri=REDIRECT_URI&code=CODE";

	public static String getAuthorizeUrl(String clientId, String redirectUri){
        return AUTHORIZE_URL.replace("CLIENT_ID", clientId)
                .replace("REDIRECT_URI", redirectUri);
	}

    public static String[] getAccessTokenAndUserId(String clientId, String clientSecret, String redirectUri, String code){
        String[] returns = null;
        String accessToken = null;
        String userId = null;
        String url = GET_ACCESS_TOKEN_URL.replace("CLIENT_ID", clientId)
                .replace("CLIENT_SECRET", clientSecret)
                .replace("REDIRECT_URI", redirectUri)
                .replace("CODE", code);
        Object responseObject = HttpClientUtils.post(url, null, null, null, null);
        String response = null;
        if(responseObject != null){
            response = (String)responseObject;
        }
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
            returns[1] = userId;
        }
        return returns;
    }
}
