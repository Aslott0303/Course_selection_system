package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.CourseOffering;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseOfferingDAOImpl implements BaseDAO<CourseOffering> {

    @Override
    public int insert(CourseOffering offering) throws SQLException {
        String sql = "INSERT INTO course_offerings (course_id, teacher_id, semester, major_id, max_capacity, current_enrolled) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, offering.getCourseId());
            pstmt.setInt(2, offering.getTeacherId());
            pstmt.setInt(3, offering.getSemester());
            pstmt.setInt(4, offering.getMajorId());
            pstmt.setInt(5, offering.getMaxCapacity());
            pstmt.setInt(6, offering.getCurrentEnrolled());
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
    public void update(CourseOffering offering) throws SQLException {
        String sql = "UPDATE course_offerings SET course_id=?, teacher_id=?, semester=?, major_id=?, max_capacity=?, current_enrolled=? " +
                "WHERE class_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offering.getCourseId());
            pstmt.setInt(2, offering.getTeacherId());
            pstmt.setInt(3, offering.getSemester());
            pstmt.setInt(4, offering.getMajorId());
            pstmt.setInt(5, offering.getMaxCapacity());
            pstmt.setInt(6, offering.getCurrentEnrolled());
            pstmt.setInt(7, offering.getClassId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM course_offerings WHERE class_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public CourseOffering findById(int id) throws SQLException {
        String sql = "SELECT * FROM course_offerings WHERE class_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToOffering(rs);
            }
        }
        return null;
    }

    @Override
    public List<CourseOffering> findAll() throws SQLException {
        List<CourseOffering> list = new ArrayList<>();
        String sql = "SELECT * FROM course_offerings";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToOffering(rs));
        }
        return list;
    }

    private CourseOffering mapRowToOffering(ResultSet rs) throws SQLException {
        CourseOffering co = new CourseOffering();
        co.setClassId(rs.getInt("class_id"));
        co.setCourseId(rs.getInt("course_id"));
        co.setTeacherId(rs.getInt("teacher_id"));
        co.setSemester(rs.getInt("semester"));
        co.setMajorId(rs.getInt("major_id"));
        co.setMaxCapacity(rs.getInt("max_capacity"));
        co.setCurrentEnrolled(rs.getInt("current_enrolled"));
        return co;
    }
}