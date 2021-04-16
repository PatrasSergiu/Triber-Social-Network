package socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long,Long>> {
    enum requestStatus {
        denied,
        pending,
        accepted
    }

    private String status ="pending";
    private LocalDateTime data;
    public FriendRequest(Long id1, Long id2) {
        setId( new Tuple(id1,id2));
        this.data = LocalDateTime.now();
        this.status = "pending";
    }


    public FriendRequest(Long id1, Long id2, String stat, LocalDateTime data) {
        setId( new Tuple(id1,id2));
        this.status = stat;
        this.data = data;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public Long getRequested(){return getId().getLeft();}
    public Long getRecieved(){return getId().getRight();}
    public LocalDateTime getData(){return this.data;}

    public String getDataString() {
        return this.data.toString();
    }

    public String getStatus() {
        return status;
    }
}
