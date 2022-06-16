package com.vvv.servlet.user;

import com.vvv.pojo.User;
import com.vvv.service.user.UserService;
import com.vvv.service.user.UserServiceImpl;
import com.vvv.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HP
 * @date 2022/4/10
 */
@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {

    //Servlet：控制层，调用业务层代码
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //和数据库中的密码对比，调用业务层
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        //查有此人，可以登录
        if(user!=null){

            //将用户信息放入session中，只要他没注销，我就可以随时查
            req.getSession().setAttribute(Constants.USER_SESSION,user);

            //登录成功，跳转内部主页。重定向
            resp.sendRedirect(req.getContextPath()+"/jsp/frame.jsp");
        }else { //查无此人，转发回登陆页面
            req.setAttribute("error","用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
