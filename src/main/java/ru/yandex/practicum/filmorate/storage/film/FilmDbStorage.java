package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
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

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();
    private long newId;

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
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"), filmRows.getString("name"));
            long id = filmRows.getLong("film_id");
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate releaseDate = filmRows.getDate("release_date").toLocalDate();
            int duration = filmRows.getInt("duration");
            Mpa mpa = mpaDbStorage.findById(filmRows.getInt("mpa_id")).get();
            Film film = new Film(id, name, description, releaseDate, duration, mpa);
            final Set<Genre> genres = new HashSet<>();
            genres.addAll(genreDbStorage.findByFilmId(id));
            film.setGenres(genres);
            final Set<Long> likes = new HashSet<>();
            return Optional.of(film);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", filmId);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Mpa mpa = mpaDbStorage.findById(rs.getInt("mpa_id")).get();
        Film film = new Film(id, name, description, releaseDate, duration, mpa);
        final Set<Genre> genres = new HashSet<>();
        genres.addAll(genreDbStorage.findByFilmId(id));
        film.setGenres(genres);
        final Set<Long> likes = new HashSet<>();
        return film;
    }

    @Override
    public Film create(Film film) {
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
        film.setId(filmId);

        if(film.getGenres().size() > 0) {
            String sqlForGenres = "insert into film_genre (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenres,
                        filmId,
                        genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        System.out.println("запрос3");
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

        if(film.getGenres().size() > 0) {
            String sqlForGenres = "insert into film_genre (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenres,
                        film.getId(),
                        genre.getId());
            }
        }
        return findById(film.getId()).get();
        }

}
