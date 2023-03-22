package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
    }

    public List<Film> findAll() {
        return storage.findAll();
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public Film findById(Long filmId){
        if (storage.findById(filmId).isPresent()) {
            return storage.findById(filmId).get();
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public Film addLike (Long filmId, Long userId) {
        if (storage.findById(filmId).isPresent() && storage.findById(friendId).isPresent()){
            User user = storage.findById(userId).get();
            User friend = storage.findById(friendId).get();
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.debug("Пользователи с id " + userId + ", " + friendId + " добавлены друг другу в друзья");
            return user;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }
}
