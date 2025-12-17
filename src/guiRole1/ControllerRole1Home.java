package guiRole1;

import guiTicketSystem.ControllerTicketSystem;   // Chuan NEW
import guiGradingSystem.ViewStudentList;        // Added: grading system view
import guiViewFeedback.ControllerViewFeedback;  // Brian NEW - added for Private Feedback

/*******
 * <p> Title: ControllerRole1Home Class </p>
 *
 * <p> Description: This controller defines the event-handling behavior for
 * the Staff (Role1) Home Page. This class is not instantiated; instead,
 * it provides a group of protected static methods that can be called by
 * ViewRole1Home to perform actions such as navigating to the Ticket System,
 * logging out, or quitting the program.
 * </p>
 *
 * <p> This follows the architectural pattern used in FoundationsF25 where
 * controllers for GUI pages are structured as collections of static UI
 * methods invoked from a singleton View. </p>
 *
 * <p> Copyright:
 * Lynn Robert Carter © 2025 </p>
 *
 * @author
 *  Chuan Nguyen (added Ticket System integration)
 *
 * @version
 *  1.01 — Added openTicketSystem() method and documentation.
 *  
 * @version 1.02 (Daniel / added Grading System integration)
 *
 * @since
 *  2025-11-23
 */
public class ControllerRole1Home {

    /*-*******************************************************************************************

    User Interface Actions for this page
    
    This controller is not a class that gets instantiated.  Rather, it is a collection of protected
    static methods that can be called by the View (which is a singleton instantiated object) and 
    the Model is often just a stub, or will be a singleton instantiated object.
    
     */

    /**
     * <p><b>Method:</b> openTicketSystem()</p>
     *
     * <p>
     * Opens the Ticket System GUI for Staff (Role1). This method forwards the
     * call to {@link guiTicketSystem.ControllerTicketSystem#displayTicketSystem}
     * and supplies the current Role1 stage and user as parameters.
     * </p>
     *
     * <p>
     * This is the entry point for Staff users to access ticket functionality,
     * including creating, viewing, editing, and discussing tickets.
     * </p>
     *
     * @see guiTicketSystem.ControllerTicketSystem#displayTicketSystem
     * @since 1.01 (Chuan NEW)
     */
    protected static void openTicketSystem() {     // Chuan NEW
        ControllerTicketSystem.displayTicketSystem(
            ViewRole1Home.theStage,
            ViewRole1Home.theUser
        );
    }
    
    /**
     * <p><b>Method:</b> openGradingSystem()</p>
     *
     * <p>
     * Opens the Grading System GUI for Staff (Role1). This method forwards the
     * call to {@link guiGradingSystem.ViewStudentList#display} and supplies the
     * current Role1 stage and user as parameters.
     * </p>
     *
     * <p>
     * This provides staff users access to the grading/score management UI.
     * </p>
     *
     * @see guiGradingSystem.ViewStudentList#display
     * @since 1.02
     */
    protected static void openGradingSystem() {
        ViewStudentList.display(ViewRole1Home.theStage, ViewRole1Home.theUser);
    }

    /**
     * <p><b>Method:</b> openFeedbackSystem()</p>
     *
     * <p>
     * Opens the Private Feedback Messaging GUI for Staff (Role1). This method forwards the
     * call to {@link guiViewFeedback.ControllerViewFeedback#displayViewFeedback}
     * and supplies the current Role1 stage and user as parameters.
     * </p>
     *
     * @see guiViewFeedback.ControllerViewFeedback#displayViewFeedback
     * @since 1.02 (Brian NEW)
     */
    protected static void openFeedbackSystem() {
        ControllerViewFeedback.displayViewFeedback(
            ViewRole1Home.theStage,
            ViewRole1Home.theUser
        );
    }
    
    /**
     * <p><b>Method:</b> performViewPosts()</p>
     *
     * <p>
     * Opens the View Posts GUI for Staff (Role1). This method forwards the
     * call to {@link guiViewPosts.ControllerViewPosts#displayViewPosts}
     * and supplies the current Role1 stage and user as parameters.
     * </p>
     *
     * @see guiViewPosts.ControllerViewPosts#displayViewPosts
     * @since 1.01 (Omar)
     */
    protected static void performViewPosts() {
    	guiViewPosts.ControllerViewPosts.displayViewPosts(ViewRole1Home.theStage, ViewRole1Home.theUser);
    }

    /**********
     * <p> Method: performLogout() </p>
     * 
     * <p> Description: This method logs out the current user and proceeds to the normal login
     * page where existing users can log in or potential new users with a invitation code can
     * start the process of setting up an account. </p>
     * 
     * @since 1.00
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
    }
    
    
    /**********
     * <p> Method: performQuit() </p>
     * 
     * <p> Description: This method terminates the execution of the program.  It leaves the
     * database in a state where the normal login page will be displayed when the application is
     * restarted.</p>
     *
     * @since 1.00
     */ 
    protected static void performQuit() {
        System.exit(0);
    }
}
