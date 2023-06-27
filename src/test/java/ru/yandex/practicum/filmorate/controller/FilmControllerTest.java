package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Должен добавить фильм")
    void shouldCreateFilm() {
        //Act
        Film createdFilm = filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50));
        //Assert
        assertNotNull(createdFilm.getId(), "Объект не был добавлен");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownRuntimeExceptionWhenFilmIsNull() {
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
    void shouldThrownRuntimeExceptionWhenNameIsBlank() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "", "фильммм", LocalDate.of(2000, 1, 1), 50))
        );
        //Assert
        Assertions.assertEquals("Наименование не должно быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownRuntimeExceptionWhenDescriptionIsTooLong() {
        //Arrange
        String description = "Очень длинное описание какого-то очень старого фильма с длинным началом и очень нудным концом. Смотреть не советую, очень нудно и скучно очень долго и так далее и тому подобное 1234556789012345567788900000000";
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", description, LocalDate.of(2000, 1, 1), 50))
        );
        //Assert
        Assertions.assertEquals("Описание не должно превышать 200 символов", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка слишком раннего релиза")
    void shouldThrownRuntimeExceptionWhenReleaseDateTooEarly() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", "description", LocalDate.of(1895, 12, 27), 50))
        );
        //Assert
        Assertions.assertEquals("Дата создания фильма не может быть ранее 28.12.1895", ex.getMessage());
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отрицательной продолжительности фильма")
    void shouldThrownRuntimeExceptionWhenDurationIsNegative() {
        //Act
        ValidationException ex = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(new Film(null, "Очень длинный фильм", "description", LocalDate.of(1895, 12, 28), -1))
        );
        //Assert
        Assertions.assertEquals("Фильм должен иметь положительную продолжительность", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть список из 2 фильмов")
    void shouldGet2Films() {
        //Arrange
        Film createdFilm1 = filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50));
        Film createdFilm2 = filmController.createFilm(new Film(null, "черное солнце джунглей", "фильммм", LocalDate.of(2001, 1, 1), 50));
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
        filmController.createFilm(new Film(null, "синее солнце джунглей", "фильммм", LocalDate.of(2000, 1, 1), 50));
        //Act
        Film updatedFilm = filmController.updateFilm(new Film(1, "синее солнце джунглей upd", "фильммм", LocalDate.of(2002, 1, 1), 55));
        //Assert
        assertArrayEquals(new Film[]{updatedFilm}, filmController.getAllFilms().toArray(), "Возвращен некорректный список фильмов");
    }

    @Test
    @DisplayName("Должна быть выдана ошибка отсутствия фильма")
    void shouldThrownRuntimeExceptionWhenIdIsIncorrect() {
        //Act
        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> filmController.updateFilm(new Film(1, "синее солнце джунглей upd", "фильммм", LocalDate.of(2002, 1, 1), 55))
        );
        //Assert
        Assertions.assertEquals("Данный фильм отсутствует в базе", ex.getMessage());
    }
}