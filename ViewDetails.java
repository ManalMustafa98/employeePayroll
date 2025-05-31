import javax.swing.*;

public class ViewDetails {
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JTextField nameTextField;
    private JTextField ageTextField;
    private JTextField salaryTextField;
    private JTextField contactTextField;
    private JTextField departmentTextField;
    private JButton backButton;
    private JPanel panel1;
    public ViewDetails(JFrame frame, Employee currentEmployee) {
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(frame, currentEmployee).getPanel());
            frame.revalidate();
        });
    }
    public JPanel getPanel() {
        return panel1;
    }


}
