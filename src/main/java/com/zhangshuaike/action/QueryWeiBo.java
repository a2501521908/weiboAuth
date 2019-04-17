package com.zhangshuaike.action;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.TypeReference;
import com.zhangshuaike.utils.HttpsUtil;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.User;
import weibo4j.model.WeiboException;


@WebServlet(name = "QueryWeiBo", urlPatterns = "/auto.do")
public class QueryWeiBo extends HttpServlet {
	private final static String CLIENT_ID = "";
	private final static String CLIENT_SERCRET = "";
	private final static String GET_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";
	private final static String REDIRECT_URI = "";
	private final static String GET_USER_INFO = "https://api.weibo.com/2/users/show.json";
	private final static String GET_TOKEN_INFO_URL = "https://api.weibo.com/oauth2/get_token_info";
	private final static String STATE = "register";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		// 获取code
		String code = request.getParameter("code");
		String access_token = "";
		String expires_in = "";
		String uid = "";

		// 获取token
		JSONObject token = null;
		try {
			token = getAccessToken(code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			access_token = token.getString("access_token");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			uid = token.getString("uid");
			System.out.println(uid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			expires_in = String.valueOf(token.getInt("expires_in"));
			System.out.println(expires_in);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 获取用户信息
		JSONObject userInfo = null;
		try {
			userInfo = getUserInfo(access_token, uid);
			request.setAttribute("userInfo", userInfo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String nickname = userInfo.getString("screen_name");
			request.setAttribute("nickname", nickname);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String profile_image_url = userInfo.getString("profile_image_url");
			request.setAttribute("profile_image_url", profile_image_url);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String gender = "f".equals(userInfo.getString("gender")) ? "1" : "0";
			request.setAttribute("gender", gender);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		request.getRequestDispatcher("auth.jsp").forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * 获取AccessToken
	 * 
	 * @throws JSONException
	 */
	private JSONObject getAccessToken(String code) throws JSONException {
		StringBuilder sb = new StringBuilder();
		sb.append("grant_type=authorization_code");
		sb.append("&client_id=" + CLIENT_ID);
		sb.append("&client_secret=" + CLIENT_SERCRET);
		sb.append("&redirect_uri=" + REDIRECT_URI);
		sb.append("&code=" + code);
		String result = HttpsUtil.post(GET_TOKEN_URL, sb.toString());
		/**
		 * 返回数据 { "access_token": "ACCESS_TOKEN", "expires_in": 1234,
		 * "remind_in":"798114", "uid":"12341234" }
		 */
		JSONObject json = new JSONObject(result);
		return json;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param access_token
	 * @param uid
	 *            查询的用户ID
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getUserInfo(String access_token, String uid) throws JSONException {
		StringBuilder sb = new StringBuilder();
		sb.append("?access_token=" + access_token);
		sb.append("&uid=" + uid);
		String result = HttpsUtil.get(GET_USER_INFO + sb.toString());
		// 返回参数：查看http://open.weibo.com/wiki/2/users/show
		JSONObject json = new JSONObject(result);
		return json;
	}

}