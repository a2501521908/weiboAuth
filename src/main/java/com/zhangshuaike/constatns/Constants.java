package com.zhangshuaike.constatns;

/**
 * 微博登录demo项目相关常用常量类
 *
 * @author : <a href="mailto:zhangshuaike@matcloudplus.com">张帅轲</a>
 * @version : 1.0
 * @date : 2021/4/8 10:05
 */
public class Constants {

    /**
     * app id
     */
    public static final String CLIENT_ID = "3442370524";

    /**
     * app secret
     *
     * 此密钥已进行重置，请申请您自己的密钥进行
     */
    public static final String CLIENT_SECRET = "";

    /**
     * redirect_uri
     */
    public static final String REDIRECT_URI = "http://127.0.0.1:8080/auth.do";

    public static final String GET_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";
    public static final String GET_USER_INFO = "https://api.weibo.com/2/users/show.json";
    /**
     * weibo auth URL
     */
    public static final String URL =
            "https://api.weibo.com/oauth2/authorize?client_id="+CLIENT_ID+"&response_type=code&redirect_uri="+REDIRECT_URI;

}
