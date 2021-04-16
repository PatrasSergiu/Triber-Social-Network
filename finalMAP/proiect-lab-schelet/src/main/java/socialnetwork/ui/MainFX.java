package socialnetwork.ui;


import controller.mainLayout;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.EventValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.service.EventService;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainFX extends Application {

    public mainLayout ml;

    private UtilizatorService userService;
    private MessageService messageService;
    private RequestService requestService;
    private EventService eventService;
    private Properties properties;

    public MainFX() {

    }

    public void setServices() {
       // String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        Validator<Event> EventVal = new EventValidator();
        //String fileName="data/users.csv";
       // String prieteniFileName = "data/prieteni.csv";
        try {
            properties = new Properties();
            try {
                properties.load(new FileReader("bd.config"));
            }
            catch(IOException e ){
                System.out.println("Cannot find bd.config " + e);
            }
        }catch(Exception e){
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setContentText("Error while starting app "+e);
            alert.showAndWait();
        }

        Repository<Long, Utilizator> UserDatabaseRepository = new UserDB(properties, new UtilizatorValidator());
        Repository<Tuple<Long,Long>, Prietenie> friendshipDatabaseRepository =
                new PrietenieDB(properties , new UtilizatorValidator());

        Repository<Tuple<Long, Long>, FriendRequest> friendRequestDatabaseRepository =
                new FriendRequestDB(properties);

        Repository<Long, Message> messageRepository =
                new MessageDB(properties);

        UserDatabaseRepository.findAll();

        EventsDB repoEvent = new EventsDB(properties, EventVal);

      //  Repository<Tuple<Long, Long>, Prietenie> prietenieRepository = new FriendshipFile(prieteniFileName, new PrietenieValidator());
        UtilizatorService srv = new UtilizatorService(UserDatabaseRepository, friendshipDatabaseRepository);
        MessageService msrv = new MessageService(messageRepository, UserDatabaseRepository);
        RequestService rsrv = new RequestService(friendRequestDatabaseRepository, UserDatabaseRepository, friendshipDatabaseRepository);
        EventService esrv = new EventService(repoEvent, EventVal);
        this.userService = srv;
        this.messageService = msrv;
        this.requestService = rsrv;
        this.eventService = esrv;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setServices();


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/mainLayout.fxml"));
        Pane root;
        root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Social Network");
        ml = loader.getController();
        ml.setServices(userService, messageService, requestService, eventService);
        ml.setStage(primaryStage);
        primaryStage.show();

    }

    public static void main(String[] args){
        launch();
    }

}
