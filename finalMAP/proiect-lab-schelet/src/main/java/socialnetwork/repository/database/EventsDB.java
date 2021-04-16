package socialnetwork.repository.database;


import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import javax.xml.transform.Result;
import java.nio.file.LinkOption;
import java.sql.*;

import java.time.LocalDateTime;
import java.util.*;

public class EventsDB implements PagingRepository<Long, Event> {


    private JdbcUtils dbUtils;
    private Validator<Event> validator;

    public EventsDB(Properties properties, Validator<Event> val) {
        dbUtils = new JdbcUtils(properties);
        this.validator = val;
    }

    public Event findByDescription(String s) {
        String q = "SELECT * FROM eveniment WHERE descriere =?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setString(1, s);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long idEvent = resultSet.getLong("ideveniment");
                String descriere = resultSet.getString("descriere");
                String data = resultSet.getString("data");


                Event event = new Event(descriere, LocalDateTime.parse(data));
                event.setId(idEvent);
                return event;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Event findOne(Long id) {
        String q = "SELECT * FROM eveniment WHERE ideveniment = ?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1, id.intValue());
            ResultSet resultSet = statement.executeQuery();
            //System.out.println(resultSet);


            if (resultSet.next()) {
                Long idEvent = resultSet.getLong("ideveniment");
                String descriere = resultSet.getString("descriere");
                String data = resultSet.getString("data");


                Event event = new Event(descriere, LocalDateTime.parse(data));
                event.setId(idEvent);
                return event;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from eveniment");

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("ideveniment");
                String descriere = resultSet.getString("descriere");
                String data = resultSet.getString("data");

                Event event = new Event(descriere, LocalDateTime.parse(data));
                event.setId(id);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        String q = "INSERT INTO eveniment Values(?,?,?)";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1, entity.getId().intValue());
            statement.setString(2, entity.getDescriere());
            statement.setString(3, entity.getData().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entity;
    }

    @Override
    public Event delete(Long aLong) {

        Event ret=findOne(aLong);
        if(ret == null)
            throw new IllegalArgumentException("The event you were trying to delete does not exists.");
        Connection connection = dbUtils.getConnection();
        String q = "DELETE from eveniment where ideveniment=?";
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1,aLong.intValue());
            statement.executeUpdate();

        }catch(SQLException e)
        {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    @Override
    public Event findByUsername(String s) {
        return null;
    }

    @Override
    public Event findByName(String lastName, String firstName) {
        return null;
    }

    public void addAbonare(Utilizator user, Event event) {
        String q = "INSERT INTO abonari(iduser, ideveniment) VALUES (?,?)";
        Connection connection = dbUtils.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(q);

            statement.setLong(1, user.getId());
            statement.setLong(2, event.getId());
            statement.execute();
        }
        catch(SQLException e) {
            throw new ValidationException("Esti deja abonat");
        }
    }

    public int findAbonare(Utilizator user, Event event) {
        String q = "SELECT * FROM abonari WHERE iduser = ? and ideveniment=?";
        Connection connection = dbUtils.getConnection();
        try {
            PreparedStatement stat = connection.prepareStatement(q);

            System.out.println(user);
            stat.setLong(1, user.getId());
            stat.setLong(2, event.getId());
            ResultSet rezultat = stat.executeQuery();
            rezultat.next();
            Long id1 = rezultat.getLong("iduser");
            Long id2 = rezultat.getLong("ideveniment");
            if (id1 != null || id2 != null) {
                return 1;
            }
        }
        catch(SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    public void deleteAbonare(Utilizator user, Event event) {
        String q = "DELETE FROM abonari WHERE iduser=? and ideveniment=?";
        Connection connection = dbUtils.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(q);

            statement.setLong(1, user.getId());
            statement.setLong(2, event.getId());
            statement.execute();
        }
        catch(SQLException e) {
            throw new ValidationException("Stergerea a esuat");
        }
    }

    public void deleteAllAbonari(Event e) {
        String q = "DELETE FROM abonari WHERE ideveniment=?";
        Connection connection = dbUtils.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(q);

            statement.setLong(1, e.getId());
            statement.executeUpdate();
        }
        catch(SQLException exception) {
            throw new ValidationException("Eroare stergere abonati");
        }


    }

    public List<Long> findAllAbonari(Utilizator user) {
        List<Long> lista = new ArrayList<Long>();
        String q = "SELECT * FROM abonari WHERE iduser=?";
        Connection connection = dbUtils.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(q);

            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("ideveniment");
                lista.add(id);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return lista;
    }


    @Override
    public Page<Event> findAll(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<>(pageable,this.findAll());
        return paginator.paginate();
    }
}