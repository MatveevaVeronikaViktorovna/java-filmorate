package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;

@Component
@Slf4j
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator validator = new UserValidator();
    private long newId;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена: не заполнены поля пользователя");
            throw new ValidationException("Валидация не пройдена: не заполнены поля пользователя");
        }
        if (!validator.isValid(user)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }
        newId++;
        user.setId(newId);
        users.put(user.getId(), user);
        log.debug("Добавлен новый пользователь: " + user);
        return user;
    }

    public Optional<User> findById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
        return Optional.of(users.get(userId));
    }

    public User update(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена: не заполнены поля пользователя");
            throw new ValidationException("Валидация не пройдена: не заполнены поля пользователя");
        }
        if (users.containsKey(user.getId()) && validator.isValid(user)) {
            users.put(user.getId(), user);
            log.debug("Пользователь с id " + user.getId() + " обновлен: " + user);
            return user;
        } else {
            log.warn("Пользователь с id " + user.getId() + " не найден");
            throw new InvalidIdException("Пользователь с id " + user.getId() + " не найден");
        }
    }

}
