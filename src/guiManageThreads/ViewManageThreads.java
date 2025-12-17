package guiManageThreads;

import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

/*******
 * <p> Title: ViewManageThreads Class </p>
 *
 * <p> Description: GUI for managing threads (create, rename, delete).  This view
 * gives a list of existing threads, input fields for creating and renaming,
 * and buttons to perform thread management operations through {@link ControllerManageThreads}.</p>
 *
 * <p> This class is a <b>View</b> component in the MVC architecture.  It is a "dumb"
 * UI that delegates actions to the {@link ControllerManageThreads} controller.</p>
 *
 * @author Omar Munoz
 *
 */
public class ViewManageThreads {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static ViewManageThreads theView;
	private static Scene theScene;
	private static Pane theRootPane;

	protected static Stage theStage;
	protected static User theUser;

	private ListView<String> listThreads;
	private TextField text_NewThread;
	private TextField text_RenameThread;
	private Button btn_Create;
	private Button btn_Rename;
	private Button btn_Delete;
	private Button btn_Return;

	/**
	 * Public entry point to show the Manage Threads view.
	 *
	 * @param ps   JavaFX stage
	 * @param user current user (permission logic can be extended)
	 */
	public static void displayManageThreads(Stage stage, User user) {
		theStage = stage;
		theUser = user;
		if (theView == null) theView = new ViewManageThreads();
		theStage.setTitle("Manage Threads");
		theStage.setScene(theScene);
		theStage.show();
		theView.reloadThreadList();
	}

	// Constructor builds the GUI
	private ViewManageThreads() {
		theRootPane = new Pane();
		theScene = new Scene(theRootPane, width, height);

		Label title = new Label("Manage Threads");
		title.setFont(Font.font("Arial", 24));
		title.setLayoutX(20);
		title.setLayoutY(10);

		listThreads = new ListView<>();
		listThreads.setLayoutX(20);
		listThreads.setLayoutY(60);
		listThreads.setPrefSize(300, height - 180);

		// Create new thread area
		Label lblNew = new Label("Create new thread:");
		lblNew.setLayoutX(350);
		lblNew.setLayoutY(60);
		lblNew.setFont(Font.font("Arial", 16));

		text_NewThread = new TextField();
		text_NewThread.setLayoutX(350);
		text_NewThread.setLayoutY(90);
		text_NewThread.setMinWidth(300);

		btn_Create = new Button("Create Thread");
		btn_Create.setLayoutX(350);
		btn_Create.setLayoutY(130);
		btn_Create.setOnAction(e -> {
			String name = text_NewThread.getText();
			boolean ok = ControllerManageThreads.performCreateThread(name);
			if (ok) {
				// Clear input and refresh list so new thread show
				text_NewThread.clear();
				reloadThreadList();
				// Select the newly created thread in the list
				listThreads.getSelectionModel().select(name);
				// Refresh View Posts GUI too
				guiViewPosts.ControllerViewPosts.refreshView();
			}
		});

		// Rename selected thread area
		Label lblRename = new Label("Rename selected thread:");
		lblRename.setLayoutX(350);
		lblRename.setLayoutY(190);
		lblRename.setFont(Font.font("Arial", 16));

		text_RenameThread = new TextField();
		text_RenameThread.setLayoutX(350);
		text_RenameThread.setLayoutY(220);
		text_RenameThread.setMinWidth(300);

		btn_Rename = new Button("Rename Thread");
		btn_Rename.setLayoutX(350);
		btn_Rename.setLayoutY(260);
		btn_Rename.setOnAction(e -> {
			String selected = listThreads.getSelectionModel().getSelectedItem();
			String newName = text_RenameThread.getText();
			boolean ok = ControllerManageThreads.performRenameThread(selected, newName);
			if (ok) {
				// Clear input and refresh list so new thread show
				text_RenameThread.clear();
				reloadThreadList();
				// Select the renamed thread in the list
				listThreads.getSelectionModel().select(newName);
				// Refresh View Posts GUI too
				guiViewPosts.ControllerViewPosts.refreshView();
			}
		});

		// Delete selected thread
		btn_Delete = new Button("Delete Selected Thread");
		btn_Delete.setLayoutX(350);
		btn_Delete.setLayoutY(320);
		btn_Delete.setOnAction(e -> {
			String selected = listThreads.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Alert a = new Alert(Alert.AlertType.INFORMATION);
				a.setTitle("No selection");
				a.setHeaderText(null);
				a.setContentText("Please select a thread first.");
				a.showAndWait();
				return;
			}
			// Confirm delete
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Confirm Delete");
			confirm.setHeaderText("Delete thread '" + selected + "'?");
			confirm.setContentText("Posts in this thread will be reassigned to 'General' before deletion (if applicable).");
			confirm.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					boolean ok = ControllerManageThreads.performDeleteThread(selected);
					if (ok) {
						// Refresh list
						reloadThreadList();
						// Refresh View Posts GUI too
						guiViewPosts.ControllerViewPosts.refreshView(); 
					}
				}
			});
		});

		// Return button
		btn_Return = new Button("Return");
		btn_Return.setLayoutX(350);
		btn_Return.setLayoutY(height - 80);
		btn_Return.setOnAction(e -> {
			// Go back to posts list
			guiViewPosts.ControllerViewPosts.displayViewPosts(theStage, theUser);
			guiViewPosts.ControllerViewPosts.refreshView();
		});

		// Put items on the pane
		theRootPane.getChildren().addAll(title, listThreads, lblNew, text_NewThread, btn_Create,
				lblRename, text_RenameThread, btn_Rename, btn_Delete, btn_Return);
	}

	/**
	 * Reloads current threads from database into the list view.
	 */
	protected void reloadThreadList() {
		List<String> threads = ControllerManageThreads.getThreadList();
		listThreads.setItems(FXCollections.observableArrayList(threads));
	}

	/**
	 * Return the JavaFX scene so a controller can set it on the stage.
	 *
	 * @return the Scene
	 */
	protected Scene getScene() {
		return theScene;
	}
}
