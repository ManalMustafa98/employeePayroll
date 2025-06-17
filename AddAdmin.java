import javax.swing.*;
import java.io.IOException;

public class AddAdmin {
    private JPanel rootPanel;
    private JTextField nameTextField;
    private JTextField contactTextField;
    private JTextField ageTextField;   // New field for age
    private JButton addButton;
    private JButton backButton;

    public AddAdmin(JFrame frame, Admin currentAdmin) {
        addButton.addActionListener(e -> {
            String name = nameTextField.getText().trim();
            String contact = contactTextField.getText().trim();
            int age = Integer.parseInt(ageTextField.getText().trim());

            try {
                if (!Admin.isValidName(name) || !Admin.isValidContact(contact)|| !Admin.isValidAge(age)) {
                    JOptionPane.showMessageDialog(rootPanel, "Invalid input format!");
                    return;
                }

                Admin newAdmin = new Admin(name,contact,age);
                newAdmin.saveToFiles();

                JOptionPane.showMessageDialog(rootPanel, "✅ Admin Created!\nUsername: " +
                        newAdmin.getUsername() + "\nPassword: " + newAdmin.getPassword());
                clearFields();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPanel, "❌ Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

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