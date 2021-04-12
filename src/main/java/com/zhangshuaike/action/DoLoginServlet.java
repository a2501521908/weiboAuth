package com.zhangshuaike.action;

import com.zhangshuaike.constatns.Constants;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理用户登录微博的请求,带着用户去登录
 * <p>注意，此url生成的链接可以完全写死前端，给后端是给部分定制化业务的开发去使用</p>
 *
 * @author : <a href="mailto:2501521908@qq.com">张帅轲</a>
 * @version : 1.0
 * @date : 2021/04/08
 */
@WebServlet(name = "DoLoginServlet", urlPatterns = "/dologin.do")
public class DoLoginServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //redirect  login url
        resp.sendRedirect(Constants.URL);
    }
}
