package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;

    @Override
    public Film addFilm(Film film){
        isValid(film);
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm (Film film){
        isValid(film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms(){
        return filmStorage.getAllFilms();
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        Set<Integer> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        filmStorage.updateFilm(film);
        return null;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        Set<Integer> likes = film.getLikes();
        if (likes.contains(userId)){
            likes.remove(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        } else {
            throw new NoObjectException("Данный пользователь не ставил лайк");
        }
        return null;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }

    private void isValid(Film film) {
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
    }
}
