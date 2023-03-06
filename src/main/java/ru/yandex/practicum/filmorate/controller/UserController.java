package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    UserValidator validator = new UserValidator();
    private int newId;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
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

    @PutMapping
    public User update(@RequestBody User user) {
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
