package socialnetwork.domain;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Prietenie() {
        date = LocalDateTime.now();
    }

    public Prietenie(LocalDateTime date) {
        this.date = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    public int getMonth() {
        return date.getMonthValue();
    }
}
