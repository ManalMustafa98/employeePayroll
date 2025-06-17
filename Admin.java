import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Admin extends Users {
    private int age;

    public Admin(String name, String contact, int age) throws IOException {
        super(name, contact, "admin");
        if (!isValidAge(age)) {
            throw new IllegalArgumentException("Age must be between 1 and 149.");
        }
        this.age = age;
    }

    public Admin(String username, String password, String name, String contact) {
        super(username, password, name, contact, "admin");
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age < 150;
    }

    @Override
    public String toDetailsFileLine() {
        return String.join(",",
                getUsername(),
                getName(),
                getContact(),
                String.valueOf(age),
                "admin"
        );
    }

    protected boolean isSuperAdmin() {
        return this.getUsername().equals(getSuperAdminUsername());
    }

    private String getSuperAdminUsername() {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                return firstLine.split(" ")[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading super admin: " + e.getMessage());
        }
        return "";
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. View All Users");
            System.out.println("2. Add New Employee");
            System.out.println("3. Add New Admin");
            System.out.println("4. Remove User");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> addNewEmployee(scanner);
                case 3 -> addNewAdmin(scanner);
                case 4 -> System.out.println("Logging out...");
                default -> System.out.println("❌ Invalid choice.");
            }
        } while (choice != 6);
    }

    private void addNewAdmin(Scanner scanner) {
        if (!isSuperAdmin()) {
            System.out.println("❌ Only Super Admin can add new Admins.");
            return;
        }

        try {
            System.out.print("Enter Admin Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Contact (+92...): ");
            String contact = scanner.nextLine();
            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            Admin newAdmin = new Admin(name, contact, age);
            newAdmin.saveToFiles();

            System.out.println("✅ Admin Created:");
            System.out.println("Username: " + newAdmin.getUsername());
            System.out.println("Password: " + newAdmin.getPassword());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void addNewEmployee(Scanner scanner) {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Department (HR, IT, FINANCE...): ");
            DepartmentName deptEnum = DepartmentName.valueOf(scanner.nextLine().trim().toUpperCase());
            Department dept = Department.getInstance(deptEnum);

            System.out.print("Age: ");
            int age = scanner.nextInt();

            System.out.print("Contact (+92...): ");
            scanner.nextLine();
            String contact = scanner.nextLine();

            System.out.print("Salary: ");
            double salary = scanner.nextDouble();
            scanner.nextLine();

            Employee emp = new Employee(name, contact, dept);
            emp.setAge(age);
            emp.setBasicSalary(salary);
            emp.saveToFiles();

            System.out.println("✅ Employee Created:");
            System.out.println("Username: " + emp.getUsername());
            System.out.println("Password: " + emp.getPassword());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            scanner.nextLine();
        }
    }

    public void removeEmployee(String username) {
        try {
            if (username.equals(this.getUsername())) {
                System.out.println("❌ You cannot delete your own account.");
                return;
            }

            String role = getUserRole(username);
            if (!"employee".equals(role)) {
                System.out.println("❌ User is not an employee.");
                return;
            }

            boolean success = removeLineFromFile(USERS_FILE, username) &&
                    removeLineFromFile(DETAILS_FILE, username);

            if (success) {
                System.out.println("✅ Employee removed: " + username);
            } else {
                System.out.println("❌ Failed to remove employee.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public void removeAdmin(String username) {
        try {
            if (username.equals(this.getUsername())) {
                System.out.println("❌ You can't remove your own account.");
                return;
            }

            String role = getUserRole(username);
            if (!"admin".equals(role)) {
                System.out.println("❌ User is not an admin.");
                return;
            }

            boolean success = removeLineFromFile(USERS_FILE, username) &&
                    removeLineFromFile(DETAILS_FILE, username);

            if (success) {
                System.out.println("✅ Admin removed: " + username);
            } else {
                System.out.println("❌ Failed to remove admin.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private boolean removeLineFromFile(String filename, String username) {
        File input = new File(filename);
        File temp = new File("temp_" + filename);

        boolean found = false;

        try (
                BufferedReader reader = new BufferedReader(new FileReader(input));
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                String firstToken = trimmed.split("[ ,]")[0];

                if (!firstToken.equals(username)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error while removing: " + e.getMessage());
            return false;
        }

        if (input.delete()) {
            return temp.renameTo(input) && found;
        } else {
            System.out.println("❌ Failed to delete original file: " + filename);
            return false;
        }
    }

    private boolean updatePassword(String username, String newPassword) {
        File inputFile = new File(USERS_FILE);
        File tempFile = new File("temp_users.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(username)) {
                    writer.write(username + " " + newPassword + " " + parts[2]);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            return false;
        }

        inputFile.delete();
        return tempFile.renameTo(inputFile);
    }

    public static String getUserRole(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return parts[2];
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading user role: " + e.getMessage());
        }
        return null;
    }

    private void viewAllUsers() {
        try (
                BufferedReader userReader = new BufferedReader(new FileReader(USERS_FILE));
                BufferedReader detailReader = new BufferedReader(new FileReader(DETAILS_FILE))
        ) {
            System.out.printf("%-12s | %-20s | %-10s | %-12s | %-10s\n",
                    "Username", "Name", "Role", "Contact", "Extra");
            System.out.println("--------------------------------------------------------");

            String userLine, detailLine;
            while ((userLine = userReader.readLine()) != null &&
                    (detailLine = detailReader.readLine()) != null) {

                String[] u = userLine.split(" ");
                String[] d = detailLine.split(",");

                if (u.length >= 3 && d.length >= 4) {
                    String uname = u[0];
                    String role = u[2];
                    String name = d[1];
                    String contact = d[2];
                    String extra = d.length > 5 ? "Salary: " + d[5] : "Age: " + d[3];

                    System.out.printf("%-12s | %-20s | %-10s | %-12s | %-10s\n",
                            uname, name, role, contact, extra);
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
