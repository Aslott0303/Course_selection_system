package com.system.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    private static String url;
    private static String username;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new IOException("db.properties 未找到，请放在 src/resources 下并标记为 Resources Root");
            }
            props.load(is);
            url = props.getProperty("url");
            username = props.getProperty("username");
            password = props.getProperty("password");

            // 注册驱动（MySQL 8+ 可省略，但保留兼容性）
            //Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("数据库配置加载失败");
        }
    }

    /** 获取数据库连接 */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /** 关闭资源 - 重载1：Connection */
    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** 关闭资源 - 重载2：Connection + Statement */
    public static void close(Connection conn, Statement stmt) {
        close(stmt);
        close(conn);
    }

    /** 关闭资源 - 重载3：Connection + Statement + ResultSet */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    private static void close(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private static void close(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
