package entityClasses;

import java.time.Instant;

/*******
 * <p> Title: TicketComment Class </p>
 *
 * <p> Description: Represents a comment in a ticket discussion thread. </p>
 */
public class TicketComment {

    /** The unique ID of the comment. */
    private int id;

    /** The ID of the ticket that this comment belongs to. */
    private int ticketId;

    /** The username of the author who wrote this comment. */
    private String authorUserName;

    /** The text content of the comment. */
    private String content;

    /** The timestamp when this comment was created. */
    private Instant createdAt;

    /** 
     * Default constructor for a TicketComment.
     */
    public TicketComment() {}

    /**
     * Returns the unique ID of this comment.
     * @return the comment's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of this comment.
     * @param id the comment's ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of the ticket associated with this comment.
     * @return the ticket ID
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * Sets the ticket ID associated with this comment.
     * @param ticketId the ID of the ticket
     */
    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * Returns the username of the author of this comment.
     * @return the author's username
     */
    public String getAuthorUserName() {
        return authorUserName;
    }

    /**
     * Sets the username of the author of this comment.
     * @param authorUserName the author's username
     */
    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    /**
     * Returns the text content of this comment.
     * @return the comment content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the text content of this comment.
     * @param content the comment text
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the timestamp when this comment was created.
     * @return the creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this comment was created.
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
