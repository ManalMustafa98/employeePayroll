import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public abstract class Users {
    protected String name;
    protected String contact;      // replacing address with contact
    protected String username;
    protected String password;
    protected String role;

    protected static final String USERS_FILE = "users.txt";
    protected static final String DETAILS_FILE = "usersdetails.txt";

    // Constructor for new user creation - Admin will override this if needed for department removal
    public Users(String name, String contact, String role) throws IOException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces.");
        }
        if (!isValidContact(contact)) {
            throw new IllegalArgumentException("Invalid contact number format.");
        }

        this.name = name.trim();
        this.contact = contact.trim();
        this.role = role.trim().toLowerCase();

        do {
            this.username = generateUsername();
        } while (isUsernameTaken(this.username));

        this.password = generatePassword();
    }

    // Constructor for rebuilding from file (used in loading)
    public Users(String username, String password, String name, String contact, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.role = role;
    }

    public abstract void saveToFiles() throws IOException;

    // Validation for name: letters and spaces only
    public static boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z\\s]+$", name);
    }

    // Validation for contact (example: must be digits and 10-15 chars)
    public static boolean isValidContact(String contact) {
        return Pattern.matches("^\\+?[0-9]{10,15}$", contact);
    }

    // Username generation: first name + random number
    public String generateUsername() {
        String base = name.toLowerCase().split(" ")[0];
        int rand = new Random().nextInt(90) + 10;
        return base + rand;
    }

    // Password generation: 8 chars alphanumeric
    public String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);
        Random r = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Check username existence in USERS_FILE
    public boolean isUsernameTaken(String username) throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + " ")) return true;
            }
        }
        return false;
    }

    protected void appendToFile(String filename, String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    // User file line format: username password role
    public String toUserFileLine() {
        return username + " " + password + " " + role;
    }

    // Details file line format: username,name,contact
    public String toDetailsFileLine() {
        return username + "," + name + "," + contact;
    }

    // Getters
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
