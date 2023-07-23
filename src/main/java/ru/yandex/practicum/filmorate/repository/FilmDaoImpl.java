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
import ru.yandex.practicum.filmorate.model.Director;
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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films").usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(), "description",
                film.getDescription(), "releaseDate", film.getReleaseDate().toString(),
                "duration", film.getDuration().toString(),
                "mpa_id", film.getMpa().getId().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId((Integer) id);

        insertGenres(film);
        insertDirectors(film);

        return film;
    }

    @Override
    public void deleteFilm(Integer filmId) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, releaseDate = ?, "
                + "duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        String sqlQueryGenres = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryGenres, film.getId());

        String sqlQueryDirectors = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDirectors, film.getId());

        insertGenres(film);
        insertDirectors(film);

        return film;
    }

    @Override
    public List<Film> getAllFilms() {

        List<Film> films = jdbcTemplate.query("SELECT f.id, f.name, f.description, "
                + "f.releaseDate, f.duration, f.mpa_id, mpa_ratings.name as mpa_name "
                + "from films f left join mpa_ratings on mpa_ratings.id = f.mpa_id "
                + "ORDER BY f.id ASC", filmRowMapper());
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        setGenres(filmMap);
        setDirectors(filmMap);

        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query("SELECT f.id, f.name, f.description, "
                + "f.releaseDate, f.duration, f.mpa_id, mpa_ratings.name as mpa_name "
                + "from films f left join mpa_ratings on mpa_ratings.id = f.mpa_id "
                + "WHERE f.id = ? ORDER BY f.id ASC", filmRowMapper(), filmId);
        if (films.size() != 1) {
            return null;
        }
        Film film = films.get(0);
        Map<Integer, Film> filmMap = new HashMap<>();
        filmMap.put(filmId, film);
        setGenres(filmMap);
        setDirectors(filmMap);

        return film;
    }

    @Override
    public List<Integer> getLikes(Integer filmId) {
        return jdbcTemplate.query("SELECT user_id From likes where film_id = ?",
                (rs, rowNum) -> {
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
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year)  {
        String sqlQuery;
        List<Film> films;
        StringBuilder sbWithoutConditions = new StringBuilder("SELECT f.id, f.name, f.description, "
                + "f.releaseDate, f.duration, f.mpa_id, mpa_ratings.name  as mpa_name, "
                + "COUNT(l.user_id) from films f "
                + "left join mpa_ratings on mpa_ratings.id = f.mpa_id "
                + "left join likes l on l.film_id = f.id "
                + "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, "
                + "f.mpa_id, mpa_ratings.name ");
        StringBuilder sbWithGenres = new StringBuilder("SELECT f.id, f.name, f.description, "
                + "f.releaseDate, f.duration, f.mpa_id, mpa_ratings.name as mpa_name, fg.genre_id, "
                + "COUNT(l.user_id) from films f "
                + "left join mpa_ratings on mpa_ratings.id = f.mpa_id "
                + "left join likes l on l.film_id = f.id "
                + "left join films_genres fg on fg.film_id = f.id "
                + "GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, "
                + "f.mpa_id, mpa_ratings.name, fg.genre_id ");
        String orderAndLimit = "ORDER BY f.id DESC LIMIT ?";
        if (genreId == null & year == null) {
            sbWithoutConditions.append(orderAndLimit);
            sqlQuery = sbWithoutConditions.toString();
            films = jdbcTemplate.query(sqlQuery, filmRowMapper(), count);
        } else if (genreId == null) {
            sbWithoutConditions.append("HAVING EXTRACT (YEAR FROM f.releaseDate) = ? ")
                    .append(orderAndLimit);
            sqlQuery = sbWithoutConditions.toString();
            films = jdbcTemplate.query(sqlQuery, filmRowMapper(), year, count);
        } else if (year == null) {
            sbWithGenres.append("HAVING fg.genre_id = ? ")
                    .append(orderAndLimit);
            sqlQuery = sbWithGenres.toString();
            films = jdbcTemplate.query(sqlQuery, filmRowMapper(), genreId, count);
        } else {
            sbWithGenres.append("HAVING fg.genre_id = ? and EXTRACT (YEAR FROM f.releaseDate) = ? ")
                    .append(orderAndLimit);
            sqlQuery = sbWithGenres.toString();
            films = jdbcTemplate.query(sqlQuery, filmRowMapper(), genreId, year, count);
        }
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        setGenres(filmMap);
        setDirectors(filmMap);
        return films;
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        String orderBySql = (sortBy.equals("year")) ? "EXTRACT (YEAR FROM releaseDate)" :
                "(SELECT COUNT(*) FROM LIKES l WHERE l.FILM_ID = f.id) DESC";
        List<Film> films = jdbcTemplate.query(
                "SELECT f.* , mpa_ratings.name as mpa_name FROM directors d " +
                        "LEFT JOIN film_directors fd ON d.id = fd.director_id " +
                        "LEFT JOIN films f ON fd.film_id = f.id " +
                        "LEFT JOIN mpa_ratings ON mpa_ratings.id = f.mpa_id " +
                        "WHERE d.id = ? " +
                        "ORDER BY " +
                        orderBySql, filmRowMapper(), directorId);
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        setGenres(filmMap);
        setDirectors(filmMap);

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
            film.setMpa(new MPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            film.setLikes(new HashSet<>(getLikes(film.getId())));
            film.setGenres(new HashSet<>());
            return film;
        };
    }

    private void insertGenres(Film film) {
        String sqlSubQueryGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlSubQueryGenres, film.getId(), genre.getId());
            }
        }
    }

    private void insertDirectors(Film film) {
        String sqlSubQueryDirectors = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        if (film.getDirectors() != null) {
            for (Director director: film.getDirectors()) {
                jdbcTemplate.update(sqlSubQueryDirectors, film.getId(), director.getId());
            }
        }
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

    private void setDirectors(Map<Integer, Film> films) {
        Set<Integer> filmsIds = films.keySet();
        if (filmsIds.isEmpty()) {
            return;
        }

        SqlParameterSource param = new MapSqlParameterSource("filmsId", filmsIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT d.id, d.name, fd.film_id FROM directors d " +
                "JOIN film_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id IN (:filmsId) ORDER BY fd.film_id, d.id";
        namedParameterJdbcTemplate.query(sql, param, (rs, rownum) -> {
            Film film = films.get(rs.getInt("film_id"));
            return film.getDirectors().add(new Director(rs.getInt("id"), rs.getString("name")));
        });
    }
}

