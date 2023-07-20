package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        String sqlQuery = "INSERT INTO reviews (is_positive, content, user_id, film_id, useful) " +
                "values (?, ?, ?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setBoolean(1, review.getIsPositive());
            stmt.setString(2, review.getContent());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            stmt.setInt(5, review.getUseful());
            return stmt;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey().intValue());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET is_positive = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                review.getIsPositive(),
                review.getContent(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateUseful(Integer id, Integer rating) {
        String sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, rating, id);
        return getReviewById(id);
    }

    @Override
    public void deleteReview(Integer id) {
        String sqlQuery = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review getReviewById(Integer id) {
        String sqlQuery = "SELECT id, is_positive, content, user_id, film_id, useful FROM reviews WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, reviewRowMapper(), id);
        if (reviews.size() != 1) {
            return null;
        } else {
            return reviews.get(0);
        }
    }

    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        String sqlQuery;
        if (filmId == null) {
            sqlQuery = "SELECT id, is_positive, content, user_id, film_id, useful FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, reviewRowMapper(), count);
        } else {
            sqlQuery = "SELECT id, is_positive, content, user_id, film_id, useful FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sqlQuery, reviewRowMapper(), filmId, count);
        }
    }

    private RowMapper<Review> reviewRowMapper() {
        return (rs, rowNum) -> {
            Review review = new Review();
            review.setReviewId(rs.getInt("id"));
            review.setContent(rs.getString("content"));
            review.setIsPositive(rs.getBoolean("is_positive"));
            review.setUserId(rs.getInt("user_id"));
            review.setFilmId(rs.getInt("film_id"));
            review.setUseful(rs.getInt("useful"));
            return review;
        };
    }
}
