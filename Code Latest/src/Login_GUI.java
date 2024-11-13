import javax.swing.*;
import java.awt.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.security.*;
import java.util.Base64;

public class Login_GUI {
    public Login_GUI() {
        // Set up JFrame
        JFrame frame = new JFrame("Login");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        // System.err.println("Full-screen mode is not supported.");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize if full-screen not supported

        // Define colors
        Color backgroundColor = Color.decode("#8aaba5");
        Color inputBoxColor = Color.decode("#FFFFFF");
        Color buttonColor = Color.decode("#809c97");

        // Set background color
        frame.getContentPane().setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Field
        JLabel unameLabel = new JLabel("Username: ");
        unameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(unameLabel, gbc);

        JTextField uname = new JTextField(20);
        uname.setBackground(inputBoxColor);
        uname.setPreferredSize(new Dimension(200, 30)); // Increase field size
        gbc.gridx = 1;
        frame.add(uname, gbc);

        // Password Field
        JLabel passLabel = new JLabel("Password: ");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy++;
        gbc.gridx = 0;
        frame.add(passLabel, gbc);

        JPasswordField pass = new JPasswordField(20);
        pass.setBackground(inputBoxColor);
        pass.setPreferredSize(new Dimension(200, 30)); // Increase field size
        gbc.gridx = 1;
        frame.add(pass, gbc);

        // Warning Label
        JLabel warning = new JLabel("Invalid Username or Password!");
        warning.setVisible(false);
        warning.setForeground(Color.RED);
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(warning, gbc);

        // Submit Button
        JButton submit = new JButton("Submit");
        submit.setBackground(buttonColor);
        submit.setPreferredSize(new Dimension(100, 40));
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center-align the button
        frame.add(submit, gbc);

        // Submit Button Action
        submit.addActionListener(e -> {
            String unameString = uname.getText().trim();
            String passString = new String(pass.getPassword());

            // Connect to the database
            DB_Functions dbfunc = new DB_Functions();
            try (Connection conn = dbfunc.connect_to_db()) {
                // Prepare the query to prevent SQL injection
                String query = "SELECT user_id,password,user_type FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, unameString);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        String userType= rs.getString("user_type");
                        int userID = rs.getInt("user_id");
                        // Check if the password matches the stored hash
                        if (verifyPassword(passString, storedHash)) {
                            JOptionPane.showMessageDialog(frame, "Login Successful!");
                            if(userType.equals("owner")) new OwnerDashboard(userID);
                            else new StudentHomePageGUI(userID);

                            frame.dispose();
                            // Proceed to Student Dashboard
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

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String enteredPassword, String storedHash) {
        try {
            String enteredHash = hashPassword(enteredPassword);
            return enteredHash.equals(storedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new);
    }
}
