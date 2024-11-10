//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.math.BigDecimal;
//import java.sql.*;
//import javax.imageio.ImageIO;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RoommateMatchingApp extends JFrame {
//
//    public RoommateMatchingApp() {
//        setTitle("Roommate Matching");
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        JScrollPane scrollPane = new JScrollPane(mainPanel);
//
//        List<Student> matchedStudents = fetchMatchedStudents(getCurrentUserId());
//
//        for (Student student : matchedStudents) {
//            JPanel studentCard = createStudentCard(student);
//            mainPanel.add(studentCard);
//        }
//
//        add(scrollPane);
//        setVisible(true);
//    }
//
//    private int getCurrentUserId() {
//        // Implement logic to get the currently logged-in user's ID
//        return 1; // Placeholder, replace with actual user ID
//    }
//
//    private List<Student> fetchMatchedStudents(int currentUserId) {
//        List<Student> matchedStudents = new ArrayList<>();
//        try (Connection conn = DriverManager.getConnection("jdbc:your_database_url", "username", "password")) {
//            // Fetch current user details for comparison
//            String currentUserQuery = "SELECT * FROM student_details WHERE student_id = ?";
//            PreparedStatement currentUserPs = conn.prepareStatement(currentUserQuery);
//            currentUserPs.setInt(1, currentUserId);
//            ResultSet currentUserRs = currentUserPs.executeQuery();
//            if (!currentUserRs.next()) return matchedStudents;
//
//            Student currentUser = new Student(currentUserRs);
//
//            // Fetch all other students and calculate matching scores
//            String allStudentsQuery = "SELECT u.user_id, u.firstname, u.lastname, u.phone_number, u.email, u.photo, s.* " +
//                    "FROM student_details s JOIN users u ON s.student_id = u.user_id WHERE s.student_id != ?";
//            PreparedStatement ps = conn.prepareStatement(allStudentsQuery);
//            ps.setInt(1, currentUserId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Student student = new Student(rs);
//                int score = calculateMatchingScore(currentUser, student);
//                if (score >= 80) {
//                    student.setScore(score);
//                    matchedStudents.add(student);
//                }
//            }
//
//            // Sort students by score in descending order
//            matchedStudents.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return matchedStudents;
//    }
//
//    private int calculateMatchingScore(Student s1, Student s2) {
//        int score = 0;
//
//        if (s1.getSocialLifestyle().equals(s2.getSocialLifestyle())) score += 20;
//        if (s1.getMealPreference().equals(s2.getMealPreference())) score += 20;
//
//        // Budget match calculation
//        if (Math.abs(s1.getMaxBudgetForRoommate().subtract(s2.getMaxBudgetForRoommate()).doubleValue()) <= 100) score += 20;
//
//        // Preferred rent match calculation
//        if (Math.abs(s1.getPreferredRent().subtract(s2.getPreferredRent()).doubleValue()) <= 100) score += 20;
//
//        // Location match (simple example)
//        if (s1.getPreferredLocation().equalsIgnoreCase(s2.getPreferredLocation())) score += 20;
//
//        return score;
//    }
//
//    private JPanel createStudentCard(Student student) {
//        JPanel card = new JPanel(new BorderLayout());
//        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
//        card.setPreferredSize(new Dimension(500, 200));
//        card.setBackground(Color.WHITE);
//
//        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
//        String name = student.getFirstname() + " " + student.getLastname();
//        JLabel nameLabel = new JLabel("Name: " + name);
//        JLabel rentLabel = new JLabel("Preferred Rent: $" + student.getPreferredRent());
//        JLabel lifestyleLabel = new JLabel("Social Lifestyle: " + student.getSocialLifestyle());
//        JLabel mealLabel = new JLabel("Meal Preference: " + student.getMealPreference());
//
//        infoPanel.add(nameLabel);
//        infoPanel.add(rentLabel);
//        infoPanel.add(lifestyleLabel);
//        infoPanel.add(mealLabel);
//
//        // Display profile photo
//        JLabel photoLabel = new JLabel();
//        byte[] photoData = student.getPhoto();
//        if (photoData != null) {
//            try {
//                BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoData));
//                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
//                photoLabel.setIcon(new ImageIcon(scaledImg));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        // Score and Send Request button
//        JLabel scoreLabel = new JLabel("Matching Score: " + student.getScore() + "%");
//        JButton sendRequestButton = new JButton("Send Request");
//        sendRequestButton.addActionListener(e -> sendRequest(student.getUserId()));
//
//        // Arrange components in card
//        JPanel bottomPanel = new JPanel(new FlowLayout());
//        bottomPanel.add(scoreLabel);
//        bottomPanel.add(sendRequestButton);
//
//        card.add(photoLabel, BorderLayout.WEST);
//        card.add(infoPanel, BorderLayout.CENTER);
//        card.add(bottomPanel, BorderLayout.SOUTH);
//
//        return card;
//    }
//
//    private void sendRequest(int recipientId) {
//        try (Connection conn = DriverManager.getConnection("jdbc:your_database_url", "username", "password")) {
//            String insertRequest = """
//                INSERT INTO requests (sender_id, recipient_id, request_type, status)
//                VALUES (?, ?, 'roommate_request', 'pending')
//            """;
//            PreparedStatement ps = conn.prepareStatement(insertRequest);
//            ps.setInt(1, getCurrentUserId()); // Replace with the sender's user ID
//            ps.setInt(2, recipientId);
//            ps.executeUpdate();
//
//            JOptionPane.showMessageDialog(this, "Request sent successfully!");
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Failed to send request.");
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(RoommateMatchingApp::new);
//    }
//}
//
//class Student {
//    private int userId;
//    private String firstname;
//    private String lastname;
//    private String phoneNumber;
//    private String email;
//    private byte[] photo;
//    private BigDecimal preferredRent;
//    private String preferredLocation;
//    private String socialLifestyle;
//    private String mealPreference;
//    private BigDecimal maxBudgetForRoommate;
//    private int score;
//
//    public Student(ResultSet rs) throws SQLException {
//        this.userId = rs.getInt("user_id");
//        this.firstname = rs.getString("firstname");
//        this.lastname = rs.getString("lastname");
//        this.phoneNumber = rs.getString("phone_number");
//        this.email = rs.getString("email");
//        this.photo = rs.getBytes("photo");
//        this.preferredRent = rs.getBigDecimal("preferred_rent");
//        this.preferredLocation = rs.getString("preferred_location");
//        this.socialLifestyle = rs.getString("social_lifestyle");
//        this.mealPreference = rs.getString("meal_preference");
//        this.maxBudgetForRoommate = rs.getBigDecimal("max_budget_for_roommate");
//    }
//
//    // Getters and setters...
//
//    public int getUserId() { return userId; }
//    public String getFirstname() { return firstname; }
//    public String getLastname() { return lastname; }
//    public byte[] getPhoto() { return photo; }
//    public BigDecimal getPreferredRent() { return preferredRent; }
//    public String getPreferredLocation() { return preferredLocation; }
//    public String getSocialLifestyle() { return socialLifestyle; }
//    public String getMealPreference() { return mealPreference; }
//    public BigDecimal getMaxBudgetForRoommate() { return maxBudgetForRoommate; }
//    public int getScore() { return score; }
//    public void setScore(int score) { this.score = score; }
//}
//
