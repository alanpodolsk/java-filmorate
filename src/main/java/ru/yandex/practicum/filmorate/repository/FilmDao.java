package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    public Integer addFilm(Film film);

    public Integer updateFilm(Film film);

    public List<Film> getAllFilms();

    public Film getFilmById(Integer id);

    public List<Integer> getLikes(Integer filmId);

    public void addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);
}
