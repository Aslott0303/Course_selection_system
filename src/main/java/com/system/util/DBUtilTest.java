package com.system.util;

import java.sql.Connection;

public class DBUtilTest {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("数据库连接成功！" + conn);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败，请检查 db.properties 和 MySQL 服务");
        }
    }
}