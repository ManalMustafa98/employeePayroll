import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminLogin  {
    private JPanel panel1;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton loginButton;
    private JButton backButton;

    public AdminLogin(JFrame frame) {
        loginButton.addActionListener(e -> {
                    String inputUsername = usernameTextField.getText().trim();
                    String inputPassword = passwordTextField.getText().trim();

                });

        backButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return panel1;
    }
}

