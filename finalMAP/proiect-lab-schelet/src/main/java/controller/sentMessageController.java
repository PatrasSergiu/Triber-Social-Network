package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.awt.*;

import static java.lang.Math.min;

public class sentMessageController {
    
    private boolean replying = false;
    private String reply = "";
    private userPageController parent;

    @FXML
    private Label sentLabel;
    @FXML
    private Label recievedLabel;
    @FXML
    private Label replyLabel;
    @FXML
    private Button replyBtn;

    public void initialise() {

    }

    public Button getReplyBtn() {
        return replyBtn;
    }

    public Label getReplyLabel() {
        return replyLabel;
    }

    public void setReplyLabel (String s ){
        this.replyLabel.setText(s);
    }
    public String getReplyLabelText() {
        return this.replyLabel.getText();
    }

    public void setSentLabel(String s) {
        sentLabel.setText(s);

    }

    public void setRecievedLabel(String s) {
        this.recievedLabel.setText(s);
    }



    public void replyToMessage(MouseEvent mouseEvent) {
        Long id = parent.getFirstFreeMsgId();
        Long idReply = getIdLabel();
        Long idWhoReplies = parent.getUserLogat().getId();
        String mesajInitial = parent.getMessageService().getOne(idReply).getMessage();
        String mesaj = "Replying to(" + mesajInitial.toString().substring(0,min(5, mesajInitial.length())) + ") " + parent.getChatMessage();
        System.out.println(id);
        System.out.println(idReply);
        System.out.println(idWhoReplies);
        System.out.println(mesaj);
        parent.getMessageService().replyAll(id, idReply, idWhoReplies, mesaj);
        parent.printMessage();
    }

    public void setParent(userPageController userPageController) {

        this.parent = userPageController;

    }

    public void setIdLabel(Long id) {

        this.replyLabel.setText(id.toString());

    }

    public Long getIdLabel() {
        return Long.parseLong(this.replyLabel.getText());
    }
}
