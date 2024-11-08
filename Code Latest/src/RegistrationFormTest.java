//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.JUnitCore;
//import org.junit.runner.Result;
//import org.junit.runner.notification.Failure;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class RegistrationFormTest extends RegistrationForm_GUI        {
//
//    private RegistrationForm_GUI registrationForm;
//
//    @BeforeEach
//    void setUp() {
//        registrationForm = new RegistrationForm_GUI();
//    }
//
//    @Test
//    void testValidRegistration() {
//        // Simulate user input for valid registration
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertTrue(result, "The registration should be successful with valid input.");
//    }
//
//    @Test
//    void testEmptyUsername() {
//        registrationForm.userNameField.setText("");
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail due to empty username.");
//    }
//
//    @Test
//    void testEmptyPassword() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail due to empty password.");
//    }
//
//    @Test
//    void testEmptyEmail() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail due to empty email.");
//    }
//
//    @Test
//    void testInvalidEmailFormat() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("invalidEmail");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail due to invalid email format.");
//    }
//
//    @Test
//    void testPasswordLengthTooShort() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("short");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail due to password being too short.");
//    }
//
//    @Test
//    void testPasswordWithoutNumbers() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("NoNumberPassword");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail because password should contain numbers.");
//    }
//
//    @Test
//    void testPasswordWithoutSpecialCharacter() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("NoSpecialChar123");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail because password should contain a special character.");
//    }
//
//    @Test
//    void testPasswordWithValidLengthAndSpecialChar() {
//        registrationForm.userNameField.setText("testUser");
//        registrationForm.passwordField.setText("Valid@123Password");
//        registrationForm.emailField.setText("testuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertTrue(result, "The registration should be successful with valid password containing a special character.");
//    }
//
//    @Test
//    void testUsernameAlreadyTaken() {
//        registrationForm.userNameField.setText("existingUser"); // assuming this username is already in the database
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("newuser@example.com");
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail if the username is already taken.");
//    }
//
//    @Test
//    void testEmailAlreadyUsed() {
//        registrationForm.userNameField.setText("newUser");
//        registrationForm.passwordField.setText("validPassword123");
//        registrationForm.emailField.setText("existinguser@example.com"); // assuming this email is already registered
//
//        boolean result = registrationForm.registerUser();
//        assertFalse(result, "The registration should fail if the email is already used.");
//    }
//
//    public static void main(String[] args) {
//        Result result = JUnitCore.runClasses(RegistrationFormTest.class);
//        System.out.println("Test run was successful: " + result.wasSuccessful());
//        for (Failure failure : result.getFailures()) {
//            System.out.println("Test failed: " + failure.toString());
//        }
//    }
//}
