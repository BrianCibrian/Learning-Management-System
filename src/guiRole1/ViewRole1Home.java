package guiRole1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;

// Chuan NEW - import ticket system controller
import guiTicketSystem.ControllerTicketSystem;   // Chuan NEW


/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 *
 * <p> Description: The JavaFX-based Role1 (Staff) Home Page. This page
 * provides a simplified interface for staff-level users to access account
 * settings and system features. Most of the center area of the screen is a
 * placeholder for future expansion of Staff-specific tools. </p>
 *
 * <p> This class follows the FoundationsF25 architecture: a singleton View
 * initializes and displays GUI widgets, while the Controller performs the
 * operational logic. </p>
 *
 * <p> Copyright:
 * Lynn Robert Carter © 2025 </p>
 *
 * @author
 *  Lynn Robert Carter / Updated by Chuan Nguyen (added Ticket System integration)
 *
 * @version
 *  1.01 — Added Ticket System button and documentation.
 *
 * @since
 *  2025-08-20
 */
public class ViewRole1Home {
    
    /*-*******************************************************************************************

    Attributes
    
     */
    
    // These are the application values required by the user interface
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


    // These are the widget attributes for the GUI. There are 3 areas for this GUI.
    
    // GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
    // and a button to allow this user to update the account settings
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");
    
    // This is a separator and it is used to partition the GUI for various tasks
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

    // GUI Area 2: This is a stub for future role-specific widgets
	// Omar TP3: added button for view post
	protected static Button button_ViewPosts = new Button("View Posts");
    
    // Chuan NEW — Ticket System button
    protected static Button button_TicketSystem = new Button("Ticket System");   // Chuan NEW
    
    // Brian NEW: Feedback System Button 
    protected static Button button_FeedbackSystem = new Button("Private Feedback"); // Brian NEW
    
    // Grading System button 
    protected static Button button_GradingSystem = new Button("Grading System");
    
    protected static Line line_Separator4 = new Line(20, 525, width-20,525);
    
    // GUI Area 3: This is last of the GUI areas. It is used for quitting the application and for
    // logging out.
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // End of GUI widget objects.
    
    // These attributes configure the page and populate it with this user's information
    private static ViewRole1Home theView;        // Used to determine if instantiation is needed

    // Reference for the in-memory database so this package has access
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;             // JavaFX Stage for this GUI
    
    protected static Pane theRootPane;           // Root Pane holding all GUI widgets
    protected static User theUser;               // The currently logged-in User
    

    private static Scene theViewRole1HomeScene;  // Shared Scene reused each display
    protected static final int theRole = 2;      // Admin: 1; Role1: 2; Role2: 3

    /*-*******************************************************************************************

    Constructors
    
     */


    /**
     * <p><b>Method:</b> displayRole1Home(Stage ps, User user)</p>
     *
     * <p>
     * This is the public entry point for displaying the Staff (Role1) Home Page.
     * It establishes references to the stage and the logged-in user, initializes
     * the singleton view if necessary, and then populates dynamic fields such as
     * the username. Finally, it sets the scene and displays the window.
     * </p>
     *
     * @param ps   the Stage used to display this GUI
     * @param user the User whose session is being displayed
     *
     * @since 1.00
     */
    public static void displayRole1Home(Stage ps, User user) {
        
        // Establish the references to the GUI and the current user
        theStage = ps;
        theUser = user;
        
        // If not yet established, populate the static aspects of the GUI
        if (theView == null) theView = new ViewRole1Home();        // Instantiate singleton if needed
        
        // Populate the dynamic aspects
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        
        label_UserDetails.setText("User: " + theUser.getUserName());
                
        // Set the title for the window, display the page
        theStage.setTitle("CSE 360 Foundations: Staff Home Page");
        theStage.setScene(theViewRole1HomeScene);
        theStage.show();
    }
    
    /**
     * <p><b>Constructor: ViewRole1Home()</b></p>
     *
     * <p>
     * Initializes all GUI widgets, sets layout, fonts, spacing, and attaches
     * event handlers. This method is only executed once because this View is
     * implemented as a singleton.
     * </p>
     *
     * @since 1.00
     */
    private ViewRole1Home() {

        // Create the Pane and Scene
        theRootPane = new Pane();
        theViewRole1HomeScene = new Scene(theRootPane, width, height);
        
        // GUI Area 1
        label_PageTitle.setText("Staff Home Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((event) ->
            { ViewUserUpdate.displayUserUpdate(theStage, theUser); });
        
        
        // GUI Area 2 – currently a stub


        // Chuan NEW — Ticket System button setup
        setupButtonUI(button_TicketSystem, "Dialog", 16, 250, Pos.CENTER, 300, 270);   // Chuan NEW
        button_TicketSystem.setOnAction((event) -> {                                   // Chuan NEW
            ControllerRole1Home.openTicketSystem();                                    // Chuan NEW
        });                                                                            // Chuan NEW
        
		// TP3 Omar: added for view post
		setupButtonUI(button_ViewPosts, "Dialog", 16, 250, Pos.CENTER, 20, 270);
		button_ViewPosts.setOnAction((event) -> {
			guiViewPosts.ControllerViewPosts.displayViewPosts(theStage, theUser);
		});
		
		// Brian NEW: Feedback System Button Setup
        setupButtonUI(button_FeedbackSystem, "Dialog", 16, 250, Pos.CENTER, 20, 330); // Brian NEW
        button_FeedbackSystem.setOnAction((event) -> {
            ControllerRole1Home.openFeedbackSystem(); 
        });
        
        // Grading System button 
        setupButtonUI(button_GradingSystem, "Dialog", 16, 250, Pos.CENTER, 300, 330);
        button_GradingSystem.setOnAction((event) -> {
            ControllerRole1Home.openGradingSystem();
        });
        
        // GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> { ControllerRole1Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> { ControllerRole1Home.performQuit(); });

        
        // Add all widgets to root pane
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
            button_TicketSystem,     // Chuan NEW
            button_FeedbackSystem,   // Brian NEW 
            line_Separator4, button_Logout, button_Quit, button_ViewPosts, button_GradingSystem
        );
    }
    
    
    /*-********************************************************************************************
    Helper methods to reduce code length
     */

    /**
     * Sets common layout, font, alignment, and position parameters for Labels.
     *
     * @param l   the Label to configure
     * @param ff  font family
     * @param f   font size
     * @param w   minimum width
     * @param p   alignment position
     * @param x   x-coordinate
     * @param y   y-coordinate
     *
     * @since 1.00
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
            double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }
    
    
    /**
     * Sets common layout, font, alignment, and position parameters for Buttons.
     *
     * @param b   the Button to configure
     * @param ff  font family
     * @param f   font size
     * @param w   minimum width
     * @param p   alignment position
     * @param x   x-coordinate
     * @param y   y-coordinate
     *
     * @since 1.00
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
            double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }
}
