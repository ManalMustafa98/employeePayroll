import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewEmployeeByDept_Admin {
    private JPanel rootPanel;
    private JComboBox<DepartmentName> comboBox1;
    private JList<String> list1; // Name
    private JList<String> list2; // Contact
    private JList<String> list3; // Age
    private JList<String> list4; // Salary
    private JButton backButton;

    private final DefaultListModel<String> modelName    = new DefaultListModel<>();
    private final DefaultListModel<String> modelContact = new DefaultListModel<>();
    private final DefaultListModel<String> modelAge     = new DefaultListModel<>();
    private final DefaultListModel<String> modelSalary  = new DefaultListModel<>();

    public ViewEmployeeByDept_Admin(JFrame frame, Admin admin) {
        // 1) Attach models to the JLists
        list1.setModel(modelName);
        list2.setModel(modelContact);
        list3.setModel(modelAge);
        list4.setModel(modelSalary);

        // 2) Fill department dropdown with enum values
        comboBox1.setModel(new DefaultComboBoxModel<>(DepartmentName.values()));

        // 3) When selection changes, reload employees
        comboBox1.addActionListener(e -> loadEmployeesForSelectedDepartment());

        // 4) Initial load for the first department
        comboBox1.setSelectedIndex(0);
        loadEmployeesForSelectedDepartment();

        // 5) BACK button returns to AdminPage
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    /**
     * Clears current list models and repopulates them from the shared Department instance.
     */
    private void loadEmployeesForSelectedDepartment() {
        modelName.clear();
        modelContact.clear();
        modelAge.clear();
        modelSalary.clear();

        DepartmentName selectedDept = (DepartmentName) comboBox1.getSelectedItem();
        // Fetch the shared Department instance
        Department dept = Department.getInstance(selectedDept);
        List<Employee> employees = dept.getEmployees();

        for (Employee emp : employees) {
            modelName.addElement(emp.getName());                                   // Name
            modelContact.addElement(emp.getContact());                             // Contact
            modelAge.addElement(emp.getAge());                                     // Age
            modelSalary.addElement(String.valueOf(emp.getBasicSalary()));          // Salary
        }
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
