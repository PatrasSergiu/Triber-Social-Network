package socialnetwork.service;

import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageService {
    private Repository<Long, Message> repoMessage;
    private Repository<Long,Utilizator> repoUtilizator;


    public MessageService(Repository<Long,Message> repo,Repository<Long,Utilizator> repo2)
    {
        this.repoMessage = repo;
        this.repoUtilizator = repo2;
    }

    public Iterable<Message> getAll()
    {
        return repoMessage.findAll();
    }

    public Message getMessageForUser(Long id)
    {
        return repoMessage.findOne(id);
    }

    public void sendMessage(Long idMesaj,Long idExpeditor,ArrayList<String>destinatari,String mesaj) {
        Utilizator exp = repoUtilizator.findOne(idExpeditor);

        List<Long> toSend = new ArrayList<>();
        for(String id : destinatari) {
            toSend.add(Long.parseLong(id));
        }

        List<Utilizator> listaDestinatari = new ArrayList<Utilizator>();

        for(Long i: toSend) {
            listaDestinatari.add(repoUtilizator.findOne(i));
        }

        Message message = new Message(exp,listaDestinatari,mesaj);
        message.setId(idMesaj);
        repoMessage.save(message);
    }

    public void replyAll(Long newId,Long idToReply, Long idReplying, String newMessage) {
        Message mesajInitial = repoMessage.findOne(idToReply);
        System.out.println(mesajInitial.getTo());

        Utilizator s = mesajInitial.getFrom();

        List<Utilizator> lista_destinatari = new ArrayList<Utilizator>();
        lista_destinatari.add(mesajInitial.getFrom());
        for(Utilizator i : mesajInitial.getTo())
        {
            if(! i.equals(repoUtilizator.findOne(idReplying)))
                lista_destinatari.add(i);
        }

        ReplyMessage reply = new ReplyMessage(repoUtilizator.findOne(idReplying),lista_destinatari,newMessage);
        reply.setId(newId);

        reply.setReply(mesajInitial);
        System.out.println(reply.getReply());

        repoMessage.save(reply);

    }



    public void replyFilter(Long newId,Long idToReply, Long idReplying, String newMessage,ArrayList<Long> sendTo) {
        Message mesajInitial = repoMessage.findOne(idToReply);

        Utilizator s = mesajInitial.getFrom();
        ArrayList<Utilizator> lista_dest = new ArrayList<Utilizator>();
        ArrayList<Utilizator> lista_din_mesaj  = (ArrayList<Utilizator>) mesajInitial.getTo();
        sendTo.forEach(x -> System.out.println(x));
        lista_din_mesaj.forEach(y -> System.out.println(y.getId()));
        for (Long id: sendTo)
            for(Utilizator user: lista_din_mesaj)
                if(user.getId() == id)
                    lista_dest.add(user);

        lista_dest.add(s);
        ReplyMessage reply = new ReplyMessage(repoUtilizator.findOne(idReplying),lista_dest,newMessage);
        reply.setId(newId);
        reply.setReply(mesajInitial);

        repoMessage.save(reply);
    }

    public List<Message> ChronologicMessage(Long id1,Long id2)
    {
        List<Message> mesaje = new ArrayList<Message>();

        repoMessage.findAll().forEach(x->
        {

            if(x.getFrom().getId()==id1 && x.getTo().contains(repoUtilizator.findOne(id2)))
                mesaje.add(x);
            else if(x.getFrom().getId() == id2 && x.getTo().contains(repoUtilizator.findOne(id1)))
                mesaje.add(x);

        });

        return mesaje.stream().sorted((m1,m2)->m1.getData().compareTo(m2.getData())).collect(Collectors.toList());


    }
    public Message getOne(Long id1) {
        return repoMessage.findOne(id1);
    }


    public List<Message> getAllRecieved (Utilizator u) {
        List<Message> mesaje = new ArrayList<Message>();

        repoMessage.findAll().forEach(x->
        {
            if(x.getTo().indexOf(u) != -1)
                mesaje.add(x);
        });

        return mesaje.stream().sorted((m1,m2)->m1.getData().compareTo(m2.getData())).collect(Collectors.toList());

    }


}
