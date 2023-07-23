package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Integer id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLikeToReview(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.increaseReviewUseful(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeToReview(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.reduceReviewUseful(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLikeFromReview(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.reduceReviewUseful(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislikeFromReview(@PathVariable Integer id, @PathVariable Integer userId) {
        return reviewService.increaseReviewUseful(id, userId);
    }


}
