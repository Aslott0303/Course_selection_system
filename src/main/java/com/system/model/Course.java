package com.system.model;

public class Course {
    private int courseId;
    private String courseName;
    private int credits;
    private int majorId;
    private String description;

    public Course() {}

    public Course(String courseName, int credits, int majorId, String description) {
        this.courseName = courseName;
        this.credits = credits;
        this.majorId = majorId;
        this.description = description;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", majorId=" + majorId +
                ", description='" + description + '\'' +
                '}';
    }
}
