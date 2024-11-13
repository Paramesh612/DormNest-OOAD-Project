import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentHomePageGUI extends JFrame {

    int userID;
    private JPanel contentPanel;
    private JComboBox<String> rentFilter;
    private JTextField locationField;

    public StudentHomePageGUI(int userID) {

        this.userID = userID;

        setTitle("Student Home Page");
        setSize(800, 600); // Full page size
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Student Notification Heading
        JLabel notificationHeading = new JLabel("Student Home Page", SwingConstants.CENTER);
        notificationHeading.setFont(new Font("Arial", Font.BOLD, 16));
        mainHeaderPanel.add(notificationHeading, BorderLayout.NORTH);

        // Notification Icon Button
        JButton notificationButton = new JButton("🔔");
        notificationButton.setPreferredSize(new Dimension(50, 50));
        mainHeaderPanel.add(notificationButton, BorderLayout.WEST);
        notificationButton.addActionListener(e -> {
            StudentNotificationGUI sn = new StudentNotificationGUI(userID);
        });


        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        locationField = new JTextField("Search By Location");

        // Rent Filter Dropdown
        rentFilter = new JComboBox<>(new String[] { "All", "00-1000", "1000-2000", "2000-3000", "3000-4000", "4000+" });
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(new FilterAction());

        JButton roommateMatchingButton = new JButton("Roommate Matching");
        roommateMatchingButton.addActionListener(e -> {

            new RoommateMatchingApp(userID);
            // Implement the action when the "Roommate Matching" button is clicked
//            JOptionPane.showMessageDialog(this, "Roommate Matching feature under development!");
            // You could replace the above line with your desired functionality,
            // such as opening a new window or performing an action for roommate matching.
        });

        searchPanel.add(locationField);
        searchPanel.add(rentFilter);
        searchPanel.add(filterButton);
        searchPanel.add(roommateMatchingButton);

        mainHeaderPanel.add(searchPanel, BorderLayout.CENTER);

        // Add mainHeaderPanel to the top of the frame
        add(mainHeaderPanel, BorderLayout.NORTH);

        // Scrollable Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Initial data load
        loadAccommodations(null, null);

        // Add scrollPane to the frame
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadAccommodations(String location, String rentRange) {
        contentPanel.removeAll();

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String query = String.format("SELECT * FROM accommodation where 1=1");

            if (location != null && !location.isEmpty()) {
                query += " AND accommodation_address ILIKE ?";
            }

            if (rentRange != null) {
                if (rentRange.equals("0-1000")) {
                    query += " AND rent <= 1000";
                } else if (rentRange.equals("1000-2000")) {
                    query += " AND rent > 1000 AND rent <= 2000";
                } else if (rentRange.equals("2000-3000")) {
                    query += " AND rent > 2000 AND rent <= 3000";
                } else if (rentRange.equals("3000-4000")) {
                    query += " AND rent > 3000 AND rent <= 4000";
                } else if (rentRange.equals("4000+")) {
                    query += " AND rent > 4000";
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
                contentPanel.add(accommodationCard);
                contentPanel.add(Box.createVerticalStrut(10)); // Spacing between cards
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createAccommodationCard(int accoID, String accName, ImageIcon accImage, String address, String price,
            int roommateCount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(700, 150)); // Constant card size
        card.setMaximumSize(new Dimension(700, 150)); // Enforce consistent size
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setBackground(Color.WHITE);

        // Landscape photo placeholder
        JLabel photoLabel = new JLabel(accImage); // "Photo", SwingConstants.CENTER
        photoLabel.setPreferredSize(new Dimension(280, 200)); // Wider and shorter for landscape orientation
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        card.add(photoLabel, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Name
        JLabel accNameLabel = new JLabel("Name: " + accName);
        accNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(accNameLabel);

        // Address
        JLabel addressLabel = new JLabel("Address: " + address);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(addressLabel);

        // Price and Roommate count panel
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel priceLabel = new JLabel("Price: " + price);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JLabel roommateCountLabel = new JLabel("Roommate count: " + roommateCount);
        roommateCountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        detailsPanel.add(priceLabel);
        infoPanel.add(detailsPanel);
        infoPanel.add(roommateCountLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // More details button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton detailsButton = new JButton("More details...");
        detailsButton.addActionListener(e -> {
            // Pass the accommodation ID and user ID to the next screen

            // JDialog notificationPage = new JDialog(this, "Notifications",true);
            // notificationPage.add(new AccommodationDetailsSwingGUI(userID,accoID));

            AccommodationDetailsSwingGUI accDetailedPage = new AccommodationDetailsSwingGUI(userID, accoID, true);
        });
        buttonPanel.add(detailsButton);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }
    //
    // // Load accommodations with filters
    // private void loadAccommodations(String location, String rentRange) {
    // contentPanel.removeAll();
    //
    // DB_Functions db = new DB_Functions();
    // try (Connection conn = db.connect_to_db()) {
    // String query = "SELECT * FROM accommodation WHERE 1=1";
    //
    // if (location != null && !location.isEmpty()) {
    // query += " AND accommodation_address ILIKE ?";
    // }
    //
    // if (rentRange != null) {
    // if (rentRange.equals("0-100")) {
    // query += " AND rent <= 100";
    // } else if (rentRange.equals("100-200")) {
    // query += " AND rent > 100 AND rent <= 200";
    // } else if (rentRange.equals("200-300")) {
    // query += " AND rent > 200 AND rent <= 300";
    // } else if (rentRange.equals("300-400")) {
    // query += " AND rent > 300 AND rent <= 400";
    // } else if (rentRange.equals("400+")) {
    // query += " AND rent > 400";
    // }
    // }
    //
    // PreparedStatement stmt = conn.prepareStatement(query);
    // int paramIndex = 1;
    //
    // if (location != null && !location.isEmpty()) {
    // stmt.setString(paramIndex++, "%" + location + "%");
    // }
    //
    // ResultSet rs = stmt.executeQuery();
    // while (rs.next()) {
    // String address = rs.getString("accommodation_address");
    // String price = "$" + rs.getDouble("rent");
    // int roommateCount = rs.getInt("numRooms");
    // int accId = rs.getInt("accommodation_id");
    //
    // // Query for the image associated with the accommodation
    // String query2 = "SELECT image_data FROM accommodation_images WHERE
    // accommodation_id = ?";
    // PreparedStatement stmt2 = conn.prepareStatement(query2);
    // stmt2.setInt(1, accId);
    // ResultSet forImage = stmt2.executeQuery();
    //
    // ImageIcon accImage = null;
    // ImageIcon scaledAccImage = null;
    // if (forImage.next()) {
    // // Retrieve the image as a byte array from the database
    // byte[] imageBytes = forImage.getBytes("image_data");
    // if (imageBytes != null) {
    // accImage = new ImageIcon(imageBytes);
    //
    // // Scale the image
    // Image scaledImage = accImage.getImage().getScaledInstance(150, 100,
    // Image.SCALE_SMOOTH);
    // scaledAccImage = new ImageIcon(scaledImage);
    // }
    // }
    // forImage.close();
    // stmt2.close();
    //
    // JPanel accommodationCard = createAccommodationCard(accId, scaledAccImage,
    // address, price, roommateCount);
    // contentPanel.add(accommodationCard);
    // contentPanel.add(Box.createVerticalStrut(10)); // Spacing between cards
    // }
    // rs.close();
    // stmt.close();
    // } catch (SQLException e) {
    // JOptionPane.showMessageDialog(null, e.getMessage());
    // }
    //
    // contentPanel.revalidate();
    // contentPanel.repaint();
    // }
    //
    //
    // private JPanel createAccommodationCard(int accoID,ImageIcon accImage ,String
    // address, String price, int roommateCount) {
    // JPanel card = new JPanel(new BorderLayout());
    // card.setPreferredSize(new Dimension(700, 150)); // Constant card size
    // card.setMaximumSize(new Dimension(700, 150)); // Enforce consistent size
    // card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    // card.setBackground(Color.WHITE);
    //
    // // Landscape photo placeholder
    // JLabel photoLabel = new JLabel(accImage); //"Photo", SwingConstants.CENTER
    // photoLabel.setPreferredSize(new Dimension(150, 100)); // Wider and shorter
    // for landscape orientation
    // photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    // card.add(photoLabel, BorderLayout.WEST);
    //
    // // Info Panel
    // JPanel infoPanel = new JPanel(new GridLayout(2, 1));
    // infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    //
    // // Address
    // JLabel addressLabel = new JLabel("Address: " + address);
    // addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
    // infoPanel.add(addressLabel);
    //
    // // Price and Roommate count panel
    // JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    // JLabel priceLabel = new JLabel("Price: " + price);
    // priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    // JLabel roommateCountLabel = new JLabel("Roommate count: " + roommateCount);
    // roommateCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    // detailsPanel.add(priceLabel);
    // detailsPanel.add(roommateCountLabel);
    // infoPanel.add(detailsPanel);
    //
    // card.add(infoPanel, BorderLayout.CENTER);
    //
    // // More details button
    // JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    // JButton detailsButton = new JButton("More details...");
    // detailsButton.addActionListener(e -> {
    // // Pass the accommodation ID and user ID to the next screen
    //
    //// JDialog notificationPage = new JDialog(this, "Notifications",true);
    //// notificationPage.add(new AccommodationDetailsSwingGUI(userID,accoID));
    //
    // AccommodationDetailsSwingGUI accDetailedPage = new
    // AccommodationDetailsSwingGUI(2, accoID , true);
    // });
    // buttonPanel.add(detailsButton);
    //
    // card.add(buttonPanel, BorderLayout.EAST);
    //
    // return card;
    // }

    private class FilterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String location = locationField.getText().equals("Search By Location") ? "" : locationField.getText();
            String rentRange = (String) rentFilter.getSelectedItem();
            loadAccommodations(location, rentRange.equals("All") ? null : rentRange);
        }
    }

    public static void main(String[] args) {
        new StudentHomePageGUI(1);
    }
}
