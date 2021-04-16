package socialnetwork.domain.validators;

import socialnetwork.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        //TODO: implement method validate

        if(entity.getFirstName().equals("") || entity.getFirstName().trim().equals(""))
            throw new ValidationException("First Name cannot be empty");

        if(entity.getLastName().equals("") || entity.getLastName().trim().equals(""))
            throw new ValidationException("Last Name cannot be emtpy");
    }
}
