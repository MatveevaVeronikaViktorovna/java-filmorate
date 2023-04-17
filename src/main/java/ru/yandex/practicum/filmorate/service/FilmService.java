package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired

    public FilmService(@Qualifier("filmDbStorage")FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
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
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }




    public Film addLike(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isPresent() && userStorage.findById(userId).isPresent()) {
            Film film = filmStorage.findById(filmId).get();
      //      film.getLikes().add(userId);
            log.debug("Пользователь с id " + userId + " поставил лайк фильму с id " + filmId);
            return film;
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
            Film film = filmStorage.findById(filmId).get();
        //    film.getLikes().remove(userId);
            log.debug("Пользователь с id " + userId + " удалил лайк фильму с id " + filmId);
            return film;
        } else if (filmStorage.findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    public List<Film> findMostPopularFilms(Integer count) {
        return filmStorage.findAll().stream()
          //      .sorted((p0, p1) -> (p1.getLikes().size() - p0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
