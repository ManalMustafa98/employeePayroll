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
        addAdminButton.setVisible(admin.isSuperAdmin());

        addAdminButton.addActionListener(e -> {
            frame.setContentPane(new AddAdmin(frame, admin).getPanel());
            frame.revalidate();
        });

        addEmployeeButton.addActionListener(e -> {
            frame.setContentPane(new AddEmployee(frame, admin).getPanel());
            frame.revalidate();
        });

        viewAllEmployeesButton.addActionListener(e -> {
            ViewAllEmployeeAdmin viewAllPanel = new ViewAllEmployeeAdmin(frame, admin);
            frame.setContentPane(viewAllPanel.getRootPanel());
            frame.revalidate();
        });

        viewAttendanceButton.addActionListener(e -> {
            ViewAttendanceForAllEmployee_Admin viewAttPanel =
                    new ViewAttendanceForAllEmployee_Admin(frame, admin);
            // ▶ Corrected: call instance method, not the class
            frame.setContentPane(viewAttPanel.getPanel());
            frame.revalidate();
        });

        viewByDepartmentButton.addActionListener(e -> {
            ViewEmployeeByDept_Admin viewByDeptPanel =
                    new ViewEmployeeByDept_Admin(frame, admin);
            frame.setContentPane(viewByDeptPanel.getPanel());
            frame.revalidate();
        });


        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });

        // 8️⃣ Optional: Pressing Enter triggers “Add Employee” by default
        frame.getRootPane().setDefaultButton(addEmployeeButton);
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}