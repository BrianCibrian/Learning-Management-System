package guiGradingSystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entityClasses.User;
import entityClasses.GradingParameter;
import entityClasses.StudentScore;
import java.util.List;


/*******
 * <p> Title: ModelGradingSystem Class </p>
 * * <p> Description: This class serves as the Model in the MVC architecture for the Grading System.
 * It holds the observable lists of data (students, parameters, scores) that the Views bind to.
 * This allows for reactive UI updates when the Controller modifies the data state.</p>
 * * @author Daniel Ortiz Figueroa
 */

public class ModelGradingSystem {
    
    // These lists hold the data for the Views to display
	
    private static ObservableList<User> studentList = FXCollections.observableArrayList();
    
    private static ObservableList<GradingParameter> parameterList = FXCollections.observableArrayList();
   
    private static ObservableList<StudentScore> currentStudentScores = FXCollections.observableArrayList();

    // Getters
    /*@return ObservableList of User objects.
	 */
    public static ObservableList<User> getStudentList() { return studentList; }
    /* @return ObservableList of GradingParameter objects.
     */
    public static ObservableList<GradingParameter> getParameterList() { return parameterList; }
    /* @return ObservableList of StudentScore objects.
     */
    public static ObservableList<StudentScore> getCurrentStudentScores() { return currentStudentScores; }

    // Setters (used by Controller)
    /*******
     * <p> Method: setStudentList(List<User> users) </p>
     * * <p> Description: Updates the student list model with new data from the database.</p>
     * * @param users A List of User objects to populate the model.
     */
    public static void setStudentList(List<User> users) {
        studentList.clear();
        studentList.addAll(users);
    }

    /*******
     * <p> Method: setParameterList(List<GradingParameter> params) </p>
     * * <p> Description: Updates the parameter list model with new data from the database.</p>
     * * @param params A List of GradingParameter objects.
     */
    public static void setParameterList(List<GradingParameter> params) {
        parameterList.clear();
        parameterList.addAll(params);
    }

    /*******
     * <p> Method: setCurrentStudentScores(List<StudentScore> scores) </p>
     * * <p> Description: Updates the scores list model with new data for a specific student.</p>
     * * @param scores A List of StudentScore objects.
     */
    public static void setCurrentStudentScores(List<StudentScore> scores) {
        currentStudentScores.clear();
        currentStudentScores.addAll(scores);
    }
}