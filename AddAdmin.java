import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;

public class AddAdmin {
    private JPanel rootPanel;
    private JTextField nameTextField;
    private JTextField contactTextField;
    private JButton addButton;
    private JButton backButton;
    private JPanel Panel1;

    public AddAdmin(JFrame frame, Admin currentAdmin) {
        // Set up live validation and initial button state
        initializeValidation();

        // Add-button action
        addButton.addActionListener(e -> handleAdd(frame));

        // Back-button action
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    private void initializeValidation() {
        addButton.setEnabled(false);  // disabled until inputs valid

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validateInputs(); }
            @Override public void removeUpdate(DocumentEvent e) { validateInputs(); }
            @Override public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };

        nameTextField.getDocument().addDocumentListener(dl);
        contactTextField.getDocument().addDocumentListener(dl);
    }

    private void validateInputs() {
        String name    = nameTextField.getText().trim();
        String contact = contactTextField.getText().trim();
        boolean valid = Admin.isValidName(name)
                && Admin.isValidContact(contact);
        addButton.setEnabled(valid);
    }

    private void handleAdd(Component parent) {
        String name    = nameTextField.getText().trim();
        String contact = contactTextField.getText().trim();

        try {
            Admin newAdmin = new Admin(name, contact);
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

    private void clearFields() {
        nameTextField.setText("");
        contactTextField.setText("");
        addButton.setEnabled(false);
        nameTextField.requestFocusInWindow();
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
