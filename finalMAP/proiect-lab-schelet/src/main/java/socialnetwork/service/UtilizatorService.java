package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.*;
import sun.nio.ch.Util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService  {
    private Repository<Long, Utilizator> repo;
    private Repository<Tuple<Long, Long>, Prietenie> repoPrietenie;
    private HashSet<Utilizator> hm;
    private int nrComunitati = 0;
    private Map<Tuple<Long,Long>, Boolean> community;
    private HashSet<Tuple<Long, Long>> sociabili;
    private int page;
    private int size;
    private Pageable pagina;

    public UtilizatorService(Repository<Long, Utilizator> repo, Repository<Tuple<Long,Long>, Prietenie> repo2) {
        this.repo = repo;
        this.repoPrietenie = repo2;
        community = new HashMap<Tuple<Long,Long>, Boolean>();
        sociabili = new HashSet<Tuple<Long, Long>>();
        hm = new HashSet<Utilizator>();
        addAllFriends();
    }

    public Set<Utilizator> getNextUser(int page){
        this.page = page;
        Pageable pageable = new PageableClass(page, this.size);
        Paginator<Utilizator> paginator = new Paginator<>(pageable,this.getAll());
        Page<Utilizator> userPage = paginator.paginate();
        return userPage.getContent().collect(Collectors.toSet());
    }

    public void setPageSize(int size) {
        this.size = size;
    }

    public List<Utilizator> getFriends(Long id1) {
       return null;
    }


    public void addAllFriends() {
        repoPrietenie.findAll().forEach(x -> {

            Utilizator user1 = repo.findOne(x.getId().getLeft());
            Utilizator user2 = repo.findOne(x.getId().getRight());

            user1.addFriend(user2);
            user2.addFriend(user1);

        });
    }

    public Utilizator addUtilizator(Utilizator messageTask) {
        Utilizator u1,u2,u3;
        u1 = repo.findOne(messageTask.getId());
        u2 = repo.findByName(messageTask.getLastName(), messageTask.getFirstName());
        u3 = repo.findByUsername(messageTask.getUsername());
        if(u1 != null || u2 != null || u3 != null)
            throw new ValidationException("Exista deja un utilizator cu aceste date!");


        Utilizator task = repo.save(messageTask);
        return task;
    }

    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }
    ///TO DO: add other methods
    public void deleteUser(Long id) {
        ArrayList<Tuple<Long, Long>> ar = new ArrayList<Tuple<Long, Long>>();
        repoPrietenie.findAll().forEach(e ->{
            if(e.getId().getLeft() == id || e.getId().getRight() == id) {
                ar.add(e.getId());
            }
        });

    for(Tuple<Long, Long> e: ar) {
        this.deleteFriendship(e.getLeft(), e.getRight());
    }

        repo.delete(id);
        System.out.println("Utilizatorul a fost sters cu succes!");
    }

    public List<Prietenie> mainGet(Long id, Predicate<Prietenie> filterMethod) {

        Utilizator user = repo.findOne(id);
        if(user == null){
            throw new ValidationException("ID is not in the list.");
        }
        List<String> toPrint = null;
        List <Prietenie> rez = new ArrayList<Prietenie>();
        repoPrietenie.findAll().forEach(e -> {
            rez.add(e);
        });
        return rez.stream().filter(e ->
            e.getId().isPart(user.getId())).filter(filterMethod).collect(Collectors.toList());

    }

    public Utilizator getUser(Long id) {
        return repo.findOne(id);
    }


    public List<Utilizator> getAllNoFriends(Utilizator u) {
        List<Utilizator> users = StreamSupport
                .stream(this.getAll().spliterator(), false)
                .collect(Collectors.toList());
        users.remove(u);
        List<Utilizator> prieteni = new ArrayList<Utilizator>();
        for(Utilizator x : users) {
                if (repoPrietenie.findOne(new Tuple(u.getId(), x.getId())) != null
                        || repoPrietenie.findOne(new Tuple(x.getId(), u.getId())) != null)
                    prieteni.add(x);
        }

        prieteni.forEach(x -> {
            users.remove(x);
        });

        return users;
    }

    public List<Prietenie> friendsByMonth(Long id, int month) {
        Predicate <Prietenie> pr = (e -> {
            return e.getMonth() == month;
        });
        return mainGet(id, pr);
    }

    public List<Prietenie> getFriendList(Long id) {
        Predicate<Prietenie> pr = (e -> {
            return true;
        });
        return mainGet(id, pr);
    }

    public void deleteFriendship(Long id1, Long id2) {
        Utilizator user1 = repo.findOne(id1);
        Utilizator user2 = repo.findOne(id2);
        if(user1 == null) {
            throw new ValidationException("There is no user with the first id...");
        }
        if(user2 == null) {
            throw new ValidationException("There is no user with the second id...");
        }
        if(repoPrietenie.findOne(new Tuple(id1,id2)) != null) {
            repoPrietenie.delete(new Tuple(id1, id2));
        }
        if(repoPrietenie.findOne(new Tuple(id2,id1)) != null)
            repoPrietenie.delete(new Tuple(id2,id1));
    }

    public void dfs(Utilizator user) {
        if(hm.contains(user)){
            return;
        }
        hm.add(user);
        user.getFriends().forEach(fr -> {
            dfs(fr);
        });

    }

    public int numberOfCommunities() {
        nrComunitati = 0;
        hm.clear();
        repo.findAll().forEach(user -> {
            if(!hm.contains(user))
                nrComunitati++;
            dfs(user);
        });

        return nrComunitati;

    }
