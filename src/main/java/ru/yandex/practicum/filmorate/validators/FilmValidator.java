package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;


@Slf4j
public class FilmValidator implements Validator<Film> {

    @Override
    public boolean isValid(Film film) {
  //      return isValidName(film.getName()) && isValidDescription(film.getDescription()) &&
  //              isValidReleaseDate(film.getReleaseDate()) && isValidDuration(film.getDuration());
    return true;
    }

    public boolean isValidName(String name) {
        if (!name.isBlank()) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
    }

    public boolean isValidDescription(String description) {
        if (description.length() <= 200) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
    }

    public boolean isValidReleaseDate(LocalDate releaseDate) {
        LocalDate cinemasBirthday = LocalDate.of(1895, 12, 28);
        if (releaseDate.isAfter(cinemasBirthday)) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    public boolean isValidDuration(int duration) {
        if (duration >= 0) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

}
