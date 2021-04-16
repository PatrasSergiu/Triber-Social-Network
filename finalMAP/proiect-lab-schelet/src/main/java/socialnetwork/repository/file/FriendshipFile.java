package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple <Long, Long>, Prietenie> {
    public FriendshipFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }

    @Override
    public Prietenie extractEntity(List<String> entity) {
        Tuple t = new Tuple(Long.parseLong(entity.get(0)),Long.parseLong(entity.get(1)));
        Prietenie p = new Prietenie(LocalDateTime.parse(entity.get(2)));
        p.setId(t);

        return p;
    }


}
