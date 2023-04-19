package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final UserValidator validator = new UserValidator();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findById(Long userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", userId);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"),
                    userRows.getString("name"));
            long id = userRows.getLong("user_id");
            String email = userRows.getString("email");
            String login = userRows.getString("login");
            String name = userRows.getString("name");
            LocalDate birthday = userRows.getDate("birthday").toLocalDate();
            User user = new User(id, email, login, name, birthday);
            Set<Long> friends = new HashSet<>(findFriendsByUserId(id));
            user.setFriends(friends);
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = new User(id, email, login, name, birthday);
        Set<Long> friends = new HashSet<>(findFriendsByUserId(id));
        user.setFriends(friends);
        return user;
    }

    @Override
    public User create(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена: не заполнены поля пользователя");
            throw new ValidationException("Валидация не пройдена: не заполнены поля пользователя");
        }
        if (!validator.isValid(user)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }

        String sql = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long userId = keyHolder.getKey().longValue();
        return findById(userId).get();
    }

    @Override
    public User update(User user) {
        if (user == null) {
            log.warn("Валидация не пройдена: не заполнены поля пользователя");
            throw new ValidationException("Валидация не пройдена: не заполнены поля пользователя");
        }
        if (findById(user.getId()).isPresent()) {
            String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    java.sql.Date.valueOf(user.getBirthday()),
                    user.getId());
            return findById(user.getId()).get();
        } else {
            log.warn("Пользователь с id " + user.getId() + " не найден");
            throw new InvalidIdException("Пользователь с id " + user.getId() + " не найден");
        }
    }

    @Override
    public User addFriend(long requestFrom, long requestTo) {
        if (findById(requestFrom).isPresent() && findById(requestTo).isPresent()) {
            if (findById(requestTo).get().getFriends().contains(requestFrom)) {
                String sql = "update friendship set is_confirmed = ? where request_from = ? and request_to = ?";
                jdbcTemplate.update(sql,
                        true,
                        requestTo,
                        requestFrom);
            } else {
                String sql = "insert into friendship(request_from, request_to, is_confirmed) values (?, ?, ?)";
                jdbcTemplate.update(sql,
                        requestFrom,
                        requestTo,
                        false);
            }
            return findById(requestFrom).get();
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
        if (findById(requestFrom).get().getFriends().contains(requestTo)) {
            if (findById(requestTo).get().getFriends().contains(requestFrom)) {
                String sql1 = "delete from friendship where request_from = ? and request_to = ?";
                jdbcTemplate.update(sql1, requestFrom, requestTo);
                String sql2 = "delete from friendship where request_from = ? and request_to = ?";
                jdbcTemplate.update(sql2, requestTo, requestFrom);

                String sql3 = "insert into friendship(request_from, request_to, is_confirmed) values (?, ?, ?)";
                jdbcTemplate.update(sql3,
                        requestTo,
                        requestFrom,
                        false);
            } else {
                String sqlQuery = "delete from friendship where request_from = ? and request_to = ?";
                jdbcTemplate.update(sqlQuery, requestFrom, requestTo);
            }
            return findById(requestFrom).get();
        } else {
            throw new InvalidIdException("Пользователь с id " + requestTo + " не является другом пользователю с id "
                    + requestFrom);
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT request_to FROM friendship WHERE request_from = ? " +
                "UNION SELECT request_from FROM friendship WHERE request_to = ? AND is_confirmed = true)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        if (findById(userId).isPresent() && findById(otherUserId).isPresent()) {
            List<Long> userFriends = new ArrayList<>(findFriendsByUserId(userId));
            userFriends = userFriends.stream()
                    .filter(findFriendsByUserId(otherUserId)::contains)
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

    public Collection<Long> findFriendsByUserId(long userId) {
        String sql = "SELECT user_id FROM users WHERE user_id IN (SELECT request_to FROM friendship " +
                "WHERE request_from = ? UNION SELECT request_from FROM friendship WHERE request_to = ? " +
                "AND is_confirmed = true)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), userId, userId);
    }

}




