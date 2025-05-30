import javax.swing.*;

public class AdminPage {
    private JPanel rootPanel;
    private JButton ADDADMINButton;
    private JButton ADDEMPLOYEEButton;
    private JButton LOGOUTButton;
    private JButton VIEWEMPLOYEEBYDEPARTMENTButton;
    private JButton VIEWATTENDANCEButton;
    private JButton VIEWALLEMPLOYEEButton;

    public AdminPage(JFrame frame, Admin admin) {
        ADDADMINButton.addActionListener(e -> {
            frame.setContentPane(new AddAdmin(frame, admin).getPanel());
            frame.revalidate();
        });

        ADDEMPLOYEEButton.addActionListener(e -> {
            frame.setContentPane(new AddEmployee(frame, admin).getPanel());
            frame.revalidate();
        });

        LOGOUTButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
