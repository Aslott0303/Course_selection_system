package com.system.view;

import com.system.dao.impl.StudentDAOImpl;
import com.system.model.Student;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.time.Year;

public class RegisterWindow {
    private Shell shell;
    private Text studentNumberText;
    private Text nameText;
    private Combo genderCombo;
    private Combo majorCombo;
    private Text enrollmentYearText;
    private Label tipLabel;
    private StudentDAOImpl studentDAO = new StudentDAOImpl();

    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createContents() {
        shell = new Shell(SWT.SHELL_TRIM);
        shell.setSize(400, 350);
        shell.setText("学生注册");
        shell.setLayout(new GridLayout(2, false));

        // 学号标签+输入框
        Label snLabel = new Label(shell, SWT.NONE);
        snLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        snLabel.setText("学号*");
        studentNumberText = new Text(shell, SWT.BORDER);
        studentNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        studentNumberText.setMessage("请输入6位以上学号");

        // 姓名标签+输入框
        Label nameLabel = new Label(shell, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        nameLabel.setText("姓名*");
        nameText = new Text(shell, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameText.setMessage("请输入真实姓名");

        // 性别标签+下拉框
        Label genderLabel = new Label(shell, SWT.NONE);
        genderLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        genderLabel.setText("性别*");
        genderCombo = new Combo(shell, SWT.READ_ONLY);
        genderCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        genderCombo.setItems("男", "女");
        genderCombo.select(0);

        // 专业标签+下拉框
        Label majorLabel = new Label(shell, SWT.NONE);
        majorLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        majorLabel.setText("专业*");
        majorCombo = new Combo(shell, SWT.READ_ONLY);
        majorCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        majorCombo.setItems("计算机科学与技术");
        majorCombo.select(0);

        // 入学年份标签+输入框
        Label yearLabel = new Label(shell, SWT.NONE);
        yearLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        yearLabel.setText("入学年份*");
        enrollmentYearText = new Text(shell, SWT.BORDER);
        enrollmentYearText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        enrollmentYearText.setMessage("如：2024");

        // 提示标签
        tipLabel = new Label(shell, SWT.NONE);
        tipLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        tipLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));

        // 注册按钮
        Button regBtn = new Button(shell, SWT.NONE);
        regBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        regBtn.setText("提交注册");

        // 注册按钮事件
        regBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doRegister();
            }
        });
    }

    private void doRegister() {
        String sn = studentNumberText.getText().trim();
        String name = nameText.getText().trim();
        String gender = genderCombo.getText();
        int majorId = 1;
        String yearStr = enrollmentYearText.getText().trim();

        // 表单验证
        if (sn.isEmpty() || name.isEmpty() || yearStr.isEmpty()) {
            tipLabel.setText("带*的字段不能为空！");
            return;
        }
        if (sn.length() < 6) {
            tipLabel.setText("学号需6位以上！");
            return;
        }
        if (!yearStr.matches("\\d{4}")) {
            tipLabel.setText("入学年份需为4位数字！");
            return;
        }

        // 校验学号是否已存在
        try {
            if (studentDAO.isStudentNumberExists(sn)) {
                tipLabel.setText("该学号已注册！");
                return;
            }
        } catch (Exception ex) {
            tipLabel.setText("查询失败，请重试！");
            return;
        }

        // 封装学生对象
        Student student = new Student();
        student.setStudentNumber(sn);
        student.setName(name);
        student.setGender(gender);
        student.setMajorId(majorId);
        student.setEnrollmentYear(Year.of(Integer.parseInt(yearStr)));

        // 插入数据库
        try {
            int studentId = studentDAO.insert(student);
            if (studentId > 0) {
                MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION);
                msg.setText("注册成功");
                msg.setMessage("注册成功！学号：" + sn);
                msg.open();
                shell.close();
            } else {
                tipLabel.setText("注册失败！");
            }
        } catch (Exception ex) {
            tipLabel.setText("注册异常：" + ex.getMessage());
        }
    }
}