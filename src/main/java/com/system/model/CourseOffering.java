package com.system.model;

/**
 * 开课实例实体类（对应数据库 course_offerings 表）
 * 功能：封装具体学期的课程实例信息，关联课程、教师、专业表
 * 注意：classId（自增主键）、currentEnrolled（默认0）勿手动赋值
 */
public class CourseOffering {
    // 主键字段（自增，数据库生成）
    private int classId;

    // 外键字段（关联其他表，需通过DAO获取合法ID）
    private int courseId;    // 关联 Course.courseId
    private int teacherId;   // 关联 Teacher.teacherId
    private int majorId;     // 关联 Major.majorId

    // 业务字段（严格匹配数据库类型与约束，补充teacherName用于界面展示）
    private int semester;          // 学期（1-8，1=大一上）
    private int maxCapacity;       // 最大选课人数（≥0）
    private int currentEnrolled = 0; // 当前选课人数（默认0，仅DAO/Service可修改）
    // 新增：teacherName字段（用于界面直接展示教师姓名，无需每次查Teacher表）

    // 无参构造（DAO层反射必需）
    public CourseOffering() {
    }

    // 有参构造（创建开课实例时使用，排除自增和默认值字段，补充teacherName参数）
    public CourseOffering(int courseId, int teacherId, String teacherName, int semester, int majorId, int maxCapacity) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.semester = semester;
        this.majorId = majorId;
        this.maxCapacity = maxCapacity;
    }

    // Getter 方法（补充teacherName的getter）
    public int getClassId() {
        return classId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getSemester() {
        return semester;
    }

    public int getMajorId() {
        return majorId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getCurrentEnrolled() {
        return currentEnrolled;
    }

    // Setter 方法（补充teacherName的setter，DAO层赋值用）
    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setSemester(int semester) {
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("学期必须为1-8（1=大一上，8=大四下）");
        }
        this.semester = semester;
    }

    public void setMajorId(int majorId) {
        this.majorId = majorId;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("课程容量不能为负数");
        }
        this.maxCapacity = maxCapacity;
    }

    public void setCurrentEnrolled(int currentEnrolled) {
        if (currentEnrolled < 0 || (maxCapacity > 0 && currentEnrolled > maxCapacity)) {
            throw new IllegalArgumentException("当前人数不能为负或超过最大容量");
        }
        this.currentEnrolled = currentEnrolled;
    }

    // 重写toString：补充teacherName打印
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