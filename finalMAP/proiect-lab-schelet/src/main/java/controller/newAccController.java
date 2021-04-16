package controller;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.EventService;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;
import sun.security.util.Password;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class newAccController {

    private Stage newAccStage;
    private UtilizatorService userSrv;
    private MessageService messageService;
    private RequestService requestService;
    private EventService eventService;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField numeTextField;
    @FXML
    private TextField prenumeTextField;
    @FXML
    private PasswordField confPasswordTextField;

    public void setStage(Stage s) {
        this.newAccStage = s;
    }
    public void setServices(UtilizatorService s, MessageService msrv, RequestService rsrv, EventService esrv) {
        this.userSrv = s;
        this.messageService = msrv;
        this. requestService = rsrv;
        this.eventService = esrv;
    }


    public Long getFirstFreeId() {
        List<Utilizator> users = StreamSupport
                .stream(userSrv.getAll().spliterator(), false)
                .collect(Collectors.toList());
        Collections.sort(users, new Comparator<Utilizator>() {
            public int compare(Utilizator utilizator, Utilizator t1) {
                if(utilizator.getId() < t1.getId())
                    return 1;
                else
                    return -1;
            }
        });
    return users.get(0).getId() +1;

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


    public void addNewUser(ActionEvent actionEvent) {
        String username, password, nume,prenume, confPassword;
        username = usernameTextField.getText();
        password = passwordTextField.getText();
        nume = numeTextField.getText();
        prenume = prenumeTextField.getText();
        confPassword = confPasswordTextField.getText();

        if(username.isEmpty() || password.isEmpty() || confPassword.isEmpty() || nume.isEmpty() || prenume.isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("oof");
            errorAlert.setHeaderText("Inregistrare esuata");
            errorAlert.setContentText("Campurile nu au voie sa fie goale.");
            errorAlert.showAndWait();
            usernameTextField.clear();
            passwordTextField.clear();
            numeTextField.clear();
            prenumeTextField.clear();
            confPasswordTextField.clear();
            return;
        }
        if(!password.equals(confPassword)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("oof");
            errorAlert.setHeaderText("Inregistrare esuata");
            errorAlert.setContentText("Parolele nu coincid");
            errorAlert.showAndWait();
            usernameTextField.clear();
            passwordTextField.clear();
            numeTextField.clear();
            prenumeTextField.clear();
            confPasswordTextField.clear();
            return;
        }
        try {
            Utilizator user = new Utilizator(prenume, nume);
            user.setUserPass(username, hash(password));
            user.setId(getFirstFreeId());
            System.out.println(user.getPassword() + " " + user.getUsername());
            userSrv.addUtilizator(user);
            Alert errorAlert = new Alert(Alert.AlertType.CONFIRMATION);
            errorAlert.setTitle("yes");
            errorAlert.setHeaderText("Succes");
            errorAlert.setContentText("Inregistrarea a avut loc cu succes!");
            errorAlert.showAndWait();
            newAccStage.close();
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/mainLayout.fxml"));
            Pane root;
            Stage primaryStage = new Stage();
            root = loader.load();

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Social Network");
            mainLayout ml = loader.getController();
            ml.setServices(userSrv, messageService, requestService,eventService);
            ml.setStage(primaryStage);
            primaryStage.show();

        }
        catch(Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("oof");
            errorAlert.setHeaderText("Inregistrare esuata");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            usernameTextField.clear();
            passwordTextField.clear();
            numeTextField.clear();
            prenumeTextField.clear();

            return;
        }
    }

    public void backToLogin(MouseEvent mouseEvent) throws Exception{

        newAccStage.close();
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/mainLayout.fxml"));
        Pane root;
        Stage primaryStage = new Stage();
        root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Social Network");
        mainLayout ml = loader.getController();
        ml.setServices(userSrv, messageService, requestService,eventService);
        ml.setStage(primaryStage);
        primaryStage.show();

    }
}
