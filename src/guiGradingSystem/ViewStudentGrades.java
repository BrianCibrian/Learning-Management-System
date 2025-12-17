package guiGradingSystem;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import entityClasses.StudentScore;
import java.util.Optional;

/*******
 * <p> Title: ViewStudentGrades Class </p>
 * * <p> Description: This class represents the GUI for grading a specific student.
 * It displays a table of grading categories and scores, allowing the Staff member to 
 * edit scores via a double-click action or an edit button.</p>
 * * @author Daniel Ortiz Figueroa
 */

public class ViewStudentGrades {

	
    private static Stage theStage;
    private static Pane theRootPane;
    private static Scene theScene;
    private static User currentUser; 
    private static User studentUser; 

    private static TableView<StudentScore> tableScores = new TableView<>();
    private static Label lblStudentName = new Label();

    /*******
     * <p> Method: display(Stage stage, User student, User staff) </p>
     * * <p> Description: Sets up and displays the Grading screen for a specific student.</p>
     * * @param stage The main application stage.
     * @param student The student being graded.
     * @param staff The current logged-in Staff member.
     */
    public static void display(Stage stage, User student, User staff) {
        theStage = stage;
        studentUser = student;
        currentUser = staff;

        if (theRootPane == null) initialize();

        lblStudentName.setText("Grading: " + student.getFullName());
        // Model is already loaded by Controller before switching here
        refreshTable();
        
        theStage.setTitle("Grading - " + student.getUserName());
        theStage.setScene(theScene);
    }

    /*******
     * <p> Method: initialize() </p>
     * * <p> Description: Initializes the UI components, layout, and event handlers for the scene.
     */
    private static void initialize() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, 440, 500);

        lblStudentName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblStudentName.setLayoutX(20); lblStudentName.setLayoutY(20);
        // Sets up table of grade parameters, scores, and max-score.
        TableColumn<StudentScore, String> colParam = new TableColumn<>("Category");
        colParam.setCellValueFactory(new PropertyValueFactory<>("parameterName"));
        colParam.setPrefWidth(200);

        TableColumn<StudentScore, Double> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colScore.setPrefWidth(100);

        TableColumn<StudentScore, Double> colMax = new TableColumn<>("Max");
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        colMax.setPrefWidth(100);

        tableScores.getColumns().add(colParam);
        tableScores.getColumns().add(colScore);
        tableScores.getColumns().add(colMax);
        
        tableScores.setLayoutX(20); tableScores.setLayoutY(60);
        tableScores.setPrefSize(400, 350);

        tableScores.setRowFactory(tv -> {
            TableRow<StudentScore> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showEditDialog(row.getItem());
                }
            });
            return row;
        });
        //Edit a specific grade.
        Button btnEdit = new Button("Edit Selected Grade");
        btnEdit.setLayoutX(300); btnEdit.setLayoutY(420);
        btnEdit.setOnAction(e -> {
            StudentScore selected = tableScores.getSelectionModel().getSelectedItem();
            if(selected != null) showEditDialog(selected);
        });
        //Back to the list of Students
        Button btnBack = new Button("Back");
        btnBack.setLayoutX(20); btnBack.setLayoutY(420);
        btnBack.setOnAction(e -> ControllerGradingSystem.backToStudentList(theStage, currentUser));

        theRootPane.getChildren().addAll(lblStudentName, tableScores, btnEdit, btnBack);
    }

    /*******
     * <p> Method: showEditDialog(StudentScore ss) </p>
     * * <p> Description: Opens a dialog box allowing the user to input a new score.
     * If confirmed, it delegates the update action to the Controller.</p>
     * * @param ss The StudentScore object being edited.
     */
    private static void showEditDialog(StudentScore ss) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(ss.getScore()));
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Enter new score for " + ss.getParameterName());
        dialog.setContentText("Max Score is " + ss.getMaxScore() + ":");

        Optional<String> result = dialog.showAndWait();
        // Logic is passed to Controller
        result.ifPresent(val -> {
            ControllerGradingSystem.updateStudentScore(studentUser, ss.getParameterId(), val);
            refreshTable();
        });
    }

    //Refreshes the table view by binding it to the latest data in the Model.
    private static void refreshTable() {
        // Bind to Model
        tableScores.setItems(ModelGradingSystem.getCurrentStudentScores());
    }
}