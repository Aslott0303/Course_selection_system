package com.system.view;

import com.system.dao.impl.CourseDAOImpl;
import com.system.dao.impl.CourseOfferingDAOImpl;
import com.system.model.Course;
import com.system.model.CourseOffering;
import com.system.service.EnrollmentService;
import com.system.dao.impl.TeacherDAOImpl;
import com.system.model.Teacher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import java.sql.SQLException;
import java.util.ArrayList; // 新增：导入ArrayList类，解决红波浪线
import java.util.List;

public class StudentCourseWindow {
    private Shell shell;
    private int studentId;
    private Table courseTable;
    // 新增：CourseOfferingDAO（查询开课实例）+ CourseDAO（通过courseId查课程基础信息）
    private final CourseOfferingDAOImpl courseOfferingDAO = new CourseOfferingDAOImpl();
    private final CourseDAOImpl courseDAO = new CourseDAOImpl();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    public StudentCourseWindow(int studentId) {
        this.studentId = studentId;
        initialize();
    }

    private int calculateCurrentSemester(int enrollmentYear) {
        LocalDate now = LocalDate.now();  // 当前日期
        int currentYear = now.getYear();
        Month currentMonth = now.getMonth();

        // 判断当前属于哪个学年
        int academicYear;
        boolean isFirstSemester;  // true: 上学期（奇数），false: 下学期（偶数）

        if (currentMonth.getValue() >= 9) {
            // 9~12月 → 本学年上学期
            academicYear = currentYear;
            isFirstSemester = true;
        } else if (currentMonth.getValue() <= 2) {
            // 1~2月 → 上学年上学期（但属于当前学年）
            academicYear = currentYear;
            isFirstSemester = true;
        } else {
            // 3~8月 → 本学年下学期
            academicYear = currentYear;
            isFirstSemester = false;
        }

        // 计算已过学年数
        int yearsPassed = academicYear - enrollmentYear;

        // 当前学期 = 已过完整学年×2 + 本学年是否上学期
        int semester = yearsPassed * 2 + (isFirstSemester ? 1 : 2);

        // 限制在1-8学期
        if (semester < 1) semester = 1;
        if (semester > 8) semester = 8;

        return semester;
    }

