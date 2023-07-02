package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm (Film film);

    public List<Film> getAllFilms();

    public Film getFilm();
}
