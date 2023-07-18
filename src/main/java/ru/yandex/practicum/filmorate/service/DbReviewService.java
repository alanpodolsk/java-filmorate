package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public class DbReviewService implements ReviewService{
    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public void deleteReviewById(Integer id) {

    }

    @Override
    public Review getReviewById(Integer id) {
        return null;
    }

    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        return null;
    }

    @Override
    public Review addLikeToReview(Integer id, Integer userId) {
        return null;
    }

    @Override
    public Review addDislikeToReview(Integer id, Integer userId) {
        return null;
    }

    @Override
    public Review deleteLikeFromReview(Integer id, Integer userId) {
        return null;
    }

    @Override
    public Review deleteDislikeFromReview(Integer id, Integer userId) {
        return null;
    }
}
