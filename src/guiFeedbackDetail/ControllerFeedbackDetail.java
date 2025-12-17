package guiFeedbackDetail;
import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Feedback;
import entityClasses.User;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * <p> Class: ControllerFeedbackDetail </p>
 *
 * <p> Description: Controller responsible for displaying the details of a single feedback
 * message for performing actions on the feedback like deletion.
 * This class interacts with the database via the shared {@link FoundationsMain#database}
 * instance and delegates UI rendering to {@link ViewFeedbackDetail}.</p>
 */
public class ControllerFeedbackDetail {
	/** Reference to the application's shared database instance. */
	private static Database theDatabase = FoundationsMain.database;
    
    /**
     * <p> Method: displayFeedbackDetail(Stage ps, User user, Feedback feedback) </p>
     *
     * <p> Description: Opens a modal pop-up window that displays the details for the
     * feedback item.</p>
     *
     * @param ps the parent Stage that owns the popup
     * @param user the current user viewing the feedback details
     * @param feedback the Feedback object whose details should be displayed
     */
    public static void displayFeedbackDetail(Stage ps, User user, Feedback feedback) {
        // Create a brand new stage for the pop-up dialog
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(ps);

        // Pass the new popupStage to the view's constructor
        ViewFeedbackDetail view = new ViewFeedbackDetail(popupStage, user, feedback); 
        popupStage.setTitle("Feedback Details: " + feedback.getSubject());
        
        popupStage.setScene(view.getScene());
        popupStage.showAndWait(); // Show as modal
    }
    
    /**
     * <p> Method: deleteFeedback(int feedbackId) </p>
     *
     * <p> Description: Deletes the feedback message from the database. After deletion,
     * the main feedback list view is refreshed to show in UI.</p>
     *
     * @param feedbackId the unique ID of the feedback message to delete
     * @throws Exception if the database operation fails
     */
    protected static void deleteFeedback(int feedbackId) throws Exception {
        theDatabase.deleteFeedback(feedbackId);
        // After deletion, the main list view must be refreshed
        guiViewFeedback.ControllerViewFeedback.refreshView();
    }
}
