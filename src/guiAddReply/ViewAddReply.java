package guiAddReply;

import entityClasses.User;
import entityClasses.Post;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import applicationMain.FoundationsMain;

/*******
 * <p> Title: ViewAddReply Class </p>
 * 
 * <p> Description: This ViewAddReply class makes the GUI for making a new Reply.
 *  It shows a text area for the reply content and along with Create and Cancel buttons.
 *  The Create button sends the reply data to the ControllerAddReply class </p>
 *  * <p>This class is a <b>View</b> component in the MVC architecture. 
 * It is a "dumb" screen that collects user input (reply content) and 
 * passes it to the {@link ControllerAddReply} for processing.</p>
 * 
 * <li><b>4.1 (Packages):</b> This class is part of the 'guiAddReply' package, 
 * a new GUI package for implementing the Student User Stories.</li>
 * <li><b>4.2 (MVC):</b> This class is a 'View' component, focused on UI.
 * <li><b>4.3 (User Stories):</b> Directly implements the UI for the 
 * "Reply to a post" user story.
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz
 */
public class ViewAddReply {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static ViewAddReply theView;
	private static Scene theScene;
	private static Pane theRootPane;

	/**
     * Text area for the new reply's content.
     */
	private TextArea text_Body;
	
	/**
     * Header label to show which post is being replied to.
     */
	private Label label_Header;
	private Label label_Status;

	private static Stage theStage;
	private static User theUser;
	/**
     * The ID of the post being replied to.
     */
	private static int thePostId;

	
	
	/**********
	 * <p> 
	 * Title: displayAddReply() Method. </p>
	 * 
	 * <p> Description: makes the Add Reply gui by making the stage, user, 
	 *  and post ID references. makes sure the view is created if it doesn’t exist. </p>
	 *  * <p><b>(Operations):</b> This static method 
	 * is the public entry point to this View, allowing the main 
	 * controller ({@link guiViewPosts.ControllerViewPosts}) to 
	 * display this screen.</p>
	 * 
	 * @param ps The primary JavaFX stage.
     * @param user The currently logged-in user.
     * @param postId The ID of the post being replied to.
	 */
	
	public static void displayAddReply(Stage ps, User user, int postId) {
		theStage = ps;
		theUser = user;
		thePostId = postId;
		
		// Singleton pattern for the View.
		if (theView == null) theView = new ViewAddReply();
		theStage.setTitle("Add Reply");
		theStage.setScene(theScene);
		theStage.show();
	}

	
	/**********
	 * <p> 
	 * Title: ViewAddReply() Constructor. </p>
	 * 
	 * <p> Description: makes the Add Reply GUI, places all the interface elements, 
	 *  and makes the button actions </p>
	 *  
	 *  * <p><b>(Operations):</b> This constructor builds 
	 * the entire UI layout for the "Reply to a post" story.</p>
	 * 
	 */
	private ViewAddReply() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);

		// header label, shows what post is being replied to
		// Initial text is set here, but it is
		// dynamically updated in the displayAddReply() method.
		label_Header = new Label("Reply to Post ID: " + thePostId);
		label_Header.setFont(Font.font("Arial", 20));
		label_Header.setLayoutX(20);
		label_Header.setLayoutY(20);

		// text area for replies
		text_Body = new TextArea();
		text_Body.setLayoutX(20);
		text_Body.setLayoutY(60);
		text_Body.setPrefSize(width - 40, height - 200);

		/**
	     * Button to submit the new reply.
	     */
		Button btnCreate = new Button("Create Reply");
		btnCreate.setFont(Font.font("Dialog", 16));
		btnCreate.setLayoutX(20);
		btnCreate.setLayoutY(height - 120);
		btnCreate.setOnAction(e -> {
			// calls controller for reply creation and shows success message
			// (MVC): The View gathers its own data
			// and passes it to the Controller for processing.
			boolean ok = ControllerAddReply.performCreateReply(theStage, theUser, thePostId, text_Body.getText());
			// Provides immediate user feedback on success.
			if (ok) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Reply Created");
				a.setHeaderText(null);
				a.setContentText("Reply created successfully.");
				a.showAndWait();
			}
		});

		//cancel button to go back to posts view
		Button btnCancel = new Button("Cancel");
		btnCancel.setFont(Font.font("Dialog", 16));
		btnCancel.setLayoutX(180);
		btnCancel.setLayoutY(height - 120);
		btnCancel.setOnAction(e -> {
			// (MVC): View navigates back to the
			// main posts view via its Controller.
			guiViewPosts.ControllerViewPosts.displayViewPosts(theStage, theUser);
		});

		// status label for messages or updates
		label_Status = new Label("");
		label_Status.setFont(Font.font("Arial", 14));
		label_Status.setLayoutX(20);
		label_Status.setLayoutY(height - 160);
		label_Status.setMinWidth(600);
		label_Status.setAlignment(Pos.BASELINE_LEFT);

		//adds everything to the pane
		theRootPane.getChildren().addAll(label_Header, text_Body, btnCreate, btnCancel, label_Status);
	}
}
