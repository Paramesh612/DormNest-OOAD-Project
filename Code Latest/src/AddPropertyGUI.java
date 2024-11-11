import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

public class AddPropertyGUI extends JFrame {
    private JTextField nameField, priceField, numPeopleField;
    private JTextArea ownerNoteArea, addressField;
    private JButton imageButton, submitButton;
    private JPanel imageDisplayPanel;
    private ArrayList<File> selectedFiles = new ArrayList<>();

    public AddPropertyGUI() {
        setTitle("Add Property");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add Property Label
        JLabel addPropertyLabel = new JLabel("Add Property:");
        addPropertyLabel.setFont(new Font("Arial", Font.BOLD, 30));
        addPropertyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(addPropertyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Image Button Panel
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imageButton = new JButton("+");
        imageButton.setPreferredSize(new Dimension(75, 75));
        imageButton.setFont(new Font("Arial", Font.BOLD, 48));
        imageButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Action listener to upload multiple images
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(true); // Enable multiple selection
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    for (File file : files) {
                        selectedFiles.add(file);
                        addImageThumbnail(file); // Display each image as a thumbnail
                    }
                }
            }
        });

        imagePanel.add(imageButton);
        mainPanel.add(imagePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel to display selected images
        imageDisplayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(imageDisplayPanel);

        // Name, Number of People, and Price Panel
        JPanel upperFieldsPanel = new JPanel(new GridLayout(1, 3, 20, 0));

        // Name Field
        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Name",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(nameField);

        // Number of People Field
        numPeopleField = new JTextField();
        numPeopleField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "Number of People", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        numPeopleField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(numPeopleField);

        // Price Field
        priceField = new JTextField();
        priceField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Price",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        priceField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(priceField);

        mainPanel.add(upperFieldsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Address Field
        addressField = new JTextArea();
        addressField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "Address", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        addressField.setFont(new Font("Arial", Font.PLAIN, 20));
        mainPanel.add(addressField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Owner's Note Field
        ownerNoteArea = new JTextArea();
        ownerNoteArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "Owner's Note", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        ownerNoteArea.setFont(new Font("Arial", Font.PLAIN, 20));
        mainPanel.add(ownerNoteArea);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Submit Button
        JPanel submitPanel = new JPanel();
        submitPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(400, 60));
        submitButton.setFont(new Font("Arial", Font.BOLD, 25));
        submitPanel.add(submitButton);

        submitButton.addActionListener(new SubmitAction());

        mainPanel.add(submitPanel);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addImageThumbnail(File file) {
        // Create a scaled thumbnail and add it to imageDisplayPanel
        ImageIcon icon = new ImageIcon(
                new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        JLabel thumbnailLabel = new JLabel(icon);
        imageDisplayPanel.add(thumbnailLabel);
        imageDisplayPanel.revalidate();
        imageDisplayPanel.repaint();
    }

    private class SubmitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DB_Functions db = new DB_Functions();
            try (Connection conn = db.connect_to_db()) {

                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
                    return;
                }

                // Insert into accommodation table
                String accInsertQuery = "INSERT INTO accommodation (user_id, accommodation_name, accommodation_address, status, numRooms, rent, owner_note) "
                        + "VALUES (?, ?, ?, 'vacant', ?, ?, ?) RETURNING accommodation_id";

                try (PreparedStatement accStmt = conn.prepareStatement(accInsertQuery)) {
                    int userId = 1; // Replace with actual user ID
                    accStmt.setInt(1, userId);
                    accStmt.setString(2, nameField.getText());
                    accStmt.setString(3, addressField.getText());
                    accStmt.setInt(4, Integer.parseInt(numPeopleField.getText()));
                    accStmt.setDouble(5, Double.parseDouble(priceField.getText()));
                    accStmt.setString(6, ownerNoteArea.getText());

                    ResultSet rs = accStmt.executeQuery();
                    int accommodationId = 0;
                    if (rs.next()) {
                        accommodationId = rs.getInt("accommodation_id");
                    }

                    // Insert multiple images into accommodation_images table
                    if (accommodationId != 0 && !selectedFiles.isEmpty()) {
                        String imgInsertQuery = "INSERT INTO accommodation_images (accommodation_id, image_data, image_description) VALUES (?, ?, '')";
                        try (PreparedStatement imgStmt = conn.prepareStatement(imgInsertQuery)) {
                            for (File file : selectedFiles) {
                                byte[] imageBytes = Files.readAllBytes(file.toPath());
                                imgStmt.setInt(1, accommodationId);
                                imgStmt.setBytes(2, imageBytes);
                                imgStmt.addBatch();
                            }
                            imgStmt.executeBatch();
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Successfully Registered");

                } catch (Exception error) {
                    JOptionPane.showMessageDialog(null, error.getMessage());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddPropertyGUI::new);
    }
}
