import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class EmployeePage {
    private JPanel rootPanel;
    private JButton CHECKINButton;
    private JButton checkOutButton;
    private JButton viewDetailsButton;
    private JButton viewPayslipButton;
    private JButton logoutButton;

    private Attendance currentAttendance;
    private boolean hasCheckedIn;
    private boolean hasCheckedOut;

    public EmployeePage(JFrame frame, Employee employee) {
        LocalDate today = LocalDate.now();
        String month = today.getMonth().toString();
        int year = today.getYear();

        // ✅ Load attendance from file
        currentAttendance = Attendance.loadFromFile(employee.getUsername(), month, year);

        // ✅ Determine if already checked in or out based on saved file data
        DailyAttendance todayRecord = currentAttendance.getRecord(today);
        hasCheckedIn = (todayRecord != null && todayRecord.getCheckIn() != null);
        hasCheckedOut = (todayRecord != null && todayRecord.getCheckOut() != null);

        // ✅ Check-in button logic
        CHECKINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!hasCheckedIn) {
                    String inTime = JOptionPane.showInputDialog(frame, "Enter check-in time (e.g. 09:00):");
                    if (inTime != null && !inTime.trim().isEmpty()) {
                        try {
                            currentAttendance.checkIn(today, inTime.trim());
                            currentAttendance.saveToFile(); // ✅ Persist
                            JOptionPane.showMessageDialog(frame, "✅ Checked in at " + inTime);
                            hasCheckedIn = true;
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame,
                                    "⚠ Invalid time format! Use HH:mm (e.g. 09:00)",
                                    "Invalid Input",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠ You already checked in today.");
                }
            }
        });

        // ✅ Check-out button logic
        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasCheckedIn && !hasCheckedOut) {
                    String outTime = JOptionPane.showInputDialog(frame, "Enter check-out time (e.g. 17:00):");
                    if (outTime != null && !outTime.trim().isEmpty()) {
                        try {
                            currentAttendance.checkOut(today, outTime.trim());
                            currentAttendance.saveToFile(); // ✅ Persist
                            JOptionPane.showMessageDialog(frame, "✅ Checked out at " + outTime);
                            hasCheckedOut = true;
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame,
                                    "⚠ Invalid time format! Use HH:mm (e.g. 17:00)",
                                    "Invalid Input",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (!hasCheckedIn) {
                    JOptionPane.showMessageDialog(frame, "⚠ You must check in first.");
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠ You already checked out today.");
                }
            }
        });

        // ✅ View Details
        viewDetailsButton.addActionListener(e -> {
            frame.setContentPane(new ViewDetails(frame, employee).getPanel());
            frame.revalidate();
        });

        // ✅ View Payslip
        viewPayslipButton.addActionListener(e -> {
            frame.setContentPane(new ViewPayslip_EmployeePage(frame, employee).getPanel());
            frame.revalidate();
        });

        // ✅ Logout
        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.revalidate();
        });
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
