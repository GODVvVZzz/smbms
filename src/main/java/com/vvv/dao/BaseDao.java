package com.vvv.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * @author HP
 * @date 2022/4/9
 * 操作数据库的公共类
 */
public class BaseDao {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //类加载时候就初始化
    static {
        Properties properties = new Properties();

        //通过类加载器读取对应的资源
        InputStream in = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    //获取数据库的连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            //初始化该类
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共方法
    public static ResultSet executeQuery(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //i+1是因为setobject从1开始
            preparedStatement.setObject(i+1,params[i]);
        }

        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共方法
    public static int executeUpdate(Connection connection,PreparedStatement preparedStatement,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //i+1是因为setobject从1开始
            preparedStatement.setObject(i+1,params[i]);
        }

        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //释放资源
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag = true;

        if(resultSet != null){
            try {
                resultSet.close();
                //GC垃圾回收
                resultSet = null;
            } catch (Exception throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        if(preparedStatement != null){
            try {
                preparedStatement.close();
                //GC垃圾回收
                preparedStatement = null;
            } catch (Exception throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                //GC垃圾回收
                connection = null;
            } catch (Exception throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }
}


