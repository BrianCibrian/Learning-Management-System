package guiAdminHome;

import database.Database;
import entityClasses.User;
import guiRole2.ViewRole2Home;

//Omar new imports
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.control.CheckMenuItem;

import javafx.scene.control.TextInputDialog; // Chuan Nguyen added this
import java.util.Random; // Chuan Nguyen added this

import guiTicketSystem.ControllerTicketSystem; // Chuan NEW
import guiViewFeedback.ControllerViewFeedback;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		//Omar Note - added for invite roles list view
		List<String> selected = new ArrayList<>();
		for (MenuItem mi : ViewAdminHome.menu_SelectRoles.getItems()) {
		    if (mi instanceof CheckMenuItem) {
		        CheckMenuItem cmi = (CheckMenuItem) mi;
		        if (cmi.isSelected()) {
		            selected.add(cmi.getText());
		        }
		    }
		}
		if (selected == null || selected.isEmpty()) {
		    ViewAdminHome.alertEmailError.setContentText("Please select at least one role to include in the invitation.");
		    ViewAdminHome.alertEmailError.showAndWait();
		    return;
		}
		String rolesCSV = String.join(",", selected);
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		// Omar - changed to see cvs of roles and for listview
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				rolesCSV);
		String msg = "Code: " + invitationCode + " for role(s) " + rolesCSV + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	//Omar note - removed for now
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	
	
	private static String generateSixDigitOtp() { //Chuan Nguyen added
		Random r = new Random();
		int n = r.nextInt(1_000_000);
		String s = Integer.toString(n);
		String otp = "000000".substring(s.length()) + s;
		return otp;
	}
	protected static void setOnetimePassword() { //Chuan Nguyen added
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Set One-Time Password");
		dialog.setHeaderText("Enter the username to receive a one-time password:");
		dialog.setContentText("Username:");
		var result = dialog.showAndWait();
		if (result.isEmpty()) return;
		String username = result.get().trim();
		
		if (username.isEmpty()) {
			ViewAdminHome.label_OtpStatus.setText("Please enter a username");
			return;
		}
		String otp = generateSixDigitOtp();
		theDatabase.updateUserPassword(username.trim(), otp);
		ViewAdminHome.label_OtpStatus.setText("One-Time Password for " + username + ": " + otp);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
	    guiDeleteUser.ViewDeleteUser.displayDeleteUser(
	            ViewAdminHome.theStage,
	            ViewAdminHome.theUser
	        );
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		guiListUsers.ViewListUsers.displayListUsers(
	            ViewAdminHome.theStage,
	            ViewAdminHome.theUser
	            );
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performViewPosts() Method. </p>
	 * 
	 * <p> Description: Opens the View Posts GUI for Admins (and, via role checks in
	 * the view-posts controller, for Staff as well). </p>
	 */
	protected static void performViewPosts() {
		guiViewPosts.ControllerViewPosts.displayViewPosts(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}
	
	//Chuan NEW
	/**********
     * <p> 
     * 
     * Title: openTicketSystem () Method. </p>
     * 
     * <p> Description: Opens the Ticket System for Admins (and, via role checks in
     * the ticket controller, for Staff as well). </p>
     */
    protected static void openTicketSystem() {
        ControllerTicketSystem.displayTicketSystem(
                ViewAdminHome.theStage,
                ViewAdminHome.theUser
        );
    }

    /**
     * <p> <b>Method:</b> openGradingSystem() </p>
     *
     * <p> Description: Opens the Grading System UI for admins.</p>
     *
     * @since 1.02
     */
    protected static void openGradingSystem() {
        // Delegate to the grading system view (keeps controller thin)
        guiGradingSystem.ViewStudentList.display(ViewAdminHome.theStage, ViewAdminHome.theUser);
    }

    /**
     * <p> <b>Method:</b> openFeedbackSystem() </p>
     *
     * <p> Description: Opens the Private Feedback Messaging UI for admins. Delegates to
     * {@link guiViewFeedback.ControllerViewFeedback#displayViewFeedback} using the admin stage and user.</p>
     */
    protected static void openFeedbackSystem() {
        ControllerViewFeedback.displayViewFeedback(ViewAdminHome.theStage, ViewAdminHome.theUser);
    }

	/**********
	 * <p>  
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
