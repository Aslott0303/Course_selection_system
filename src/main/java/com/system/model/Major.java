package com.system.model;

public class Major {
    private int majorId;
    private String majorName;
    private String faculty;

    public Major() {}

    public Major(String majorName, String faculty) {
        this.majorName = majorName;
        this.faculty = faculty;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        return "Major{" +
                "majorId=" + majorId +
                ", majorName='" + majorName + '\'' +
                ", faculty='" + faculty + '\'' +
                '}';
    }
}
