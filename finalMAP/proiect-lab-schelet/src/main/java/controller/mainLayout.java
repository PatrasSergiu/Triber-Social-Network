package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.vm.ci.meta.Local;
import org.w3c.dom.Text;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.EventService;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public class mainLayout {

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Stage logInStage;

    userPageController ucl = new userPageController();
    Stage newAccountStage = new Stage();
    Stage userPageStage = new Stage();
    UtilizatorService userService;
    MessageService messageService;
    RequestService requestService;
    EventService eventService;


    public void setStage(Stage s) {
        this.logInStage = s;
    }

    public void setServices(UtilizatorService srv, MessageService msrv, RequestService rsrv, EventService esrv) {
        this.userService = srv;
        this.requestService = rsrv;
        this.messageService = msrv;
        this.eventService = esrv;
        deleteOldEvents();
    }

    public void guiLogin(MouseEvent mouseEvent) {

    }

    public void deleteOldEvents(){
        eventService.getAll().forEach(event -> {
            if(LocalDateTime.now().isAfter(event.getData())) {
                eventService.deleteEvent(event);
                eventService.deleteAllAbonati(event);
            }
        });
    }

    public void openUserPage(Utilizator user) throws Exception{
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/userPage.fxml"));
        Pane root;
        root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/views/user.css");
        userPageStage.setScene(scene);
        userPageStage.setTitle("Buna ziua " + user.getLastName() + " " + user.getFirstName());
        logInStage.close();
        userPageController cl = loader.getController();
        cl.setServices(userService, messageService, requestService, eventService);
        cl.setUsername(user.getUsername());
        cl.initStart();
        cl.setStage(userPageStage);
        userPageStage.show();
    }

    public String hash(String pass)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes(),0,pass.length());
            String z = new BigInteger(1,md.digest()).toString(16);
            System.out.println(z);
            return z;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public void logInMethod(ActionEvent actionEvent) throws Exception {
        String username, password;
        username = usernameTextField.getText();
        password = passwordTextField.getText();
        //System.out.println(username + "  " + password);
        if(username.isEmpty() || password.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Log in failed");
            errorAlert.setContentText("Campurile nu au voie sa fie goale.");
            errorAlert.showAndWait();
            usernameTextField.clear();
            passwordTextField.clear();
        }
        else {
            Utilizator user = userService.findByUsername(username);

            if (user.getPassword().equals(hash(password))) {
                openUserPage(user);
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Log in failed");
                errorAlert.setContentText("The username and password combination does not match.");
                errorAlert.showAndWait();
                usernameTextField.clear();
                passwordTextField.clear();
            }
        }

    }

    public void openNewAccountPage() {

    }

    public void newAccountMethod(MouseEvent mouseEvent) throws Exception {
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/newAccountPage.fxml"));
        Pane root;
        root = loader.load();

        newAccountStage.setScene(new Scene(root));
        newAccountStage.setTitle("Inregistrare");
        logInStage.close();
        newAccController cl = loader.getController();
        cl.setServices(userService, messageService, requestService, eventService);
        cl.setStage(newAccountStage);
        newAccountStage.show();


    }
}
