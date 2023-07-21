package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@AllArgsConstructor
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        director.setId(id);

        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return getDirectorById(director.getId());
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT id, name FROM directors  ORDER BY id", directorRowMapper());
    }

    @Override
    public Director getDirectorById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name FROM directors WHERE id = ?", directorRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void deleteDirectorById(Integer id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"));
    }
}
