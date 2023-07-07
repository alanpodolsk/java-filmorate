package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPADao {
    public MPA getMpaById(Integer id);
    public List<MPA> getAllMPA();
}
