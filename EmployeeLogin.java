import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EmployeeLogin {
    private JPanel        rootPanel;
    private JTextField    usernameTextField;
    private JPasswordField passwordField;    // Renamed to avoid conflict with class
    private JButton       loginButton;
    private JButton       backButton;

    public EmployeeLogin(JFrame frame) {
        // 1️⃣ Disable Login until both fields are non-empty
        loginButton.setEnabled(false);
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)  { validateInputs(); }
            @Override
            public void removeUpdate(DocumentEvent e)  { validateInputs(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        usernameTextField.getDocument().addDocumentListener(dl);
        passwordField.getDocument().addDocumentListener(dl);

        // 2️⃣ Handle Login click
        loginButton.addActionListener(e -> {
            String user = usernameTextField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (isValidEmployee(user, pass)) {
                Employee emp = loadEmployee(user);
                if (emp != null) {
                    frame.setContentPane(new EmployeePage(frame, emp).getPanel());
                    frame.pack();
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(
                            rootPanel,
                            "❌ Unable to load employee details.",
                            "Load Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        rootPanel,
                        "❌ Invalid username or password.",
                        "Authentication Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // 3️⃣ Handle Back click
        backButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    /** Enables the login button only when both username and password fields are non-empty */
    private void validateInputs() {
        boolean ok = !usernameTextField.getText().trim().isEmpty()
                && passwordField.getPassword().length > 0;
        loginButton.setEnabled(ok);
    }

    /** Checks users.txt to verify that the given username/password belong to an employee */
    private boolean isValidEmployee(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3
                        && parts[0].equals(username)
                        && parts[1].equals(password)
                        && parts[2].equalsIgnoreCase("employee")) {
                    return true;
                }
            }
        } catch (IOException ignored) { }
        return false;
    }

    /**
     * Loads the Employee’s details from usersdetails.txt.
     * Expects format: username,name,department,age,contact,salary
     */
    private Employee loadEmployee(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.DETAILS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    // parts[0] = username
                    String name           = parts[1];
                    String departmentName = parts[2];
                    String age            = parts[3];
                    String contact        = parts[4];
                    double salary         = Double.parseDouble(parts[5]);

                    DepartmentName deptEnum = DepartmentName.valueOf(departmentName.toUpperCase());
                    Department dept = Department.getInstance(deptEnum);

                    // Use the updated constructor signature (username, password, name, contact, department)
                    Employee emp = new Employee(username, /* password not stored here */ "", name, contact, dept);
                    emp.setAge(age);
                    emp.setBasicSalary(salary);
                    return emp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
