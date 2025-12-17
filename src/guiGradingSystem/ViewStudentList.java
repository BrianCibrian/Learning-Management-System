package guiGradingSystem;

import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p> Title: ViewStudentList Class </p>
 * * <p> Description: This class represents the GUI for viewing the list of students available for grading.
 * It includes a search bar for filtering students by name and navigation buttons to access
 * the grading interface or the parameter management screen.</p>
 * * @author Daniel Ortiz Figueroa
 */

public class ViewStudentList {

    private static Stage theStage;
    private static Pane theRootPane;
    private static Scene theScene;
    private static User currentUser;

    private static TableView<User> tableStudents = new TableView<>();
    private static TextField fieldSearch = new TextField();
    private static Button btnBack = new Button("Back");
    private static Button btnGradeStudent = new Button("Grade Selected Student");
    private static Button btnManageParams = new Button("Manage Grading Parameters");

    /*******
     * <p> Method: display(Stage stage, User user) </p>
     * * <p> Description: Sets up and displays the Student List screen.</p>
     * * @param stage The main application stage.
     * @param user The current logged-in user (Staff).
     */
    public static void display(Stage stage, User user) {
        theStage = stage;
        currentUser = user;
        
        if (theRootPane == null) initialize();
        
        // Ask Controller to load data into the Model
        ControllerGradingSystem.prepareStudentList();
        populateTable();
        
        theStage.setTitle("Grading System - Student List");
        theStage.setScene(theScene);
        theStage.show();
    }

    /*******
     * <p> Method: initialize() </p>
     * * <p> Description: Initializes the UI components, layout, and event handlers for the scene.
     */
    private static void initialize() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, 640, 600);

        Label title = new Label("Student Grading Portal");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        title.setLayoutX(20); title.setLayoutY(20);

        // Search Bar
        fieldSearch.setPromptText("Search Student Name");
        fieldSearch.setLayoutX(20); fieldSearch.setLayoutY(60);
        fieldSearch.setPrefWidth(300);

        // Manage Parameters Button 
        btnManageParams.setLayoutX(390); btnManageParams.setLayoutY(60);
        btnManageParams.setPrefWidth(230);
        btnManageParams.setOnAction(e -> ControllerGradingSystem.openGradingParameters(theStage, currentUser));

        // Table Columns
        TableColumn<User, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setPrefWidth(200);

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUser.setPrefWidth(150);
        
        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        colEmail.setPrefWidth(250);

        tableStudents.getColumns().clear(); 
        tableStudents.getColumns().add(colName);
        tableStudents.getColumns().add(colUser);
        tableStudents.getColumns().add(colEmail);
        
        // Table Setup
        tableStudents.setLayoutX(20); tableStudents.setLayoutY(100);
        tableStudents.setPrefSize(600, 400); 

        tableStudents.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ControllerGradingSystem.openStudentGrades(theStage, row.getItem(), currentUser);
                }
            });
            return row;
        });

        // Grade Button 
        btnGradeStudent.setLayoutX(390); btnGradeStudent.setLayoutY(520);
        btnGradeStudent.setPrefWidth(230);
        btnGradeStudent.setOnAction(e -> {
            User selected = tableStudents.getSelectionModel().getSelectedItem();
            if(selected != null) ControllerGradingSystem.openStudentGrades(theStage, selected, currentUser);
        });

        // Back Button
        btnBack.setLayoutX(20); btnBack.setLayoutY(520);
        btnBack.setPrefWidth(100);
        btnBack.setOnAction(e -> ControllerGradingSystem.backToHome(theStage, currentUser));

        theRootPane.getChildren().addAll(title, fieldSearch, btnManageParams, tableStudents, btnGradeStudent, btnBack);
    }

    private static void populateTable() {
        // Bind Table to Model
        FilteredList<User> filteredData = new FilteredList<>(ModelGradingSystem.getStudentList(), p -> true);

        fieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return user.getFullName().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        tableStudents.setItems(filteredData);
    }
}