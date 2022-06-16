package com.vvv.dao.UserDao;

import com.mysql.cj.util.StringUtils;
import com.vvv.dao.BaseDao;
import com.vvv.pojo.Role;
import com.vvv.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HP
 * @date 2022/4/9
 */
public class UserDaoImpl implements UserDao{


    public User getLoginUser(Connection connection, String userCode) throws SQLException {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if(connection!=null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};

            rs = BaseDao.executeQuery(connection,pstm,rs,sql,params);
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }

        return user;
    }

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException {

        PreparedStatement pstm = null;
        int updateRows = 0;

        if (connection!=null){
            String sql = "update smbms_user set userPassword=? where id=?";
            Object[] params = {password,id};
            updateRows = BaseDao.executeUpdate(connection, pstm, sql, params);

            //在哪用在哪关
            BaseDao.closeResource(null,pstm,null);
        }

        return updateRows;
    }

    public int getUserCount(Connection connection,String username, int userRole) throws SQLException{

        PreparedStatement pstm = null;
        ResultSet res = null;
        int count = 0;

        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");

            List<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and userName like ?");
                list.add("%"+username+"%");
            }

            if(userRole>0){
                sql.append(" and userRole = ?");
                list.add(userRole);
            }

            Object[] params = list.toArray();
            res = BaseDao.executeQuery(connection,pstm,res,sql.toString(),params);

            if(res.next()){
                count = res.getInt("count");
            }
        }
        BaseDao.closeResource(null,pstm,res);

        return count;
    }


    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet res = null;
        List<User> userList = new ArrayList<>();

        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");

            List<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and userName like ?");
                list.add("%"+username+"%");
            }

            if(userRole>0){
                sql.append(" and userRole = ?");
                list.add(userRole);
            }
            sql.append(" ORDER BY u.creationDate DESC LIMIT ?,?");
            int currentPageStart = (currentPageNo-1)*pageSize;
            list.add(currentPageStart);
            list.add(pageSize);

            Object[] params = list.toArray();
            res = BaseDao.executeQuery(connection,pstm,res,sql.toString(),params);

            while (res.next()){
                User _user = new User();
                _user.setId(res.getInt("id"));
                _user.setUserCode(res.getString("userCode"));
                _user.setUserName(res.getString("userName"));
                _user.setGender(res.getInt("gender"));
                _user.setBirthday(res.getDate("birthday"));
                _user.setPhone(res.getString("phone"));
                _user.setUserRole(res.getInt("userRole"));
                _user.setUserRoleName(res.getString("userRoleName"));
                userList.add(_user);
            }
        }
        BaseDao.closeResource(null,pstm,res);

        return userList;
    }


    @Test
    public void test() throws SQLException {
        Connection connection = BaseDao.getConnection();
        List<User> userList = this.getUserList(connection,null,0,1,5);
        for (User user : userList) {
            System.out.println(user.getUserName());
        }
        BaseDao.closeResource(connection,null,null);
    }
}

