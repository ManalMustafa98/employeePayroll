import java.time.LocalDate;
import java.time.LocalTime;

public class DailyAttendance {
    private LocalDate date;
    private AttendanceStatus status;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private int overtimeHours;

    public DailyAttendance(LocalDate date) {
        this.date = date;
        this.status = AttendanceStatus.ABSENT;
        this.checkIn = null;
        this.checkOut = null;
        this.overtimeHours = 0;
    }

    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public LocalTime getCheckIn() { return checkIn; }
    public LocalTime getCheckOut() { return checkOut; }
    public int getOvertimeHours() { return overtimeHours; }

    public void checkIn(String timeStr) {
        this.checkIn = LocalTime.parse(timeStr);
        this.status = AttendanceStatus.PRESENT;
    }

    public void checkOut(String timeStr) {
        this.checkOut = LocalTime.parse(timeStr);
        LocalTime standardOut = LocalTime.of(17, 0);
        if (checkOut.isAfter(standardOut)) {
            this.overtimeHours = (int) java.time.Duration.between(standardOut, checkOut).toHours();
        }
    }
}
enum AttendanceStatus {
    PRESENT, ABSENT
}

