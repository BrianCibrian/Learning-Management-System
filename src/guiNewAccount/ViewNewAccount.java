package guiNewAccount;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;


/*******
 * <p> Title: ViewNewAccount Class. </p>
 * 
 * <p> Description: The ViewNewAccount Page is used to enable a potential user with an invitation
 * code to establish an account after they have specified an invitation code on the standard login
 * page. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-19 Initial version
 *  
 */

public class ViewNewAccount {
	
	/*-********************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// This is a simple GUI login Page, very similar to the FirstAdmin login page.  The only real
	// difference is in this case we also know an email address, since it was used to send the
	// invitation to the potential user.
	protected static Label label_NewUserCreation = new Label(" User Account Creation.");
    protected static Label label_NewUserLine = new Label("Please enter a username and a password.");
    protected static TextField text_Username = new TextField();
    protected static PasswordField text_Password1 = new PasswordField();
    protected static PasswordField text_Password2 = new PasswordField();
    protected static Button button_UserSetup = new Button("User Setup");
    protected static TextField text_Invitation = new TextField();
        

	// This alert is used should the invitation code be invalid
    protected static Alert alertInvitationCodeIsInvalid = new Alert(AlertType.INFORMATION);

	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// Student Note: alert used when username is invalid
	protected static Alert alertInvalidUsername = new Alert(AlertType.INFORMATION);
	// Student Note: allert used if password is invalid 
	protected static Alert alertInvalidPassword = new Alert(AlertType.INFORMATION);

    protected static Button button_Quit = new Button("Quit");
    
    // Student Note: username requirement text
    protected static Label label_UNameRequirements = new Label("A valid username must satisfy:");
	protected static Label label_UStart = new Label();
	protected static Label label_UAllowed = new Label();
	protected static Label label_USpecialFollow = new Label();
	protected static Label label_ULength = new Label();
	
	// Student Note: password requirement text
	static protected Label validPassword = new Label();
	static protected Label label_Requirements = new Label("A valid password must satisfy:");
	static protected Label label_UpperCase = new Label();
	static protected Label label_LowerCase = new Label();
	static protected Label label_NumericDigit = new Label();	
	static protected Label label_SpecialChar = new Label();
	static protected Label label_LongEnough = new Label();
	static protected Label label_NotTooLong = new Label(); 	
	static protected Label label_PasswordsMatch = new Label();

	// These attributes are used to configure the page and populate it with this user's information
	private static ViewNewAccount theView;		// Is instantiation of the class needed?

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current logged in User
   
    protected static String theInvitationCode;	// The invitation code links to an email address
    											// and a role for this user
    protected static String emailAddress;		// Established here for use by the controller
    protected static String theRole;			// Established here for use by the controller
	public static Scene theNewAccountScene = null;	// Access to the User Update page's GUI Widgets
	

	/*-********************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayNewAccount(Stage ps, String ic) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the NewAccount page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param ic specifies the user's invitation code for this GUI and it's methods
	 * 
	 */
	public static void displayNewAccount(Stage ps, String ic) {
		// This is the only way some component of the system can cause a New User Account page to
		// appear.  The first time, the class is created and initialized.  Every subsequent call it
		// is reused with only the elements that differ being initialized.
		
		// Establish the references to the GUI and the current user
		theStage = ps;				// Save the reference to the Stage for the rest of this package
		theInvitationCode = ic;		// Establish the invitation code so it can be easily accessed
		
		if (theView == null) theView = new ViewNewAccount();
		
		text_Username.setText("");	// Clear the input fields so previously entered values do not
		text_Password1.setText("");	// appear for a new user
		text_Password2.setText("");
		
		// Fetch the role for this user
		theRole = theDatabase.getRoleGivenAnInvitationCode(theInvitationCode);
		
		if (theRole.length() == 0) {// If there is an issue with the invitation code, display a
			alertInvitationCodeIsInvalid.showAndWait();	// dialog box saying that are when it it
			return;					// acknowledged, return so the proper code can be entered
		}
		
		// Get the email address associated with the invitation code
		emailAddress = theDatabase.getEmailAddressUsingCode(theInvitationCode);
		
    	// Place all of the established GUI elements into the pane
		theRootPane.getChildren().clear();
		theRootPane.getChildren().addAll(
		    label_NewUserCreation, label_NewUserLine, text_Username,
		    text_Password1, text_Password2, button_UserSetup, button_Quit,
		    label_UNameRequirements, label_UStart, label_UAllowed, label_USpecialFollow, label_ULength,
		    label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit, label_SpecialChar,
		    label_LongEnough, label_NotTooLong, validPassword,
		    label_PasswordsMatch 
		);

		updatePasswordMatchLabel();

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: New User Account Setup");	
        theStage.setScene(theNewAccountScene);
		theStage.show();
	}
	
