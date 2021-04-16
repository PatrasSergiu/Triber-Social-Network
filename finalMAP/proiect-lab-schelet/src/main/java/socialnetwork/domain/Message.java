package socialnetwork.domain;

import javafx.scene.control.Button;
import sun.nio.ch.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {


    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    LocalDateTime data;
    private Button button;

    public Message(Utilizator f,List<Utilizator>to, String m) {
        this.from = f;
        this.to = to;
        this.message = m;
        this.data = LocalDateTime.now();
    }

    public Message(Utilizator f, List<Utilizator> to, String m, Button btn) {
        this.from = f;
        this.to = to;
        this. message = m;
        this.data = LocalDateTime.now();
        this.button = btn;
    }

    public Message(){};


    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Long getReply() {
        return 0l;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getData() {
        return this.data;
    }


    @Override
    public String toString(){
        List<Long>lst=new ArrayList<Long>();
        for(Utilizator ut:to)
            lst.add(ut.getId());
        String msg=""+this.getId()+";"+from.getId()+";"+lst+";"+data+";"+message;

        return msg;
    }

}
