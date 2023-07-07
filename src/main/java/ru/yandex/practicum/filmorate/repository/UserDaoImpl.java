package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Primary
@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Integer addUser(User user) {
        String sqlQuery = "INSERT INTO users (name, email, login, birthday) " +
                "values (?, ?, ?,?)";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        return getUserIdByLoginAndEmail(user.getLogin(), user.getEmail());
    }

    @Override
    public List<User> getFriendsById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT u.id, u.name, u.email, u.login, u.birthday, f.friend_id from users u left join friends f on u.id = f.user_id WHERE u.id in (SELECT friend_id from friends WHERE user_id = ?)", userRowMapper(), id);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return new ArrayList<User>();
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherUserId) {
        try {
            return jdbcTemplate.queryForObject("SELECT u.id, u.name, u.email, u.login, u.birthday, f.friend_id from users u left join friends f on u.id = f.user_id WHERE u.id in (SELECT friend_id from friends WHERE user_id = ? INTERSECT SELECT friend_id from friends WHERE user_id = ?)", userRowMapper(), userId, otherUserId);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return new ArrayList<User>();
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Integer updateUser(User user) {
        String sqlQuery = "UPDATE users" +
                " SET name = ?, email = ?, login = ?, birthday = ?" +
                " WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user.getId();
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
        try {
            return jdbcTemplate.queryForObject("SELECT u.id, u.name, u.email, u.login, u.birthday, f.friend_id " +
                    "from users u left join friends f on u.id = f.user_id", userRowMapper());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return new ArrayList<>();
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public User getUserById(Integer id) {
        List<User> users;
        try {
            users = jdbcTemplate.queryForObject("SELECT u.id, u.name, u.email, u.login, u.birthday, f.friend_id " +
                    "from users u left join friends f on u.id = f.user_id " +
                    "where u.id = ?", userRowMapper(), id);
        } catch (RuntimeException e) {
            return null;
        }
        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    private RowMapper<List<User>> userRowMapper() {
        return (rs, rowNum) -> {
            List<User> users = new ArrayList<>();
            User user = new User();
            Set<Integer> friends = new HashSet<>();
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setBirthday(LocalDate.parse(rs.getDate("birthday").toString()));
            do {
                if (user.getId() != rs.getInt("id")) {
                    user.setFriends(friends);
                    users.add(user);
                    user = new User();
                    friends = new HashSet<>();
                    user.setId(rs.getInt("id"));
                    user.setLogin(rs.getString("login"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setBirthday(LocalDate.parse(rs.getDate("birthday").toString()));
                }
                Integer friendId = rs.getInt("friend_id");
                if (friendId > 0) {
                    friends.add(rs.getInt("friend_id"));
                }
            } while (rs.next());
            user.setFriends(friends);
            users.add(user);
            return users;
        };

    }

    private Integer getUserIdByLoginAndEmail(String login, String email) {
        return jdbcTemplate.queryForObject("SELECT id From users where login = ? AND email = ?", Integer.class, login, email);
    }
}
