package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.MPADao;

import java.util.List;

@Service
@AllArgsConstructor
public class DbMPAService implements MPAService {
    private final MPADao mpaDao;


    @Override
    public MPA getMPAById(Integer id) {
        MPA mpa = mpaDao.getMpaById(id);
        if (mpa == null) {
            throw new NoObjectException("Рейтинг с id = " + id + " не найден в базе");
        } else {
            return mpa;
        }
    }

    @Override
    public List<MPA> getAllMPA() {
        return mpaDao.getAllMPA();
    }
}
