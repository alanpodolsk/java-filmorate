package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreDao;

import java.util.List;

@AllArgsConstructor
@Service
public class DbGenreService implements GenreService{
    private GenreDao genreDao;
    @Override
    public Genre getGenreById(Integer id) {
        Genre genre = genreDao.getGenreById(id);
        if (genre == null){
            throw new NoObjectException("Жанр с id = "+id+" не найден в базе");
        } else {
            return genre;
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
