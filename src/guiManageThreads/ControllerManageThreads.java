package guiManageThreads;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.util.List;

/*******
 * <p> Title: ControllerManageThreads Class </p>
 *
 * <p> Description: Controller for managing threads. Provides operations
 * for creating, renaming, and deleting threads. This controller validates
 * input, calls the Database methods, and drives the ViewManageThreads UI.</p>
 *
 * <p> This class is a <b>Controller</b> component in the MVC architecture.
 * It mediates between {@link ViewManageThreads} and the {@link Database}.</p>
 *
 * @author Omar Munoz
 *
 */
public class ControllerManageThreads {

	private static Database theDatabase = FoundationsMain.database;

	protected static Stage theStage;
	protected static User theUser;

	/**
	 * Display the Manage Threads view.
	 *
	 * @param stage the primary JavaFX stage
	 * @param user  the currently logged-in user (used for permissions, future extension)
	 */
	public static void displayManageThreads(Stage stage, User user) {
		theStage = stage;
		theUser = user;

		ViewManageThreads.displayManageThreads(stage, user);
	}

	/**
	 * Create a new thread with the given name, shows alert on error/creation
	 *
	 * @param name the new thread name
	 * @return true if created successfully
	 */
	protected static boolean performCreateThread(String name) {
		if (name == null || name.trim().isEmpty()) {
			showAlert(AlertType.ERROR, "Validation Error", "Thread name cannot be empty.");
			return false;
		}
		name = name.trim();
		try {
			boolean ok = theDatabase.createThread(name);
			if (!ok) {
				showAlert(AlertType.ERROR, "Create Failed", "Thread already exists or could not be created.");
				return false;
			}
			showAlert(AlertType.INFORMATION, "Thread Created", "Thread '" + name + "' created.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "Database Error", "Failed to create thread: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Rename an existing thread along with posts being updated to the new name
	 * (Database.updateThreadName is expected to handle the process).
	 *
	 * @param oldName the current thread name
	 * @param newName the new thread name
	 * @return true if rename succeeded
	 */
	protected static boolean performRenameThread(String oldName, String newName) {
		if (oldName == null || oldName.trim().isEmpty()) {
			showAlert(AlertType.ERROR, "Validation Error", "No thread selected to rename.");
			return false;
		}
		if (newName == null || newName.trim().isEmpty()) {
			showAlert(AlertType.ERROR, "Validation Error", "New thread name cannot be empty.");
			return false;
		}
		oldName = oldName.trim();
		newName = newName.trim();
		if (oldName.equalsIgnoreCase(newName)) {
			showAlert(AlertType.INFORMATION, "No Change", "The new name is the same as the old name.");
			return false;
		}
		try {
			boolean ok = theDatabase.updateThreadName(oldName, newName);
			if (!ok) {
				showAlert(AlertType.ERROR, "Rename Failed", "Could not rename thread (duplicate name or DB error).");
				return false;
			}
			showAlert(AlertType.INFORMATION, "Thread Renamed", "Thread '" + oldName + "' renamed to '" + newName + "'.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "Database Error", "Failed to rename thread: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Delete a thread. Client prevents deletion of "General" (General Thread is the backdrop thread posts will fall to if a thread is deleted) and confirms with DB response.
	 *
	 * @param name the thread name to delete
	 * @return true if deleted 
	 */
	protected static boolean performDeleteThread(String name) {
		if (name == null || name.trim().isEmpty()) {
			showAlert(AlertType.ERROR, "Validation Error", "No thread selected to delete.");
			return false;
		}
		name = name.trim();
		if ("General".equalsIgnoreCase(name)) {
			showAlert(AlertType.ERROR, "Operation Denied", "The 'General' thread cannot be deleted.");
			return false;
		}
		try {
			boolean ok = theDatabase.deleteThread(name);
			if (!ok) {
				showAlert(AlertType.ERROR, "Delete Failed", "Could not delete thread. It may not exist or DB refused.");
				return false;
			}
			showAlert(AlertType.INFORMATION, "Thread Deleted", "Thread '" + name + "' was deleted. Posts (if any) were reassigned.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "Database Error", "Failed to delete thread: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Obtain current thread list from the database.
	 *
	 * @return list of thread names or empty list
	 */
	protected static List<String> getThreadList() {
		try {
			List<String> threads = theDatabase.getThreads();
			return threads == null ? java.util.Collections.emptyList() : threads;
		} catch (Exception e) {
			e.printStackTrace();
			return java.util.Collections.emptyList();
		}
	}

	private static void showAlert(AlertType type, String title, String body) {
		Alert a = new Alert(type);
		a.setTitle(title);
		a.setHeaderText(null);
		a.setContentText(body);
		a.showAndWait();
	}
}
