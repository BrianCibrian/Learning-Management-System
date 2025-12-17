package guiDeleteUser;

import applicationMain.FoundationsMain;
import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class ControllerDeleteUser {
	
	private static Database localDatabase = FoundationsMain.database;
	
    /**********
     * <p> Method: performDeleteUser() </p>
     * 
     * <p> Description: Deletes the currently selected user from the database. </p>
     * 
     */
	protected static void performDeleteUser(String userName) {
    	// Check if valid user has been picked    	
    	if (userName == null || userName.equals("<Select a User>")) {
    		System.out.println("No valid user selected for deletion.");
    		Alert alert = new Alert(Alert.AlertType.ERROR);
    	    alert.setTitle("Invalid Selection");
    	    alert.setHeaderText(null);
    	    alert.setContentText("No valid user selected for deletion.");
    	    alert.showAndWait();
            return;
        }

        // Prevent if target is an admin
        if (localDatabase.isAdmin(userName)) {
            System.out.println("You cannot delete another admin.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Action Denied");
            alert.setHeaderText(null);
            alert.setContentText("You cannot delete another admin.");
            alert.showAndWait();
            return;
        }

        // Confirmation popup
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Are you sure you want to delete user: " + userName + "?");
        confirmAlert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        // Continue deletion if user confirms
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = localDatabase.deleteUser(userName);
            // Notify user of successful deletion
            if (success) {
                System.out.println("User deleted: " + userName);
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("User Deleted");
                info.setHeaderText(null);
                info.setContentText("User deleted: " + userName);
                info.showAndWait();
                ViewDeleteUser.updateUserList();
            } else {
                System.out.println("Failed to delete user: " + userName);
                // Show error if deletion failed
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Deletion Failed");
                error.setHeaderText(null);
                error.setContentText("Failed to delete user: " + userName);
                error.showAndWait();
            }
        } else {
        	// Notify user that deletion was cancelled
            System.out.println("Deletion cancelled");
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Deletion Cancelled");
            info.setHeaderText(null);
            info.setContentText("Deletion cancelled");
            info.showAndWait();
        }
    }


    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(
            ViewDeleteUser.myStage,
            ViewDeleteUser.myUser
        );
    }

    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.myStage);
    }

    protected static void performQuit() {
        System.exit(0);
    }
}
