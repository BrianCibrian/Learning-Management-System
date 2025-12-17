package guiListUsers;

import entityClasses.User;
import guiAdminHome.ViewAdminHome;
import javafx.collections.FXCollections;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: View List User </p>
 * 
 * <p> Description: Responsible for the presentation layer (the user interface).  </p>
 * 
 * 
 * @author Daniel Ortiz Figueroa
 * 
 * 
 */ 

public class ViewListUsers {

    private static ViewListUsers instance = null;
    private static Stage theStage;
    private static User currentUser;

    //These are instance fields
    private Scene scene;
    private Pane rootPane;
    private TableView<User> userTable = new TableView<>();

    // Private constructor 
    private ViewListUsers() {}

    /**
     * @return The single instance of the ViewListUsers class.
     */
    public static ViewListUsers getInstance() {
        if (instance == null) {
            instance = new ViewListUsers();
            instance.initializeView(); // Set up the UI components once
        }
        return instance;
    }

 
    public static void displayListUsers(Stage stage, User user) {
        theStage = stage;
        currentUser = user;
        
        // Get the instance of the view
        ViewListUsers view = getInstance();
        
        // Tell the controller to load data into the table
        ControllerListUsers.getInstance().populateUserTable();

        theStage.setTitle("List All Users");
        theStage.setScene(view.scene); // Use the scene created by the instance
        theStage.show();
    }

    /**
     * Sets up the UI components. 
     */
private void initializeView() {
    rootPane = new Pane();
    scene = new Scene(rootPane, 650, 500);

    // Define Table Columns using PropertyValueFactory for all columns
    TableColumn<User, String> userCol = new TableColumn<>("Username");
    userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

    TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

    TableColumn<User, String> emailCol = new TableColumn<>("Email Address");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));

    TableColumn<User, String> rolesCol = new TableColumn<>("Roles");
    rolesCol.setCellValueFactory(new PropertyValueFactory<>("rolesAsString"));

    // Set column widths
    userCol.setPrefWidth(120);
    nameCol.setPrefWidth(150);
    emailCol.setPrefWidth(200);
    rolesCol.setPrefWidth(150);
    
    userTable.getColumns().add(userCol);
    userTable.getColumns().add(nameCol);
    userTable.getColumns().add(emailCol);
    userTable.getColumns().add(rolesCol);
    
    Label title = new Label("Registered User Accounts");
    title.setFont(Font.font("Arial", 24));
    title.setLayoutX(20);
    title.setLayoutY(20);

    userTable.setLayoutX(20);
    userTable.setLayoutY(60);
    userTable.setPrefSize(610, 350);

    Button backButton = new Button("Back to Admin Home");
    backButton.setFont(Font.font("Dialog", 16));
    backButton.setLayoutX(20);
    backButton.setLayoutY(430);
    backButton.setOnAction(e -> ViewAdminHome.displayAdminHome(theStage, currentUser));

    rootPane.getChildren().addAll(title, userTable, backButton);
}

    /**
     * Public method to allow the Controller to set the table's data.
     * @param userList The list of users from the Model.
     */
    public void setUserData(java.util.List<User> userList) {
        userTable.setItems(FXCollections.observableArrayList(userList));
    }
}
