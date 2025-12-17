package guiAddPost;

import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;						// OMAR HW3 NEW
import java.util.Arrays;					// OMAR HW3 NEW
import applicationMain.FoundationsMain;		// OMAR HW3 NEW

/*******
 * <p> Title: ViewAddPost Class </p>
 * 
 * <p> Description: This ViewAddPost class provides the GUI for creating a new Post.
 *  It displays fields for title, thread, and body along with Create and Cancel buttons.
 *  The Create button delegates validation and persistence to ControllerAddPost. </p>
 * 
 * * <p>This class is a <b>View</b> component in the MVC architecture. 
 * It is a "dumb" screen that collects user input and passes it to the 
 * {@link ControllerAddPost} for processing.</p>
 * 
 * <li><b>4.1 (Packages):</b> This class is part of the 'guiAddPost' package, 
 * a new GUI package for implementing the Student User Stories.</li>
 * <li><b>4.2 (MVC):</b> This class is a 'View' component, focused on UI.
 * <li><b>4.3 (User Stories):</b> Directly implements the UI for the 
 * "Create a post" user story.
 * <li><b>4.4 (Documentation):</b> This JavaDoc documents the class 
 * purpose, attributes, and operations.
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz
 * 
 */ 
public class ViewAddPost {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static ViewAddPost theView;
	private static Scene theScene;
	private static Pane theRootPane;

	/**
	 * Text field for the new post's title.
	 * <li><b>4.3 (User Stories):</b>  Create post by student
	 * */
	private TextField text_Title;
	/**
	 * Text area for the new post's body content.
	 * 
	 * */
	private TextArea text_Body;
	/**
	 * Dropdown for selecting the post's thread.
	 *  */
	private ComboBox<String> combo_Thread;
	private Label label_Status;

	private static Stage theStage;
	private static User theUser;


	/**********
	 * <p> 
	 * Title: displayAddPost() Method. </p>
	 * 
	 * <p> Description: helps with the Create Post GUI, makes the
	 *  stage and user references and the single view if needed. </p>
	 *  * <p><b>Requirement 4.4 (Operations):</b> This static method 
	 * is the public entry point to this View, allowing the main 
	 * controller ({@link guiViewPosts.ControllerViewPosts}) to 
	 * display this screen.</p>
	 */
	
	public static void displayAddPost(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		//Singleton pattern for the View.
		if (theView == null) theView = new ViewAddPost();
		theStage.setTitle("Create New Post");
		theStage.setScene(theScene);
		theStage.show();
	}

	/**********
	 * <p> 
	 * Title: ViewAddPost() Constructor. </p>
	 * 
	 * <p> Description: makes the Create Post GUI, builds and places all widgets,
	 *  and helps with their actions, (create or cancel) </p>
	 *  * <p><b>Requirement 4.4 (Operations):</b> This constructor builds 
	 * the entire UI layout for the "Create a post" story.</p>
	 */
	private ViewAddPost() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);
		
		//page title
		Label title = new Label("Create a New Post");
		title.setFont(Font.font("Arial", 24));
		title.setLayoutX(20);
		title.setLayoutY(10);

		// title label and text field
		Label lblTitle = new Label("Title:");
		lblTitle.setFont(Font.font("Arial", 16));
		lblTitle.setLayoutX(20);
		lblTitle.setLayoutY(60);

		text_Title = new TextField();
		text_Title.setLayoutX(100);
		text_Title.setLayoutY(60);
		text_Title.setMinWidth(600);

		// tread Label and combo box
		Label lblThread = new Label("Thread:");
		lblThread.setFont(Font.font("Arial", 16));
		lblThread.setLayoutX(20);
		lblThread.setLayoutY(100);

		// OMAR HW3 NEW: Changed how threads worked in database, changed to to adapt to it
		combo_Thread = new ComboBox<>();
		// Populate threads from the database. Fall back to a built-in list if the DB returns nothing.
		List<String> threadList = FoundationsMain.database.getThreads();
		if (threadList == null || threadList.isEmpty()) {
			threadList = Arrays.asList("General", "Announcements", "Help", "Off-topic");
		}
		combo_Thread.setItems(FXCollections.observableArrayList(threadList));
		combo_Thread.setValue(threadList.contains("General") ? "General" : threadList.get(0));
		combo_Thread.setLayoutX(100);
		combo_Thread.setLayoutY(100);
		combo_Thread.setMinWidth(200);

		// body label and text area
		Label lblBody = new Label("Body:");
		lblBody.setFont(Font.font("Arial", 16));
		lblBody.setLayoutX(20);
		lblBody.setLayoutY(140);

		text_Body = new TextArea();
		text_Body.setLayoutX(100);
		text_Body.setLayoutY(140);
		text_Body.setPrefSize(width - 140, height - 330);

		/**
		 * Button to submit the new post.
		 * The primary action button that triggers the post creation logic in the controller.
		 * */
		Button btnCreate = new Button("Create Post");
		btnCreate.setFont(Font.font("Dialog", 16));
		btnCreate.setLayoutX(100);
		btnCreate.setLayoutY(height - 160);
		btnCreate.setOnAction(e -> {
			//calls controller to create post, shows message if it succeeds
			boolean ok = ControllerAddPost.performCreatePost(theStage, theUser, text_Title.getText(), text_Body.getText(), combo_Thread.getValue());
			if (ok) {
				//Provides immediate user feedback on success.
				//This is important for usability.
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Post Created");
				a.setHeaderText(null);
				a.setContentText("Post created successfully.");
				a.showAndWait();
			}
		});

		// cancel button
		Button btnCancel = new Button("Cancel");
		btnCancel.setFont(Font.font("Dialog", 16));
		btnCancel.setLayoutX(240);
		btnCancel.setLayoutY(height - 160);
		btnCancel.setOnAction(e -> {
			//go back to post list
			//main posts view via its Controller.
			guiViewPosts.ControllerViewPosts.displayViewPosts(theStage, theUser);
		});

		label_Status = new Label("");
		label_Status.setFont(Font.font("Arial", 14));
		label_Status.setLayoutX(100);
		label_Status.setLayoutY(height - 190);
		label_Status.setMinWidth(600);
		label_Status.setAlignment(Pos.BASELINE_LEFT);

		// adds everything to the root pane
		theRootPane.getChildren().addAll(title, lblTitle, text_Title, lblThread, combo_Thread, lblBody, text_Body, btnCreate, btnCancel, label_Status);
	}
}
