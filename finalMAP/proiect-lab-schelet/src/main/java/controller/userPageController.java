package controller;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventService;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;
import sun.nio.ch.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class userPageController {

    private UtilizatorService userSrv;
    private MessageService messageService;
    private RequestService requestService;
    private EventService eventService;

    ObservableList<Utilizator> modelTable = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelTableFr = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelTableRecieved = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelTableSent = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelTableChat = FXCollections.observableArrayList();
    ObservableList<Event> modelTableEvent = FXCollections.observableArrayList();

    private String username;
    private Utilizator userLogat;
    private Stage userPageStage;
    private Stage chatStage;
    private boolean areLoc = false;

    @FXML
    private ImageView btnSend;
    @FXML
    private Pagination chatPagination;
    @FXML
    private Pagination paginarePrieteni;
    @FXML
    private Pagination paginareUsers;
    @FXML
    private VBox vboxAbonare;
    @FXML
    private VBox homeVbox;
    @FXML
    private TextField descriereEveniment;
    @FXML
    private DatePicker dataEveniment;
    @FXML
    private DatePicker dataInceput;
    @FXML
    private DatePicker dataSfarsit;
    @FXML
    private TextField messageField;
    @FXML
    private TextField numeTextField;
    @FXML
    private TextField prenumeTextField;
    @FXML
    private TextField prietenRaport;
    @FXML
    private Label userLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TableView <Utilizator> friendsTableView;
    @FXML
    private TableView <Utilizator> usersTableView;
    @FXML
    private TableView <Utilizator> chatTableView;
    @FXML
    TableView <Event> eventTableView;
    @FXML
    private TableColumn<Event, String> columnDataEvent;
    @FXML
    private TableColumn<Event, String> columnDescriereEvent;
    @FXML
    private TableColumn<Utilizator, String> columnUtilizatori;
    @FXML
    private TableColumn<Utilizator, String> columnUtilizatori1;
    @FXML
    private TableColumn<Utilizator, String> columnPrieteni;
    @FXML
    private TableView <FriendRequest> recievedRequestsTable;
    @FXML
    private TableView <FriendRequest> sentRequestsTable;

    @FXML
    private VBox chatVBox;

    @FXML
    TableColumn <FriendRequest, String> columnUtilizatorSent;
    @FXML
    TableColumn columnDataSent;
    @FXML
    TableColumn columnStatusSent;
    @FXML
    TableColumn columnStatusRecieved;
    @FXML
    TableColumn columnDataRecieved;
    @FXML
    TableColumn <FriendRequest, String> columnUtilizatorRecieved;
    @FXML
    TableColumn columnUtilizatorChat;
    @FXML
    ScrollPane textArea;

    public String getChatMessage() {
        return this.messageField.getText();
    }



    public void setUsername(String user) {
        this.username = user;
        userLogat = userSrv.findByUsername(user);
        userLabel.setText(this.userLogat.getNumeComplet());
    }
    public void setStage(Stage s) {
        this.userPageStage = s;
    }

    public UtilizatorService getUserSrv() {
        return userSrv;
    }
    public MessageService getMessageService () {
        return messageService;
    }

    public Utilizator getUserLogat () {
        return userLogat;
    }

    public void setServices(UtilizatorService srv, MessageService msrv, RequestService rsrv, EventService esrv) {
        this.userSrv = srv;
        this.messageService = msrv;
        this.requestService = rsrv;
        this.eventService = esrv;
    }

    public userPageController(){

    }



    public void initStart() {
        // to do
        // reply
        numeTextField.setOnAction(e-> {
            String search = numeTextField.getText();
            List<Utilizator> users = StreamSupport
                    .stream(userSrv.getAll().spliterator(), false)
                    .collect(Collectors.toList());
            users.remove(userSrv.getUser(userLogat.getId()));

            List<Utilizator> rez = new ArrayList<>();
            users.forEach(u -> {
                if(u.getNumeComplet().toLowerCase().contains(search.toLowerCase()))
                    rez.add(u);
            });

            modelTable.setAll(rez);

        });
        initHome();
        initTable();
        initChat();
        initTabeleCereri();
        initEventTable();
        initAbonari();
    }

    public void initHome() {
        welcomeLabel.setText("Bine ai venit, " + userLogat.getLastName() + " " + userLogat.getFirstName());
        homeVbox.getChildren().clear();
        List<Long> abonari = eventService.getAllAbonari(userLogat);
        areLoc = false;
            abonari.forEach(ab -> {
                Event e = eventService.getOne(ab);
                Month m1,m2;
                m1 = e.getData().getMonth();
                m2 = LocalDateTime.now().getMonth();
                if(e.getData().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()){
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/views/news.fxml"));
                        AnchorPane root  = (AnchorPane) loader.load();

                        // stage
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Edit Message");
                        dialogStage.initModality(Modality.APPLICATION_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        newsController nc = loader.getController();
                        nc.setLabel(e.getDescriere() + " are loc in data: " + e.getData());
                        homeVbox.getChildren().add(root);
                        areLoc = true;
                    }
                    catch(Exception exception){
                        exception.printStackTrace();
                    }
                }
            });
            if(areLoc == false) {
                Label noNews = new Label("Nu aveti nici un eveniment astazi!");
                noNews.setAlignment(Pos.BASELINE_CENTER);
                noNews.setFont(new Font("Century",18));
                homeVbox.setAlignment(Pos.BASELINE_CENTER);
                homeVbox.getChildren().addAll(noNews);
            }
        }


    public void initAbonari() {
        vboxAbonare.getChildren().clear();
        eventService.getAllAbonari(userLogat).forEach(x ->{
            Event event = eventService.getOne(x);
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/news.fxml"));
                AnchorPane root  = (AnchorPane) loader.load();

                // stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Message");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                newsController nc = loader.getController();
                nc.setLabel(event.getDescriere() + " are loc in data: " + event.getData());
                vboxAbonare.getChildren().add(root);
            }
            catch(Exception exception){
                exception.printStackTrace();
            }
        });
    }

    public void initEventTable() {

        List<Event> events = StreamSupport
                .stream(eventService.getAll().spliterator(), false)
                .collect(Collectors.toList());

        eventTableView.setItems(modelTableEvent);
        this.columnDescriereEvent.setCellValueFactory(new PropertyValueFactory<Event, String>("Descriere"));
        this.columnDataEvent.setCellValueFactory(new PropertyValueFactory<Event, String>("Data"));
        modelTableEvent.setAll(events);
    }

    public void initTabeleCereri() {

        List<FriendRequest> sentRequests = StreamSupport
                .stream(requestService.sentRequests(userLogat.getId()).spliterator(), false)
                .collect(Collectors.toList());

        sentRequestsTable.setItems(modelTableSent);
        this.columnUtilizatorSent.setCellValueFactory(c -> new SimpleStringProperty(userSrv.getUser(c.getValue().getRecieved()).getNumeComplet()));
        //this.columnUtilizatorSent.setCellValueFactory(new PropertyValueFactory<FriendRequest , String>("Recieved"));
        this.columnStatusSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("Status"));
        this.columnDataSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("DataString"));
        modelTableSent.setAll(sentRequests);
        //// recieved requests
        List<FriendRequest> recievedRequests = StreamSupport
                .stream(requestService.recievedRequests(userLogat.getId()).spliterator(), false)
                .collect(Collectors.toList());

        recievedRequestsTable.setItems(modelTableRecieved);
        this.columnUtilizatorRecieved.setCellValueFactory(c -> new SimpleStringProperty(userSrv.getUser(c.getValue().getRequested()).getNumeComplet()));
        this.columnStatusRecieved.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("Status"));
        this.columnDataRecieved.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("DataString"));
        modelTableRecieved.setAll(recievedRequests);
    }

    public void printMessage() {
        Long id1, id2;
        id1 = userLogat.getId();
        Utilizator u = (Utilizator)chatTableView.getSelectionModel().getSelectedItem();
        id2 = u.getId();
        chatVBox.getChildren().clear();
        messageService.ChronologicMessage(id1,id2).stream().forEach(x -> {
            FXMLLoader loader = new FXMLLoader();
            //  if(x.getReply() == null)
            loader.setLocation(getClass().getResource("/views/sentMessage.fxml"));
            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(getClass().getResource("/views/recievedMessage.fxml"));
            if(x.getFrom().getId() == id1) {
                try {

                    AnchorPane root  = (AnchorPane) loader.load();

                    // stage
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Message");
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    sentMessageController sc = loader.getController();
                    sc.setParent(this);
                    sc.setIdLabel(x.getId());
                    sc.setSentLabel(x.getMessage());

                    chatVBox.getChildren().add(root);
                }
                catch(Exception exception){
                    exception.printStackTrace();
                }
            }
            else {
                try {

                    AnchorPane root  = (AnchorPane) loader2.load();

                    // stage
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Message");
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    sentMessageController sc = loader2.getController();
                    sc.setParent(this);
                    sc.setIdLabel(x.getId());
                    sc.setRecievedLabel(x.getMessage());

                    chatVBox.getChildren().add(root);
                }
                catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });
        textArea.setVvalue(textArea.getVmax());
    }

    public void initChat() {
            chatTableView.setItems(modelTableChat);
            List<Utilizator> users = StreamSupport
                .stream(userSrv.getAll().spliterator(), false)
                .collect(Collectors.toList());

            users.remove(userSrv.getUser(userLogat.getId()));

        chatPagination.setPageCount(users.size()/13 +1);
        chatPagination.setPageFactory((i)->{
            int st = i*12;
            int dr = st+11;
            dr = Math.min(dr, users.size()-1);
            List<Utilizator> rez = new ArrayList<>();
            for(int x=st; x<=dr; x++){
                rez.add(users.get(x));
            }
            modelTableChat.setAll(rez);
            return new Pane();
        });

            //


        this.columnUtilizatorChat.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("NumeComplet"));

        chatTableView.getSelectionModel().selectedItemProperty().addListener(c ->
            printMessage());
            modelTableChat.setAll(users);
    }

    public void initTable() {
        usersTableView.setItems(modelTable);
        friendsTableView.setItems(modelTableFr);

        this.columnUtilizatori.setCellValueFactory(new PropertyValueFactory<Utilizator , String>("NumeComplet"));
        this.columnUtilizatori1.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("NumeComplet"));
        /*List<Utilizator> users = StreamSupport
                .stream(userSrv.getAll().spliterator(), false)
                .collect(Collectors.toList());
        users.remove(userSrv.getUser(userLogat.getId()));

        */
        List<Utilizator> users = userSrv.getAllNoFriends(userLogat);

        paginareUsers.setPageCount(users.size()/12 +1);
        paginareUsers.setPageFactory((i)->{
            int st = i*11;
            int dr = st+10;
            dr = Math.min(dr, users.size()-1);
            List<Utilizator> rez = new ArrayList<>();
            for(int x=st; x<=dr; x++){
                rez.add(users.get(x));
            }
            modelTable.setAll(rez);
            return new Pane();
            });
        //rpieteni
        List<Utilizator> prieteni = new ArrayList<>();
        userSrv.getFriendList(userLogat.getId()).forEach(x -> {
                if(x.getId().getLeft() != userLogat.getId())
                    prieteni.add(userSrv.getUser(x.getId().getLeft()));
                else
                    prieteni.add(userSrv.getUser(x.getId().getRight()));
        });
        paginarePrieteni.setPageCount(prieteni.size()/12 +1);
        paginarePrieteni.setPageFactory((i)->{
            int st = i*11;
            int dr = st+10;
            dr = Math.min(dr, prieteni.size()-1);
            List<Utilizator> rez = new ArrayList<>();
            for(int x=st; x<=dr; x++){
                rez.add(prieteni.get(x));
            }
            modelTableFr.setAll(rez);
            return new Pane();
        });


        //modelTable.setAll(users);
        ///tabel principal
        //sent requests
    }


    public void logOutMethod(ActionEvent actionEvent) throws Exception {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/mainLayout.fxml"));
        Pane root;
        root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Social Network");
        mainLayout ml = new mainLayout();
        ml = loader.getController();
        ml.setServices(userSrv, messageService, requestService, eventService);
        ml.setStage(primaryStage);
        userPageStage.close();
        primaryStage.show();

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
        initTabeleCereri();
    }

    public void showFriendRequests(ActionEvent actionEvent) throws Exception {

        Stage friendRequestStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/friendRequestsPage.fxml"));
        Pane root;
        root = loader.load();

        friendRequestStage.setScene(new Scene(root));
        friendRequestStage.setTitle("Cereri de prietenie");
        FriendRequestsController fr = new FriendRequestsController();
        fr = loader.getController();
        if(userSrv == null) {
            System.out.println("oof");
        }
        fr.setServices(userSrv, messageService, requestService);
        fr.setUsername(userLogat.getUsername());
        fr.setStage(friendRequestStage);
        userPageStage.close();
        friendRequestStage.show();


    }
    public void openChat(ActionEvent actionEvent) throws Exception {

        chatStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/chatPage.fxml"));
        Pane rootChat;
        rootChat = loader.load();

        chatStage.setScene(new Scene(rootChat));
        chatStage.setTitle("Cereri de prietenie");
        chatPageController cp = new chatPageController();
        cp = loader.getController();
        cp.setServices(userSrv, messageService, requestService);
        cp.setUsername(userLogat.getUsername());
        cp.setStage(chatStage);
        chatStage.show();


    }
    public void addFriendByName(ActionEvent actionEvent) {
        String nume = numeTextField.getText();
        String prenume = prenumeTextField.getText();
        try {
            Utilizator user = userSrv.findByName(nume, prenume);
            if (user == null) {
                throw new ValidationException("Nu a fost gasita nici o persoana cu acel nume");
            }
            userSrv.addFriendship(userLogat.getId(), user.getId());

            Alert succesAlert = new Alert(Alert.AlertType.CONFIRMATION);
            succesAlert.setHeaderText("Succes!");
            succesAlert.setContentText("Cerere trimisa cu succes!");
            succesAlert.showAndWait();
        }
        catch(Exception e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
        initTabeleCereri();
    }

    public void addFriend(ActionEvent actionEvent) {

        Utilizator user = usersTableView.getSelectionModel().getSelectedItem();
        try {
            requestService.sendRequest(userLogat.getId(), user.getId());
            Alert succesAlert = new Alert(Alert.AlertType.CONFIRMATION);
            succesAlert.setHeaderText("Succes!");
            succesAlert.setContentText("Cerere trimisa cu succes!");
            succesAlert.showAndWait();
            initTable();
            initTabeleCereri();
        }
        catch(Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
        initTabeleCereri();
    }

    public void deleteFriend(MouseEvent mouseEvent) {
        Utilizator user = friendsTableView.getSelectionModel().getSelectedItem();
        try {
            userSrv.deleteFriendship(user.getId(), userLogat.getId());
                try {
                    requestService.deleteRequest(new FriendRequest(userLogat.getId(), user.getId()));
                    requestService.deleteRequest(new FriendRequest(user.getId(), userLogat.getId()));
                }
                catch(Exception exception) {

                }
            Alert succesAlert = new Alert(Alert.AlertType.CONFIRMATION);
            succesAlert.setHeaderText("Succes!");
            succesAlert.setContentText("Prietenul a fost sters!");
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

    public Long getFirstFreeMsgId() {
        List<Message> users = StreamSupport
                .stream(messageService.getAll().spliterator(), false)
                .collect(Collectors.toList());
        Collections.sort(users, new Comparator<Message>() {
            public int compare(Message m1, Message m2) {
                if(m1.getId() < m2.getId())
                    return 1;
                else
                    return -1;
            }
        });
        if(!users.isEmpty())
            return users.get(0).getId() +1;
        else
            return 1L;
    }

    public void sendMessage(MouseEvent mouseEvent) {
        Long idMesaj = getFirstFreeMsgId();
        Long idExpeditor = userLogat.getId();
        String mesaj = messageField.getText();
        if(mesaj.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Mesajul nu poate fi gol!");
            errorAlert.showAndWait();
            return;
        }
        Utilizator u = chatTableView.getSelectionModel().getSelectedItem();
        ArrayList<String> destinatari = new ArrayList<String>();
        destinatari.add(u.getId().toString());
        messageService.sendMessage(idMesaj, idExpeditor, destinatari, mesaj);
        printMessage();
        messageField.clear();

    }
    public Long getFirstFreeEventId() {
        List<Event> events = StreamSupport
                .stream(eventService.getAll().spliterator(), false)
                .collect(Collectors.toList());
        Collections.sort(events, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                if(e1.getId() < e2.getId())
                    return 1;
                else
                    return -1;
            }
        });
        if(events.isEmpty())
            return 1L;
        return events.get(0).getId() +1;
    }

    public void createEventMethod(ActionEvent actionEvent) {

        String descriere = descriereEveniment.getText();
        if(descriere.isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Descrierea evenimentului nu are voie sa fie goala");
            errorAlert.showAndWait();
            return;
        }
        LocalDate data2 = dataEveniment.getValue();
        LocalDateTime data = LocalDateTime.of(data2.getYear(), data2.getMonth(), data2.getDayOfMonth(), 20, 00, 00);
        Event event = new Event(descriere, data);
        event.setId(getFirstFreeEventId());
        try {
            eventService.createEvent(event);
            initEventTable();
        }
        catch(Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            return;
        }
    }

    public void abonareEvent(ActionEvent actionEvent) {
        Event e = eventTableView.getSelectionModel().getSelectedItem();
        if(e == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Trebuie sa selectati un eveniment.");
            errorAlert.showAndWait();
            return;
        }

        Label a = new Label(e.getDescriere());
        try {
            eventService.addAbonat(e, userLogat);
            initAbonari();
            initHome();
        }
        catch(Exception exception) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(exception.getMessage());
            errorAlert.showAndWait();
            return;
        }
    }

    public void dezabonareEvent(ActionEvent actionEvent) {
        Event e = eventTableView.getSelectionModel().getSelectedItem();
        if(e == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Trebuie sa selectati un eveniment.");
            errorAlert.showAndWait();
            return;
        }
        try {
            eventService.deleteAbonat(e, userLogat);
            initAbonari();
            initHome();
        }
        catch(Exception exception) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(exception.getMessage());
            errorAlert.showAndWait();
            return;
        }

    }

    public void acceptRequest(ActionEvent actionEvent) {

        FriendRequest fr = recievedRequestsTable.getSelectionModel().getSelectedItem();
        if(fr == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Trebuie sa selectati o cerere");
            errorAlert.showAndWait();
            return;
        }
        else {
            try {
                requestService.respondToRequest(fr.getRequested(), fr.getRecieved(), 1L);
                initTable();
                initTabeleCereri();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("e bai");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
                return;
            }
        }
    }

    public void declineRequest(ActionEvent actionEvent) {
        FriendRequest fr = recievedRequestsTable.getSelectionModel().getSelectedItem();
        if(fr == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText("Trebuie sa selectati o cerere");
            errorAlert.showAndWait();
            return;
        }
        else {
            try {
                requestService.respondToRequest(fr.getRequested(), fr.getRecieved(), 0L);
                initTabeleCereri();
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("e bai");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
                return;
            }
        }


    }

    public void chatNextPage(MouseEvent mouseEvent) {

        int page = this.chatPagination.getCurrentPageIndex();
        initChat();


    }

    public void hoverSend(MouseDragEvent mouseDragEvent) {


    }

    public void normalSend(MouseDragEvent mouseDragEvent) {
    }



    public void pdfMesaje(MouseEvent mouseEvent) {

            String nume = prietenRaport.getText();
            try {
                if(nume.isEmpty())
                    throw new ValidationException("Campul 'Nume prieten' nu poate fi gol.");
                LocalDate d1,d2;
                d1 = dataInceput.getValue();
                d2 = dataSfarsit.getValue();
                if(d1.isAfter(d2))
                    throw new ValidationException("Interval data invalid.");

                com.itextpdf.text.Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("pdfMesaje.pdf"));
                String numeImpartit[] = nume.trim().split("\\s+");
                Utilizator userPrieten = userSrv.findByName(numeImpartit[1],numeImpartit[0]);
                document.open();

                Paragraph paragraph = new Paragraph("-------Mesaje intre "+ userLogat.getNumeComplet()
                        + " si "+ userPrieten.getNumeComplet() + " in perioda "+ d1.getDayOfMonth() + "/"+d1.getMonthValue() + "/" +d1.getYear()
                            + " --- " + d2.getDayOfMonth() + "/" + d2.getMonthValue() + "/" + d2.getYear());
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(3);
                table.addCell("Nume");
                table.addCell("Mesaj");
                table.addCell("Data");
                List<Message> mesaje = messageService.ChronologicMessage(userLogat.getId(), userPrieten.getId());
                for(Message y: mesaje) {

                    if(y.getData().isAfter(d1.atStartOfDay()) && d2.atTime(23, 59).isAfter(y.getData())) {
                        table.addCell(y.getFrom().getNumeComplet());
                        table.addCell(y.getMessage().toString());
                        table.addCell(y.getData().toString());
                    }
                };
                document.add(table);

                document.close();
                prietenRaport.clear();

            }

            catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }

            catch(Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("e bai");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
                return;
            }



    }

    public void activitate(MouseEvent mouseEvent) {

        try {
            LocalDate d1,d2;
            d1 = dataInceput.getValue();
            d2 = dataSfarsit.getValue();
            if(d1.isAfter(d2))
                throw new ValidationException("Interval data invalid.");

            com.itextpdf.text.Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("pdfActivitate.pdf"));
            document.open();
            Paragraph paragraph = new Paragraph("-------------\tPrietenii noi create in perioada "+ + d1.getDayOfMonth() + "/"+d1.getMonthValue() + "/" +d1.getYear()
                    + " --- " + d2.getDayOfMonth() + "/" + d2.getMonthValue() + "/" + d2.getYear());
            document.add(paragraph);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            table.addCell("Nume");
            table.addCell("Nume");
            table.addCell("Data");
            List<Prietenie> prieteni = userSrv.getFriendList(userLogat.getId());
            for(Prietenie pr : prieteni) {

                if(pr.getDate().isAfter(d1.atStartOfDay()) && d2.atTime(23, 59).isAfter(pr.getDate())) {
                    table.addCell(userSrv.getUser(pr.getId().getLeft()).getNumeComplet());
                    table.addCell(userSrv.getUser(pr.getId().getRight()).getNumeComplet());
                    table.addCell(pr.getDate().toString());
                }
            };
            document.add(table);


            ///////////////////////////////////////////////////////// mesaje
            PdfPTable table2 = new PdfPTable(3);
            table2.addCell("Nume");
            table2.addCell("Mesaj");
            table2.addCell("Data");
            List<Message> mesaje = messageService.getAllRecieved(userLogat);
            for(Message m : mesaje) {

                if(m.getData().isAfter(d1.atStartOfDay()) && d2.atTime(23, 59).isAfter(m.getData())) {
                    table2.addCell(m.getFrom().getNumeComplet());
                    table2.addCell(m.getMessage());
                    table2.addCell(m.getData().toString());
                }
            };

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            paragraph = new Paragraph("-------------\tMesaje primite in perioada "+ + d1.getDayOfMonth() + "/"+d1.getMonthValue() + "/" +d1.getYear()
                    + " --- " + d2.getDayOfMonth() + "/" + d2.getMonthValue() + "/" + d2.getYear());
            document.add(paragraph);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(table2);

            document.close();

        }
        catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }

        catch(Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("e bai");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            return;
        }
    }
}
