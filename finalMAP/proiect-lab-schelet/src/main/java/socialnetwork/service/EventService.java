package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.EventsDB;

import java.sql.SQLException;
import java.util.List;

public class EventService {

    private EventsDB repoEvent;
    private Validator<Event> val;

    public EventService(EventsDB repo, Validator<Event> val) {
        this.repoEvent = repo;
        this.val = val;
    }


    public void createEvent(Event event) {

            val.validate(event);
            Event e = repoEvent.findByDescription(event.getDescriere());
            if (e == null)
                repoEvent.save(event);
            else
                throw new ValidationException("Descrierea nu poate fi identica");

    }

    public void deleteEvent(Event event) {
        repoEvent.delete(event.getId());
    }

    public void deleteAllAbonati(Event event) {
        repoEvent.deleteAllAbonari(event);
    }

    public List<Long> getAllAbonari(Utilizator user) {
        return repoEvent.findAllAbonari(user);
    }


    public void addAbonat(Event event, Utilizator user) {
        int found = repoEvent.findAbonare(user, event);
        if(found == 1)
            throw new ValidationException("Sunteti deja abonat la acest eveniment!");
        repoEvent.addAbonare(user, event);
    }

    public void deleteAbonat(Event event, Utilizator user) {
        repoEvent.deleteAbonare(user,event);
    }

    public Iterable<Event> getAll()
    {
        return repoEvent.findAll();
    }

    public Event getOne(Long id1) {
        return repoEvent.findOne(id1);
    }

}
