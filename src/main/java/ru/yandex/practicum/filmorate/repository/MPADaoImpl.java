package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

@Component
@AllArgsConstructor
public class MPADaoImpl implements MPADao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getMpaById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name From mpa_ratings WHERE id = ?", mpaRowMapper(), id);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return null;
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public List<MPA> getAllMPA() {
        return jdbcTemplate.query("SELECT id, name From mpa_ratings  ORDER BY id ASC", mpaRowMapper());
    }


    private RowMapper<MPA> mpaRowMapper() {
        return (rs, rowNum) -> new MPA(rs.getInt("id"), rs.getString("name"));
    }

}

