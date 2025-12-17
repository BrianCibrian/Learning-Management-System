package guiTicketSystem;

import entityClasses.Ticket;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*******
 * <p>Title: ViewTicketSystem Class</p>
 *
 * <p>Description: Provides the main Ticket System screen where staff/admin users
 * can create, edit, close, reopen, delete, and view tickets. Includes filtering
 * by keyword, status, and ownership.</p>
 */
public class ViewTicketSystem {

    /** The JavaFX Scene representing this ticket system view. */
    private Scene theScene;

    /** Text field for keyword search (title/body). */
    private TextField txtSearchTitle;

    /** Status filter dropdown (All, Open, Closed). */
    private ComboBox<String> cmbStatus;

    /** Checkbox to restrict view to tickets created by the current user. */
    private CheckBox chkMineOnly;

    /** Table displaying tickets. */
    private TableView<Ticket> tblTickets;

    /**********
     * <p>Constructor: ViewTicketSystem()</p>
     *
     * <p>Builds the Ticket System scene and initializes UI components.</p>
     */
    public ViewTicketSystem() {
        buildScene();
    }

    /**********
     * <p>Method: getScene()</p>
     *
     * <p>Returns the built Scene so the controller can set it on the Stage.</p>
     *
     * @return The Ticket System Scene.
     */
    public Scene getScene() {
        return theScene;
    }

    /**********
     * <p>Method: buildScene()</p>
     *
     * <p>Constructs all UI elements, filters, the ticket table, and the action buttons.</p>
     */
    private void buildScene() {
        BorderPane root = new BorderPane();

        // Top
        Label lblTitle = new Label("Ticket System");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        txtSearchTitle = new TextField();
        txtSearchTitle.setPromptText("Search title/body...");

        cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("All", "Open", "Closed");
        cmbStatus.setValue("All");

        chkMineOnly = new CheckBox("Show only my tickets");

        HBox filterRow = new HBox(8,
                new Label("Keyword:"), txtSearchTitle,
                new Label("Status:"), cmbStatus,
                chkMineOnly
        );

        VBox topBox = new VBox(5, lblTitle, filterRow);
        topBox.setPadding(new Insets(10));

        // Table
        tblTickets = new TableView<>();

        TableColumn<Ticket, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Ticket, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(250);

        TableColumn<Ticket, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(80);

        TableColumn<Ticket, String> colCreator = new TableColumn<>("Creator");
        colCreator.setCellValueFactory(new PropertyValueFactory<>("creatorUserName"));
        colCreator.setPrefWidth(150);

        tblTickets.getColumns().addAll(colId, colTitle, colStatus, colCreator);

        // Double-click open detail
        tblTickets.setRowFactory(tv -> {
            TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Ticket t = row.getItem();
                    ControllerTicketSystem.openTicketDetail(t);
                }
            });
            return row;
        });

        // Buttons
        Button btnCreate = new Button("Create Ticket");
        Button btnEdit   = new Button("Edit");
        Button btnDelete = new Button("Delete");
        Button btnClose  = new Button("Close");
        Button btnReopen = new Button("Reopen");
        Button btnDetail = new Button("View Detail");
        Button btnReturnHome = new Button("Return to Home"); 

        btnCreate.setOnAction(e -> ControllerTicketSystem.openTicketDetail(null));
        btnCreate.setOnAction(e -> guiTicketSystem.ViewCreateTicket.displayCreateTicket(
                ControllerTicketSystem.theStage, ControllerTicketSystem.theUser, null));

        btnEdit.setOnAction(e -> {
            Ticket selected = tblTickets.getSelectionModel().getSelectedItem();
            ControllerTicketSystem.editTicket(selected);
        });

        btnDelete.setOnAction(e -> {
            Ticket selected = tblTickets.getSelectionModel().getSelectedItem();
            ControllerTicketSystem.deleteTicket(selected);
        });

        btnClose.setOnAction(e -> {
            Ticket selected = tblTickets.getSelectionModel().getSelectedItem();
            ControllerTicketSystem.closeTicket(selected);
        });

        btnReopen.setOnAction(e -> {
            Ticket selected = tblTickets.getSelectionModel().getSelectedItem();
            ControllerTicketSystem.reopenTicket(selected);
        });

        btnDetail.setOnAction(e -> {
            Ticket selected = tblTickets.getSelectionModel().getSelectedItem();
            ControllerTicketSystem.openTicketDetail(selected);
        });

        btnReturnHome.setOnAction(e -> {                        
            ControllerTicketSystem.returnToHome();             
        });                                                     

        HBox bottomBox = new HBox(8,
                btnCreate, btnEdit, btnDelete, btnClose, btnReopen, btnDetail,
                btnReturnHome
        );
        bottomBox.setPadding(new Insets(10));

        // Reload on filter changes
        txtSearchTitle.textProperty().addListener((obs, o, n) -> reloadTable());
        cmbStatus.valueProperty().addListener((obs, o, n) -> reloadTable());
        chkMineOnly.selectedProperty().addListener((obs, o, n) -> reloadTable());

        root.setTop(topBox);
        root.setCenter(tblTickets);
        root.setBottom(bottomBox);

        theScene = new Scene(root, 800, 500);
    }

    /**********
     * <p>Method: reloadTable()</p>
     *
     * <p>Reloads the ticket table based on current filter selections:
     * keyword, status filter, and ownership filter.</p>
     */
    public void reloadTable() {
        String keyword = txtSearchTitle.getText();
        String sel = cmbStatus.getValue();
        String statusFilter = null;
        if ("Open".equalsIgnoreCase(sel))   statusFilter = "OPEN";
        else if ("Closed".equalsIgnoreCase(sel)) statusFilter = "CLOSED";

        boolean onlyMine = chkMineOnly.isSelected();

        List<Ticket> tickets = ControllerTicketSystem.loadTickets(keyword, statusFilter, onlyMine);
        tblTickets.getItems().clear();
        if (tickets != null) {
            tblTickets.getItems().addAll(tickets);
        }
    }
}
