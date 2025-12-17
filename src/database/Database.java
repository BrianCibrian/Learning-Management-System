package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
import entityClasses.Post;	//OMAR HW2 NEW
import entityClasses.Reply; //OMAR HW2 NEW
import entityClasses.Ticket; //Chuan NEW
import entityClasses.TicketComment; //Chuan NEW

import entityClasses.GradingParameter; // DANIEL TP3 NEW (from Version B)
import entityClasses.StudentScore;    // DANIEL TP3 NEW (from Version B)

import entityClasses.Feedback; //Brian NEW

import java.time.Instant; //Omar New

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {

	    /*******
	     * @author Daniel Ortiz Figueroa
	    */
	    // Create Grading Parameters table (from Version B)
	    String gradingParamsTable = "CREATE TABLE IF NOT EXISTS GradingParameters ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "name VARCHAR(255), "
	            + "maxScore DOUBLE)";
	    statement.execute(gradingParamsTable);

	    // Create Student Scores table (The actual grades) (from Version B)
	    // Using composite primary key to ensure one score per parameter per student
	    String studentScoresTable = "CREATE TABLE IF NOT EXISTS StudentScores ("
	            + "studentUserName VARCHAR(255), "
	            + "paramId INT, "
	            + "score DOUBLE, "
	            + "PRIMARY KEY (studentUserName, paramId), "
	            + "FOREIGN KEY (paramId) REFERENCES GradingParameters(id) ON DELETE CASCADE)";
	    statement.execute(studentScoresTable);

	    // Insert default parameters if empty (from Version B)
	    try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS cnt FROM GradingParameters")) {
	        if (rs.next() && rs.getInt("cnt") == 0) {
	            statement.execute("INSERT INTO GradingParameters (name, maxScore) VALUES ('Participation', 20.0)");
	            statement.execute("INSERT INTO GradingParameters (name, maxScore) VALUES ('Behavior', 10.0)");
	            statement.execute("INSERT INTO GradingParameters (name, maxScore) VALUES ('Performance', 70.0)");
	        }
	    }

		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
		// Omar note - added roles and a created at for expirations
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "emailAddress VARCHAR(255), "
				+ "roles VARCHAR(255), "
				+ "created_at TIMESTAMP)";
		statement.execute(invitationCodesTable);

		// OMAR HW2 NEW: Create Posts table
		String postsTable = "CREATE TABLE IF NOT EXISTS Posts ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(1024), "
				+ "body CLOB, "
				+ "author VARCHAR(255), "
				+ "thread VARCHAR(255), "
				+ "created_at TIMESTAMP, "
				+ "deleted BOOL DEFAULT FALSE)";
		statement.execute(postsTable);

		// OMAR HW2 NEW: Create Replies table
		String repliesTable = "CREATE TABLE IF NOT EXISTS Replies ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "postId INT, "
				+ "content CLOB, "
				+ "author VARCHAR(255), "
				+ "created_at TIMESTAMP, "
				+ "FOREIGN KEY (postId) REFERENCES Posts(id) ON DELETE CASCADE)";
		statement.execute(repliesTable);

		// OMAR HW2 NEW: Create ReplyReadStatus table, tracks read/unread for each user
		String replyReadStatusTable = "CREATE TABLE IF NOT EXISTS ReplyReadStatus ("
				+ "replyId INT, "
				+ "userName VARCHAR(255), "
				+ "isRead BOOL DEFAULT FALSE, "
				+ "PRIMARY KEY (replyId, userName), "
				+ "FOREIGN KEY (replyId) REFERENCES Replies(id) ON DELETE CASCADE)";
		statement.execute(replyReadStatusTable);

		// OMAR HW2 NEW: Create PostReadStatus table, tracks whether a user has read a Post
		String postReadStatusTable = "CREATE TABLE IF NOT EXISTS PostReadStatus ("
				+ "postId INT, "
				+ "userName VARCHAR(255), "
				+ "isRead BOOL DEFAULT FALSE, "
				+ "PRIMARY KEY (postId, userName), "
				+ "FOREIGN KEY (postId) REFERENCES Posts(id) ON DELETE CASCADE)";
		statement.execute(postReadStatusTable);
		
		
		// OMAR HW3 NEW
		// Create Threads table
		String threadsTable = "CREATE TABLE IF NOT EXISTS Threads ("
				+ "name VARCHAR(255) PRIMARY KEY)";
		statement.execute(threadsTable);

		// Chuan New
		/** 
		 * Creates the Tickets table if it does not already exist.
		 * Stores ticket metadata including creator, status, and timestamps.
		 */
		String ticketsTable = "CREATE TABLE IF NOT EXISTS Tickets ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(255), "
				+ "body CLOB, "
				+ "creatorUsername VARCHAR(255), "
				+ "status VARCHAR(20), "			// 'OPEN' or 'CLOSED'
				+ "reopenedFromId INT, "
				+ "created_at TIMESTAMP, "
				+ "deleted BOOL DEFAULT FALSE)";
		statement.execute(ticketsTable);
		// Chuan New
		/**
		 * Creates the TicketComments table if it does not already exist.
		 * Stores comments associated with a ticket.
		 */
		String ticketCommentsTable = "CREATE TABLE IF NOT EXISTS TicketComments ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "ticketId INT, "
				+ "authorUsername VARCHAR(255), "
				+ "content CLOB, "
				+ "created_at TIMESTAMP, "
				+ "FOREIGN KEY (ticketId) REFERENCES Tickets(id) ON DELETE CASCADE)";
		statement.execute(ticketCommentsTable);

		
		// OMAR HW3 NEW
		// Inserts default threads if threads are empty
		String countThreads = "SELECT COUNT(*) AS cnt FROM Threads";
		try (PreparedStatement pstmt = connection.prepareStatement(countThreads);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				int cnt = rs.getInt("cnt");
				if (cnt == 0) {
					// Insert default threads
					String insertThread = "INSERT INTO Threads (name) VALUES (?)";
					try (PreparedStatement ip = connection.prepareStatement(insertThread)) {
						ip.setString(1, "General");
						ip.executeUpdate();
						ip.setString(1, "Announcements");
						ip.executeUpdate();
						ip.setString(1, "Help");
						ip.executeUpdate();
						ip.setString(1, "Off-topic");
						ip.executeUpdate();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

        // BRIAN NEW: Create FeedbackMessages table if doesn't exist (from Version B)
		String feedbackTable = "CREATE TABLE IF NOT EXISTS FeedbackMessages ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "senderUsername VARCHAR(255), "
                + "receiverUsername VARCHAR(255), "
                + "subject VARCHAR(1024), "
                + "content CLOB, "
                + "created_at TIMESTAMP, "
                + "isRead BOOL DEFAULT FALSE, " // Status tracked for the receiver
                + "FOREIGN KEY (senderUsername) REFERENCES userDB(userName), "
                + "FOREIGN KEY (receiverUsername) REFERENCES userDB(userName)"
                + ")";
        statement.execute(feedbackTable);
	}



/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */ 
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
			return null;
		}
//		System.out.println(userList);
		return userList;
	}

	/*****
	 * <p> Method: getAllUsers()
	 * </p>
	 * 
	 * <p> Description: This method retrieves a complete list of all users from the
	 * userDB table. It creates a User object for each record and returns them
	 * in a list, sorted alphabetically by the user's last name.
	 * </p>
	 * 
	 * 
	 * @return A List<User> containing all user objects from the database.
	 * 
	 * @author Daniel Ortiz Figueroa
	 */
	public List<User> getAllUsers(){
		List<User> userList = new ArrayList<>();
		String query = "SELECT * FROM userDB ORDER BY lastName ASC";
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				
				User user = new User(
					rs.getString("userName"),
					rs.getString("password"),
					rs.getString("firstName"),
					rs.getString("middleName"),
					rs.getString("lastName"),
					rs.getString("preferredFirstName"),
					rs.getString("emailAddress"),
					rs.getBoolean("adminRole"),
					rs.getBoolean("newRole1"),
					rs.getBoolean("newRole2")
				);
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
		
	}
	
