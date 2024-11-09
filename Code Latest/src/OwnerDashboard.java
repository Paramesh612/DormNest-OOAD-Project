import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class OwnerDashboard extends JFrame {
    private JTextArea ownerDetailsArea;
    private JPanel propertyPanel;

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
        JLabel ownerImageLabel = new JLabel("Owner Image"); // Placeholder text
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
        String url = "jdbc:mysql://localhost:3306/your_database"; // Replace with your DB URL
        String user = "postgre"; // Replace with your DB username
        String password = "root"; // Replace with your DB password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Fetch owner details
            String ownerQuery = "SELECT name, experience, specialization FROM owners WHERE id = 1"; // Adjust query as needed
            try (PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery)) {
                ResultSet ownerRs = ownerStmt.executeQuery();
                if (ownerRs.next()) {
                    String ownerDetails = "Owner Details:\n"
                        + "Name: " + ownerRs.getString("name") + "\n"
                        + "Experience: " + ownerRs.getInt("experience") + " years\n"
                        + "Specialization: " + ownerRs.getString("specialization");
                    ownerDetailsArea.setText(ownerDetails);
                }
            }

            // Fetch property listings
            String propertyQuery = "SELECT description FROM properties WHERE owner_id = 1"; // Adjust query as needed
            try (PreparedStatement propertyStmt = conn.prepareStatement(propertyQuery)) {
                ResultSet propertyRs = propertyStmt.executeQuery();
                while (propertyRs.next()) {
                    String propertyDetails = propertyRs.getString("description");
                    addPropertyListing(propertyDetails);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to add a property listing with a border
    private void addPropertyListing(String propertyDetails) {
        JLabel propertyLabel = new JLabel(propertyDetails);
        propertyLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        propertyLabel.setPreferredSize(new Dimension(600, 50)); // Width and height for each property entry
        propertyLabel.setMaximumSize(new Dimension(600, 50)); // Ensure consistent height for each listing
        propertyPanel.add(propertyLabel);
        propertyPanel.add(Box.createVerticalStrut(10)); // Space between listings
        propertyPanel.revalidate(); // Refresh panel to show new listings
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OwnerDashboard::new);
    }
}
