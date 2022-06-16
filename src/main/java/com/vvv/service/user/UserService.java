package com.vvv.service.user;

import com.vvv.pojo.User;

import java.util.List;

/**
 * @author HP
 * @date 2022/4/10
 */
public interface UserService {
    //用户登录
    public User login(String userCode, String password);

    //修改密码
    public boolean updatePwd(int id, String pwd);

    //查询记录数
    public int getUserCount(String username,int userRole);

    //查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);
}

