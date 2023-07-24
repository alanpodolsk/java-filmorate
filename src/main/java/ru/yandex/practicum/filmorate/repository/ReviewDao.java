package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    Review addReview(Review review);

    Review updateReview(Review review);

    Review updateUseful(Integer id, Integer rating);

    void deleteReview(Integer id);

    Review getReviewById(Integer id);

    List<Review> getAllReviews(Integer filmId, Integer count);

}
