package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.Teacher;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAOImpl implements BaseDAO<Teacher> {

    @Override
    public int insert(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers (name, gender, title) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getGender());
            pstmt.setString(3, teacher.getTitle());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
            return rows;
        }
    }

    @Override
    public void update(Teacher teacher) throws SQLException {
        String sql = "UPDATE teachers SET name=?, gender=?, title=? WHERE teacher_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getGender());
            pstmt.setString(3, teacher.getTitle());
            pstmt.setInt(4, teacher.getTeacherId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM teachers WHERE teacher_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Teacher findById(int id) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE teacher_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToTeacher(rs);
            }
        }
        return null;
    }

    @Override
    public List<Teacher> findAll() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToTeacher(rs));
        }
        return list;
    }

    private Teacher mapRowToTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getInt("teacher_id"));
        t.setName(rs.getString("name"));
        t.setGender(rs.getString("gender"));
        t.setTitle(rs.getString("title"));
        return t;
    }
}