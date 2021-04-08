package com.zhangshuaike.action;

import com.zhangshuaike.constatns.Constants;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 处理用户登录微博的请求
 *
 * @author : <a href="mailto:2501521908@qq.com">张帅轲</a>
 * @date : 2021/04/08
 * @version : 1.0
 */
@WebServlet(name = "DoLoginServlet", urlPatterns = "/dologin.do")
public class DoLoginServlet extends HttpServlet {

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
  	//redirect  login url
    resp.sendRedirect(Constants.URL);
  }
}
