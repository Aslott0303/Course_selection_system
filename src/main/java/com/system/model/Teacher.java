package com.system.model;

public class Teacher {
    private int teacherId;
    private String name;
    private String gender;   // "男" 或 "女"
    private String title;

    public Teacher() {}

    public Teacher(String name, String gender, String title) {
        this.name = name;
        this.gender = gender;
        this.title = title;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
