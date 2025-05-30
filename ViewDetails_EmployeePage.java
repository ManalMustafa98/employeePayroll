import javax.swing.*;
import java.awt.*;

public class ViewDetails_EmployeePage {
    private JTextField textField1; // Username
    private JTextField textField2; // Name
    private JTextField textField3; // Age
    private JTextField textField4; // Contact
    private JTextField textField5; // Department
    private JTextField textField6; // Salary
    private JButton backButton;
    private JPanel rootPanel;

    public ViewDetails_EmployeePage(JFrame frame, Employee emp) {
        // Set values from backend
        textField1.setText(emp.getUsername());
        textField2.setText(emp.getName());
        textField3.setText(emp.getAge());
        textField4.setText(emp.getContact());
        textField5.setText(emp.getDepartment().getName().toString());
        textField6.setText("₹" + emp.getBasicSalary());

        // Improve field appearance (look like labels)
        JTextField[] fields = { textField1, textField2, textField3, textField4, textField5, textField6 };
        for (JTextField tf : fields) {
            tf.setEditable(false);
            tf.setBorder(null);
            tf.setBackground(null);
            tf.setForeground(Color.BLACK);
            tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        // Optional: customize button font
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Back button functionality
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(emp, frame).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
