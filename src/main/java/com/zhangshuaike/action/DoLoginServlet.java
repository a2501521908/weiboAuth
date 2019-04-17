package com.zhangshuaike.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weibo4j.Oauth;
import weibo4j.model.WeiboException;
@WebServlet(name="DoLoginServlet",urlPatterns="/dologin.do")
public class DoLoginServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Oauth oauth = new Oauth();
		String url = null;
		try {
			url = oauth.authorize("code", null);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		response.sendRedirect(url);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
