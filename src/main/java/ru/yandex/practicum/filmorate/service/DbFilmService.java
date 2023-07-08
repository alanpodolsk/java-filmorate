package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class DbFilmService implements FilmService {
    private FilmDao filmDao;
    private UserDao userDao;

    @Override
    public Film addFilm(Film film) {
        isValid(film);
        return filmDao.getFilmById(filmDao.addFilm(film).getId());
    }

    @Override
    public Film updateFilm(Film film) {
        isValid(film);
        if (filmDao.getFilmById(film.getId()) == null) {
            throw new NoObjectException("Данный фильм отсутствует в базе");
        } else {
            filmDao.updateFilm(film);
            return filmDao.getFilmById(film.getId());
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        if (filmDao.getFilmById(filmId) == null) {
            throw new NoObjectException("Данный фильм отсутствует в базе");
        } else if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Данный пользователь отсутствует в базе");
        } else {
            filmDao.addLike(filmId, userId);
            return filmDao.getFilmById(filmId);
        }
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        if (filmDao.getFilmById(filmId) == null) {
            throw new NoObjectException("Данный фильм отсутствует в базе");
        } else if (userDao.getUserById(userId) == null) {
            throw new NoObjectException("Данный пользователь отсутствует в базе");
        } else {
            filmDao.deleteLike(filmId, userId);
            return filmDao.getFilmById(filmId);
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }

    @Override
    public Film getFilm(Integer id) {
        Film film = filmDao.getFilmById(id);
        if (film == null) {
            throw new NoObjectException("Фильм с id = " + id + " не найден в базе");
        } else {
            return film;
        }
    }

    private Film isValid(Film film) {
        if (film == null) {
            throw new ValidationException("Передан пустой объект фильма");
        } else if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Наименование не должно быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата создания фильма не может быть ранее 28.12.1895");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Фильм должен иметь положительную продолжительность");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }
}
