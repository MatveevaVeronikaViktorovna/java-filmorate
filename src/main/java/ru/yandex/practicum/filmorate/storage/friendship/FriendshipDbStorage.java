package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FriendshipDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long requestFrom, long requestTo) {
        String sql = "insert into friendship(request_from, request_to) values (?, ?)";
        jdbcTemplate.update(sql,
                requestFrom,
                requestTo);
    }

    public void deleteFriend(long requestFrom, long requestTo) {
        String sqlQuery = "delete from friendship where request_from = ? and request_to = ?";
        jdbcTemplate.update(sqlQuery, requestFrom, requestTo);
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT request_to FROM friendship WHERE request_from = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> UserDbStorage.makeUser(rs), userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        List<User> userFriends = getFriends(userId);
        return userFriends.stream()
                .filter(getFriends(otherUserId)::contains)
                .collect(Collectors.toList());
    }

}
