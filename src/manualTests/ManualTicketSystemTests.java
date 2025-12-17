package manualTests;

/**
 * <h1>ManualTicketSystemTests</h1>
 *
 * <p>
 * Console-based <b>manual</b> test script for the Ticket System features added in TP3.
 * These are not automated tests. Instead, each test method prints a clearly
 * structured description of a manual test case (ID: TS-01, TS-02, ...).
 * </p>
 *
 * <p>
 * The intent is:
 * </p>
 * <ul>
 *   <li> Document the manual tests in executable form.</li>
 * </ul>
 *
 * <p>
 * The content of these methods should match the "Manual Tests.pdf" document
 * that is part of the TP3 deliverables. Each test focuses on the staff/admin
 * ticket system behavior 
 * </p>
 *
 * @author Chuan Nguyen
 * @version 1.00 2025-11-23
 */
public class ManualTicketSystemTests {

    /**
     * Entry point for running all manual ticket system tests.
     *
     * <p>
     * When executed, this method prints all test case descriptions (TS-01 through
     * TS-14) to the console. Testers can then follow the printed steps in the GUI
     * and compare the observed behavior with the expected results.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        printHeader();

        test_TS01_StaffOpensTicketSystem();
        test_TS02_StaffCreatesTicket();
        test_TS03_FilterShowOnlyMyTickets();
        test_TS04_FilterStatus();
        test_TS05_StaffEditsOwnTicket();
        test_TS06_StaffCannotEditOthersTicket();
        test_TS07_StaffClosesOwnTicket();
        test_TS08_StaffReopensClosedTicket();
        test_TS09_TicketDiscussionBetweenStaffAndAdmin();
        test_TS10_CommentsDisabledOnClosedTicket();
        test_TS11_AdminViewsAllTickets();
        test_TS12_AdminCanCloseAnyTicket();
        test_TS13_StudentCannotAccessTicketSystem();
        test_TS14_BackButtonReturnsToCorrectHome();
    }

    /**
     * Prints a header for the manual ticket system test suite.
     *
     * <p>
     * This runs once at the beginning of {@link #main(String[])}.
     * </p>
     */
    private static void printHeader() {
        System.out.println("============================================================");
        System.out.println(" Manual Ticket System Tests (TP3) - MANUAL (NO JUnit)");
        System.out.println("============================================================");
        System.out.println("These are MANUAL tests.");
        System.out.println("Follow the printed steps in the running GUI.");
        System.out.println();
    }

    /**
     * TS-01 – Staff opens the Ticket System.
     *
     * <p>
     * Verifies that a staff user (Role1) can navigate from the Staff Home Page
     * to the Ticket System main screen using the provided navigation control.
     * </p>
     */
    public static void test_TS01_StaffOpensTicketSystem() {
        System.out.println("============================================================");
        System.out.println("TS-01 – Staff Opens Ticket System");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify that a staff member can open the Ticket System GUI");
        System.out.println("    from the Staff Home page.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Application is running.");
        System.out.println("  - A valid staff account exists (Role1 = true).");
        System.out.println("  - Staff user is logged in and on the Staff Home Page.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. On the Staff Home Page, locate the 'Ticket System' button.");
        System.out.println("  2. Click the 'Ticket System' button.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - A window/scene titled 'Ticket System' is displayed.");
        System.out.println("  - The screen shows filters, table, and action buttons.");
        System.out.println();
    }

    /**
     * TS-02 – Staff creates a ticket.
     *
     * <p>
     * Verifies that a staff user can create a new ticket and that it appears
     * in the Ticket System table with status OPEN and the correct creator.
     * </p>
     */
    public static void test_TS02_StaffCreatesTicket() {
        System.out.println("============================================================");
        System.out.println("TS-02 – Staff Creates a Ticket");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify creation of a new ticket via the GUI and its");
        System.out.println("    appearance in the table.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - TS-01 passed (Ticket System is open for a staff user).");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. In the Ticket System view, click 'Create Ticket'.");
        System.out.println("  2. Enter a Title (e.g., 'Cannot access homework thread').");
        System.out.println("  3. Enter a Body (e.g., 'Error appears when clicking...').");
        System.out.println("  4. Click 'Save' / 'Create'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Dialog closes without error.");
        System.out.println("  - A new row appears in the tickets table with:");
        System.out.println("      Title = the entered value,");
        System.out.println("      Status = OPEN,");
        System.out.println("      Creator = current staff.");
        System.out.println();
    }

