package socialnetwork.domain;

import java.time.LocalDateTime;

public class Event  extends Entity<Long> {

    private String descriere;
    private LocalDateTime data;


    public Event (String descriere, LocalDateTime data) {
        this.descriere = descriere;
        this.data = data;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}
