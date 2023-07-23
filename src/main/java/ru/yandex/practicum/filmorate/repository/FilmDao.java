package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {
    public Film addFilm(Film film);

    public void deleteFilm(Integer filmId);

    public Film updateFilm(Film film);

    public List<Film> getAllFilms();

    public Film getFilmById(Integer id);

    public List<Integer> getLikes(Integer filmId);

    public void addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);

    public List<Film> getPopularFilms(Integer count);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    List<Film> getFilmsSearch(String text, List<String> ls);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
  
    public List<Film> getRecomendFilms(Integer id, Integer sameUserId);
}
