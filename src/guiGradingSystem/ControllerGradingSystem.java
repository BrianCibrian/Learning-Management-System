package guiGradingSystem;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import entityClasses.User;
import entityClasses.GradingParameter;
import database.Database;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: ControllerGradingSystem Class </p>
 * * <p> Description: This controller mediates the interaction between the Grading System Views 
 * and the underlying Database. It handles logic such as data retrieval, 
 * input validation, and navigation.</p>
 * @author Daniel Ortiz Figueroa
 */

public class ControllerGradingSystem {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    // --- Navigation & Data Loading ---
    
    /*******
     * <p> Method: prepareStudentList() </p>
     * * <p> Description: Fetches all users from the database, filters them to include only students, 
     * and updates the Model's student list. This prepares the data for the Student List View.</p>
     */
    public static void prepareStudentList() {
        // Fetch all users, filter for students, update Model
        List<User> allUsers = theDatabase.getAllUsers();
        List<User> students = allUsers.stream()
                .filter(u -> u.getNewRole2() || (u.getNewRole1() == false && u.getAdminRole() == false))
                .collect(Collectors.toList());
        ModelGradingSystem.setStudentList(students);
    }
    
    /*******
     * <p> Method: prepareGradingParameters() </p>
     * * <p> Description: Fetches all grading parameters from the database and updates 
     * the Model. This prepares the data for the Grading Parameters View.</p>
     */
    public static void prepareGradingParameters() {
        // Fetch parameters from DB, update Model
        ModelGradingSystem.setParameterList(theDatabase.getGradingParameters());
    }
    
    /*******
     * <p> Method: prepareStudentGrades(User student) </p>
     * * <p> Description: Fetches the specific scores for a given student from the database 
     * and updates the Model. This prepares the data for the Student Grades View.</p>
     * * @param student The user entity representing the student whose grades are being retrieved.
     */
    public static void prepareStudentGrades(User student) {
        // Fetch scores for specific student, update Model
        ModelGradingSystem.setCurrentStudentScores(theDatabase.getStudentScores(student.getUserName()));
    }

    // --- Actions ---
    
    /*******
     * <p> Method: addGradingParameter(String name, String scoreStr) </p>
     * * <p> Description: Validates the input and adds a new grading parameter to the system. 
     * If the score input is invalid, an alert is shown.</p>
     * * @param name The name of the new grading category (e.g., "Participation").
     * @param scoreStr The maximum score/weight for this category as a string.
     */
    public static void addGradingParameter(String name, String scoreStr) {
        try {
            double score = Double.parseDouble(scoreStr);
            theDatabase.addGradingParameter(name, score);
            prepareGradingParameters(); // Refresh data
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid Number").showAndWait();
        }
    }

    /*******
     * <p> Method: deleteGradingParameter(GradingParameter gp) </p>
     * * <p> Description: Deletes a selected grading parameter from the database.</p>
     * * @param gp The GradingParameter object to be deleted.
     */
    public static void deleteGradingParameter(GradingParameter gp) {
        if (gp != null) {
            theDatabase.deleteGradingParameter(gp.getId());
            prepareGradingParameters(); // Refresh data
        }
    }

    /*******
     * <p> Method: updateStudentScore(User student, int paramId, String newScoreStr) </p>
     * * <p> Description: Validates the new score input and updates the student's grade in the database.
     * If the input is not a valid number, an alert is displayed.</p>
     * * @param student The student whose grade is being updated.
     * @param paramId The ID of the grading parameter (category).
     * @param newScoreStr The new score as a string.
     */
    public static void updateStudentScore(User student, int paramId, String newScoreStr) {
        try {
            double newScore = Double.parseDouble(newScoreStr);
            theDatabase.updateStudentScore(student.getUserName(), paramId, newScore);
            prepareStudentGrades(student); // Refresh data
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid number").showAndWait();
        }
    }

    // --- Navigation Helpers ---
    
    /*******
     * <p> Method: openGradingParameters(Stage stage, User currentUser) </p>
     * * <p> Description: Navigates the user to the Grading Parameters management screen.</p>
     * * @param stage The current JavaFX stage.
     * @param currentUser The Staff member currently logged in.
     */
    public static void openGradingParameters(Stage stage, User currentUser) {
        prepareGradingParameters();
        ViewGradingParameters.display(stage, currentUser);
    }

    /*******
     * <p> Method: openStudentGrades(Stage stage, User student, User currentUser) </p>
     * * <p> Description: Navigates the user to the specific grading screen for a selected student.</p>
     * * @param stage The current JavaFX stage.
     * @param student The student selected for grading.
     * @param currentUser The Staff member currently logged in.
     */
    public static void openStudentGrades(Stage stage, User student, User currentUser) {
        prepareStudentGrades(student);
        ViewStudentGrades.display(stage, student, currentUser);
    }
    
    /*******
     * <p> Method: backToStudentList(Stage stage, User currentUser) </p>
     * * <p> Description: Navigates the user back to the main Student List view.</p>
     * * @param stage The current JavaFX stage.
     * @param currentUser The Staff member currently logged in.
     */
    public static void backToStudentList(Stage stage, User currentUser) {
        prepareStudentList();
        ViewStudentList.display(stage, currentUser);
    }
    
    /*******
     * <p> Method: backToHome(Stage stage, User currentUser) </p>
     * * <p> Description: Navigates the user back to their Home Page.</p>
     * * @param stage The current JavaFX stage.
     * @param currentUser The Staff member currently logged in.
     */
    public static void backToHome(Stage stage, User currentUser) {
        int theRole = applicationMain.FoundationsMain.activeHomePage;
        switch (theRole) {
            case 1:
                guiAdminHome.ViewAdminHome.displayAdminHome(stage, currentUser);
                break;
            case 2:
                guiRole1.ViewRole1Home.displayRole1Home(stage, currentUser);
                break;
            case 3:
                guiRole2.ViewRole2Home.displayRole2Home(stage, currentUser);
                break;
            default:
                System.err.println("*** ERROR *** backToHome has an invalid role: " + theRole);
                System.exit(0);
        }
    }
}