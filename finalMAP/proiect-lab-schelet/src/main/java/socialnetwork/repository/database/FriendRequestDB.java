package socialnetwork.repository.database;

import jdk.nashorn.internal.ir.RuntimeNode;
import socialnetwork.domain.FriendRequest;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class FriendRequestDB implements PagingRepository<Tuple<Long,Long>, FriendRequest> {


    private JdbcUtils dbUtils;
    private Validator<Utilizator> validator;

    public FriendRequestDB(Properties properties) {
        dbUtils = new JdbcUtils(properties);
    }

    @Override
    public FriendRequest findOne(Tuple<Long, Long> tuple) {
            String q = "SELECT * FROM requests WHERE idSent = ? AND idRecieved = ?";
        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(q)){
            statement.setInt(1,tuple.getLeft().intValue());
            statement.setInt(2,tuple.getRight().intValue());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Long idSent = resultSet.getLong("idSent");
                Long idRecieved = resultSet.getLong("idRecieved");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("data"));
                FriendRequest req =  new FriendRequest(idSent,idRecieved,status,date);
                req.setId(new Tuple(idSent,idRecieved));
                return req;

            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public Iterable<FriendRequest> findAll() {
        String q = "SELECT * from requests";
        Connection connection = dbUtils.getConnection();
        Set<FriendRequest> reqs = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(q)){

            ResultSet resultSet = statement.executeQuery();
            //System.out.println(resultSet);

            while(resultSet.next()) {
                Long idSent = resultSet.getLong("idSent");
                Long idRecieved = resultSet.getLong("idRecieved");
                String status = resultSet.getString("status");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("data"));
                FriendRequest req =  new FriendRequest(idSent,idRecieved,status,date);
                req.setId(new Tuple(idSent,idRecieved));

                reqs.add(req);

            }
            return reqs;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return reqs;
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        String q = "INSERT INTO requests Values(?,?,?,?)";
        Connection connection = dbUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1,entity.getRequested().intValue());
            statement.setInt(2,entity.getRecieved().intValue());
            statement.setString(3,entity.getStatus());
            statement.setString(4,entity.getData().toString());

            statement.executeUpdate();


        }catch(SQLException e)
        {
            throw new ValidationException("Deja aveti o cerere trimisa!");
        }

        return entity;
    }

    @Override
    public FriendRequest delete(Tuple<Long, Long>tuple) {
        if(findOne(tuple)==null)
            throw new IllegalArgumentException("ID does not exist...");
        FriendRequest req=findOne(tuple);
        Connection connection = dbUtils.getConnection();
        String q = "DELETE FROM requests where idSent = ? and idRecieved = ?";
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setInt(1,tuple.getLeft().intValue());
            statement.setInt(2,tuple.getRight().intValue());
            statement.executeUpdate();

        }catch(SQLException e)
        {
            e.printStackTrace();
        }

        return req;
    }

    @Override
    public FriendRequest update(FriendRequest entity) {
        Tuple<Long,Long> tuple = entity.getId();
        if(findOne(tuple)==null)
            throw new IllegalArgumentException("this id dont exists...");
        Connection connection = dbUtils.getConnection();
        String q = "UPDATE requests SET status = ? WHERE idSent = ? AND idRecieved = ?";
        try (PreparedStatement statement = connection.prepareStatement(q)) {

            statement.setString(1,entity.getStatus());
            statement.setInt(2,entity.getRequested().intValue());
            statement.setInt(3,entity.getRecieved().intValue());
            statement.executeUpdate();

        }catch(SQLException e)
        {
            e.printStackTrace();
        }


        return findOne(tuple);
    }

    @Override
    public FriendRequest findByUsername(String s) {
        return null;
    }

    @Override
    public FriendRequest findByName(String lastName, String firstName) {
        return null;
    }


    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        Paginator<FriendRequest> paginator = new Paginator<>(pageable,this.findAll());
        return paginator.paginate();
    }
}
