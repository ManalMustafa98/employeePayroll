import javax.swing.*;
import java.io.*;

public class AdminLogin {
    private JPanel panel1;
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;
    private JButton loginButton;
    private JButton backButton;

    public AdminLogin(JFrame frame) {
        loginButton.addActionListener(e -> {
            String inputUsername = usernameTextField.getText().trim();
            String inputPassword = new String(passwordTextField.getPassword()).trim();

            if (authenticateAdmin(inputUsername, inputPassword)) {
                Admin loggedInAdmin = loadAdmin(inputUsername);
                frame.setContentPane(new AdminPage(frame, loggedInAdmin).getPanel());
                frame.revalidate();
            } else {
                JOptionPane.showMessageDialog(frame, "❌ Invalid credentials or not an admin.");
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    private boolean authenticateAdmin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Users.USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[0].equals(username) &&
                        parts[1].equals(password) && parts[2].equals("admin")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Admin loadAdmin(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Users.DETAILS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return new Admin(username, "", parts[1], parts[2]); // password blanked
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JPanel getPanel() {
        return panel1;
    }
}