    /**
     * TS-03 – Filter: Show only my tickets.
     *
     * <p>
     * Confirms that the "Show only my tickets" checkbox filters the table so
     * only the current staff member's tickets are displayed.
     * </p>
     */
    public static void test_TS03_FilterShowOnlyMyTickets() {
        System.out.println("============================================================");
        System.out.println("TS-03 – Filter: Show Only My Tickets");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify 'Show only my tickets' checkbox filters tickets");
        System.out.println("    by current staff user.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Ticket System is open.");
        System.out.println("  - At least one ticket by this staff user.");
        System.out.println("  - At least one ticket by a different user.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Ensure 'Show only my tickets' is UNCHECKED.");
        System.out.println("  2. Confirm the table shows tickets from multiple users.");
        System.out.println("  3. CHECK 'Show only my tickets'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Only tickets created by the current staff user remain.");
        System.out.println();
    }

    /**
     * TS-04 – Filter by status (All / Open / Closed).
     *
     * <p>
     * Ensures the Status combo box correctly filters the ticket list by
     * OPEN and CLOSED status.
     * </p>
     */
    public static void test_TS04_FilterStatus() {
        System.out.println("============================================================");
        System.out.println("TS-04 – Filter: Status (All / Open / Closed)");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Confirm that the Status combo box filters OPEN");
        System.out.println("    and CLOSED tickets correctly.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Ticket System is open.");
        System.out.println("  - At least one OPEN ticket exists.");
        System.out.println("  - At least one CLOSED ticket exists.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Set Status to 'All' and observe tickets (OPEN and CLOSED).");
        System.out.println("  2. Set Status to 'Open' and observe table contents.");
        System.out.println("  3. Set Status to 'Closed' and observe table contents.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - 'All': both OPEN and CLOSED visible.");
        System.out.println("  - 'Open': only OPEN visible.");
        System.out.println("  - 'Closed': only CLOSED visible.");
        System.out.println();
    }

    /**
     * TS-05 – Staff edits their own ticket. 
     *
     * <p>
     * Validates that a staff user can edit the title/body of an OPEN ticket
     * they created and see the updated values in the table.
     * </p>
     */
    public static void test_TS05_StaffEditsOwnTicket() {
        System.out.println("============================================================");
        System.out.println("TS-05 – Staff Edits Their Own Ticket");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify staff can edit their own ticket and updates");
        System.out.println("    appear in the table.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Ticket System is open for staff.");
        System.out.println("  - At least one OPEN ticket created by this staff user.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Select an OPEN ticket created by this staff user.");
        System.out.println("  2. Click 'Edit'.");
        System.out.println("  3. Change the title (e.g., append '(URGENT)').");
        System.out.println("  4. Click 'Save'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Edited title appears in the table.");
        System.out.println("  - Status remains OPEN.");
        System.out.println();
    }

    /**
     * TS-06 – Staff cannot edit tickets created by other users.
     *
     * <p>
     * Confirms that staff members are blocked from editing tickets they did not
     * create (only admins may edit any ticket).
     * </p>
     */
    public static void test_TS06_StaffCannotEditOthersTicket() {
        System.out.println("============================================================");
        System.out.println("TS-06 – Staff Cannot Edit Others’ Tickets");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify that staff cannot edit tickets they did not create.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Staff user is logged in.");
        System.out.println("  - Ticket System open.");
        System.out.println("  - At least one ticket created by another user.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Select a ticket where Creator is NOT the current staff.");
        System.out.println("  2. Attempt to click 'Edit'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Staff cannot modify that ticket:");
        System.out.println("    (button disabled, no effect, or a permission error).");
        System.out.println();
    }

