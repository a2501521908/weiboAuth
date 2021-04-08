package com.zhangshuaike.action;

import java.io.IOException;

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
 * @date : 2021/04/08
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
    String accessToken,expiresIn,uid;

    // 获取token
    JSONObject token;

    // 获取用户信息
    JSONObject userInfo;
    token = getAccessToken(code);
    accessToken = token.getString("access_token");
    uid = token.getString("uid");
    expiresIn = token.getString("expires_in");

    // 获取用户信息
    userInfo = getUserInfo(accessToken, uid);
    String nickname = userInfo.getString("screen_name");
    String profileImageUrl = userInfo.getString("profile_image_url");
    String gender = "f".equals(userInfo.getString("gender")) ? "1" : "0";
    request.setAttribute("gender", gender);
    request.setAttribute("userInfo", userInfo);
    request.setAttribute("nickname", nickname);
    request.setAttribute("profile_image_url", profileImageUrl);
    request.setAttribute("expires_in", expiresIn);

    request.getRequestDispatcher("auth.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    this.doGet(request, response);
  }

  /** 获取AccessToken */
  private JSONObject getAccessToken(String code) {
    String params =
        "grant_type=authorization_code"
            + "&client_id="
            + Constants.CLIENT_ID
            + "&client_secret="
            + Constants.CLIENT_SECRET
            + "&redirect_uri="
            + Constants.REDIRECT_URI
            + "&code="
            + code;
    String result = HttpUtil.post(Constants.GET_TOKEN_URL, params);
    // 返回数据 { "access_token": "ACCESS_TOKEN", "expires_in": 1234, "remind_in":"798114","uid":"12341234" }
    return JSONObject.parseObject(result);
  }

  /**
   * 获取用户信息
   *
   * @param accessToken 临时授权访问资源的token
   * @param uid 查询的用户ID
   * @return UserInfo JSON
   */
  private JSONObject getUserInfo(String accessToken, String uid) {
    String params = "?access_token=" + accessToken + "&uid=" + uid;
    String result = HttpUtil.get(Constants.GET_USER_INFO + params);
    // 返回参数：查看http://open.weibo.com/wiki/2/users/show
    return JSONObject.parseObject(result);
  }
}
