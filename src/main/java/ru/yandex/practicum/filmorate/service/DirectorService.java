package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director addDirector(Director director);

    Director updateDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirector(Integer id);

    void deleteDirector(Integer id);
}
