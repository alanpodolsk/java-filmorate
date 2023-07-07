package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.MPADao;

import java.util.List;

@Service
@AllArgsConstructor
public class DbMPAService implements MPAService {
    private final MPADao mpaDao;


    @Override
    public MPA getMPAById(Integer id) {
        return mpaDao.getMpaById(id);
    }

    @Override
    public List<MPA> getAllMPA() {
        return mpaDao.getAllMPA();
    }
}