    /**
     * TS-07 – Staff closes their own ticket.
     *
     * <p>
     * Verifies that a staff user can close an OPEN ticket that they created,
     * changing its status to CLOSED.
     * </p>
     */
    public static void test_TS07_StaffClosesOwnTicket() {
        System.out.println("============================================================");
        System.out.println("TS-07 – Staff Closes Their Own Ticket");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Confirm a staff user can close their own OPEN ticket.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Staff user logged in.");
        System.out.println("  - Ticket System open.");
        System.out.println("  - At least one OPEN ticket created by this staff user.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Select an OPEN ticket created by this staff user.");
        System.out.println("  2. Click 'Close'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Ticket status changes to CLOSED in the table.");
        System.out.println();
    }

    /**
     * TS-08 – Staff reopens a closed ticket (linked ticket).
     *
     * <p>
     * Checks that a closed staff ticket can be reopened, creating a new OPEN
     * ticket linked to the original CLOSED ticket.
     * </p>
     */
    public static void test_TS08_StaffReopensClosedTicket() {
        System.out.println("============================================================");
        System.out.println("TS-08 – Staff Reopens a Closed Ticket (Linked Ticket)");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify a staff user can reopen their own CLOSED ticket,");
        System.out.println("    creating a new linked OPEN ticket.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Staff user logged in.");
        System.out.println("  - At least one CLOSED ticket created by this staff user.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Select a CLOSED ticket created by this staff user.");
        System.out.println("  2. Click 'Reopen'.");
        System.out.println("  3. Enter a new title/body in the reopen dialog.");
        System.out.println("  4. Save the new ticket.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - New OPEN ticket appears, linked to the original.");
        System.out.println("  - Original ticket remains CLOSED.");
        System.out.println();
    }

    /**
     * TS-09 – Ticket discussion between staff and admin.
     *
     * <p>
     * Verifies that a ticket's discussion thread can include comments from
     * both staff and admin, and that they are visible to both.
     * </p>
     */
    public static void test_TS09_TicketDiscussionBetweenStaffAndAdmin() {
        System.out.println("============================================================");
        System.out.println("TS-09 – Ticket Discussion Between Staff and Admin");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify comments from both staff and admin appear");
        System.out.println("    correctly in a ticket discussion.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Ticket System open for staff.");
        System.out.println("  - At least one OPEN ticket.");
        System.out.println("  - Admin account exists.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  Staff side:");
        System.out.println("    1. Open detail for an OPEN ticket.");
        System.out.println("    2. Add comment: 'I am unable to open this thread.'");
        System.out.println("    3. Log out.");
        System.out.println("  Admin side:");
        System.out.println("    4. Log in as admin.");
        System.out.println("    5. Open same ticket detail.");
        System.out.println("    6. Add comment: 'Please try clearing your cache.'");
        System.out.println("    7. Log out.");
        System.out.println("  Staff again:");
        System.out.println("    8. Log in as original staff user.");
        System.out.println("    9. Open ticket detail again.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Both comments visible with correct usernames.");
        System.out.println("  - Comments are ordered chronologically.");
        System.out.println();
    }

    /**
     * TS-10 – Comments disabled on closed tickets.
     *
     * <p>
     * Confirms that once a ticket is CLOSED, no new comments can be added by
     * staff or admin.
     * </p>
     */
    public static void test_TS10_CommentsDisabledOnClosedTicket() {
        System.out.println("============================================================");
        System.out.println("TS-10 – Comments Disabled on Closed Ticket");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Confirm no new comments can be added to CLOSED tickets.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - A CLOSED ticket exists.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Open detail view for a CLOSED ticket.");
        System.out.println("  2. Attempt to type and submit a new comment.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - User cannot successfully add a new comment:");
        System.out.println("    (input/submit disabled or operation rejected).");
        System.out.println();
    }

