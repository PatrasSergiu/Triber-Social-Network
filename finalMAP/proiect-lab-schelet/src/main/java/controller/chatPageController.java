package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class chatPageController {

    private UtilizatorService userService;
    private MessageService messageService;
    private RequestService requestService;
    ObservableList<Utilizator> modelTable = FXCollections.observableArrayList();
    private String username;
    private Utilizator userLogat;
    private Stage chatPageStage;

    @FXML
    private Label userLabel;
    @FXML
    private TableView<Utilizator> friendsTableView;
    @FXML
    private TableColumn<Utilizator, String> columnUtilizatori;
    @FXML
    private TableColumn<Utilizator, String> columnPrieteni;

    public void setUsername(String user) {
        this.username = user;
        userLogat = userService.findByUsername(user);
    }
    public void setStage(Stage s) {
        this.chatPageStage = s;
    }

    public void setServices(UtilizatorService srv, MessageService msrv, RequestService rsrv) {
        this.userService = srv;
        this.messageService = msrv;
        this.requestService = rsrv;
    }
    public void initTable() {
        friendsTableView.setItems(modelTable);

        this.columnUtilizatori.setCellValueFactory(new PropertyValueFactory<Utilizator , String>("NumeComplet"));
        this.columnPrieteni.setCellValueFactory(new PropertyValueFactory<Utilizator , String>("Status"));

        userLabel.setText(userLogat.getNumeComplet());

        List<Utilizator> users = StreamSupport
                .stream(userService.getAll().spliterator(), false)
                .collect(Collectors.toList());

        List<Prietenie> prietenii = userService.getFriendList(userLogat.getId());

        int index = -1;

        for(Prietenie p : prietenii) {
            for (Utilizator user: users) {
                if (p.getId().getLeft().equals(user.getId()) || p.getId().getRight().equals(user.getId())) {
                    if(user.getId().equals(userLogat.getId())) {
                        user.setPrieten(false);
                        index = users.indexOf(user);
                    }
                    else
                        user.setPrieten(true);
                }
            }
        }
        if(index != -1)
            users.remove(index);
        modelTable.setAll(users);

    }


}
