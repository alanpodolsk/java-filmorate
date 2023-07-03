package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/films")

public class FilmController {
    private FilmStorage filmStorage;
    private InMemoryFilmService inMemoryFilmService;

    @Autowired
    public FilmController (FilmStorage filmStorage, InMemoryFilmService inMemoryFilmService){
        this.filmStorage = filmStorage;
        this.inMemoryFilmService = inMemoryFilmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {

    }

    @GetMapping
    public List<Film> getAllFilms() {
        ;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        isValid(film);
        if (film.getId() == null || films.get(film.getId()) == null) {
            throw new NoObjectException("Данный фильм отсутствует в базе");
        }
        films.put(film.getId(), film);
        return film;
    }


}

