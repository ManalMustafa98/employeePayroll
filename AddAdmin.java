import javax.swing.*;
import java.io.IOException;

public class AddAdmin {
    private JPanel Panel1;
    private JTextField nameTextField;
    private JTextField contactTextField;
    private JTextField ageTextField1;
    private JButton addButton;
    private JButton backButton;

    public AddAdmin(JFrame frame, Admin currentAdmin) {
        addButton.addActionListener(e -> {
            String name = nameTextField.getText().trim();
            String contact = contactTextField.getText().trim();

            try {
                if (!Admin.isValidName(name) || !Admin.isValidContact(contact)) {
                    JOptionPane.showMessageDialog(Panel1, "Invalid input format!");
                    return;
                }

                Admin newAdmin = new Admin(name, contact);
                newAdmin.saveToFiles();

                JOptionPane.showMessageDialog(Panel1, "✅ Admin Created!\nUsername: " +
                        newAdmin.getUsername() + "\nPassword: " + newAdmin.getPassword());

                nameTextField.setText("");
                contactTextField.setText("");
                ageTextField1.setText("");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Panel1, "❌ Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, currentAdmin).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return Panel1;
    }
}
