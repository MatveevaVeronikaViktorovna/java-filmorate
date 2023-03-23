package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
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
        if (validator.isValid(user)) {
            newId++;
            user.setId(newId);
            users.put(user.getId(), user);
            log.debug(user.toString());
            return user;
        } else {
            return null;
        }
    }

    public Optional<User> findById(Long userId) {
        return Optional.of(users.get(userId));
    }

    public User update(User user) {
        if (users.containsKey(user.getId()) && validator.isValid(user)) {
            users.put(user.getId(), user);
            log.debug(user.toString());
            return user;
        } else {
            log.warn("Неверный идентификатор");
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

}
