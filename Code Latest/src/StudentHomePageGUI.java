import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentHomePageGUI extends JFrame {

    private JPanel contentPanel;
    private JComboBox<String> rentFilter;
    private JTextField locationField;

    public StudentHomePageGUI( int userID ) {
        setTitle("Student Home Page");
        setSize(800, 600); // Full page size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Header Panel
        JPanel mainHeaderPanel = new JPanel(new BorderLayout());
        mainHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Student Notification Heading
        JLabel notificationHeading = new JLabel("Student Notification", SwingConstants.CENTER);
        notificationHeading.setFont(new Font("Arial", Font.BOLD, 16));
        mainHeaderPanel.add(notificationHeading, BorderLayout.NORTH);

        // Notification Icon Button
        JButton notificationButton = new JButton("🔔");
        notificationButton.setPreferredSize(new Dimension(50, 50));
        mainHeaderPanel.add(notificationButton, BorderLayout.WEST);
        notificationButton.addActionListener(e -> {
            StudentNotificationGUI sn = new StudentNotificationGUI();
            dispose();
        });

        // Search and Filter Panel
        JPanel searchPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        locationField = new JTextField("Search By Location");

        // Rent Filter Dropdown
        rentFilter = new JComboBox<>(new String[]{"All", "0-100", "100-200", "200-300", "300-400", "400+"});
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(new FilterAction());

        searchPanel.add(locationField);
        searchPanel.add(rentFilter);
        searchPanel.add(filterButton);

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

    // Load accommodations with filters
    private void loadAccommodations(String location, String rentRange) {
        contentPanel.removeAll();

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            String query = "SELECT * " +
                    "FROM accommodation WHERE user_id = ?";

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
            stmt.setInt(1, 2); // Assuming current user ID is 2

            if (location != null && !location.isEmpty()) {
                stmt.setString(2, "%" + location + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String address = rs.getString("accommodation_address");
                String price = "$" + rs.getDouble("rent");
                int roommateCount = rs.getInt("numRooms");
                int accId = rs.getInt("accommodation_id");
                JPanel accommodationCard = createAccommodationCard(accId, address, price, roommateCount);
                contentPanel.add(accommodationCard);
                contentPanel.add(Box.createVerticalStrut(10)); // Spacing between cards
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createAccommodationCard(int accoID, String address, String price, int roommateCount) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(700, 150)); // Constant card size
        card.setMaximumSize(new Dimension(700, 150)); // Enforce consistent size
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        card.setBackground(Color.WHITE);

        // Landscape photo placeholder
        JLabel photoLabel = new JLabel("Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(150, 100)); // Wider and shorter for landscape orientation
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        card.add(photoLabel, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Address
        JLabel addressLabel = new JLabel("Address: " + address);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(addressLabel);

        // Price and Roommate count panel
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel priceLabel = new JLabel("Price: " + price);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel roommateCountLabel = new JLabel("Roommate count: " + roommateCount);
        roommateCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsPanel.add(priceLabel);
        detailsPanel.add(roommateCountLabel);
        infoPanel.add(detailsPanel);

        card.add(infoPanel, BorderLayout.CENTER);

        // More details button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton detailsButton = new JButton("More details...");
        detailsButton.addActionListener(e -> {
            // Pass the accommodation ID and user ID to the next screen
            new AccommodationDetailsSwingGUI(2, accoID);
        });
        buttonPanel.add(detailsButton);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }


    private class FilterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String location = locationField.getText().equals("Search By Location") ? "" : locationField.getText();
            String rentRange = (String) rentFilter.getSelectedItem();
            loadAccommodations(location, rentRange.equals("All") ? null : rentRange);
        }
    }

    public static void main(String[] args) {
         new StudentHomePageGUI(2);
    }
}
