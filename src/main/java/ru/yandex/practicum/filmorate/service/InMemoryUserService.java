package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class InMemoryUserService implements UserService{
    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(Integer userId) {
        return null;
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        return null;
    }
}
