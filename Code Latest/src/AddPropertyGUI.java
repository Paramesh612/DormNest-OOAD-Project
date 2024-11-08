import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;

public class AddPropertyGUI extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField priceField;
    private JTextField numPeopleField;
    private JTextArea ownerNoteArea;
    private JButton imageButton;
    private JButton submitButton;

    public AddPropertyGUI() {
        setTitle("Add Property");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add Property Label (Top left corner)
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
        imagePanel.add(imageButton);
        mainPanel.add(imagePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Name, Number of People, and Price Panel
        JPanel upperFieldsPanel = new JPanel(new GridLayout(1, 3, 20, 0));

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Name", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(nameField);

        numPeopleField = new JTextField();
        numPeopleField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Number of People", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.PLAIN, 20)));
        numPeopleField.setFont(new Font("Arial", Font.PLAIN, 20));
        upperFieldsPanel.add(numPeopleField);

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

        // Submit Button (Center alignment)
        JPanel submitPanel = new JPanel();
        submitPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(400, 60));
        submitButton.setFont(new Font("Arial", Font.BOLD, 25));
        submitPanel.add(submitButton);

        mainPanel.add(submitPanel);

        // Load data from database
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
                nameField.setText(rs.getString("Name"));
                addressField.setText(rs.getString("Address"));
                priceField.setText(rs.getString("Price"));
                numPeopleField.setText(rs.getString("Number of People"));
                ownerNoteArea.setText(rs.getString("owner's Note"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddPropertyGUI());
    }
}
