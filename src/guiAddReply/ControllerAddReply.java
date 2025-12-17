package guiAddReply;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Reply;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerAddReply Class </p>
 * 
 * <p> Description: This ControllerAddReply class handles validation, permission checks,
 *  making of Reply objects, persisting them to the tb, and returning
 *  the user to the View Posts screen once a reply is made. </p>
 * 
 * * <p>This class is a <b>Controller</b> component in the MVC architecture. 
 * It is responsible for all business logic related to the "Reply to a post" 
 * user story. It mediates between the {@link ViewAddReply} and the 
 * database (Model).</p>
 * 
 *<li><b>(MVC):</b> This class is a 'Controller' component. It 
 * receives data from the View, processes it, creates a Model 
 * ({@link Reply}), and persists it.
 * <li><b>(User Stories):</b> Implements the core logic for the 
 * "Reply to a post" user story.
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz Figueroa
 * 
 */
public class ControllerAddReply {

	private static Database theDatabase = FoundationsMain.database;

	/**********
	 * <p> 
	 * Title: performCreateReply() Method. </p>
	 * 
	 * <p> Description: Protected method that validates reply content, makes
	 *  a Reply object, persists it to the db, and returns to the View Posts
	 *  gui after successfully making a reply. Returns true if creation succeeds,
	 *  otherwise false </p>
	 *  * <p><b>(Operations):</b> This is the single, 
	 * primary operation of this controller, encapsulating all logic 
	 * for the "Reply to a post" story. It is called by {@link ViewAddReply}.</p>
	 * 
	 * @param stage The primary JavaFX stage, used for navigation.
     * @param user The user creating the reply.
     * @param postId The ID of the post being replied to.
     * @param content The reply content from the View.
     * @return true if creation was successful, false otherwise.
	 */
	protected static boolean performCreateReply(Stage stage, User user, int postId, String content) {
		// reply cant be empty
		// Implements "Reply to a post" (with validation).
		// Validation check to prevent empty replies.
		if (content == null || content.trim().isEmpty()) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Validation Error");
			a.setHeaderText(null);
			a.setContentText("Reply content cannot be empty.");
			a.showAndWait();
			return false;
		}

		// makes reply
		// (MVC): Controller creates a new Model object(Reply). 
		Reply r = new Reply();
		r.setPostId(postId);
		r.setContent(content.trim());
		r.setAuthorUsername(user == null ? "<unknown>" : user.getUserName());
		r.setRead(false);

		// (MVC): Controller persists the new model
		// object via the database layer
		int newId = theDatabase.createReply(r);
		if (newId == -1) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Database Error");
			a.setHeaderText(null);
			a.setContentText("Failed to create reply. Please try again.");
			a.showAndWait();
			return false;
		}

		// (MVC): Controller navigates back to the main
		// view (ViewViewPosts) and triggers a refresh to show the new data
		// return to the view post screen and refreshes the page
		guiViewPosts.ControllerViewPosts.displayViewPosts(stage, user);
		guiViewPosts.ControllerViewPosts.refreshView();

		return true;
	}
}
