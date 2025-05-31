import javax.swing.*;
import java.io.*;
import java.util.*;

public class ViewAttendanceForAllEmployee_Admin {
    private JPanel rootPanel;
    private JButton backButton;
    private JList<String> listStatus; // Present/Absent
    private JList<String> listOvertime; // Overtime
    private JList<String> listCheckout; // Checkout
    private JList<String> listCheckin; // Checkin
    private JList<String> listName; // Name

    private DefaultListModel<String> modelStatus   = new DefaultListModel<>();
    private DefaultListModel<String> modelOvertime = new DefaultListModel<>();
    private DefaultListModel<String> modelCheckout = new DefaultListModel<>();
    private DefaultListModel<String> modelCheckin  = new DefaultListModel<>();
    private DefaultListModel<String> modelName     = new DefaultListModel<>();

    public ViewAttendanceForAllEmployee_Admin(JFrame frame, Admin admin) {
        listStatus.setModel(modelStatus);
        listOvertime.setModel(modelOvertime);
        listCheckout.setModel(modelCheckout);
        listCheckin.setModel(modelCheckin);
        listName.setModel(modelName);

        loadAttendanceData();

        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, admin).getPanel());
            frame.pack();
            frame.revalidate();
        });
    }

    private void loadAttendanceData() {
        File file = new File("attendance.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(rootPanel, "Attendance file not found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            Set<String> processed = new HashSet<>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String username = parts[0];
                String date     = parts[1];
                String status   = parts[2];
                String checkin  = parts[3];
                String checkout = parts[4];
                String overtime = parts[5];

                // Don't duplicate entries
                String uniqueKey = username + date;
                if (processed.contains(uniqueKey)) continue;
                processed.add(uniqueKey);

                // Resolve name from usersdetails.txt
                String name = lookupName(username);

                modelName.addElement(name != null ? name : username);
                modelCheckin.addElement(checkin.equals("null") ? "--" : checkin);
                modelCheckout.addElement(checkout.equals("null") ? "--" : checkout);
                modelOvertime.addElement(overtime);
                modelStatus.addElement(status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lookupName(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader("usersdetails.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