    /**
     * TS-11 – Admin views all tickets.
     *
     * <p>
     * Ensures that an admin user can see tickets created by any user (staff or
     * admin) in the Ticket System table.
     * </p>
     */
    public static void test_TS11_AdminViewsAllTickets() {
        System.out.println("============================================================");
        System.out.println("TS-11 – Admin Views All Tickets");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify admin can see tickets from all users.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Admin account exists.");
        System.out.println("  - Tickets exist for multiple creators.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Log in as admin.");
        System.out.println("  2. Open the Ticket System view.");
        System.out.println("  3. Observe creators listed in the table.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Tickets from multiple users (staff and admin) are visible.");
        System.out.println();
    }

    /**
     * TS-12 – Admin can close any ticket.
     *
     * <p>
     * Verifies that an admin user can close an OPEN ticket, even if it was
     * originally created by a staff member.
     * </p>
     */
    public static void test_TS12_AdminCanCloseAnyTicket() {
        System.out.println("============================================================");
        System.out.println("TS-12 – Admin Can Close Any Ticket");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify admin can close an OPEN ticket created by staff.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Admin is logged in.");
        System.out.println("  - At least one OPEN staff-created ticket exists.");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. In Ticket System, select an OPEN ticket created by staff.");
        System.out.println("  2. Click 'Close'.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - Ticket status changes to CLOSED.");
        System.out.println("  - Creator remains the original staff user.");
        System.out.println();
    }

    /**
     * TS-13 – Student cannot access the Ticket System.
     *
     * <p>
     * Validates that a student user (Role2 only) has no navigation or access
     * to the Ticket System features.
     * </p>
     */
    public static void test_TS13_StudentCannotAccessTicketSystem() {
        System.out.println("============================================================");
        System.out.println("TS-13 – Student Cannot Access Ticket System");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Confirm that students (Role2) have no access to");
        System.out.println("    the Ticket System.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - A student account exists (Role2 = true, no admin/Role1).");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Log in as the student user.");
        System.out.println("  2. Observe the Student Home page.");
        System.out.println("  3. Look for any Ticket System button/menu.");
        System.out.println();
        System.out.println("Expected Result:");
        System.out.println("  - No Ticket System navigation is visible.");
        System.out.println("  - Student cannot open Ticket System.");
        System.out.println();
    }

    /**
     * TS-14 – Back button returns to the correct home page.
     *
     * <p>
     * Checks that the "Back" button in the Ticket System routes users back
     * to the correct home page: Staff Home for staff and Admin Home for admins.
     * </p>
     */
    public static void test_TS14_BackButtonReturnsToCorrectHome() {
        System.out.println("============================================================");
        System.out.println("TS-14 – Back Button Returns to Correct Home Page");
        System.out.println("============================================================");
        System.out.println("Purpose:");
        System.out.println("  - Verify that the Back button in Ticket System returns");
        System.out.println("    staff to the Staff Home Page and admin to Admin Home.");
        System.out.println();
        System.out.println("Preconditions:");
        System.out.println("  - Back button implemented in Ticket System.");
        System.out.println("  - Staff and admin accounts exist.");
        System.out.println();
        System.out.println("Steps (Staff):");
        System.out.println("  1. Log in as staff.");
        System.out.println("  2. Open Ticket System from Staff Home.");
        System.out.println("  3. Click 'Back'.");
        System.out.println("Expected (Staff):");
        System.out.println("  - Staff Home Page is shown again.");
        System.out.println();
        System.out.println("Steps (Admin):");
        System.out.println("  4. Log in as admin.");
        System.out.println("  5. Open Ticket System from Admin Home.");
        System.out.println("  6. Click 'Back'.");
        System.out.println("Expected (Admin):");
        System.out.println("  - Admin Home Page is shown again.");
        System.out.println();
    }
}
