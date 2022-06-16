package com.vvv.dao.RoleDao;

import com.vvv.dao.BaseDao;
import com.vvv.pojo.Role;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HP
 * @date 2022/4/11
 */
public class RoleDaoImpl implements RoleDao{


    public List<Role> getRoleList(Connection connection) throws SQLException {

        PreparedStatement pstm = null;
        ResultSet res = null;
        List<Role> roleList = new ArrayList<>();

        if(connection!=null){
            String sql = "select * from smbms_role";
            Object[] params = {};
            res = BaseDao.executeQuery(connection,pstm,res,sql,params);

            while (res.next()){
                Role _role = new Role();
                _role.setId(res.getInt("id"));
                _role.setRoleCode(res.getString("roleCode"));
                _role.setRoleName(res.getString("roleName"));

                roleList.add(_role);
            }

            BaseDao.closeResource(null,pstm,res);
        }

        return roleList;
    }

    @Test
    public void test() throws SQLException {
        Connection connection = BaseDao.getConnection();
        List<Role> roleList = null;

        roleList = this.getRoleList(connection);

        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }

}
