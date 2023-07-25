package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;
import ru.yandex.practicum.filmorate.repository.EventDao;

import java.time.LocalDate;
import java.util.*;

@Service
@Primary
@AllArgsConstructor
public class DbUserService implements UserService {
    private UserDao userDao;
    private FilmDao filmDao;
    private EventDao eventDao;

    @Override
    public User addUser(User user) {
        isValid(user);
        return userDao.addUser(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userId > 0 || userDao.getUserById(userId) != null) {
            userDao.deleteUser(userId);
        }
    }

    @Override
    public User updateUser(User user) {
        isValid(user);
        if (user.getId() == null || userDao.getUserById(user.getId()) == null) {
            throw new NoObjectException("Данный пользователь отсутствует в базе");
        }
        return userDao.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Пользователь с ID =" + userId + " не найден");
        } else if (userDao.getUserById(friendId) == null) {
            throw new NoObjectException("Пользователь с ID =" + friendId + " не найден");
        } else {
            userDao.addFriend(userId, friendId);
            return userDao.getUserById(userId);
        }
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Пользователь с ID =" + userId + " не найден");
        } else if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Пользователь с ID =" + friendId + " не найден");
        } else {
            userDao.deleteFriend(userId, friendId);
            return userDao.getUserById(userId);
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Пользователь с id = " + userId + "отсутствует в базе");
        } else {
            return userDao.getFriendsById(userId);
        }
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Пользователь с id = " + userId + "отсутствует в базе");
        } else if (userDao.getUserById(otherId) == null) {
            throw new NoObjectException("Пользователь с id = " + otherId + "отсутствует в базе");
        } else {
            return userDao.getMutualFriends(userId, otherId);
        }
    }

    @Override
    public User getUser(Integer id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new NoObjectException("Пользователь с id = " + id + " отсутствует в базе");
        } else {
            return user;
        }
    }

    @Override
    public List<Film> recommendFilms(Integer id) {
        List<Integer> sameUserId = userDao.searchSameUser(id);
        if (sameUserId.size() < 2) {
            return new ArrayList<>();
        }
        return filmDao.getRecomendFilms(id, sameUserId.get(1));
    }

    @Override
    public List<Event> getEventsList(Integer id) {
        if (userDao.getUserById(id) == null) {
            throw new NoObjectException("Данный пользователь не найден в базе");
        }
        return eventDao.getFeedById(id);
    }

    private User isValid(User user) {
        if (user == null) {
            throw new ValidationException("Передан пустой объект пользователя");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не должна быть позднее сегодня");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректно указан email");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setNameLikeLogin();
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return user;
    }
}
