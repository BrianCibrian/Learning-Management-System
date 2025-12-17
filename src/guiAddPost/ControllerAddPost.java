package guiAddPost;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Post;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.time.Instant;

/*******
 * <p> Title: ControllerAddPost Class </p>
 * 
 * <p> Description: This ControllerAddPost class does validation, permission checks,
 *  making Post objects, persist to the Database class, and goes back
 *  to the View Posts page after creating a post </p>
 * * <p>This class is a <b>Controller</b> component in the MVC architecture. 
 * It is responsible for all business logic related to the "Create a post" 
 * user story. It mediates between the {@link ViewAddPost} and the 
 * database (Model).</p>
 * <li><b>4.1 (Packages):</b> This class is part of the 'guiAddPost' package.
 * <li><b>4.2 (MVC):</b> This class is a 'Controller' component. It 
 * receives data from the View, processes it, creates a Model 
 * ({@link Post}), and persists it.
 * <li><b>4.3 (User Stories):</b> Implements the core logic for the 
 * "Create a post" user story.
 * 
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz
 * 
 */
public class ControllerAddPost {

	private static Database theDatabase = FoundationsMain.database;

	/**********
	 * <p> 
	 * Title: performCreatePost() Method. </p>
	 * 
	 * <p> Description: Protected method for post information, manages permissions,
	 * construct and persist a Post object to the db, and return to the View Posts
	 * screen upon creation. Returns true if the post was created successfully,
	 * and false if something fails. </p>
	 * </p>This is the single, primary operation of this controller, encapsulating all logic 
	 * for the "Create a post" story. It is called by {@link ViewAddPost}.</p>
	 * @param stage The primary JavaFX stage, used for navigation.
	 * @param user The user creating the post, used for permissions.
	 * @param title The post title from the View.
	 * @param body The post body from the View.
	 * @param thread The post thread from the View.
	 * @return true if creation was successful, false otherwise.
	 */
	
	protected static boolean performCreatePost(Stage stage, User user, String title, String body, String thread) {
		// default thread "General", make it impossible to make a empty thread post
		String normalizedThread = (thread == null || thread.trim().isEmpty()) ? "General" : thread.trim();

		// prevent role2 or student users from making a post in Announcements
		// OMAR HW3 NEW: only admins can post in announcements 
		if (user != null && user.getNewRole2() && !user.getAdminRole() && "Announcements".equalsIgnoreCase(normalizedThread)) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Permission Denied");
			a.setHeaderText(null);
			a.setContentText("You do not have permission to create posts in the Announcements thread.");
			a.showAndWait();
			return false;
		}

		
		// Implements "Create a post" (with validation).
		// Validation logic to ensure required fields are not empty.
		if (title == null || title.trim().isEmpty()) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Validation Error");
			a.setHeaderText(null);
			a.setContentText("Title cannot be empty.");
			a.showAndWait();
			return false;
		}

		// makes Post object
		// (MVC): Controller creates a new Model object (Post).
		Post p = new Post();
		p.setTitle(title.trim());
		p.setBody(body == null ? "" : body.trim());
		p.setAuthorUsername(user == null ? "<unknown>" : user.getUserName());
		p.setThread(normalizedThread);
		p.setCreatedAt(Instant.now());
		p.setDeleted(false);

		// (MVC): Controller persists the new Model
		// object via the database layer.
		// Persist to database
		int newId = theDatabase.createPost(p);
		if (newId == -1) {
			Alert a = new Alert(AlertType.ERROR);
			a.setTitle("Database Error");
			a.setHeaderText(null);
			a.setContentText("Failed to create post. Please try again.");
			a.showAndWait();
			return false;
		}

		// updates Post id and returns to view post gui, refreshing the list
		p.setId(newId);
		
		// (MVC): Controller navigates back to the main
		// view (ViewViewPosts) and triggers a refresh to show the new data.
		guiViewPosts.ControllerViewPosts.displayViewPosts(stage, user);
		guiViewPosts.ControllerViewPosts.refreshView();
		return true;
	}
}
