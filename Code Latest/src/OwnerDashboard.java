import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class OwnerDashboard extends JFrame {
    private JTextArea ownerDetailsArea;
    private JPanel propertyPanel;
    int userID; // get from Session
    JLabel ownerImageLabel;
    JPanel containerPanel;

    public OwnerDashboard(int userID) {
        this.userID = userID;

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton sendRequestButton = new JButton("Add Property");
        sendRequestButton.setPreferredSize(new Dimension(150, 50));
        sendRequestButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendRequestButton.setBackground(new Color(52, 14, 15)); // Blue background
        sendRequestButton.setForeground(Color.WHITE); // White text
        sendRequestButton.setFocusPainted(false); // Remove focus outline
        sendRequestButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        sendRequestButton.setBorderPainted(false); // Remove border outline
        sendRequestButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor
        buttonPanel.add(sendRequestButton);
        sendRequestButton.addActionListener(e -> {
            new AddPropertyGUI(userID);
        });

        setTitle("Owner Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the frame to full screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main container panel for all elements
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 3)); // Blue border
        JLabel titleLabel = new JLabel("Owner Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(52, 14, 15)); // Blue color
        titlePanel.add(titleLabel);

        // Owner Info Panel
        JPanel ownerInfoPanel = new JPanel(new BorderLayout(10, 10));
        ownerInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 2)); // Blue border

        // Owner image
        ImageIcon ownerImage;
        ownerImageLabel = new JLabel(""); // Placeholder text
        ownerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ownerImageLabel.setPreferredSize(new Dimension(150, 150));
        ownerImageLabel.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 2));

        // Owner details
        ownerDetailsArea = new JTextArea();
        ownerDetailsArea.setEditable(false);
        ownerDetailsArea.setLineWrap(true);
        ownerDetailsArea.setWrapStyleWord(true);
        ownerDetailsArea.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 2)); // Blue border
        ownerDetailsArea.setFont(new Font("Arial", Font.BOLD, 20));
        ownerDetailsArea.setBackground(new Color(242, 241, 237));

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

        // Create Notification Button Panel
        JPanel notificationPanel = new JPanel(new BorderLayout());

        JButton notificationButton = new JButton("🔔" + " Check for Notifications");
        notificationButton.setPreferredSize(new Dimension(50, 50));
        notificationButton.setBackground(new Color(52, 14, 15)); // Blue background
        notificationButton.setForeground(Color.WHITE); // White text
        notificationButton.setFont(new Font("Arial", Font.BOLD, 14)); // Adjust font size
        notificationPanel.add(notificationButton, BorderLayout.CENTER);
        notificationButton.addActionListener(e -> {
            StudentNotificationGUI sn = new StudentNotificationGUI(userID);
            // dispose();
        });

        add(notificationPanel, BorderLayout.NORTH);

        // Add the rest of the components
        add(containerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);

        // Ensure frame is visible
        setVisible(true);

        // Load data from database
        loadAccommodations("", null);
        getOwnerDetails();
    }

    private void getOwnerDetails() {
        DB_Functions db = new DB_Functions();
        Connection conn = db.connect_to_db();

        String ownerQuery = "SELECT firstname, lastname, email, phone_number, photo FROM users WHERE user_id = ?";
        try (PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery)) {
            ownerStmt.setInt(1, userID); // Use ownerId from session
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
                    ImageIcon unscaled = new ImageIcon(imageData);
                    Image scaled = unscaled.getImage().getScaledInstance(150, 190, 0);
                    ownerImageLabel.setIcon(new ImageIcon(scaled));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void loadAccommodations(String location, String rentRange) {
        propertyPanel.removeAll();

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String query = String.format("SELECT * FROM accommodation WHERE user_id=%d", userID);

            if (location != null && !location.isEmpty()) {
                query += " AND accommodation_address ILIKE ?";
            }

            if (rentRange != null) {
                if (rentRange.equals("0-100")) {
                    query += " AND rent <= 100";
                } else if (rentRange.equals("100-200")) {
                    query += " AND rent > 100 AND rent <= 200";
                } else if (rentRange.equals("200-300")) {
                    query += " AND rent > 200 AND rent <= 300";
                } else if (rentRange.equals("300-400")) {
                    query += " AND rent > 300 AND rent <= 400";
                } else if (rentRange.equals("400+")) {
                    query += " AND rent > 400";
                }
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            int paramIndex = 1;

            if (location != null && !location.isEmpty()) {
                stmt.setString(paramIndex++, "%" + location + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String accName = rs.getString("accommodation_name");
                String address = rs.getString("accommodation_address");
                String price = "$" + rs.getDouble("rent");
                int roommateCount = rs.getInt("numRooms");
                int accId = rs.getInt("accommodation_id");

                // Query for the image associated with the accommodation
                String query2 = "SELECT image_data FROM accommodation_images WHERE accommodation_id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(query2);
                stmt2.setInt(1, accId);
                ResultSet forImage = stmt2.executeQuery();

                ImageIcon accImage = null;
                ImageIcon scaledAccImage = null;
                if (forImage.next()) {
                    // Retrieve the image as a byte array from the database
                    byte[] imageBytes = forImage.getBytes("image_data");
                    if (imageBytes != null) {
                        accImage = new ImageIcon(imageBytes);

                        // Scale the image
                        Image scaledImage = accImage.getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH);
                        scaledAccImage = new ImageIcon(scaledImage);
                    }
                }
                forImage.close();
                stmt2.close();

                JPanel accommodationCard = createAccommodationCard(accId, accName, scaledAccImage, address, price,
                        roommateCount);
                propertyPanel.add(accommodationCard);
                propertyPanel.add(Box.createVerticalStrut(10)); // Spacing between cards
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        propertyPanel.revalidate();
        propertyPanel.repaint();
    }

    private JPanel createAccommodationCard(int accoID, String accName, ImageIcon accImage, String address, String price,
            int roommateCount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(700, 150)); // Constant card size
        card.setMaximumSize(new Dimension(700, 150)); // Enforce consistent size
        card.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 2)); // Blue border
        card.setBackground(Color.WHITE);

        // Landscape photo placeholder
        JLabel photoLabel = new JLabel(accImage); // "Photo", SwingConstants.CENTER
        photoLabel.setPreferredSize(new Dimension(280, 200)); // Wider and shorter for landscape orientation
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(52, 14, 15), 2));
        card.add(photoLabel, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Name
        JLabel accNameLabel = new JLabel("Name: " + accName);
        accNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        accNameLabel.setForeground(new Color(52, 14, 15)); // Blue color
        infoPanel.add(accNameLabel);

        // Address
        JLabel addressLabel = new JLabel("Address: " + address);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 18));
        addressLabel.setForeground(new Color(52, 14, 15)); // Blue color
        infoPanel.add(addressLabel);

        // Price and Roommate count panel
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel priceLabel = new JLabel("Price: " + price);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        priceLabel.setForeground(new Color(52, 14, 15)); // Blue color
        JLabel roommateCountLabel = new JLabel("Roommate count: " + roommateCount);
        roommateCountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roommateCountLabel.setForeground(new Color(52, 14, 15)); // Blue color
        detailsPanel.add(priceLabel);
        infoPanel.add(detailsPanel);
        infoPanel.add(roommateCountLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // More details button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton detailsButton = new JButton("More details...");
        detailsButton.setBackground(new Color(52, 14, 15)); // Blue button
        detailsButton.setForeground(Color.WHITE); // White text
        detailsButton.setFont(new Font("Arial", Font.BOLD, 14)); // Adjust font size
        detailsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        detailsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor
        detailsButton.addActionListener(e -> {
            // Pass the accommodation ID and user ID to the next screen
            AccommodationDetailsSwingGUI accDetailedPage = new AccommodationDetailsSwingGUI(2, accoID, false);
        });
        buttonPanel.add(detailsButton);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    public static void main(String[] args) {
        new OwnerDashboard(7);
    }
}
