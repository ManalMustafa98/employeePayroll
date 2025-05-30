import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Employee extends Users {
    private String age;
    private double basicSalary;
    private Department department;

    public Employee(String name, String contact, Department department) throws IOException {
        super(name, contact, "employee");
        this.department = department;
        department.addEmployee(this);
    }

    public Employee(String username, String password, String contact ,String name, Department department) {
        super(username, password, name, contact, "employee");
        this.department = department;
        department.addEmployee(this);
    }

    @Override
    public void saveToFiles() throws IOException {
        appendToFile(USERS_FILE, toUserFileLine());
        appendToFile(DETAILS_FILE, username + "," + name + "," + department.getName() + "," + age + "," + contact + "," + basicSalary);
    }

    public void viewPayslip(String month, int year) {
        Attendance attendance = Attendance.loadFromFile(username, month, year);
        Payroll<Employee> payroll = new Payroll<>(this, attendance);
        payroll.generatePayslip();
    }

    public void performCheckInCheckOut(String month, int year) {
        Attendance attendance = Attendance.loadFromFile(username, month, year);
        LocalDate today = LocalDate.now();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter check-in time (e.g. 09:00): ");
        String in = scanner.nextLine();
        attendance.checkIn(today, in);

        System.out.print("Enter check-out time (e.g. 17:00): ");
        String out = scanner.nextLine();
        attendance.checkOut(today, out);

        attendance.saveToFile();
        System.out.println("✅ Check-in and check-out recorded for " + today);
    }

    public void setAge(String age) { this.age = age; }

    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }

    public String getAge() { return age; }

    public double getBasicSalary() { return basicSalary; }
    public Department getDepartment() {
        return department;
    }

    public void updateDepartment(Department department) {
        this.department = department;
    }

    public void viewAttendance() {
        LocalDate today = LocalDate.now();
        String month = today.getMonth().toString();
        int year = today.getYear();

        Attendance attendance = Attendance.loadFromFile(username, month, year);
        System.out.println("\n--- Attendance Summary for " + name + " ---");
        System.out.println("Present Days: " + attendance.getPresentDays());
        System.out.println("Absent Days: " + attendance.getAbsentDays());
        System.out.println("Overtime Hours: " + attendance.calculateMonthlyOvertime());
    }

}
