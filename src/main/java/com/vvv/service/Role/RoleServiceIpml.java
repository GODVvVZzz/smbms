package com.vvv.service.Role;

import com.vvv.dao.BaseDao;
import com.vvv.dao.RoleDao.RoleDao;
import com.vvv.dao.RoleDao.RoleDaoImpl;
import com.vvv.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HP
 * @date 2022/4/11
 */
public class RoleServiceIpml implements RoleService{

    private RoleDao roleDao;

    public RoleServiceIpml(){
        roleDao = new RoleDaoImpl();
    }

    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;

        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }

        return roleList;
    }

}
