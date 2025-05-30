import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class EmployeePage {
    private JPanel panel1;
    private JLabel welcomeLabel;
    private JButton CHECKINButton;
    private JButton CHECKOUTButton;
    private JButton VIEWPAYSLIPButton;
    private JButton LOGOUTButton;
    private JButton VIEWDETAILSButton;

    private Employee currentEmployee;
    private Attendance currentAttendance;
    private boolean hasCheckedIn = false;
    private boolean hasCheckedOut = false;

    public EmployeePage(JFrame frame, Employee employee) {
        this.currentEmployee = employee;
        LocalDate today = LocalDate.now();
        String month = today.getMonth().toString();
        int year = today.getYear();

//        welcomeLabel.setText("Welcome, " + employee.getName() + "!");
//        currentAttendance = Attendance.loadFromFile(employee.getUsername(), month, year);

        CHECKINButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!hasCheckedIn) {
                    String inTime = JOptionPane.showInputDialog(frame, "Enter check-in time (e.g. 09:00):");
                    if (inTime != null && !inTime.trim().isEmpty()) {
                        currentAttendance.checkIn(LocalDate.now(), inTime.trim());
                        JOptionPane.showMessageDialog(frame, "✅ Checked in at " + inTime);
                        hasCheckedIn = true;
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠️ You already checked in today.");
                }
            }
        });

        CHECKOUTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hasCheckedIn && !hasCheckedOut) {
                    String outTime = JOptionPane.showInputDialog(frame, "Enter check-out time (e.g. 17:00):");
                    if (outTime != null && !outTime.trim().isEmpty()) {
                        currentAttendance.checkOut(LocalDate.now(), outTime.trim());
                        currentAttendance.saveToFile();
                        JOptionPane.showMessageDialog(frame, "✅ Checked out at " + outTime);
                        hasCheckedOut = true;
                    }
                } else if (!hasCheckedIn) {
                    JOptionPane.showMessageDialog(frame, "⚠️ You must check in first.");
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠️ You already checked out today.");
                }
            }
        });



        LOGOUTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new WelcomePage(frame).getPanel());
                frame.revalidate();
            }
        });

        VIEWPAYSLIPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ViewPayslip_EmployeePage(frame, currentEmployee).getPanel() );
                frame.revalidate();
            }
        });

        VIEWPAYSLIPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ViewDetails(frame, currentEmployee).getPanel());
                frame.revalidate();
            }
        });
    }

    public JPanel getPanel() {
        return panel1;
    }
}
