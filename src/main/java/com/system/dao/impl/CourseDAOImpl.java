package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.Course;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements BaseDAO<Course> {

    @Override
    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_name, credits, major_id, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setInt(3, course.getMajorId());
            pstmt.setString(4, course.getDescription());
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
    public void update(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_name=?, credits=?, major_id=?, description=? WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setInt(3, course.getMajorId());
            pstmt.setString(4, course.getDescription());
            pstmt.setInt(5, course.getCourseId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Course findById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToCourse(rs);
            }
        }
        return null;
    }

    @Override
    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToCourse(rs));
        }
        return list;
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseName(rs.getString("course_name"));
        c.setCredits(rs.getInt("credits"));
        c.setMajorId(rs.getInt("major_id"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}