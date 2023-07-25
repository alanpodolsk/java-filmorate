package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public Film addFilm(Film film);

    public void deleteFilm(Integer filmId);

    public Film updateFilm(Film film);

    public List<Film> getAllFilms();

    public Film addLike(Integer filmId, Integer userId);

    public Film deleteLike(Integer filmId, Integer userId);

    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    public Film getFilm(Integer id);

    List<Film> getFilmsSearch(String text, List<String> ls);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}