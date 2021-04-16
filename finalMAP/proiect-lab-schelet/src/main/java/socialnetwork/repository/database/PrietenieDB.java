package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDB implements PagingRepository<Tuple<Long, Long >, Prietenie> {
    private JdbcUtils dbUtils;
    private Validator<Utilizator> validator;


    public PrietenieDB(Properties properties, Validator<Utilizator> validator) {
        dbUtils = new JdbcUtils(properties);
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        String q = "SELECT * FROM prietenii WHERE id1 = ? AND id2 = ?";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {
            statement.setInt(1, id.getLeft().intValue());
            statement.setInt(2, id.getRight().intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Long id1 =resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime data = LocalDateTime.parse(resultSet.getString("data"));

                Prietenie pr = new Prietenie(data);
                pr.setId(new Tuple<Long, Long>(id1, id2));
                return pr;
            }
        return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Map<Tuple<Long,Long>, Prietenie> prietenii = new HashMap<>();
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from prietenii");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("data"));
                Prietenie  prietenie = new Prietenie(date);
                prietenie.setId(new Tuple(id1,id2));

                prietenii.put(new Tuple(id1,id2),prietenie);

            }
            return prietenii.values();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prietenii.values();
    }

    @Override
    public Prietenie save(Prietenie entity) {
        String q = "INSERT INTO prietenii Values(?,?,?)";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1, entity.getId().getLeft().intValue());
            statement.setInt(2, entity.getId().getRight().intValue());
            statement.setString(3, entity.getDate().toString());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Prietenie delete(Tuple <Long, Long> id) {
        if(findOne(id) == null) {
            throw new ValidationException("There is no friendship with that id.");
        }
        Prietenie pr = findOne(id);
        Connection connection = dbUtils.getConnection();
        String q = "DELETE FROM prietenii WHERE id1 = ? AND id2 = ?";
        try(PreparedStatement statement = connection.prepareStatement(q)){
            statement.setInt(1,id.getLeft().intValue());
            statement.setInt(2, id.getRight().intValue());
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return pr;
    }
    @Override
    public Prietenie update(Prietenie entity) {
        return null;
    }

    @Override
    public Prietenie findByUsername(String s) {
        return null;
    }

    @Override
    public Prietenie findByName(String lastName, String firstName) {
        return null;
    }

    @Override
    public Page<Prietenie> findAll(Pageable pageable) {
        Paginator<Prietenie> paginator = new Paginator<>(pageable,this.findAll());
        return paginator.paginate();
    }
}
