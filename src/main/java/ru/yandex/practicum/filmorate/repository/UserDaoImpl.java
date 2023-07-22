package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users (name, email, login, birthday) " +
                "values (?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey().intValue());
        user.setId(id);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        String sqlQueryDeleteFriends = "DELETE FROM friends WHERE friend_id = ?";
        String sqlQueryDeleteUser = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQueryDeleteFriends, userId);
        jdbcTemplate.update(sqlQueryDeleteUser, userId);
    }

    @Override
    public List<User> getFriendsById(Integer id) {
        return jdbcTemplate.query("SELECT u.id, u.name, u.email, u.login, u.birthday from users u WHERE u.id in (SELECT friend_id from friends WHERE user_id = ?)", userRowMapper(), id);
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherUserId) {
        return jdbcTemplate.query("SELECT u.id, u.name, u.email, u.login, u.birthday from users u WHERE u.id in (SELECT friend_id from friends WHERE user_id = ? INTERSECT SELECT friend_id from friends WHERE user_id = ?)", userRowMapper(), userId, otherUserId);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users" +
                " SET name = ?, email = ?, login = ?, birthday = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return getUserById(user.getId());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "INSERT INTO friends VALUES (?,?)";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM friends " +
                "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery,
                userId,
                friendId);
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "from users u", userRowMapper());
    }

    @Override
    public User getUserById(Integer id) {
        List<User> users = jdbcTemplate.query("SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "from users u  " +
                "where u.id = ?", userRowMapper(), id);

        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setBirthday(LocalDate.parse(rs.getDate("birthday").toString()));
            user.setFriends(new HashSet<>(getFriendsIds(user.getId())));
            return user;
        };

    }

    private List<Integer> getFriendsIds(Integer id) {
        return jdbcTemplate.query("SELECT friend_id From friends where user_id = ?", (rs, rowNum) -> rs.getInt("friend_id"), id);
    }

    public List<Integer> searchSameUser(Integer id) {
        return jdbcTemplate.query("SELECT user_id,COUNT(distinct film_id) from likes " +
                "WHERE film_id IN (SELECT distinct film_id from likes where user_id = ?) " +
                "GROUP BY user_id " +
                "ORDER BY COUNT(distinct film_id) DESC " +
                "LIMIT 2", (rs, rowNum) -> {
                                            rs.getInt("COUNT(distinct film_id)");
                                            return  rs.getInt("user_id");
                                            }, id);
    }

    @Override
    public List<Integer> recommendFilmsId(Integer id) {
        List<Integer> sameUserId = searchSameUser(id);
        if (sameUserId.size() < 2) {
            return new ArrayList<>();
        }
        return jdbcTemplate.query("SELECT id from films " +
                "WHERE id in " +
                "(SELECT distinct film_id from likes where user_id = ?) " +
                "EXCEPT " +
                "(SELECT distinct film_id from likes where user_id = ?)",(rs, rowNum) -> rs.getInt("id"), sameUserId.get(1), id);
    }
}
