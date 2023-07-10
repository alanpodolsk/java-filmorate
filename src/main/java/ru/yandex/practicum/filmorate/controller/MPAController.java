package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MPAController {

    private final MPAService mpaService;

    @GetMapping
    public List<MPA> getAllMPA() {
        return mpaService.getAllMPA();
    }

    @GetMapping("/{id}")
    public MPA getMPAById(@PathVariable Integer id) {
        return mpaService.getMPAById(id);
    }
}
