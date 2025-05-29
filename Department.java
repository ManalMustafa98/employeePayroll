import java.util.*;

public class Department {
    private DepartmentName departmentName;
    private List<Employee> employees;

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



