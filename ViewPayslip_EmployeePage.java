import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ViewPayslip_EmployeePage {
    private JPanel rootPanel;
    private JButton backButton;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField salaryTextField;
    private JTextField bonusTextField;
    private JTextField deductionTextField;
    private JTextField taxTextField;
    private JTextField allowanceTextField;
    private JTextField totalTextField;

    public ViewPayslip_EmployeePage(JFrame frame, Employee currentEmployee) {
        // Load attendance for the current month
        LocalDate today = LocalDate.now();
        String month = today.getMonth().toString();
        int year = today.getYear();

        Attendance attendance = Attendance.loadFromFile(currentEmployee.getUsername(), month, year);
        Payroll<Employee> payroll = new Payroll<>(currentEmployee, attendance);

        double basic     = currentEmployee.getBasicSalary();
        double allowance = 0.10 * basic;
        double bonus     = payroll.calculateBonus();
        double deduction = payroll.calculateDeduction();
        double tax       = payroll.calculateTax(basic);
        double net       = payroll.calculateTotalSalary();

        // Set text to labels
        salaryTextField.setText("₹" + basic);
        allowanceTextField.setText("₹" + allowance);
        bonusTextField.setText("₹" + bonus);
        deductionTextField.setText("₹" + deduction);
        taxTextField.setText("₹" + tax);
        totalTextField.setText("₹" + net);

        JTextField[] fields = {
                salaryTextField, allowanceTextField, bonusTextField, deductionTextField,
                taxTextField, totalTextField
        };

        for (JTextField field : fields) {
            field.setEditable(false);
//            field.setBorder(null);
            field.setBackground(null);
            field.setForeground(Color.BLACK);
            field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        }

        // Back button
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(frame, currentEmployee).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
