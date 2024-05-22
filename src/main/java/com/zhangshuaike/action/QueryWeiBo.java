package com.zhangshuaike.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhangshuaike.constatns.Constants;

/**
 * 微博相关控制层,优化版本
 *
 * @author : <a href="mailto:2501521908@qq.com">张帅轲</a>
 * @version : 1.0
 */
@WebServlet(name = "QueryWeiBo", urlPatterns = "/auth.do")
public class QueryWeiBo extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        // 获取code
        String code = request.getParameter("code");

        //获取accessToken
        JSONObject accessTokenJson = getAccessToken(code);
        //获取accessToken中的信息和uid等，为获取用户信息做准备
        String accessToken = accessTokenJson.getString("access_token");
        String uid = accessTokenJson.getString("uid");

        // 获取用户信息，里面包含了许多，全都在userInfo里面
        JSONObject userInfo = getUserInfo(accessToken, uid);

        //设置视图模型，为展示做准备
        setIndexView(request, userInfo);
        // 转发到auth.jsp页面
        request.getRequestDispatcher("auth.jsp").forward(request, response);
    }

    /**
     * 设置索引页面的视图信息。
     * 该方法会从userInfo中提取用户的基本信息，并将这些信息设置到request中，以便在索引页面上显示。
     *
     * @param request HttpServletRequest对象，用于在请求范围内存储属性。
     * @param userInfo 包含用户信息的JSONObject对象，需要从中提取用户的基本信息。
     */
    private void setIndexView(HttpServletRequest request, JSONObject userInfo) {
        // 提取用户基本信息
        String nickname = userInfo.getString("screen_name");
        String profileImageUrl = userInfo.getString("profile_image_url");
        String gender = "f".equals(userInfo.getString("gender")) ? "1" : "0";
        // 设置用户性别属性
        request.setAttribute("gender", gender);
        // 设置用户详细信息属性
        request.setAttribute("userInfo", userInfo);
        // 设置用户昵称属性
        request.setAttribute("nickname", nickname);
        // 设置用户头像图片URL属性
        request.setAttribute("profile_image_url", profileImageUrl);
    }


    /**
     * 获取AccessToken
     *
     * @param code 微博返回临时获取token令牌
     * @return 返回数据 { "access_token": "ACCESS_TOKEN", "expires_in": 1234, "remind_in":"798114","uid":"12341234" }
     */
    private JSONObject getAccessToken(String code) {
        // 封装请求参数
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("client_id", Constants.CLIENT_ID);
        paramMap.put("client_secret", Constants.CLIENT_SECRET);
        paramMap.put("redirect_uri", Constants.REDIRECT_URI);
        paramMap.put("code", code);
        //得到本次请求服务器结果
        String result = HttpUtil.post(Constants.GET_TOKEN_URL, paramMap);
        return JSONObject.parseObject(result);
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 临时授权访问资源的token
     * @param uid         查询的用户ID
     * @return UserInfo JSON 返回参数：查看http://open.weibo.com/wiki/2/users/show
     */
    private JSONObject getUserInfo(String accessToken, String uid) {
        // 封装请求参数
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("access_token", accessToken);
        paramMap.put("uid", uid);
        //得到本次请求服务器结果
        String result = HttpUtil.get(Constants.GET_USER_INFO ,paramMap);
        return JSONObject.parseObject(result);
    }
}
