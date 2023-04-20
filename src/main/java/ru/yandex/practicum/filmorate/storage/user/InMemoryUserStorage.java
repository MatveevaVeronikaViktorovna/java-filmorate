package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator validator = new UserValidator();
    private long newId;

    public Optional<User> findById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
        return Optional.of(users.get(userId));
    }

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

    @Override
    public User addFriend(long requestFrom, long requestTo) {
        if (findById(requestFrom).isPresent() && findById(requestTo).isPresent()) {
            User user = findById(requestFrom).get();
            User friend = findById(requestTo).get();
            user.getFriends().add(requestTo);
            friend.getFriends().add(requestFrom);
            log.debug("Пользователи с id " + requestFrom + " и " + requestTo + " добавлены друг другу в друзья");
            return user;
        } else if (findById(requestFrom).isPresent()) {
            log.warn("Пользователь с id " + requestTo + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        } else {
            log.warn("Пользователь с id " + requestFrom + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        }
    }

    @Override
    public User deleteFriend(long requestFrom, long requestTo) {
        if (findById(requestFrom).isPresent() && findById(requestTo).isPresent()) {
            User user = findById(requestFrom).get();
            User friend = findById(requestTo).get();
            user.getFriends().remove(requestTo);
            friend.getFriends().remove(requestFrom);
            log.debug("Пользователи с id " + requestFrom + " и " + requestTo + " удалены друг у друга из друзей");
            return user;
        } else if (findById(requestFrom).isPresent()) {
            log.warn("Пользователь с id " + requestTo + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestTo + " не найден");
        } else {
            log.warn("Пользователь с id " + requestFrom + " не найден");
            throw new InvalidIdException("Пользователь с id " + requestFrom + " не найден");
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        if (findById(userId).isPresent()) {
            Set<Long> userFriendsId = findById(userId).get().getFriends();
            List<User> friends = new ArrayList<>();
            for (Long id : userFriendsId) {
                User friend = findById(id).get();
                friends.add(friend);
            }
            return friends;
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        if (findById(userId).isPresent() && findById(otherUserId).isPresent()) {
            User user = findById(userId).get();
            User otherUser = findById(otherUserId).get();
            List<Long> userFriends = new ArrayList<>(user.getFriends());
            userFriends = userFriends.stream()
                    .filter(otherUser.getFriends()::contains)
                    .collect(Collectors.toList());

            List<User> commonFriends = new ArrayList<>();
            for (Long id : userFriends) {
                User friend = findById(id).get();
                commonFriends.add(friend);
            }
            return commonFriends;
        } else if (findById(userId).isPresent()) {
            log.warn("Пользователь с id " + otherUserId + " не найден");
            throw new InvalidIdException("Пользователь с id " + otherUserId + " не найден");
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        }
    }

}
