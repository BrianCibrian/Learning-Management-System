package entityClasses;

import java.time.Instant;

/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: This Reply class represents a reply entity in the system.  It contains the reply's
 *  details such as content, author, associated post id, timestamp, and read flag.</p>
 * 
 * 
 * @author Omar Munoz
 * 
 */ 
public class Reply {

	private int id;						//reply id
	private int postId;					//id of the post this reply belongs to
	private String content;				// reply message content
	private String authorUsername;		// reply author
	private Instant createdAt;			// time reply was made
	private boolean read;				// if reply was read

	/*****
	 * <p> Method: Reply() </p>
	 * 
	 * <p> Description: Default constructor, makes a empty Reply object with default values </p>
	 */
	public Reply() {
		this.id = 0;
		this.postId = 0;
		this.content = "";
		this.authorUsername = "";
		this.createdAt = Instant.now();
		this.read = false;
	}
	
	/*****
	 * <p> Method: Reply(int id, int postId, String content, String author, Instant createdAt, boolean read) </p>
	 * 
	 * <p> Description: Constructs a Reply with all attributes specified </p>
	 * @param id unique identifier for the reply
	 * @param postId id of the post this reply belongs to
	 * @param content text content of the reply
	 * @param author username of the reply author
	 * @param createdAt timestamp when the reply was created
	 * @param read whether the reply is marked as read
	 */
	public Reply(int id, int postId, String content, String author, Instant createdAt, boolean read) {
		this.id = id;
		this.postId = postId;
		this.content = content;
		this.authorUsername = author;
		this.createdAt = (createdAt == null) ? Instant.now() : createdAt;
		this.read = read;
	}

	/*****
	 * <p> Method: markRead() </p>
	 * 
	 * <p> Description: Marks this reply as read, doesn't persist to db, db is responsible for it. </p>
	 */
	public void markRead() {
		this.read = true;
	}

	/** Returns the unique ID of this reply.
	 *  @return the unique ID of this reply 
	 */
	public int getId() { return id; }

	/** Sets the unique ID of this reply.
	 *  @param id sets the unique ID of this reply 
	 */
	public void setId(int id) { this.id = id; }

	/** Returns the post ID this reply belongs to.
	 *  @return the ID of the post this reply belongs to 
	 */
	public int getPostId() { return postId; }

	/** Sets the associated post ID for this reply.
	 *  @param postId sets the post ID this reply belongs to 
	 */
	public void setPostId(int postId) { this.postId = postId; }

	/** Returns the text content of the reply.
	 *  @return the text content of the reply 
	 */
	public String getContent() { return content; }

	/** Sets the text content of the reply.
	 *  @param content sets the text content of the reply 
	 */
	public void setContent(String content) { this.content = content; }

	/** Returns the username of the reply’s author.
	 *  @return the username of the reply’s author 
	 */
	public String getAuthorUsername() { return authorUsername; }

	/** Sets the username of the reply’s author.
	 *  @param authorUsername sets the username of the reply’s author 
	 */
	public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

	/** Returns the timestamp when this reply was created.
	 *  @return the timestamp when this reply was created 
	 */
	public Instant getCreatedAt() { return createdAt; }

	/** Sets the creation timestamp of this reply.
	 *  @param createdAt sets the creation timestamp of this reply 
	 */
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	/** Returns whether the reply has been read.
	 *  @return true if the reply has been marked as read, false otherwise 
	 */
	public boolean getRead() { return read; }

	/** Sets whether the reply has been read.
	 *  @param read sets whether the reply has been read 
	 */
	public void setRead(boolean read) { this.read = read; }
}
