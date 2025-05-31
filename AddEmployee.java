import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;

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
        // 1️⃣ Populate the department dropdown with enum values
        departmentField.setModel(new DefaultComboBoxModel<>(DepartmentName.values()));

        // 2️⃣ Initialize validation so ADD is disabled until inputs are valid
        initializeValidation();

        // 3️⃣ Handle ADD button click
        ADDButton.addActionListener(e -> handleAdd(frame));

        // 4️⃣ Handle BACK button click
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    /** Set up DocumentListeners to enable/disable ADD based on valid inputs */
    private void initializeValidation() {
        ADDButton.setEnabled(false);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateInputs(); }
            public void removeUpdate(DocumentEvent e)  { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };

        nameField.getDocument().addDocumentListener(dl);
        ageTextField.getDocument().addDocumentListener(dl);
        contactField.getDocument().addDocumentListener(dl);
        salaryField.getDocument().addDocumentListener(dl);
        // departmentField always has a valid selection from enum
    }

    /** Check that each field meets its criteria before enabling ADD */
    private void validateInputs() {
        String name    = nameField.getText().trim();
        String ageTxt  = ageTextField.getText().trim();
        String contact = contactField.getText().trim();
        String salTxt  = salaryField.getText().trim();

        boolean valid = Admin.isValidName(name)
                && isValidAge(ageTxt)
                && Admin.isValidContact(contact)
                && isValidSalary(salTxt);
        ADDButton.setEnabled(valid);
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

    /** Called when ADD is clicked */
    private void handleAdd(JFrame frame) {
        String name    = nameField.getText().trim();
        String age     = ageTextField.getText().trim();
        String contact = contactField.getText().trim();
        DepartmentName deptName = (DepartmentName) departmentField.getSelectedItem();
        String salTxt  = salaryField.getText().trim();

        try {
            // Replace new Department(...) with the singleton lookup
            Department dept = Department.getInstance(deptName);

            double salary = Double.parseDouble(salTxt);
            Employee emp = new Employee(name, contact, dept);
            emp.setAge(age);
            emp.setBasicSalary(salary);
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
            // In case enum conversion or parsing fails
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "Invalid input: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "❌ Error saving employee: " + ex.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Reset fields for the next entry */
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
