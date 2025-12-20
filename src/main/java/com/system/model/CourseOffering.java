package com.system.model;

public class CourseOffering {
    private int classId;
    private int courseId;
    private int teacherId;
    private int semester;           // 1-8
    private int majorId;
    private int maxCapacity;
    private int currentEnrolled;    // 默认0

    public CourseOffering() {}

    public CourseOffering(int courseId, int teacherId, int semester, int majorId, int maxCapacity) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.semester = semester;
        this.majorId = majorId;
        this.maxCapacity = maxCapacity;
        this.currentEnrolled = 0;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getMajorId() {
        return majorId;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentEnrolled() {
        return currentEnrolled;
    }

    public void setCurrentEnrolled(int currentEnrolled) {
        this.currentEnrolled = currentEnrolled;
    }

    @Override
    public String toString() {
        return "CourseOffering{" +
                "classId=" + classId +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", semester=" + semester +
                ", majorId=" + majorId +
                ", maxCapacity=" + maxCapacity +
                ", currentEnrolled=" + currentEnrolled +
                '}';
    }
}
