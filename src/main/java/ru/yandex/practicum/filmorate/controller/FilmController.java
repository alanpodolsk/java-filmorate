package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;


@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private FilmService filmService;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
       return filmService.getAllFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId){
        return filmService.addLike(id,userId);
    }
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId){
        return filmService.deleteLike(id,userId);
    }
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam Integer count){
        return filmService.getPopularFilms(count);
    }
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id){
        return filmService.getFilm(id);
    }
}

