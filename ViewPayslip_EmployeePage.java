import javax.swing.*;

public class ViewPayslip_EmployeePage {
    private JButton backButton;
    private JPanel panel1;
    public ViewPayslip_EmployeePage(JFrame frame, Employee currentEmployee) {
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(frame, currentEmployee).getPanel());
            frame.revalidate();
        });
    }
    public JPanel getPanel() {
        return panel1;
    }
}
