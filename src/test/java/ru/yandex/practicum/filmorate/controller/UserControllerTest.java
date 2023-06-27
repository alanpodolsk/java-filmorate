package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Должен быть добавлен пользователь")
    void shouldCreateUser() {
        //Act
        User createdUser = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1)));
        //Assert
        assertNotNull(createdUser.getId(), "Пользователь не добавлен - id отсутствует");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка некорректного email")
    void shouldThrownValidationExceptionWhenEmailIsIncorrect() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "alanpoya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1)))
        );
        //Assert
        Assertions.assertEquals("Некорректно указан email", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка пустого пользователя")
    void shouldThrownValidationExceptionWhenUserIsNull() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(null)
        );
        //Assert
        Assertions.assertEquals("Передан пустой объект пользователя", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка пустого логина")
    void shouldThrownValidationExceptionWhenLoginIsBlank() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "alanpo@ya.ru", "", "alan", LocalDate.of(2000, 1, 1)))
        );
        //Assert
        Assertions.assertEquals("Логин не должен быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка даты рождения")
    void shouldThrownValidationExceptionWhenBirthdayInFuture() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2030, 1, 1)))
        );
        //Assert
        Assertions.assertEquals("Дата рождения не должна быть позднее сегодня", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть список из 2 пользователей")
    void shouldGet2Users() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1)));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1)));
        //Act
        List<User> users = userController.getAllUsers();
        //Assert
        assertArrayEquals(new User[]{createdUser1, createdUser2}, users.toArray(), "Возвращен некорректный список пользователей");
    }

    @Test
    @DisplayName("Должен вернуть пустой список пользователей")
    void shouldGet0Users() {
        //Act
        List<User> users = userController.getAllUsers();
        //Assert
        assertArrayEquals(new User[]{}, users.toArray(), "Возвращен некорректный список пользователей");
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void updateUser() {
        //Arrange
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1)));
        //Act
        userController.updateUser(new User(1, "alanpo@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1)));
        List<User> users = userController.getAllUsers();
        //Assert
        assertArrayEquals(new User[]{new User(1, "alanpo@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1))}, users.toArray(), "Пользователь обновлен некорректно");
    }


    @Test
    @DisplayName("Должна быть выдана отсутствия пользователя")
    void shouldThrownRuntimeExceptionWhenIdIsIncorrect() {
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> userController.updateUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2020, 1, 1)))
        );
        //Assert
        Assertions.assertEquals("Данный пользователь отсутствует в базе", ex.getMessage());
    }
}