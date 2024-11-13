import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoommateMatchingApp extends JFrame {
    int userID;
    public RoommateMatchingApp(int userID) {

        this.userID=userID;

        setTitle("Roommate Matching");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        // Fetch data from database
        List<HashMap<String, Object>> matchedStudents = fetchMatchedStudents();

        for (HashMap<String, Object> student : matchedStudents) {
            JPanel studentCard = userCard(student);
            mainPanel.add(studentCard);
        }

        add(scrollPane);
        setVisible(true);
    }
    private int getCurrentUserScore() {
        int score = 0;
        String query = "SELECT score FROM student_details WHERE student_id = ?";

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userID);  // Retrieve current user’s ID
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                score = rs.getInt("score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch user score: " + e.getMessage());
        }

        return score;
    }

    private List<HashMap<String, Object>> fetchMatchedStudents() {
        List<HashMap<String, Object>> data = new ArrayList<>();
        int currentUserScore = getCurrentUserScore();  // Fetch current user's score
        String currentUserLocation = getCurrentUserLocation();  // Fetch current user's location

        String query = "SELECT u.user_id, u.firstname, u.lastname, u.photo, sd.preferred_location, " +
                "sd.max_budget_for_roommate, sd.score " +
                "FROM users u " +
                "JOIN student_details sd ON u.user_id = sd.student_id " +
                "WHERE ABS(sd.score - ?) <= 10 "                // Score tolerance filter
                + "AND sd.preferred_location ILIKE ?";            // Partial match on location (case-insensitive)

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, currentUserScore);  // Use current user's score for filtering
            ps.setString(2, "%" + currentUserLocation + "%");  // Partial location match

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> student = new HashMap<>();
                student.put("id", rs.getInt("user_id"));
                student.put("firstname", rs.getString("firstname"));
                student.put("lastname", rs.getString("lastname"));
                student.put("preferred_location", rs.getString("preferred_location"));
                student.put("score", rs.getInt("score"));
                student.put("photo", rs.getBytes("photo"));
                student.put("max_budget_for_roommate", rs.getInt("max_budget_for_roommate"));
                data.add(student);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch data: " + e.getMessage());
        }

        return data;
    }

    private String getCurrentUserLocation() {
        String location = "";
        String query = "SELECT preferred_location FROM student_details WHERE student_id = ?";

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userID);  // Retrieve current user’s ID
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                location = rs.getString("preferred_location");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch user location: " + e.getMessage());
        }

        return location;
    }



    private JPanel userCard(HashMap<String, Object> studentDetails) {
        // Card with fixed size
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 180)); // Adjusted card size for more information
        card.setMaximumSize(new Dimension(500, 180)); // Maximum size to prevent stretching
        card.setBackground(Color.WHITE);

        // Info panel with fixed grid and larger text
        JPanel infoPanel = new JPanel(new GridLayout(5, 1)); // Adjusted grid layout for new field
        infoPanel.setFont(new Font("Arial", Font.PLAIN, 16)); // Increase font size
        String name = studentDetails.get("firstname") + " " + studentDetails.get("lastname");
        JLabel nameLabel = new JLabel("Name: " + name);
        JLabel phoneLabel = new JLabel("Phone: " + studentDetails.get("phone_number"));
        JLabel emailLabel = new JLabel("Email: " + studentDetails.get("email"));
        JLabel budgetLabel = new JLabel("Max Budget: $" + studentDetails.get("max_budget_for_roommate"));

        infoPanel.add(nameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(budgetLabel);

        // Display profile photo (No photo or placeholder text)
        byte[] photoBytes = (byte[]) studentDetails.get("photo"); // Get photo bytes
        JLabel photoLabel;
        if (photoBytes != null) {
            ImageIcon unscaled = new ImageIcon(photoBytes);
            Image scaled = unscaled.getImage().getScaledInstance(100, 140, 0);
            photoLabel = new JLabel(new ImageIcon(scaled));
        } else {
            // Provide a default image or text if no photo exists
            photoLabel = new JLabel("No photo available");
            photoLabel.setPreferredSize(new Dimension(100, 100));
            photoLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Accept Request button
        JButton acceptButton = new JButton("Send roommate request");
        acceptButton.addActionListener(e -> sendRequest((int) studentDetails.get("id")));

        // Arrange components in card
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(acceptButton);

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        return card;
    }


    private void sendRequest(int recipientId) {
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String insertRequest = "INSERT INTO requests (sender_id, recipient_id, request_type, status) " +
                    "VALUES (?, ?, 'roommate_request', 'pending')";
            PreparedStatement ps = conn.prepareStatement(insertRequest);
            ps.setInt(1, userID);
            ps.setInt(2, recipientId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request sent successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send request: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        new RoommateMatchingApp(6);
    }
}
