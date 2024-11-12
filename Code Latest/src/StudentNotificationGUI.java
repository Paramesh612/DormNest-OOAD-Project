import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

public class StudentNotificationGUI extends JFrame {

    private DB_Functions db = new DB_Functions();

    public StudentNotificationGUI() {
        setTitle("Notifications");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main Panel inside ScrollPane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Add header panel
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Notifications");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));  // Large font for header
        headerPanel.add(headerLabel);

        // Fetch notifications from the database for user ID 2
        List<HashMap<String, Object>> notifications = fetchNotifications(2);

        // If no data, show message (for debugging)
        if (notifications.isEmpty()) {
            mainPanel.add(new JLabel("No notifications available"));
        } else {
            for (HashMap<String, Object> notification : notifications) {
                JPanel notificationCard = createNotificationCard(notification);
                mainPanel.add(notificationCard);
                mainPanel.add(Box.createVerticalStrut(10)); // Add spacing between cards
            }
        }

        // Add header and scroll pane to frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private int getCurrentUserId() {
        return 2; // Current user ID is set to 2
    }

    private List<HashMap<String, Object>> fetchNotifications(int currentUserId) {
        List<HashMap<String, Object>> notifications = new ArrayList<>();

        try (Connection conn = db.connect_to_db()) {
            String query = "SELECT r.request_id, r.sender_id, r.recipient_id, r.request_type, r.status, "
                    + "u.firstname, u.lastname, u.phone_number, u.email, u.photo, s.max_budget_for_roommate "
                    + "FROM requests r "
                    + "JOIN users u ON r.sender_id = u.user_id "
                    + "JOIN student_details s ON u.user_id = s.student_id "
                    + "WHERE r.recipient_id = ? AND r.status = 'pending'";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        HashMap<String, Object> notification = new HashMap<>();
                        notification.put("request_id", rs.getInt("request_id"));
                        notification.put("sender_id", rs.getInt("sender_id"));
                        notification.put("firstname", rs.getString("firstname"));
                        notification.put("lastname", rs.getString("lastname"));
                        notification.put("phone_number", rs.getString("phone_number"));
                        notification.put("email", rs.getString("email"));
                        notification.put("photo", rs.getBytes("photo")); // Fetch photo as BYTEA
                        notification.put("max_budget_for_roommate", rs.getBigDecimal("max_budget_for_roommate"));
                        notifications.add(notification);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exception
        }

        return notifications;
    }

    private JPanel createNotificationCard(HashMap<String, Object> notification) {
        // Card with fixed size
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 180)); // Adjusted card size for more information
        card.setMaximumSize(new Dimension(500, 180)); // Maximum size to prevent stretching
        card.setBackground(Color.WHITE);

        // Info panel with fixed grid and larger text
        JPanel infoPanel = new JPanel(new GridLayout(5, 1)); // Adjusted grid layout for new field
        infoPanel.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size
        String name = notification.get("firstname") + " " + notification.get("lastname");
        JLabel nameLabel = new JLabel("Name: " + name);
        JLabel phoneLabel = new JLabel("Phone: " + notification.get("phone_number"));
        JLabel emailLabel = new JLabel("Email: " + notification.get("email"));
        JLabel budgetLabel = new JLabel("Max Budget: $" + notification.get("max_budget_for_roommate"));

        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(budgetLabel);

        // Display profile photo (No photo or placeholder text)
        JLabel photoLabel = new JLabel("No photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Accept Request button
        JButton acceptButton = new JButton("Accept Request");
        acceptButton.addActionListener(e -> acceptRequest((int) notification.get("request_id")));

        // Arrange components in card
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(acceptButton);

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        return card;
    }

    private void acceptRequest(int requestId) {
        // Placeholder for accept request logic (prints a message for testing)
        JOptionPane.showMessageDialog(this, "Request with ID " + requestId + " accepted!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentNotificationGUI::new);
    }
}
