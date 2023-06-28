package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        user = isValid(user);
        user.setId(generatedId++);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        user = isValid(user);
        if (user.getId() == null || users.get(user.getId()) == null) {
            throw new NoObjectException("Данный пользователь отсутствует в базе");
        }
        users.put(user.getId(), user);
        return user;
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
        return user;
    }
}
