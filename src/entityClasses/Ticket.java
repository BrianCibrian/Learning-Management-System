package entityClasses;

import java.time.Instant;

/*******
 * <p> Title: Ticket Class </p>
 *
 * <p> Description: Represents a staff/admin request ticket. </p>
 *
 * <p>This class models tickets used in the staff/admin help system.
 * A ticket contains a title, body, creator username, status, an optional
 * reference to another ticket (in case of reopening), a timestamp,
 * and a visual-delete flag.</p>
 */
public class Ticket {

    /** Unique numeric ID of the ticket (auto-incremented in database). */
    private int id;

    /** Short text title summarizing the issue. */
    private String title;

    /** Full description or body text of the ticket. */
    private String body;

    /** Username of the ticket creator; corresponds to userDB.userName. */
    private String creatorUserName;

    /** Status of the ticket, typically "OPEN" or "CLOSED". */
    private String status;

    /** 
     * ID of the original ticket if this ticket was created as a "reopened" version.
     * May be {@code null}. 
     */
    private Integer reopenedFromId;

    /** Timestamp (UTC) indicating when the ticket was created. */
    private Instant createdAt;

    /** Whether this ticket has been visually deleted (soft delete). */
    private boolean deleted;

    /** Default empty constructor. */
    public Ticket() {}

    /**
     * Gets the ticket ID.
     * @return the unique ticket ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ticket ID.
     * @param id the numeric ID to assign to this ticket
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ticket title.
     * @return the title string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the ticket title.
     * @param title the new title for this ticket
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the ticket body/description.
     * @return the body text
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the ticket body text.
     * @param body the content describing the issue
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the username of the ticket creator.
     * @return a username string
     */
    public String getCreatorUserName() {
        return creatorUserName;
    }

    /**
     * Sets the username of the ticket creator.
     * @param creatorUserName the creator's username
     */
    public void setCreatorUserName(String creatorUserName) {
        this.creatorUserName = creatorUserName;
    }

    /**
     * Gets the ticket status ("OPEN", "CLOSED").
     * @return the status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the ticket status.
     * @param status must be "OPEN" or "CLOSED"
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the ID of the ticket from which this ticket was reopened.
     * @return the ID of the previous ticket, or {@code null} if none
     */
    public Integer getReopenedFromId() {
        return reopenedFromId;
    }

    /**
     * Sets the reference ID to the original ticket if this one was reopened.
     * @param reopenedFromId the original ticket's ID
     */
    public void setReopenedFromId(Integer reopenedFromId) {
        this.reopenedFromId = reopenedFromId;
    }

    /**
     * Gets the timestamp when this ticket was created.
     * @return {@link Instant} creation time
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp at which this ticket was created.
     * @param createdAt creation time value
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Checks if the ticket is visually deleted.
     * @return {@code true} if deleted, {@code false} otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Marks the ticket as visually deleted or not.
     * @param deleted the new deleted state
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
