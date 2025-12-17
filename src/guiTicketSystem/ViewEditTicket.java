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
 * <p> Title: ViewEditTicket Class </p>
 *
 * <p> Description: Provides the JavaFX dialog for editing the
 * title and body of an existing ticket. This window is modal and
 * requires valid input before the ticket is updated. </p>
 *
 * <p> Copyright:
 * Lynn Robert Carter © 2025 </p>
 *
 * @author Chuan Nguyen
 * @version 1.00
 */
public class ViewEditTicket {

    /**********
     * <p> Method: displayEditTicket(Stage, User, Ticket) </p>
     *
     * <p> Description: Displays a modal dialog allowing the user to
     * edit the title and body of an existing ticket. If the ticket
     * reference is null, the dialog is not displayed. When the user
     * confirms the update, the modified values are passed to the
     * ticket controller. </p>
     *
     * @param owner  The parent Stage for this dialog.
     * @param user   The currently logged-in user (not modified).
     * @param ticket The ticket being edited. Must not be null.
     */
    public static void displayEditTicket(Stage owner, User user, Ticket ticket) {
        if (ticket == null) return;

        Stage window = new Stage();
        window.initOwner(owner);
        window.initModality(Modality.APPLICATION_MODAL);

        Label lblHeader = new Label("Edit Ticket");
        lblHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label lblTitle = new Label("Title:");
        TextField txtTitle = new TextField(ticket.getTitle());

        Label lblBody = new Label("Body:");
        TextArea txtBody = new TextArea(ticket.getBody());
        txtBody.setPrefRowCount(6);
        txtBody.setWrapText(true);

        Button btnOk = new Button("Save");
        Button btnCancel = new Button("Cancel");

        btnCancel.setOnAction(e -> window.close());

        btnOk.setOnAction(e -> {
            String newTitle = txtTitle.getText();
            String newBody = txtBody.getText();
            if (newTitle == null || newTitle.trim().isEmpty()) {
                return;
            }
            ControllerTicketSystem.updateTicketDetails(ticket, newTitle.trim(), newBody);
            window.close();
        });

        VBox root = new VBox(8, lblHeader, lblTitle, txtTitle, lblBody, txtBody, btnOk, btnCancel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 300);
        window.setScene(scene);
        window.setTitle("Edit Ticket");
        window.showAndWait();
    }
}
