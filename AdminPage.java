import javax.swing.*;

public class AdminPage {
    private JPanel rootPanel;
    private JButton addAdminButton;
    private JButton addEmployeeButton;
    private JButton viewAllEmployeesButton;
    private JButton viewAttendanceButton;
    private JButton viewByDepartmentButton;
    private JButton logoutButton;

    public AdminPage(JFrame frame, Admin admin) {
        // 1️⃣ Only super‐admin sees the “Add Admin” button
        addAdminButton.setVisible(admin.isSuperAdmin());

        // 2️⃣ Wire “Add Admin” → AddAdmin panel
        addAdminButton.addActionListener(e -> {
            frame.setContentPane(new AddAdmin(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 3️⃣ Wire “Add Employee” → AddEmployee panel
        addEmployeeButton.addActionListener(e -> {
            frame.setContentPane(new AddEmployee(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 4️⃣ Wire “View All Employees” → ViewAllEmployeeAdmin panel
        viewAllEmployeesButton.addActionListener(e -> {
            ViewAllEmployeeAdmin viewAllPanel = new ViewAllEmployeeAdmin(frame, admin);
            frame.setContentPane(viewAllPanel.getRootPanel());
            frame.pack();
            frame.revalidate();
        });

        // 5️⃣ Wire “View Attendance” → ViewAttendanceForAllEmployee_Admin panel
        viewAttendanceButton.addActionListener(e -> {
            ViewAttendanceForAllEmployee_Admin viewAttPanel =
                    new ViewAttendanceForAllEmployee_Admin(frame, admin);
            // ▶ Corrected: call instance method, not the class
            frame.setContentPane(viewAttPanel.getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 6️⃣ Wire “View by Department” → ViewEmployeeByDept_Admin panel
        viewByDepartmentButton.addActionListener(e -> {
            ViewEmployeeByDept_Admin viewByDeptPanel =
                    new ViewEmployeeByDept_Admin(frame, admin);
            frame.setContentPane(viewByDeptPanel.getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 7️⃣ Wire “Logout” → WelcomePage panel
        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 8️⃣ Optional: Pressing Enter triggers “Add Employee” by default
        frame.getRootPane().setDefaultButton(addEmployeeButton);
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
