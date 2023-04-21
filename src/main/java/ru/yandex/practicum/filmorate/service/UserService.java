package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipStorage;
    private final UserValidator validator = new UserValidator();

    @Autowired
    public UserService(UserStorage userStorage, FriendshipDbStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User findById(Long userId) {
        if (userStorage.findById(userId).isPresent()) {
            return userStorage.findById(userId).get();
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

    public List<User> findAll() {
        return userStorage.findAll();
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
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена: не заполнены поля пользователя");
            throw new ValidationException("Валидация не пройдена: не заполнены поля пользователя");
        }
        if (!validator.isValid(user)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }
        if (userStorage.findById(user.getId()).isPresent()) {
            return userStorage.update(user);
        } else {
            log.warn("Пользователь с id " + user.getId() + " не найден");
            throw new InvalidIdException("Пользователь с id " + user.getId() + " не найден");
        }
    }

    public User addFriend(long requestFrom, long requestTo) {
        if (userStorage.findById(requestFrom).isPresent() && userStorage.findById(requestTo).isPresent()) {
            friendshipStorage.addFriend(requestFrom, requestTo);
            return userStorage.findById(requestFrom).get();
        } else if (userStorage.findById(requestFrom).isPresent()) {
            log.warn("Пользователь с id " + requestTo + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        } else {
            log.warn("Пользователь с id " + requestFrom + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        }
    }

    public User deleteFriend(long requestFrom, long requestTo) {
        if (userStorage.findById(requestFrom).isPresent() && userStorage.findById(requestTo).isPresent()) {
            friendshipStorage.deleteFriend(requestFrom, requestTo);
            return userStorage.findById(requestFrom).get();
        } else if (userStorage.findById(requestFrom).isPresent()) {
            log.warn("Пользователь с id " + requestTo + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        } else {
            log.warn("Пользователь с id " + requestFrom + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        }
    }

    public List<User> getFriends(long userId) {
        if (userStorage.findById(userId).isPresent()) {
            return friendshipStorage.getFriends(userId);
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        if (userStorage.findById(userId).isPresent() && userStorage.findById(otherUserId).isPresent()) {
            return friendshipStorage.getCommonFriends(userId, otherUserId);
        } else if (userStorage.findById(userId).isPresent()) {
            log.warn("Пользователь с id " + otherUserId + " не найден");
            throw new InvalidIdException("Пользователь с id " + otherUserId + " не найден");
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

}
