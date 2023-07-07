package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    public Genre getGenreById(Integer id);

    public List<Genre> getAllGenres();
}
