package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();
    private long newId;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        if (film == null) {
            log.warn("Валидация не пройдена: не заполнены поля фильма");
            throw new ValidationException("Валидация не пройдена: не заполнены поля фильма");
        }
        if (!validator.isValid(film)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }
        newId++;
        film.setId(newId);
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм: " + film);
        return film;
    }

    public Film update(Film film) {
        if (film == null) {
            log.warn("Валидация не пройдена: не заполнены поля фильма");
            throw new ValidationException("Валидация не пройдена: не заполнены поля фильма");
        }
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
