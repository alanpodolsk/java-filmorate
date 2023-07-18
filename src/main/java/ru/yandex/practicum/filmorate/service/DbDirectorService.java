package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorDao;

import java.util.List;

@Service
@AllArgsConstructor
public class DbDirectorService implements DirectorService {
    DirectorDao directorDao;


    @Override
    public Director addDirector(Director director) {
        isValid(director);

        return directorDao.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        isValid(director);

        if (directorDao.getDirectorById(director.getId()) == null) {
            throw new NoObjectException("Данный режиссер отсутствует в базе");
        }

        return directorDao.updateDirector(director);
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorDao.getDirectors();
    }

    @Override
    public Director getDirector(Integer id) {
        Director director = directorDao.getDirectorById(id);
        if (director == null) {
            throw new NoObjectException("Режиссер с id = " + id + " не найден в базе");
        }

        return director;
    }

    @Override
    public void deleteDirector(Integer id) {
        directorDao.deleteDirectorById(id);
    }

    private Director isValid(Director director) {
        if (director == null) {
            throw new ValidationException("Передан пустой объект режиссера");
        } else if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Наименование не должно быть пустым");
        }

        return director;
    }
}
