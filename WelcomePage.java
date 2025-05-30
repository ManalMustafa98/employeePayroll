import javax.swing.*;

public class WelcomePage{
    private JPanel panel1;
    private JButton continueAsEmployeeButton;
    private JButton continueAsAdminButton;

    public WelcomePage(JFrame frame) {
        continueAsAdminButton.addActionListener(e -> {
            frame.setContentPane(new AdminLogin(frame).getPanel());
            frame.revalidate();
        });

        continueAsEmployeeButton.addActionListener(e -> {
            frame.setContentPane(new EmployeeLogin(frame).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return panel1;
    }
}
