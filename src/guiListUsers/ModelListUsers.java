package guiListUsers;

import java.util.List;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: Model List User </p>
 * 
 * <p> Description: It's responsible for fetching the list of users from the database. </p>
 * 
 * 
 * @author Daniel Ortiz Figueroa
 * 
 * 
 */ 

public class ModelListUsers {
	// The single, static instance of the Model
	private static ModelListUsers instance = null;
    private Database database;
    //  Private constructor to prevent direct instantiation
    private ModelListUsers() {
        database = new Database();
        try {
            database.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Public static method to get the single instance
    public static ModelListUsers getInstance() {
        if (instance == null) {
            instance = new ModelListUsers();
        }
        return instance;
    }

    /**
     * Fetches a list of all User objects from the database.
     * @return A List of User objects.
     */
    public List<User> getAllUsers() {
        return database.getAllUsers();
    }

}
