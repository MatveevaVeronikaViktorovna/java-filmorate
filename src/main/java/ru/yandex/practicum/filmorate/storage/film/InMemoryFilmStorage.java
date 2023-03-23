package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    FilmValidator validator = new FilmValidator();
    private long newId;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        if (validator.isValid(film)) {
            newId++;
            film.setId(newId);
            films.put(film.getId(), film);
            log.debug("Добавлен новый фильм: " + film);
            return film;
        } else {
            return null;
        }
    }

    public Film update(Film film) {
        if (films.containsKey(film.getId()) && validator.isValid(film)) {
            films.put(film.getId(), film);
            log.debug("Фильм с id " + film.getId() + " обновлен: " + film);
            return film;
        } else {
            log.warn("Фильм с id " + film.getId() + " не найден");
            throw new InvalidIdException("Фильм с id " + film.getId() + " не найден");
        }
    }

    public Optional<Film> findById(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
        return Optional.of(films.get(filmId));
    }

}
