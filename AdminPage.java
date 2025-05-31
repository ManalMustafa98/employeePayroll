import javax.swing.*;
import java.awt.*;

public class AdminPage {
    private JPanel              rootPanel;
    private JButton             addAdminButton;
    private JButton             addEmployeeButton;
    private JButton             viewAllEmployeesButton;
    private JButton             viewAttendanceButton;
    private JButton             viewByDepartmentButton;
    private JButton             logoutButton;

    public AdminPage(JFrame frame, Admin admin) {
        // 1️⃣ Show Add-Admin only for super-admin
        addAdminButton.setVisible(admin.isSuperAdmin());

        // 2️⃣ Wire each button to its screen
        addAdminButton.addActionListener(e -> {
            frame.setContentPane(new AddAdmin(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        addEmployeeButton.addActionListener(e -> {
            frame.setContentPane(new AddEmployee(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        viewAllEmployeesButton.addActionListener(e -> {
            frame.setContentPane(new ViewAllEmployees_AdminPage(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        viewAttendanceButton.addActionListener(e -> {
            frame.setContentPane(new ViewAllAttendance_AdminPage(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        viewByDepartmentButton.addActionListener(e -> {
            frame.setContentPane(new ViewByDepartment_AdminPage(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });

        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.pack();
            frame.revalidate();
        });

        // 3️⃣ Optional: set a default button so Enter triggers "Add Employee"
        frame.getRootPane().setDefaultButton(addEmployeeButton);
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
