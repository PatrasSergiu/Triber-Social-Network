package socialnetwork.domain;

import socialnetwork.domain.validators.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean prieten = false;
    private List<Utilizator> friends;

    public String getStatus() {
        if(prieten)
            return "Da";
        else
            return "Nu";
    }

    public String getNumeComplet() {
        return lastName + " " + firstName;
    }

    public void setPrieten(boolean b) {
        prieten = b;
    }

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friends = new ArrayList<Utilizator>();
    }

    public void setUserPass(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Utilizator(){};

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void addFriend(Utilizator friend) {
        for(Utilizator user: friends) {
            if(user.equals(friend))
                throw new ValidationException("Friendship already exists!");
        }

        friends.add(friend);
    }

    public void deleteFriend(Utilizator friend) {
        int ok = 0;
        for(Utilizator user: friends) {
            if(user.equals(friend)){
                ok = 1;
            }
        }
        if(ok == 0)
            throw new ValidationException("User is not in friendlist.");
        else
            friends.remove(friend);
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        String str="[ ";
        for(Utilizator i:friends)
            str=str+i.getId() + " ";
        str=str+"]";
        return "Utilizator{" +
                "Id=" + this.getId() + '\'' +
        "firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", friends=" + str +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}