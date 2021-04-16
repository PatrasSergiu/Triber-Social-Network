/*package socialnetwork.ui;

import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class UI {

    private UtilizatorService srv;
    private MessageService msrv;
    private RequestService rsrv;
    private Scanner scanner;

    public UI(UtilizatorService srv, MessageService srv2, RequestService srv3) {
        this.srv = srv;
        this.msrv = srv2;
        this.rsrv = srv3;
    }

    public void showMenu() {
        System.out.println("Press the corresponding number for the action you wish to perfom: ");
        System.out.println("1. Add user");
        System.out.println("2. Show all users");
        System.out.println("3. Delete user");
        System.out.println("4. Add friend");
        System.out.println("5. Delete Friendship");
        System.out.println("6. Print friend list");
        System.out.println("7. Number of communities");
        System.out.println("8. Most sociable community");
        System.out.println("9. Friends made in a month");
        System.out.println("10. Respond to friend request");
        System.out.println("11. Send a messsage");
        System.out.println("12. Reply to a message");
        System.out.println("13. Afisare cronologica");
        System.out.println("0. Exit");
    }

    public static boolean isInt(String id)
    {
        try{
            int i = Integer.parseInt(id);
        }catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }

    public Utilizator getUser(List<Utilizator> lista,Long id)
    {
        for(Utilizator u:lista)
        {
            if(u.getId() == id)
                return u;
        }
        return null;

    }

    public void AfisareCronologica()
    {
        System.out.println("id for user 1: ");
        String id1 = scanner.next();

        System.out.println("id for user 2: ");
        String id2 = scanner.next();


        List<Message> mesaje = msrv.ChronologicMessage(Long.parseLong(id1),Long.parseLong(id2));

        mesaje.forEach(x->
        {
            if(x.getFrom().getId() == Long.parseLong(id1))
                System.out.println(x.getFrom().getFirstName() +
                        " " + x.getFrom().getLastName() + " catre "
                        + getUser(x.getTo(),Long.parseLong(id2)).getFirstName() + " " +
                        getUser(x.getTo(),Long.parseLong(id2)).getLastName()
                        + "  MESAJ:  " + x.getMessage());


            else if(x.getFrom().getId() == Long.parseLong(id2))
                System.out.println(x.getFrom().getFirstName() +
                        " " + x.getFrom().getLastName() + " catre "
                        + getUser(x.getTo(),Long.parseLong(id1)).getFirstName() + " " +
                        getUser(x.getTo(),Long.parseLong(id1)).getLastName()
                        + "  MESAJ:  " + x.getMessage());

        });


    }

    public void replyToAMessage() {
        System.out.println("ID of the message you want to reply to: ");
        String idMesajInitial, idMesajNou, answer;

        idMesajInitial = scanner.next();

        System.out.println("ID mesaj raspuns: ");
        idMesajNou = scanner.next();

        System.out.println("ID persoana care raspunde: ");
        String id = scanner.next();

        System.out.println("1 reply to all, 2 reply to user");
        answer = scanner.next();
        int a = 0;
        if(isInt(answer))
            a = parseInt(answer);
        System.out.println("Raspuns: ");
        String mesaj = scanner.nextLine();
        mesaj = scanner.nextLine();
        if(a == 1) {
            //reply to all
            msrv.replyAll(Long.parseLong(idMesajNou), Long.parseLong(idMesajInitial), Long.parseLong(id), mesaj);
        }
        else {
            System.out.println("ID la care sa trimiti raspunsul: (0 pentru stop)");
            ArrayList<Long> to = new ArrayList<Long>();
            while(true) {
                String tempId = scanner.next();
                if (Long.parseLong(tempId) == 0)
                    break;
                else {
                    to.add(Long.parseLong(tempId));
                }
            }
           // System.out.println(to);
            msrv.replyFilter(Long.parseLong(idMesajNou), Long.parseLong(idMesajInitial), Long.parseLong(id), mesaj, to);
        }
        System.out.println("Replied succesfully.");

    }

    public void printUsers() {
        srv.getAll().forEach(System.out::println);
    }

    public void addUser() {
        String nume, prenume, id;

        System.out.println("Last Name: ");
        prenume = scanner.next();

        System.out.println("First Name: ");
        nume = scanner.next();

        System.out.println("ID: ");
        id = scanner.next();

        Utilizator u = new Utilizator(prenume,nume);
        u.setId(Long.parseLong(id));

        srv.addUtilizator(u);
        System.out.println("User added succesfully!");

    }

    public void deleteUser() {
        System.out.println("ID of user that you want to delete: ");
        String id;
        id = scanner.next();
        if(isInt(id)) {
            srv.deleteUser(Long.parseLong(id));
        }
        else
            throw new ValidationException("ID must be a positive non-null number");

    }

    public void addFriendship() {
        String id1, id2;
        System.out.println("ID of first friend: ");
        id1 = scanner.next();
        System.out.println("ID of second friend: ");
        id2 = scanner.next();
        if(isInt(id1) && isInt(id2)) {
            //srv.addFriendship(Long.parseLong(id1), Long.parseLong(id2));
            rsrv.sendRequest(Long.parseLong(id1), Long.parseLong(id2));
        }
        else {
            throw new ValidationException("IDs must be non-null positive numbers");
        }
        System.out.println("Friend added!");
    }

    public void deleteFriendship() {
        String id1, id2;
        System.out.println("ID of person that wants to delete a friend: ");
        id1 = scanner.next();
        System.out.println("ID of friend to be deleted: ");
        id2 = scanner.next();
        if(isInt(id1) && isInt(id2)) {
            srv.deleteFriendship(Long.parseLong(id1), Long.parseLong(id2));
        }
        else {
            throw new ValidationException("IDs must be non-null positive numbers");
        }
    System.out.println("Friend deleted!");
    }

    public void printFriendList() {
        System.out.println("ID of the person you want the see the friends of: ");
        String id;
        id = scanner.next();
        if(isInt(id)) {
            List<Prietenie> pr = new ArrayList<Prietenie>();
            Utilizator user;
            pr = srv.getFriendList(Long.parseLong(id));
            for(Prietenie prieten: pr) {
                if(prieten.getId().getRight() == Long.parseLong(id)) {
                    user = srv.getUser(prieten.getId().getLeft());
                }
                else {
                    user = srv.getUser(prieten.getId().getRight());
                }

                System.out.println(user.getLastName() + " | " + user.getFirstName() + " | " + prieten.getDate());

            }

        }
        else {
            throw new ValidationException("IDs must be positive non-null numbers");
        }
    }
    public void friendsByMonth() {
        System.out.println("ID of the person you want the see the friends of: ");
        String id, luna;
        id = scanner.next();
        System.out.println("Month: ");
        luna = scanner.next();
        if(isInt(id) && isInt(luna) && parseInt(luna) <= 12 && parseInt(luna) >= 1) {
            List<Prietenie> pr = new ArrayList<Prietenie>();
            Utilizator user;
            pr = srv.getFriendList(Long.parseLong(id));
            int afisat = 0;
            for (Prietenie prieten : pr) {
                if (prieten.getMonth() == parseInt(luna)) {
                    afisat = 1;
                    if (prieten.getId().getRight() == Long.parseLong(id)) {
                        user = srv.getUser(prieten.getId().getLeft());
                    } else {
                        user = srv.getUser(prieten.getId().getRight());
                    }

                    System.out.println(user.getLastName() + " | " + user.getFirstName() + " | " + prieten.getDate());

                }
            }
            if(afisat == 0)
                System.out.println("No friendships have been made in that month.");
        }
        else {
            throw new ValidationException("IDs and month must be integers.");
        }
    }

    public void numberOfCommunities() {
        System.out.print("Numar de comunitati: ");
        System.out.println(srv.numberOfCommunities());
    }

    public void mostSociableCommunity() {
        System.out.println("Cea mai sociabila comunitate: ");
        srv.mostSociableCommunity().forEach(e -> {
            System.out.println(e);
        });
    }

    public void sendAMessage() {
        String id, idFrom;
        ArrayList<String> to = new ArrayList<String>();
        String mesaj;
        LocalDateTime date;

        System.out.println("Introduceti ID mesaj: ");
        id = scanner.next();
        System.out.println("ID-ul care trimite mesaj: ");
        idFrom = scanner.next();
        System.out.println("Mesajul: ");
        mesaj = scanner.nextLine();
        mesaj = scanner.nextLine();
        System.out.println("Introduceti cate un ID la care sa fie trimis mesajul, urmat de 0: ");
        while(true) {
            String tempId = scanner.next();
            if(parseInt(tempId) == 0)
                break;
            else {
                to.add(tempId);
            }
        }
        msrv.sendMessage(Long.parseLong(id), Long.parseLong(idFrom), to, mesaj);

    }

    public void respondToRequest() {

        System.out.println("Your ID: ");
        String id1 = scanner.next();

        System.out.println("ID of person who sent request: ");
        String id2 = scanner.next();

        if(isInt(id1) && isInt(id2)) {
            //
            System.out.println("1 for accept, 2 for decline");
            String answer = scanner.next();
            rsrv.respondToRequest(Long.parseLong(id1), Long.parseLong(id2), Long.parseLong(answer));
        }
        else {
            throw new ValidationException("IDs must be non-null positive numbers");
        }

    }



    public void run() {
        scanner = new Scanner(System.in);
        while (true) {
            showMenu();
            int cmd;
            System.out.println("Introduceti comanda: ");
            cmd = scanner.nextInt();
            try {
                switch (cmd) {
                    case (1): {
                        addUser();
                        break;
                    }
                    case (2): {
                        printUsers();
                        break;
                    }
                    case (3): {
                        deleteUser();
                        break;
                    }
                    case (4): {
                        addFriendship();
                        break;
                    }
                    case (5): {
                        deleteFriendship();
                        break;
                    }
                    case (6): {
                        printFriendList();
                        break;
                    }
                    case(7): {
                        numberOfCommunities();
                        break;
                    }
                    case(8): {
                        mostSociableCommunity();
                        break;
                    }
                    case(9): {
                        friendsByMonth();
                        break;
                    }
                    case(10): {
                        respondToRequest();
                        break;
                    }
                    case(11): {
                        sendAMessage();
                        break;
                    }
                    case(12): {
                        replyToAMessage();
                        break;
                    }
                    case(13): {
                        AfisareCronologica();
                        break;
                    }
                    case (0): {
                        System.out.println("Sfarsitul executiei!");
                        return;
                    }
                    default: {
                        System.out.println("Coamnda nerecunoscuta!");
                        break;
                    }
                }
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            } catch (ValidationException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
*/