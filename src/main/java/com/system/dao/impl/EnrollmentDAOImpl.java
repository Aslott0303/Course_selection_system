package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.Enrollment;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements BaseDAO<Enrollment> {

    @Override
    public int insert(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, class_id, enroll_date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getClassId());
            pstmt.setDate(3, new Date(enrollment.getEnrollDate().getTime()));
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
    public void update(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET student_id=?, class_id=?, enroll_date=? WHERE enrollment_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getClassId());
            pstmt.setDate(3, new Date(enrollment.getEnrollDate().getTime()));
            pstmt.setInt(4, enrollment.getEnrollmentId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Enrollment findById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToEnrollment(rs);
            }
        }
        return null;
    }

    @Override
    public List<Enrollment> findAll() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToEnrollment(rs));
        }
        return list;
    }

    private Enrollment mapRowToEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setClassId(rs.getInt("class_id"));
        e.setEnrollDate(rs.getDate("enroll_date"));
        return e;
    }
}