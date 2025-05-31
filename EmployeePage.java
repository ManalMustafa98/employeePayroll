import javax.swing.*;
import java.time.LocalDate;

public class EmployeePage {
    private JPanel rootPanel;
    private JLabel welcomeLabel;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JButton viewDetailsButton;
    private JButton viewPayslipButton;
    private JButton logoutButton;

    private final Employee employee;
    private final Attendance attendance;
    private boolean hasCheckedIn;
    private boolean hasCheckedOut;

    public EmployeePage(JFrame frame, Employee employee) {
        this.employee   = employee;
        LocalDate today = LocalDate.now();
        String month    = today.getMonth().toString();
        int year        = today.getYear();

        // Load and inspect today’s record
        attendance     = Attendance.loadFromFile(employee.getUsername(), month, year);
        DailyAttendance record = attendance.getRecordByDate(today);
        if (record != null) {
            hasCheckedIn  = (record.getCheckIn()  != null);
            hasCheckedOut = (record.getCheckOut() != null);
        }

        // Set welcome text
        welcomeLabel.setText("Welcome, " + employee.getName() + "!");

        // Initialize buttons
        checkInButton.setEnabled(!hasCheckedIn);
        checkOutButton.setEnabled(hasCheckedIn && !hasCheckedOut);

        // Wire actions
        checkInButton.addActionListener(e -> handleCheckIn(frame));
        checkOutButton.addActionListener(e -> handleCheckOut(frame));

        viewDetailsButton.addActionListener(e -> {
            frame.setContentPane(new ViewDetails(frame, employee).getPanel());

            frame.pack(); frame.revalidate();
        });

        viewPayslipButton.addActionListener(e -> {
            frame.setContentPane(new ViewPayslip_EmployeePage(frame, employee).getPanel());
            frame.pack(); frame.revalidate();
        });

        logoutButton.addActionListener(e -> {
            frame.setContentPane(new WelcomePage(frame).getPanel());
            frame.pack(); frame.revalidate();
        });
    }

    private void handleCheckIn(JFrame frame) {
        LocalDate today = LocalDate.now();
        String inTime = JOptionPane.showInputDialog(
                frame,
                "Enter check-in time (HH:mm):",
                "Check In",
                JOptionPane.QUESTION_MESSAGE
        );
        if (inTime != null && !inTime.isBlank()) {
            attendance.checkIn(today, inTime.trim());
            attendance.saveToFile();
            hasCheckedIn = true;
            checkInButton.setEnabled(false);
            checkOutButton.setEnabled(true);
            JOptionPane.showMessageDialog(
                    frame,
                    "✅ Checked in at " + inTime,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void handleCheckOut(JFrame frame) {
        LocalDate today = LocalDate.now();
        String outTime = JOptionPane.showInputDialog(
                frame,
                "Enter check-out time (HH:mm):",
                "Check Out",
                JOptionPane.QUESTION_MESSAGE
        );
        if (outTime != null && !outTime.isBlank()) {
            attendance.checkOut(today, outTime.trim());
            attendance.saveToFile();
            hasCheckedOut = true;
            checkOutButton.setEnabled(false);
            JOptionPane.showMessageDialog(
                    frame,
                    "✅ Checked out at " + outTime,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public JPanel getPanel() {
        return rootPanel;
    }
}
