package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@AllArgsConstructor
@Primary
public class GenreDaoImpl implements GenreDao{
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Genre getGenreById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT id, name From genres WHERE id = ?", genreRowMapper(), id);

    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT id, name From genres", genreRowMapper());
    }

    public RowMapper<Genre> genreRowMapper(){
        return (rs, rowNum) -> {
            return new Genre(rs.getInt("id"),rs.getString("name"));
        };
    }
}
