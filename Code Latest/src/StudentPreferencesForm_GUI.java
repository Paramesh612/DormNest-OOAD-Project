import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class StudentPreferencesForm_GUI extends JFrame {
    private JTextField studentIdField, preferredRentField, preferredLocationField, maxDistanceField, maxBudgetField;
    private JRadioButton hasPetsYes, hasPetsNo, worksAtNightYes, worksAtNightNo;
    private JComboBox<String> cleanlinessPrefCombo, socialLifestyleCombo, mealPreferenceCombo, transportationMethodCombo;
    private JTextArea allergyInformationArea;

    public StudentPreferencesForm_GUI() {
        setTitle("Student Preferences Form");
        setSize(500, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student ID
        panel.add(new JLabel("Student ID:"), gbc);
        studentIdField = new JTextField(10);
        panel.add(studentIdField, gbc);

        // Preferred Rent
        gbc.gridy = 1;
        panel.add(new JLabel("Preferred Rent:"), gbc);
        preferredRentField = new JTextField(10);
        panel.add(preferredRentField, gbc);

        // Preferred Location
        gbc.gridy = 2;
        panel.add(new JLabel("Preferred Location:"), gbc);
        preferredLocationField = new JTextField(10);
        panel.add(preferredLocationField, gbc);

        // Has Pets
        gbc.gridy = 3;
        panel.add(new JLabel("Do You Have Any Pets:"), gbc);
        hasPetsYes = new JRadioButton("Yes");
        hasPetsNo = new JRadioButton("No");
        ButtonGroup hasPetsGroup = new ButtonGroup();
        hasPetsGroup.add(hasPetsYes);
        hasPetsGroup.add(hasPetsNo);
        JPanel hasPetsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hasPetsPanel.add(hasPetsYes);
        hasPetsPanel.add(hasPetsNo);
        panel.add(hasPetsPanel, gbc);

        // Works At Night
        gbc.gridy = 4;
        panel.add(new JLabel("Do You Work At Night:"), gbc);
        worksAtNightYes = new JRadioButton("Yes");
        worksAtNightNo = new JRadioButton("No");
        ButtonGroup worksAtNightGroup = new ButtonGroup();
        worksAtNightGroup.add(worksAtNightYes);
        worksAtNightGroup.add(worksAtNightNo);
        JPanel worksAtNightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        worksAtNightPanel.add(worksAtNightYes);
        worksAtNightPanel.add(worksAtNightNo);
        panel.add(worksAtNightPanel, gbc);

        // Max Distance
        gbc.gridy = 5;
        panel.add(new JLabel("Max Distance (km):"), gbc);
        maxDistanceField = new JTextField(10);
        panel.add(maxDistanceField, gbc);

        // Cleanliness Preference
        gbc.gridy = 6;
        panel.add(new JLabel("Cleanliness Preference:"), gbc);
        cleanlinessPrefCombo = new JComboBox<>(new String[]{"Very Clean", "Moderate", "Not a Priority"});
        panel.add(cleanlinessPrefCombo, gbc);

        // Social Lifestyle
        gbc.gridy = 7;
        panel.add(new JLabel("Social Lifestyle:"), gbc);
        socialLifestyleCombo = new JComboBox<>(new String[]{"Introverted", "Extroverted", "Flexible"});
        panel.add(socialLifestyleCombo, gbc);

        // Allergy Information
        gbc.gridy = 8;
        panel.add(new JLabel("Allergy Information:"), gbc);
        allergyInformationArea = new JTextArea(3, 20);
        JScrollPane allergyScrollPane = new JScrollPane(allergyInformationArea);
        panel.add(allergyScrollPane, gbc);

        // Meal Preference
        gbc.gridy = 9;
        panel.add(new JLabel("Meal Preference:"), gbc);
        mealPreferenceCombo = new JComboBox<>(new String[]{"Vegetarian", "Non-Vegetarian", "Vegan"});
        panel.add(mealPreferenceCombo, gbc);

        // Transportation Method
        gbc.gridy = 10;
        panel.add(new JLabel("Transportation Method:"), gbc);
        transportationMethodCombo = new JComboBox<>(new String[]{"Car", "Bike", "Public Transport", "Walk"});
        panel.add(transportationMethodCombo, gbc);

        // Max Budget for Roommate
        gbc.gridy = 11;
        panel.add(new JLabel("Max Budget for Roommate:"), gbc);
        maxBudgetField = new JTextField(10);
        panel.add(maxBudgetField, gbc);

        // Submit Button
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        panel.add(submitButton, gbc);

        add(panel);
        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Retrieving values from form fields
            String studentId = studentIdField.getText().trim();
            String preferredRent = preferredRentField.getText().trim();
            String preferredLocation = preferredLocationField.getText().trim();
            boolean hasPets = hasPetsYes.isSelected();
            boolean worksAtNight = worksAtNightYes.isSelected();
            String maxDistance = maxDistanceField.getText().trim();
            String cleanlinessPreference = (String) cleanlinessPrefCombo.getSelectedItem();
            String socialLifestyle = (String) socialLifestyleCombo.getSelectedItem();
            String allergyInformation = allergyInformationArea.getText().trim();
            String mealPreference = (String) mealPreferenceCombo.getSelectedItem();
            String transportationMethod = (String) transportationMethodCombo.getSelectedItem();
            String maxBudget = maxBudgetField.getText().trim();

            // Display the values in a confirmation dialog
            JOptionPane.showMessageDialog(null, "Student Preferences Saved Successfully!\n"
                    + "Student ID: " + studentId + "\n"
                    + "Preferred Rent: " + preferredRent + "\n"
                    + "Preferred Location: " + preferredLocation + "\n"
                    + "Has Pets: " + hasPets + "\n"
                    + "Works at Night: " + worksAtNight + "\n"
                    + "Max Distance: " + maxDistance + "\n"
                    + "Cleanliness Preference: " + cleanlinessPreference + "\n"
                    + "Social Lifestyle: " + socialLifestyle + "\n"
                    + "Allergy Information: " + allergyInformation + "\n"
                    + "Meal Preference: " + mealPreference + "\n"
                    + "Transportation Method: " + transportationMethod + "\n"
                    + "Max Budget for Roommate: " + maxBudget);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentPreferencesForm_GUI::new);
    }
}
