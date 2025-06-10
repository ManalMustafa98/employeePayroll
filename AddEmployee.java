import javax.swing.*;

public class AddEmployee {
    private JPanel rootPanel;
    private JTextField nameField;
    private JTextField ageTextField;
    private JTextField contactField;
    private JComboBox<DepartmentName> departmentField;  // Typed to DepartmentName
    private JTextField salaryField;
    private JButton ADDButton;
    private JButton backButton;

    public AddEmployee(JFrame frame, Admin currentAdmin) {
        ADDButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String ageText = ageTextField.getText().trim();
            String salaryText = salaryField.getText().trim();

            try {
                int age = Integer.parseInt(ageText); // ✅ Parse age outside the if()

                if (!Admin.isValidName(name) || !Admin.isValidContact(contact) || !Admin.isValidAge(age)) { // ✅ Fixed logic
                    JOptionPane.showMessageDialog(rootPanel, "Invalid name, contact, or age format.");
                    return;
                }

                String selectedDept = ((DepartmentName) departmentField.getSelectedItem()).name();
                Department department = Department.getInstance(DepartmentName.valueOf(selectedDept));

                double salary = Double.parseDouble(salaryText);

                Employee emp = new Employee(name, contact, department);
                emp.setAge(ageText);
                emp.setBasicSalary(salary);
                emp.saveToFiles();

                JOptionPane.showMessageDialog(rootPanel, "✅ Employee Created!\nUsername: " +
                        emp.getUsername() + "\nPassword: " + emp.getPassword());
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
