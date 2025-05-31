import java.util.*;

/**
 * A singleton-per-enum Department class.
 * Ensures all employees of the same DepartmentName go into the same list.
 */
public class Department {
    // Static registry of one Department instance per DepartmentName
    private static final Map<DepartmentName, Department> registry = new EnumMap<>(DepartmentName.class);

    static {
        for (DepartmentName dn : DepartmentName.values()) {
            registry.put(dn, new Department(dn));
        }
    }

    /**
     * Fetch the single Department instance for the given DepartmentName.
     */
    public static Department getInstance(DepartmentName deptName) {
        return registry.get(deptName);
    }

    private final DepartmentName departmentName;
    private final List<Employee> employees;

    // Private constructor to enforce singleton-per-enum pattern
    private Department(DepartmentName departmentName) {
        this.departmentName = departmentName;
        this.employees = new ArrayList<>();
    }

    public DepartmentName getName() {
        return departmentName;
    }

    /**
     * Returns an unmodifiable view of the employees in this department.
     */
    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }

    /**
     * Add an employee to this department (avoids duplicates).
     */
    public void addEmployee(Employee employee) {
        if (employee != null && !employees.contains(employee)) {
            employees.add(employee);
        }
    }

    /**
     * Remove an employee from this department (e.g., upon deletion).
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
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
