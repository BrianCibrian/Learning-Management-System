package guiTicketSystem;

import entityClasses.Ticket;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewCreateTicket Class </p>
 *
 * <p> Description: Simple dialog for creating or reopening a ticket. </p>
 */
public class ViewCreateTicket {

    /**********
     * <p> Method: displayCreateTicket(Stage, User, Ticket) </p>
     *
     * <p> Description: Displays the modal window for creating a
     * new ticket or reopening an existing one. If {@code reopenedFrom}
     * is not null, the title field is pre-filled to indicate a
     * reopen operation. </p>
     *
     * @param owner The parent stage.
     * @param user  The user creating the ticket.
     * @param reopenedFrom The original ticket when reopening, or null.
     */
    public static void displayCreateTicket(Stage owner, User user, Ticket reopenedFrom) {
        Stage window = new Stage();
        window.initOwner(owner);
        window.initModality(Modality.APPLICATION_MODAL);

        String headerText = (reopenedFrom == null) ? "Create Ticket" : "Reopen Ticket";

        Label lblHeader = new Label(headerText);
        lblHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label lblTitle = new Label("Title:");
        TextField txtTitle = new TextField();
        if (reopenedFrom != null && reopenedFrom.getTitle() != null) {
            txtTitle.setText("Reopen: " + reopenedFrom.getTitle());
        }

        Label lblBody = new Label("Body (optional):");
        TextArea txtBody = new TextArea();
        txtBody.setPrefRowCount(6);
        txtBody.setWrapText(true);

        Button btnOk = new Button("OK");
        Button btnCancel = new Button("Cancel");

        btnCancel.setOnAction(e -> window.close());

        btnOk.setOnAction(e -> {
            String title = txtTitle.getText();
            if (title == null || title.trim().isEmpty()) {
                return;
            }
            Ticket t = new Ticket();
            t.setTitle(title.trim());
            t.setBody(txtBody.getText());
            t.setCreatorUserName(user.getUserName());
            t.setStatus("OPEN");
            if (reopenedFrom != null) {
                t.setReopenedFromId(reopenedFrom.getId());
            }
            ControllerTicketSystem.createTicket(t);
            window.close();
        });

        VBox root = new VBox(8, lblHeader, lblTitle, txtTitle, lblBody, txtBody, btnOk, btnCancel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 300);
        window.setScene(scene);
        window.setTitle(headerText);
        window.showAndWait();
    }
}
