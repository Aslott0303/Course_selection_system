-- 创建数据库
CREATE DATABASE IF NOT EXISTS course_selection_system DEFAULT CHARACTER SET utf8mb4;

USE course_selection_system;

-- 1. 学生表
CREATE TABLE students (
                          student_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          gender ENUM('男', '女') NOT NULL,
                          major_id INT NOT NULL,
                          enrollment_year YEAR NOT NULL,
                          student_number VARCHAR(20) UNIQUE NOT NULL,
                          FOREIGN KEY (major_id) REFERENCES majors(major_id)
);

-- 2. 专业表
CREATE TABLE majors (
                        major_id INT AUTO_INCREMENT PRIMARY KEY,
                        major_name VARCHAR(100) NOT NULL,
                        faculty VARCHAR(100) NOT NULL
);

-- 3. 教师表
CREATE TABLE teachers (
                          teacher_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          gender ENUM('男', '女') NOT NULL,
                          title VARCHAR(50) NOT NULL
);

-- 4. 课程表
CREATE TABLE courses (
                         course_id INT AUTO_INCREMENT PRIMARY KEY,
                         course_name VARCHAR(100) NOT NULL,
                         credits INT NOT NULL,
                         major_id INT NOT NULL,
                         description TEXT,
                         FOREIGN KEY (major_id) REFERENCES majors(major_id)
);

-- 5. 开课表
CREATE TABLE course_offerings (
                                  class_id INT AUTO_INCREMENT PRIMARY KEY,
                                  course_id INT NOT NULL,
                                  teacher_id INT NOT NULL,
                                  semester INT NOT NULL CHECK (semester BETWEEN 1 AND 8),
                                  major_id INT NOT NULL,
                                  max_capacity INT NOT NULL,
                                  current_enrolled INT DEFAULT 0,
                                  FOREIGN KEY (course_id) REFERENCES courses(course_id),
                                  FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id),
                                  FOREIGN KEY (major_id) REFERENCES majors(major_id)
);

-- 6. 选课表
CREATE TABLE enrollments (
                             enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                             student_id INT NOT NULL,
                             class_id INT NOT NULL,
                             enroll_date DATE NOT NULL,
                             FOREIGN KEY (student_id) REFERENCES students(student_id),
                             FOREIGN KEY (class_id) REFERENCES course_offerings(class_id),
                             UNIQUE KEY unique_enrollment (student_id, class_id)
);

-- 插入测试数据
INSERT INTO majors (major_name, faculty) VALUES ('计算机科学', '信息工程学院'), ('电子工程', '电子学院');

INSERT INTO students (name, gender, major_id, enrollment_year, student_number)
VALUES ('张三', '男', 1, 2024, '2024001'), ('李四', '女', 2, 2024, '2024002');

INSERT INTO teachers (name, gender, title) VALUES ('王五', '男', '教授'), ('赵六', '女', '副教授');

INSERT INTO courses (course_name, credits, major_id, description)
VALUES ('Java编程', 4, 1, 'Java基础课程'), ('电路设计', 3, 2, '电子电路基础');

INSERT INTO course_offerings (course_id, teacher_id, semester, major_id, max_capacity)
VALUES (1, 1, 1, 1, 50), (2, 2, 1, 2, 40);

INSERT INTO enrollments (student_id, class_id, enroll_date) VALUES (1, 1, CURDATE());
UPDATE course_offerings SET current_enrolled = current_enrolled + 1 WHERE class_id = 1;