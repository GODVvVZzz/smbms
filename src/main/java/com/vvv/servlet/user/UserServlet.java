package com.vvv.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.vvv.pojo.Role;
import com.vvv.pojo.User;
import com.vvv.service.Role.RoleService;
import com.vvv.service.Role.RoleServiceIpml;
import com.vvv.service.user.UserService;
import com.vvv.service.user.UserServiceImpl;
import com.vvv.util.Constants;
import com.vvv.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HP
 * @date 2022/4/10
 */
@WebServlet("/jsp/user.do")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getParameter("method");
        if(method!=null && method.equals("savepwd")){
            this.updataPwd(req,resp);
        }else if (method!=null && method.equals("pwdmodify")){
            this.pwdModify(req,resp);
        }else if (method!=null && method.equals("query")){
            this.query(req,resp);
        }


    }

    //查询用户列表
    private void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        UserService userService = new UserServiceImpl();

        //第一次走页面一定是第一页，页面大小是固定的

        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        if(StringUtils.isNullOrEmpty(queryUserName)){
            queryUserName = "";
        }

        if(!StringUtils.isNullOrEmpty(temp)){
            queryUserRole = Integer.parseInt(temp);
        }

        if(pageIndex != null){
            currentPageNo = Integer.valueOf(pageIndex);
        }

        //总数量(表）
        int totalCount = userService.getUserCount(queryUserName,queryUserRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();

        //控制尾页首页
        if (currentPageNo < 1){
            currentPageNo = 1;
        }else if (currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        List<User> userList = null;
        userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
        req.setAttribute("userList",userList);
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceIpml();
        roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        //数据持续显示在页面上
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);

        req.getRequestDispatcher("userlist.jsp").forward(req,resp);
    }

    public void updataPwd(HttpServletRequest req, HttpServletResponse resp){
        //从session拿用户id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //从jsp拿newpassword
        String newpassword = req.getParameter("newpassword");

        boolean flag = false;
        if (o!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if (flag){
                req.setAttribute("message","修改密码成功，请退出，使用新密码登录");
                //密码修改成功，移除当前用户session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{
                req.setAttribute("message","修改密码失败");
            }
        }else {
            req.setAttribute("message","新密码有问题");
        }

        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){

        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");


        Map<String, String> resultMap = new HashMap<String, String>();

        if(o==null){
            resultMap.put("result","sessionError");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");
        }else {
            String sessionPwd = ((User)o).getUserPassword();
            if (sessionPwd.equals(oldpassword)) {
                resultMap.put("result", "true");
            }else {
                resultMap.put("result", "false");
            }
        }

        //将map转换成json
        try {
            resp.setContentType("/application/json");
            PrintWriter writer = resp.getWriter();

            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
