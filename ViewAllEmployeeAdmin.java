import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Displays a table‐like view of all employees.
 * Each column is a JList: Name | Age | Contact | Department | Salary
 */
public class ViewAllEmployeeAdmin {
    private JPanel rootPanel;
    private JList<String> list1;   // Name
    private JList<String> list2;   // Age
    private JList<String> list3;   // Contact
    private JList<String> list4;   // Department
    private JList<String> list5;   // Salary
    private JButton backButton;

    // Underlying list models for each column
    private final DefaultListModel<String> modelName       = new DefaultListModel<>();
    private final DefaultListModel<String> modelAge        = new DefaultListModel<>();
    private final DefaultListModel<String> modelContact    = new DefaultListModel<>();
    private final DefaultListModel<String> modelDepartment = new DefaultListModel<>();
    private final DefaultListModel<String> modelSalary     = new DefaultListModel<>();

    /**
     * Constructor: wires up models, populates data, and sets Back button behavior.
     *
     * @param frame      The main application JFrame (so we can swap panels).
     * @param currentAdmin  The currently logged‐in Admin (so we can pass back to AdminPage).
     */
    public ViewAllEmployeeAdmin(JFrame frame, Admin currentAdmin) {
        // 1) Attach each DefaultListModel to its JList
        list1.setModel(modelName);
        list2.setModel(modelAge);
        list3.setModel(modelContact);
        list4.setModel(modelDepartment);
        list5.setModel(modelSalary);

        // 2) Read from usersdetails.txt and populate only employee lines
        loadAllEmployeesFromFile();

        // 3) Back button returns to AdminPage
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    /**
     * Reads usersdetails.txt line by line. Lines with exactly 6 comma‐separated parts
     * are assumed to be employees (format: username,name,department,age,contact,salary).
     * Populate the five list models with name, age, contact, department, salary respectively.
     */
    private void loadAllEmployeesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Users.DETAILS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Only consider lines with 6 fields => employees
                if (parts.length == 6) {
                    String name       = parts[1];
                    String deptName   = parts[2];
                    String age        = parts[3];
                    String contact    = parts[4];
                    String salary     = parts[5];

                    modelName.addElement(name);
                    modelAge.addElement(age);
                    modelContact.addElement(contact);
                    modelDepartment.addElement(deptName);
                    modelSalary.addElement(salary);
                }
            }
        } catch (IOException e) {
            // If file not found or read fails, show an error but continue gracefully
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "Error loading employee details:\n" + e.getMessage(),
                    "File Read Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Required by IntelliJ/Swing Designer momentarily; our logic lives in the constructor. */
    private void createUIComponents() {
        // No custom component creation required here.
        // The JLists and JButton will be bound by the GUI designer.
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
