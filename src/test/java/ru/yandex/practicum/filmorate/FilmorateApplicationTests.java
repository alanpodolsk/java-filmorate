package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDao userDao;
    private final FilmDao filmDao;
    private final GenreDao genreDao;
    private final MPADao mpaDao;

    private final DirectorDao directorDao;

    Director director1 = new Director(null, "Director");

    @Test
    @DisplayName("Должен вернуть пустой список пользователей")
    public void shouldReturn0Users() {
        //Assert
        List<User> users = userDao.getAllUsers();
        assertArrayEquals(new User[]{}, users.toArray(), "Возвращен непустой список пользователей");
    }


    @Test
    @DisplayName("Должен добавить пользователя в базу данных")
    void shouldAddUserInDB() {
        //Act
        User user = userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        //Assert
        Assertions.assertEquals(1, user.getId(), "Пользователь сформирован с некорректным id");
    }

    @Test
    @DisplayName("Должен вернуть пользователя с id = 1 из базы данных")
    void shouldGetUserId1() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        //Act
        User user = userDao.getUserById(1);
        //Assert
        Assertions.assertEquals(1, user.getId(), "Возвращен пользователь с некорректным id");
    }

    @Test
    @DisplayName("Должен вернуть обновленного пользователя с id = 1 из базы данных")
    void shouldUpdateUserId1() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        //Act
        User user = userDao.updateUser(new User(1, "alanpi@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        //Assert
        Assertions.assertEquals(1, user.getId(), "Возвращен пользователь с некорректным id");
        Assertions.assertEquals("alanpi@ya.ru", user.getEmail(), "Email не обновлен");
    }

    @Test
    @DisplayName("Должен вернуть двух пользователей")
    public void shouldReturn2Users() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2002, 1, 1), new HashSet<>()));
        User user1 = userDao.getUserById(1);
        User user2 = userDao.getUserById(2);
        //Assert
        assertArrayEquals(new User[]{user1, user2}, userDao.getAllUsers().toArray(), "Возвращен некорректный список пользователей");
    }

    @Test
    @DisplayName("Должен создать дружбу 1 - 2 и вернуть друга 2")
    public void shouldAddFriend2to1() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2002, 1, 1), new HashSet<>()));
        //Act
        userDao.addFriend(1, 2);
        //Assert
        assertArrayEquals(new User[]{userDao.getUserById(2)}, userDao.getFriendsById(1).toArray(), "Возвращен некорректный список друзей");
    }

    @Test
    @DisplayName("Должен вернуть общего друга id 3")
    public void shouldReturnMutualFriend3() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2002, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpq@ya.ru", "alanpq", "alan", LocalDate.of(2004, 1, 1), new HashSet<>()));
        userDao.addFriend(2, 3);
        userDao.addFriend(1, 3);
        //Assert
        assertArrayEquals(new User[]{userDao.getUserById(3)}, userDao.getMutualFriends(1, 2).toArray(), "Возвращен некорректный список общих друзей");
    }

    @Test
    @DisplayName("Должен удалить друга у пользователя 1")
    public void shouldDeleteFriend3FromUser2() {
        //Arrange
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2002, 1, 1), new HashSet<>()));
        userDao.addFriend(1, 2);
        assertArrayEquals(new User[]{userDao.getUserById(2)}, userDao.getFriendsById(1).toArray(), "Возвращен некорректный список друзей");
        userDao.deleteFriend(1, 2);
        //Assert
        assertArrayEquals(new User[]{}, userDao.getFriendsById(1).toArray(), "Возвращен некорректный список друзей");
    }

    @Test
    @DisplayName("Должен создать фильм с id 1")
    public void shouldAddFilmId1() {
        //Act
        Film film = filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        //Assert
        Assertions.assertEquals(1, film.getId(), "Фильм сформирован с некорректным id");
    }

    @Test
    @DisplayName("Должен вернуть фильм с id 1")
    public void shouldGetFilmId1() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        //Act
        Film film = filmDao.getFilmById(1);
        //Assert
        Assertions.assertEquals(1, film.getId(), "Возвращен фильм с некорректным id");
    }

    @Test
    @DisplayName("Должен не вернуть ничего")
    public void shouldReturnNull() {
        Film film = filmDao.getFilmById(1);
        //Assert
        Assertions.assertNull(film, "Возвращен фильм");
    }

    @Test
    @DisplayName("Должен вернуть фильм с id 1")
    public void shouldUpdateFilmId1() {
        //Arrange
        filmDao.addFilm(new Film(null, "голубое солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        //Act
        filmDao.updateFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        //Assert
        Film film = filmDao.getFilmById(1);
        Assertions.assertEquals("голубое солнце джунглей", film.getName(), "Имя фильма не изменено");
    }

    @Test
    @DisplayName("Должен пустой список фильмов")
    public void shouldReturn0Films() {
        //Assert
        assertArrayEquals(new Film[]{}, filmDao.getAllFilms().toArray(), "Возвращен непустой список фильмов");
    }

    @Test
    @DisplayName("Должен вернуть список из 2 фильмов")
    public void shouldReturn2Films() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        Film film1 = filmDao.getFilmById(1);
        Film film2 = filmDao.getFilmById(2);
        //Assert
        assertArrayEquals(new Film[]{film1, film2}, filmDao.getAllFilms().toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Должен добавить лайк фильму 1")
    public void shouldAddLikeToFilm1() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        //Act
        filmDao.addLike(1, 1);
        //Assert
        assertArrayEquals(new Integer[]{1}, filmDao.getFilmById(1).getLikes().toArray(), "Возвращен некорректный список лайков");
    }

    @Test
    @DisplayName("Должен удалить лайк с фильма 1")
    public void shouldDeleteLikeToFilm1() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        filmDao.addLike(1, 1);
        assertArrayEquals(new Integer[]{1}, filmDao.getFilmById(1).getLikes().toArray(), "Возвращен некорректный список лайков");
        //Act
        filmDao.deleteLike(1, 1);
        //Assert
        assertArrayEquals(new Integer[]{}, filmDao.getFilmById(1).getLikes().toArray(), "Возвращен некорректный список лайков");
    }

    @Test
    @DisplayName("Должен вернуть список из 2 лайков")
    public void shouldReturn2Likes() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        filmDao.addLike(1, 2);
        filmDao.addLike(1, 1);
        //Assert
        assertArrayEquals(new Integer[]{2, 1}, filmDao.getLikes(1).toArray(), "Возвращен некорректный список лайков");
    }

    @Test
    @DisplayName("Должен вернуть упорядоченный по популярности список")
    public void shouldReturnFilmsOrderedByLikes() {
        //Arrange
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        filmDao.addFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, null, new MPA(1, null)));
        userDao.addUser(new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        userDao.addUser(new User(null, "alanpu@ya.ru", "alanpu", "alan", LocalDate.of(2000, 1, 1), new HashSet<>()));
        filmDao.addLike(1, 2);
        filmDao.addLike(1, 1);
        //Assert
        assertArrayEquals(new Film[]{filmDao.getFilmById(2), filmDao.getFilmById(1)}, filmDao.getPopularFilms(10).toArray(), "Возвращен некорректный список популярных фильмов");
    }

    @Test
    @DisplayName("Должен вернуть список жанров")
    public void shouldReturnAllGenres() {
        //Assert
        Assertions.assertEquals(6, genreDao.getAllGenres().size(), "Возвращено некорректное количество жанров");
    }

    @Test
    @DisplayName("Должен вернуть жанр id 3")
    public void shouldReturnGenre3() {
        //Assert
        Assertions.assertEquals("Мультфильм", genreDao.getGenreById(3).getName(), "Возвращен некорректный жанр");
    }

    @Test
    @DisplayName("Должен не вернуть ничего - некорректный id жанра")
    public void shouldReturnNullGenre() {
        //Assert
        Assertions.assertNull(genreDao.getGenreById(9999), "Возвращен жанр");
    }

    @Test
    @DisplayName("Должен вернуть перечень MPA")
    public void shouldReturnAllMPA() {
        //Assert
        Assertions.assertEquals(5, mpaDao.getAllMPA().size(), "Возвращен некорректный список MPA");
    }

    @Test
    @DisplayName("Должен не вернуть ничего - некорректный id MPA")
    public void shouldReturnNullMPA() {
        //Assert
        Assertions.assertNull(mpaDao.getMpaById(9999), "Возвращен рейтинг МРА");
    }

    @Test
    @DisplayName("Должен вернуть МРА NC17 (id 5)")
    public void shouldReturnNC17() {
        //Assert
        Assertions.assertEquals("NC-17", mpaDao.getMpaById(5).getName(), "Возвращен некорректный рейтинг МРА");
    }

    @Test
    @DisplayName("Должен добавить режиссера")
    public void shouldAddDirector() {
        // Act
        Director storedDirector = directorDao.addDirector(director1);
        // Assert
        Assertions.assertNotNull(storedDirector.getId(), "Возвращенный режиссер не имеет id");
        Assertions.assertEquals(storedDirector, directorDao.getDirectorById(storedDirector.getId()), "Возвращен некорректный режиссер");
    }

    @Test
    @DisplayName("Должен обновить режиссера")
    public void shouldUpdateDirector() {
        // Arrange
        Director storedDirector = directorDao.addDirector(director1);
        Director director2 = new Director(storedDirector.getId(), "Updated");
        // Act
        director2 = directorDao.updateDirector(director2);
        // Assert
        Assertions.assertNotEquals(storedDirector, director2, "Режиссер не был обновлен");
        Assertions.assertEquals(director2.getName(), "Updated", "Имя режиссера не было обновлено");
    }

    @Test
    @DisplayName("Должен вернуть 2 режиссеров после добавления")
    public void shouldReturn2DirectorsInGetAll() {
        // Arrange
        directorDao.addDirector(director1);
        directorDao.addDirector(new Director(null, "Director 2"));
        // Act
        List<Director> directors = directorDao.getDirectors();
        // Assert
        Assertions.assertEquals(directors.size(), 2, "Недостаточное количество режиссеров в списке");
    }

    @Test
    @DisplayName("Должен вернуть null при поиске по несуществующему id")
    public void shouldReturnNullOnNoExistingId() {
        // Act
        Director director = directorDao.getDirectorById(34);
        // Assert
        Assertions.assertNull(director, "Режиссер не должен существовать в базе");
    }

    @Test
    @DisplayName("Должен удалить добавленного режиссера")
    public void shouldDeleteDirector() {
        // Arrange
        Director storedDirector = directorDao.addDirector(director1);

        storedDirector = directorDao.getDirectorById(storedDirector.getId());
        Assertions.assertNotNull(storedDirector, "Режиссер не был добавлен в базу");
        // Act
        directorDao.deleteDirectorById(storedDirector.getId());
        // Assert
        Assertions.assertNull(directorDao.getDirectorById(storedDirector.getId()), "Режиссер должен быть удален");
    }

    @Test
    @DisplayName("Фильм должен получить режиссера в Post films")
    public void shouldAddDirectorToFilm() {
        // Arrange
        Director storedDirector = directorDao.addDirector(director1);
        Film film = new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, new HashSet<>(), new MPA(1, null));
        film.getDirectors().add(storedDirector);
        // Act
        Film storedFilm = filmDao.addFilm(film);
        storedFilm = filmDao.getFilmById(storedFilm.getId());
        // Assert
        Assertions.assertEquals(storedFilm.getDirectors().size(), 1, "Режиссер не был добавлен к фильму");
    }

    @Test
    @DisplayName("Должен вывести список фильмов режиссера по возрастанию года")
    public void shouldReturnFilmListOfDirectorYearAscending() {
        // Arrange
        Director storedDirector = directorDao.addDirector(director1);

        Film film1 = new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, new HashSet<>(), new MPA(1, null));
        Film film2 = new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, new HashSet<>(), new MPA(1, null));
        film1.getDirectors().add(storedDirector);
        film2.getDirectors().add(storedDirector);
        film1 = filmDao.addFilm(film1);
        film2 = filmDao.addFilm(film2);
        // Act
        List<Film> films = filmDao.getFilmsByDirector(storedDirector.getId(), "year");
        // Assert
        Assertions.assertEquals(film1.getReleaseDate(), films.get(0).getReleaseDate(), "Фильм с годом 2000 должен быть первым");
        Assertions.assertEquals(film2.getReleaseDate(), films.get(1).getReleaseDate(), "Фильм с годом 2010 должен быть вторым");
    }

    @Test
    @DisplayName("Должен вывести список фильмов режиссера по убыванию лайков")
    public void shouldReturnFilmListOfDirectorLikeDescending() {
        // Arrange
        Director storedDirector = directorDao.addDirector(director1);

        Film film1 = new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, new HashSet<>(), new MPA(1, null));
        Film film2 = new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null, null, new HashSet<>(), new MPA(1, null));
        film1.getDirectors().add(storedDirector);
        film2.getDirectors().add(storedDirector);
        film1 = filmDao.addFilm(film1);
        film2 = filmDao.addFilm(film2);

        User user1 = new User(null, "alanpo@ya.ru", "alanpo", "alan", LocalDate.of(2000, 1, 1), new HashSet<>());
        User user2 = new User(null, "alanpo2@ya.ru", "alanpo2", "alan2", LocalDate.of(2090, 1, 1), new HashSet<>());
        user1 = userDao.addUser(user1);
        user2 = userDao.addUser(user2);

        filmDao.addLike(film1.getId(), user1.getId());
        filmDao.addLike(film2.getId(), user1.getId());
        filmDao.addLike(film2.getId(), user2.getId());
        // Act
        List<Film> films = filmDao.getFilmsByDirector(storedDirector.getId(), "likes");
        // Assert
        Assertions.assertEquals(film2.getReleaseDate(), films.get(0).getReleaseDate(), "Фильм 2 должен быть первым (2 лайка)");
        Assertions.assertEquals(film1.getReleaseDate(), films.get(1).getReleaseDate(), "Фильм 1 должен быть вторым (1 лайк)");
    }
}