import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class AddAdmin {
    private JPanel rootPanel;
    private JTextField nameTextField;
    private JTextField contactTextField;
    private JTextField ageTextField;   // New field for age
    private JButton addButton;
    private JButton backButton;

    public AddAdmin(JFrame frame, Admin currentAdmin) {
        // 1️⃣ Set up live validation and initial button state
        initializeValidation();

        // 2️⃣ Add-button action
        addButton.addActionListener(e -> handleAdd(frame));

        // 3️⃣ Back-button action
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    /** Disable Add until name, contact, and age are valid */
    private void initializeValidation() {
        addButton.setEnabled(false);

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { validateInputs(); }
            @Override public void removeUpdate(DocumentEvent e)  { validateInputs(); }
            @Override public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };

        nameTextField.getDocument().addDocumentListener(dl);
        contactTextField.getDocument().addDocumentListener(dl);
        ageTextField.getDocument().addDocumentListener(dl);
    }

    /** Check that each field meets its criteria before enabling Add */
    private void validateInputs() {
        String name    = nameTextField.getText().trim();
        String contact = contactTextField.getText().trim();
        String ageTxt  = ageTextField.getText().trim();

        boolean validName    = Admin.isValidName(name);
        boolean validContact = Admin.isValidContact(contact);
        boolean validAge     = false;

        try {
            int a = Integer.parseInt(ageTxt);
            validAge = (a > 0 && a < 150);
        } catch (NumberFormatException ignored) { }

        addButton.setEnabled(validName && validContact && validAge);
    }

    /** Called when Add is clicked */
    private void handleAdd(Component parent) {
        String name    = nameTextField.getText().trim();
        String contact = contactTextField.getText().trim();
        int age;

        try {
            age = Integer.parseInt(ageTextField.getText().trim());
        } catch (NumberFormatException e) {
            // Should not happen because we validated already, but just in case
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "❌ Age must be a valid integer between 1 and 149.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Admin newAdmin = new Admin(name, contact, age);
            newAdmin.saveToFiles();

            JOptionPane.showMessageDialog(
                    rootPanel,
                    "✅ Admin Created!\nUsername: " + newAdmin.getUsername() +
                            "\nPassword: " + newAdmin.getPassword(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "❌ Error saving admin:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /** Reset all fields and disable the Add button */
    private void clearFields() {
        nameTextField.setText("");
        contactTextField.setText("");
        ageTextField.setText("");
        addButton.setEnabled(false);
        nameTextField.requestFocusInWindow();
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
