import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employee Payroll System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 550);
            frame.setLocationRelativeTo(null); // Center on screen
            WelcomePage welcomePanel = new WelcomePage(frame);
            frame.setContentPane(welcomePanel.getPanel());
            frame.setVisible(true);
        });
    }
}
