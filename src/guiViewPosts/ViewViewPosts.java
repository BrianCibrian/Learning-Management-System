package guiViewPosts;

import applicationMain.FoundationsMain;
import entityClasses.Post;
import entityClasses.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*******
 * <p> Title: ViewViewPosts Class </p>
 * 
 * <p> Description: This ViewViewPosts class makes the main interface for
 * browsing, searching, filtering, and managing posts, users can view post
 * details, add replies, toggle filters, and delete
 * their own posts. </p>
 * * <p>This class is a primary <b>View</b> component in the MVC architecture. 
 * It is responsible for displaying the list of all posts and providing 
 * UI controls for interaction. It does not handle business logic, but 
 * instead delegates all actions (button clicks, double-clicks) to 
 * {@link ControllerViewPosts}.</p>
 * 
 * <li><b>(Packages):</b> This class is part of the 'guiViewPosts' package, 
 * a new GUI package for implementing the Student User Stories.</li>
 * <li><b>(MVC):</b> This class is a 'View' component, focused 
 * on UI presentation.
 * <li><b>(User Stories):</b> This view is the main hub that 
 * implements or provides entry points for ALL Student User Stories:
 * <ul>
 * <li>"View all posts" (the table itself)
 * <li>"Create a post" (Add Post button)
 * <li>"Reply to a post" (Add Reply button)
 * <li>"View post and replies" (double-clicking a post)
 * <li>"Delete post" (Delete Selected button)
 * <li>Filtering by "My Posts" and "Unread"
 * </ul>
 * 
 * @author Omar Munoz
 * @author Daniel Ortiz Figueroa
 * 
 */
public class ViewViewPosts {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private Pane theRootPane;
	private Scene theScene;
	
	/**
	 * The main table displaying the list of all posts.
	 */
	private TableView<Post> postTable = new TableView<>();
	
	/**
	 * Text field for keyword searches.
	 * Allows users to input search terms to filter the post list.
	 * */
	private TextField text_Search;
	
	/**
	 * Drop-down menu for filtering by thread.
	 * Provides a pre-defined list of threads for filtering.
	 * */
	
	private ComboBox<String> combo_Thread;
	private Label label_UnreadCount;
	private Label label_Title;
	
	/**
	 * Internal state flag for the "My Posts" filter.
	 * 
	 */
	private boolean onlyMine = false;
	
	/**
	 * The button that toggles the 'onlyMine' filter.
	 * */
	private Button button_ToggleMine;

	/**
	 * Internal state flag for the "Show Unread" filter.
	 * */
	private boolean onlyUnread = false;
	
	/**
	 * The button that toggles the 'onlyUnread' filter. 
	 * */
	private Button button_UnreadFilter;

