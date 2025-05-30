import javax.swing.*;
import java.io.IOException;

public class AddEmployee {
    private JPanel rootPanel;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField departmentField;
    private JTextField salaryField;
    private JTextField ageTextField;
    private JButton ADDButton;
    private JButton backButton;

    public AddEmployee(JFrame frame, Admin currentAdmin) {
        ADDButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String deptName = departmentField.getText().trim().toUpperCase();
            String age = ageTextField.getText().trim();
            String salaryText = salaryField.getText().trim();

            try {
                if (!Admin.isValidName(name) || !Admin.isValidContact(contact)) {
                    JOptionPane.showMessageDialog(rootPanel, "Invalid name or contact format.");
                    return;
                }

                Department department = new Department(DepartmentName.valueOf(deptName));
                double salary = Double.parseDouble(salaryText);

                Employee emp = new Employee(name, contact, department);
                emp.setAge(age);
                emp.setBasicSalary(salary);
                emp.saveToFiles();

                JOptionPane.showMessageDialog(rootPanel, "✅ Employee Created!\nUsername: " +
                        emp.getUsername() + "\nPassword: " + emp.getPassword());

                // ✅ Clear fields after success
                nameField.setText("");
                contactField.setText("");
                departmentField.setText("");
                ageTextField.setText("");
                salaryField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "❌ Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
