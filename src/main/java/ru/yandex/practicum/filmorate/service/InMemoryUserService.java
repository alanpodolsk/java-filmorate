package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        isValid(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        isValid(user);
        if (user.getId() == null || userStorage.getUser(user.getId()) == null) {
            throw new NoObjectException("Данный пользователь отсутствует в базе");
        }
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(friendId);
        if (otherUser == null)
            throw new NoObjectException("Пользователь с ID =" + friendId + " не найден");
        else if (user == null) {
            throw new NoObjectException("Пользователь с ID =" + id + " не найден");
        }
        Set<Integer> friends = user.getFriends();
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friendId);
        user.setFriends(friends);
        userStorage.updateUser(user);
        Set<Integer> otherUserFriends = otherUser.getFriends();
        if (otherUserFriends == null) {
            otherUserFriends = new HashSet<>();
        }
        otherUserFriends.add(id);
        otherUser.setFriends(otherUserFriends);
        userStorage.updateUser(otherUser);
        return user;
    }

    @Override
    public User deleteFriend(Integer id, Integer friendId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(friendId);
        if (otherUser == null)
            throw new NoObjectException("Пользователь с ID =" + friendId + " не найден");
        else if (user == null) {
            throw new NoObjectException("Пользователь с ID =" + id + " не найден");
        }
        Set<Integer> friends = user.getFriends();
        if (!friends.contains(friendId)) {
            throw new NoObjectException("Данный пользователь не был в друзьях");
        } else {
            friends.remove(friendId);
            user.setFriends(friends);
            userStorage.updateUser(user);
            Set<Integer> otherUserFriends = otherUser.getFriends();
            otherUserFriends.remove(id);
            otherUser.setFriends(otherUserFriends);
            userStorage.updateUser(otherUser);
            return user;
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NoObjectException("Пользователь с ID =" + userId + " не найден");
        }
        List<User> friends = new ArrayList<>();
        if (user.getFriends() != null) {
            for (Integer friend : user.getFriends()) {
                friends.add(userStorage.getUser(friend));
            }
        }
        return friends;
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherId);
        return getMutualFriends(user.getFriends(), otherUser.getFriends());
    }

    @Override
    public User getUser(Integer id) {
        if (userStorage.getUser(id) != null) {
            return userStorage.getUser(id);
        } else {
            throw new NoObjectException("Пользователь с id=" + id + " не найден");
        }
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

    private List<User> getMutualFriends(Set<Integer> userFriends, Set<Integer> otherUserFriends) {
        List<User> mutualFriends = new ArrayList<>();
        if (userFriends != null && otherUserFriends != null) {
            for (Integer friend : userFriends) {
                if (otherUserFriends.contains(friend)) {
                    mutualFriends.add(userStorage.getUser(friend));
                }
            }
        }
        return mutualFriends;
    }
}
