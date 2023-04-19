package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();
    private long newId;

    public Optional<Film> findById(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
        return Optional.of(films.get(filmId));
    }

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

    @Override
    public Film addLike(long filmId, long userId) {
        if (findById(filmId).isPresent() && findById(userId).isPresent()) {
            Film film = findById(filmId).get();
            film.getLikes().add(userId);
            log.debug("Пользователь с id " + userId + " поставил лайк фильму с id " + filmId);
            return film;
        } else if (findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        if (findById(filmId).isPresent() && findById(userId).isPresent()) {
            Film film = findById(filmId).get();
            film.getLikes().remove(userId);
            log.debug("Пользователь с id " + userId + " удалил лайк фильму с id " + filmId);
            return film;
        } else if (findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public List<Film> findMostPopularFilms(Integer count) {
        return findAll().stream()
                .sorted((p0, p1) -> (p1.getLikes().size() - p0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
