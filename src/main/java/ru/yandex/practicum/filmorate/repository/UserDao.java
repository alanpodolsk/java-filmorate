package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    public User getUserById(Integer id);
    public List<User> getAllUsers();
    public Integer updateUser(User user);
    public void addFriend(Integer userId, Integer friendId);
    public void deleteFriend(Integer userId, Integer friendId);
    public Integer addUser(User user);
    public List<User> getFriendsById(Integer id);
    public List<User> getMutualFriends(Integer userId, Integer otherUserId);
}