/*
    public int bfs(Tuple<Long, Long> friend) {
        Queue<Tuple<Long,Long>> current = new LinkedList<Tuple<Long, Long>>();
        current.add(friend);
        int rezultat = 0;
        while(!current.isEmpty()){
            Tuple<Long,Long> pid = current.remove();
            community.put(pid, true);
            rezultat++;
            repo.findOne(pid.getLeft()).getFriends().forEach(prieten -> {
                Long id1 = pid.getLeft();
                Long id2 = prieten.getId();
                Tuple<Long, Long> aux;
                aux = new Tuple<Long, Long>(id1,id2);
                if(community.get(aux) == false) {
                    current.add(aux);
                }
            });
        }
        return rezultat;
    }

    public List<Utilizator> mostSociableCommunity() {
        community.clear();
        int largest = 0;
        List<Utilizator> result = new ArrayList<Utilizator>();
        repoPrietenie.findAll().forEach(p -> {
            community.put(p.getId(), false);
        }); // populez map-ul cu toate prieteniile si daca au fost vizitate sau nu

        for(Tuple<Long,Long>friend: community.keySet()) {
            if(community.get(friend) == false) {
                int aux = bfs(friend);
                if(aux > largest) {
                    largest = aux;
                    result.clear();
                    for(Tuple<Long,Long>f: community.keySet()){
                        if(community.get(f) == true) {
                            result.add(repo.findOne(f.getLeft()));
                        }
                    }
                }
            }
        }
      return result;
    }

*/

public Utilizator findByUsername(String s) {
    return repo.findByUsername(s);
}
public Utilizator findByName(String lastName, String firstName) {
    return repo.findByName(lastName, firstName);
}


public int bk(Utilizator user) {
    int length = 0;
    List<Utilizator> friends = user.getFriends();
    for(Utilizator friend: friends){
        Tuple <Long, Long> prietenie = new Tuple<Long,Long>(user.getId(), friend.getId());
        if(!sociabili.contains(prietenie)){
            sociabili.add(prietenie);
            int val = bk(friend);
            if(val + 1 > length)
                length = val + 1;
        }
    };
    return length;
}

public List<Utilizator> mostSociableCommunity() {
    int lantMaxim = 0;
    List<Utilizator> result = new ArrayList<Utilizator>();
    for(Utilizator user: repo.findAll()) {
        int lungimeLant = bk(user);
        if(lantMaxim < lungimeLant){
            lantMaxim = lungimeLant;
            hm.clear();
            dfs(user);
        }
    };

    for(Utilizator user: hm) {
        result.add(user);
    }

    return result;
}

    public void addFriendship(Long id1, Long id2) {

        Utilizator user1 = repo.findOne(id1);
        Utilizator user2 = repo.findOne(id2);

        if(user1 == null) {
            throw new ValidationException("There is no user with the first id...");
        }
        if(user2 == null) {
            throw new ValidationException("There is no user with the second id...");
        }

        if(user1 == user2){
            throw new ValidationException("You cannot add yourself as a friend.");
        }

        if(repoPrietenie.findOne(new Tuple(id1,id2)) != null || repoPrietenie.findOne(new Tuple(id2,id1)) != null) {
            throw new ValidationException("This friendship already exists!") ;
        }

        user1.addFriend(user2);
        user2.addFriend(user1);

        Prietenie p = new Prietenie();
        Tuple tuple = new Tuple(id1, id2);
        p.setId(tuple);
        repoPrietenie.save(p);
    }

}