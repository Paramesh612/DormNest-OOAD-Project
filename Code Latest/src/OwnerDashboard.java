import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class OwnerDashboard extends JFrame {
    private JTextArea ownerDetailsArea;
    private JPanel propertyPanel;
    int ownerId = 1; // get from Session
    JLabel ownerImageLabel;
    public OwnerDashboard() {

        setTitle("Owner Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the frame to full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main container panel for all elements
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel titleLabel = new JLabel("Owner Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Owner Info Panel
        JPanel ownerInfoPanel = new JPanel(new BorderLayout(10, 10));
        ownerInfoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Owner image
        ImageIcon ownerImage;
        ownerImageLabel = new JLabel("Owner Image"); // Placeholder text
        ownerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ownerImageLabel.setPreferredSize(new Dimension(150, 150));
        ownerImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Owner details
        ownerDetailsArea = new JTextArea();
        ownerDetailsArea.setEditable(false);
        ownerDetailsArea.setLineWrap(true);
        ownerDetailsArea.setWrapStyleWord(true);
        ownerDetailsArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        ownerInfoPanel.add(ownerImageLabel, BorderLayout.WEST);
        ownerInfoPanel.add(ownerDetailsArea, BorderLayout.CENTER);

        // Property Listings Section in a Scrollable Panel
        propertyPanel = new JPanel();
        propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));

        // Scroll pane for property listings
        JScrollPane scrollPane = new JScrollPane(propertyPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 500)); // Set preferred size for the scrollable area

        // Adding components to container panel with spacing
        containerPanel.add(titlePanel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between title and details
        containerPanel.add(ownerInfoPanel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between details and properties
        containerPanel.add(scrollPane); // Add the scrollable panel

        // Add container panel to main frame
        add(containerPanel, BorderLayout.CENTER);

        // Load data from database
        loadDataFromDatabase();

        // Ensure frame is visible
        setVisible(true);
    }

    // Helper method to load data from the database
    private void loadDataFromDatabase() {

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Connection is NULL");
            }

            // Fetch owner details
            String ownerQuery = "SELECT firstname, lastname, email, phone_number, photo FROM users WHERE user_id = ?";
            try (PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery)) {
                ownerStmt.setInt(1, ownerId); // Use ownerId from session
                ResultSet ownerRs = ownerStmt.executeQuery();

                if (ownerRs.next()) {
                    String ownerDetails = "Owner Details:\n"
                            + "Name: " + ownerRs.getString("firstname") + " " + ownerRs.getString("lastname") + "\n"
                            + "Phone Number: " + ownerRs.getString("phone_number") + "\n"
                            + "Email: " + ownerRs.getString("email");
                    ownerDetailsArea.setText(ownerDetails);
                    // Retrieve and set image (assuming photo is stored as binary data)
                    byte[] imageData = ownerRs.getBytes("photo");
                    if (imageData != null) {
                        ImageIcon ownerImage = new ImageIcon(imageData);
                        ownerImageLabel.setIcon(ownerImage);
                    }
                }
            }

            // Fetch property listings
            String propertyQuery = "SELECT accommodation_name, numRooms, accommodation_address, rent FROM accommodation WHERE user_id = ?";
            try (PreparedStatement propertyStmt = conn.prepareStatement(propertyQuery)) {
                propertyStmt.setInt(1, ownerId);
                ResultSet propertyRs = propertyStmt.executeQuery();
                while (propertyRs.next()) {
                    String propertyName = propertyRs.getString("accommodation_name");
                    int rooms = propertyRs.getInt("numRooms");
                    String address = propertyRs.getString("accommodation_address");
                    double rent = propertyRs.getDouble("rent");

                    // Fetch property image (assuming first image only for simplicity)
                    ImageIcon propertyImage = null;
                    String imageQuery = "SELECT image_data FROM accommodation_images WHERE accommodation_id = (SELECT accommodation_id FROM accommodation WHERE user_id = ? LIMIT 1)";
                    try (PreparedStatement imageStmt = conn.prepareStatement(imageQuery)) {
                        imageStmt.setInt(1, ownerId);
                        ResultSet imageRs = imageStmt.executeQuery();
                        if (imageRs.next()) {
                            byte[] imageData = imageRs.getBytes("image_data");
                            if (imageData != null) {
                                propertyImage = new ImageIcon(imageData);
                            }
                        }
                    }

                    addPropertyCard(propertyName, rooms, address, rent, propertyImage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPropertyCard(String name, int rooms, String address, double rent, ImageIcon image) {
        PropertyCard propertyCard = new PropertyCard(name, rooms, address, rent, image);
        propertyPanel.add(propertyCard);
        propertyPanel.add(Box.createVerticalStrut(10)); // Space between cards
        propertyPanel.revalidate(); // Refresh panel to show new listings
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OwnerDashboard::new);
    }
}
