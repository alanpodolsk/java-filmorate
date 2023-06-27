package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int generatedId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        isValid(user);
        user.setId(generatedId++);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        isValid(user);
        if (user.getId() == null || users.get(user.getId()) == null) {
            throw new RuntimeException("Данный пользователь отсутствует в базе");
        }
        users.put(user.getId(), user);
        return user;
    }

    private void isValid(User user) {
        if (user == null) {
            throw new ValidationException("Передан пустой объект пользователя");
        } else if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не должна быть позднее сегодня");
        } else if (user.getEmail().contains("@") == false) {
            throw new ValidationException("Некорректно указан email");
        }
    }
}
