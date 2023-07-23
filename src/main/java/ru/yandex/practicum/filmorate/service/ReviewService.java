package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(Integer id);

    Review getReviewById(Integer id);

    List<Review> getAllReviews(Integer filmId, Integer count);

    Review increaseReviewUseful(Integer id, Integer userId);

    Review reduceReviewUseful(Integer id, Integer userId);

}
