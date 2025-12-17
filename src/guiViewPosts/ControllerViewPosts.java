package guiViewPosts;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import java.util.List;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerViewPosts Class </p>
 * 
 * <p> Description: ControllerViewPosts class controls all user actions from 
 * the ViewViewPosts GUI, handles navigation between views, gets posts and replies, 
 * updating read/unread statuses, and works with the db. </p>
 * 
 ** <p>This class is the primary <b>Controller</b> component in the MVC 
 * architecture for the post-viewing feature. It is the "brains" of the 
 * operation, mediating all communication between the {@link ViewViewPosts} 
 * (and other views) and the {@link Database} (which manages the Model 
 * objects like {@link Post} and {@link Reply}).</p>
 * 
 * <ul>
 * <li><b>(Packages):</b> This class is part of the 'guiViewPosts' package.
 * <li><b>(MVC):</b> This class is the 'Controller' component. It 
 * holds references to the View ({@code theView}) and the Model 
 * ({@code theDatabase}) and handles all data flow and logic.
 * <li><b>(User Stories):</b> This class implements the logic for 
 * all Student User Stories, either directly ({@code loadPosts}) 
 * or by navigating to other views ({@code openAddPost}).
 * </ul>
 * @author Omar Munoz
 * @author Daniel Ortiz Figueroa
 * 
 * 
 */
public class ControllerViewPosts {

	/**
     * A reference to the database, which manages all Model objects.
     * */
	private static Database theDatabase = FoundationsMain.database;
	/**
     * The main application window.
     * */
	protected static Stage theStage;
	/**
     * The currently logged-in user.
     * */
	protected static User theUser;
	/**
     * A reference to the main View this Controller manages.
     * */
	protected static ViewViewPosts theView;

	/**********
	 * <p> 
	 * Title: displayViewPosts() Method. </p>
	 * 
	 * <p> Description: shows the View Posts window. Initializes the stage, 
	 *  user reference, and sets up the view if not already created. </p>
	 *  
	 *  * <p><b>(Operations):</b> This is the main entry 
	 * point for the feature. It wires the Model (User) and View 
	 * (ViewViewPosts) together and displays the main screen.</p>
	 * 
	 * @param ps The primary JavaFX stage.
     * @param user The currently logged-in user.
	 */
	public static void displayViewPosts(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		// Singleton pattern for the View to
		// preserve its state (filters, etc.)
		if (theView == null) theView = new ViewViewPosts();
		theStage.setTitle("CSE 360 Foundation Code: View Posts");
		// (MVC): Controller sets the Scene on the Stage.
		theStage.setScene(theView.getScene());
		theStage.show();
	}

	/**********
	 * <p> 
	 * Title: openAddPost() Method. </p>
	 * 
	 * <p> Description: Opens the Add Post view to make a new post. </p>
	 * *  <p><b>(User Stories):</b> This operation 
	 * fulfills the "Create a post" story by navigating to the 
	 * "Add Post" feature.</p>
	 * <p><b>(Operations):</b> An operation that 
	 * facilitates navigation between views.</p>
	 */
	protected static void openAddPost() {
		// (MVC): Controller navigates to the AddPost view.
		guiAddPost.ViewAddPost.displayAddPost(theStage, theUser);
	}
	
	/**********
	 * <p> 
	 * Title: getRepliesForPost() Method. </p>
	 * 
	 * <p> Description: Returns a list of replies under a post. </p>
	 * * * <p><b>(Operations):</b> This operation allows 
	 * the View ({@link ViewPostDetail}) to get Model data 
	 * ({@link Reply} list) from the Controller.</p>
	 *
	 *@param postId The ID of the post to get replies for.
     *@return A {@code List} of {@link Reply} objects.
	*/
	protected static List<Reply> getRepliesForPost(int postId) {
		return theDatabase.getRepliesForPost(postId);
	}

