import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

public class ViewAttendanceForAllEmployee_Admin {
    private JPanel      rootPanel;
    private JButton     backButton;
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
        // 1) Attach models to JLists
        listName.setModel(modelName);
        listCheckin.setModel(modelCheckin);
        listCheckout.setModel(modelCheckout);
        listOvertime.setModel(modelOvertime);
        listStatus.setModel(modelStatus);

        // 2) Load only this month’s attendance data
        loadAttendanceDataForCurrentMonth();

        // 3) Back button goes to AdminPage
        backButton.addActionListener(e -> {
            frame.setContentPane(new AdminPage(frame, admin).getPanel());
            frame.revalidate();
        });
    }

    /**
     * Reads ATTENDANCE_FILE and populates today’s (really, “this month’s”) attendance.
     * Format in attendance.txt:
     *    username,date,status,checkIn,checkOut,overtimeHours
     *   - date is “YYYY-MM-DD” (ISO)
     *   - status is “PRESENT” or “ABSENT”
     *   - checkIn/checkOut are either “HH:mm” or “null”
     *   - overtimeHours is an integer
     *
     * Only the first record per (username + date) is shown. We filter to the current month/year.
     */
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
        int currentYear  = today.getYear();
        int currentMonth = today.getMonthValue();

        Set<String> seenUserDate = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String username    = parts[0];
                String dateStr     = parts[1];
                String status      = parts[2];
                String checkin     = parts[3];
                String checkout    = parts[4];
                String overtimeStr = parts[5];

                LocalDate recordDate;
                try {
                    recordDate = LocalDate.parse(dateStr);
                } catch (DateTimeParseException dtpe) {
                    // Skip malformed date
                    continue;
                }

                // Filter to current month and year
                if (recordDate.getYear() != currentYear || recordDate.getMonthValue() != currentMonth) {
                    continue;
                }

                // Only show one entry per user+date
                String userDateKey = username + "_" + dateStr;
                if (seenUserDate.contains(userDateKey)) {
                    continue;
                }
                seenUserDate.add(userDateKey);

                // Look up the employee’s name (or use username if not found)
                String name = lookupEmployeeName(username);

                modelName.addElement((name != null) ? name : username);
                modelCheckin.addElement(checkin.equals("null") ? "--" : checkin);
                modelCheckout.addElement(checkout.equals("null") ? "--" : checkout);
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

    /**
     * Scans Users.DETAILS_FILE for the given username and returns the stored name.
     * Details format (for employees) is:
     *    username,name,department,age,contact,salary
     * We only need parts[1] if parts.length >= 2.
     */
    private String lookupEmployeeName(String username) {
        File file = new File(Users.DETAILS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            // Ignore; we’ll return null and fallback to username
        }
        return null;
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
