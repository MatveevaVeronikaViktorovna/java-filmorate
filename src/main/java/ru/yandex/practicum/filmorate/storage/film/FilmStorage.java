package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(Long filmId);

    Film addLike(long filmId, long userId);

    Film deleteLike(Long filmId, Long userId);

    List<Film> findMostPopularFilms(Integer count);
}
