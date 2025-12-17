package guiRole2;

import guiViewFeedback.ControllerViewFeedback;

/**
 * <p> Title: ControllerRole2Home Class </p>
 *
 * <p> Description: Controller for the Role2 (Student) Home Page. This class is a collection
 * of protected static methods used by {@link ViewRole2Home} to do actions
 * such as opening the account update screen, viewing posts, opening the private feedback
 * messaging system, logging out, and quitting the project.</p>
 */
public class ControllerRole2Home {
	
	/**
	 * <p> Method: performUpdate() </p>
	 *
	 * <p> Description: Open the account update dialog for the current Role2 user.
	 * Delegates to {@link guiUserUpdate.ViewUserUpdate#displayUserUpdate(Stage, entityClasses.User)}.</p>
	 *
	 * @see guiUserUpdate.ViewUserUpdate#displayUserUpdate
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	/**
	 * <p> Method: performViewPosts() </p>
	 *
	 * <p> Description: Open the application posts view for Role2 users. This delegates to
	 * {@link guiViewPosts.ControllerViewPosts#displayViewPosts(Stage, entityClasses.User)} so
	 * the student can see forum posts and threads.</p>
	 *
	 * @see guiViewPosts.ControllerViewPosts#displayViewPosts
	 */
	protected static void performViewPosts() {
		guiViewPosts.ControllerViewPosts.displayViewPosts(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}

	/**
     * <p><b>Method:</b> openFeedbackSystem()</p>
     *
     * <p>
     * Opens the Private Feedback Messaging GUI for Students (Role2). 
     * </p>
     */
	protected static void openFeedbackSystem() {
	    ControllerViewFeedback.displayViewFeedback(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}

	/**
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Log the current user out and return to the main login screen.
	 * This method delegates to {@link guiUserLogin.ViewUserLogin#displayUserLogin(Stage)}.</p>
	 *
	 * <p> After calling this method the application will show the normal login page where
	 * another user may sign in or a new account may be created (via an invitation code).</p>
	 *
	 * @see guiUserLogin.ViewUserLogin#displayUserLogin
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	/**
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminate application..</p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}
