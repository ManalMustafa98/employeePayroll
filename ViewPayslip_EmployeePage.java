import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ViewPayslip_EmployeePage {
    private JPanel rootPanel;
    private JLabel basicSalaryLabel;
    private JLabel bonusLabel;
    private JLabel deductionLabel;
    private JLabel taxLabel;
    private JLabel allowanceLabel;
    private JLabel totalLabel;
    private JButton backButton;

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
        basicSalaryLabel.setText("₹" + basic);
        allowanceLabel.setText("₹" + allowance);
        bonusLabel.setText("₹" + bonus);
        deductionLabel.setText("₹" + deduction);
        taxLabel.setText("₹" + tax);
        totalLabel.setText("₹" + net);

        // Back button
        backButton.addActionListener(e -> {
            frame.setContentPane(new EmployeePage(frame, currentEmployee).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
