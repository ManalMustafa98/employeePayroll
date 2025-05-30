import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class AddEmployee {
    private JPanel        rootPanel;
    private JTextField    nameField;
    private JTextField    ageTextField;
    private JTextField    contactField;
    private JComboBox<DepartmentName> departmentComboBox;
    private JTextField    salaryField;
    private JButton       addButton;
    private JButton       backButton;

    public AddEmployee(JFrame frame, Admin currentAdmin) {
        // 1️⃣ Populate department dropdown in code
        departmentComboBox.setModel(
                new DefaultComboBoxModel<>(DepartmentName.values())
        );

        // 2️⃣ Set up live validation
        initializeValidation();

        // 3️⃣ ADD button handler
        addButton.addActionListener(e -> handleAdd(frame));

        // 4️⃣ BACK button
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    private void initializeValidation() {
        addButton.setEnabled(false);
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateInputs(); }
            public void removeUpdate(DocumentEvent e)  { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        nameField.getDocument().addDocumentListener(dl);
        ageTextField.getDocument().addDocumentListener(dl);
        contactField.getDocument().addDocumentListener(dl);
        salaryField.getDocument().addDocumentListener(dl);
        // departmentComboBox always has one of the enum values selected
    }

    private void validateInputs() {
        String name    = nameField.getText().trim();
        String ageTxt  = ageTextField.getText().trim();
        String contact = contactField.getText().trim();
        String salTxt  = salaryField.getText().trim();

        boolean valid = Admin.isValidName(name)
                && isValidAge(ageTxt)
                && Admin.isValidContact(contact)
                && isValidSalary(salTxt);
        addButton.setEnabled(valid);
    }

    private boolean isValidAge(String txt) {
        try {
            int a = Integer.parseInt(txt);
            return a > 0 && a < 150;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidSalary(String txt) {
        try {
            double s = Double.parseDouble(txt);
            return s >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void handleAdd(Component parent) {
        String name    = nameField.getText().trim();
        String age     = ageTextField.getText().trim();
        String contact = contactField.getText().trim();
        DepartmentName deptName = (DepartmentName) departmentComboBox.getSelectedItem();
        String salTxt  = salaryField.getText().trim();

        try {
            Department dept = new Department(deptName);
            Employee emp = new Employee(name, contact, dept);
            emp.setAge(age);
            emp.setBasicSalary(Double.parseDouble(salTxt));
            emp.saveToFiles();

            JOptionPane.showMessageDialog(
                    rootPanel,
                    "✅ Employee Created!\nUsername: " + emp.getUsername() +
                            "\nPassword: " + emp.getPassword(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearFields();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "Invalid input: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "❌ Error saving employee:\n" + ex.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageTextField.setText("");
        contactField.setText("");
        salaryField.setText("");
        departmentComboBox.setSelectedIndex(0);
        addButton.setEnabled(false);
        nameField.requestFocusInWindow();
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
