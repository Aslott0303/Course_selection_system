package com.system.model;

import java.time.Year;

public class Student {
    private int studentId;
    private String name;
    private String gender;          // "男" 或 "女"
    private int majorId;
    private Year enrollmentYear;    // 如 2024
    private String studentNumber;   // 学号

    public Student() {}

    public Student(String name, String gender, int majorId, Year enrollmentYear, String studentNumber) {
        this.name = name;
        this.gender = gender;
        this.majorId = majorId;
        this.enrollmentYear = enrollmentYear;
        this.studentNumber = studentNumber;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public Year getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Year enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", majorId=" + majorId +
                ", enrollmentYear=" + enrollmentYear +
                ", studentNumber='" + studentNumber + '\'' +
                '}';
    }
}