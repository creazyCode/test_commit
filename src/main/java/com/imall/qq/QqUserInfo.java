package com.imall.qq;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.utils.QQConnectConfig;
import com.qq.connect.utils.http.PostParameter;
import org.apache.commons.lang.StringUtils;

public class QqUserInfo extends UserInfo{
    private String appId;

    public QqUserInfo(String appId, String token, String openID) {
        super(token, openID);
        this.appId = appId;
    }

    private UserInfoBean getUserInfo(String openid) throws QQConnectException {
        return new UserInfoBean(this.client.get(QQConnectConfig.getValue("getUserInfoURL"),
                new PostParameter[]{new PostParameter("openid", openid),
                        new PostParameter("oauth_consumer_key", !StringUtils.isBlank(this.appId) ? this.appId : QQConnectConfig.getValue("app_ID")),
                        new PostParameter("access_token", this.client.getToken()),
                        new PostParameter("format", "json")}).asJSONObject());
    }

    public UserInfoBean getUserInfo() throws QQConnectException {
        return this.getUserInfo(this.client.getOpenID());
    }
}
