package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

public interface DirectorDao {
    Director addDirector(Director director);

    Director updateDirector(Director director);

    List<Director> getDirectors();

    Director getDirectorById(Integer id);

    void deleteDirectorById(Integer id);
}
