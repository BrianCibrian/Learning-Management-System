package guiListUsers;

import java.util.List;
import entityClasses.User;

/*******
 * <p> Title: Controller List User </p>
 * 
 * <p> Description: Handles user input (if any) and coordinating updates between the Model and the View. </p>
 * 
 * 
 * @author Daniel Ortiz Figueroa
 * 
 * 
 */ 

public class ControllerListUsers {

    private static ControllerListUsers instance = null;
    private ModelListUsers model;
    private ViewListUsers view;

   //Private constructor
    private ControllerListUsers() {
        this.model = ModelListUsers.getInstance();
        this.view = ViewListUsers.getInstance();
    }

    //Public static method to get the single instance
    public static ControllerListUsers getInstance() {
        if (instance == null) {
            instance = new ControllerListUsers();
        }
        return instance;
    }

    /**
     * Fetches the full list of users from the Model and passes it to the View
     * to be displayed in the table.
     */
    public void populateUserTable() {
        //Get the list of all users from the Model.
        List<User> allUsers = model.getAllUsers();
        
        //Pass the list directly to the View.
        view.setUserData(allUsers);
    }
}