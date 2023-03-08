package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    FilmValidator validator = new FilmValidator();
    private int newId;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (validator.isValid(film)) {
            newId++;
            film.setId(newId);
            films.put(film.getId(), film);
            log.debug(film.toString());
            return film;
        } else {
            return null;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (films.containsKey(film.getId()) && validator.isValid(film)) {
            films.put(film.getId(), film);
            log.debug(film.toString());
            return film;
        } else {
            log.warn("Неверный идентификатор");
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

}
