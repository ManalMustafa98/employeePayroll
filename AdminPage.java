import javax.swing.*;

public class AdminPage {
    private JPanel rootPanel;
    private JButton addAdminButton;
    private JButton addEmployeeButton;
    private JButton viewAllEmployeesButton;
    private JButton viewAttendanceButton;
    private JButton viewByDepartmentButton;
    private JButton logoutButton;
    private JButton removeEmployeeButton;
    private JButton removeAdminButton;

    public AdminPage(JFrame frame, Admin admin) {
        addAdminButton.setVisible(admin.isSuperAdmin());
        removeAdminButton.setVisible(admin.isSuperAdmin());

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
            frame.setContentPane(viewAttPanel.getPanel());
            frame.revalidate();
        });

        viewByDepartmentButton.addActionListener(e -> {
            ViewEmployeeByDept_Admin viewByDeptPanel =
                    new ViewEmployeeByDept_Admin(frame, admin);
            frame.setContentPane(viewByDeptPanel.getPanel());
            frame.revalidate();
        });

        removeEmployeeButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(frame, "Enter employee username to remove:");
            if (username != null && !username.trim().isEmpty()) {
                admin.removeEmployee(username.trim());
                JOptionPane.showMessageDialog(frame, "✅ Employee '" + username.trim() + "' has been successfully removed.");
            }
        });

        removeAdminButton.addActionListener(e -> {
            if (!admin.isSuperAdmin()) {
                JOptionPane.showMessageDialog(frame, "❌ Only Super Admin can remove admins.");
                return;
            }

            String username = JOptionPane.showInputDialog(frame, "Enter admin username to remove:");
            if (username != null && !username.trim().isEmpty()) {
                admin.removeAdmin(username.trim());
                JOptionPane.showMessageDialog(frame, "✅ Admin '" + username.trim() + "' has been successfully removed.");
            }
        });

        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });

        frame.getRootPane().setDefaultButton(addEmployeeButton);
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
