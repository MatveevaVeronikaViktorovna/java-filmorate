package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private final UserStorage userStorage;
    private final FilmValidator validator = new FilmValidator();

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeDbStorage likeDbStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
        this.userStorage = userStorage;
    }

    public Film findById(Long filmId) {
        if (filmStorage.findById(filmId).isPresent()) {
            return filmStorage.findById(filmId).get();
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
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
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film == null) {
            log.warn("Валидация не пройдена: не заполнены поля фильма");
            throw new ValidationException("Валидация не пройдена: не заполнены поля фильма");
        }
        if (!validator.isValid(film)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }
        if (filmStorage.findById(film.getId()).isPresent()) {
            return filmStorage.update(film);
        } else {
            log.warn("Фильм с id " + film.getId() + " не найден");
            throw new InvalidIdException("Фильм с id " + film.getId() + " не найден");
        }
    }

    public Film addLike(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isPresent() && userStorage.findById(userId).isPresent()) {
            likeDbStorage.addLike(filmId, userId);
            return filmStorage.findById(filmId).get();
        } else if (filmStorage.findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isPresent() && userStorage.findById(userId).isPresent()) {
            likeDbStorage.deleteLike(filmId, userId);
            return filmStorage.findById(filmId).get();
        } else if (filmStorage.findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    public List<Film> findMostPopularFilms(Integer count) {
        return filmStorage.findMostPopularFilms(count);
    }

}
