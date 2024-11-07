import javax.swing.*;
import java.awt.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Login_GUI {
    public Login_GUI() {
        // Set up JFrame
        JFrame frame = new JFrame("Login");
        frame.setSize(600, 600);
        GridBagLayout gbl = new GridBagLayout();
        frame.setLayout(gbl);

        GridBagConstraints gbc = new GridBagConstraints();

        // Username Field
        JLabel unameLabel = new JLabel("UserName: ");
        JTextField uname = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(unameLabel, gbc);
        gbc.gridx++;
        frame.add(uname, gbc);

        // Password Field
        JLabel passLabel = new JLabel("Password: ");
        JPasswordField pass = new JPasswordField(20);
        gbc.gridy++;
        gbc.gridx = 0;
        frame.add(passLabel, gbc);
        gbc.gridx++;
        frame.add(pass, gbc);

        // Submit Button
        JButton submit = new JButton("Submit");
        gbc.gridy++;
        gbc.gridx = 0;
        frame.add(submit, gbc);

        // Warning Label
        JLabel warning = new JLabel("Invalid Username or Password!!!!");
        warning.setVisible(false);
        warning.setForeground(Color.RED);
        gbc.gridx++;
        frame.add(warning, gbc);

        // Submit Button Action
        submit.addActionListener(e -> {
            String unameString = uname.getText().trim();
            String passString = new String(pass.getPassword());

            // Connect to the database
            DB_Functions dbfunc = new DB_Functions();
            try (Connection conn = dbfunc.connect_to_db("DormNest", "postgres", "root")) {
                // Prepare the query to prevent SQL injection
                String query = "SELECT password FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, unameString); // Set the username parameter
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String storedHash = rs.getString("password");

                        // Check if the password matches the stored hash
                        if (verifyPassword(passString, storedHash)) {
                            // Login successful
                            JOptionPane.showMessageDialog(frame, "Login Successful!");
                            // Proceed to Student Dashboard
//                            Student_Dashboard_GUI dashBoard = new Student_Dashboard_GUI();
                        } else {
                            warning.setVisible(true);
                        }
                    } else {
                        warning.setVisible(true);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    // Method to hash passwords with PBKDF2 (and safely store salt and hash)
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256; // Key length for the hash
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    // Method to hash a password
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Generate hash using PBEKeySpec and SecretKeyFactory
        byte[] hash = generateHash(password.toCharArray(), salt);

        // Encode salt and hash in Base64 for storage
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        // Combine salt and hash into a single string
        return saltBase64 + ":" + hashBase64;
    }

    // Method to verify the entered password against the stored hash
// Modify the verifyPassword method to use SHA-256 for comparison
    public static boolean verifyPassword(String enteredPassword, String storedHash) {
        try {
            // Directly compare the SHA-256 hash (no salt or iterations)
            String enteredHash = hashPassword(enteredPassword);
            boolean flag = enteredHash.equals(storedHash)?true:false;
            if(!flag) System.out.println("Password Dont match");
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // Helper method to generate hash
    private static byte[] generateHash(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new);
    }
}
