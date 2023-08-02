package ru.yandex.practicum.filmorate.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.DirectorDao;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DbFilmServiceTest {

    private DbFilmService dbFilmService;
    private final EasyRandom generator = new EasyRandom();

    @Mock
    UserDao userDao;
    @Mock
    FilmDao filmDao;
    @Mock
    DirectorDao directorDao;

    @BeforeEach
    public void setUp(){
        dbFilmService = new DbFilmService(filmDao,userDao,directorDao);
    }

    @Test
    @DisplayName("Должен добавить фильм")
    public void shouldAddFilmToDb(){
        //Arrange
        Film film = generator.nextObject(Film.class);
        film.setId(null);
        when(filmDao.getFilmById(Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    film.setId(1);
                    return film;
                });
        when(filmDao.addFilm(Mockito.any(Film.class)))
                .thenAnswer(invocationOnMock -> {
                    film.setId(1);
                    return film;
                });
        //Act
        Film addedFilm = dbFilmService.addFilm(film);
        //Assert
        assertNotNull(addedFilm,"Метод не вернул сохраненный фильм");
        assertEquals(1,addedFilm.getId(),"Возвращен фильм с некорректным ID");
    }
    @Test
    @DisplayName("Должен отказать в добавлении фильма - пустой объект фильма")
    public void shouldThrownValidationExceptionOnNullFilm(){
        //Act
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> dbFilmService.addFilm(null)
        );
        //Assert
        assertEquals("Передан пустой объект фильма", e.getMessage());
    }
    @Test
    @DisplayName("Должен отказать в добавлении фильма - пустое имя")
    public void shouldThrownValidationExceptionOnNullFilmName(){
        //Arrange
        Film film = generator.nextObject(Film.class);
        film.setId(null);
        film.setName("");
        //Act
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> dbFilmService.addFilm(film)
        );
        //Assert
        assertEquals("Наименование не должно быть пустым", e.getMessage());
    }
    @Test
    @DisplayName("Должен отказать в добавлении фильма - слишком длинное описание")
    public void shouldThrownValidationExceptionOnTooLongFilmDescription(){
        //Arrange
        Film film = generator.nextObject(Film.class);
        film.setId(null);
        film.setDescription(new String("*").repeat(201));
        //Act
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> dbFilmService.addFilm(film)
        );
        //Assert
        assertEquals("Описание не должно превышать 200 символов", e.getMessage());
    }
    @Test
    @DisplayName("Должен отказать в добавлении фильма - слишком длинное описание")
    public void shouldThrownValidationExceptionOnTooEarlyCreationDate(){
        //Arrange
        Film film = generator.nextObject(Film.class);
        film.setId(null);
        film.setName("*");
        film.setDescription(new String("*").repeat(200));
        film.setReleaseDate(LocalDate.of(1895,12,27));
        //Act
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> dbFilmService.addFilm(film)
        );
        //Assert
        assertEquals("Дата создания фильма не может быть ранее 28.12.1895", e.getMessage());
    }
    @Test
    @DisplayName("Должен отказать в добавлении фильма - слишком короткий")
    public void shouldThrownValidationExceptionOnNoPositiveDuration(){
        //Arrange
        Film film = generator.nextObject(Film.class);
        film.setId(null);
        film.setName("*");
        film.setDescription(new String("*").repeat(200));
        film.setReleaseDate(LocalDate.of(1895,12,28));
        film.setDuration(0);
        //Act
        ValidationException e = assertThrows(
                ValidationException.class,
                () -> dbFilmService.addFilm(film)
        );
        //Assert
        assertEquals("Фильм должен иметь положительную продолжительность", e.getMessage());
    }




}