	/**********
	 * <p> Title: ViewViewPosts() Constructor </p>
	 * 
	 * <p> Description: makes the main View Posts interface, all
	 * GUI parts, sets up event handlers for buttons, and populates
	 * the table with post data. </p>
	 * * <p><b>(Operations):</b> This constructor is the 
	 * primary operation, building the entire View. It links UI actions 
	 * (e.g., button clicks) directly to the Controller 
	 * ({@link ControllerViewPosts}).</p>
	 */
	public ViewViewPosts() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);

		label_Title = new Label("All Posts");
		label_Title.setFont(Font.font("Arial", 24));
		label_Title.setLayoutX(20);
		label_Title.setLayoutY(10);

		text_Search = new TextField();
		text_Search.setPromptText("Search title or body (leave empty for all)");
		text_Search.setLayoutX(20);
		text_Search.setLayoutY(50);
		// OMAR HW3 CHANGED: reduce search bar width to half for space
		text_Search.setMinWidth(200);

		// OMAR HW3 NEW: Changed for new Thread behavior
		combo_Thread = new ComboBox<>();
		List<String> threadList = FoundationsMain.database.getThreads();
		if (threadList == null || threadList.isEmpty()) {
			threadList = java.util.Arrays.asList("General", "Announcements", "Help", "Off-topic");
		}
		List<String> comboItems = new ArrayList<>();
		comboItems.add("All");
		comboItems.addAll(threadList);
		combo_Thread.setItems(FXCollections.observableArrayList(comboItems));
		combo_Thread.setValue("All"); // default is all threads

		// OMAR HW3 CHANGED: move combo to the left for more space
		combo_Thread.setLayoutX(260);
		combo_Thread.setLayoutY(50);
		combo_Thread.setMinWidth(160);


		Button button_Search = new Button("Search");
		// OMAR HW3 CHANGED: moved left for more space
		button_Search.setLayoutX(440);
		button_Search.setLayoutY(50);
		// (MVC): View delegates action (Search) to
		// its own reloadTable() method, which calls the Controller.
		button_Search.setOnAction(e -> reloadTable());

		Button button_AddPost = new Button("Add Post");
		// OMAR HW3 CHANGED: moved left for more space
		button_AddPost.setLayoutX(520);
		button_AddPost.setLayoutY(50);
		// Supports "Create a post" user story.
		button_AddPost.setOnAction(e -> ControllerViewPosts.openAddPost());

		label_UnreadCount = new Label();
		label_UnreadCount.setFont(Font.font("Arial", 14));
		label_UnreadCount.setLayoutX(20);
		label_UnreadCount.setLayoutY(85); 
		
		// OMAR HW3 NEW: Manage Threads button visible only to Admins
		Button button_ManageThreads = new Button("Manage Threads");
		// OMAR HW3 CHANGED: moved left for more space
		button_ManageThreads.setLayoutX(600);
		button_ManageThreads.setLayoutY(50);
		button_ManageThreads.setOnAction(e -> {
			// open the manage threads sub GUI
			guiManageThreads.ViewManageThreads.displayManageThreads((Stage) theScene.getWindow(), ControllerViewPosts.theUser);
		});
		// Add to theRootPane only if user is admin or staff
		if (ControllerViewPosts.theUser != null &&
			    (ControllerViewPosts.theUser.getAdminRole() || ControllerViewPosts.theUser.getNewRole1())) {
			theRootPane.getChildren().add(button_ManageThreads);
		}


		setupTable();

		postTable.setLayoutX(20);
		postTable.setLayoutY(115);
		postTable.setPrefSize(width - 40, height - 200);
		postTable.setOnMouseClicked(event -> {
			// Supports "View post and replies" user story.
			if (event.getClickCount() == 2) {
				Post sel = postTable.getSelectionModel().getSelectedItem();
				if (sel != null) {
					ControllerViewPosts.openPostDetail(sel.getId());
				}
			}
		});

		Button button_AddReply = new Button("Add Reply to Selected");
		button_AddReply.setLayoutX(20);
		button_AddReply.setLayoutY(height - 70);
		
		// Supports "Reply to a post" user story.
		button_AddReply.setOnAction(e -> {
			Post sel = postTable.getSelectionModel().getSelectedItem();
			if (sel == null) {
				// User-friendly validation message.
				Alert a = new Alert(Alert.AlertType.INFORMATION);
				a.setTitle("No selection");
				a.setHeaderText(null);
				a.setContentText("Please select a post first.");
				a.showAndWait();
				return;
			}
			// (MVC): View delegates action to Controller.
			ControllerViewPosts.openAddReply(sel.getId());
		});

		Button button_Refresh = new Button("Refresh");
		// OMAR HW3 CHANGED: moved left for more space
		button_Refresh.setLayoutX(160);
		button_Refresh.setLayoutY(height - 70);
		button_Refresh.setOnAction(e -> reloadTable());

		button_ToggleMine = new Button("My Posts");
		// OMAR HW3 CHANGED: moved left for more space
		button_ToggleMine.setLayoutX(280);
		button_ToggleMine.setLayoutY(height - 70);
		
		//Supports "My Posts" filter story.
		button_ToggleMine.setOnAction(e -> {
			onlyMine = !onlyMine;
			button_ToggleMine.setText(onlyMine ? "All Posts" : "My Posts");
			reloadTable();
		});

		button_UnreadFilter = new Button("Show Unread");
		// OMAR HW3 CHANGED: moved left for more space
		button_UnreadFilter.setLayoutX(400); 
		button_UnreadFilter.setLayoutY(height - 70);
		
		// Supports "Show Unread" filter story.
		button_UnreadFilter.setOnAction(e -> {
			onlyUnread = !onlyUnread;
			button_UnreadFilter.setText(onlyUnread ? "Show All" : "Show Unread");
			reloadTable();
		});

		Button button_DeleteSelected = new Button("Delete Selected");
		// OMAR HW3 CHANGED: moved left for more space
		button_DeleteSelected.setLayoutX(520); 
		button_DeleteSelected.setLayoutY(height - 70);
		
		// Supports "Delete post" user story.
		button_DeleteSelected.setOnAction(e -> {
			Post sel = postTable.getSelectionModel().getSelectedItem();
			if (sel == null) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("No selection");
				a.setHeaderText(null);
				a.setContentText("Please select a post first.");
				a.setGraphic(null);
				a.showAndWait();
				return;
			}
			
			// Non-obvious logic. This is a critical
			// permission check to ensure users can only delete their own posts.
			// This logic resides in the View, but delegates the final
			// action to the Controller.
			// OMAR HW3 NEW: changed to allow Admins to delete Posts
			// OMAR TP3 NEW: changed to allow Staff to delete Posts
			String currentUser = (ControllerViewPosts.theUser == null) ? null : ControllerViewPosts.theUser.getUserName();
			String author = sel.getAuthorUsername();
			boolean isAdminOrStaff = (ControllerViewPosts.theUser != null && (ControllerViewPosts.theUser.getAdminRole() || ControllerViewPosts.theUser.getNewRole1()));
			if (currentUser == null || author == null || (!currentUser.equals(author) && !isAdminOrStaff)) {
			    Alert a = new Alert(AlertType.ERROR);
			    a.setTitle("Action Denied");
			    a.setHeaderText(null);
			    a.setContentText("You cannot delete another user's post.");
			    a.setGraphic(null);
			    a.showAndWait();
			    return;
			} 


			
			// Confirmation dialog to prevent
			// accidental deletion, which is a good design choice
			Alert confirm = new Alert(AlertType.CONFIRMATION);
			confirm.setTitle("Confirm Delete");
			confirm.setHeaderText("Are you sure you want to delete this post?");
			confirm.setContentText("This will set the title to 'Post deleted' and clear the body.");
			confirm.setGraphic(null);
			ButtonType okBtn = new ButtonType("OK", ButtonData.OK_DONE);
			ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			confirm.getButtonTypes().setAll(okBtn, cancelBtn);
			Optional<ButtonType> result = confirm.showAndWait();
			if (result.isPresent() && result.get() == okBtn) {
				// (MVC): View delegates action to Controller.
				boolean success = ControllerViewPosts.performVisualDelete(sel.getId());
				if (success) {
					// (MVC): View updates itself on success.
					// This is a "visual delete" in the View to match
					// the action, preventing a full data reload.
					sel.setTitle("Post deleted");
					sel.setBody("");
					postTable.refresh();
				} else {
					Alert a = new Alert(AlertType.ERROR);
					a.setTitle("Deletion Failed");
					a.setHeaderText(null);
					a.setContentText("Failed to delete post. Please try again.");
					a.setGraphic(null);
					a.showAndWait();
				}
			}
		});


		Button button_Back = new Button("Back");
		// OMAR HW3 CHANGED: moved left for more space
		button_Back.setLayoutX(640); 
		button_Back.setLayoutY(height - 70);
		button_Back.setOnAction(e -> goToUserHomePage((Stage) theScene.getWindow(), ControllerViewPosts.theUser));

		theRootPane.getChildren().addAll(
				label_Title, text_Search, combo_Thread, button_Search, button_AddPost,
				label_UnreadCount, postTable, button_AddReply, button_Refresh,
				button_ToggleMine, button_UnreadFilter, button_DeleteSelected, button_Back
		);
		
		// Load initial data for "View all posts" story.
		reloadTable();
	}

	/**********
	 * <p> Title: setupTable() Method </p>
	 * 
	 * <p> Description: makes all columns used in the post table,
	 * like title, author, thread, reply counts, unread counts, and
	 * creation timestamps </p>
	 * * <p><b>(Operations):</b> This method encapsulates 
	 * the complex logic of setting up the TableView, a key part of 
	 * supporting the "View all posts" story. It defines how Model
	 * data (from {@link Post}) maps to View columns.</p>
	 * 
	 */
	private void setupTable() {
		// Column for "View all posts"
		TableColumn<Post, String> colTitle = new TableColumn<>("Title");
		// Binds table column to the 'title' 
		// attribute of the Post model.
		colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
		colTitle.setPrefWidth(300);

		// Column for "View all posts"
		TableColumn<Post, String> colAuthor = new TableColumn<>("Author");
		
		// Non-obvious code. A cell value factory
		// lambda is used instead of PropertyValueFactory to handle
		// null or empty authors gracefully, preventing ugly UI.
		colAuthor.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			String a = cell.getValue().getAuthorUsername();
			return a == null || a.isEmpty() ? "<unknown>" : a;
		}));
		colAuthor.setPrefWidth(140);

		// Column for "View all posts"
		TableColumn<Post, String> colThread = new TableColumn<>("Thread");
		colThread.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			String t = cell.getValue().getThread();
			return t == null ? "" : t;
		}));
		colThread.setPrefWidth(120);

		// Column for "View all posts" (Reply Count)
		TableColumn<Post, Integer> colReplies = new TableColumn<>("Replies");
		// Non-obvious code. This cell value factory
		// must call the Controller to fetch data (reply count) that
		// is not on the Post object itself.
		colReplies.setCellValueFactory(cell -> {
			int id = cell.getValue().getId();
			// (MVC): View calls Controller for data.
			int count = ControllerViewPosts.getReplyCountForPost(id);
			return new SimpleIntegerProperty(count).asObject();
		});
		colReplies.setPrefWidth(80);
		
		// Column for "View all posts" (Unread Count)
		TableColumn<Post, Integer> colUnreadReplies = new TableColumn<>("Unread Replies");
		// Non-obvious code. This cell value factory
		// must call the Controller to fetch data (reply count) that
		// is not on the Post object itself.
		colUnreadReplies.setCellValueFactory(cell -> {
			int id = cell.getValue().getId();
			// (MVC): View calls Controller for data.
			int unread = ControllerViewPosts.getUnreadCountForPost(id);
			return new SimpleIntegerProperty(unread).asObject();
		});
		colUnreadReplies.setPrefWidth(110);

		// Column for "View all posts" (Read Status)
		TableColumn<Post, String> colReadStatus = new TableColumn<>("Read?");
		colReadStatus.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			int id = cell.getValue().getId();
			// (MVC): View calls Controller for data.
			boolean read = ControllerViewPosts.isPostReadForCurrentUser(id);
			return read ? "Read" : "Unread";
		}));
		colReadStatus.setPrefWidth(90);

		
		// Column for "View all posts"
		TableColumn<Post, String> colCreated = new TableColumn<>("Created");
		
		colCreated.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			Instant t = cell.getValue().getCreatedAt();
			if (t == null) return "";
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault()).format(t);
		}));
		colCreated.setPrefWidth(140);

		TableColumn<Post, String> colBody = new TableColumn<>("Body (preview)");
		colBody.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(() -> {
			String b = cell.getValue().getBody();
			if (b == null) return "";
			
			return b.length() > 200 ? b.substring(0, 200) + "..." : b;
		}));
		colBody.setPrefWidth(600);

		postTable.getColumns().addAll(colTitle, colAuthor, colThread, colReplies, colUnreadReplies, colReadStatus, colCreated, colBody);
	}

	/**********
	 * <p> Title: getScene() Method </p>
	 * 
	 * <p> Description: Returns the scene for the view so it can
	 * be displayed by the controller. </p>
	 */
	protected Scene getScene() {
		return theScene;
	}

	/**********
	 * <p> Title: reloadTable() Method </p>
	 * 
	 * <p> Description: Gets and filters posts from the db based on
	 * search terms, thread, ownership, and unread status. Updates the
	 * table view </p>
	 * * <p><b>(Operations):</b> This operation is called 
	 * by the Controller (or internal event handlers) to refresh 
	 * the View's data.
	 */
	protected void reloadTable() {
		// (MVC): View requests data from Controller.
		String kw = text_Search.getText();
		String th = combo_Thread.getValue();
		if ("All".equals(th)) th = null;
		List<Post> posts = ControllerViewPosts.loadPosts(kw, th);

		// Implements "My Posts" filter.
		if (onlyMine) {
			List<Post> filtered = new ArrayList<>();
			String currentUser = (ControllerViewPosts.theUser == null) ? null : ControllerViewPosts.theUser.getUserName();
			if (currentUser != null) {
				for (Post p : posts) {
					if (currentUser.equals(p.getAuthorUsername())) {
						filtered.add(p);
					}
				}
			}
			posts = filtered;
		}

		// Implements "Show Unread" filter.
		if (onlyUnread) {
			List<Post> filteredUnread = new ArrayList<>();
			String currentUser = (ControllerViewPosts.theUser == null) ? null : ControllerViewPosts.theUser.getUserName();
			if (currentUser != null) {
				for (Post p : posts) {
					// (MVC): View calls Controller for data.
					if (!ControllerViewPosts.isPostReadForCurrentUser(p.getId())) {
						filteredUnread.add(p);
					}
				}
			} else {
				filteredUnread = posts;
			}
			posts = filteredUnread;
		}

		//(MVC): View updates its own state with new data.
		postTable.setItems(FXCollections.observableArrayList(posts));
	}
	
	/**********
	 * <p> Title: goToUserHomePage() Method </p>
	 * 
	 * <p> Description: Returns the user to their home page based
	 * on role (Admin, Staff, or Student). </p>
	 */
	private void goToUserHomePage(Stage theStage, User theUser) {
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
}
