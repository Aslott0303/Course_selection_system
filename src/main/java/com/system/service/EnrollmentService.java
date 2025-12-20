package com.system.service;

import com.system.dao.impl.CourseOfferingDAOImpl;
import com.system.dao.impl.EnrollmentDAOImpl;
import com.system.model.CourseOffering;
import com.system.model.Enrollment;
import com.system.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnrollmentService {

    private final CourseOfferingDAOImpl offeringDAO = new CourseOfferingDAOImpl();
    private final EnrollmentDAOImpl enrollmentDAO = new EnrollmentDAOImpl();

    /**
     * 选课事务（已提供）
     * @return 0成功 1已选 2人数已满 3异常
     */
    public int enroll(int studentId, int classId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 检查是否已选
            String checkSql = "SELECT 1 FROM enrollments WHERE student_id=? AND class_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, classId);
                if (pstmt.executeQuery().next()) {
                    conn.rollback();
                    return 1; // 已选
                }
            }

            // 检查容量
            CourseOffering offering = offeringDAO.findById(classId);
            if (offering == null || offering.getCurrentEnrolled() >= offering.getMaxCapacity()) {
                conn.rollback();
                return offering == null ? 3 : 2; // 不存在或已满
            }

            // 插入选课记录
            Enrollment enrollment = new Enrollment(studentId, classId, new Date());
            enrollmentDAO.insert(enrollment);

            // 更新人数
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE course_offerings SET current_enrolled = current_enrolled + 1 WHERE class_id=?")) {
                pstmt.setInt(1, classId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return 0;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return 3;
        } finally {
            DBUtil.close(conn);
        }
    }

    /**
     * 退课事务
     * @param studentId 学生ID
     * @param classId   开课ID
     * @return 0成功 1未选该课 3异常
     */
    public int withdraw(int studentId, int classId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 检查是否已选
            String checkSql = "SELECT 1 FROM enrollments WHERE student_id=? AND class_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, classId);
                if (!pstmt.executeQuery().next()) {
                    conn.rollback();
                    return 1; // 未选该课
                }
            }

            // 删除选课记录
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "DELETE FROM enrollments WHERE student_id=? AND class_id=?")) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, classId);
                pstmt.executeUpdate();
            }

            // 更新当前人数 -1
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE course_offerings SET current_enrolled = current_enrolled - 1 WHERE class_id=? AND current_enrolled > 0")) {
                pstmt.setInt(1, classId);
                pstmt.executeUpdate();
            }

            conn.commit();
            return 0;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return 3;
        } finally {
            DBUtil.close(conn);
        }
    }

    /**
     * 查询学生已选课程（返回详细视图对象，便于界面显示）
     */
    public static class EnrolledCourseView {
        public int classId;
        public String courseName;
        public String teacherName;
        public int semester;
        public int credits;
        public int currentEnrolled;
        public int maxCapacity;
        public Date enrollDate;

        @Override
        public String toString() {
            return courseName + " (" + teacherName + ") 第" + semester + "学期";
        }
    }

    /**
     * 查询某学生已选的所有课程详细信息
     * @param studentId 学生ID
     * @return List<EnrolledCourseView>
     */
    public List<EnrolledCourseView> getEnrolledCourses(int studentId) {
        List<EnrolledCourseView> list = new ArrayList<>();
        String sql = """
            SELECT
                co.class_id,
                c.course_name,
                t.name AS teacher_name,
                co.semester,
                c.credits,
                co.current_enrolled,
                co.max_capacity,
                e.enroll_date
            FROM enrollments e
            JOIN course_offerings co ON e.class_id = co.class_id
            JOIN courses c ON co.course_id = c.course_id
            JOIN teachers t ON co.teacher_id = t.teacher_id
            WHERE e.student_id = ?
            ORDER BY co.semester DESC, c.course_name
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EnrolledCourseView view = new EnrolledCourseView();
                    view.classId = rs.getInt("class_id");
                    view.courseName = rs.getString("course_name");
                    view.teacherName = rs.getString("teacher_name");
                    view.semester = rs.getInt("semester");
                    view.credits = rs.getInt("credits");
                    view.currentEnrolled = rs.getInt("current_enrolled");
                    view.maxCapacity = rs.getInt("max_capacity");
                    view.enrollDate = rs.getDate("enroll_date");
                    list.add(view);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}