package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User findById(Long userId) {
        if (storage.findById(userId).isPresent()) {
            return storage.findById(userId).get();
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public User addFriend(long requestFrom, long requestTo) {
        return storage.addFriend(requestFrom, requestTo);
    }

    public User deleteFriend(long requestFrom, long requestTo) {
        return storage.deleteFriend(requestFrom, requestTo);
    }


    public List<User> getFriends(long userId) {
        return storage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return storage.getCommonFriends(userId, otherUserId);
    }

}
