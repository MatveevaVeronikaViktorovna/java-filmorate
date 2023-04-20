package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre findById(Integer genreId) {
        if (genreDbStorage.findById(genreId).isPresent()) {
            return genreDbStorage.findById(genreId).get();
        } else {
            throw new InvalidIdException("Жанр с id " + genreId + " не найден");
        }
    }

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

}

