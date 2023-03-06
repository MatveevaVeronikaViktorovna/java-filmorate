package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    UserValidator validator = new UserValidator();

    @Test
    void shouldReturnTrueWhenEmailNotEmptyAndContainsAt() {
        User correctUser = new User("user@mail.ru", "user", "UserName", LocalDate.of(1991,
                12, 12));
        boolean isValid = validator.isValidEmail(correctUser.getEmail());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenEmailEmpty() {
        User incorrectUser = new User(" ", "user", "UserName", LocalDate.of(1991,
                12, 12));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidEmail(incorrectUser.getEmail()));

        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailNoContainAt() {
        User incorrectUser = new User("usermail.ru", "user", "UserName", LocalDate.of(1991,
                12, 12));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidEmail(incorrectUser.getEmail()));

        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'",
                exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenLoginNotEmptyAndWithoutSpace() {
        User correctUser = new User("user@mail.ru", "user", "UserName", LocalDate.of(1991,
                12, 12));
        boolean isValid = validator.isValidLogin(correctUser.getLogin());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenLoginEmpty() {
        User incorrectUser = new User("user@mail.ru", " ", "UserName", LocalDate.of(1991,
                12, 12));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidLogin(incorrectUser.getLogin()));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpace() {
        User incorrectUser = new User("user@mail.ru", "user user", "UserName", LocalDate.of(1991,
                12, 12));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidLogin(incorrectUser.getLogin()));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenBirthdayIsBeforeNow() {
        User correctUser = new User("user@mail.ru", "user", "UserName", LocalDate.of(2023,
                3, 5));
        boolean isValid = validator.isValidBirthday(correctUser.getBirthday());

        assertTrue(isValid);
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsAfterNow() {
        User incorrectUser = new User("user@mail.ru", "user user", "UserName", LocalDate.of(2023,
                3, 7));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.isValidBirthday(incorrectUser.getBirthday()));

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldReturnLoginWhenNameEmpty() {
        User userWithoutName = new User("user@mail.ru", "user", " ", LocalDate.of(2023,
                3, 5));
        boolean isValid = validator.isValid(userWithoutName);

        assertTrue(isValid);
        assertEquals("user", userWithoutName.getName());
    }

    @Test
    void shouldReturnTrueWhenAllIsValid() {
        User correctUser = new User("user@mail.ru", "user", "UserName", LocalDate.of(1991,
                12, 12));
        boolean isValid = validator.isValid(correctUser);

        assertTrue(isValid);
    }

}