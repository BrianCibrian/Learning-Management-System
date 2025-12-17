package guiTicketSystem;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Ticket;
import entityClasses.TicketComment;
import entityClasses.User;
import java.util.List;
import javafx.stage.Stage;
import guiAdminHome.ViewAdminHome;
import guiRole1.ViewRole1Home;
import guiRole2.ViewRole2Home;
import guiUserLogin.ViewUserLogin;

/*******
 * <p> Title: ControllerTicketSystem Class </p>
 *
 * <p> Description: Controls the Ticket System views, including the
 * main list, detail view, create/edit, and discussion comments. </p>
 */
public class ControllerTicketSystem {

    private static Database theDatabase = FoundationsMain.database;
    protected static Stage theStage;
    protected static User theUser;
    protected static ViewTicketSystem theMainView;

    /**********
     * Show the Ticket System main view (staff/admin only).
     */
    public static void displayTicketSystem(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        // Only Admin or Staff (Role1) can see tickets
        if (!(theUser.getAdminRole() || theUser.getNewRole1())) {
            // silently ignore or optionally show an error dialog
            return;
        }

        if (theMainView == null) theMainView = new ViewTicketSystem();
        theStage.setTitle("Ticket System");
        theStage.setScene(theMainView.getScene());
        theStage.show();
        refreshMainView();
    }

    /**********
     * Load tickets with filters.
     *
     * @param keyword      search in title/body
     * @param statusFilter "OPEN", "CLOSED", or null for all
     * @param onlyMine     if true, only current user's tickets
     */
    protected static List<Ticket> loadTickets(String keyword, String statusFilter, boolean onlyMine) {
        if (theUser == null) return null;
        String creatorUserName = onlyMine ? theUser.getUserName() : null;
        return theDatabase.getTickets(keyword, statusFilter, creatorUserName);
    }

    /**********
     * Create a new ticket in the DB, then refresh.
     */
    protected static void createTicket(Ticket t) {
        if (t == null) return;
        theDatabase.createTicket(t);
        refreshMainView();
    }

    /**********
     * Attempt to close a ticket (status -> CLOSED).
     * Staff can close their own; Admin can close any.
     */
    protected static void closeTicket(Ticket t) {
        if (t == null || theUser == null) return;
        if (!canCloseOrReopenTicket(t)) return;
        if ("CLOSED".equalsIgnoreCase(t.getStatus())) return;

        theDatabase.updateTicketStatus(t.getId(), "CLOSED");
        refreshMainView();
    }

    /**********
     * Reopen a closed ticket: opens create dialog with link to original.
     */
    protected static void reopenTicket(Ticket t) {
        if (t == null || theUser == null) return;
        if (!"CLOSED".equalsIgnoreCase(t.getStatus())) return;
        if (!canCloseOrReopenTicket(t)) return;

        ViewCreateTicket.displayCreateTicket(theStage, theUser, t);
    }

    /**********
     * Edit a ticket (title/body).
     * Staff can edit their own; Admin can edit any.
     */
    protected static void editTicket(Ticket t) {
        if (t == null || theUser == null) return;
        if (!canEditOrDeleteTicket(t)) return;
        ViewEditTicket.displayEditTicket(theStage, theUser, t);
    }

    /**********
     * Called from ViewEditTicket to actually update title/body.
     */
    protected static void updateTicketDetails(Ticket t, String newTitle, String newBody) {
        if (t == null || theUser == null) return;
        if (!canEditOrDeleteTicket(t)) return;
        theDatabase.updateTicketDetails(t.getId(), newTitle, newBody);
        refreshMainView();
    }

    /**********
     * Delete (visual) a ticket: staff can delete their own, admin any.
     */
    protected static void deleteTicket(Ticket t) {
        if (t == null || theUser == null) return;
        if (!canEditOrDeleteTicket(t)) return;
        theDatabase.visuallyDeleteTicket(t.getId(), theUser.getUserName());
        refreshMainView();
    }

    /**********
     * Open the ticket detail (discussion) window.
     */
    protected static void openTicketDetail(Ticket t) {
        if (t == null || theUser == null) return;
        ViewTicketDetail.displayTicketDetail(theStage, theUser, t);
    }

    /**********
     * Load comments for a ticket.
     */
    protected static List<TicketComment> loadCommentsForTicket(int ticketId) {
        return theDatabase.getCommentsForTicket(ticketId);
    }

    /**********
     * Add a comment to a ticket.
     */
    protected static void addCommentToTicket(Ticket t, String content) {
        if (t == null || theUser == null) return;
        if (content == null || content.trim().isEmpty()) return;
        // Do not allow comments on CLOSED tickets
        if ("CLOSED".equalsIgnoreCase(t.getStatus())) return;
        theDatabase.createTicketComment(t.getId(), theUser.getUserName(), content.trim());
    }

    /**********
     * Permission: Admin can close/reopen any; Staff only own.
     */
    protected static boolean canCloseOrReopenTicket(Ticket t) {
        boolean isAdmin  = theUser.getAdminRole();
        boolean isStaff  = theUser.getNewRole1();   // Role1 = staff
        boolean isOwner  = t.getCreatorUserName() != null
                        && t.getCreatorUserName().equals(theUser.getUserName());

        if (isAdmin) return true;
        if (isStaff && isOwner) return true;
        return false;
    }

    /**********
     * Permission: Admin can edit/delete any; Staff only own.
     */
    protected static boolean canEditOrDeleteTicket(Ticket t) {
        boolean isAdmin  = theUser.getAdminRole();
        boolean isStaff  = theUser.getNewRole1();
        boolean isOwner  = t.getCreatorUserName() != null
                        && t.getCreatorUserName().equals(theUser.getUserName());

        if (isAdmin) return true;
        if (isStaff && isOwner) return true;
        return false;
    }

    /**********
     * Return the current user to their appropriate home page.
     */
    protected static void returnToHome() {                                
		int theRole = applicationMain.FoundationsMain.activeHomePage;
		switch (theRole) {
			case 1:
				guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
				break;
			case 2:
				guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
				break;
			case 3:
				guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
				break;
			default:
				System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + theRole);
				System.exit(0);
		}                                                              
    }                                                                    

    /**********
     * Refresh main ticket list view.
     */
    public static void refreshMainView() {
        if (theMainView != null) theMainView.reloadTable();
    }
}
