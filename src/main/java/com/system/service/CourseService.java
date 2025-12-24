package com.system.service;

import com.system.dao.impl.CourseDAOImpl;
import com.system.model.Course;
import java.sql.SQLException;
import java.util.List;

public class CourseService {

    // 实例化你截图里的那个 DAO
    private CourseDAOImpl courseDAO = new CourseDAOImpl();

    // 1. 业务：添加课程
    // 参数对应你截图里的字段：name, credits, majorId, description
    public void addCourse(String name, int credits, int majorId, String description) {
        Course c = new Course();
        // 注意：根据你的 Course 类，Setter 名字可能叫 setCourseName 或 setName，请根据实际调整
        c.setCourseName(name);
        c.setCredits(credits);
        c.setMajorId(majorId);
        c.setDescription(description);

        try {
            // 调用你截图里的 insert 方法
            int result = courseDAO.insert(c);
            if (result > 0) {
                System.out.println(" [成功] 课程添加成功！");
            } else {
                System.out.println(" [失败] 添加失败。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" [错误] 数据库连接异常：" + e.getMessage());
        }
    }

    // 2. 业务：删除课程
    public void removeCourse(int courseId) {
        try {
            // 先检查课程存不存在 (调用你截图里的 findById)
            Course exist = courseDAO.findById(courseId);
            if (exist == null) {
                System.out.println(" [失败] 删除失败，找不到 ID 为 " + courseId + " 的课程。");
                return;
            }

            // 调用你截图里的 deleteById
            courseDAO.deleteById(courseId);
            System.out.println(" [成功] 课程已删除。");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. 业务：修改课程
    public void modifyCourse(int courseId, String newName, int newCredits, int newMajorId, String newDesc) {
        try {
            // 1. 先把旧课程查出来
            Course c = courseDAO.findById(courseId);
            if (c == null) {
                System.out.println(" [失败] 找不到 ID 为 " + courseId + " 的课程。");
                return;
            }

            // 2. 修改它的属性
            c.setCourseName(newName);
            c.setCredits(newCredits);
            c.setMajorId(newMajorId);
            c.setDescription(newDesc);

            // 3. 调用你截图里的 update 方法保存回去
            courseDAO.update(c);
            System.out.println(" [成功] 课程修改成功！");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. 业务：查询所有
    public void showAllCourses() {
        try {
            // 调用你截图里的 findAll
            List<Course> list = courseDAO.findAll();
            System.out.println("\n======  课程列表 ======");
            for (Course c : list) {
                // 这里调用 getter 方法，根据你 Course.java 里的名字调整
                System.out.println("ID: " + c.getCourseId() +
                        " | 名称: " + c.getCourseName() +
                        " | 学分: " + c.getCredits() +
                        " | 专业ID: " + c.getMajorId());
            }
            System.out.println("========================\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}