package com.imall.qq;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.utils.QQConnectConfig;
import com.qq.connect.utils.http.PostParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QqOpenId extends OpenID {
    public static final Logger LOGGER = LoggerFactory.getLogger(QqOpenId.class);
    private static final long serialVersionUID = 6913009055508673584L;

    public QqOpenId(String token) {
        super(token);
    }

    private String[] getUserOpenIdAndUnionId(String accessToken) throws QQConnectException {
        String openId = null;
        String unionId = null;
        String jsonp = this.client.get(QQConnectConfig.getValue("getOpenIDURL"),
                new PostParameter[]{new PostParameter("access_token", accessToken),
                        new PostParameter("unionid", 1)}).asString();
        Matcher openidMatcher = Pattern.compile("\"openid\"\\s*:\\s*\"(\\w+)\"").matcher(jsonp);
        if (openidMatcher.find()) {
            openId = openidMatcher.group(1);

            Matcher unionIdMatcher = Pattern.compile("\"unionid\"\\s*:\\s*\"(\\w+)\"").matcher(jsonp);
            if(unionIdMatcher.find()){
                unionId = unionIdMatcher.group(1);
            }
        }
        return new String[]{openId, unionId};
    }

    public String[] getUserOpenIdAndUnionId(){
        String accessToken = null;
        try {
            accessToken = this.client.getToken();
            return this.getUserOpenIdAndUnionId(accessToken);
        } catch (QQConnectException e) {
            LOGGER.error("get open id and union id by access token: " + accessToken + " error", e);
        }
        return null;
    }

    public String getUnionId(String clientId, String openId){
        String unionId = null;
        String jsonp = null;
        try {
            jsonp = this.client.get(QQConnectConfig.getValue("getUnionIdURL"),
                    new PostParameter[]{new PostParameter("openid", openId),
                            new PostParameter("client_id", clientId)}).asString();
            Matcher unionIdMatcher = Pattern.compile("\"unionid\"\\s*:\\s*\"(\\w+)\"").matcher(jsonp);
            if (unionIdMatcher.find()) {
                unionId = unionIdMatcher.group(1);
            }
        } catch (QQConnectException e) {
            LOGGER.error("get union id by client id: " + clientId + " and open id: " + openId + " error", e);
        }
        return unionId;
    }
}
