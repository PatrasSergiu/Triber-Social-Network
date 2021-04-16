package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("ID cannot be null");
        return entities.get(id);
    }

    public E findByUsername(String username) {
        return null;
    }

    @Override
    public E findByName(String lastName, String firstName) {
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("The entity cannot be null");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null) {
            throw new IllegalArgumentException("This ID is already in use!");
        }
        else
            entities.put(entity.getId(),entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("ID can't be null");
        else
            if(entities.get(id) == null)
                throw new IllegalArgumentException("ID is not in the list");

        return entities.remove(id);
    }

    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }



}
