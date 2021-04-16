package socialnetwork.service;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RequestService {
    private Repository<Tuple<Long, Long>, FriendRequest> repoRequest;
    private Repository<Long, Utilizator> repoUtilizatori;
    private Repository<Tuple<Long,Long>, Prietenie> repoPrietenie;

    public RequestService(Repository<Tuple<Long, Long>, FriendRequest> repo1,Repository<Long,Utilizator> repo2,Repository<Tuple<Long,Long>,Prietenie> repo3) {
        this.repoRequest = repo1;
        this.repoUtilizatori = repo2;
        this.repoPrietenie = repo3;
    }
    public FriendRequest getOne(Tuple<Long,Long> id){return repoRequest.findOne(id);}
    public Iterable<FriendRequest> getAll(){
        return repoRequest.findAll();
    }

    public void deleteRequest(FriendRequest f) {
        repoRequest.delete(f.getId());
    }

    public void respondToRequest(Long id1, Long id2, Long answer) {

        Tuple tup = new Tuple(id2,id1);

        FriendRequest req = repoRequest.findOne( new Tuple(id1,id2));
        if(req.getStatus().equals("accepted"))
            throw new ValidationException("Deja ati acceptat aceasta cerere");
        if(answer == 1) {
            req.setStatus("accepted");
            repoRequest.update(req);

            Prietenie p = new Prietenie();
            p.setId(tup);
            repoPrietenie.save(p);
        }
        else {
            req.setStatus("declined");
            repoRequest.update(req);
        }

    }
    public List<FriendRequest> mainGet(Long id, Predicate<FriendRequest> filterMethod) {

        List<String> toPrint = null;
        List <FriendRequest> rez = new ArrayList<FriendRequest>();
        repoRequest.findAll().forEach(e -> {
            rez.add(e);
        });
        return rez.stream().filter(e ->
                e.getId().isPart(id)).filter(filterMethod).collect(Collectors.toList());

    }
    public List<FriendRequest> sentRequests(Long id) {
        Predicate<FriendRequest> fr = (e -> {
            return e.getId().getLeft() == id;
        });
        return mainGet(id, fr);
    }
    public List<FriendRequest> recievedRequests(Long id) {
        Predicate<FriendRequest> fr = (e -> {
            return e.getId().getRight() == id;
        });
        return mainGet(id, fr);
    }

    public void sendRequest(Long id1, Long id2) {
        Tuple t = new Tuple(id1,id2);
        Tuple t2 = new Tuple(id2,id1);

        Utilizator user1 = repoUtilizatori.findOne(id1);
        Utilizator user2 = repoUtilizatori.findOne(id2);

        if(user1 == null)
            throw new ValidationException("No user found with the first id.");
        if(user2 == null)
            throw new ValidationException("No user found with the second id.");

        if(user1 == user2)
            throw new ValidationException("You cannot add yourself to the friendlist");

        if(repoPrietenie.findOne(t) != null || repoPrietenie.findOne(t2) != null) {
            throw new ValidationException("You are already friends with this person.");
        }

        FriendRequest request = new FriendRequest(id1,id2);
        FriendRequest test;
        test = repoRequest.findOne(request.getId());
        if(repoRequest.findOne(request.getId()) == null && repoRequest.findOne(new Tuple(id2,id1)) == null) {
                repoRequest.save(request);
        }
        else {
            String s = request.getStatus().toString();
            if(s.equals("pending"))
                throw new ValidationException("You already sent a friend request to this person");
            if(s.equals("declined")) {
                request.setStatus("pending");
                repoRequest.update(request);
            }

        }


    }

}
