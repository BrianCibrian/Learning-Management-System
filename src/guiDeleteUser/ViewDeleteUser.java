package guiDeleteUser;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;

public class ViewDeleteUser {

    private static double width = FoundationsMain.WINDOW_WIDTH;
    private static double height = FoundationsMain.WINDOW_HEIGHT;

    protected static Stage myStage;
    protected static Pane myRootPane;
    protected static User myUser;

    private static Database localDatabase = FoundationsMain.database;

    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();
    protected static Button button_DeleteUser = new Button("Delete User");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    private static ViewDeleteUser myView;
    public static Scene deleteUserScene = null;

    // Display Delete User page
    public static void displayDeleteUser(Stage ps, User user) {
        myStage = ps;
        myUser = user;

        if (myView == null) myView = new ViewDeleteUser();
        
        // UPdate dropdown
        updateUserList();

        myStage.setTitle("Delete User Page");
        myStage.setScene(deleteUserScene);
        myStage.show();
    }

    public ViewDeleteUser() {
        myRootPane = new Pane();
        deleteUserScene = new Scene(myRootPane, width, height);

        Label label_PageTitle = new Label("Delete User Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 20);

        setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 200, 280, 100);
        updateUserList();
        
        // Delete User button calls controller method, sends username from combobox
        setupButtonUI(button_DeleteUser, "Dialog", 16, 200, Pos.CENTER, 280, 160);
        button_DeleteUser.setOnAction((event) -> 
            ControllerDeleteUser.performDeleteUser(combobox_SelectUser.getValue())
        );

        // Nav buttons (Return, Logout, Quit)
        setupButtonUI(button_Return, "Dialog", 16, 200, Pos.CENTER, 20, 250);
        button_Return.setOnAction((event) ->
        	ControllerDeleteUser.performReturn()
        );

        setupButtonUI(button_Logout, "Dialog", 16, 200, Pos.CENTER, 280, 250);
        button_Logout.setOnAction((event) ->
        	ControllerDeleteUser.performLogout()
        );

        setupButtonUI(button_Quit, "Dialog", 16, 200, Pos.CENTER, 540, 250);
        button_Quit.setOnAction((event) ->
        	ControllerDeleteUser.performQuit()
        );

        myRootPane.getChildren().addAll(
            label_PageTitle,
            combobox_SelectUser,
            button_DeleteUser,
            button_Return,
            button_Logout,
            button_Quit
        );
    }

    // Updates the ComboBox with all users in the database
    protected static void updateUserList() {
        List<String> userList = localDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
        combobox_SelectUser.getSelectionModel().select(0);
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    private static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}
