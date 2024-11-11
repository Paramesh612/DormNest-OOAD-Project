import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Comparator;
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

        // Fetch matched students based on the current user's preferences
        List<HashMap<String, Object>> matchedStudents = fetchMatchedStudents(getCurrentUserId());

        for (HashMap<String, Object> student : matchedStudents) {
            JPanel studentCard = createStudentCard(student);
            mainPanel.add(studentCard);
        }

        add(scrollPane);
        setVisible(true);
    }

    private int getCurrentUserId() {
        return 1; // Using user ID 1 as a sample ID
    }

    private List<HashMap<String, Object>> fetchMatchedStudents(int currentUserId) {
        List<HashMap<String, Object>> matchedStudents = new ArrayList<>();
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            // Fetch current user details
            String currentUserQuery = "SELECT * FROM student_details WHERE student_id = ?";
            PreparedStatement currentUserPs = conn.prepareStatement(currentUserQuery);
            currentUserPs.setInt(1, currentUserId);
            ResultSet currentUserRs = currentUserPs.executeQuery();
            if (!currentUserRs.next()) {
                return matchedStudents; // Return if current user not found
            }
            HashMap<String, Object> currentUser = extractStudentData(currentUserRs);

            // Fetch other students' details and calculate matching scores
            // Use aliases to avoid ambiguity
            String allStudentsQuery = """
                    SELECT u.user_id AS user_id, u.firstname AS firstname, u.lastname AS lastname,
                           u.phone_number AS phone_number, u.email AS email, u.photo AS photo,
                           s.*
                    FROM student_details s
                    JOIN users u ON s.student_id = u.user_id
                    WHERE s.student_id != ?
                    """;

            PreparedStatement ps = conn.prepareStatement(allStudentsQuery);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> student = extractStudentData(rs);
                int score = calculateMatchingScore(currentUser, student);
                if (score >= 80) { // Only add students with score >= 80
                    student.put("score", score);
                    matchedStudents.add(student);
                }
            }

            // Sort students by score in descending order
            matchedStudents.sort(Comparator.comparingInt(s -> -(int) s.get("score")));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
        return matchedStudents;
    }

    private HashMap<String, Object> extractStudentData(ResultSet rs) throws SQLException {
        HashMap<String, Object> studentData = new HashMap<>();
        studentData.put("user_id", rs.getInt("user_id"));
        studentData.put("firstname", rs.getString("firstname"));
        studentData.put("lastname", rs.getString("lastname"));
        studentData.put("phone_number", rs.getString("phone_number"));
        studentData.put("email", rs.getString("email"));
        studentData.put("photo", rs.getBytes("photo"));
        studentData.put("preferred_rent", rs.getBigDecimal("preferred_rent"));
        studentData.put("preferred_location", rs.getString("preferred_location"));
        studentData.put("social_lifestyle", rs.getString("social_lifestyle"));
        studentData.put("meal_preference", rs.getString("meal_preference"));
        studentData.put("max_budget_for_roommate", rs.getBigDecimal("max_budget_for_roommate"));
        return studentData;
    }

    private int calculateMatchingScore(HashMap<String, Object> s1, HashMap<String, Object> s2) {
        int score = 0;

        if (s1.get("social_lifestyle").equals(s2.get("social_lifestyle"))) {
            score += 20;
        }
        if (s1.get("meal_preference").equals(s2.get("meal_preference"))) {
            score += 20;
        }

        // Budget match calculation
        BigDecimal budget1 = (BigDecimal) s1.get("max_budget_for_roommate");
        BigDecimal budget2 = (BigDecimal) s2.get("max_budget_for_roommate");
        if (budget1 != null && budget2 != null && Math.abs(budget1.subtract(budget2).doubleValue()) <= 100) {
            score += 20;
        }

        // Preferred rent match calculation
        BigDecimal rent1 = (BigDecimal) s1.get("preferred_rent");
        BigDecimal rent2 = (BigDecimal) s2.get("preferred_rent");
        if (rent1 != null && rent2 != null && Math.abs(rent1.subtract(rent2).doubleValue()) <= 100) {
            score += 20;
        }

        // Location match
        if (s1.get("preferred_location").equals(s2.get("preferred_location"))) {
            score += 20;
        }

        return score;
    }

    private JPanel createStudentCard(HashMap<String, Object> student) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setPreferredSize(new Dimension(500, 200));
        card.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        String name = student.get("firstname") + " " + student.get("lastname");
        JLabel nameLabel = new JLabel("Name: " + name);
        JLabel rentLabel = new JLabel("Preferred Rent: $" + student.get("preferred_rent"));
        JLabel lifestyleLabel = new JLabel("Social Lifestyle: " + student.get("social_lifestyle"));
        JLabel mealLabel = new JLabel("Meal Preference: " + student.get("meal_preference"));

        infoPanel.add(nameLabel);
        infoPanel.add(rentLabel);
        infoPanel.add(lifestyleLabel);
        infoPanel.add(mealLabel);

        // Display profile photo
        JLabel photoLabel = new JLabel();
        byte[] photoData = (byte[]) student.get("photo");
        if (photoData != null) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoData));
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Score and Send Request button
        JLabel scoreLabel = new JLabel("Matching Score: " + student.get("score") + "%");
        JButton sendRequestButton = new JButton("Send Request");
        sendRequestButton.addActionListener(e -> sendRequest((int) student.get("user_id")));

        // Arrange components in card
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(scoreLabel);
        bottomPanel.add(sendRequestButton);

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private void sendRequest(int recipientId) {
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String insertRequest = """
                        INSERT INTO requests (sender_id, recipient_id, request_type, status)
                        VALUES (?, ?, 'roommate_request', 'pending')
                    """;
            PreparedStatement ps = conn.prepareStatement(insertRequest);
            ps.setInt(1, getCurrentUserId()); // Sender's user ID
            ps.setInt(2, recipientId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request sent successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send request.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RoommateMatchingApp::new);
    }
}
