package guiViewFeedback;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Feedback;
import entityClasses.User;
import guiFeedbackDetail.ControllerFeedbackDetail;

import java.util.List;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerViewFeedback Class </p>
 * 
 * <p> Description: Controller for the feedback viewing system. 
 * It manages user actions, data flow between the view (ViewViewFeedback) and the database 
 * (which manages Model objects like Feedback). </p>
 * 
 * <p>This class is the primary <b>Controller</b> component in the MVC 
 * architecture for the feedback feature. It mediates communication between the 
 * {@link ViewViewFeedback} and the {@link Database}.</p>
 * 
 * @author Brian Cibrian
 */
public class ControllerViewFeedback {

	private static Database theDatabase = FoundationsMain.database;
	protected static Stage theStage;
	protected static User theUser; 
	protected static ViewViewFeedback theView;

	/**
	 * <p> Method: displayViewFeedback(Stage ps, User user) </p>
	 *
	 * <p> Description: Initialize and display the feedback main view. This sets up
	 * the controller's references to the stage, user, and view.</p>
	 *
	 * @param ps the primary Stage used to display the feedback view
	 * @param user the currently logged-in user
	 */
	public static void displayViewFeedback(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		if (theView == null) theView = new ViewViewFeedback();
        
        // Get the scene
		theStage.setScene(theView.getScene());
        
        // Configure the view's internal UI elements based on the role
        theView.configureRoleSpecificUI(user.getNewRole1());
        
		theStage.setTitle("LMS: Private Feedback Messages");
		theStage.show();
        refreshView(); 
	}
    
    /**
     * <p> Method: openAddFeedback() </p>
     *
     * <p> Description: Open the "Add Feedback" dialog using the current stage
     * and user. Delegates to {@link guiAddFeedback.ControllerAddFeedback}.</p>
     */
    protected static void openAddFeedback() {
        guiAddFeedback.ControllerAddFeedback.displayAddFeedback(theStage, theUser);
    }
    
    /**
     * <p> Title: goBackToHome() Method. </p>
     * <p> Description: Navigates back to the appropriate user home page (Role1 or Role2) 
     * based on the user's role configuration. If no recognized role is present the method
     * falls back to the user login view.</p>
     */
    protected static void goBackToHome() {
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		switch (theRole) {
			case 1:
				guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
				break;
			case 2:
				guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
				break;
			case 3:
				guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
				break;
			default:
				System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
				System.exit(0);
		}    
    }
    
    /**
     * <p> Method: openFeedbackDetail(int feedbackId) </p>
     *
     * <p> Description: Load a specific feedback item by ID, mark it read if the current
     * user is the receiver and the message was unread, and then display the feedback
     * detail dialog via {@link ControllerFeedbackDetail}.</p>
     *
     * @param feedbackId the unique ID of the feedback message to open
     */
    protected static void openFeedbackDetail(int feedbackId) {
        Feedback f = theDatabase.getFeedbackById(feedbackId);
        if (f == null) return;
        
        if (!f.isRead() && f.getReceiverUsername().equals(theUser.getUserName())) {
            try {
                theDatabase.markRead(feedbackId); 
                f.setRead(true);
                refreshView(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        guiFeedbackDetail.ControllerFeedbackDetail.displayFeedbackDetail(theStage, theUser, f);
    }

    /**
     * <p> Method: loadFeedback(String filterStudent, boolean filterUnreadOnly) </p>
     *
     * <p> Description: Loads feedback based on the user's role and view filters.
     * This method correctly calls the specific database methods you defined (getAllFeedback/getFeedbackForUser).</p>
     *
     * @param filterStudent Username filter (staff only)
     * @param filterUnreadOnly Boolean filter for read status
     * @return List of relevant Feedback objects
     */
	protected static List<Feedback> loadFeedback(String filterStudent, boolean filterUnreadOnly) {
        if (theUser.getNewRole1()) {
            // Staff: calls getAllFeedback with optional filters
            return theDatabase.getAllFeedback(filterStudent, filterUnreadOnly);
        } else {
            // Student: calls getFeedbackForUser for only their username
            return theDatabase.getFeedbackForUser(theUser.getUserName(), filterUnreadOnly);
        }
	}
    
    /**
     * <p> Method: refreshView() </p>
     *
     * <p> Description: Refresh the current view by asking {@link ViewViewFeedback} to
     * reload and re-render the list of feedback messages.</p>
     */
	public static void refreshView() {
		if (theView != null) {
			theView.refreshFeedbackList();
		}
	}
}
