package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@AllArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("films").usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(), "description", film.getDescription(), "releaseDate", film.getReleaseDate().toString(), "duration", film.getDuration().toString(), "mpa_id", film.getMpa().getId().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId((Integer) id);
        String sqlSubQuery = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlSubQuery, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        sqlQuery = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " + "f.mpa_id, mpa_ratings.name from films f left join mpa_ratings on mpa_ratings.id = f.mpa_id ORDER BY f.id ASC", filmRowMapper());
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        setGenres(filmMap);
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, " + "f.mpa_id, mpa_ratings.name from films f left join mpa_ratings on mpa_ratings.id = f.mpa_id WHERE f.id = ? ORDER BY f.id ASC", filmRowMapper(), filmId);
        if (films.size() != 1) {
            return null;
        }
        Film film = films.get(0);
        Map<Integer, Film> filmMap = new HashMap<>();
        filmMap.put(filmId, film);
        setGenres(filmMap);
        return film;
    }

    @Override
    public List<Integer> getLikes(Integer filmId) {
        return jdbcTemplate.query("SELECT user_id From likes where film_id = ?", (rs, rowNum) -> {
            return rs.getInt("user_id");
        }, filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM likes " + "WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = jdbcTemplate.query("SELECT f.id, f.name, f.description, f.releaseDate, f.duration," +
                " f.mpa_id, mpa_ratings.name, COUNT(l.user_id)" +
                " from films f left join mpa_ratings on mpa_ratings.id = f.mpa_id left join likes l on l.film_id = f.id " +
                "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, mpa_ratings.name ORDER BY f.id DESC LIMIT ?", filmRowMapper(),count);
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        setGenres(filmMap);
        return films;
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            Set<Integer> likes = new HashSet<>();
            Set<Genre> genres = new HashSet<>();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(LocalDate.parse(rs.getDate("releaseDate").toString()));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new MPA(rs.getInt("mpa_id"), rs.getString("mpa_ratings.name")));
            film.setLikes(new HashSet<>(getLikes(film.getId())));
            film.setGenres(new HashSet<>());
            return film;
        };
    }

    private void setGenres(Map<Integer, Film> films) {
        Set<Integer> filmsIds = films.keySet();
        if (filmsIds.isEmpty()) {
            return;
        }
        SqlParameterSource param = new MapSqlParameterSource("filmsId", filmsIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT g.id, g.name, fg.film_id FROM genres g " + "JOIN films_genres fg ON g.id = fg.genre_id " + "WHERE fg.film_id IN (:filmsId) ORDER BY fg.film_id ASC, g.id ASC";
        namedParameterJdbcTemplate.query(sql, param, (rs, rownum) -> {
            Film film = films.get(rs.getInt("film_id"));
            return film.getGenres().add(new Genre(rs.getInt("id"), rs.getString("name")));
        });
    }
}

