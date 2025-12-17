package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.stage.Stage;
import guiNewAccount.PasswordValidator; //Student Note reusing same java files
import guiNewAccount.UsernameValidator; //Student Note reusing same java files

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";		
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	
	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
		//Student Note: updates button state
		ViewFirstAdmin.updateUsernameAssessments(); 
		ViewFirstAdmin.updateUserSetupButtonState();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		//ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
		//student note: updates labels
		ViewFirstAdmin.updatePasswordAssessments(); 
		ViewFirstAdmin.updatePasswordMatchLabel(); 
		ViewFirstAdmin.updateUserSetupButtonState(); 
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		//ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
		//student note: updates labels
		ViewFirstAdmin.updatePasswordMatchLabel(); 
		ViewFirstAdmin.updateUserSetupButtonState(); 
		
	}
	
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		//Student Note: checks if username valid and passwords valid
		String username = ViewFirstAdmin.text_AdminUsername.getText();
		if (username == null) username = "";
		username = username.trim();
		String p1 = ViewFirstAdmin.text_AdminPassword1.getText();
		if (p1 == null) p1 = "";
		String p2 = ViewFirstAdmin.text_AdminPassword2.getText();
		if (p2 == null) p2 = "";
		//Student Note: username and password rules
		String usernameErr = UsernameValidator.checkForValidUserName(username); 
		if (!usernameErr.isEmpty()) {
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("Invalid Username"); 
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("The username is not valid."); 
			ViewFirstAdmin.alertUsernamePasswordError.setContentText(usernameErr); 
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait(); 
			return;
		}	
		String passErr = PasswordValidator.evaluatePassword(p1); 
		if (!passErr.isEmpty()) {
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("Invalid Password"); 
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("The password does not satisfy the requirements."); //New
			ViewFirstAdmin.alertUsernamePasswordError.setContentText(passErr); 
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait(); 
			return;
		}
		//Student Note: checks passwords match
		if (!p1.equals(p2)) {
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!"); //revised
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("Passwords Do Not Match"); //New
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("The two passwords must be identical."); //New
			ViewFirstAdmin.alertUsernamePasswordError.setContentText("Correct the passwords and try again."); //New
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait(); //New
			return;
		}
		//Student Note: if everything valid make user
		if (p1.compareTo(p2) == 0) {
        	User user = new User(username, p1, "", "", "", "", "", true, false, 
        			false);
            try {
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // Omar - changed to go to first login user
            guiFirstLoginUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
		}
		else { //note the same
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!");
		}
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}

