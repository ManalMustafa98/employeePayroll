import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

public class ViewAttendanceForAllEmployee_Admin {
    private JPanel rootPanel;
    private JButton backButton;
    private JList<String> listName;      // Employee Name
    private JList<String> listCheckin;   // Check-In Time
    private JList<String> listCheckout;  // Check-Out Time
    private JList<String> listOvertime;  // Overtime Hours
    private JList<String> listStatus;    // Present/Absent

    private final DefaultListModel<String> modelName      = new DefaultListModel<>();
    private final DefaultListModel<String> modelCheckin   = new DefaultListModel<>();
    private final DefaultListModel<String> modelCheckout  = new DefaultListModel<>();
    private final DefaultListModel<String> modelOvertime  = new DefaultListModel<>();
    private final DefaultListModel<String> modelStatus    = new DefaultListModel<>();

    public ViewAttendanceForAllEmployee_Admin(JFrame frame, Admin admin) {
        // Bind models to the JList components
        listName.setModel(modelName);
        listCheckin.setModel(modelCheckin);
        listCheckout.setModel(modelCheckout);
        listOvertime.setModel(modelOvertime);
        listStatus.setModel(modelStatus);

        // Load current month’s attendance
        loadAttendanceDataForCurrentMonth();

        // Back button returns to AdminPage
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, admin).getPanel());
            frame.revalidate();
        });
    }

    private void loadAttendanceDataForCurrentMonth() {
        File file = new File(Attendance.ATTENDANCE_FILE);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "Attendance file not found:\n" + Attendance.ATTENDANCE_FILE,
                    "File Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        Set<String> seenUserDate = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String username = parts[0];
                String dateStr = parts[1];
                String status = parts[2];
                String checkin = parts[3];
                String checkout = parts[4];
                String overtimeStr = parts[5];

                LocalDate recordDate;
                try {
                    recordDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException dtpe) {
                    continue;
                }

                if (recordDate.getYear() != currentYear || recordDate.getMonthValue() != currentMonth) {
                    continue;
                }

                String key = username + "_" + dateStr;
                if (!seenUserDate.add(key)) continue;

                String name = lookupEmployeeName(username);
                modelName.addElement(name != null ? name : username);
                modelCheckin.addElement("null".equals(checkin) ? "--" : checkin);
                modelCheckout.addElement("null".equals(checkout) ? "--" : checkout);
                modelOvertime.addElement(overtimeStr);
                modelStatus.addElement(status);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    rootPanel,
                    "Error reading attendance file:\n" + e.getMessage(),
                    "I/O Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String lookupEmployeeName(String username) {
        File file = new File(Users.DETAILS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username)) {
                    return parts[1]; // name
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
