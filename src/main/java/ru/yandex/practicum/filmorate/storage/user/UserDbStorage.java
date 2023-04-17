package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserValidator validator = new UserValidator();
    private long newId;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> findById(Long userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", userId);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("name"));
            long id = userRows.getLong("user_id");
            String email = userRows.getString("email");
            String login = userRows.getString("login");
            String name = userRows.getString("name");
            LocalDate birthday = userRows.getDate("birthday").toLocalDate();
            User user = new User(id, email, login, name, birthday);
            Set<Long> friends = new HashSet<>();
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
        User user = new User(id,email,login,name,birthday);
        Set<Long> friends = new HashSet<>();
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
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());

        return findById(user.getId()).get();
    }

}


