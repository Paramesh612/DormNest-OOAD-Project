import javax.swing.*;
import java.awt.*;

public class PropertyCard extends JPanel {
    private JLabel propertyImageLabel;
    private JLabel propertyNameLabel;
    private JLabel roomsLabel;
    private JLabel addressLabel;
    private JLabel rentLabel;

    public PropertyCard(String propertyName, int rooms, String address, double rent, ImageIcon propertyImage) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(600, 150));

        // Property Image
        propertyImageLabel = new JLabel(propertyImage);
        propertyImageLabel.setPreferredSize(new Dimension(150, 150));
        add(propertyImageLabel, BorderLayout.WEST);

        // Property Details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        propertyNameLabel = new JLabel("Name: " + propertyName);
        roomsLabel = new JLabel("Rooms: " + rooms);
        addressLabel = new JLabel("Address: " + address);
        rentLabel = new JLabel("Rent: $" + rent);

        detailsPanel.add(propertyNameLabel);
        detailsPanel.add(roomsLabel);
        detailsPanel.add(addressLabel);
        detailsPanel.add(rentLabel);

        add(detailsPanel, BorderLayout.CENTER);
    }
}