	/**********
	 * <p> 
	 * Title: openPostDetail() Method. </p>
	 * 
	 * <p> Description: Opens the Post Detail view for a  post. 
	 *  Marks the post as read for the current user before showing details. </p>
	 *  * <p><b>(User Stories):</b> This operation 
	 * fulfills the "View post and replies" story.</p>
	 * <p><b>(Operations):</b> A navigation 
	 * operation that passes Model data ({@link Post}) to the
	 * detail view.</p>
	 * @param postId The ID of the post to open.
	 */
	protected static void openPostDetail(int postId) {
		if (theUser != null) {
			try {
				// Side-effect. Viewing a post marks it as read.
				theDatabase.markPostAsRead(postId, theUser.getUserName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// (MVC): Controller fetches Model (Post)...
		Post p = getPostById(postId);
		if (p == null) return;
		// ...and passes it to the View (ViewPostDetail) for display.
		ViewPostDetail.displayPostDetail(theStage, theUser, p);
	}

	/**********
	 * <p> 
	 * Title: openAddReply() Method. </p>
	 * 
	 * <p> Description: Opens the Add Reply view to make a reply for a post. </p>
	 * * <p><b>(User Stories):</b> This operation 
	 * fulfills the "Reply to a post" story by navigating to the 
	 * "Add Reply" feature.</p>
	 * <p><b>(Operations):</b> A navigation operation.</p>
	 * 
	 * @param postId The ID of the post to reply to.
	 */
	protected static void openAddReply(int postId) {
		// (MVC): Controller navigates to the AddReply view.
		guiAddReply.ViewAddReply.displayAddReply(theStage, theUser, postId);
	}


	/**********
	 * <p> 
	 * Title: loadPosts() Method. </p>
	 * 
	 * <p> Description: Loads posts from the database, optionally filters by keyword 
	 *  or thread. Returns all posts if no filters are applied. </p>
	 *  
	 *  * <p><b>(Operations):</b> This is the core data-access 
	 * operation for the main view, supporting the "View all posts" and 
	 * filtering stories.</p>
	 * 
	 * @param keyword The search term (or null/empty).
     * @param thread The thread to filter by (or null).
     * @return A {@code List} of {@link Post} objects.
	 */
	protected static List<Post> loadPosts(String keyword, String thread) {
		boolean noKeyword = (keyword == null || keyword.trim().isEmpty());
		boolean noThread  = (thread == null || thread.trim().isEmpty());
		// (MVC): Controller fetches Model data from the database layer.
		if (noKeyword && noThread) {
			return theDatabase.getPosts();
		} else {
			return theDatabase.searchPosts(keyword, thread);
		}
	}

	/**********
	 * <p> 
	 * Title: getReplyCountForPost() Method. </p>
	 * 
	 * <p> Description: Returns the total number of replies for a given post </p>
	 * * <p><b>(Operations):</b> Supports the 
	 * "View all posts" story by providing data for the 'Replies' column.</p>
	 * @param postId The post ID.
     * @return The total count of replies.
	 */
	protected static int getReplyCountForPost(int postId) {
		List<Reply> replies = theDatabase.getRepliesForPost(postId);
		return replies == null ? 0 : replies.size();
	}
	
	/**********
	 * <p> 
	 * Title: getUnreadCountForPost() Method. </p>
	 * 
	 * <p> Description: Returns the number of unread replies for the current user 
	 *  on a post. </p>
	 *  
	 *  * <p><b>(Operations):</b> Supports the 
	 * "View all posts" story by providing data for the 'Unread' column.</p>
	 * @param postId The post ID.
     * @return The count of unread replies for the current user.
	 */
	protected static int getUnreadCountForPost(int postId) {
		if (theUser == null) return 0;
		return theDatabase.getUnreadReplyCountForPostForUser(postId, theUser.getUserName());
	}

	/**********
	 * <p> 
	 * Title: getTotalUnreadForUser() Method. </p>
	 * 
	 * <p> Description: returns the total unread reply count for the current user 
	 *  for all posts. </p>
	 */
	protected static int getTotalUnreadForUser() {
		if (theUser == null) return 0;
		return theDatabase.getUnreadReplyCountForUser(theUser.getUserName());
	}

	/**********
	 * <p> 
	 * Title: refreshView() Method. </p>
	 * 
	 * <p> Description: Refreshes the posts view </p>
	 * * <p><b>(Operations):</b> This operation allows 
	 * other controllers (like AddPost) to tell this controller to 
	 * refresh its View, ensuring data is always current.</p>
	 */
	public static void refreshView() {
		if (theView != null) theView.reloadTable();
	}

	/**********
	 * <p> 
	 * Title: getPostById() Method. </p>
	 * 
	 * <p> Description: Retrieves a post from the db by its ID. </p>
	 */
	protected static Post getPostById(int id) {
		return theDatabase.getPostById(id); 
	}

	/**********
	 * <p> 
	 * Title: isPostReadForCurrentUser() Method. </p>
	 * 
	 * <p> Description: Checks if a post has been marked as read 
	 *  by the user. </p>
	 *  * <p><b>(Operations):</b> Supports the 
	 * "View all posts" story by providing data for the 'Read?' column.</p>
	 * @param postId The post ID.
     * @return true if the post is read, false otherwise.
	 */
	protected static boolean isPostReadForCurrentUser(int postId) {
		if (theUser == null) return false;
		try {
			return theDatabase.isPostReadByUser(postId, theUser.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**********
	 * <p> 
	 * Title: performVisualDelete() Method. </p>
	 * 
	 * <p> Description: Marks a post as visually deleted in the database 
	 *  without really removing it </p>
	 *  <p><b>(User Stories):</b> This operation 
	 * provides the core logic for the "Delete post" story.</p>
	 * <p><b>(Operations):</b> A data-modification
	 * operation called by the View.</p>
	 *
     * @param postId The post to delete.
     * @return true if successful, false otherwise.
	 */
	protected static boolean performVisualDelete(int postId) {
		try {
			// (MVC): Controller updates the Model.
			return theDatabase.updatePostToDeletedVisual(postId);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
