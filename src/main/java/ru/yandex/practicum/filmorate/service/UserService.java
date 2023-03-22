package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
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

    public User findUserById(Long userId) {
        if (storage.findUserById(userId).isPresent()) {
            return storage.findUserById(userId).get();
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public User addFriend (long userId, long friendId) {
        if (storage.findUserById(userId).isPresent() && storage.findUserById(friendId).isPresent()){
            User user = storage.findUserById(userId).get();
            User friend = storage.findUserById(friendId).get();
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.debug("Пользователи с id " + userId + ", " + friendId + " добавлены друг другу в друзья");
            return user;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public User deleteFriend (long userId, long friendId) {
        if (storage.findUserById(userId).isPresent() && storage.findUserById(friendId).isPresent()){
            User user = storage.findUserById(userId).get();
            User friend = storage.findUserById(friendId).get();
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            log.debug("Пользователи с id " + userId + ", " + friendId + " удалены друг у друга из друзей");
            return user;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public List<User> getFriends(long userId) {
        if (storage.findUserById(userId).isPresent()) {
            Set <Long> userFriendsId = storage.findUserById(userId).get().getFriends();
            List<User>friends = new ArrayList<>();
            for (Long id : userFriendsId) {
                User friend = storage.findUserById(id).get();
                friends.add(friend);
            }
            return friends;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        if (storage.findUserById(userId).isPresent() && storage.findUserById(otherUserId).isPresent()) {
            Set <Long> userFriendsId = storage.findUserById(userId).get().getFriends();
            Set <Long> otherUserFriendsId = storage.findUserById(otherUserId).get().getFriends();
            userFriendsId.retainAll(otherUserFriendsId);

            List<User>commonFriends = new ArrayList<>();
            for (Long id : userFriendsId) {
                User friend = storage.findUserById(id).get();
                commonFriends.add(friend);
            }
            return commonFriends;
        } else {
            throw new InvalidIdException("Неверный идентификатор");
        }
    }



}
