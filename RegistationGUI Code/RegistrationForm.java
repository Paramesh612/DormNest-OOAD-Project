import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm extends JFrame {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegistrationForm() {
        // Set up the form
        setTitle("Register");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form panel with GridBagLayout for better alignment
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between elements
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Registration Form", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // First Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        // User Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("User Name:"), gbc);
        userNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userNameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span across both columns
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
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Basic validation
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!");
                return;
            }

            // Insert into database
            registerUser(firstName, lastName, userName, password);
        }
    }

    private void registerUser(String firstName, String lastName, String userName, String password) {
        DB_Functions db = new DB_Functions();
        Connection conn = db.connect_to_db("TestDB", "postgres", "root");

        String sql = "INSERT INTO users (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, userName);
            pstmt.setString(4, password);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registration successful!");

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
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}