    public void open() {
        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void initialize() {
        shell = new Shell(SWT.SHELL_TRIM);
        shell.setSize(700, 500);
        shell.setText("学生选课中心");
        shell.setLayout(new GridLayout(2, false));

        // 欢迎标签
        Label welcomeLabel = new Label(shell, SWT.NONE);
        welcomeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        welcomeLabel.setText("欢迎学生（ID：" + studentId + "）进入选课系统");

        // 功能按钮（逻辑不变，仅修改后续调用的DAO方法）
        Button optionalCourseBtn = new Button(shell, SWT.NONE);
        optionalCourseBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        optionalCourseBtn.setText("查看可选课程");
        optionalCourseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadOptionalCourses();
            }
        });

        Button selectedCourseBtn = new Button(shell, SWT.NONE);
        selectedCourseBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        selectedCourseBtn.setText("查看已选课程");
        selectedCourseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadSelectedCourses();
            }
        });

        Button enrollCourseBtn = new Button(shell, SWT.NONE);
        enrollCourseBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        enrollCourseBtn.setText("选课");
        enrollCourseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doEnrollCourse();
            }
        });

        Button dropCourseBtn = new Button(shell, SWT.NONE);
        dropCourseBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        dropCourseBtn.setText("退选课程");
        dropCourseBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doDropCourse();
            }
        });

        // 表格配置（列名不变，数据源改为CourseOffering）
        courseTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
        courseTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        courseTable.setHeaderVisible(true);
        courseTable.setLinesVisible(true);

        String[] columnNames = {"开课ID", "课程名称", "学分", "授课教师", "当前人数/容量"};
        for (String colName : columnNames) {
            TableColumn column = new TableColumn(courseTable, SWT.CENTER);
            column.setText(colName);
            column.setWidth(140);
        }
    }

    // 1. 修改 loadOptionalCourses() 中的学期获取（简化：先固定测试，或后续从学生年级推算）
    private void loadOptionalCourses() {
        clearTableCompletely();  // 替换原 courseTable.removeAll();
        try {
            // 1. 获取学生真实专业ID
            com.system.dao.impl.StudentDAOImpl studentDAO = new com.system.dao.impl.StudentDAOImpl();
            com.system.model.Student student = studentDAO.findById(studentId);
            if (student == null) {
                showMsg("错误", "无法获取学生信息", SWT.ICON_ERROR);
                return;
            }
            int majorId = student.getMajorId();
            int enrollmentYear = student.getEnrollmentYear().getValue();

            // 2. 计算当前学期
            int currentSemester = calculateCurrentSemester(enrollmentYear);

            // 3. 查询该专业 + 当前学期的开课
            com.system.dao.impl.CourseOfferingDAOImpl offeringDAO = new com.system.dao.impl.CourseOfferingDAOImpl();
            List<CourseOffering> optionalOfferings = offeringDAO.findByMajorAndSemester(majorId, currentSemester);

            // 4. 填充表格（使用你已修复的版本）
            fillCourseTable(optionalOfferings);

            showMsg("提示",
                    String.format("当前第 %d 学期，共加载 %d 门可选课程", currentSemester, optionalOfferings.size()),
                    SWT.ICON_INFORMATION);

        } catch (SQLException e) {
            showMsg("错误", "加载可选课程失败：" + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }

    // 2. 新方法：真实从数据库获取学生专业ID
    private int getRealStudentMajorId() {
        try {
            // 需注入或new StudentDAOImpl
            com.system.dao.impl.StudentDAOImpl studentDAO = new com.system.dao.impl.StudentDAOImpl();
            com.system.model.Student student = studentDAO.findById(studentId);
            if (student != null) {
                return student.getMajorId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // 兜底
    }

    // 新增辅助方法（放在类中任意位置）
    private void clearTableCompletely() {
        // 先 dispose 所有 item（彻底释放）
        for (TableItem item : courseTable.getItems()) {
            item.dispose();
        }
        courseTable.removeAll();  // 再清空
    }

    // 核心修改2：加载已选课程→查询学生关联的CourseOffering
    private void loadSelectedCourses() {
        clearTableCompletely();  // 替换原 courseTable.removeAll();
        try {
            // 获取该学生已选的所有开课实例（需EnrollmentService提供方法返回List<CourseOffering>）
            List<CourseOffering> selectedOfferings = enrollmentService.getSelectedCourseOfferings(studentId);

            fillCourseTable(selectedOfferings);  // 复用已正确实现的方法

        } catch (SQLException e) {
            e.printStackTrace();
            showMsg("错误", "加载已选课程失败！", SWT.ICON_ERROR);
        }
    }

    // 辅助方法1：获取学生的专业ID（从数据库查询）
    private int getStudentMajorId() throws SQLException {
        // 从StudentDAO查询当前学生的majorId（此处简化，实际需注入StudentDAO）
        // 假设学生ID=studentId对应的专业ID为1（实际需替换为真实查询逻辑）
        // 完整逻辑：return studentDAO.findById(studentId).getMajorId();
        return 1;
    }


    // 辅助方法3：通过课程名称查课程ID（简化逻辑，实际应通过view的courseId直接关联）
    private int getCourseIdByCourseName(String courseName) throws SQLException {
        // 实际逻辑：return courseDAO.findByName(courseName).getCourseId();
        // 此处简化，假设“Java编程”对应courseId=1，“数据库原理”对应courseId=2
        return courseName.contains("Java") ? 1 : 2;
    }

    // 选课逻辑（不变，依赖EnrollmentService）
    private void doEnrollCourse() {
        TableItem[] selectedItems = courseTable.getSelection();
        if (selectedItems.length == 0) {
            showMsg("提示", "请先选中要选的课程！", SWT.ICON_INFORMATION);
            return;
        }

        try {
            int classId = Integer.parseInt(selectedItems[0].getText(0));
            int result = enrollmentService.enroll(studentId, classId);
            switch (result) {
                case 0:
                    showMsg("成功", "选课成功！", SWT.ICON_INFORMATION);
                    loadOptionalCourses();
                    break;
                case 1:
                    showMsg("提示", "您已选过该课程，无需重复选择！", SWT.ICON_WARNING);
                    break;
                case 2:
                    showMsg("提示", "该课程已满员，无法选课！", SWT.ICON_WARNING);
                    break;
                case 3:
                    showMsg("错误", "选课失败，请重试！", SWT.ICON_ERROR);
                    break;
            }
        } catch (NumberFormatException e) {
            showMsg("错误", "开课ID格式错误！", SWT.ICON_ERROR);
        }
    }

    // 退课逻辑（不变，依赖EnrollmentService）
    private void doDropCourse() {
        TableItem[] selectedItems = courseTable.getSelection();
        if (selectedItems.length == 0) {
            showMsg("提示", "请先选中要退的课程！", SWT.ICON_INFORMATION);
            return;
        }

        try {
            int classId = Integer.parseInt(selectedItems[0].getText(0));
            int result = enrollmentService.withdraw(studentId, classId);
            switch (result) {
                case 0:
                    showMsg("成功", "退课成功！", SWT.ICON_INFORMATION);
                    loadSelectedCourses();
                    break;
                case 1:
                    showMsg("提示", "您未选该课程，无需退课！", SWT.ICON_WARNING);
                    break;
                case 3:
                    showMsg("错误", "退课失败，请重试！", SWT.ICON_ERROR);
                    break;
            }
        } catch (NumberFormatException e) {
            showMsg("错误", "开课ID格式错误！", SWT.ICON_ERROR);
        }
    }

    // 核心修改3：表格填充→数据源改为List<CourseOffering>（彻底解决红波浪线）
    private void fillCourseTable(List<CourseOffering> offerings) {
        clearTableCompletely();  // 替换原 courseTable.removeAll();

        if (offerings == null || offerings.isEmpty()) {
            showMsg("提示", "暂无已选课程", SWT.ICON_INFORMATION);
            return;
        }

        TeacherDAOImpl teacherDAO = new TeacherDAOImpl();
        CourseDAOImpl courseDAO = new CourseDAOImpl();

        for (CourseOffering offering : offerings) {
            TableItem item = new TableItem(courseTable, SWT.NONE);

            Course course = null;
            String teacherName = "暂无";
            String teacherTitle = "";
            try {
                course = courseDAO.findById(offering.getCourseId());
                Teacher teacher = teacherDAO.findById(offering.getTeacherId());
                if (teacher != null) {
                    teacherName = teacher.getName() != null ? teacher.getName() : "暂无";
                    teacherTitle = teacher.getTitle() != null ? teacher.getTitle() : "";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String teacherDisplay = teacherName;
            if (!teacherTitle.isEmpty()) {
                teacherDisplay += " (" + teacherTitle + ")";
            }

            item.setText(new String[]{
                    String.valueOf(offering.getClassId()),
                    course != null ? course.getCourseName() : "未知课程",
                    course != null ? String.valueOf(course.getCredits()) : "0",
                    teacherDisplay,
                    offering.getCurrentEnrolled() + "/" + offering.getMaxCapacity()
            });
        }

        for (TableColumn column : courseTable.getColumns()) {
            column.pack();
        }
    }

    // 辅助方法4：通过CourseOffering获取关联的Course（解决课程信息来源）
    private Course getCourseByOffering(CourseOffering offering) {
        try {
            return courseDAO.findById(offering.getCourseId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 弹窗提示（不变）
    private void showMsg(String title, String content, int iconType) {
        MessageBox msgBox = new MessageBox(shell, iconType);
        msgBox.setText(title);
        msgBox.setMessage(content);
        msgBox.open();
    }
}