import javax.swing.*;
import java.awt.*;

public class ViewDetails {
    private JPanel rootPanel;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JTextField nameTextField;
    private JTextField ageTextField;
    private JTextField salaryTextField;
    private JTextField contactTextField;
    private JTextField departmentTextField;
    private JButton backButton;

    public ViewDetails(JFrame frame, Employee employee) {
        // Fill all fields
        usernameTextField.setText(employee.getUsername());
        passwordTextField.setText(employee.getPassword());
        nameTextField.setText(employee.getName());
        ageTextField.setText(employee.getAge());
        salaryTextField.setText(String.valueOf(employee.getBasicSalary()));
        contactTextField.setText(employee.getContact());
        departmentTextField.setText(employee.getDepartment().getName().toString());

        // Make all fields look like labels
        JTextField[] fields = {
                usernameTextField, passwordTextField, nameTextField,
                ageTextField, salaryTextField, contactTextField, departmentTextField
        };

        for (JTextField field : fields) {
            field.setEditable(false);
//            field.setBorder(null);
            field.setBackground(null);
            field.setForeground(Color.BLACK);
            field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        // Back to employee page
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(frame, employee).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
