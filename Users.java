import java.io.*;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.Random;

public abstract class Users {
    protected String name;
    protected String contact;
    protected String username;
    protected String password;
    protected String role;

    protected static final String USERS_FILE   = "users.txt";
    protected static final String DETAILS_FILE = "usersdetails.txt";

    /**
     * Constructor for new user creation.
     * Automatically generates a unique username and a random password.
     */
    public Users(String name, String contact, String role) throws IOException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces.");
        }
        if (!isValidContact(contact)) {
            throw new IllegalArgumentException("Invalid contact number format.");
        }

        this.name    = name.trim();
        this.contact = contact.trim();
        this.role    = role.trim().toLowerCase();

        // Generate a username until we find one that isn't taken
        do {
            this.username = generateUsername();
        } while (isUsernameTaken(this.username));

        this.password = generatePassword();
    }

    /**
     * Constructor for rebuilding from file (used in loading).
     */
    public Users(String username, String password, String name, String contact, String role) {
        this.username = username;
        this.password = password;
        this.name     = name;
        this.contact  = contact;
        this.role     = role;
    }

    /**
     * Each subclass must implement this to provide its own details‐file line.
     * For example, Employee will output:
     *   username,name,department,age,contact,salary
     * Admin might output:
     *   username,name,contact,age,admin
     */

    // THIS IS THE OVERLOADED METHOD FOR SIR REFERNECE
    public abstract String toDetailsFileLine();

    /** Save login/role to users.txt and subclass details to usersdetails.txt */
    public void saveToFiles() throws IOException {
        appendToFile(USERS_FILE, toUserFileLine());
        appendToFile(DETAILS_FILE, toDetailsFileLine());
    }

    /**
     * Validate that a name contains only letters and spaces.
     */
    public static boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z\\s]+$", name);
    }

    /**
     * Validate that a contact string is + optional, followed by 10–15 digits.
     */
    public static boolean isValidContact(String contact) {
        return Pattern.matches("^\\+?[0-9]{10,15}$", contact);
    }

    /**
     * Generate a username by taking the first word of the name (lowercased)
     * and appending a random two‐digit number (10–99).
     */
    public String generateUsername() {
        String base = name.toLowerCase().split(" ")[0];
        int rand = new Random().nextInt(90) + 10;  // random between 10 and 99
        return base + rand;
    }

    /**
     * Generate a random 8‐character alphanumeric password.
     */
    public String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";
        StringBuilder sb = new StringBuilder(8);
        Random r = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Check if a username already exists in users.txt. Splits each line on spaces
     * and compares the first token to avoid prefix‐matching errors.
     */
    public boolean isUsernameTaken(String username) throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Append a single line to the given filename (creates file if it doesn’t exist).
     */
    protected void appendToFile(String filename, String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    /**
     * Defines how user credentials are stored in users.txt:
     *   username password role
     */
    public static String getUserRole(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return parts[2]; // returns the role
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading user role: " + e.getMessage());
        }
        return null; // not found
    }

    public String toUserFileLine() {
        return username + " " + password + " " + role;
    }

    // Getters
    public String getName()     { return name; }
    public String getContact()  { return contact; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }
}
