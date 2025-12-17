package guiViewPosts;

import applicationMain.FoundationsMain;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiViewPosts.ControllerViewPosts;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: ViewPostDetail Class </p>
 * 
 * <p> Description: This ViewPostDetail class makes the GUI for viewing a post 
 *  and its replies. It displays the post’s title, author, and content, 
 *  along with a table showing all replies, the view allows users to filter replies, 
 *  open a reply detail, or add a new reply </p>
 * * <p>This class is a <b>View</b> component in the MVC architecture. 
 * It is responsible for displaying the data of a single {@link Post} 
 * and its list of {@link Reply} objects. It delegates all actions to 
 * the {@link ControllerViewPosts} or other views.</p>
 * * <ul>
 * <li><b>(Packages):</b> This class is part of the 'guiViewPosts' package.
 * <li><b>(MVC):</b> This class is a 'View' component.
 * <li><b>(User Stories):</b> Directly implements the 
 * "View post and replies" user story. It also provides 
 * entry points for "Reply to a post" (Add Reply button) 
 * and "View reply details" (double-clicking a reply).
 * </ul>
 * @author Omar Munoz
 * 
 * @author Daniel Ortiz Figueroa
 * 
 */
public class ViewPostDetail {

	private static double width = FoundationsMain.WINDOW_WIDTH;
	private static double height = FoundationsMain.WINDOW_HEIGHT;
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

