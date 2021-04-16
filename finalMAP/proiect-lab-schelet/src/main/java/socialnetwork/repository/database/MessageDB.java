package socialnetwork.repository.database;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLWarning;
import socialnetwork.domain.Message;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MessageDB  implements Repository<Long, Message> {

    private JdbcUtils dbUtils;


    public MessageDB(Properties properties){
        dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Message findOne(Long aLong) {


        String q = "SELECT * FROM mesaje where id = ?";
        List<Utilizator> list_destinatari = new ArrayList<Utilizator>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)){

            statement.setInt(1,aLong.intValue());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_mesaj = resultSet.getLong("id");
                Long expeditor = resultSet.getLong("expeditor");
                String mesaj = resultSet.getString("mesaje");
                LocalDateTime data = LocalDateTime.parse(resultSet.getString("data"));
                Long reply = resultSet.getLong("reply");

                ArrayList<Long> id_users = new ArrayList<Long>();
                Utilizator exp = new Utilizator();

                String q2 = "SELECT * from destinatari where id_mesaj = ?";
                PreparedStatement statement2 = connection.prepareStatement(q2);
                statement2.setInt(1,id_mesaj.intValue());
                resultSet = statement2.executeQuery();
                while(resultSet.next())
                {
                    Long id_user = resultSet.getLong("id_utilizator");
                    id_users.add(id_user);
                }

                String q3 = "SELECT * from users";
                PreparedStatement statement3 = connection.prepareStatement(q3);
                resultSet = statement3.executeQuery();
                while(resultSet.next())
                {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("nume");
                    String lastName = resultSet.getString("prenume");

                    if(id == expeditor)
                    {

                        exp.setFirstName(firstName);
                        exp.setLastName(lastName);
                        exp.setId(id);
                    }

                    else if(id_users.contains(id))
                    {
                        Utilizator user = new Utilizator(firstName, lastName);
                        user.setId(id);
                        list_destinatari.add(user);
                    }

                }
                Message message = new Message(exp,list_destinatari,mesaj);
                message.setData(data);
                message.setId(id_mesaj);
                return message;

            }
            return null;
        } catch (PSQLException e)
        {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }


    @Override
    public Iterable<Message> findAll() {


        String q = "SELECT * FROM mesaje ";
        List<Message> lista_mesaje= new ArrayList<Message>();

        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)){

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id_mesaj = resultSet.getLong("id");
                Long expeditor = resultSet.getLong("expeditor");
                String mesaj = resultSet.getString("mesaje");
                LocalDateTime data = LocalDateTime.parse(resultSet.getString("data"));
                Long reply = resultSet.getLong("reply");


                ArrayList<Long> id_users = new ArrayList<Long>();
                List<Utilizator> list_destinatari = new ArrayList<Utilizator>();
                Utilizator exp = new Utilizator();

                String q2 = "SELECT * from destinatari where id_mesaj = ?";

                PreparedStatement statement2 = connection.prepareStatement(q2);
                statement2.setInt(1, id_mesaj.intValue());
                ResultSet resultSet2 = statement2.executeQuery();
                while (resultSet2.next()) {
                    Long id_user = resultSet2.getLong("id_utilizator");
                    id_users.add(id_user);
                }

                String q3 = "SELECT * from users";

                PreparedStatement statement3 = connection.prepareStatement(q3);
                ResultSet resultSet3 = statement3.executeQuery();
                while (resultSet3.next()) {
                    Long id = resultSet3.getLong("id");
                    String firstName = resultSet3.getString("nume");
                    String lastName = resultSet3.getString("prenume");

                    if (id == expeditor) {

                        exp.setFirstName(firstName);
                        exp.setLastName(lastName);
                        exp.setId(id);
                    } else if (id_users.contains(id)) {
                        Utilizator user = new Utilizator(firstName, lastName);
                        user.setId(id);
                        list_destinatari.add(user);
                    }

                }
                Message message = new Message(exp, list_destinatari, mesaj);
                message.setData(data);
                message.setId(id_mesaj);
                lista_mesaje.add(message);
                id_users.clear();

            }
            return lista_mesaje;
        }catch (PSQLException e)
        {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);

        }

        return null;
    }

    @Override
    public Message save(Message entity) {
        Connection connection = dbUtils.getConnection();
        String q = "Insert into mesaje values(?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,entity.getFrom().getId().intValue());
            statement.setString(3,entity.getMessage());
            statement.setString(4,entity.getData().toString());
            statement.setInt(5,entity.getReply().intValue());
            statement.executeUpdate();

            for(Utilizator i:entity.getTo())
            {
                String q2 = "INSERT INTO destinatari values(?,?)";
                PreparedStatement statement2 = connection.prepareStatement(q2);
                statement2.setInt(1,i.getId().intValue());
                statement2.setInt(2,entity.getId().intValue());
                statement2.executeUpdate();
            }


        }catch (PSQLException e)
        {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);

        }


        return entity;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    @Override
    public Message findByUsername(String s) {
        return null;
    }

    @Override
    public Message findByName(String lastName, String firstName) {
        return null;
    }
}
