import java.io.*;
import java.util.*;

/**
 * Admin extends Users.  Each Admin has a name, contact, generated username/password, role="admin", and an age.
 */
public class Admin extends Users {
    private int age;

    /**
     * Constructor for creating a brand‐new Admin.
     * Automatically calls Users(name, contact, "admin") to generate username/password.
     */
    public Admin(String name, String contact, int age) throws IOException {
        super(name, contact, "admin");
        this.age = age;
    }

    /**
     * Constructor for rebuilding an Admin from file.
     * Parameters: (username, password, name, contact).
     * The age is stored in the details line, so setAge must be called after loading.
     */
    public Admin(String username, String password, String name, String contact) {
        super(username, password, name, contact, "admin");
    }

    /**
     * Save both login credentials and details.
     * In Users.java, saveToFiles() calls:
     *   appendToFile(USERS_FILE, toUserFileLine());
     *   appendToFile(DETAILS_FILE, toDetailsFileLine());
     */
    // No need to override saveToFiles()—inherit from Users

    /**
     * Defines how this Admin’s details are written to usersdetails.txt.
     * Format: username,name,contact,age,admin
     */
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

    /**
     * Displays the CLI menu for this Admin (view users, add employees/admins, remove, change password).
     */
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
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> addNewEmployee(scanner);
                case 3 -> addNewAdmin(scanner);
                case 4 -> removeUser(scanner);
                case 5 -> changePassword(scanner);
                case 6 -> System.out.println("Logging out...");
                default -> System.out.println("\u274C Invalid choice.");
            }
        } while (choice != 6);
    }

    private void addNewAdmin(Scanner scanner) {
        if (!isSuperAdmin()) {
            System.out.println("\u274C Only Super Admin can add new Admins.");
            return;
        }

        try {
            System.out.print("Enter Admin Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Admin Contact (e.g. +1234567890): ");
            String contact = scanner.nextLine();
            System.out.print("Enter Admin Age: ");
            int age = Integer.parseInt(scanner.nextLine());

            Admin newAdmin = new Admin(name, contact, age);
            newAdmin.saveToFiles();

            System.out.println("✅ Admin created:");
            System.out.println("Username: " + newAdmin.getUsername());
            System.out.println("Password: " + newAdmin.getPassword());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addNewEmployee(Scanner scanner) {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();

            System.out.print("Department (e.g. HR, IT, FINANCE): ");
            DepartmentName deptEnum = DepartmentName.valueOf(scanner.nextLine().trim().toUpperCase());
            Department dept = Department.getInstance(deptEnum);

            System.out.print("Age: ");
            String age = scanner.nextLine();

            System.out.print("Contact (e.g. +1234567890): ");
            String contact = scanner.nextLine();

            System.out.print("Salary: ");
            Double salary = scanner.nextDouble();
            scanner.nextLine();  // consume leftover newline

            Employee emp = new Employee(name, contact, dept);
            emp.setAge(age);
            emp.setBasicSalary(salary);
            emp.saveToFiles();

            System.out.println("\u2705 Employee created:");
            System.out.println("Username: " + emp.getUsername());
            System.out.println("Password: " + emp.getPassword());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            scanner.nextLine(); // clear buffer if error happens during nextDouble
        }
    }

    private void removeUser(Scanner scanner) {
        System.out.print("Enter username to remove: ");
        String target = scanner.nextLine();

        if (target.equals(this.getUsername())) {
            System.out.println("\u274C You cannot delete your own account.");
            return;
        }
        if (target.equals(getSuperAdminUsername())) {
            System.out.println("\u274C You cannot delete the Super Admin.");
            return;
        }
        if (removeLineFromFile(USERS_FILE, target) && removeLineFromFile(DETAILS_FILE, target)) {
            System.out.println("\u2705 Removed user: " + target);
        } else {
            System.out.println("\u274C User not found or error during removal.");
        }
    }

    private void changePassword(Scanner scanner) {
        System.out.print("Enter username to change password: ");
        String uname = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPass = scanner.nextLine();

        if (uname.equals(getSuperAdminUsername()) && !isSuperAdmin()) {
            System.out.println("\u274C Only Super Admin can change its own password.");
            return;
        }
        if (updatePassword(uname, newPass)) {
            System.out.println("\u2705 Password updated for " + uname);
        } else {
            System.out.println("\u274C Error updating password.");
        }
    }

    private void viewAllUsers() {
        try (
                BufferedReader userReader = new BufferedReader(new FileReader(USERS_FILE));
                BufferedReader detailReader = new BufferedReader(new FileReader(DETAILS_FILE))
        ) {
            System.out.printf("%-12s | %-20s | %-15s | %-10s | %-10s\n",
                    "Username", "Name", "Role", "Contact", "Extra Info");
            System.out.println("---------------------------------------------------------------");

            String userLine, detailLine;
            while ((userLine = userReader.readLine()) != null &&
                    (detailLine = detailReader.readLine()) != null) {
                String[] u = userLine.split(" ");
                String[] d = detailLine.split(",");
                if (u.length < 3 || d.length < 4) continue;  // ensure enough fields
                String username = u[0];
                String name     = d[1];
                String role     = u[2];
                String contact  = u[4]; // “contact” is the 4th index in Admin’s detail line
                String extra    = "Age: " + d[3]; // displayAdmin age in extra column

                System.out.printf("%-12s | %-20s | %-15s | %-10s | %-10s\n",
                        username, name, role, contact, extra);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    protected boolean isSuperAdmin() {
        return this.getUsername().equals(getSuperAdminUsername());
    }

    private String getSuperAdminUsername() {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.split(" ");
                return parts[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading super admin: " + e.getMessage());
        }
        return "";
    }

    private boolean removeLineFromFile(String fileName, String username) {
        File inputFile = new File(fileName);
        File tempFile = new File("temp.txt");
        boolean found = false;

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(username + ",") && !line.startsWith(username + " ")) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return false;
        }

        inputFile.delete();
        return tempFile.renameTo(inputFile) && found;
    }

    private boolean updatePassword(String username, String newPassword) {
        File inputFile = new File(USERS_FILE);
        File tempFile  = new File("temp_users.txt");

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
}
