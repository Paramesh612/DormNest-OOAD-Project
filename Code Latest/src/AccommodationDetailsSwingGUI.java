
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class AccommodationDetailsSwingGUI extends JFrame {

    int userID;
    int recipientId;
    int accommodationID;

    private JLabel titleLabel;
    private JLabel nameLabel;
    private JLabel locationLabel;
    private JLabel rentLabel;
    private JLabel numRoomsLabel;
    private JLabel availabilityLabel;
    private JTextArea ownerNoteTextArea;
    private List<ImageIcon> images;
    private JPanel imagePanel;

    public AccommodationDetailsSwingGUI(int userID, int accommodationID , boolean displaySendReqButton) {
        this.accommodationID = accommodationID;
        this.userID = userID;

        setTitle("Accommodation Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        Font titleFont = new Font("Arial", Font.BOLD, 24);
        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font textAreaFont = new Font("Arial", Font.PLAIN, 16);

        // Title Label at the top center
        titleLabel = new JLabel("Accommodation Details", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);

        nameLabel = new JLabel("Name: ");
        nameLabel.setFont(labelFont);
        locationLabel = new JLabel("Location: ");
        locationLabel.setFont(labelFont);
        rentLabel = new JLabel("Rent: ");
        rentLabel.setFont(labelFont);
        numRoomsLabel = new JLabel("Number of Rooms: ");
        numRoomsLabel.setFont(labelFont);
        availabilityLabel = new JLabel("Availability: ");
        availabilityLabel.setFont(labelFont);
        ownerNoteTextArea = new JTextArea(5, 30);
        ownerNoteTextArea.setFont(textAreaFont);
        ownerNoteTextArea.setEditable(false);

        JPanel centerPanel = new JPanel(new BorderLayout());

        // Image panel with vertical BoxLayout for scrolling
        imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));

        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        imageScrollPane.setPreferredSize(new Dimension(600, 600));

        // Use GridBagLayout for the details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); // Add vertical space between components
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; // Start with the first column

        // Name label
        gbc.gridy = 0;
        detailsPanel.add(nameLabel, gbc);

        // Location label
        gbc.gridy = 1;
        detailsPanel.add(locationLabel, gbc);

        // Rent label
        gbc.gridy = 2;
        detailsPanel.add(rentLabel, gbc);

        // Number of Rooms label
        gbc.gridy = 3;
        detailsPanel.add(numRoomsLabel, gbc);

        // Availability label
        gbc.gridy = 4;
        detailsPanel.add(availabilityLabel, gbc);

        // Scrollable area for the details panel
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setPreferredSize(new Dimension(400, 600));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imageScrollPane, detailsScrollPane);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.3);

        centerPanel.add(splitPane, BorderLayout.CENTER);

        JPanel ownerNotePanel = new JPanel();
        ownerNotePanel.setLayout(new BorderLayout());
        ownerNotePanel.add(new JLabel("Owner's Note:"), BorderLayout.NORTH);
        ownerNotePanel.add(new JScrollPane(ownerNoteTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton sendRequestButton = new JButton("Send Request");
        sendRequestButton.setPreferredSize(new Dimension(150, 50));
        sendRequestButton.setFont(new Font("Arial", Font.BOLD, 16));
        if(displaySendReqButton) buttonPanel.add(sendRequestButton);

        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(ownerNotePanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.PAGE_END);

        sendRequestButton.addActionListener(e -> sendRequest());
        loadAccommodationDetails();

        setVisible(true);
    }

    private void loadAccommodationDetails() {
        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT *, "
                    + "(SELECT array_agg(image_data) FROM accommodation_images WHERE accommodation_id = accommodation.accommodation_id) AS images "
                    + "FROM accommodation WHERE accommodation_id = ?");
            stmt.setInt(1, accommodationID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("accommodation_name"));
                locationLabel.setText("Location: " + rs.getString("accommodation_address"));
                rentLabel.setText("<html><span style='color: darkgreen;'>Rent: $" + rs.getDouble("rent") + "</span></html>");
                numRoomsLabel.setText("Number of Rooms: " + rs.getInt("numrooms"));
                String availability = rs.getString("status");
                availabilityLabel.setText("Availability: " + availability);
                if ("vacant".equalsIgnoreCase(availability)) {
                    availabilityLabel.setForeground(Color.GREEN);
                } else {
                    availabilityLabel.setForeground(Color.RED);
                }
                ownerNoteTextArea.setText(rs.getString("owner_note"));

                recipientId = rs.getInt("user_id");

                Array imagesData = rs.getArray("images");
                if (imagesData != null) {
                    byte[][] imageBytesArray = (byte[][]) imagesData.getArray();
                    images = new ArrayList<>();
                    for (byte[] imageBytes : imageBytesArray) {
                        ImageIcon imageIcon = new ImageIcon(imageBytes);
                        Image scaledImage = imageIcon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                        images.add(new ImageIcon(scaledImage));
                    }
                    if (!images.isEmpty()) {
                        displayImageList();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accommodation details.\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayImageList() {
        imagePanel.removeAll();
        for (ImageIcon image : images) {
            Image scaled = image.getImage().getScaledInstance((int) Math.round(image.getIconWidth() * (1.5)), (int) Math.round(image.getIconHeight() * (1.5)), 0);
            JLabel scaledImageLabel = new JLabel(new ImageIcon(scaled));
            imagePanel.add(scaledImageLabel);
            imagePanel.add(Box.createRigidArea(new Dimension(10, 10))); // Adds spacing between images
        }
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void sendRequest() {
        DB_Functions db = new DB_Functions();
        String query = String.format("Insert into requests (sender_id,recipient_id,request_type,status,request_message) values(%d,%d,'accommodation_inquiry','pending','');", userID, recipientId);
        try (Connection conn = db.connect_to_db(); Statement st = conn.createStatement()) {
            st.executeUpdate(query);
            JOptionPane.showMessageDialog(this, "Request sent!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public static void main(String[] args) {
        new AccommodationDetailsSwingGUI(2, 1 , true);
    }
}
