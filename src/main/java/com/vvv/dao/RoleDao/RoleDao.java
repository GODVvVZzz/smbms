package com.vvv.dao.RoleDao;

import com.vvv.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author HP
 * @date 2022/4/11
 */
public interface RoleDao {
    //查询角色列表
    public List<Role> getRoleList(Connection connection) throws SQLException;
}
