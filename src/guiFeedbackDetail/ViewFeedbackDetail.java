package guiFeedbackDetail;

import entityClasses.Feedback;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.stage.Stage;


/*******
 * <p> Title: ViewFeedbackDetail Class </p>
 * 
 * <p> Description: The JavaFX GUI view for displaying the details of a single feedback message with the option 
 * to delete the message if the user is a staff member. 
 * This is the primary <b>View</b> component for detail viewing.</p>
 * 
 * @author Brian Cibrian
 */
public class ViewFeedbackDetail {
    private Stage stage;
    private Scene scene;
    private Feedback feedback;
    private User currentUser;
    private Button deleteButton;

    /**
     * <p> Method: ViewFeedbackDetail(Stage ps, User user, Feedback feedback) </p>
     *
     * <p> Description: Constructs the detail view for a single feedback item. The provided Stage
     * will be used for this view. The currentUser is used to determine permissions.</p>
     *
     * @param ps the Stage that will host this view
     * @param user the current  user
     * @param feedback the Feedback object
     */
    public ViewFeedbackDetail(Stage ps, User user, Feedback feedback) {
        this.stage = ps;
        this.currentUser = user;
        this.feedback = feedback;
        initialize();
    }

    /**
     * <p> Method: void initialize() </p>
     *
     * <p> Description: Build and layout the UI controls for displaying the feedback details.</p>
     */
    private void initialize() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestamp = feedback.getSubmissionDate().format(formatter);
        String status = feedback.isRead() ? "Read" : "Unread";

        Label subjectLabel = new Label(feedback.getSubject());
        subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label metaLabel = new Label(
            String.format("From: %s | To: %s | Submitted: %s | Status: %s",
                feedback.getSenderUsername(), feedback.getReceiverUsername(), timestamp, status)
        );
        metaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        TextArea contentArea = new TextArea(feedback.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(300);
        
        deleteButton = new Button("Delete Feedback");
        deleteButton.setOnAction(e -> handleDelete());

        HBox buttonBox = new HBox(10, deleteButton);
        
        // Logic to hide the delete button if the user is neither the owner nor staff
        boolean isOwner = currentUser.getUserName().equals(feedback.getSenderUsername());
        boolean isStaff = currentUser.getNewRole1(); // Using getNewRole1 as per previous fix

        if (!isOwner && !isStaff) {
            deleteButton.setVisible(false);
            deleteButton.setManaged(false); // Hide it completely from layout
        }

        root.getChildren().addAll(subjectLabel, metaLabel, contentArea, buttonBox);
        
        // This view could also optionally contain a "Reply" button, 
        // which would trigger a new "Add Feedback" view pre-populated with recipient/subject info.

        this.scene = new Scene(root, 600, 400);
    }
    
    /**
     * <p> Method: void handleDelete() </p>
     *
     * <p> Description: Prompt the user for confirmation and, if confirmed, request the controller
     * to delete the feedback item. On successful deletion the view's stage is closed.</p>
     */
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete this feedback message?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ControllerFeedbackDetail.deleteFeedback(feedback.getFeedbackId());
                stage.close(); 
            } catch (Exception e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to delete feedback.");
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * <p> Method: Scene getScene() </p>
     *
     * <p> Description: Returns the JavaFX {@link Scene} constructed for the feedback detail view.</p>
     *
     * @return the Scene for this view
     */
    public Scene getScene() {
        return scene;
    }
}
