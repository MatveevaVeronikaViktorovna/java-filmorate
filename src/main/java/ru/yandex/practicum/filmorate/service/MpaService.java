package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

@Service
@Slf4j
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa findById(Integer mpaId) {
        if (mpaDbStorage.findById(mpaId).isPresent()) {
            return mpaDbStorage.findById(mpaId).get();
        } else {
            throw new InvalidIdException("Mpa с id " + mpaId + " не найден");
        }
    }

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

}
