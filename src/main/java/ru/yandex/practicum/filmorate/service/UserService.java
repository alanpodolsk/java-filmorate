package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public User addUser(User user);

    public void deleteUser(Integer userId);

    public User updateUser(User user);

    public List<User> getAllUsers();

    public User addFriend(Integer userId, Integer friendId);

    public User deleteFriend(Integer userId, Integer friendId);

    public List<User> getFriends(Integer userId);

    public List<User> getMutualFriends(Integer userId, Integer otherId);

    public User getUser(Integer id);

    public List<Film> recommendFilms(Integer id);
}
