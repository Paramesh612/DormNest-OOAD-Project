import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class RegistrationForm_GUI extends JFrame {
    protected JTextField firstNameField;
    protected JTextField lastNameField;
    protected JTextField userNameField;
    protected JTextField emailField;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JRadioButton studentRadioButton;
    protected JRadioButton ownerRadioButton;

    public RegistrationForm_GUI() {

        UIManager.put("Label.font", new Font("Arial", Font.BOLD, 16));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 16));

        setTitle("Register");
        // setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize if full-screen not supported

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#8aaba5"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Registration Form", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("User Name:"), gbc);
        userNameField = new JTextField(15);
        // userNameField.setBackground(Color.decode("#aecec8"));
        gbc.gridx = 1;
        panel.add(userNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        // passwordField.setBackground(Color.decode("#aecec8"));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // User Type Radio Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("User Type:"), gbc);
        studentRadioButton = new JRadioButton("Student");
        studentRadioButton.setBackground(Color.decode("#8aaba5"));
        ownerRadioButton = new JRadioButton("Owner");
        ownerRadioButton.setBackground(Color.decode("#8aaba5"));
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(studentRadioButton);
        userTypeGroup.add(ownerRadioButton);
        studentRadioButton.setSelected(true); // Default selection
        gbc.gridx = 1;
        panel.add(studentRadioButton, gbc);
        gbc.gridy = 8;
        panel.add(ownerRadioButton, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(Color.decode("#809c97"));
        submitButton.addActionListener(new SubmitButtonListener());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String userName = userNameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Determine user type
            String userType = studentRadioButton.isSelected() ? "student" : "owner";

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword != null) {
                registerUser(firstName, lastName, userName, email, hashedPassword, userType);
            } else {
                JOptionPane.showMessageDialog(null, "Error hashing password!");
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerUser(String firstName, String lastName, String userName, String email, String hashedPassword,
            String userType) {
        DB_Functions db = new DB_Functions();
        Connection conn = db.connect_to_db();

        String sql = "INSERT INTO users (firstname, lastname, username, email, password, user_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, userName);
            pstmt.setString(4, email);
            pstmt.setString(5, hashedPassword);
            pstmt.setString(6, userType);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registration successful!");
            Login_GUI log = new Login_GUI();
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Registration failed: " + ex.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm_GUI::new);
    }

    // Testing Part
    public String getUsername() {
        return userNameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getEmail() {
        return emailField.getText();
    }

    public boolean registerUser() {
        // Basic validation checks (this can be customized further)
        if (getUsername().isEmpty() || getPassword().isEmpty() || getEmail().isEmpty()) {
            return false; // validation failed
        }

        // Validate email format
        if (!getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false; // Invalid email format
        }

        // Password validation example
        if (getPassword().length() < 8) {
            return false; // Password too short
        }

        // Example: Add more validation and functionality (e.g., checking if
        // username/email is already taken)

        return true; // Registration successful
    }
}
