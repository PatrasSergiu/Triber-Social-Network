package socialnetwork.domain.validators;

import socialnetwork.domain.Event;
import socialnetwork.domain.Utilizator;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventValidator  implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity.getData().compareTo(LocalDateTime.now())<0)
            throw new ValidationException("Data a trecut!");
    }
}
