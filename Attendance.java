import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Attendance {
    private static final String ATTENDANCE_FILE = "attendance.txt";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String NULL_TOKEN = "null";

    private final String username;
    private final YearMonth yearMonth;
    private final List<DailyAttendance> records;

    /**
     * Constructor: builds a list of DailyAttendance for every weekday in the given month.
     */
    public Attendance(String username, String month, int year) {
        this.username  = username;
        this.yearMonth = YearMonth.of(year, Month.valueOf(month.toUpperCase()));
        this.records   = new ArrayList<>();
        initRecords();
    }

    // Helper: populate 'records' with all weekdays for this yearMonth
    private void initRecords() {
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                records.add(new DailyAttendance(date));
            }
        }
    }

    /**
     * Find the DailyAttendance record for the exact date, or null if not a weekday in this month.
     */
    public DailyAttendance getRecordByDate(LocalDate date) {
        for (DailyAttendance r : records) {
            if (r.getDate().equals(date)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Mark check-in for a given date (must be a weekday in this month).
     * Does NOT itself save—to persist, GUI should call saveToFile() immediately afterward.
     */
    public void checkIn(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) {
            record.checkIn(timeStr);
        }
    }

    /**
     * Mark check-out for a given date (must be a weekday in this month).
     * Does NOT itself save—to persist, GUI should call saveToFile() immediately afterward.
     */
    public void checkOut(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) {
            record.checkOut(timeStr);
        }
    }

    /** Count how many days are marked PRESENT in this month. */
    public int getPresentDays() {
        int count = 0;
        for (DailyAttendance r : records) {
            if (r.getStatus() == AttendanceStatus.PRESENT) {
                count++;
            }
        }
        return count;
    }

    /** Total days in this month minus present days = absent days. */
    public int getAbsentDays() {
        return records.size() - getPresentDays();
    }

    /** Sum of overtimeHours for all records in this month. */
    public int calculateMonthlyOvertime() {
        int sum = 0;
        for (DailyAttendance r : records) {
            sum += r.getOvertimeHours();
        }
        return sum;
    }

    /**
     * Persist current month's attendance for this user.
     * Overwrites attendance.txt by:
     *  1) Reading all existing lines
     *  2) Filtering out any lines for this.username & this.yearMonth
     *  3) Appending each of this.month's records
     *  4) Writing back in one shot
     */
    public void saveToFile() {
        File file = new File(ATTENDANCE_FILE);
        List<String> buffer = new ArrayList<>();

        // 1) Read existing lines, if file exists
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length != 6) {
                        // Keep malformed or unrelated lines
                        buffer.add(line);
                        continue;
                    }
                    String fileUser = parts[0];
                    LocalDate date = LocalDate.parse(parts[1], DATE_FMT);
                    YearMonth ym = YearMonth.from(date);

                    // 2) Filter out lines for this user-month
                    if (!(fileUser.equals(username) && ym.equals(yearMonth))) {
                        buffer.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading existing attendance file: " + e.getMessage());
                // Proceed to overwrite anyway
            }
        }

        // 3) Append current records for this user-month
        for (DailyAttendance r : records) {
            String dateStr = r.getDate().format(DATE_FMT);
            String status  = r.getStatus().name();
            String in      = (r.getCheckIn()  != null) ? r.getCheckIn().format(TIME_FMT)  : NULL_TOKEN;
            String out     = (r.getCheckOut() != null) ? r.getCheckOut().format(TIME_FMT) : NULL_TOKEN;
            String ot      = String.valueOf(r.getOvertimeHours());

            String newLine = String.join(",", username, dateStr, status, in, out, ot);
            buffer.add(newLine);
        }

        // 4) Write entire buffer back (overwrite mode)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : buffer) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    /**
     * Load attendance from file, merging lines for this user-month into a fresh Attendance object.
     * This fully sets status, checkIn, checkOut, and overtimeHours on each relevant DailyAttendance.
     */
    public static Attendance loadFromFile(String username, String month, int year) {
        Attendance attendance = new Attendance(username, month, year);
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) {
            return attendance; // No file → all records remain default (ABSENT, no times, zero OT)
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String fileUser = parts[0];
                if (!fileUser.equals(username)) continue;

                LocalDate date = LocalDate.parse(parts[1], DATE_FMT);
                YearMonth ym = YearMonth.from(date);
                if (!ym.equals(attendance.yearMonth)) continue;

                // Parse fields
                AttendanceStatus status = AttendanceStatus.valueOf(parts[2]);
                LocalTime checkIn  = parts[3].equals(NULL_TOKEN) ? null : LocalTime.parse(parts[3], TIME_FMT);
                LocalTime checkOut = parts[4].equals(NULL_TOKEN) ? null : LocalTime.parse(parts[4], TIME_FMT);
                int overtime = Integer.parseInt(parts[5]);

                // Populate DailyAttendance
                DailyAttendance rec = attendance.getRecordByDate(date);
                if (rec != null) {
                    // Use package-private setters (you must add these to DailyAttendance)
                    rec.setStatus(status);
                    rec.setCheckIn(checkIn);
                    rec.setCheckOut(checkOut);
                    rec.setOvertimeHours(overtime);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
        }

        return attendance;
    }
}
