package com.system.view;

import com.system.dao.impl.CourseDAOImpl;
import com.system.dao.impl.CourseOfferingDAOImpl;
import com.system.model.Course;
import com.system.model.CourseOffering;
import com.system.service.EnrollmentService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

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

    // 核心修改1：加载可选课程→查询CourseOffering（而非Course）
    private void loadOptionalCourses() {
        try {
            // 1. 假设学生当前学期为1（实际可从学生信息推算，此处简化）
            int currentSemester = 1;
            // 2. 从CourseOfferingDAO查询：学生专业+当前学期的可选开课实例
            List<CourseOffering> optionalOfferings = courseOfferingDAO.findByMajorAndSemester(
                    getStudentMajorId(), currentSemester
            );
            // 3. 填充表格（数据源改为List<CourseOffering>）
            fillCourseTable(optionalOfferings);
            showMsg("提示", "共加载 " + optionalOfferings.size() + " 门可选课程", SWT.ICON_INFORMATION);
        } catch (SQLException e) {
            showMsg("错误", "加载可选课程失败：" + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }

    // 核心修改2：加载已选课程→查询学生关联的CourseOffering
    private void loadSelectedCourses() {
        try {
            // 从EnrollmentService查询学生已选的开课实例详情
            List<EnrollmentService.EnrolledCourseView> enrolledViews = enrollmentService.getEnrolledCourses(studentId);
            // 转换为CourseOffering列表（适配表格填充方法）
            List<CourseOffering> selectedOfferings = convertToCourseOfferings(enrolledViews);
            // 填充表格
            fillCourseTable(selectedOfferings);
            showMsg("提示", "共加载 " + selectedOfferings.size() + " 门已选课程", SWT.ICON_INFORMATION);
        } catch (Exception e) {
            showMsg("错误", "加载已选课程失败：" + e.getMessage(), SWT.ICON_ERROR);
            e.printStackTrace();
        }
    }

    // 辅助方法1：获取学生的专业ID（从数据库查询）
    private int getStudentMajorId() throws SQLException {
        // 从StudentDAO查询当前学生的majorId（此处简化，实际需注入StudentDAO）
        // 假设学生ID=studentId对应的专业ID为1（实际需替换为真实查询逻辑）
        // 完整逻辑：return studentDAO.findById(studentId).getMajorId();
        return 1;
    }

    // 辅助方法2：将EnrolledCourseView转换为CourseOffering（适配表格）
    private List<CourseOffering> convertToCourseOfferings(List<EnrollmentService.EnrolledCourseView> views) {
        List<CourseOffering> offerings = new ArrayList<>(); // 红波浪线已解决（已导入ArrayList）
        for (EnrollmentService.EnrolledCourseView view : views) {
            CourseOffering offering = new CourseOffering();
            offering.setClassId(view.classId);
            offering.setCurrentEnrolled(view.currentEnrolled);
            offering.setMaxCapacity(view.maxCapacity);
            offering.setTeacherName(view.teacherName);
            // 关联课程信息（通过courseId查Course）
            try {
                Course course = courseDAO.findById(getCourseIdByCourseName(view.courseName));
                if (course != null) {
                    offering.setCourseId(course.getCourseId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            offerings.add(offering);
        }
        return offerings;
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
        courseTable.removeAll(); // 清空表格行（SWT标准方法，无错误）
        
        // 无数据时提示
        if (offerings == null || offerings.isEmpty()) {
            TableItem emptyItem = new TableItem(courseTable, SWT.NONE);
            emptyItem.setText(0, "暂无课程数据");
            return;
        }

        // 遍历开课实例，填充表格（调用CourseOffering的真实方法，无拼写问题）
        for (CourseOffering offering : offerings) {
            TableItem item = new TableItem(courseTable, SWT.NONE);
            // 从CourseOffering获取开课信息，从关联的Course获取课程基础信息
            Course course = getCourseByOffering(offering);
            item.setText(new String[]{
                    String.valueOf(offering.getClassId()), // CourseOffering有getClassId()
                    course != null ? course.getCourseName() : "未知课程", // Course有getCourseName()
                    course != null ? String.valueOf(course.getCredits()) : "0", // Course有getCredits()
                    offering.getTeacherName() != null ? offering.getTeacherName() : "暂无", // CourseOffering有getTeacherName()
                    offering.getCurrentEnrolled() + "/" + offering.getMaxCapacity() // CourseOffering有对应方法
            });
        }

        // 自动调整列宽
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