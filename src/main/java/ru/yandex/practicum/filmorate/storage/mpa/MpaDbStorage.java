package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Mpa> findById(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa where mpa_id = ?", id);
        if(mpaRows.next()) {
            log.info("Найден mpa: {} {}", mpaRows.getString("mpa_id"), mpaRows.getString("name"));
            Mpa mpa = new Mpa();
            mpa.setId(mpaRows.getInt("mpa_id"));
            mpa.setName(mpaRows.getString("name"));
            return Optional.of(mpa);
        } else {
            log.info("Mpa с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    public Collection<Mpa> findAll() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(name);
        return mpa;
    }

}
