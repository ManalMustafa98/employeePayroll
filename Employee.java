import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Represents an Employee, extends Users.  Each Employee belongs to exactly one Department.
 */
public class Employee extends Users {
    private int age;
    private double basicSalary;
    private Department department;

    /**
     * Constructor for creating a brand‐new Employee.
     * Calls Users(name, contact, "employee") to set up username/password.
     */
    public Employee(String name, String contact, Department department) throws IOException {
        super(name, contact, "employee");
        this.department = department;
        department.addEmployee(this);
    }

    /**
     * Constructor used when loading from file.
     * Parameters order: (username, password, name, contact, department).
     */
    public Employee(String username, String password, String name, String contact, Department department) {
        super(username, password, name, contact, "employee");
        this.department = department;
        department.addEmployee(this);
    }

    /**
     * (No override of saveToFiles() needed—Users.saveToFiles() will call this.toDetailsFileLine()).
     */

    /**
     * Defines how this Employee’s “details” line is written into usersdetails.txt.
     * Format required by EmployeeLogin.loadEmployee():
     *    username,name,department,age,contact,salary
     */
    @Override
    public String toDetailsFileLine() {
        // department.getName() yields a DepartmentName enum; convert to uppercase string
        String deptName = department.getName().name().toUpperCase();
        return String.join(",",
                getUsername(),
                getName(),
                deptName,
                Integer.toString(getAge()),
                getContact(),
                String.valueOf(basicSalary)
        );
    }

    /**
     * Helper for the Swing frontend: view current month’s payslip in console.
     */
    public void viewPayslip(String month, int year) {
        Attendance attendance = Attendance.loadFromFile(getUsername(), month, year);
        Payroll<Employee> payroll = new Payroll<>(this, attendance);
        payroll.generatePayslip();
    }

    /**
     * CLI helper: perform a check‐in/check‐out using Scanner input (console).
     */
    public void performCheckInCheckOut(String month, int year) {
        Attendance attendance = Attendance.loadFromFile(getUsername(), month, year);
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

    // Setter for age (String, since we want to preserve leading zeros or custom formatting)
    public void setAge(int age) {
        this.age = age;
    }

    // Setter for basic salary
    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public int getAge() {
        return age;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public Department getDepartment() {
        return department;
    }

    /**
     * If you ever change an employee’s department, call this:
     *   oldDept.removeEmployee(this);
     *   setDepartment(newDept);
     *   newDept.addEmployee(this);
     */
    public void updateDepartment(Department department) {
        this.department = department;
    }

    /**
     * Prints a simple attendance summary to console.
     */
    public void viewAttendance() {
        LocalDate today = LocalDate.now();
        String month = today.getMonth().toString();
        int year = today.getYear();

        Attendance attendance = Attendance.loadFromFile(getUsername(), month, year);
        System.out.println("\n--- Attendance Summary for " + getName() + " ---");
        System.out.println("Present Days: " + attendance.getPresentDays());
        System.out.println("Absent Days: " + attendance.getAbsentDays());
        System.out.println("Overtime Hours: " + attendance.calculateMonthlyOvertime());
    }
    public static void loadAllEmployeesFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("usersdetails.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                String username = parts[0];
                String name = parts[1];
                DepartmentName deptEnum = DepartmentName.valueOf(parts[2].toUpperCase());
                int age = Integer.parseInt(parts[3]);
                String contact = parts[4];
                double salary = Double.parseDouble(parts[5]);

                // Also get password from users.txt
                String password = findPasswordForUsername(username);
                if (password == null) continue;

                Department dept = Department.getInstance(deptEnum);
                Employee emp = new Employee(username, password, name, contact, dept);
                emp.setAge(age);
                emp.setBasicSalary(salary);
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to load employees: " + e.getMessage());
        }
    }

    private static String findPasswordForUsername(String username) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[0].equals(username) && parts[2].equals("employee")) {
                    return parts[1];
                }
            }
        }
        return null;
    }

}
