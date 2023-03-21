package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    FilmValidator validator = new FilmValidator();
    private int newId;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
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

    public Film update(Film film) {
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
