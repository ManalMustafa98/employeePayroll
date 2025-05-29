import java.io.*;
import java.util.*;

public class Admin extends Users {

    // Constructor for new admin creation (no department)
    public Admin(String name, String contact) throws IOException {
        super(name, contact, "admin");
    }

    // Constructor for loading from file
    public Admin(String username, String password, String name, String contact) {
        super(username, password, name, contact, "admin");
    }

    @Override
    public void saveToFiles() throws IOException {
        appendToFile(USERS_FILE, toUserFileLine());
        appendToFile(DETAILS_FILE, toDetailsFileLine());
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

            Admin newAdmin = new Admin(name, contact);
            newAdmin.saveToFiles();

            System.out.println(" Admin created:");
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
            System.out.print("Department: ");
            Department dept = new Department(DepartmentName.valueOf(scanner.nextLine()));
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
            while ((userLine = userReader.readLine()) != null && (detailLine = detailReader.readLine()) != null) {
                String[] u = userLine.split(" ");
                String[] d = detailLine.split(",");
                if (u.length < 3 || d.length < 3) continue;
                String username = u[0];
                String name = d[1];
                String role = u[2];
                String contact = d[2];
                String extra = ""; // Can be extended, e.g., Employee salary or department if needed

                System.out.printf("%-12s | %-20s | %-15s | %-10s | %-10s\n",
                        username, name, role, contact, extra);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean isSuperAdmin() {
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
}
