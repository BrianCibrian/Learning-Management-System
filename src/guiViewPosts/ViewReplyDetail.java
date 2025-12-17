package guiViewPosts;

import applicationMain.FoundationsMain;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;			// OMAR HW3 NEW
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: ViewReplyDetail Class </p>
 * 
 * <p> Description: This ViewReplyDetail class displays the details of a single reply.
 * It shows the reply's author, timestamp, and content, marks it as read for the
 * current user,+ and allows can go back to the post view screen. </p>
 * 
 * * <p>This class is a <b>View</b> component in the MVC architecture. 
 * It is responsible for displaying the data of a single {@link Reply} 
 * model object. It also has a Controller-like behavior where it 
 * *writes* data (marks reply as read) upon being displayed.</p>
 * 
 * <li><b>(Packages):</b> This class is part of the 'guiViewPosts' package.
 * <li><b>(MVC):</b> This class is a 'View' component.
 * <li><b>(User Stories):</b> Directly implements the 
 * "View reply details" user story.
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz Figueroa
 * 
 */
public class ViewReplyDetail { 

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

	/**********
	 * <p> Title: displayReplyDetail() Method </p>
	 * 
	 * <p> Description: marks the reply as read for the
	 * user, creates the GUI elements to show reply details, and provides
	 * navigation back to the post. </p>
	 * 
	 * * <p><b>(Operations):</b> This is the main 
	 * operation of the class, responsible for building and 
	 * displaying the entire view. It is called by 
	 * {@link ViewPostDetail}.</p>
	 * 
	 * @param stage The primary JavaFX stage.
	 * @param user The currently logged-in {@link User}.
	 * @param reply The {@link Reply} model object to display.
	 */
	public static void displayReplyDetail(Stage stage, User user, Reply reply) {
		// Implements "View reply details" story.
		// Non-obvious side-effect. Viewing a reply
		// *immediately* marks it as read in the database. This is a
		// key piece of logic for the "Unread" feature.
		if (user != null) {
			try {
				FoundationsMain.database.markReplyAsRead(reply.getId(), user.getUserName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Pane root = new Pane();
		Scene scene = new Scene(root, width, height);

		String titleText = "Reply by " + (reply.getAuthorUsername() == null || reply.getAuthorUsername().isEmpty() ? "<unknown>" : reply.getAuthorUsername());
		Label title = new Label(titleText);
		title.setFont(Font.font("Arial", 22));
		title.setLayoutX(20);
		title.setLayoutY(10);

		String created = reply.getCreatedAt() == null ? "" : dtf.format(reply.getCreatedAt());
		Label meta = new Label("Created: " + created);
		meta.setFont(Font.font("Arial", 14));
		meta.setLayoutX(20);
		meta.setLayoutY(40);
		
		/**
		 * The text area displaying the full content of the reply.
		 * */
		TextArea body = new TextArea(reply.getContent() == null ? "" : reply.getContent());
		body.setEditable(false);
		body.setWrapText(true);
		body.setLayoutX(20);
		body.setLayoutY(80);
		body.setPrefSize(width - 40, height - 180);

		Button back = new Button("Back to Post");
		back.setLayoutX(20);
		back.setLayoutY(height - 80);
		back.setOnAction(e -> { 
			// (MVC): View navigates back to the
			// PostDetail view via the main Controller.
			ControllerViewPosts.openPostDetail(reply.getPostId());
			ControllerViewPosts.refreshView();
		});
		
		// OMAR HW3 NEW: deleteReply Button for admins
		// OMAR TP3 NEW: deleteReply Button for Staff
		Button deleteReply = new Button("Delete Reply");
		deleteReply.setLayoutX(120);
		deleteReply.setLayoutY(height - 80);
		// Only show up for admins and staff
		if (user != null && (user.getAdminRole() || user.getNewRole1())) {
			deleteReply.setOnAction(e -> {
				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
				confirm.setTitle("Confirm Delete");
				confirm.setHeaderText("Are you sure you want to delete this reply?");
				confirm.setContentText("This will replace the reply content with 'Reply deleted'.");
				java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();
				if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
					boolean ok = FoundationsMain.database.updateReplyToDeletedVisual(reply.getId());
					if (ok) {
						// return to post detail and refresh
						ControllerViewPosts.openPostDetail(reply.getPostId());
						ControllerViewPosts.refreshView();
					} else {
						Alert a = new Alert(Alert.AlertType.ERROR);
						a.setTitle("Delete Failed");
						a.setHeaderText(null);
						a.setContentText("Failed to delete reply. Please try again.");
						a.showAndWait();
					}
				}
			});
			root.getChildren().add(deleteReply);
		}


		root.getChildren().addAll(title, meta, body, back);

		stage.setTitle("Reply Detail");
		stage.setScene(scene);
		stage.show();
	}
}
