package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage storage;

    @Autowired
    public FilmController(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    @GetMapping
    public List<Film> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return storage.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return storage.update(film);
    }

}
