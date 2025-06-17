import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Attendance {
    protected static final String ATTENDANCE_FILE = "attendance.txt";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String NULL_TOKEN = "null";

    private final String username;
    private final YearMonth yearMonth;
    private final List<DailyAttendance> records;

    public Attendance(String username, String month, int year) {
        this.username = username;
        this.yearMonth = YearMonth.of(year, Month.valueOf(month.toUpperCase()));
        this.records = new ArrayList<>();
        initRecords();
    }

    private void initRecords() {
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            DayOfWeek dow = date.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                records.add(new DailyAttendance(date));
            }
        }
    }

    public DailyAttendance getRecordByDate(LocalDate date) {
        for (DailyAttendance r : records) {
            if (r.getDate().equals(date)) {
                return r;
            }
        }
        return null;
    }

    public DailyAttendance getRecord(LocalDate date) {
        return getRecordByDate(date);
    }

    public void checkIn(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) {
            record.checkIn(timeStr);
            saveToFile(); // ✅ Immediately save
        }
    }

    public void checkOut(LocalDate date, String timeStr) {
        DailyAttendance record = getRecordByDate(date);
        if (record != null) {
            record.checkOut(timeStr);
            saveToFile(); // ✅ Immediately save
        }
    }

    public int getPresentDays() {
        int count = 0;
        for (DailyAttendance r : records) {
            if (r.getStatus() == AttendanceStatus.PRESENT) {
                count++;
            }
        }
        return count;
    }

    public int getAbsentDays() {
        return records.size() - getPresentDays();
    }

    public int calculateMonthlyOvertime() {
        int sum = 0;
        for (DailyAttendance r : records) {
            sum += r.getOvertimeHours();
        }
        return sum;
    }

    public void saveToFile() {
        File file = new File(ATTENDANCE_FILE);
        List<String> existingLines = new ArrayList<>();

        // 1) Load existing content
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    existingLines.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading attendance file: " + e.getMessage());
            }
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 2) Go through all relevant records
        for (DailyAttendance r : records) {
            LocalDate recordDate = r.getDate();

            // ✅ Auto-mark absent if it's today, after 2PM, and not already checked in
            if (recordDate.equals(today) && r.getCheckIn() == null && r.getStatus() == AttendanceStatus.ABSENT) {
                if (now.isAfter(LocalTime.of(14, 0))) {
                    r.setStatus(AttendanceStatus.ABSENT);
                }
            }

            // ✅ Skip empty records (not today, still default and nulls)
            if (!recordDate.equals(today) &&
                    r.getStatus() == AttendanceStatus.ABSENT &&
                    r.getCheckIn() == null &&
                    r.getCheckOut() == null &&
                    r.getOvertimeHours() == 0) {
                continue; // Don’t save purely empty rows
            }

            // ✅ Remove previous lines for same user+date
            existingLines.removeIf(line -> {
                String[] parts = line.split(",");
                if (parts.length != 6) return false;
                String fileUser = parts[0];
                LocalDate fileDate = LocalDate.parse(parts[1], DATE_FMT);
                return fileUser.equals(username) && fileDate.equals(recordDate);
            });

            // ✅ Write new line
            String dateStr = recordDate.format(DATE_FMT);
            String status  = r.getStatus().name();
            String in      = (r.getCheckIn()  != null) ? r.getCheckIn().format(TIME_FMT)  : NULL_TOKEN;
            String out     = (r.getCheckOut() != null) ? r.getCheckOut().format(TIME_FMT) : NULL_TOKEN;
            String ot      = String.valueOf(r.getOvertimeHours());

            String newLine = String.join(",", username, dateStr, status, in, out, ot);
            existingLines.add(newLine);
        }

        // 3) Write back to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : existingLines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    public static Attendance loadFromFile(String username, String month, int year) {
        Attendance attendance = new Attendance(username, month, year);
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) return attendance;

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

                AttendanceStatus status = AttendanceStatus.valueOf(parts[2]);
                LocalTime checkIn  = parts[3].equals(NULL_TOKEN) ? null : LocalTime.parse(parts[3], TIME_FMT);
                LocalTime checkOut = parts[4].equals(NULL_TOKEN) ? null : LocalTime.parse(parts[4], TIME_FMT);
                int overtime = Integer.parseInt(parts[5]);

                DailyAttendance rec = attendance.getRecordByDate(date);
                if (rec != null) {
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
