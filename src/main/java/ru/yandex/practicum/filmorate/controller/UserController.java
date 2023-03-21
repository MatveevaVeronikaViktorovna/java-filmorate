package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserStorage storage;

    @Autowired
    public UserController(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    @GetMapping
    public List<User> findAll() {
        return storage.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return storage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return storage.update(user);
    }

}
