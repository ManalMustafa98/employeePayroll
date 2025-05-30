import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class EmployeeLogin {
    private JPanel panel1;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton loginButton;
    private JButton backButton;

    public EmployeeLogin(JFrame frame) {
        loginButton.addActionListener(e -> {
            String inputUsername = usernameTextField.getText().trim();
            String inputPassword = passwordTextField.getText().trim();

            if (isValidEmployee(inputUsername, inputPassword)) {
                Employee employee = loadEmployee(inputUsername);
                frame.setContentPane(new EmployeePage(frame, employee).getPanel());
                frame.revalidate();
            } else {
                JOptionPane.showMessageDialog(panel1, "Invalid credentials!");
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    private boolean isValidEmployee(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3 && parts[0].equals(username)
                        && parts[1].equals(password) && parts[2].equalsIgnoreCase("employee")) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private Employee loadEmployee(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.DETAILS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    String name = parts[1];
                    String departmentName = parts[2];
                    String age = parts[3];
                    String contact = parts[4];
                    double salary = Double.parseDouble(parts[5]);

                    Department dept = new Department(DepartmentName.valueOf(departmentName.toUpperCase()));
                    Employee emp = new Employee(username, "",name, contact, dept);
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
        return panel1;
    }
}
