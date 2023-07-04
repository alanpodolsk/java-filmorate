package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;


    @BeforeEach
    void setUp() {

        filmController = new FilmController(new InMemoryFilmService(new InMemoryFilmStorage()));
    }

    @Test
    @DisplayName("Должен добавить фильм")
    void shouldCreateFilm() {
        //Act
        Film createdFilm = filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        //Assert
        assertNotNull(createdFilm.getId(), "Объект не был добавлен");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownValidationExceptionWhenFilmIsNull() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(null)
        );
        //Assert
        Assertions.assertEquals("Передан пустой объект фильма", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownValidationExceptionWhenNameIsBlank() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "", "фильммм", LocalDate.of(2000, 1, 1), 50, null))
        );
        //Assert
        Assertions.assertEquals("Наименование не должно быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownValidationExceptionWhenDescriptionIsTooLong() {
        //Arrange
        String description = "Очень длинное описание какого-то очень старого фильма с длинным началом и очень нудным концом. Смотреть не советую, очень нудно и скучно очень долго и так далее и тому подобное 1234556789012345567788900000000";
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", description, LocalDate.of(2000, 1, 1), 50, null))
        );
        //Assert
        Assertions.assertEquals("Описание не должно превышать 200 символов", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка слишком раннего релиза")
    void shouldThrownValidationExceptionWhenReleaseDateTooEarly() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", "description", LocalDate.of(1895, 12, 27), 50, null))
        );
        //Assert
        Assertions.assertEquals("Дата создания фильма не может быть ранее 28.12.1895", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отрицательной продолжительности фильма")
    void shouldThrownValidationExceptionWhenDurationIsNegative() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", "description", LocalDate.of(1895, 12, 28), -1, null))
        );
        //Assert
        Assertions.assertEquals("Фильм должен иметь положительную продолжительность", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть список из 2 фильмов")
    void shouldGet2Films() {
        //Arrange
        Film createdFilm1 = filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        Film createdFilm2 = filmController.createFilm(new Film(null, "черное солнце джунглей", "фильммм", LocalDate.of(2001, 1, 1), 50, null));
        //Act
        List<Film> films = filmController.getAllFilms();
        //Assert
        assertArrayEquals(new Film[]{createdFilm1, createdFilm2}, films.toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Должен вернуть список из 0 фильмов")
    void shouldGet0Films() {
        //Act
        List<Film> films = filmController.getAllFilms();
        //Assert
        assertArrayEquals(new Film[]{}, films.toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Должен обновить фильм")
    void shouldUpdateFilm() {
        //Arrange
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        //Act
        Film updatedFilm = filmController.updateFilm(new Film(1, "синее солнце джунглей upd", "фильммм", LocalDate.of(2002, 1, 1), 55, new HashSet<>()));
        //Assert
        assertArrayEquals(new Film[]{updatedFilm}, filmController.getAllFilms().toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownNoObjectExceptionWhenIdIsIncorrect() {
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> filmController.updateFilm(new Film(1, "синее солнце джунглей upd", "фильммм", LocalDate.of(2002, 1, 1), 55, null))
        );
        //Assert
        Assertions.assertEquals("Данный фильм отсутствует в базе", ex.getMessage());
    }

    @Test
    @DisplayName("Должен поставить лайк фильму")
    void shouldAddLikeToFilm() {
        //Arrange
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        //Act
        filmController.addLike(1, 1);
        //Assert
        assertArrayEquals(new Integer[]{1}, filmController.getFilm(1).getLikes().toArray(), "Лайк не установлен");
    }

    @Test
    @DisplayName("Должен снять лайк с фильма")
    void shouldDeleteLikeFromFilm() {
        //Arrange
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        filmController.addLike(1, 1);
        assertArrayEquals(new Integer[]{1}, filmController.getFilm(1).getLikes().toArray(), "Лайк не установлен");
        //Act
        filmController.deleteLike(1, 1);
        //Assert
        assertArrayEquals(new Integer[]{}, filmController.getFilm(1).getLikes().toArray(), "Лайк не удалён");
    }

    @Test
    @DisplayName("Должен вернуть фильмы согласно количеству лайков")
    void shouldReturn2FilmsOrderedByLikesCountDesc() {
        //Arrange
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        filmController.createFilm(new Film(null, "черное солнце джунглей", "фильммм", LocalDate.of(2001, 1, 1), 50, null));
        filmController.addLike(2, 1);
        filmController.addLike(2, 3);
        filmController.addLike(1, 1);
        filmController.addLike(1, 1);
        //Act
        filmController.addLike(1, 1);
        //Assert
        assertArrayEquals(new Film[]{filmController.getFilm(2), filmController.getFilm(1)}, filmController.getPopularFilms(10).toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Добавление лайка - должна быть выдана ошибка отсутствия фильма")
    void shouldThrownNoObjectExceptionInAddLikeWhenFilmIdIsIncorrect() {
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> filmController.addLike(1, 1)
        );
        //Assert
        Assertions.assertEquals("Данный фильм отсутствует в базе", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление лайка - должна быть выдана ошибка отсутствия фильма")
    void shouldThrownNoObjectExceptionInDeleteLikeWhenFilmIdIsIncorrect() {
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> filmController.deleteLike(1, 1)
        );
        //Assert
        Assertions.assertEquals("Данный фильм отсутствует в базе", ex.getMessage());
    }

    @Test
    @DisplayName("Удаление лайка - должна быть выдана ошибка отсутствия лайка")
    void shouldThrownNoObjectExceptionInDeleteLikeWhenLikesIsNotExist() {
        //Arrange
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50, null));
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> filmController.deleteLike(1, 1)
        );
        //Assert
        Assertions.assertEquals("Данный пользователь не ставил лайк", ex.getMessage());
    }
}