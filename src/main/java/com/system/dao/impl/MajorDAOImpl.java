package com.system.dao.impl;

import com.system.dao.BaseDAO;
import com.system.model.Major;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAOImpl implements BaseDAO<Major> {

    @Override
    public int insert(Major major) throws SQLException {
        String sql = "INSERT INTO majors (major_name, faculty) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, major.getMajorName());
            pstmt.setString(2, major.getFaculty());
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
    public void update(Major major) throws SQLException {
        String sql = "UPDATE majors SET major_name=?, faculty=? WHERE major_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, major.getMajorName());
            pstmt.setString(2, major.getFaculty());
            pstmt.setInt(3, major.getMajorId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM majors WHERE major_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Major findById(int id) throws SQLException {
        String sql = "SELECT * FROM majors WHERE major_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapRowToMajor(rs);
            }
        }
        return null;
    }

    @Override
    public List<Major> findAll() throws SQLException {
        List<Major> list = new ArrayList<>();
        String sql = "SELECT * FROM majors";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapRowToMajor(rs));
        }
        return list;
    }

    private Major mapRowToMajor(ResultSet rs) throws SQLException {
        Major m = new Major();
        m.setMajorId(rs.getInt("major_id"));
        m.setMajorName(rs.getString("major_name"));
        m.setFaculty(rs.getString("faculty"));
        return m;
    }
}