	/**********
	 * <p> 
	 * Title: displayPostDetail() Method. </p>
	 * 
	 * <p> Description: Builds and displays the Post Detail GUI. 
	 *  It sets up the layout, shows post info, lists all replies, 
	 *  and makes buttons for adding replies, filtering unread replies, 
	 *  and returning to the post list. </p>
	 *  * * <p><b>(Operations):</b> This is the main 
	 * operation of the class, responsible for building and 
	 * displaying the entire view. It is called by 
	 * {@link ControllerViewPosts}.</p>
	 * 
	 * @param stage The primary JavaFX stage.
     * @param user The currently logged-in {@link User}.
     * @param post The {@link Post} model object to display
	 */
	public static void displayPostDetail(Stage stage, User user, Post post) {
		Pane root = new Pane();
		Scene scene = new Scene(root, width, height);

		// post title label
		Label title = new Label(post.getTitle());
		title.setFont(Font.font("Arial", 24));
		title.setLayoutX(20);
		title.setLayoutY(10);

		// post author and creation time label
		String author = post.getAuthorUsername() == null || post.getAuthorUsername().isEmpty() ? "<unknown>" : post.getAuthorUsername();
		Label meta = new Label("By " + author + "	Created: " + (post.getCreatedAt() == null ? "" : dtf.format(post.getCreatedAt())));
		meta.setFont(Font.font("Arial", 14));
		meta.setLayoutX(20);
		meta.setLayoutY(50);

		// post body text area
		TextArea body = new TextArea(post.getBody() == null ? "" : post.getBody());
		body.setEditable(false);
		body.setWrapText(true);
		body.setLayoutX(20);
		body.setLayoutY(80);
		body.setPrefSize(width - 40, 180);

		
		/**
	     * The table displaying the list of replies for this post.
	     * */
		
		TableView<Reply> replyTable = new TableView<>();
		replyTable.setLayoutX(20);
		replyTable.setLayoutY(280);
		replyTable.setPrefSize(width - 40, height - 380);

		TableColumn<Reply, String> cAuthor = new TableColumn<>("Author");
		// Binds column to 'authorUsername' 
		// attribute of the Reply model.
		cAuthor.setCellValueFactory(new PropertyValueFactory<>("authorUsername"));
		cAuthor.setPrefWidth(160);

		// Column for "View post and replies"
		TableColumn<Reply, String> cCreated = new TableColumn<>("Created");
		cCreated.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			Instant t = cell.getValue().getCreatedAt();
			return t == null ? "" : dtf.format(t);
		}));
		cCreated.setPrefWidth(180);

		// Column for "View post and replies" (Read Status)
		TableColumn<Reply, String> cRead = new TableColumn<>("Read?");

		cRead.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			int id = cell.getValue().getId();
			// (MVC): View calls database (via Controller) 
			// for dynamic data.
			boolean read = FoundationsMain.database.isReplyReadByUser(id, (user == null) ? "" : user.getUserName());
			return read ? "Read" : "Unread";
		}));
		cRead.setPrefWidth(80);

		// Column for "View post and replies
		TableColumn<Reply, String> cContent = new TableColumn<>("Content");
		cContent.setCellValueFactory(new PropertyValueFactory<>("content"));
		cContent.setPrefWidth(width - 460);

		replyTable.getColumns().addAll(cAuthor, cCreated, cRead, cContent);

		// 0 = Show All, 1 = Unread Only
		final int[] filterMode = {0};

		/**********
		 * <p> 
		 * Title: reloadReplies Runnable. </p>
		 * 
		 * <p> Description: Reloads the reply table based on current filter mode.
		 *  When set to unread-only, only replies the user hasn't read will appear. </p>
		 *  
		 */
		Runnable reloadReplies = () -> {
			// (MVC): View requests data from Controller.
			List<Reply> allReplies = ControllerViewPosts.getRepliesForPost(post.getId());
			
			// Implements "Unread Replies" filter story.
			if (filterMode[0] == 1 && user != null) {
				List<Reply> unreadOnly = new ArrayList<>();
				for (Reply r : allReplies) {
					// View requests data from Model/DB.
					boolean isRead = FoundationsMain.database.isReplyReadByUser(r.getId(), user.getUserName());
					if (!isRead) unreadOnly.add(r);
				}
				replyTable.setItems(FXCollections.observableArrayList(unreadOnly));
			} else {
				replyTable.setItems(FXCollections.observableArrayList(allReplies));
			}
		};

		// Initial reply load
		reloadReplies.run();

		// Supports "View reply details" user story.
		// double-click a reply to open reply detail, ViewReplyDetail marks the reply read
		replyTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Reply sel = replyTable.getSelectionModel().getSelectedItem();
				if (sel != null) {
					// (MVC): View delegates to another View.
					ViewReplyDetail.displayReplyDetail(stage, user, sel);
					// After returning from reply detail, reload replies into the table to for any changed read flags
					reloadReplies.run();
				}
			}
		});

		/**
	     * Button to add a new reply to the current post.
	     * * "Add Reply" screen.
	     * */
		
		Button addReply = new Button("Add Reply");
		addReply.setLayoutX(20);
		addReply.setLayoutY(height - 80);
		// Supports "Reply to a post" user story.
		addReply.setOnAction(e -> {
			try {
				// (MVC): View delegates to AddReply feature.
				guiAddReply.ViewAddReply.displayAddReply(stage, user, post.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		/**
	     * Button to filter the replies table between "Show All" and "Unread Only."
	     * 
	     */
		Button button_FilterMode = new Button();
		button_FilterMode.setLayoutX(120);
		button_FilterMode.setLayoutY(height - 80);
		int initialNext = (filterMode[0] + 1) % 2;
		button_FilterMode.setText(initialNext == 0 ? "Show All" : "Unread Replies");
		// Implements "Unread Replies" filter story.
		button_FilterMode.setOnAction(e -> {
			filterMode[0] = (filterMode[0] + 1) % 2;
			reloadReplies.run();
			int next = (filterMode[0] + 1) % 2;
			button_FilterMode.setText(next == 0 ? "Show All" : "Unread Replies");
		});

		// back button to go to list of posts
		Button back = new Button("Back to Posts");
		back.setLayoutX(260);
		back.setLayoutY(height - 80);
		back.setOnAction(e -> {
			// (MVC): View tells Controller to 
			// display the main view.
			ControllerViewPosts.displayViewPosts(stage, user);
			ControllerViewPosts.refreshView();
		});

		// adds elements to pane
		root.getChildren().addAll(title, meta, body, replyTable, addReply, button_FilterMode, back);

		// sets up stage
		stage.setTitle("Post Detail");
		stage.setScene(scene);
		stage.show();
	}
}
