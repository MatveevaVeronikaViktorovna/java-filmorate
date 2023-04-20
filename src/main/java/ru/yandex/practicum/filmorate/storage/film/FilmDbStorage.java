package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final FilmValidator validator = new FilmValidator();

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", filmId);
        if (filmRows.next()) {
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"),
                    filmRows.getString("name"));
            long id = filmRows.getLong("film_id");
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate releaseDate = filmRows.getDate("release_date").toLocalDate();
            int duration = filmRows.getInt("duration");
            Mpa mpa = mpaDbStorage.findById(filmRows.getInt("mpa_id")).get();
            Film film = new Film(id, name, description, releaseDate, duration, mpa);
            final Set<Genre> genres = new HashSet<>(genreDbStorage.findByFilmId(id));
            film.setGenres(genres);
            final Set<Long> likes = new HashSet<>(findLikesByFilmId(id));
            film.setLikes(likes);
            return Optional.of(film);
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        System.out.println("сюда дошел запрос");
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Mpa mpa = mpaDbStorage.findById(rs.getInt("mpa_id")).get();
        Film film = new Film(id, name, description, releaseDate, duration, mpa);
        final Set<Genre> genres = new HashSet<>(genreDbStorage.findByFilmId(id));
        film.setGenres(genres);
        final Set<Long> likes = new HashSet<>(findLikesByFilmId(id));
        film.setLikes(likes);
        return film;
    }

    @Override
    public Film create(Film film) {
        if (film == null) {
            log.warn("Валидация не пройдена: не заполнены поля фильма");
            throw new ValidationException("Валидация не пройдена: не заполнены поля фильма");
        }
        if (!validator.isValid(film)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Валидация не пройдена");
        }
        String sql = "insert into films (name, description, release_date, duration, mpa_id) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long filmId = keyHolder.getKey().longValue();
        if (film.getGenres().size() > 0) {
            String sqlForGenres = "insert into film_genre (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenres,
                        filmId,
                        genre.getId());
            }
        }
        return findById(filmId).get();
    }

    @Override
    public Film update(Film film) {
        if (film == null) {
            log.warn("Валидация не пройдена: не заполнены поля фильма");
            throw new ValidationException("Валидация не пройдена: не заполнены поля фильма");
        }
        if (findById(film.getId()).isPresent()) {
            String sql = "update films set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                    "where film_id = ?";
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    java.sql.Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            String sqlForDeleteGenres = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlForDeleteGenres, film.getId());
            if (film.getGenres().size() > 0) {
                String sqlForGenres = "insert into film_genre (film_id, genre_id) values (?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlForGenres,
                            film.getId(),
                            genre.getId());
                }
            }
            return findById(film.getId()).get();
        } else {
            log.warn("Фильм с id " + film.getId() + " не найден");
            throw new InvalidIdException("Фильм с id " + film.getId() + " не найден");
        }
    }

    @Override
    public Film addLike(long filmId, long userId) {
        if (findById(filmId).isPresent() && findById(userId).isPresent()) {
            String sql = "insert into likes(film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sql,
                    filmId,
                    userId);
            return findById(filmId).get();
        } else if (findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        if (findById(filmId).isPresent() && findById(userId).isPresent()) {
            String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
            jdbcTemplate.update(sqlQuery, filmId, userId);
            return findById(filmId).get();
        } else if (findById(filmId).isPresent()) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new InvalidIdException("Пользователь с id " + userId + " не найден");
        } else {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new InvalidIdException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public List<Film> findMostPopularFilms(Integer count) {
        String sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                "from films as f left outer join likes as l on f.film_id = l.film_id " +
                "group by f.film_id order by count(l.user_id) desc " +
                "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Collection<Long> findLikesByFilmId(long filmId) {
        String sql = "select user_id from likes where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

}
