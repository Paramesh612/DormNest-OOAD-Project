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

    public StudentNotificationGUI() {
        setTitle("Notifications");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        // Fetch notifications for the current user
        List<HashMap<String, Object>> notifications = fetchNotifications(getCurrentUserId());

        for (HashMap<String, Object> notification : notifications) {
            JPanel notificationCard = createNotificationCard(notification);
            mainPanel.add(notificationCard);
        }

        add(scrollPane);
        setVisible(true);
    }

    private int getCurrentUserId() {
        // Placeholder for the current user's ID, replace with actual logic if needed
        return 1;
    }

    private List<HashMap<String, Object>> fetchNotifications(int currentUserId) {
        List<HashMap<String, Object>> notifications = new ArrayList<>();
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String query = """
                        SELECT r.request_id, u.user_id, u.firstname, u.lastname, u.phone_number, u.email, u.photo
                        FROM requests r JOIN users u ON r.sender_id = u.user_id
                        WHERE r.recipient_id = ? AND r.status = 'pending'
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> notification = new HashMap<>();
                notification.put("request_id", rs.getInt("request_id"));
                notification.put("user_id", rs.getInt("user_id"));
                notification.put("firstname", rs.getString("firstname"));
                notification.put("lastname", rs.getString("lastname"));
                notification.put("phone_number", rs.getString("phone_number"));
                notification.put("email", rs.getString("email"));
                notification.put("photo", rs.getBytes("photo"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
        return notifications;
    }

    private JPanel createNotificationCard(HashMap<String, Object> notification) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 200));
        card.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        String name = notification.get("firstname") + " " + notification.get("lastname");
        JLabel nameLabel = new JLabel("Name: " + name);
        JLabel phoneLabel = new JLabel("Phone: " + notification.get("phone_number"));
        JLabel emailLabel = new JLabel("Email: " + notification.get("email"));

        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(emailLabel);

        // Display profile photo
        JLabel photoLabel = new JLabel();
        byte[] photoData = (byte[]) notification.get("photo");
        if (photoData != null) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoData));
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

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
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String updateStatus = "UPDATE requests SET status = 'accepted' WHERE request_id = ?";
            PreparedStatement ps = conn.prepareStatement(updateStatus);
            ps.setInt(1, requestId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request accepted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to accept request.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentNotificationGUI::new);
    }
}
