import java.io.*;
import java.time.*;
import java.util.*;

public class Attendance {
    private static final String ATTENDANCE_FILE = "attendance.txt";
    private String username;
    private List<DailyAttendance> records;

    public Attendance(String username, String month, int year) {
        this.username = username;
        this.records = new ArrayList<>();

        YearMonth ym = YearMonth.of(year, Month.valueOf(month.toUpperCase()));
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, ym.getMonth(), day);
            if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                records.add(new DailyAttendance(date));
            }
        }
    }

    public DailyAttendance getRecordByDate(LocalDate date) {
        return records.stream().filter(r -> r.getDate().equals(date)).findFirst().orElse(null);
    }

    public void checkIn(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) record.checkIn(timeStr);
    }

    public void checkOut(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) record.checkOut(timeStr);
    }

    public int getPresentDays() {
        return (int) records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
    }

    public int getAbsentDays() {
        return records.size() - getPresentDays();
    }

    public int calculateMonthlyOvertime() {
        return records.stream().mapToInt(DailyAttendance::getOvertimeHours).sum();
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ATTENDANCE_FILE, true))) {
            for (DailyAttendance r : records) {
                writer.write(username + "," + r.getDate() + "," +
                        r.getStatus() + "," +
                        (r.getCheckIn() != null ? r.getCheckIn() : "null") + "," +
                        (r.getCheckOut() != null ? r.getCheckOut() : "null") + "," +
                        r.getOvertimeHours());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    public static Attendance loadFromFile(String username, String month, int year) {
        Attendance attendance = new Attendance(username, month, year);
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String fileUsername = parts[0];
                LocalDate date = LocalDate.parse(parts[1]);
                if (!fileUsername.equals(username) ||
                        !date.getMonth().toString().equalsIgnoreCase(month) ||
                        date.getYear() != year) {
                    continue;
                }

                AttendanceStatus status = AttendanceStatus.valueOf(parts[2]);
                LocalTime checkIn = parts[3].equals("null") ? null : LocalTime.parse(parts[3]);
                LocalTime checkOut = parts[4].equals("null") ? null : LocalTime.parse(parts[4]);
                int overtime = Integer.parseInt(parts[5]);

                DailyAttendance record = attendance.getRecordByDate(date);
                if (record != null && status == AttendanceStatus.PRESENT) {
                    record.checkIn(parts[3]);
                    record.checkOut(parts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
        }
        return attendance;
    }
}
