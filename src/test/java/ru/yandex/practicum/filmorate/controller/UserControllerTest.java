package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;


    @BeforeEach
    public void setUp() {
        userController = new UserController(new InMemoryUserService(new InMemoryUserStorage()));
    }

    @Test
    @DisplayName("Должен быть добавлен пользователь")
    void shouldCreateUser() {
        //Act
        User createdUser = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Assert
        assertNotNull(createdUser.getId(), "Пользователь не добавлен - id отсутствует");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка некорректного email")
    void shouldThrownValidationExceptionWhenEmailIsIncorrect() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "alanpoya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null))
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
                () -> userController.createUser(new User(null, "alanpo@ya.ru", "", "alan", LocalDate.of(2000, 1, 1), null))
        );
        //Assert
        Assertions.assertEquals("Логин не должен быть пустым или содержать пробелы", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка даты рождения")
    void shouldThrownValidationExceptionWhenBirthdayInFuture() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2030, 1, 1), null))
        );
        //Assert
        Assertions.assertEquals("Дата рождения не должна быть позднее сегодня", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть список из 2 пользователей")
    void shouldGet2Users() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
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
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Act
        userController.updateUser(new User(1, "alanpo@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1), null));
        List<User> users = userController.getAllUsers();
        //Assert
        assertArrayEquals(new User[]{new User(1, "alanpo@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1), new HashSet<Integer>())}, users.toArray(), "Пользователь обновлен некорректно");
    }


    @Test
    @DisplayName("Должна быть выдана отсутствия пользователя")
    void shouldThrownNoObjectExceptionWhenIdIsIncorrect() {
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.updateUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2020, 1, 1), null))
        );
        //Assert
        Assertions.assertEquals("Данный пользователь отсутствует в базе", ex.getMessage());
    }

    @Test
    @DisplayName("Должен добавить друга")
    void shouldAddFriend() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        assertArrayEquals(new User[]{createdUser1, createdUser2}, userController.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
        //Act
        userController.addFriend(1, 2);
        //Assert
        assertArrayEquals(new Integer[]{2}, userController.getUser(1).getFriends().toArray(), "Не указан друг с id 2 у пользователя с id 1");
        assertArrayEquals(new Integer[]{1}, userController.getUser(2).getFriends().toArray(), "Не указан друг с id 1 у пользователя с id 2");
    }

    @Test
    @DisplayName("Должен добавить друга")
    void shouldDeleteFriend() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        assertArrayEquals(new User[]{createdUser1, createdUser2}, userController.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
        userController.addFriend(1, 2);
        assertArrayEquals(new Integer[]{2}, userController.getUser(1).getFriends().toArray(), "Не указан друг с id 2 у пользователя с id 1");
        assertArrayEquals(new Integer[]{1}, userController.getUser(2).getFriends().toArray(), "Не указан друг с id 1 у пользователя с id 2");
        //Act
        userController.deleteFriend(1, 2);
        //Assert
        assertArrayEquals(new Integer[]{}, userController.getUser(1).getFriends().toArray(), "Не удален друг у пользователя с id 1");
        assertArrayEquals(new Integer[]{}, userController.getUser(2).getFriends().toArray(), "Не удален друг у пользователя с id 2");
    }

    @Test
    @DisplayName("Должен вывести список друзей")
    void shouldGetFriendsN2AndN3() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        User createdUser3 = userController.createUser(new User(null, "alanpi@ya.ru", "alanpi", null, LocalDate.of(2002, 1, 1), null));
        assertArrayEquals(new User[]{createdUser1, createdUser2, createdUser3}, userController.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
        //Act
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        //Assert
        assertArrayEquals(new User[]{userController.getUser(2), userController.getUser(3)}, userController.getFriends(1).toArray(), "Не указан друг с id 2 у пользователя с id 1");
    }

    @Test
    @DisplayName("Должен вывести список общих друзей для N1 и N3")
    void shouldReturnMutualFriendN2() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        User createdUser3 = userController.createUser(new User(null, "alanpi@ya.ru", "alanpi", null, LocalDate.of(2002, 1, 1), null));
        assertArrayEquals(new User[]{createdUser1, createdUser2, createdUser3}, userController.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
        //Act
        userController.addFriend(1, 2);
        userController.addFriend(2, 3);
        userController.addFriend(1, 3);
        //Assert
        assertArrayEquals(new User[]{userController.getUser(2)}, userController.getMutualFriends(1, 3).toArray(), "Не указан общий друг с id 2");
    }

    @Test
    @DisplayName("Должен вывести пустой список общих друзей для N1 и N3")
    void shouldReturnEmptyMutualFriendsList() {
        //Arrange
        User createdUser1 = userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        User createdUser2 = userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        User createdUser3 = userController.createUser(new User(null, "alanpi@ya.ru", "alanpi", null, LocalDate.of(2002, 1, 1), null));
        assertArrayEquals(new User[]{createdUser1, createdUser2, createdUser3}, userController.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
        //Act
        userController.addFriend(1, 2);
        userController.addFriend(2, 3);
        //Assert
        assertArrayEquals(new User[]{}, userController.getMutualFriends(1, 2).toArray(), "Не должно быть общих друзей");
    }

    @Test
    @DisplayName("Добавление друга - должна быть выдана ошибка отсутствия друга с таким id")
    void shouldThrownNoObjectExceptionWhenFriendIdIsIncorrect() {
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.addFriend(1, 4)
        );
        //Assert
        Assertions.assertEquals("Пользователь с ID =4 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Добавление друга - должна быть выдана ошибка отсутствия основного пользователя с таким id")
    void shouldThrownNoObjectExceptionOnAddFriendWhenUserIdIsIncorrect() {
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.addFriend(3, 1)
        );
        //Assert
        Assertions.assertEquals("Пользователь с ID =3 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление друга - должна быть выдана ошибка отсутствия друга с таким id")
    void shouldThrownNoObjectExceptionOnDeleteFriendWhenFriendIdIsIncorrect() {
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.deleteFriend(1, 3)
        );
        //Assert
        Assertions.assertEquals("Пользователь с ID =3 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление друга - должна быть выдана ошибка отсутствия основного пользователя с таким id")
    void shouldThrownNoObjectExceptionOnDeleteFriendWhenUserIdIsIncorrect() {
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.deleteFriend(3, 1)
        );
        //Assert
        Assertions.assertEquals("Пользователь с ID =3 не найден", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление друга - должна быть выдана ошибка отсутствия удаляемого пользователя в списке друзей")
    void shouldThrownNoObjectExceptionOnDeleteFriendWhenUserIdIsNotInFriendList() {
        userController.createUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), null));
        userController.createUser(new User(null, "alanpu@ya.ru", "alanpu", null, LocalDate.of(2002, 1, 1), null));
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.deleteFriend(1, 2)
        );
        //Assert
        Assertions.assertEquals("Данный пользователь не был в друзьях", ex.getMessage());
    }

    @Test
    @DisplayName("Получить список друзей - должна быть выдана ошибка отсутствия пользователя")
    void shouldThrownNoObjectExceptionInGetFriendsWhereUserIsNotFound() {
        //Act
        NoObjectException ex = Assertions.assertThrows(
                NoObjectException.class,
                () -> userController.getFriends(1)
        );
        //Assert
        Assertions.assertEquals("Пользователь с ID =1 не найден", ex.getMessage());
    }
}