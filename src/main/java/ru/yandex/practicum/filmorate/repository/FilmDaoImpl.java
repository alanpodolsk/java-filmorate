package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@AllArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final MPADao mpaDao;
    private final GenreDao genreDao;

    @Override
    public Integer addFilm(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(), "description", film.getDescription(), "releaseDate", film.getReleaseDate().toString(), "duration", film.getDuration().toString(), "mpa_id", film.getMpa().getId().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);

        String sqlQuery = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery,
                    id,
                    genre.getId());
        }
        return (Integer) id;
    }

    @Override
    public Integer updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        sqlQuery = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    genre.getId());
        }
        return film.getId();
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            List<Film> films = jdbcTemplate.queryForObject("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, l.user_id, g.genre_id from films f left join likes l on f.id = l.film_id left join films_genres g on g.film_id = f.id ORDER BY f.id ASC, g.genre_id ASC", filmRowMapper());
            return films;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return new ArrayList<>();
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Film getFilmById(Integer filmId) {
        try {
            List<Film> films = jdbcTemplate.queryForObject("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.mpa_id, l.user_id, g.genre_id from films f left join likes l on f.id = l.film_id left join films_genres g on g.film_id = f.id WHERE f.id = ? ORDER BY f.id ASC, g.genre_id ASC", filmRowMapper(), filmId);
            if (films.size() == 1) {
                return films.get(0);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("expected 1, actual 0")) {
                return null;
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
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
        jdbcTemplate.update(sqlQuery,
                userId,
                filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery,
                userId,
                filmId);
    }


    private RowMapper<List<Film>> filmRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            Film film = new Film();
            Set<Integer> likes = new HashSet<>();
            Set<Genre> genres = new HashSet<>();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(LocalDate.parse(rs.getDate("releaseDate").toString()));
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDao.getMpaById(rs.getInt("mpa_id")));
            do {
                if (film.getId() != rs.getInt("id")) {
                    film.setLikes(likes);
                    film.setGenres(genres);
                    films.add(film);
                    film = new Film();
                    likes = new HashSet<>();
                    genres = new HashSet<>();
                    film.setId(rs.getInt("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(LocalDate.parse(rs.getDate("releaseDate").toString()));
                    film.setDuration(rs.getInt("duration"));
                    film.setMpa(mpaDao.getMpaById(rs.getInt("mpa_id")));
                }
                Integer like = rs.getInt("user_id");
                if (like > 0) {
                    likes.add(rs.getInt("friend_id"));
                }
                Integer genre = rs.getInt("genre_id");
                if (genre > 0) {
                    genres.add(genreDao.getGenreById(rs.getInt("genre_id")));
                }
            } while (rs.next());
            film.setLikes(likes);
            film.setGenres(genres);
            films.add(film);
            return films;
        };
    }

    private Integer getLastFilmId() {
        return jdbcTemplate.queryForObject("SELECT MAX(id) from films", Integer.class);
    }
}

