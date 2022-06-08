package antifraud;

import antifraud.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User(rs.getString("username"), rs.getString("name"), rs.getString("password"));
            user.setUserId(rs.getLong("user_id"));
            //user.setRole(rs.getString("role"));
            user.setEnabled(rs.getBoolean("enabled"));

            return user;
        }

    }

    public List<User> findAll() {
        return jdbcTemplate.query("select * from user ORDER BY user_id ASC", new UserRowMapper());
    }

    public User getUser(String username) {
        username = username.toLowerCase();
        User user = null;
        try {
            user = jdbcTemplate.queryForObject("select * from user where LOWER(username)=?", new Object[]{username},
                    new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException ex) {
            System.out.println("0 result");
        }
        return user;
    }

    public long addUser(User user) {
        long maxId = getMaxId();
        if (getUser(user.getUsername()) == null) {
            jdbcTemplate.update("insert into user (user_id, name, username, role, password, enabled) " + "values(?, ?, ?, ?, ?, ?)",
                    new Object[]{maxId, user.getName(), user.getUsername(), user.getRole().name(), user.getPassword(), user.isEnabled()});

            return maxId;
        }
        return 0;
    }

    public boolean deleteUser(String username) {
        username = username.toLowerCase();
        if (jdbcTemplate.update("delete from user where LOWER(username)=?", new Object[]{username}) > 0) {
            return true;
        }
        return false;
    }

    public long getMaxId() {
        long max = 0;
        try {
            max = jdbcTemplate.queryForObject("select max(user_id) from user", Long.class) + 1;
        } catch (NullPointerException ex) {
            max = 1;
            System.out.println("null max id");
        }
        return max;
    }

}
