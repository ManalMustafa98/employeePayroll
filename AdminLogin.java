import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminLogin {
    private JPanel      rootPanel;
    private JTextField  usernameTextField;
    private JPasswordField passwordTextField;
    private JButton     loginButton;
    private JButton     backButton;

    public AdminLogin(JFrame frame) {
        // 1️⃣ Disable login until both fields have input
        loginButton.setEnabled(false);
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { validateInputs(); }
            public void removeUpdate(DocumentEvent e)  { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        usernameTextField.getDocument().addDocumentListener(dl);
        passwordTextField.getDocument().addDocumentListener(dl);

        // 2️⃣ Attempt login on click
        loginButton.addActionListener(e -> {
            String user = usernameTextField.getText().trim();
            String pass = new String(passwordTextField.getPassword()).trim();

            if (authenticateAdmin(user, pass)) {
                Admin loggedInAdmin = loadAdmin(user, pass);
                if (loggedInAdmin != null) {
                    frame.setContentPane(new AdminPage(frame, loggedInAdmin).getPanel());
                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(
                            rootPanel,
                            "❌ Failed to load admin details.",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        rootPanel,
                        "❌ Invalid credentials or not an admin.",
                        "Authentication Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // 3️⃣ Back to welcome
        backButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    private void validateInputs() {
        boolean ok = !usernameTextField.getText().trim().isEmpty()
                && passwordTextField.getPassword().length > 0;
        loginButton.setEnabled(ok);
    }

    private boolean authenticateAdmin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3
                        && parts[0].equals(username)
                        && parts[1].equals(password)
                        && parts[2].equalsIgnoreCase("admin")) {
                    return true;
                }
            }
        } catch (IOException ignored) { }
        return false;
    }

    private Admin loadAdmin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Users.DETAILS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // DETAILS_FILE format: username,name,contact,[...]
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String name    = parts[1];
                    String contact = parts[2];
                    // use the password we just authenticated
                    return new Admin(username, password, name, contact);
                }
            }
        } catch (IOException ignored) { }
        return null;
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
