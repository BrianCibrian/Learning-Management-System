package guiTicketSystem;

import entityClasses.Ticket;
import entityClasses.TicketComment;
import entityClasses.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewTicketDetail Class. </p>
 *
 * <p> Description: Ticket detail + discussion view. This class provides
 * a modal window that shows a ticket's metadata and its associated
 * discussion comments, and allows adding new comments when the ticket
 * is open. </p>
 */
public class ViewTicketDetail {

    /**
     * Display the ticket detail and discussion dialog for a given ticket.
     * The dialog is modal with respect to the specified owner stage.
     *
     * @param owner  the parent {@link Stage} that owns this dialog
     * @param user   the current logged-in {@link User} (reserved for future use)
     * @param ticket the {@link Ticket} to display; if {@code null} the method returns immediately
     */
    public static void displayTicketDetail(Stage owner, User user, Ticket ticket) {
        if (ticket == null) return;

        Stage window = new Stage();
        window.initOwner(owner);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Ticket Detail - ID " + ticket.getId());

        BorderPane root = new BorderPane();

        // Top: ticket info
        Label lblTitle = new Label("Title: " + ticket.getTitle());
        Label lblCreator = new Label("Creator: " + ticket.getCreatorUserName());
        Label lblStatus = new Label("Status: " + ticket.getStatus());
        String createdStr = (ticket.getCreatedAt() != null)
                ? ticket.getCreatedAt().toString()
                : "";
        Label lblCreated = new Label("Created: " + createdStr);

        VBox infoBox = new VBox(4, lblTitle, lblCreator, lblStatus, lblCreated);
        infoBox.setPadding(new Insets(10));

        // Center: comments list
        ListView<String> lstComments = new ListView<>();
        reloadComments(ticket, lstComments);

        // Bottom: add comment
        TextArea txtComment = new TextArea();
        txtComment.setPrefRowCount(3);
        txtComment.setWrapText(true);
        txtComment.setPromptText("Add a comment...");

        Button btnAddComment = new Button("Add Comment");
        Button btnClose = new Button("Close Window");

        // Disable add-comment if ticket is CLOSED
        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {
            txtComment.setDisable(true);
            btnAddComment.setDisable(true);
        }

        btnAddComment.setOnAction(e -> {
            String content = txtComment.getText();
            ControllerTicketSystem.addCommentToTicket(ticket, content);
            txtComment.clear();
            reloadComments(ticket, lstComments);
        });

        btnClose.setOnAction(e -> window.close());

        HBox buttons = new HBox(8, btnAddComment, btnClose);
        VBox bottomBox = new VBox(5, txtComment, buttons);
        bottomBox.setPadding(new Insets(10));

        root.setTop(infoBox);
        root.setCenter(lstComments);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 600, 400);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * Reloads the comments for the given ticket and populates the
     * supplied {@link ListView} with formatted comment entries.
     *
     * @param ticket      the {@link Ticket} whose comments should be loaded
     * @param lstComments the {@link ListView} to populate with comment text
     */
    private static void reloadComments(Ticket ticket, ListView<String> lstComments) {
        lstComments.getItems().clear();
        List<TicketComment> comments = ControllerTicketSystem.loadCommentsForTicket(ticket.getId());
        if (comments == null) return;

        DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
        for (TicketComment c : comments) {
            String ts = (c.getCreatedAt() != null) ? fmt.format(c.getCreatedAt()) : "";
            String line = String.format("[%s] %s: %s", ts, c.getAuthorUserName(), c.getContent());
            lstComments.getItems().add(line);
        }
    }
}
