import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Student {

    // Default constructor

    protected int studentID;
    private String name;
    private String email;
    private String phoneNumber;
    private String preferredLocation;
    private double budget;
    public Student roommates;
    public String preferences;

    public Student() {
        // You can initialize default values here if needed
        this.studentID = 0;
        this.name = "";
        this.email = "";
        this.phoneNumber = "";
        this.preferredLocation = "";
        this.budget = 0.0;
        this.roommates = null; // Default to no roommates
        this.preferences = "";
    }

    // Constructor to initialize all fields
    public Student(int studentID, String name, String email, String phoneNumber,
            String preferredLocation, double budget, Student roommates,
            String preferences) {
        this.studentID = studentID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.preferredLocation = preferredLocation;
        this.budget = budget;
        this.roommates = roommates;
        this.preferences = preferences;
    }

    public boolean editProfile() {

        return false;
    }

    public Student viewProfile() {
        return null;
    }

    public boolean apply() {
        // TODO implement here
        return false;
    }

    public Student matchRoommates() {
        return null;
    }

    public void StudentGUI() {

        JFrame getStudentDetailsFrame = new JFrame();
        getStudentDetailsFrame.setSize(1000, 1000);
        getStudentDetailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel labelName = new JLabel("Name ");
        JLabel labelEmail = new JLabel("Email ");
        JLabel labelPhoneNumber = new JLabel("PhoneNumber ");
        JLabel labelPreferred_Location = new JLabel("Preferred_Location ");
        JLabel labelBudget = new JLabel("Budget ");

        JTextField TextName = new JTextField(50);
        JTextField TextEmail = new JTextField(50);
        JTextField TextPhoneNumber = new JTextField(50);
        JTextField TextPreferred_Location = new JTextField(50);
        JTextField TextBudget = new JTextField(50);

        // JLabel labelRoommates = new JLabel("Roommates ");
        // JLabel labelPreferences = new JLabel("Preferences ");

        getStudentDetailsFrame.setLayout(new GridLayout(6, 2));
        // Panel p = new Panel();

        getStudentDetailsFrame.add(labelName);
        getStudentDetailsFrame.add(TextName);
        getStudentDetailsFrame.add(labelEmail);
        getStudentDetailsFrame.add(TextEmail);
        getStudentDetailsFrame.add(labelPhoneNumber);
        getStudentDetailsFrame.add(TextPhoneNumber);
        getStudentDetailsFrame.add(labelPreferred_Location);
        getStudentDetailsFrame.add(TextPreferred_Location);
        getStudentDetailsFrame.add(labelBudget);
        getStudentDetailsFrame.add(TextBudget);

        JButton submitButton = new JButton("Submit");
        JButton showDetailsButton = new JButton("Show Student Details");

        getStudentDetailsFrame.add(submitButton);
        getStudentDetailsFrame.add(showDetailsButton);

        // getStudentDetailsFrame.add(p);

        Student st = new Student();

        submitButton.addActionListener(e -> {
            st.name = TextName.getText();
            st.email = TextEmail.getText();
            st.budget = Double.parseDouble(TextBudget.getText());
            st.phoneNumber = TextPhoneNumber.getText();
            st.preferredLocation = TextPreferred_Location.getText();
        });

        showDetailsButton.addActionListener(e -> {
            StringBuilder details = new StringBuilder();
            details.append("Name: ").append(st.name).append("\n")
                    .append("Email: ").append(st.email).append("\n")
                    .append("Budget: ").append(st.budget).append("\n")
                    .append("Phone Number: ").append(st.phoneNumber).append("\n")
                    .append("Preferred Location: ").append(st.preferredLocation);

            JOptionPane.showMessageDialog(getStudentDetailsFrame, details.toString(), "Student Details: ",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        getStudentDetailsFrame.pack();
        getStudentDetailsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Student st = new Student();
        st.StudentGUI();
    }

}