//import org.junit.Before;
//import org.junit.After;
//import org.junit.Test;
//import org.junit.runner.JUnitCore;
//import org.junit.runner.Result;
//import org.junit.runner.notification.Failure;
//import org.junit.runner.notification.RunListener;
//import org.junit.runner.Description;
//
//import static org.junit.Assert.*;
//
//public class RegistrationFormTest extends RegistrationForm_GUI1 {
//
// private RegistrationForm_GUI1 registrationForm;
//
// @Before
// public void setUp() {
//  registrationForm = new RegistrationForm_GUI1();
// }
//
// @After
// public void tearDown() {
//  registrationForm.dispose();
// }
//
// @Test
// public void testPasswordHashingConsistency() {
//  String password = "TestPassword123";
//  String hashedPassword1 = registrationForm.hashPassword(password);
//  String hashedPassword2 = registrationForm.hashPassword(password);
//  assertNotNull(hashedPassword1);
//  assertEquals(hashedPassword1, hashedPassword2);
// }
//
// @Test
// public void testPasswordMismatch() {
//  registrationForm.passwordField.setText("Password1");
//  registrationForm.confirmPasswordField.setText("Password2");
//
//  SubmitButtonListener submitButtonListener = registrationForm.new SubmitButtonListener();
//  submitButtonListener.actionPerformed(null);
//
//  assertFalse("Passwords do not match!".isEmpty());
// }
//
// @Test
// public void testEmptyFieldsValidation() {
//  registrationForm.firstNameField.setText("");
//  registrationForm.lastNameField.setText("");
//  registrationForm.userNameField.setText("");
//  registrationForm.emailField.setText("");
//  registrationForm.phoneNumberField.setText("");
//  registrationForm.passwordField.setText("");
//  registrationForm.confirmPasswordField.setText("");
//
//  SubmitButtonListener submitButtonListener = registrationForm.new SubmitButtonListener();
//  submitButtonListener.actionPerformed(null);
//
//  assertFalse("All fields are required!".isEmpty());
// }
//
// @Test
// public void testSuccessfulRegistration() {
//  registrationForm.firstNameField.setText("John");
//  registrationForm.lastNameField.setText("Doe");
//  registrationForm.userNameField.setText("johndoe");
//  registrationForm.emailField.setText("johndoe@example.com");
//  registrationForm.phoneNumberField.setText("1234567890");
//  registrationForm.passwordField.setText("Password123");
//  registrationForm.confirmPasswordField.setText("Password123");
//
//  String hashedPassword = registrationForm.hashPassword("Password123");
//  assertNotNull(hashedPassword);
// }
//
// public static void main(String[] args) {
//  JUnitCore junit = new JUnitCore();
//  junit.addListener(new RunListener() {
//   @Override
//   public void testStarted(Description description) {
//    System.out.println("Started: " + description.getMethodName());
//   }
//
//   @Override
//   public void testFinished(Description description) {
//    System.out.println("Passed: " + description.getMethodName());
//   }
//
//   @Override
//   public void testFailure(Failure failure) {
//    System.out.println("Failed: " + failure.getDescription().getMethodName());
//    System.out.println("Reason: " + failure.getMessage());
//   }
//  });
//
//  Result result = junit.run(RegistrationFormTest.class);
//  System.out.println("All tests passed: " + result.wasSuccessful());
// }
//}
