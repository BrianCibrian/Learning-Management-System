package guiAddFeedback;

import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: ViewAddFeedback Class </p>
 * 
 * <p> Description: The JavaFX GUI view for submitting a new feedback message. 
 * This view allows sender (student or staff) to specify a subject, content, and recipient (if staff).
 * It interacts with the {@link ControllerAddFeedback} to submit the data.</p>
 * 
 * @author Your Name
 */
public class ViewAddFeedback {
    private Stage stage;
    private User currentUser;
    private Scene scene;

    // UI Elements
    private ComboBox<String> recipientComboBox;
    private TextField subjectField;
    private TextArea contentArea;
    private Button submitButton, cancelButton;
    private Label recipientLabel;
    

    /**
     * <p> Method: ViewAddFeedback(Stage ps, User user) </p>
     *
     * <p> Description: Constructs the ViewAddFeedback instance and initializes the UI.
     * The provided Stage is used as the window for this view and the provided User
     * indicates the current user.</p>
     *
     * @param ps    the stage to host this view
     * @param user  the currently logged-in user
     */
    public ViewAddFeedback(Stage ps, User user) {
        this.stage = ps;
        this.currentUser = user;
        initialize();
    }

    /**
     * <p> Method: void initialize() </p>
     *
     * <p> Description: Sets up the JavaFX controls, layout, and event handlers for the view.
     * This method constructs the scene graph.</p>
     */
    private void initialize() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        // Recipient Selection (only visible/editable for Staff)
        recipientLabel = new Label("Recipient:");
        recipientComboBox = new ComboBox<>();
        recipientComboBox.setPromptText("Select a recipient");
        
        Label subjectLabel = new Label("Subject:");
        subjectField = new TextField();
        subjectField.setPromptText("Enter subject title");

        Label contentLabel = new Label("Message Body:");
        contentArea = new TextArea();
        contentArea.setPromptText("Write your message here...");
        contentArea.setPrefHeight(200);

        submitButton = new Button("Submit Feedback");
        submitButton.setOnAction(e -> handleSubmit());
        
        cancelButton = new Button("Cancel");
        // Action to simply close the current window
        cancelButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(recipientLabel, recipientComboBox, subjectLabel, subjectField, contentLabel, contentArea, submitButton, cancelButton);
        this.scene = new Scene(root, 500, 450);
    }

    /**
     * <p> Method: Scene getScene(boolean isStaff) </p>
     *
     * <p> Description: Returns the scene for this view. If the current user is staff (isStaff == true) the recipient dropdown will be
     * populated with all user names so staff can choose a Student. For non-staff users
     * the recipient controls are hidden.</p>
     *
     * @param isStaff boolean flag indicating if the current user has staff privileges
     * @return the JavaFX Scene representing this view
     */
    public Scene getScene(boolean isStaff) {
        if (isStaff) {
        	// Staff can choose any student as recipient
            List<User> allUsers = ControllerAddFeedback.getAllStudents(); 
            List<String> recipientUsernames = allUsers.stream()
                .map(User::getUserName)
                .collect(Collectors.toList());
            ObservableList<String> items = FXCollections.observableArrayList(recipientUsernames);
            recipientComboBox.setItems(items);
        } else {
            recipientLabel.setVisible(false);
            recipientComboBox.setVisible(false);
            recipientComboBox.setManaged(false);
        }
        return scene;
    }

    /**
     * <p> Method: void handleSubmit() </p>
     *
     * <p> Description: Validates the input fields and submits the feedback via
     * {@link ControllerAddFeedback#submitFeedback(String, String, String, String)}.
     * Provides user feedback via alerts on success or failure and closes the stage
     * when submission succeeds.</p>
     */
    private void handleSubmit() {
        String recipientUser;
        String senderUser = currentUser.getUserName();
        String subject = subjectField.getText().trim();
        String content = contentArea.getText().trim();

        if (currentUser.getNewRole1()) {
            recipientUser = recipientComboBox.getSelectionModel().getSelectedItem();
        } else {
            // Hardcode the recipient username for a student submission, 
            recipientUser = "Admin"; 
        }

        if (recipientUser == null || recipientUser.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please ensure all fields are filled and a recipient is selected.");
            alert.showAndWait();
            return;
        }

        try {
            ControllerAddFeedback.submitFeedback(senderUser, recipientUser, subject, content);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Feedback submitted successfully!");
            alert.showAndWait();
            stage.close(); 
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to submit feedback: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
