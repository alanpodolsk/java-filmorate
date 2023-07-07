package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAService {
    public MPA getMPAById(Integer id);
    public List<MPA> getAllMPA();
}
