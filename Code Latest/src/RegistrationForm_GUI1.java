import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class RegistrationForm_GUI1 extends JFrame {
    protected JTextField firstNameField;
    protected JTextField lastNameField;
    protected JTextField userNameField;
    protected JTextField emailField;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JTextField phoneNumberField;
    protected JButton photoButton;
    protected JLabel photoLabel;
    protected JRadioButton studentRadioButton;
    protected JRadioButton ownerRadioButton;
    private byte[] photoBytes; // Store the photo as a byte array

    public RegistrationForm_GUI1() {

        UIManager.put("Label.font", new Font("Arial", Font.BOLD, 16));
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 16));

        setTitle("Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

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
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Phone Number Field
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Phone Number:"), gbc);
        phoneNumberField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(phoneNumberField, gbc);

        // Photo Selection Button and Label
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Photo:"), gbc);
        photoButton = new JButton("Select Photo");
        photoLabel = new JLabel();
        gbc.gridx = 1;
        panel.add(photoButton, gbc);
        gbc.gridy = 9;
        panel.add(photoLabel, gbc);

        // Photo selection action
        photoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        photoBytes = Files.readAllBytes(selectedFile.toPath());
                        ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(img));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error loading photo.");
                    }
                }
            }
        });

        // User Type Radio Buttons
        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(new JLabel("User Type:"), gbc);
        studentRadioButton = new JRadioButton("Student");
        studentRadioButton.setBackground(Color.decode("#8aaba5"));
        ownerRadioButton = new JRadioButton("Owner");
        ownerRadioButton.setBackground(Color.decode("#8aaba5"));
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(studentRadioButton);
        userTypeGroup.add(ownerRadioButton);
        studentRadioButton.setSelected(true);
        gbc.gridx = 1;
        panel.add(studentRadioButton, gbc);
        gbc.gridy = 11;
        panel.add(ownerRadioButton, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(Color.decode("#809c97"));
        submitButton.addActionListener(new SubmitButtonListener());
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
        setVisible(true);
    }

    class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String userName = userNameField.getText();
            String email = emailField.getText();
            String phoneNumber = phoneNumberField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            String userType = studentRadioButton.isSelected() ? "student" : "owner";

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword != null) {
                registerUser(firstName, lastName, userName, email, phoneNumber, hashedPassword, userType, photoBytes);
            } else {
                JOptionPane.showMessageDialog(null, "Error hashing password!");
            }
        }
    }

    String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerUser(String firstName, String lastName, String userName, String email, String phoneNumber,
            String hashedPassword, String userType, byte[] photo) {
        DB_Functions db = new DB_Functions();
        Connection conn = db.connect_to_db();
        int userID = 0;
        String sql = "INSERT INTO users (firstname, lastname, username, email, phone_number, password, user_type, photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, userName);
            pstmt.setString(4, email);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, hashedPassword);
            pstmt.setString(7, userType);
            pstmt.setBytes(8, photo); // Set photo bytes
            ResultSet userIDSet = pstmt.executeQuery();
            if(userIDSet.next()) userID = userIDSet.getInt("user_id");

            JOptionPane.showMessageDialog(null, "Registration successful!");
            if(userType.equals("student"))
                new StudentPreferencesForm_GUI(userID);
            else
                new Login_GUI();

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
        SwingUtilities.invokeLater(RegistrationForm_GUI1::new);
    }
}
