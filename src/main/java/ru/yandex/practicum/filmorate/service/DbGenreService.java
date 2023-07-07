package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreDao;

import java.util.List;

@AllArgsConstructor
@Service
public class DbGenreService implements GenreService{
    private GenreDao genreDao;
    @Override
    public Genre getGenreById(Integer id) {
        return genreDao.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
