import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

public class AddPropertyGUI extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField priceField;
    private JTextField numPeopleField;
    private JTextArea ownerNoteArea;
    private JButton imageButton;
    private JButton submitButton;
    private JLabel imageLabel;

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
        imageButton.setPreferredSize(new Dimension(150, 150));
        imageButton.setFont(new Font("Arial", Font.BOLD, 48));
        imageButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Action listener to upload image
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageButton.setIcon(new ImageIcon(image)); // Display chosen image
                    imageButton.setText(""); // Clear text
                }
            }
        });

        imagePanel.add(imageButton);
        mainPanel.add(imagePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name, Number of People, and Price Panel
        JPanel upperFieldsPanel = new JPanel(new GridLayout(1, 3, 20, 0));

        // Name Field
        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Name", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(nameField);

        // Number of People Field
        numPeopleField = new JTextField();
        numPeopleField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Number of People", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        numPeopleField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(numPeopleField);

        // Price Field
        priceField = new JTextField();
        priceField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Price", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        priceField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(priceField);

        mainPanel.add(upperFieldsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Address Field
        addressField = new JTextField();
        addressField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Address", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        addressField.setFont(new Font("Arial", Font.PLAIN, 20));
        mainPanel.add(addressField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Owner's Note Field
        ownerNoteArea = new JTextArea();
        ownerNoteArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Owner's Note", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
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

        mainPanel.add(submitPanel);

        // Load data from the database
        loadDataFromDatabase();

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:postgresql://localhost:5432/your_database";
        String user = "your_username";
        String password = "your_password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, address, price, num_people, owner_note FROM AccommodationDetails")) {

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                addressField.setText(rs.getString("address"));
                priceField.setText(rs.getString("price"));
                numPeopleField.setText(rs.getString("num_people"));
                ownerNoteArea.setText(rs.getString("owner_note"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddPropertyGUI::new);
    }
}