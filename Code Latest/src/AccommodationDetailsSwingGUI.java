import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDetailsSwingGUI extends JFrame {
    private int currentImageIndex = 0;
    private JLabel imageLabel;
    private JLabel locationLabel;
    private JLabel rentLabel;
    private JLabel amenitiesLabel;
    private JLabel seatsAvailableLabel;
    private JLabel ownerNoteLabel;
    private ImageIcon[] images;
    private List<String> imagePaths;

    public AccommodationDetailsSwingGUI() {
        setTitle("Accommodation Details");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Content Panel to hold the image carousel and details side by side
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.X_AXIS)); // Use BoxLayout for horizontal
                                                                                       // layout

        // Image Carousel Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel("Images", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 200));
        // imagePanel.setPreferredSize(new Dimension(200, 300));

        JButton leftButton = new JButton("<");
        leftButton.setPreferredSize(new Dimension(50, 300));
        leftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousImage();
            }
        });

        JButton rightButton = new JButton(">");
        rightButton.setPreferredSize(new Dimension(50, 300));
        rightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextImage();
            }
        });

        imagePanel.add(leftButton, BorderLayout.WEST);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(rightButton, BorderLayout.EAST);

        // Accommodation Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        locationLabel = new JLabel("Location: ");
        rentLabel = new JLabel("Rent: ");
        amenitiesLabel = new JLabel("Amenities: ");
        seatsAvailableLabel = new JLabel("Seats Available: ");

        locationLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        rentLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        amenitiesLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        seatsAvailableLabel.setFont(new Font("Arial", Font.PLAIN, 25));

        detailsPanel.add(locationLabel);
        detailsPanel.add(rentLabel);
        detailsPanel.add(amenitiesLabel);
        detailsPanel.add(seatsAvailableLabel);

        // Adding image panel, spacer, and details side-by-side in mainContentPanel
        mainContentPanel.add(imagePanel);
        mainContentPanel.add(Box.createHorizontalStrut(20)); // Spacer between image and details
        mainContentPanel.add(detailsPanel);

        // Owner's Note Section
        JPanel ownerNotePanel = new JPanel();
        ownerNoteLabel = new JLabel("<< Add owner's note >>");
        ownerNoteLabel.setFont(new Font("Arial", Font.ITALIC, 25));
        ownerNotePanel.add(ownerNoteLabel);

        // Send Request Button Panel
        JPanel buttonPanel = new JPanel();
        JButton sendRequestButton = new JButton("Send Request");
        sendRequestButton.setPreferredSize(new Dimension(300, 40));
        sendRequestButton.setFont(new Font("Arial", Font.BOLD, 25));
        buttonPanel.add(sendRequestButton);

        // Adding panels to the frame in order
        add(mainContentPanel, BorderLayout.NORTH); // Top section with image and details
        add(ownerNotePanel, BorderLayout.CENTER); // Middle section with owner's note
        add(buttonPanel, BorderLayout.SOUTH); // Bottom section with button

        // Load data from database
        loadDataFromDatabase();

        setVisible(true);
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:postgresql://localhost:5432/your_database";
        String user = "your_username";
        String password = "your_password";

        DB_Functions db = new DB_Functions();
        try (Connection conn = db.connect_to_db("DormNest", "postgres", "root");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT location, rent, amenities, seats_available, owner_note, image_path FROM AccommodationDetails")) {

            if (rs.next()) {
                locationLabel.setText("Location: " + rs.getString("location"));
                rentLabel.setText("Rent: " + rs.getString("rent"));
                amenitiesLabel.setText("Amenities: " + rs.getString("amenities"));
                seatsAvailableLabel.setText("Seats Available: " + rs.getString("seats_available"));
                ownerNoteLabel.setText("Owner's Note: " + rs.getString("owner_note"));

                // Load image paths and create icons
                imagePaths = new ArrayList<>();
                do {
                    imagePaths.add(rs.getString("image_path"));
                } while (rs.next());

                loadImages();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadImages() {
        if (imagePaths != null && !imagePaths.isEmpty()) {
            images = new ImageIcon[imagePaths.size()];
            for (int i = 0; i < imagePaths.size(); i++) {
                images[i] = new ImageIcon(imagePaths.get(i));
            }
            updateImage();
        }
    }

    private void showPreviousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
            updateImage();
        }
    }

    private void showNextImage() {
        if (currentImageIndex < images.length - 1) {
            currentImageIndex++;
            updateImage();
        }
    }

    /*
     * private void updateImage() {
     * if (images != null && images.length > 0) {
     * imageLabel.setIcon(images[currentImageIndex]);
     * }
     * }
     */
    private void updateImage() {
        if (images != null && images.length > 0) {
            int maxWidth = 200; // Adjust this value as needed
            Image resizedImage = images[currentImageIndex].getImage().getScaledInstance(maxWidth, -1,
                    Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AccommodationDetailsSwingGUI();
            }
        });
    }
}
