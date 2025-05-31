import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a single day’s attendance record.
 */
public class DailyAttendance {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalDate date;
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

    public LocalDate getDate() {
        return date;
    }
    public AttendanceStatus getStatus() {
        return status;
    }
    public LocalTime getCheckIn() {
        return checkIn;
    }
    public LocalTime getCheckOut() {
        return checkOut;
    }
    public int getOvertimeHours() {
        return overtimeHours;
    }

    /**
     * Mark check-in from a string “HH:mm”. Updates status to PRESENT.
     */
    public void checkIn(String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr, TIME_FMT);
            checkIn(time);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid check-in time format: " + timeStr, ex);
        }
    }

    /**
     * Mark check-out from a string “HH:mm”. Calculates overtime if after 17:00.
     */
    public void checkOut(String timeStr) {
        try {
            LocalTime time = LocalTime.parse(timeStr, TIME_FMT);
            checkOut(time);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid check-out time format: " + timeStr, ex);
        }
    }

    /**
     * Package-private setter used when loading from file.
     * Sets status directly (PRESENT or ABSENT).
     */
    void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    /**
     * Package-private setter used when loading from file.
     * Sets check-in time (must also mark PRESENCE).
     */
    void setCheckIn(LocalTime time) {
        this.checkIn = time;
        if (time != null) {
            this.status = AttendanceStatus.PRESENT;
        }
    }

    /**
     * Package-private setter used when loading from file.
     * Sets check-out time and recalculates overtime.
     */
    void setCheckOut(LocalTime time) {
        this.checkOut = time;
        if (time != null) {
            LocalTime standardOut = LocalTime.of(17, 0);
            if (time.isAfter(standardOut)) {
                this.overtimeHours = (int) java.time.Duration.between(standardOut, time).toHours();
            }
        }
    }

    /**
     * Package-private setter used when loading from file.
     */
    void setOvertimeHours(int hours) {
        this.overtimeHours = hours;
    }

    /**
     * Convenience: mark check-in directly from a LocalTime,
     * updating status to PRESENT.
     */
    public void checkIn(LocalTime time) {
        this.checkIn = time;
        this.status = AttendanceStatus.PRESENT;
    }

    /**
     * Convenience: mark check-out directly from a LocalTime,
     * computing overtime against 17:00.
     */
    public void checkOut(LocalTime time) {
        this.checkOut = time;
        LocalTime standardOut = LocalTime.of(17, 0);
        if (time.isAfter(standardOut)) {
            this.overtimeHours = (int) java.time.Duration.between(standardOut, time).toHours();
        }
    }
}

enum AttendanceStatus {
    PRESENT,
    ABSENT
}
