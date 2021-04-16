package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController {

    private MessageService messageService;
    private UtilizatorService userService;
    private RequestService requestService;
    private Stage friendRequestStage;
    private Utilizator userLogat;

    @FXML
    private TableView <FriendRequest> recievedRequestsTable;
    @FXML
    private TableView <FriendRequest> sentRequestsTable;


    @FXML
    private Label userLabel;
    @FXML
    TableColumn columnUtilizatorSent;
    @FXML
    TableColumn columnDataSent;
    @FXML
    TableColumn columnStatusSent;
    @FXML
    TableColumn columnStatusRecieved;
    @FXML
    TableColumn columnDataRecieved;
    @FXML
    TableColumn columnUtilizatorRecieved;




    ObservableList<FriendRequest> modelTableRecieved = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelTableSent = FXCollections.observableArrayList();


    public void setServices(UtilizatorService srv, MessageService msrv, RequestService rsrv) {
        userService = srv;
        messageService = msrv;
        requestService = rsrv;
        if(userService == null)
            System.out.println("oof");
    }

    public void setUsername(String user) {
        userLogat = userService.findByUsername(user);
        initTable();
    }
    public void initTable() {

        //sent requests
        userLabel.setText(userLogat.getNumeComplet());

        List<FriendRequest> sentRequests = StreamSupport
                .stream(requestService.sentRequests(userLogat.getId()).spliterator(), false)
                .collect(Collectors.toList());

        sentRequestsTable.setItems(modelTableSent);
        this.columnUtilizatorSent.setCellValueFactory(new PropertyValueFactory<FriendRequest , String>("Recieved"));
        this.columnStatusSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("Status"));
        this.columnDataSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("DataString"));
        modelTableSent.setAll(sentRequests);
        //// recieved requests
        List<FriendRequest> recievedRequests = StreamSupport
                .stream(requestService.recievedRequests(userLogat.getId()).spliterator(), false)
                .collect(Collectors.toList());

        recievedRequestsTable.setItems(modelTableRecieved);
        this.columnUtilizatorRecieved.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("Requested"));
        this.columnStatusRecieved.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("Status"));
        this.columnDataRecieved.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("DataString"));
        modelTableRecieved.setAll(recievedRequests);


    }
    public void setStage(Stage s) {
        this.friendRequestStage = s;
    }

    public void retrageCerere(MouseEvent mouseEvent) {


        FriendRequest fr = sentRequestsTable.getSelectionModel().getSelectedItem();
        try {
            if(fr == null)
                throw new Exception("Nu a fost selectata nici o cerere");
            requestService.deleteRequest(fr);
            Alert succesAlert = new Alert(Alert.AlertType.CONFIRMATION);
            succesAlert.setHeaderText("Succes!");
            succesAlert.setContentText("Cerere a fost retrasa!");
            succesAlert.showAndWait();
        }
        catch(Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();

        }
        initTable();
    }
}
