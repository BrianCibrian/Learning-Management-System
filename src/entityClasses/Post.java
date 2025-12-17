package entityClasses;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Title: Post Class</p>
 * 
 * <p> Description: This Post class represents a post entity in the system. 
 * Contains the post's details such as title, body, author, thread, timestamp, replies and deleted flag </p>
 *
 * @author Omar Munoz 
 */
public class Post {

	/** Unique ID for the post. */
	private int id;

	/** Title or subject line of the post. */
	private String title;

	/** Main text content of the post. */
	private String body;

	/** Username of the post author. */
	private String authorUsername;

	/** Discussion thread or category (e.g., General, Help, Announcements). */
	private String thread;

	/** Timestamp when the post was created. */
	private Instant createdAt;

	/** True if the post has been logically deleted. */
	private boolean deleted;

	/** List of replies associated with this post. */
	private List<Reply> replies;

	/** Count of replies that are unread. */
	private int unreadCount;

	/** True if this post has any unread replies. */
	private boolean hasUnread;

	/** Set of usernames representing users who have read this post. */
	private Set<String> readByUsers;

	/**
	 * Default constructor.
	 * <p>Initializes a blank Post with default values.</p>
	 */
	public Post() {
		this.id = 0;
		this.title = "";
		this.body = "";
		this.authorUsername = "";
		this.thread = "General";
		this.createdAt = Instant.now();
		this.deleted = false;
		this.replies = new ArrayList<>();
		this.unreadCount = 0;
		this.hasUnread = false;
		this.readByUsers = new HashSet<>();
	}

	/**
	 * Full constructor for creating a Post with specific values.
	 *
	 * @param id		 unique identifier for the post
	 * @param title	  post title
	 * @param body	   post body content
	 * @param author	 author username
	 * @param thread	 discussion thread or category
	 * @param createdAt  timestamp of creation
	 * @param deleted	true if post is marked deleted
	 */
	public Post(int id, String title, String body, String author, String thread, Instant createdAt, boolean deleted) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.authorUsername = author;
		this.thread = (thread == null || thread.isEmpty()) ? "General" : thread;
		this.createdAt = (createdAt == null) ? Instant.now() : createdAt;
		this.deleted = deleted;
		this.replies = new ArrayList<>();
		this.unreadCount = 0;
		this.hasUnread = false;
		this.readByUsers = new HashSet<>();
	}

	/**
	 * Adds a reply to this post's list of replies.
	 *
	 * @param r the reply object to add
	 */
	public void addReply(Reply r) {
		if (r == null) return;
		replies.add(r);
	}

	/**
	 * Removes a reply from the list by its ID.
	 *
	 * @param replyId the unique reply ID
	 * @return true if successfully removed; false otherwise
	 */
	public boolean removeReplyById(int replyId) {
		for (int i = 0; i < replies.size(); i++) {
			if (replies.get(i).getId() == replyId) {
				replies.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the total number of replies currently loaded.
	 *
	 * @return number of replies
	 */
	public int getReplyCount() {
		return replies == null ? 0 : replies.size();
	}

	/**
	 * Returns a short summary of the post for display (title, author, reply count).
	 *
	 * @return formatted summary string
	 */
	public String getSummary() {
		String t = (title == null || title.isEmpty()) ? "<no title>" : title;
		String a = (authorUsername == null || authorUsername.isEmpty()) ? "<unknown>" : authorUsername;
		return t + " by " + a + " (" + getReplyCount() + " replies)";
	}

	/** Returns the post ID.
	 *  @return post ID */
	public int getId() { return id; }

	/** Sets the post ID.
	 *  @param id sets the post ID */
	public void setId(int id) { this.id = id; }

	/** Returns the post title.
	 *  @return post title */
	public String getTitle() { return title; }

	/** Sets the post title.
	 *  @param title sets the post title */
	public void setTitle(String title) { this.title = title; }

	/** Returns the post body content.
	 *  @return post body */
	public String getBody() { return body; }

	/** Sets the post body content.
	 *  @param body sets the post body */
	public void setBody(String body) { this.body = body; }

	/** Returns the author’s username.
	 *  @return author username */
	public String getAuthorUsername() { return authorUsername; }

	/** Sets the author’s username.
	 *  @param authorUsername sets the author username */
	public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

	/** Returns the thread/category.
	 *  @return post thread or category */
	public String getThread() { return thread; }

	/** Sets the thread/category.
	 *  @param thread sets the thread category */
	public void setThread(String thread) {
		this.thread = (thread == null || thread.isEmpty()) ? "General" : thread;
	}

	/** Returns the creation timestamp.
	 *  @return creation timestamp */
	public Instant getCreatedAt() { return createdAt; }

	/** Sets the creation timestamp.
	 *  @param createdAt sets the creation timestamp */
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	/** Returns whether this post is logically deleted.
	 *  @return true if deleted */
	public boolean getDeleted() { return deleted; }

	/** Sets the logical delete flag.
	 *  @param deleted sets delete flag */
	public void setDeleted(boolean deleted) { this.deleted = deleted; }

	/** Returns the list of replies.
	 *  @return list of replies */
	public List<Reply> getReplies() { return replies; }

	/** Replaces the list of replies.
	 *  @param replies sets reply list */
	public void setReplies(List<Reply> replies) { this.replies = replies == null ? new ArrayList<>() : replies; }

	/** Returns the unread reply count.
	 *  @return unread reply count */
	public int getUnreadCount() { return unreadCount; }

	/**
	 * Sets unread reply count and updates hasUnread flag.
	 *
	 * @param unreadCount new unread reply count
	 */
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
		this.hasUnread = unreadCount > 0;
	}

	/** Returns whether there are unread replies.
	 *  @return true if post has unread replies */
	public boolean hasUnread() { return hasUnread; }

	/** Sets whether there are unread replies.
	 *  @param hasUnread sets unread flag */
	public void setHasUnread(boolean hasUnread) { this.hasUnread = hasUnread; }

	/** Returns the set of users who have read this post.
	 *  @return set of usernames who have read this post */
	public Set<String> getReadByUsers() { return readByUsers; }

	/** Replaces the set of users who have read this post.
	 *  @param readByUsers sets the users who have read this post */
	public void setReadByUsers(Set<String> readByUsers) {
		this.readByUsers = readByUsers == null ? new HashSet<>() : new HashSet<>(readByUsers);
	}

	/**
	 * Marks a user as having read this post.
	 *
	 * @param username the username of the reader
	 */
	public void markReadByUser(String username) {
		if (username == null) return;
		readByUsers.add(username);
	}

	/**
	 * Checks if a specific user has read this post.
	 *
	 * @param username username to check
	 * @return true if user has read, false otherwise
	 */
	public boolean hasBeenReadByUser(String username) {
		if (username == null) return false;
		return readByUsers.contains(username);
	}

	/**
	 * Checks if the post is unread for a given user.
	 *
	 * @param username username to check
	 * @return true if post is unread, false otherwise
	 */
	public boolean isUnreadForUser(String username) {
		if (username == null) return false;
		return !hasBeenReadByUser(username);
	}

	/**
	 * Updates unread count and unread flag based on list of replies.
	 *
	 * @param replyList list of reply objects to evaluate
	 */
	public void updateUnreadFromReplies(List<Reply> replyList) {
		if (replyList == null) {
			this.unreadCount = 0;
			this.hasUnread = false;
			return;
		}
		int cnt = 0;
		for (Reply r : replyList) {
			if (r == null) continue;
			if (!r.getRead()) cnt++;
		}
		this.unreadCount = cnt;
		this.hasUnread = cnt > 0;
	}
}
