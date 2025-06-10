import javax.swing.*;

public class AddEmployee {
    private JPanel rootPanel;
    private JTextField nameField;
    private JTextField ageTextField;
    private JTextField contactField;
    private JComboBox<DepartmentName> departmentField;
    private JTextField salaryField;
    private JButton ADDButton;
    private JButton backButton;

    public AddEmployee(JFrame frame, Admin currentAdmin) {
        ADDButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String age = ageTextField.getText().trim();
            String salaryText = salaryField.getText().trim();

            try {
                if (!Admin.isValidName(name) ||
                        !Admin.isValidContact(contact) ||
                        !Admin.isValidAge(Integer.parseInt(age))) {
                    JOptionPane.showMessageDialog(rootPanel, "Invalid name, contact, or age.");
                    return;
                }

                DepartmentName deptEnum = (DepartmentName) departmentField.getSelectedItem();
                Department department = Department.getInstance(deptEnum);

                double salary = Double.parseDouble(salaryText);

                Employee emp = new Employee(name, contact, department);
                emp.setAge(age);  // Age is stored as String
                emp.setBasicSalary(salary);
                emp.saveToFiles();

                JOptionPane.showMessageDialog(rootPanel,
                        "✅ Employee Created!\nUsername: " + emp.getUsername() +
                                "\nPassword: " + emp.getPassword());
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(rootPanel, "❌ Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    private void clearFields() {
        nameField.setText("");
        ageTextField.setText("");
        contactField.setText("");
        salaryField.setText("");
        departmentField.setSelectedIndex(0);
        ADDButton.setEnabled(false);
        nameField.requestFocusInWindow();
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
