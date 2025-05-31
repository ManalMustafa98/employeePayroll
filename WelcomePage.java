import javax.swing.*;

public class WelcomePage {
    private JPanel rootPanel;
    private JButton continueAsEmployeeButton;
    private JButton continueAsAdminButton;

    public WelcomePage(JFrame frame) {
        continueAsEmployeeButton.addActionListener(e -> {
            frame.setContentPane(new EmployeeLogin(frame).getPanel());
            frame.pack();
            frame.revalidate();
        });

        continueAsAdminButton.addActionListener(e -> {
            frame.setContentPane(new AdminLogin(frame).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
