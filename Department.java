import java.util.*;

public class Department {
    private DepartmentName departmentName;
    private List<Employee> employees;

    // Static shared instances for each department
    private static final Department HR = new Department(DepartmentName.HR);
    private static final Department MARKETING = new Department(DepartmentName.MARKETING);
    private static final Department SALES = new Department(DepartmentName.SALES);
    private static final Department FINANCE = new Department(DepartmentName.FINANCE);
    private static final Department IT = new Department(DepartmentName.IT);
    private static final Department OPERATIONS = new Department(DepartmentName.OPERATIONS);

    // Static method to return correct instance
    public static Department getInstance(DepartmentName deptName) {
        switch (deptName) {
            case HR: return HR;
            case MARKETING: return MARKETING;
            case SALES: return SALES;
            case FINANCE: return FINANCE;
            case IT: return IT;
            case OPERATIONS: return OPERATIONS;
            default: throw new IllegalArgumentException("Unknown department");
        }
    }

    // Private constructor to restrict instantiation
    public Department(DepartmentName departmentName) {
        this.departmentName = departmentName;
        this.employees = new ArrayList<>();
    }

    public DepartmentName getName() {
        return departmentName;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }
}

enum DepartmentName {
    HR,
    MARKETING,
    SALES,
    FINANCE,
    IT,
    OPERATIONS
}



