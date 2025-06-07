//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import javax.swing.*;
//import java.io.File;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AddPropertyGUITest {
//
//    private AddPropertyGUI addPropertyGUI;
//
//    @BeforeEach
//    void setUp() {
//        addPropertyGUI = new AddPropertyGUI(1);
//    }
//
//    @Test
//    void testNameFieldIsEmptyInitially() {
//        assertEquals("", addPropertyGUI.nameField.getText(), "Name field should be empty initially.");
//    }
//
//    @Test
//    void testPriceFieldAcceptsNumericInput() {
//        addPropertyGUI.priceField.setText("1200.50");
//        assertDoesNotThrow(() -> Double.parseDouble(addPropertyGUI.priceField.getText()),
//                "Price field should accept numeric input.");
//    }
//
//    @Test
//    void testNumPeopleFieldAcceptsIntegerInput() {
//        addPropertyGUI.numPeopleField.setText("4");
//        assertDoesNotThrow(() -> Integer.parseInt(addPropertyGUI.numPeopleField.getText()),
//                "Number of People field should accept integer input.");
//    }
//
//    @Test
//    void testAddingImageThumbnail() {
//        int initialThumbnailCount = addPropertyGUI.imageDisplayPanel.getComponentCount();
//        File dummyFile = new File("dummy.jpg"); // Use an existing image file for testing
//        addPropertyGUI.addImageThumbnail(dummyFile);
//        assertEquals(initialThumbnailCount + 1, addPropertyGUI.imageDisplayPanel.getComponentCount(),
//                "Image thumbnail should be added to the panel.");
//    }
//}
//






















//    @Test
//    void testSuccessfulRegistration() {
//        addPropertyGUI.nameField.setText("Test Property");
//        addPropertyGUI.addressField.setText("123 Test Street");
//        addPropertyGUI.numPeopleField.setText("3");
//        addPropertyGUI.priceField.setText("1500");
//        addPropertyGUI.ownerNoteArea.setText("This is a test property.");
//
//        // Simulate database connection and check if records are inserted properly
//        DB_Functions dbFunctions = new DB_Functions();
//        try (Connection conn = dbFunctions.connect_to_db()) {
//            assertNotNull(conn, "Database connection should be established.");
//            String query = "SELECT * FROM accommodation WHERE accommodation_name = ?";
//            try (PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, "Test Property");
//                ResultSet rs = stmt.executeQuery();
//                assertTrue(rs.next(), "The property should be registered successfully in the database.");
//            }
//        } catch (Exception e) {
//            fail("Database connection or query failed: " + e.getMessage());
//        }
//    }
