package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.Student;
import com.system.util.DBUtil;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements BaseDAO<Student> {

    // 新增：根据学号查询学生（适配登录逻辑）
    public Student findByStudentNumber(String studentNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs); // 复用已有的结果集转换方法
                }
            }
        }
        return null;
    }

    // 新增：校验学号是否已存在（注册用）
    public boolean isStudentNumberExists(String studentNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE student_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, gender, major_id, enrollment_year, student_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getGender());
            pstmt.setInt(3, student.getMajorId());
            pstmt.setInt(4, student.getEnrollmentYear().getValue());
            pstmt.setString(5, student.getStudentNumber());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // 返回自增 student_id
                    }
                }
            }
            return rows;
        }
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET name=?, gender=?, major_id=?, enrollment_year=?, student_number=? WHERE student_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getGender());
            pstmt.setInt(3, student.getMajorId());
            pstmt.setInt(4, student.getEnrollmentYear().getValue());
            pstmt.setString(5, student.getStudentNumber());
            pstmt.setInt(6, student.getStudentId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Student findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        }
        return list;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setGender(rs.getString("gender"));
        s.setMajorId(rs.getInt("major_id"));
        s.setEnrollmentYear(Year.of(rs.getInt("enrollment_year")));
        s.setStudentNumber(rs.getString("student_number"));
        return s;
    }
}