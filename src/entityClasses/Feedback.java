package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Feedback Class </p>
 * 
 * <p> Description: This Feedback class represents a feedback entity in the system.  It contains
 *  details such as the senderUserName, receiverUsername, subject, content, submissionDate and isRead. </p>
 * 
 * <p> Copyright: Brian Cibrian © 2025 </p>
 * 
 * @author Brian Cibrian
 * 
 * 
 */ 

public class Feedback {
    private int feedbackId;
    private String senderUsername;
    private String receiverUsername; // The student name (staff can see all)
    private String subject;
    private String content;
    private LocalDateTime submissionDate;
    private boolean isRead; // Status for the receiver

    // Constructor
    public Feedback(int feedbackId, String senderUsername, String receiverUsername, String subject, String content, LocalDateTime submissionDate, boolean isRead) {
        this.feedbackId = feedbackId;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.subject = subject;
        this.content = content;
        this.submissionDate = submissionDate;
        this.isRead = isRead;
    }

    // Getters and Setters
    public int getFeedbackId() { return feedbackId; }
    public String getSenderUsername() { return senderUsername; }
    public String getReceiverUsername() { return receiverUsername; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    @Override
    public String toString() {
        // Display useful info in a ListView
        return String.format("[%s] To: %s | From: %s | Subject: %s", 
            isRead ? "Read" : "Unread", receiverUsername, senderUsername, subject);
    }
}
