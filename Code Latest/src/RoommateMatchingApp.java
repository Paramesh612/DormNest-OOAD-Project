import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoommateMatchingApp extends JFrame {

    public RoommateMatchingApp() {
        setTitle("Roommate Matching");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        // Sample data for testing
        List<HashMap<String, Object>> matchedStudents = getSampleData();

        for (HashMap<String, Object> student : matchedStudents) {
            JPanel studentCard = createStudentCard(student);
            mainPanel.add(studentCard);
        }

        add(scrollPane);
        setVisible(true);
    }

    private List<HashMap<String, Object>> getSampleData() {
        List<HashMap<String, Object>> data = new ArrayList<>();

        // Create sample student data
        HashMap<String, Object> student1 = new HashMap<>();
        student1.put("firstname", "John");
        student1.put("lastname", "Doe");
        student1.put("preferred_location", "New York");
        student1.put("score", 85);
        student1.put("photo", null);  // No photo provided in this example
        data.add(student1);

        HashMap<String, Object> student2 = new HashMap<>();
        student2.put("firstname", "Jane");
        student2.put("lastname", "Smith");
        student2.put("preferred_location", "Los Angeles");
        student2.put("score", 92);
        student2.put("photo", null);  // No photo provided in this example
        data.add(student2);

        return data;
    }

    private JPanel createStudentCard(HashMap<String, Object> student) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 200));
        card.setBackground(Color.WHITE);

        // Top panel for location and match button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Location: " + student.get("preferred_location"));
        JButton matchButton = new JButton("Match Roommate");

        topPanel.add(locationLabel, BorderLayout.CENTER);
        topPanel.add(matchButton, BorderLayout.EAST);

        // Main panel for student details
        JPanel mainInfoPanel = new JPanel(new BorderLayout());

        // Left side for photo
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setText("No Image");

        // Center panel for name and details
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel("Name: " + student.get("firstname") + " " + student.get("lastname"));
        JLabel detailsLabel = new JLabel("Other details (Works at... etc)");

        centerPanel.add(nameLabel);
        centerPanel.add(detailsLabel);

        mainInfoPanel.add(photoLabel, BorderLayout.WEST);
        mainInfoPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for matching score and request button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel scoreLabel = new JLabel("Matching Score: " + student.get("score") + "%");
        JButton sendRequestButton = new JButton("Send Request");

        bottomPanel.add(scoreLabel);
        bottomPanel.add(sendRequestButton);

        // Add all parts to the card
        card.add(topPanel, BorderLayout.NORTH);
        card.add(mainInfoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners for buttons (for testing)
        matchButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Matching with " + student.get("firstname")));

        sendRequestButton.addActionListener(e -> {
            // Assuming recipient's ID is the index of student in the list for simplicity
            int recipientId = student.get("id") != null ? (int) student.get("id") : 2;
            sendRequest(recipientId);
        });

        return card;
    }

    private void sendRequest(int recipientId) {
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String insertRequest =
                    "INSERT INTO requests (sender_id, recipient_id, request_type, status) " +
                            "VALUES (?, ?, 'roommate_request', 'pending')";
            PreparedStatement ps = conn.prepareStatement(insertRequest);
            ps.setInt(1, getCurrentUserId()); // Sender's user ID (assuming getCurrentUserId() is implemented)
            ps.setInt(2, recipientId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request sent successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send request: " + e.getMessage());
        }
    }

    // Simulating getting the current logged-in user ID.
    // In a real application, this should be replaced by a session or user authentication.
    private int getCurrentUserId() {
        return 1; // Example: Returns 1 for the currently logged-in user (could be dynamically retrieved).
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoommateMatchingApp::new);
    }
}


