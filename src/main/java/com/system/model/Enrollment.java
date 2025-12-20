package com.system.model;

import java.util.Date;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int classId;
    private Date enrollDate;

    public Enrollment() {}

    public Enrollment(int studentId, int classId, Date enrollDate) {
        this.studentId = studentId;
        this.classId = classId;
        this.enrollDate = enrollDate;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", classId=" + classId +
                ", enrollDate=" + enrollDate +
                '}';
    }
}
