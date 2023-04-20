package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    FilmValidator validator = new FilmValidator();

    @Test
    void shouldReturnTrueWhenNameNoEmpty() {
        Film correctFilm = new Film(1,"Titanic",
                "American epic romance and disaster film directed, written, produced, and co-edited by " +
                        "James Cameron. Incorporating both historical and fictionalized aspects",
                LocalDate.of(1997, 11, 1), 194, new Mpa());
        boolean isValid = validator.isValidName(correctFilm.getName());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenNameEmpty() {
        Film incorrectFilm = new Film(1," ",
                "American epic romance and disaster film directed, written, produced, and co-edited by " +
                        "James Cameron. Incorporating both historical and fictionalized aspects",
                LocalDate.of(1997, 11, 1), 194, new Mpa());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidName(incorrectFilm.getName()));

        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenDescription200Chars() {
        Film correctFilm = new Film(1,"Titanic",
                "Titanic is a 1997 American epic romance and disaster film directed, written, produced, " +
                        "and co-edited by James Cameron. Incorporating both historical and fictionalized aspects, " +
                        "it is based on accounts.",
                LocalDate.of(1997, 11, 1), 194, new Mpa());
        boolean isValid = validator.isValidDescription(correctFilm.getDescription());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenDescription201Chars() {
        Film incorrectFilm = new Film(1,"Titanic",
                "Titanic is a 1997 American epic romance and disaster film directed, written, produced, " +
                        "and co-edited by James Cameron. Incorporating both historical and fictionalized aspects, " +
                        "it is based on accounts o",
                LocalDate.of(1997, 11, 1), 194, new Mpa());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidDescription(incorrectFilm.getDescription()));

        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenReleaseDateIsAfterEarliestDate() {
        Film correctFilm = new Film(1,"Titanic",
                "American epic romance and disaster film directed, written, produced, and co-edited by " +
                        "James Cameron. Incorporating both historical and fictionalized aspects",
                LocalDate.of(1895, 12, 29), 194, new Mpa());
        boolean isValid = validator.isValidReleaseDate(correctFilm.getReleaseDate());

        assertTrue(isValid);

    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBeforeEarliestDate() {
        Film incorrectFilm = new Film(1,"Titanic",
                "Titanic is a 1997 American epic romance and disaster film directed, written, produced, " +
                        "and co-edited by James Cameron. Incorporating both historical and fictionalized aspects, " +
                        "it is based on accounts o",
                LocalDate.of(1895, 12, 27), 194, new Mpa());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidReleaseDate(incorrectFilm.getReleaseDate()));

        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenDurationIsPositiveOrNull() {
        Film correctFilm = new Film(1,"Titanic",
                "American epic romance and disaster film directed, written, produced, and co-edited by " +
                        "James Cameron. Incorporating both historical and fictionalized aspects",
                LocalDate.of(1895, 12, 29), 0, new Mpa());
        boolean isValid = validator.isValidDuration(correctFilm.getDuration());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film incorrectFilm = new Film(1,"Titanic",
                "Titanic is a 1997 American epic romance and disaster film directed, written, produced, " +
                        "and co-edited by James Cameron. Incorporating both historical and fictionalized aspects, " +
                        "it is based on accounts o",
                LocalDate.of(1895, 12, 27), -1, new Mpa());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidDuration(incorrectFilm.getDuration()));

        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenAllIsValid() {
        Film correctFilm = new Film(1,"Titanic",
                "American epic romance and disaster film directed, written, produced, and co-edited by " +
                        "James Cameron. Incorporating both historical and fictionalized aspects",
                LocalDate.of(1895, 12, 29), 0, new Mpa());
        boolean isValid = validator.isValid(correctFilm);

        assertTrue(isValid);
    }

}