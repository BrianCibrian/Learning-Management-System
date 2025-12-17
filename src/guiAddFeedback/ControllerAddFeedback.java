package guiAddFeedback;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import guiViewFeedback.ControllerViewFeedback;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

/**
 * <p> Class: ControllerAddFeedback </p>
 *
 * <p> Description: Controller responsible for handling actions related to submitting new
 * feedback messages, including opening the "Add Feedback" pop-up window, sending
 * feedback to the database, and retrieving student user lists when needed. </p>
 */
public class ControllerAddFeedback {

    private static Database theDatabase = FoundationsMain.database;

    /**
     * <p> Method: displayAddFeedback(Stage ps, User user) </p>
     *
     * <p> Description: Opens a popup window with the "Submit New Feedback" form.
     * After the popup closes, the feedback view is refreshed to show any new submissions. </p>
     *
     * @param ps   the parent stage that owns the popup
     * @param user the current user who is submitting feedback
     */
    public static void displayAddFeedback(Stage ps, User user) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(ps);

        ViewAddFeedback view = new ViewAddFeedback(popupStage, user);
        popupStage.setTitle("Submit New Feedback");

        popupStage.setScene(view.getScene(user.getNewRole1()));
        popupStage.showAndWait();

        ControllerViewFeedback.refreshView();
    }

    /**
     * <p> Method: submitFeedback(String sender, String receiver, String subject, String content) </p>
     *
     * <p> Description: Sends a new feedback message to the database using the provided
     * sender, receiver, subject, and content.</p>
     *
     * @param sender   the username of the person sending the feedback
     * @param receiver the username of the intended target
     * @param subject  the subject/title of the feedback message
     * @param content  the content/body of the feedback message
     * @throws Exception if the database encounters an error while inserting the record
     */
    public static void submitFeedback(String sender, String receiver, String subject, String content) throws Exception {
        theDatabase.submitFeedback(sender, receiver, subject, content);
        ControllerViewFeedback.refreshView();
    }

    /**
     * <p> Method: List<User> getAllStudents() </p>
     *
     * <p> Description: Retrieves a list of all users from the database. Used by
     * the "Add Feedback" view when a staff user needs to select a student. </p>
     *
     * @return a List of all User objects stored in the database
     */
    public static List<User> getAllStudents() {
        return theDatabase.getAllUsers();
    }
}