	/**********
	 * <p> Constructor: ViewNewAccount() </p>
	 * 
	 * <p> Description: This constructor is called just once, the first time a new account needs to
	 * be created.  It establishes all of the common GUI widgets for the page so they are only
	 * created once and reused when needed.
	 * 
	 * The do
	 * 		
	 */
	private ViewNewAccount() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theNewAccountScene = new Scene(theRootPane, width, height);
		
    	// Label to display the welcome message for the new user
    	setupLabelUI(label_NewUserCreation, "Arial", 32, width, Pos.CENTER, 0, 10);
	
    	// Label to display the  message for the first user
    	setupLabelUI(label_NewUserLine, "Arial", 24, width, Pos.CENTER, 0, 70);
		
		// Establish the text input operand asking for a username
		setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, true);
		text_Username.setPromptText("Enter the Username");
		
		// Establish the text input operand field for the password
		setupTextUI(text_Password1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, true);
		text_Password1.setPromptText("Enter the Password");
		
		// Establish the text input operand field to confirm the password
		setupTextUI(text_Password2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 260, true);
		text_Password2.setPromptText("Enter the Password Again");
		
		// If the invitation code is wrong, this alert dialog will tell the user
		alertInvitationCodeIsInvalid.setTitle("Invalid Invitation Code");
		alertInvitationCodeIsInvalid.setHeaderText("The invitation code is not valid.");
		alertInvitationCodeIsInvalid.setContentText("Correct the code and try again.");

        // Set up the account creation and login
        setupButtonUI(button_UserSetup, "Dialog", 18, 250, Pos.CENTER, 300, 510);
        button_UserSetup.setOnAction((event) -> { ControllerNewAccount.doCreateUser(); });
		
        // Enable the user to quit the application
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 550);
        button_Quit.setOnAction((event) -> {ControllerNewAccount.performQuit(); });
        
        // Student Note: Username requirement labels positions
        setupLabelUI(label_UNameRequirements, "Arial", 14, 250, Pos.BASELINE_LEFT, 370, 140);
        setupLabelUI(label_UStart, "Arial", 12, 250, Pos.BASELINE_LEFT, 370, 165);
        setupLabelUI(label_UAllowed, "Arial", 12, 250, Pos.BASELINE_LEFT, 370, 185);
        setupLabelUI(label_USpecialFollow, "Arial", 12, 250, Pos.BASELINE_LEFT, 370, 205);
        setupLabelUI(label_ULength, "Arial", 12, 250, Pos.BASELINE_LEFT, 370, 225);
        
        // Student Note: Password requirement labels positions
        setupLabelUI(label_Requirements, "Arial", 16, width, Pos.BASELINE_LEFT, 50, 300);
        setupLabelUI(label_UpperCase, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 330);
        setupLabelUI(label_LowerCase, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 355);
        setupLabelUI(label_NumericDigit, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 380);
        setupLabelUI(label_SpecialChar, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 405);
        setupLabelUI(label_LongEnough, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 430);
        setupLabelUI(label_NotTooLong, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 455);
        setupLabelUI(validPassword, "Arial", 16, width, Pos.BASELINE_LEFT, 50, 490);
        
        // Student Note: last minute label cause i forgot about it for passwords matching 
        setupLabelUI(label_PasswordsMatch, "Arial", 14, width, Pos.BASELINE_LEFT, 50, 470); 
        label_PasswordsMatch.setText("Passwords must be the same - Not yet satisfied"); 
        label_PasswordsMatch.setTextFill(Color.RED); 
        
        // Student Note; starts in not satisfied states
        resetUsernameAssessments();
        resetPasswordAssessments();
        
        // Student Note: checks if username or passwords changes in order to see if it needs to update button state
        text_Username.textProperty().addListener((obs, oldV, newV) -> {
        	updateUsernameAssessments();
        	updateUserSetupButtonState();
        });
        text_Password1.textProperty().addListener((obs, oldV, newV) -> {
        	updatePasswordAssessments();
        	updatePasswordMatchLabel();
        	updateUserSetupButtonState();
        });
        text_Password2.textProperty().addListener((obs, oldV, newV) -> {
        	updatePasswordMatchLabel();
        	updateUserSetupButtonState();
        });
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
	
	// Student Note: made to update UserSetup button and see if it follows all the requirements 
	protected static void updateUserSetupButtonState() {
		String username = text_Username.getText();
		if (username == null) username = "";
		username = username.trim();

		String p1 = text_Password1.getText();
		if (p1 == null) p1 = "";
		String p2 = text_Password2.getText();
		if (p2 == null) p2 = "";

		// checks for roles and if passwords are equal
		boolean basicOk = (theRole != null && theRole.length() > 0) && p1.equals(p2);

		if (!basicOk) {
			button_UserSetup.setDisable(true);
			return;
		}

		// checks if username is valid 
		String userErr = UsernameValidator.checkForValidUserName(username);
		if (!userErr.isEmpty()) {
			button_UserSetup.setDisable(true);
			return;
		}

		// checks if password is valid 
		String passErr = PasswordValidator.evaluatePassword(p1);
		if (!passErr.isEmpty()) {
			button_UserSetup.setDisable(true);
			return;
		}

		// everything is valid
		button_UserSetup.setDisable(false);
	}
	
	// Student Note: resets user name requirements
	static protected void resetUsernameAssessments() {
		label_UNameRequirements.setText("A valid username must satisfy:");
		label_UStart.setText("Must start with a letter - Not yet satisfied");
		label_UStart.setTextFill(Color.RED);
		label_UAllowed.setText("Only A-Z, a-z, 0-9, '-', '_', or '.' - Not yet satisfied");
		label_UAllowed.setTextFill(Color.RED);
		label_USpecialFollow.setText("Special characters must be followed by a letter or digit - Not yet satisfied");
		label_USpecialFollow.setTextFill(Color.RED);
		label_ULength.setText("Between 4 and 16 characters - Not yet satisfied");
		label_ULength.setTextFill(Color.RED);
	}
	
	// Student Note: updates username requirements
	static protected void updateUsernameAssessments() {
		String u = text_Username.getText();
		if (u == null) u = "";
		
		boolean startOk = u.length() > 0 && Character.isLetter(u.charAt(0));
		boolean lengthMin = u.length() >= 4;
		boolean lengthMax = u.length() <= 16 && u.length() > 0; 
		boolean allowed = true;
		boolean specialFollowOk = true;
		
		for (int i = 0; i < u.length(); i++) {
			char c = u.charAt(i);
			boolean isAlphaNum = Character.isLetterOrDigit(c);
			boolean isSpecial = (c == '.' || c == '-' || c == '_');
			if (!isAlphaNum && !isSpecial) {
				allowed = false;
				break;
			}
			if (isSpecial) {
				if (i == u.length() - 1) {
					specialFollowOk = false;
				} else {
					char next = u.charAt(i + 1);
					if (!Character.isLetterOrDigit(next)) {
						specialFollowOk = false;
					}
				}
			}
		}
		
		// Student Note: updates labels
		if (startOk) {
			label_UStart.setText("Must start with a letter - Satisfied");
			label_UStart.setTextFill(Color.GREEN);
		} else {
			label_UStart.setText("Must start with a letter - Not yet satisfied");
			label_UStart.setTextFill(Color.RED);
		}
				
		if (allowed) {
			label_UAllowed.setText("Only A-Z, a-z, 0-9, '-', '_', or '.' - Satisfied");
			label_UAllowed.setTextFill(Color.GREEN);
		} else {
			label_UAllowed.setText("Only A-Z, a-z, 0-9, '-', '_', or '.' - Not yet satisfied");
			label_UAllowed.setTextFill(Color.RED);
		}
				
		if (specialFollowOk) {
			label_USpecialFollow.setText("Special characters must be followed by a letter or digit - Satisfied");
			label_USpecialFollow.setTextFill(Color.GREEN);
		} else {
			label_USpecialFollow.setText("Special characters must be followed by a letter or digit - Not yet satisfied");
			label_USpecialFollow.setTextFill(Color.RED);
		}
				
		if (lengthMin && lengthMax) {
			label_ULength.setText("Between 4 and 16 characters - Satisfied");
			label_ULength.setTextFill(Color.GREEN);
		} else if (!lengthMin) {
			label_ULength.setText("Between 4 and 16 characters - Not yet satisfied (too short)");
			label_ULength.setTextFill(Color.RED);
		} else { 
			label_ULength.setText("Between 4 and 16 characters - Not yet satisfied (too long)");
			label_ULength.setTextFill(Color.RED);
		}
	}
	
	// Student Note: resets password requirements
	static protected void resetPasswordAssessments() {
		label_Requirements.setText("A valid password must satisfy the following requirements:");
	    label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
	    label_UpperCase.setTextFill(Color.RED);
	    label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
	    label_LowerCase.setTextFill(Color.RED);
	    label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
	    label_NumericDigit.setTextFill(Color.RED);
	    label_SpecialChar.setText("At least one special character - Not yet satisfied");
	    label_SpecialChar.setTextFill(Color.RED);
	    label_LongEnough.setText("At least eight characters - Not yet satisfied");
	    label_LongEnough.setTextFill(Color.RED);
	    label_NotTooLong.setText("Not more than 32 characters - Not yet satisfied");
	    label_NotTooLong.setTextFill(Color.RED);
	    validPassword.setText("");
	    label_PasswordsMatch.setText("Passwords must be the same - Not yet satisfied");
	    label_PasswordsMatch.setTextFill(Color.RED);
	}
	// Student Note: updates password requirements
	static protected void updatePasswordAssessments() {
		String p = text_Password1.getText();
		if (p == null) p = "";
		
		String err = PasswordValidator.evaluatePassword(p);
		
		if (PasswordValidator.foundUpperCase) {
			label_UpperCase.setText("At least one upper case letter - Satisfied");
			label_UpperCase.setTextFill(Color.GREEN);
		} else {
			label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
			label_UpperCase.setTextFill(Color.RED);
		}
		if (PasswordValidator.foundLowerCase) {
			label_LowerCase.setText("At least one lower case letter - Satisfied");
			label_LowerCase.setTextFill(Color.GREEN);
		} else {
			label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
			label_LowerCase.setTextFill(Color.RED);
		}
		if (PasswordValidator.foundNumericDigit) {
			label_NumericDigit.setText("At least one numeric digit - Satisfied");
			label_NumericDigit.setTextFill(Color.GREEN);
		} else {
			label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
			label_NumericDigit.setTextFill(Color.RED);
		}
		if (PasswordValidator.foundSpecialChar) {
			label_SpecialChar.setText("At least one special character - Satisfied");
			label_SpecialChar.setTextFill(Color.GREEN);
		} else {
			label_SpecialChar.setText("At least one special character - Not yet satisfied");
			label_SpecialChar.setTextFill(Color.RED);
		}
		if (PasswordValidator.foundLongEnough) {
			label_LongEnough.setText("At least eight characters - Satisfied");
			label_LongEnough.setTextFill(Color.GREEN);
		} else {
			label_LongEnough.setText("At least eight characters - Not yet satisfied");
			label_LongEnough.setTextFill(Color.RED);
		}
		if (PasswordValidator.notTooLong) {
			label_NotTooLong.setText("Not more than 32 characters - Satisfied");
			label_NotTooLong.setTextFill(Color.GREEN);
		} else {
			label_NotTooLong.setText("Not more than 32 characters - Not yet satisfied");
			label_NotTooLong.setTextFill(Color.RED);
		}
		
		if (err.isEmpty() && !p.isEmpty()) {
			validPassword.setTextFill(Color.GREEN);
			validPassword.setText("Success! The password satisfies the requirements.");
		} else if (p.isEmpty()) {
			validPassword.setText("");
		} else {
			validPassword.setTextFill(Color.RED);
			validPassword.setText("Failure! The password is not valid.");
		}
	}
	
	// Student Note: makes sure passwords matches label
	static protected void updatePasswordMatchLabel() { 
		String p1 = text_Password1.getText();
		if (p1 == null) p1 = "";
		String p2 = text_Password2.getText();
		if (p2 == null) p2 = "";
		
		if (p1.isEmpty() && p2.isEmpty()) {
			label_PasswordsMatch.setText("Passwords must be the same - Not yet satisfied");
			label_PasswordsMatch.setTextFill(Color.RED);
			return;
		}
		if (p1.equals(p2)) {
			label_PasswordsMatch.setText("Passwords match - Satisfied");
			label_PasswordsMatch.setTextFill(Color.GREEN);
		} else {
			label_PasswordsMatch.setText("Passwords must be the same - Not yet satisfied");
			label_PasswordsMatch.setTextFill(Color.RED);
		}
	}
}