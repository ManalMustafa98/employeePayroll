import java.io.*;

public class Payroll<T extends Employee> {
    private static final String PAYROLL_FILE = "payroll.txt";
    private static final double OVERTIME_RATE = 100;
    private static final double LEAVE_DEDUCTION_RATE = 500;
    private static final int FREE_LEAVES = 4;

    private T employee;
    private Attendance attendance;

    public Payroll(T employee, Attendance attendance) {
        this.employee = employee;
        this.attendance = attendance;
    }

    // 🔹 Main salary calculation
    public double calculateTotalSalary() {
        double basic = employee.getBasicSalary();
        double allowance = 0.10 * basic;
        double tax = calculateTax(basic);
        double deduction = calculateDeduction();
        double bonus = calculateBonus();

        return basic + allowance + bonus - tax - deduction;
    }

    // 🔹 Tax calculation
    public double calculateTax(double salary) {
        if (salary <= 25000) return 0;
        if (salary <= 50000) return 0.05 * salary;
        if (salary <= 100000) return 0.10 * salary;
        return 0.15 * salary;
    }

    // 🔹 Deduction for extra absences
 public double calculateDeduction() {
        int absent = attendance.getAbsentDays();
        int extraAbsent = Math.max(0, absent - FREE_LEAVES);
        return extraAbsent * LEAVE_DEDUCTION_RATE;
    }

    // 🔹 Bonus for overtime
    public double calculateBonus() {
        int overtimeHours = attendance.calculateMonthlyOvertime();
        return overtimeHours * OVERTIME_RATE;
    }



    // 🔹 Optional: Save payslip to file
    public void savePayslipToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PAYROLL_FILE, true))) {
            writer.write(employee.getUsername() + "," +
                    employee.getBasicSalary() + "," +
                    (0.10 * employee.getBasicSalary()) + "," +
                    calculateBonus() + "," +
                    calculateDeduction() + "," +
                    calculateTax(employee.getBasicSalary()) + "," +
                    calculateTotalSalary());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving payroll data: " + e.getMessage());
        }
    }
}
