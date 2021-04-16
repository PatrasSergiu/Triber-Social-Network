package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class UserDB  implements PagingRepository<Long, Utilizator> {
    private JdbcUtils dbUtils;
    private Validator<Utilizator> validator;

    public UserDB(Properties properties, Validator<Utilizator> validator) {
        dbUtils = new JdbcUtils(properties);
        this.validator = validator;
    }

    public Utilizator findByUsername(String user) {
        String q = "SELECT * FROM users WHERE username = ?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)){
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("nume");
                String lastName = resultSet.getString("prenume");
                String userName = resultSet.getString("username");
                String userPass = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setUserPass(userName, userPass);
                utilizator.setId(id);
                return utilizator;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator findByName(String lastName, String firstName) {
        String q = "SELECT * FROM users WHERE nume = ? AND prenume = ?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)){
            statement.setString(1, lastName);
            statement.setString(2, firstName);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String fName = resultSet.getString("nume");
                String lName = resultSet.getString("prenume");
                String userName = resultSet.getString("username");
                String userPass = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setUserPass(userName, userPass);
                utilizator.setId(id);
                return utilizator;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator findOne(Long aLong) {
        String q =  "SELECT * FROM users WHERE id = ?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)){
            statement.setInt(1,aLong.intValue());
            ResultSet resultSet = statement.executeQuery();
            //System.out.println(resultSet);


            if(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("nume");
                String lastName = resultSet.getString("prenume");


                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                return utilizator;
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from users")){
             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("nume");
                String lastName = resultSet.getString("prenume");

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {
        String q = "INSERT INTO users Values(?,?,?,?,?)";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {
            statement.setInt(1,entity.getId().intValue());
            statement.setString(2,entity.getFirstName());
            statement.setString(3,entity.getLastName());
            statement.setString(4, entity.getUsername());
            statement.setString(5, entity.getPassword());

            statement.executeUpdate();


        }catch(SQLException e)
        {
            e.printStackTrace();
        }

        return entity;
    }

    @Override
    public Utilizator delete(Long aLong) {
        if(findOne(aLong)==null)
            throw new IllegalArgumentException("The user you were trying to delete does not exists.");
        Utilizator ret=findOne(aLong);
        Connection connection = dbUtils.getConnection();
        String q = "DELETE from users where id=?";
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
    public Utilizator update(Utilizator entity) {
        return null;
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        Paginator<Utilizator> paginator = new Paginator<>(pageable,this.findAll());
        return paginator.paginate();
    }
}
