package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return service.update(user);
    }

    @PutMapping("/{requestFrom}/friends/{requestTo}")
    public User addFriend(@PathVariable Long requestFrom,
                          @PathVariable Long requestTo) {
        return service.addFriend(requestFrom, requestTo);
    }

    @DeleteMapping("/{requestFrom}/friends/{requestTo}")
    public User deleteFriend(@PathVariable Long requestFrom,
                             @PathVariable Long requestTo) {
        return service.deleteFriend(requestFrom, requestTo);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }





    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id,
                                        @PathVariable Long otherId) {
        return service.getCommonFriends(id, otherId);
    }
}
