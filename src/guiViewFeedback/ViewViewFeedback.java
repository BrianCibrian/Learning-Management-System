package guiViewFeedback;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import entityClasses.Feedback;
import java.util.List;

/*******
 * <p> Title: ViewViewFeedback Class </p>
 * 
 * <p> Description: The JavaFX GUI view for displaying a list of feedback items. 
 * This is the primary <b>View</b> component in the MVC architecture. 
 * It observes the user and passes control to the {@link ControllerViewFeedback}.</p>
 * 
 * @author Brian Cibrian
 */
public class ViewViewFeedback {
	private Scene scene;
    private ListView<Feedback> feedbackListView;
    private Button addButton, detailButton, refreshButton, backButton;
    private TextField studentFilterField;
    private CheckBox unreadFilterCheckbox;
    private boolean isStaffView = false;

    /**
     * <p> Method: ViewViewFeedback() </p>
     * 
     * <p> Description: Default constructor that prepares the view.</p>
     */
    public ViewViewFeedback() {
        initialize();
    }

    /**
     * <p> Method: initialize() </p>
     * 
     * <p> Description: Build and lay out the feedback
     * list view. This includes the title label, the ListView used to display Feedback
     * objects, action buttons (submit, view details, refresh, back) and filter controls.</p>
     * 
     */
    private void initialize() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Private Feedback Messages");
        root.setTop(titleLabel);

        feedbackListView = new ListView<>();
        root.setCenter(feedbackListView);

        addButton = new Button("Submit New Feedback");
        addButton.setOnAction(e -> ControllerViewFeedback.openAddFeedback());

        detailButton = new Button("View Details");
        detailButton.setOnAction(e -> {
            Feedback selected = feedbackListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ControllerViewFeedback.openFeedbackDetail(selected.getFeedbackId());
            }
        });

        refreshButton = new Button("Refresh List");
        refreshButton.setOnAction(e -> refreshFeedbackList());

        unreadFilterCheckbox = new CheckBox("Show Unread Only");
        unreadFilterCheckbox.setOnAction(e -> refreshFeedbackList());
        
        studentFilterField = new TextField();
        studentFilterField.setPromptText("Filter by student username (Staff only)");
        studentFilterField.setOnAction(e -> refreshFeedbackList());
        
        backButton = new Button("Back to Home");
        backButton.setOnAction(e -> ControllerViewFeedback.goBackToHome());

        VBox bottomBox = new VBox(10, new HBox(10, addButton, detailButton, refreshButton, backButton), new HBox(10, unreadFilterCheckbox, studentFilterField));
        root.setBottom(bottomBox);

        this.scene = new Scene(root, 750, 450);
    }

    /**
     * <p> Method: configureRoleSpecificUI(boolean isStaff) </p>
     * 
     * <p> Description: Adjust UI elements' visibility and layout depending on if
     * the current user should be shown staff controls (e.g., the student filter).</p>
     *
     * @param isStaff true if the current user is staff (Role1) and should see staff-only controls
     */
    public void configureRoleSpecificUI(boolean isStaff) {
        this.isStaffView = isStaff;
        // Adjust UI visibility based on role
        studentFilterField.setVisible(isStaff);
        if (!isStaff) {
        	// Remove from layout flow for students
            studentFilterField.setManaged(false);
        }
    }
    
    /**
     * <p> Method: refreshFeedbackList() </p>
     * 
     * <p> Description: Re-query the controller for an updated list of feedback items using the
     * current filter controls and replace
     * the contents of the ListView.</p>
     */
    public void refreshFeedbackList() {
        // Get filter criteria from the view controls
        String studentFilter = isStaffView ? studentFilterField.getText().trim() : null;
        boolean unreadOnly = unreadFilterCheckbox.isSelected();

        List<Feedback> feedbackList = ControllerViewFeedback.loadFeedback(studentFilter, unreadOnly);
        feedbackListView.getItems().setAll(feedbackList);
    }

    /**
     * <p> Method: Scene getScene() </p>
     * 
     * <p> Description: Returns the JavaFX Scene that contains this view.</p>
     * 
     * @return the Scene containing the feedback list UI
     */
    public Scene getScene() {
        return scene;
    }
}
