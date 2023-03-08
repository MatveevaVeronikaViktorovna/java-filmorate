package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator implements Validator<User> {
    @Override
    public boolean isValid(User user) {
        if (!isValidName(user.getName())) {
            user.setName(user.getLogin());
        }
        return isValidEmail(user.getEmail()) && isValidLogin(user.getLogin()) && isValidBirthday(user.getBirthday());
    }

    public boolean isValidEmail(String email) {
        if (!email.isBlank() && email.contains("@")) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Электронная почта не может быть пустой и должна содержать символ '@'");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
    }

    public boolean isValidLogin(String login) {
        if (!login.isBlank() && !login.contains(" ")) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    public boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    public boolean isValidBirthday(LocalDate birthday) {
        LocalDate now = LocalDate.now();
        if (!birthday.isAfter(now)) {
            return true;
        } else {
            log.warn("Валидация не пройдена: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}
