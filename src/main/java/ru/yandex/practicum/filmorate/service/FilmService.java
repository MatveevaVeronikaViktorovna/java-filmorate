package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public Film findById(Long filmId){
        if (filmStorage.findById(filmId).isPresent()) {
            return filmStorage.findById(filmId).get();
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public Film addLike (Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isPresent() && userStorage.findById(userId).isPresent()){
            Film film = filmStorage.findById(filmId).get();
            film.getLikes().add(userId);
            log.debug("Пользователь с id " + userId + "поставил лайу фильму с id " + filmId);
            return film;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public Film deleteLike (Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isPresent() && userStorage.findById(userId).isPresent()){
            Film film = filmStorage.findById(filmId).get();
            film.getLikes().remove(userId);
            log.debug("Пользователь с id " + userId + "удалил лайк фильму с id " + filmId);
            return film;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public List<Film> findMostPopularFilms(Integer count){
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
