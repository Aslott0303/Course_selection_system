package com.system.view;

// 移除Swing包，换成SWT的包
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

// 保留你需要的DAO和Model引用（成员A补充后可用）
import com.system.dao.impl.StudentDAOImpl;
import com.system.model.Student;

public class LoginWindow {
	// 把Swing的JFrame换成SWT的Shell
	private Shell frame;
	private Text usernameText;
	private Text nameText; // 改为姓名输入框（原passwordText）
	private Label tipLabel;

	/**
	 * Launch the application.（保留你的main方法结构，改成SWT启动）
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		LoginWindow window = new LoginWindow();
		window.frame.open();
		// SWT的事件循环
		while (!window.frame.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Create the application.
	 */
	public LoginWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.（改成SWT控件+登录逻辑）
	 */
	private void initialize() {
		// 初始化SWT的Shell（替代JFrame）
		frame = new Shell();
		frame.setSize(450, 300);
		frame.setText("选课系统登录");
		frame.setLayout(new GridLayout(2, false)); // 2列布局

		// 1. 用户名标签（改为“学号”，更直观）
		Label usernameLabel = new Label(frame, SWT.NONE);
		usernameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		usernameLabel.setText("学号");

		// 2. 用户名输入框（保留，实际输入学号）
		usernameText = new Text(frame, SWT.BORDER);
		usernameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// 3. 密码标签 → 改为“姓名”标签（核心修改1）
		Label nameLabel = new Label(frame, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		nameLabel.setText("姓名");

		// 4. 密码输入框 → 改为姓名输入框（去掉SWT.PASSWORD，核心修改2）
		nameText = new Text(frame, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// 5. 学生单选按钮（默认选中）
		Button studentRadio = new Button(frame, SWT.RADIO);
		studentRadio.setText("学生");
		studentRadio.setSelection(true);

		// 6. 管理员单选按钮
		Button adminRadio = new Button(frame, SWT.RADIO);
		adminRadio.setText("管理员");

		// 7. 登录按钮
		Button loginButton = new Button(frame, SWT.NONE);
		loginButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		loginButton.setText("登录");

		// 8. 注册按钮
		Button registerButton = new Button(frame, SWT.NONE);
		registerButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		registerButton.setText("注册");

		// 9. 错误提示标签（红色）
		tipLabel = new Label(frame, SWT.NONE);
		tipLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		tipLabel.setForeground(frame.getDisplay().getSystemColor(SWT.COLOR_RED));

		// ========== 登录按钮点击事件 ==========
		loginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获取输入内容（密码 → 姓名，核心修改3）
				String inputNum = usernameText.getText().trim();
				String inputName = nameText.getText().trim();

				// 空值验证（密码 → 姓名，核心修改4）
				if (inputNum.isEmpty() || inputName.isEmpty()) {
					tipLabel.setText("学号/姓名不能为空！");
					return;
				}

				// 管理员登录逻辑（完全保留，不变）
				if (adminRadio.getSelection()) {
					if (inputNum.equals("admin") && inputName.equals("admin123")) {
						MessageBox successMsg = new MessageBox(frame, SWT.ICON_INFORMATION);
						successMsg.setText("登录成功");
						successMsg.setMessage("管理员登录成功！");
						successMsg.open();
						frame.close(); // 关闭登录窗口
					} else {
						tipLabel.setText("管理员账号/密码错误！");
					}
					return;
				}

				// 学生登录逻辑（核心修改5：替换密码验证为姓名验证）
				try {
					StudentDAOImpl studentDAO = new StudentDAOImpl();
					Student student = studentDAO.findByStudentNumber(inputNum);

					if (student == null) {
						tipLabel.setText("该学号对应的学生不存在！");
					} else if (inputName.equals(student.getName())) { // 验证姓名匹配
						// 登录成功（修正getStudentName()为getName()，避免字段名错误）
						MessageBox successMsg = new MessageBox(frame, SWT.ICON_INFORMATION);
						successMsg.setText("登录成功");
						successMsg.setMessage("欢迎你，" + student.getName() + "！");
						successMsg.open();
						frame.close();
						// 打开选课窗口（已启用）
						new StudentCourseWindow(student.getStudentId()).open();
					} else {
						tipLabel.setText("姓名与学号不匹配！"); // 错误提示修改
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					tipLabel.setText("登录失败，请重试！");
				}
			}
		});

		// ========== 注册按钮点击事件 ==========
		registerButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 启用注册窗口（仅去掉注释，无其他修改）
				new RegisterWindow().open();
			}
		});
	}
}