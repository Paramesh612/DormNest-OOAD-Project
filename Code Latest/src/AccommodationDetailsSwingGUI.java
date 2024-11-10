import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class AccommodationDetailsSwingGUI extends JFrame {
    private JLabel descriptionLabel;
    private JLabel locationLabel;
    private JLabel priceLabel;
    private JLabel amenitiesLabel;
    private JLabel availabilityLabel;
    private JTextArea ownerNoteTextArea;
    private List<ImageIcon> images;
    private JPanel imagePanel;  // Panel to hold image list

    public AccommodationDetailsSwingGUI() {
        setTitle("Accommodation Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setLayout(new BorderLayout());

        // Initialize components
        descriptionLabel = new JLabel("Description: ");
        locationLabel = new JLabel("Location: ");
        priceLabel = new JLabel("Price: ");
        amenitiesLabel = new JLabel("Amenities: ");
        availabilityLabel = new JLabel("Availability: ");
        ownerNoteTextArea = new JTextArea(5, 30);

        // Center panel for image display and details
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Image panel that will contain all images as a list
        imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS)); // Use Y_AXIS for a vertical list

        // Details panel for text fields with individual panels for each detail
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setPreferredSize(new Dimension(500, 600)); // Increased size for details panel

        // Create individual panels for each detail
        JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descriptionPanel.add(descriptionLabel);

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.add(locationLabel);

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.add(priceLabel);

        JPanel amenitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amenitiesPanel.add(amenitiesLabel);

        JPanel availabilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availabilityPanel.add(availabilityLabel);

        // Add each panel to the detailsPanel
        detailsPanel.add(descriptionPanel);
        detailsPanel.add(locationPanel);
        detailsPanel.add(pricePanel);
        detailsPanel.add(amenitiesPanel);
        detailsPanel.add(availabilityPanel);

        // Scrollable panel for details section (optional)
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setPreferredSize(new Dimension(500, 600)); // Set a preferred size for the scroll pane

        // Set up the SplitPane (Image panel on the left, Details panel on the right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, detailsScrollPane);
        splitPane.setDividerLocation(400);  // Adjust the initial divider position to accommodate the smaller image panel
        splitPane.setResizeWeight(0.3);  // Makes the panels resizable, image panel will take 30% of the space

        // Set the split pane divider to start at 400px from the left
        centerPanel.add(splitPane, BorderLayout.CENTER);

        // Owner's note panel
        JPanel ownerNotePanel = new JPanel();
        ownerNotePanel.setLayout(new BorderLayout());
        ownerNotePanel.add(new JLabel("Owner's Note:"), BorderLayout.NORTH);
        ownerNotePanel.add(new JScrollPane(ownerNoteTextArea), BorderLayout.CENTER);

        // Button panel for send request button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton sendRequestButton = new JButton("Send Request");
        buttonPanel.add(sendRequestButton);

        // Add panels to frame
        add(centerPanel, BorderLayout.CENTER);
        add(ownerNotePanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);

        // Set up action listener for the button
        sendRequestButton.addActionListener(e -> sendRequest());

        // Load data from database
        loadAccommodationDetails(1); // Pass the ID of the accommodation you want to load

        setVisible(true);
    }

    private void loadAccommodationDetails(int accommodationId) {
        String url = "jdbc:postgresql://localhost:5432/your_database";
        String user = "your_username";
        String password = "your_password";

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db(url, user, password)){
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT description, location, price, amenities, availability, owner_note, images FROM Accommodation WHERE id = ?");

            stmt.setInt(1, accommodationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                descriptionLabel.setText("Description: " + rs.getString("description"));
                locationLabel.setText("Location: " + rs.getString("location"));
                priceLabel.setText("Price: $" + rs.getDouble("price"));
                amenitiesLabel.setText("Amenities: " + rs.getString("amenities"));
                availabilityLabel.setText("Availability: " + rs.getString("availability"));
                ownerNoteTextArea.setText(rs.getString("owner_note"));

                // Load images
                String[] imagePaths = rs.getString("images").split(","); // assuming comma-separated paths
                images = new ArrayList<>();
                for (String path : imagePaths) {
                    images.add(new ImageIcon(path.trim()));
                }

                if (!images.isEmpty()) {
                    displayImageList(); // Display all images in the panel
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accommodation details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayImageList() {
        // Clear the previous images from the image panel
        imagePanel.removeAll();

        // Add each image as a JLabel in the image panel
        for (ImageIcon image : images) {
            JLabel imageLabel = new JLabel(image);
            imagePanel.add(imageLabel);
        }

        // Refresh the image panel to reflect changes
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void sendRequest() {
        // Implement functionality for the send request action here
        JOptionPane.showMessageDialog(this, "Request sent!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccommodationDetailsSwingGUI::new);
    }
}
