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

        // Fetch data from database
        List<HashMap<String, Object>> matchedStudents = fetchMatchedStudents();

        for (HashMap<String, Object> student : matchedStudents) {
            JPanel studentCard = createStudentCard(student);
            mainPanel.add(studentCard);
        }

        add(scrollPane);
        setVisible(true);
    }

    private List<HashMap<String, Object>> fetchMatchedStudents() {
        List<HashMap<String, Object>> data = new ArrayList<>();
        String query = "SELECT u.user_id, u.firstname, u.lastname, sd.preferred_location, sd.max_budget_for_roommate " +
                "FROM users u JOIN student_details sd ON u.user_id = sd.student_id";

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                HashMap<String, Object> student = new HashMap<>();
                student.put("id", rs.getInt("user_id"));
                student.put("firstname", rs.getString("firstname"));
                student.put("lastname", rs.getString("lastname"));
                student.put("preferred_location", rs.getString("preferred_location"));
                student.put("score", 85); // Replace with actual scoring logic if needed
                student.put("photo", null);  // Fetch photo if needed
                data.add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch data: " + e.getMessage());
        }

        return data;
    }

    private JPanel createStudentCard(HashMap<String, Object> student) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 200));
        card.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Location: " + student.get("preferred_location"));
        JButton matchButton = new JButton("Match Roommate");

        topPanel.add(locationLabel, BorderLayout.CENTER);
        topPanel.add(matchButton, BorderLayout.EAST);

        JPanel mainInfoPanel = new JPanel(new BorderLayout());
        JLabel photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setText("No Image");

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel("Name: " + student.get("firstname") + " " + student.get("lastname"));
        JLabel detailsLabel = new JLabel("Other details (Works at... etc)");

        centerPanel.add(nameLabel);
        centerPanel.add(detailsLabel);

        mainInfoPanel.add(photoLabel, BorderLayout.WEST);
        mainInfoPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel scoreLabel = new JLabel("Matching Score: " + student.get("score") + "%");
        JButton sendRequestButton = new JButton("Send Request");

        bottomPanel.add(scoreLabel);
        bottomPanel.add(sendRequestButton);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(mainInfoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        matchButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Matching with " + student.get("firstname")));

        sendRequestButton.addActionListener(e -> {
            int recipientId = (int) student.get("id");
            sendRequest(recipientId);
        });

        return card;
    }

    private void sendRequest(int recipientId) {
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String insertRequest = "INSERT INTO requests (sender_id, recipient_id, request_type, status) " +
                    "VALUES (?, ?, 'roommate_request', 'pending')";
            PreparedStatement ps = conn.prepareStatement(insertRequest);
            ps.setInt(1, getCurrentUserId());
            ps.setInt(2, recipientId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request sent successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send request: " + e.getMessage());
        }
    }

    private int getCurrentUserId() {
        return 1; // Placeholder: replace with actual session or user ID retrieval logic.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoommateMatchingApp::new);
    }
}