/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
			   e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
			   e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	
	
	
	// Omar Note - invitation duration in seconds
	private static final long INVITE_EXPIRATION_SECONDS = 300; // 5 minutes
	
	// Omar Note - method for invitation is expired
	private boolean isInvitationExpired(Timestamp createdAt) {
		if (createdAt == null) return true;
		Instant created = createdAt.toInstant();
		Instant expiry = created.plusSeconds(INVITE_EXPIRATION_SECONDS);
		return Instant.now().isAfter(expiry);
	}
	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	// Omar Note - changed to accept cvs of roles and stores timestamp
	public String generateInvitationCode(String emailAddress, String rolesCSV) {
		String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
		String query = "INSERT INTO InvitationCodes (code, emailaddress, roles, created_at) VALUES (?, ?, ?, ?)";
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, rolesCSV);
			pstmt.setTimestamp(4, now);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	// Omar Note - changed to only count non expired invitations
	public int getNumberOfInvitations() {
		String query = "SELECT code, created_at FROM InvitationCodes";
		int count = 0;
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Timestamp created = rs.getTimestamp("created_at");
				String code = rs.getString("code");
				if (isInvitationExpired(created)) {
					// Clean expired invites
					removeInvitationAfterUse(code); 
				} else {
					count++;
				}
			}
		} catch  (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	// Omar Note - made to only look at non expired invites
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		String query = "SELECT code, created_at FROM InvitationCodes WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Timestamp created = rs.getTimestamp("created_at");
				String code = rs.getString("code");
				if (isInvitationExpired(created)) {
					removeInvitationAfterUse(code);
					continue;
				}
				return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	// Omar Note - changed to removes invites if expired
	public String getRoleGivenAnInvitationCode(String code) {
		String query = "SELECT roles, created_at FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Timestamp created = rs.getTimestamp("created_at");
				if (isInvitationExpired(created)) {
					removeInvitationAfterUse(code);
					return "";
				}
				return rs.getString("roles");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
		}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	// Omar Note - deletes expired invites 
	public String getEmailAddressUsingCode (String code ) {
		String query = "SELECT emailAddress, created_at FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Timestamp created = rs.getTimestamp("created_at");
				if (isInvitationExpired(created)) {
					removeInvitationAfterUse(code);
					return "";
				}
				return rs.getString("emailAddress");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	//Omar changed method
	public void removeInvitationAfterUse(String code) {
		consumeInvitation(code);
		return;
	}
	
	//Omar added method for invites
	public boolean consumeInvitation(String code) {
		String query = "DELETE FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			int affected = pstmt.executeUpdate();
			return affected > 0; 
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("firstName"); // Return the first name if user exists
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
		String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentFirstName = firstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("middleName"); // Return the middle name if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
		String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, middleName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentMiddleName = middleName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("lastName"); // Return last name role if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
		String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, lastName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentLastName = lastName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("firstName"); // Return the preferred first name if user exists
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
		String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, preferredFirstName);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPreferredFirstName = preferredFirstName;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("emailAddress"); // Return the email address if user exists
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
		String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentEmailAddress = emailAddress;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//Chuan Nguyen Added this
	public void updateUserPassword(String username, String newPassword) {
		String query = "UPDATE userDB SET password = ? WHERE username = ?";  
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			currentPassword = newPassword;  
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();			
			rs.next();
			currentUsername = rs.getString(2);
			currentPassword = rs.getString(3);
			currentFirstName = rs.getString(4);
			currentMiddleName = rs.getString(5);
			currentLastName = rs.getString(6);
			currentPreferredFirstName = rs.getString(7);
			currentEmailAddress = rs.getString(8);
			currentAdminRole = rs.getBoolean(9);
			currentNewRole1 = rs.getBoolean(10);
			currentNewRole2 = rs.getBoolean(11);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	/* ---------------------------------------------------------------------
	 *  OMAR HW2 NEW: Post & Reply related methods
	 * ------------------------------------------------------------------ */
	
	/*******
	 * <p> Method: int createPost(Post p) </p>
	 * 
	 * <p> Description: Create a new Post row in the Posts table and mark the
	 * post as read for its author. Returns the generated post id on success,
	 * or -1 on failure, also inserts PostReadStatus for the author.</p>
	 * 
	 * @param p the Post object containing title, body, author, thread and flags
	 * @return the generated post id, or -1 if creation failed
	 *  
	 */
	public int createPost(Post p) {
		String insert = "INSERT INTO Posts (title, body, author, thread, created_at, deleted) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, p.getTitle());
			pstmt.setString(2, p.getBody());
			pstmt.setString(3, p.getAuthorUsername());
			pstmt.setString(4, p.getThread());
			pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pstmt.setBoolean(6, p.getDeleted());
			pstmt.executeUpdate();
			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				if (keys.next()) {
					int newId = keys.getInt(1);
					// mark post as read for the author automatically
					try {
						String insertStatus = "INSERT INTO PostReadStatus (postId, userName, isRead) VALUES (?, ?, ?)";
						try (PreparedStatement pis = connection.prepareStatement(insertStatus)) {
							pis.setInt(1, newId);
							pis.setString(2, p.getAuthorUsername());
							pis.setBoolean(3, true);
							pis.executeUpdate();
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					return newId;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/*******
	 * <p> Method: List&lt;Post&gt; getPosts() </p>
	 * 
	 * <p> Description: Retrieve all but deleted posts from the Posts table,
	 * ordered by newest. Each database row is converted into a Post object.</p>
	 * 
	 * @return list of Post objects (empty if no posts)
	 *  
	 */
	public List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT id, title, body, author, thread, created_at, deleted FROM Posts WHERE deleted = FALSE ORDER BY created_at DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Post p = new Post();
				p.setId(rs.getInt("id"));
				p.setTitle(rs.getString("title"));
				p.setBody(rs.getString("body"));
				p.setAuthorUsername(rs.getString("author"));
				p.setThread(rs.getString("thread"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) p.setCreatedAt(ts.toInstant());
				p.setDeleted(rs.getBoolean("deleted"));
				posts.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts; 
	}

	/*******
	 * <p> Method: List&lt;Post&gt; searchPosts(String keyword, String thread) </p>
	 * 
	 * <p> Description: Search posts by keyword (title or body) and optional thread.
	 * Returns posts (not deleted posts) matching the search, ordered by newest.
	 * If keyword is empty it is ignored; if thread is empty it is ignored.</p>
	 * 
	 * @param keyword the search keyword to match in title or body (not case sensitive)
	 * @param thread the thread name to filter by, or null to ignore
	 * @return list of Post objects matching the search (may be empty)
	 *  
	 */
	public List<Post> searchPosts(String keyword, String thread) {
		List<Post> posts = new ArrayList<>();
		String base = "SELECT id, title, body, author, thread, created_at, deleted FROM Posts WHERE deleted = FALSE";
		StringBuilder sb = new StringBuilder(base);
		if (keyword != null && keyword.trim().length() > 0) {
			sb.append(" AND (LOWER(title) LIKE ? OR LOWER(body) LIKE ?)");
		}
		if (thread != null && thread.trim().length() > 0) {
			sb.append(" AND thread = ?");
		}
		sb.append(" ORDER BY created_at DESC");
		try (PreparedStatement pstmt = connection.prepareStatement(sb.toString())) {
			int idx = 1;
			if (keyword != null && keyword.trim().length() > 0) {
				String kw = "%" + keyword.toLowerCase() + "%";
				pstmt.setString(idx++, kw);
				pstmt.setString(idx++, kw);
			}
			if (thread != null && thread.trim().length() > 0) {
				pstmt.setString(idx++, thread);
			}
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Post p = new Post();
				p.setId(rs.getInt("id"));
				p.setTitle(rs.getString("title"));
				p.setBody(rs.getString("body"));
				p.setAuthorUsername(rs.getString("author"));
				p.setThread(rs.getString("thread"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) p.setCreatedAt(ts.toInstant());
				p.setDeleted(rs.getBoolean("deleted"));
				posts.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;
	}

	/*******
	 * <p> Method: Post getPostById(int id) </p>
	 * 
	 * <p> Description: Retrieve a single post by its id. Returns null if the post
	 * is not found or if it has been marked deleted.</p>
	 * 
	 * @param id the id of the post to retrieve
	 * @return the Post object, or null if not found or deleted
	 *  
	 */
	public Post getPostById(int id) {
		String query = "SELECT id, title, body, author, thread, created_at, deleted FROM Posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				boolean deleted = rs.getBoolean("deleted");
				if (deleted) return null;
				Post p = new Post();
				p.setId(rs.getInt("id"));
				p.setTitle(rs.getString("title"));
				p.setBody(rs.getString("body"));
				p.setAuthorUsername(rs.getString("author"));
				p.setThread(rs.getString("thread"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) p.setCreatedAt(ts.toInstant());
				p.setDeleted(deleted);
				return p;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*******
	 * <p> Method: boolean deletePost(int postId, String requesterUserName) </p>
	 * 
	 * <p> Description: Mark a post as deleted. Only the author of the post
	 * is allowed to perform this operation. Returns true if the post was marked
	 * deleted successfully, false if not.</p>
	 * 
	 * @param postId the id of the post to mark deleted
	 * @param requesterUserName the username of the user requesting deletion (must match author)
	 * @return true if the post was successfully marked deleted, false if not
	 *  
	 */
	public boolean deletePost(int postId, String requesterUserName) {
		// Check ownership
		String check = "SELECT author FROM Posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(check)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String author = rs.getString("author");
				if (!author.equals(requesterUserName)) {
					return false; // not owner
				}
			} else {
				return false; // no post found
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		// Mark deleted
		String update = "UPDATE Posts SET deleted = TRUE WHERE id = ?";
		try (PreparedStatement pstmt2 = connection.prepareStatement(update)) {
			pstmt2.setInt(1, postId);
			int affected = pstmt2.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: int createReply(Reply r) </p>
	 * 
	 * <p> Description: Create a new reply for a post. Inserts an entry
	 * in the Replies table and sets up ReplyReadStatus entries for all users,
	 * author is marked read, others are marked unread. Returns the generated
	 * reply id or -1 on failure.</p>
	 * 
	 * @param r the Reply object containing postId, content, and author
	 * @return generated reply id, or -1 on failure
	 *  
	 */
	public int createReply(Reply r) {
		String insert = "INSERT INTO Replies (postId, content, author, created_at) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, r.getPostId());
			pstmt.setString(2, r.getContent());
			pstmt.setString(3, r.getAuthorUsername());
			pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();
			int replyId = -1;
			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				if (keys.next()) {
					replyId = keys.getInt(1);
				}
			}
			if (replyId != -1) {
				// read status for all users, mark as read for the reply author, unread for others
				String usersQuery = "SELECT userName FROM userDB";
				try (PreparedStatement pu = connection.prepareStatement(usersQuery)) {
					ResultSet rs = pu.executeQuery();
					String insertStatus = "INSERT INTO ReplyReadStatus (replyId, userName, isRead) VALUES (?, ?, ?)";
					while (rs.next()) {
						String uname = rs.getString("userName");
						boolean isRead = uname.equals(r.getAuthorUsername());
						try (PreparedStatement pis = connection.prepareStatement(insertStatus)) {
							pis.setInt(1, replyId);
							pis.setString(2, uname);
							pis.setBoolean(3, isRead);
							pis.executeUpdate();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return replyId;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/*******
	 * <p> Method: List&lt;Reply&gt; getRepliesForPost(int postId) </p>
	 * 
	 * <p> Description: Retrieve all replies for a given post id, ordered
	 * oldest first. Returns an empty list if no replies are found.</p>
	 * 
	 * @param postId the id of the post whose replies are requested
	 * @return list of Reply objects (may be empty)
	 *  
	 */
	public List<Reply> getRepliesForPost(int postId) {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT id, postId, content, author, created_at FROM Replies WHERE postId = ? ORDER BY created_at ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Reply r = new Reply();
				r.setId(rs.getInt("id"));
				r.setPostId(rs.getInt("postId"));
				r.setContent(rs.getString("content"));
				r.setAuthorUsername(rs.getString("author"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) r.setCreatedAt(ts.toInstant());
				replies.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return replies;
	}

	/*******
	 * <p> Method: boolean markReplyAsRead(int replyId, String userName) </p>
	 * 
	 * <p> Description: Mark a specific reply as read for the given user.
	 * Returns true if a row was updated.</p>
	 * 
	 * @param replyId the id of the reply to mark read
	 * @param userName the username for whom the reply should be marked read
	 * @return true if update succeeded (affected rows > 0), false otherwise
	 *  
	 */
	public boolean markReplyAsRead(int replyId, String userName) {
		String query = "UPDATE ReplyReadStatus SET isRead = TRUE WHERE replyId = ? AND userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyId);
			pstmt.setString(2, userName);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: int getUnreadReplyCountForUser(String userName) </p>
	 * 
	 * <p> Description: Count the total number of unread replies for the given user
	 * in all posts.</p>
	 * 
	 * @param userName the username to check unread reply count for
	 * @return the number of unread replies, 0 if none or on error
	 *  
	 */
	public int getUnreadReplyCountForUser(String userName) {
		String query = "SELECT COUNT(*) AS cnt FROM ReplyReadStatus WHERE userName = ? AND isRead = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt("cnt");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*******
	 * <p> Method: int getUnreadReplyCountForPostForUser(int postId, String userName) </p>
	 * 
	 * <p> Description: Returns the number of unread replies for a post
	 * and user by joining ReplyReadStatus with Replies.</p>
	 * 
	 * @param postId the id of the post to check
	 * @param userName the username to check unread replies for
	 * @return the count of unread replies for the post and user
	 *  
	 */
	public int getUnreadReplyCountForPostForUser(int postId, String userName) {
		String query = "SELECT COUNT(*) AS cnt FROM ReplyReadStatus r JOIN Replies rp ON r.replyId = rp.id "
					 + "WHERE rp.postId = ? AND r.userName = ? AND r.isRead = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt("cnt");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p> Method: boolean markPostAsRead(int postId, String userName) </p>
	 * 
	 * <p> Description: Mark a post as read for a given user by performing an upsert
	 * into PostReadStatus, returns true on success.</p>
	 * 
	 * @param postId the id of the post to mark read
	 * @param userName the username to mark the post read for
	 * @return true if the operation succeeded, false otherwise
	 *  
	 */
	public boolean markPostAsRead(int postId, String userName) {
		String merge = "MERGE INTO PostReadStatus (postId, userName, isRead) KEY(postId, userName) VALUES (?, ?, TRUE)";
		try (PreparedStatement pstmt = connection.prepareStatement(merge)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean isPostReadByUser(int postId, String userName) </p>
	 * 
	 * <p> Description: Check if a post has been marked as read by
	 * a specified user, returns true if read, false if not or on error.</p>
	 * 
	 * @param postId the id of the post to check
	 * @param userName the username to query
	 * @return true if the post is marked read for the user, false otherwise
	 *  
	 */
	public boolean isPostReadByUser(int postId, String userName) {
		String query = "SELECT isRead FROM PostReadStatus WHERE postId = ? AND userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getBoolean("isRead");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean isReplyReadByUser(int replyId, String userName) </p>
	 * 
	 * <p> Description: Check whether a specific reply has been marked as read
	 * by a user.</p>
	 * 
	 * @param replyId the id of the reply to check
	 * @param userName the username to check read status for
	 * @return true if reply is marked read for the user, false otherwise
	 *  
	 */
	public boolean isReplyReadByUser(int replyId, String userName) {
		String query = "SELECT isRead FROM ReplyReadStatus WHERE replyId = ? AND userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyId);
			pstmt.setString(2, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("isRead");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p> Method: boolean updatePostToDeletedVisual(int postId) </p>
	 * 
	 * <p> Description: Perform a "visual delete" on a post by replacing the title
	 * with 'Post deleted' and clearing the body. Does not remove the row will returns
	 * true if the update affected a row.</p>
	 * 
	 * @param postId the id of the post to visually delete
	 * @return true if the post was updated successfully, false otherwise
	 *  
	 */
	public boolean updatePostToDeletedVisual(int postId) {
		String update = "UPDATE Posts SET title = ?, body = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, "Post deleted");
			pstmt.setString(2, "");
			pstmt.setInt(3, postId);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	/* ---------------------------------------------------------------------
	 *  OMAR HW2 NEW: END of Post & Reply related methods
	 * ------------------------------------------------------------------ */
	
	/* ---------------------------------------------------------------------
	 *  OMAR HW3 NEW: Start of threads managment
	 * ------------------------------------------------------------------ */
	/*******
	 * <p> Method: List<String> getThreads() </p>
	 *
	 * <p> Description: Return the list of thread names from the Threads table.
	 *   If the threads table is missing or empty, this may return an empty list. </p>
	 *
	 * @return list of thread names (may be empty)
	 */
	public List<String> getThreads() {
		List<String> threads = new ArrayList<>();
		String query = "SELECT name FROM Threads ORDER BY name";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				threads.add(rs.getString("name"));
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return threads;
	}

	/*******
	 * <p> Method: boolean createThread(String name) </p>
	 *
	 * <p> Description: Create a new thread name in the Threads table. Returns true if created. </p>
	 *
	 * @param name new thread name
	 * @return true if successful, else false
	 */
	public boolean createThread(String name) {
		if (name == null || name.trim().isEmpty()) return false;
		String insert = "INSERT INTO Threads (name) VALUES (?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
			pstmt.setString(1, name.trim());
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			// can be duplicate key print for debug
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: boolean updateThreadName(String oldName, String newName) </p>
	 *
	 * <p> Description: Renames a thread in the Threads table and updates any Posts that referenced
	 *   the old name to the new name. Returns true if update successful. </p>
	 *
	 * @param oldName existing thread name
	 * @param newName new thread name
	 * @return true if successful
	 */
	public boolean updateThreadName(String oldName, String newName) {
		if (oldName == null || newName == null) return false;
		String updateThreads = "UPDATE Threads SET name = ? WHERE name = ?";
		String updatePosts = "UPDATE Posts SET thread = ? WHERE thread = ?";
		try (PreparedStatement pt = connection.prepareStatement(updateThreads)) {
			pt.setString(1, newName.trim());
			pt.setString(2, oldName.trim());
			int affectedThreads = pt.executeUpdate();
			// Update posts even if the Threads table had a row
			try (PreparedStatement pp = connection.prepareStatement(updatePosts)) {
				pp.setString(1, newName.trim());
				pp.setString(2, oldName.trim());
				pp.executeUpdate();
			}
			return affectedThreads > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: boolean deleteThread(String name) </p>
	 *
	 * <p> Description: Deletes a thread entry from Threads table. Any Posts that referenced
	 *   this thread will be moved to General thread. Returns true if deleted. </p>
	 *
	 * @param name thread name to delete
	 * @return true if successful
	 */
	public boolean deleteThread(String name) {
		if (name == null || name.trim().isEmpty()) return false;
		String updatePosts = "UPDATE Posts SET thread = 'General' WHERE thread = ?";
		String deleteThread = "DELETE FROM Threads WHERE name = ?";
		try {
			// move posts 1st
			try (PreparedStatement pu = connection.prepareStatement(updatePosts)) {
				pu.setString(1, name.trim());
				pu.executeUpdate();
			}
			try (PreparedStatement pd = connection.prepareStatement(deleteThread)) {
				pd.setString(1, name.trim());
				int rows = pd.executeUpdate();
				return rows > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*******
	 * <p> Method: boolean updateReplyToDeletedVisual(int replyId) </p>
	 *
	 * <p> Description: Performs a visual delete of a reply by replacing reply body with "Reply deleted" but keeping Author name.
	 *   Returns true if update succeeded. </p>
	 *
	 * @param replyId id of the reply to visually delete
	 * @return true if successful
	 */
	public boolean updateReplyToDeletedVisual(int replyId) {
		String update = "UPDATE Replies SET content = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, "Reply deleted");
			pstmt.setInt(2, replyId);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/* ---------------------------------------------------------------------
	 *  OMAR HW3 NEW: END of threads managment
	 * ------------------------------------------------------------------ */
	
	/* ---------------------------------------------------------------------
	 *  Chuan New: Ticket System START
	 * ------------------------------------------------------------------ */

	// Create a new ticket; returns generated id or -1 on failure
	public int createTicket(Ticket t) {
		if (t == null) return -1;
		String sql = "INSERT INTO Tickets "
				   + "(title, body, creatorUsername, status, reopenedFromId, created_at, deleted) "
				   + "VALUES (?, ?, ?, ?, ?, ?, FALSE)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, t.getTitle());
			pstmt.setString(2, t.getBody());
			pstmt.setString(3, t.getCreatorUserName());
			pstmt.setString(4, t.getStatus());

			if (t.getReopenedFromId() == null) {
				pstmt.setNull(5, Types.INTEGER);
			} else {
				pstmt.setInt(5, t.getReopenedFromId());
			}

			pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

			pstmt.executeUpdate();
			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				if (keys.next()) return keys.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Update ticket status (e.g., OPEN / CLOSED)
	public boolean updateTicketStatus(int ticketId, String newStatus) {
		String sql = "UPDATE Tickets SET status = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, newStatus);
			pstmt.setInt(2, ticketId);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Update ticket title/body (edit ticket)
	public boolean updateTicketDetails(int ticketId, String newTitle, String newBody) {
		String sql = "UPDATE Tickets SET title = ?, body = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, newTitle);
			pstmt.setString(2, newBody);
			pstmt.setInt(3, ticketId);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Mark a ticket as visually deleted (owner or admin only)
	public boolean visuallyDeleteTicket(int ticketId, String requesterUserName) {
		// Check creator
		String check = "SELECT creatorUsername FROM Tickets WHERE id = ?";
		try (PreparedStatement pc = connection.prepareStatement(check)) {
			pc.setInt(1, ticketId);
			ResultSet rs = pc.executeQuery();
			if (!rs.next()) return false;
			String creator = rs.getString("creatorUsername");
			// If not owner and not admin, refuse
			if (!creator.equals(requesterUserName) && !isAdmin(requesterUserName)) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		String update = "UPDATE Tickets SET title = ?, body = ?, deleted = TRUE WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, "Ticket deleted");
			pstmt.setString(2, "");
			pstmt.setInt(3, ticketId);
			int affected = pstmt.executeUpdate();
			return affected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Retrieve a ticket by id, ignoring deleted ones
	public Ticket getTicketById(int ticketId) {
		String sql = "SELECT id, title, body, creatorUsername, status, reopenedFromId, "
				   + "created_at, deleted FROM Tickets WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, ticketId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean("deleted")) return null;
				Ticket t = new Ticket();
				t.setId(rs.getInt("id"));
				t.setTitle(rs.getString("title"));
				t.setBody(rs.getString("body"));
				t.setCreatorUserName(rs.getString("creatorUsername"));
				t.setStatus(rs.getString("status"));

				int reopenedId = rs.getInt("reopenedFromId");
				if (rs.wasNull()) t.setReopenedFromId(null);
				else t.setReopenedFromId(reopenedId);

				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) t.setCreatedAt(ts.toInstant());
				t.setDeleted(rs.getBoolean("deleted"));
				return t;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Retrieve tickets with optional filters:
	// keyword in title/body, statusFilter ("OPEN"/"CLOSED"/null), and creatorUsername for "mine"
	public List<Ticket> getTickets(String keyword, String statusFilter, String creatorUserName) {
		List<Ticket> list = new ArrayList<>();

		String base = "SELECT id, title, body, creatorUsername, status, "
					+ "reopenedFromId, created_at, deleted "
					+ "FROM Tickets WHERE deleted = FALSE";
		StringBuilder sb = new StringBuilder(base);

		boolean hasKeyword = (keyword != null && keyword.trim().length() > 0);
		boolean hasStatus  = (statusFilter != null && statusFilter.trim().length() > 0);
		boolean hasCreator = (creatorUserName != null && creatorUserName.trim().length() > 0);

		if (hasKeyword) {
			sb.append(" AND (LOWER(title) LIKE ? OR LOWER(body) LIKE ?)");
		}
		if (hasStatus) {
			sb.append(" AND status = ?");
		}
		if (hasCreator) {
			sb.append(" AND creatorUsername = ?");
		}
		sb.append(" ORDER BY created_at DESC");

		try (PreparedStatement pstmt = connection.prepareStatement(sb.toString())) {
			int idx = 1;
			if (hasKeyword) {
				String kw = "%" + keyword.toLowerCase() + "%";
				pstmt.setString(idx++, kw);
				pstmt.setString(idx++, kw);
			}
			if (hasStatus) {
				pstmt.setString(idx++, statusFilter);
			}
			if (hasCreator) {
				pstmt.setString(idx++, creatorUserName);
			}
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Ticket t = new Ticket();
				t.setId(rs.getInt("id"));
				t.setTitle(rs.getString("title"));
				t.setBody(rs.getString("body"));
				t.setCreatorUserName(rs.getString("creatorUsername"));
				t.setStatus(rs.getString("status"));

				int reopenedId = rs.getInt("reopenedFromId");
				if (rs.wasNull()) t.setReopenedFromId(null);
				else t.setReopenedFromId(reopenedId);

				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) t.setCreatedAt(ts.toInstant());
				t.setDeleted(rs.getBoolean("deleted"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	

	// Chuan NEW (Ticket System Comments)
	/**

	Inserts a new comment into the TicketComments table.

	@param ticketId The ticket this comment belongs to.

	@param authorUserName The user who wrote the comment.

	@param content The text of the comment.
	*/
	public void createTicketComment(int ticketId, String authorUserName, String content) {
		String sql = "INSERT INTO TicketComments "
				   + "(ticketId, authorUsername, content, created_at) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, ticketId);
			pstmt.setString(2, authorUserName);
			pstmt.setString(3, content);
			pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Chuan NEW
	public List<TicketComment> getCommentsForTicket(int ticketId) {
		List<TicketComment> comments = new ArrayList<>();
		String sql = "SELECT id, ticketId, authorUsername, content, created_at "
				   + "FROM TicketComments WHERE ticketId = ? ORDER BY created_at ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, ticketId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				TicketComment c = new TicketComment();
				c.setId(rs.getInt("id"));
				c.setTicketId(rs.getInt("ticketId"));
				c.setAuthorUserName(rs.getString("authorUsername"));
				c.setContent(rs.getString("content"));
				Timestamp ts = rs.getTimestamp("created_at");
				if (ts != null) c.setCreatedAt(ts.toInstant());
				comments.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comments;
	}
	/* ---------------------------------------------------------------------
	 *  Chuan New: Ticket System END
	 * ------------------------------------------------------------------ */

	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	/*******
	 * <p> Method: boolean deleteUser(String Username) </p>
	 * 
	 * <p> Description: Deletes the selected user </p>
	 * 
	 */
	public boolean deleteUser(String username) {
		String sql = "DELETE FROM userDB WHERE userName = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			int rowsAffected = stmt.executeUpdate();
			// Returns true if at least one row deleted
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error trying to delete user: " + e.getMessage());
			e.printStackTrace();
			// if delete user failed for some reason
			return false;
		}
	}
	
	public boolean isAdmin(String username) {
		String sql = "SELECT adminRole FROM userDB WHERE userName = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					//	Return twue if adminRole column is true for the user				
					return rs.getBoolean("adminRole");
				}
			}
		} catch (SQLException e) {
			System.err.println("*** ERROR *** Database error checking admin: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	  /*******
	   * --- GRADING SYSTEM METHODS (added from Version B) ---
     * * @author Daniel Ortiz Figueroa
     */
	
	 /********
     * <p> Method: getGradingParameters() </p>
     * * <p> Description: Gets the grading parameter from the database
     * with a specified maximum score. </p>
     * */
    // Get all Grading Parameters
    public List<GradingParameter> getGradingParameters() {
        List<GradingParameter> list = new ArrayList<>();
        String query = "SELECT * FROM GradingParameters";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                list.add(new GradingParameter(rs.getInt("id"), rs.getString("name"), rs.getDouble("maxScore")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /********
     * <p> Method: addGradingParameter(String name, double maxScore) </p>
     * * <p> Description: Adds a new grading parameter (e.g., "Participation") to the database
     * with a specified maximum score. </p>
     * * @param name The name of the grading parameter.
     * @param maxScore The maximum possible score or weight for this parameter.
     * */
    // Add new Parameter
    public void addGradingParameter(String name, double maxScore) {
        String query = "INSERT INTO GradingParameters (name, maxScore) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, maxScore);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*******
     * <p> Method: deleteGradingParameter(int id) </p>
     * * <p> Description: Deletes a grading parameter from the database using its unique ID.
     * This action usually cascades to remove associated scores for students. </p>
     * * @param id The unique identifier of the grading parameter to delete.
     */
    // Delete Parameter
    public void deleteGradingParameter(int id) {
        String query = "DELETE FROM GradingParameters WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    /*******
     * <p> Method: updateGradingParameter(int id, String name, double maxScore) </p>
     * * <p> Description: Updates the name and maximum score of an existing grading parameter
     * identified by its ID. </p>
     * * @param id The unique identifier of the parameter to update.
     * @param name The new name for the parameter.
     * @param maxScore The new maximum score for the parameter.
     */
    // Update Parameter
    public void updateGradingParameter(int id, String name, double maxScore) {
        String query = "UPDATE GradingParameters SET name = ?, maxScore = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, maxScore);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*******
     * <p> Method: getStudentScores(String studentUserName) </p>
     * * <p> Description: Retrieves a list of scores for a specific student. It performs a LEFT JOIN
     * between GradingParameters and StudentScores to ensure all grading categories are returned,
     * defaulting to 0.0 if no grade has been assigned yet. </p>
     * * @param studentUserName The username of the student whose scores are being retrieved.
     * @return A List of StudentScore objects containing the parameter details and the student's score.
     */
    // Get Scores for a specific student (joins with parameters to ensure all parameters show up)
    public List<StudentScore> getStudentScores(String studentUserName) {
        List<StudentScore> list = new ArrayList<>();
        // Left join ensures we see parameters even if the student hasn't been graded yet
        String query = "SELECT gp.id, gp.name, gp.maxScore, COALESCE(ss.score, 0.0) as score " +
                       "FROM GradingParameters gp " +
                       "LEFT JOIN StudentScores ss ON gp.id = ss.paramId AND ss.studentUserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new StudentScore(
                    rs.getString("name"),
                    rs.getInt("id"),
                    rs.getDouble("score"),
                    rs.getDouble("maxScore")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /*******
     * <p> Method: updateStudentScore(String studentUserName, int paramId, double score) </p>
     * * <p> Description: Updates or inserts a score for a specific student and grading parameter.
     * Uses the SQL MERGE command (Upsert) to handle both creating new grades and updating existing ones. </p>
     * * @param studentUserName The username of the student being graded.
     * @param paramId The ID of the grading parameter.
     * @param score The score to assign to the student.
     */
    // Update or Insert a score 
    public void updateStudentScore(String studentUserName, int paramId, double score) {
        String query = "MERGE INTO StudentScores (studentUserName, paramId, score) KEY(studentUserName, paramId) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUserName);
            pstmt.setInt(2, paramId);
            pstmt.setDouble(3, score);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /* --------------------------------------------------------------------------------
	 * 
	 *  BRIAN NEW METHODS FOR FEEDBACK
	 *  
	 * --------------------------------------------------------------------------------
	 * 
	 * */

    /**
     * <p> Method: submitFeedback(String sender, String receiver, String subject, String content) </p>
     *
     * <p> Description: Insert a new feedback message into the FeedbackMessages table. The
     * new message will be marked as unread
     * and the created_at timestamp is set to the current instant.</p>
     *
     * @param sender   the username of the sender
     * @param receiver the username of the receiver
     * @param subject  the subject line of the feedback message
     * @param content  the body/content of the feedback message
     * @throws SQLException when there is an issue creating the SQL command or executing it.
     */
    // BRIAN NEW: Submit new feedback
    public void submitFeedback(String sender, String receiver, String subject, String content) throws SQLException {
        String sql = "INSERT INTO FeedbackMessages (senderUsername, receiverUsername, subject, content, created_at, isRead) VALUES (?, ?, ?, ?, ?, FALSE)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, subject);
            pstmt.setClob(4, new java.io.StringReader(content));
            pstmt.setTimestamp(5, Timestamp.from(Instant.now()));
            pstmt.executeUpdate();
        }
    }
    
    /**
     * <p> Method: List<Feedback> getFeedbackForUser(String username, boolean filterRead) </p>
     *
     * <p> Description: Retrieve all feedback messages for a given receiver (student view). </p>
     *
     * @param username   the receiver username whose feedback messages are requested
     * @param filterRead if true the caller intends only unread messages
     * @return a List of Feedback objects for the given receive
     */
    // BRIAN NEW: Gets all feedback for target user (student/receiver view)
    public List<Feedback> getFeedbackForUser(String username, boolean filterRead) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT id, senderUsername, receiverUsername, subject, content, created_at, isRead "
                   + "FROM FeedbackMessages WHERE receiverUsername = ? ORDER BY created_at DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Feedback f = new Feedback(
                    rs.getInt("id"),
                    rs.getString("senderUsername"),
                    rs.getString("receiverUsername"),
                    rs.getString("subject"),
                    rs.getClob("content").getSubString(1, (int) rs.getClob("content").length()),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("isRead")
                );
                feedbackList.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }
    
    /**
     * <p> Method: List<Feedback> getAllFeedback(String filterStudentUsername, boolean filterRead) </p>
     *
     * <p> Description: Retrieve feedback messages for staff view. Can filters by a specific
     * student username (receiverUsername) and/or to unread messages when
     * filterRead is true.</p>
     *
     * @param filterStudentUsername optional receiver username to filter by
     * @param filterRead            if true only unread feedback (isRead = FALSE) will be returned
     * @return a List of Feedback objects matching the provided filters
     */
    // BRIAN NEW: Gets all feedback (staff view) and filters by student if selected
    public List<Feedback> getAllFeedback(String filterStudentUsername, boolean filterRead) {
        List<Feedback> feedbackList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, senderUsername, receiverUsername, subject, content, created_at, isRead FROM FeedbackMessages WHERE 1=1");
        int paramIndex = 1;

        if (filterStudentUsername != null && !filterStudentUsername.trim().isEmpty()) {
            sql.append(" AND receiverUsername = ?");
        }
        if (filterRead) {
             sql.append(" AND isRead = FALSE");
        }
        sql.append(" ORDER BY created_at DESC");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            if (filterStudentUsername != null && !filterStudentUsername.trim().isEmpty()) {
                 pstmt.setString(paramIndex++, filterStudentUsername);
            }
           
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Feedback f = new Feedback(
                    rs.getInt("id"),
                    rs.getString("senderUsername"),
                    rs.getString("receiverUsername"),
                    rs.getString("subject"),
                    rs.getClob("content").getSubString(1, (int) rs.getClob("content").length()),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("isRead")
                );
                feedbackList.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }
    
    /**
     * <p> Method: Feedback getFeedbackById(int feedbackId) </p>
     *
     * <p> Description: Retrieve a single feedback message by its unique ID. Returns null if no message
     * with the given ID exists.</p>
     *
     * @param feedbackId the unique ID of the feedback message
     * @return a Feedback object if found, otherwise null
     */
    // BRIAN NEW: Get single feedback item by ID
    public Feedback getFeedbackById(int feedbackId) {
        String sql = "SELECT id, senderUsername, receiverUsername, subject, content, created_at, isRead FROM FeedbackMessages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Feedback(
                    rs.getInt("id"),
                    rs.getString("senderUsername"),
                    rs.getString("receiverUsername"),
                    rs.getString("subject"),
                    rs.getClob("content").getSubString(1, (int) rs.getClob("content").length()),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("isRead")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * <p> Method: void markRead(int feedbackId) </p>
     *
     * <p> Description: Mark the feedback message with the given ID as read by setting isRead to TRUE.</p>
     *
     * @param feedbackId the unique ID of the feedback message to mark as read
     * @throws SQLException when there is an issue creating the SQL command or executing it.
     */
    // BRIAN NEW: Mark feedback as read
    public void markRead(int feedbackId) throws SQLException {
        String sql = "UPDATE FeedbackMessages SET isRead = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);
            pstmt.executeUpdate();
        }
    }

    
    /**
     * <p> Method: deleteFeedback() </p>
     * <p> Description: Deletes a feedback message by ID from the database. 
     *  Note: This also triggers cascading deletion of related FeedbackReplies due to ON DELETE CASCADE.</p>
     */
    public void deleteFeedback(int feedbackId) throws SQLException {
        String sql = "DELETE FROM FeedbackMessages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);
            pstmt.executeUpdate();
        }
    }
    
    /* --------------------------------------------------------------------------------
	 * 
	 *  BRIAN NEW FEEDBACK SYSTEM END
	 *  
	 * --------------------------------------------------------------------------------
	 * 
	 * */
    
}
