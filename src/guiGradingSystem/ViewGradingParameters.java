package guiGradingSystem;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entityClasses.User;
import entityClasses.GradingParameter;

/*******
 * <p> Title: ViewGradingParameters Class </p>
 * * <p> Description: This class represents the GUI for managing grading parameters.
 * It allows Staff members to add new grading categories ("Participation") and delete existing ones.
 * * @author Daniel Ortiz Figueroa
 */

public class ViewGradingParameters {
    
    private static Stage theStage;
    private static Pane theRootPane;
    private static Scene theScene;
    private static User currentUser;

    private static TableView<GradingParameter> tableParams = new TableView<>();
    private static TextField fieldName = new TextField();
    private static TextField fieldMaxScore = new TextField();

    
    /*******
     * <p> Method: display(Stage stage, User user) </p>
     * * <p> Description: Sets up and displays the Grading Parameters management screen.</p>
     * * @param stage The main application stage.
     * @param user The current logged-in user (Staff).
     */
    public static void display(Stage stage, User user) {
        theStage = stage;
        currentUser = user;
        if (theRootPane == null) initialize();
        
        // Model is already loaded by Controller before switching here
        refreshTable();
        
        theStage.setTitle("Manage Grading Parameters");
        theStage.setScene(theScene);
    }

    /*******
     * <p> Method: initialize() </p>
     * * <p> Description: Initializes the UI components, layout, and event handlers for the scene.
     */
    private static void initialize() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, 800, 600);
        //Title of the page
        Label title = new Label("Global Grading Parameters");
        title.setStyle("-fx-font-size: 20px;");
        title.setLayoutX(20); title.setLayoutY(20);
        
        //Sets up a table for the parameters and its details.
        TableColumn<GradingParameter, String> colName = new TableColumn<>("Category Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(300);

        TableColumn<GradingParameter, Double> colMax = new TableColumn<>("Max Score / Weight");
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        colMax.setPrefWidth(150);

        tableParams.getColumns().add(colName);
        tableParams.getColumns().add(colMax);

        tableParams.setLayoutX(20); tableParams.setLayoutY(60);
        tableParams.setPrefSize(450, 400);
        
        Button btnDelete = new Button("Delete Selected");
        btnDelete.setLayoutX(20); btnDelete.setLayoutY(470);
        // Action: Call Controller
        btnDelete.setOnAction(e -> {
            GradingParameter gp = tableParams.getSelectionModel().getSelectedItem();
            ControllerGradingSystem.deleteGradingParameter(gp);
            refreshTable();
        });

        Label lblAdd = new Label("Add / Edit Parameter");
        lblAdd.setLayoutX(500); lblAdd.setLayoutY(60);
        
        fieldName.setPromptText("Name (e.g. Participation)");
        fieldName.setLayoutX(500); fieldName.setLayoutY(90);
        
        fieldMaxScore.setPromptText("Max Score (e.g. 100)");
        fieldMaxScore.setLayoutX(500); fieldMaxScore.setLayoutY(130);

        Button btnAdd = new Button("Add New");
        btnAdd.setLayoutX(500); btnAdd.setLayoutY(170);
        // Action: Call Controller
        btnAdd.setOnAction(e -> {
            ControllerGradingSystem.addGradingParameter(fieldName.getText(), fieldMaxScore.getText());
            fieldName.clear(); fieldMaxScore.clear();
            refreshTable();
        });

        Button btnBack = new Button("Back to Student List");
        btnBack.setLayoutX(20); btnBack.setLayoutY(520);
        btnBack.setOnAction(e -> ControllerGradingSystem.backToStudentList(theStage, currentUser));

        theRootPane.getChildren().addAll(title, tableParams, btnDelete, lblAdd, fieldName, fieldMaxScore, btnAdd, btnBack);
    }

    //refreshes the table
    private static void refreshTable() {
        // Bind to Model
        tableParams.setItems(ModelGradingSystem.getParameterList());
    }
}