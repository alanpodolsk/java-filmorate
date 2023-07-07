package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreDao;

import java.util.List;

public class DbGenreService implements GenreService{
    private GenreDao genreDao;
    @Override
    public Genre getGenreById(Integer id) {
        return genreDao.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres(Integer id) {
        return genreDao.getAllGenres();
    }
}
