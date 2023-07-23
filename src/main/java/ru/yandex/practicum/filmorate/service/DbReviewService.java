package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewDao;

import java.util.List;

@Service
@Primary
@AllArgsConstructor
public class DbReviewService implements ReviewService {
    private ReviewDao reviewDao;
    private UserService userService;
    private FilmService filmService;

    @Override
    public Review createReview(Review review) {
        isValid(review);
        return reviewDao.addReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        isValid(review);
        if (reviewDao.getReviewById(review.getReviewId()) == null) {
            throw new NoObjectException("Данный отзыв отсутствует в базе");
        } else {
            return reviewDao.updateReview(review);
        }
    }

    @Override
    public void deleteReviewById(Integer id) {
        reviewDao.deleteReview(id);
    }

    @Override
    public Review getReviewById(Integer id) {
        Review review = reviewDao.getReviewById(id);
        if (review == null) {
            throw new NoObjectException("Отзыв с id = " + id + " не найден в базе");
        }
        return review;
    }

    @Override
    public List<Review> getAllReviews(Integer filmId, Integer count) {
        return reviewDao.getAllReviews(filmId, count);
    }

    @Override
    public Review increaseReviewUseful(Integer id, Integer userId) {
        Review review = reviewDao.getReviewById(id);
        if (review == null) {
            throw new NoObjectException("Отзыв не найден в БД");
        } else if (userService.getUser(userId) == null) {
            throw new ValidationException("Невозможно добавить лайк - пользователь не найден в БД");
        }
        return reviewDao.updateUseful(id, review.getUseful() + 1);
    }

    @Override
    public Review reduceReviewUseful(Integer id, Integer userId) {
        Review review = reviewDao.getReviewById(id);
        if (review == null) {
            throw new NoObjectException("Отзыв не найден в БД");
        } else if (userService.getUser(userId) == null) {
            throw new ValidationException("Невозможно добавить дизлайк - пользователь не найден в БД");
        }
        return reviewDao.updateUseful(id, review.getUseful() - 1);
    }

    private Review isValid(Review review) {
        if (review == null) {
            throw new ValidationException("Передан пустой объект отзыва");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Не указан тип отзыва");
        }
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Пустое содержимое отзыва");
        }
        if (review.getUserId() == null) {
            throw new ValidationException("Не указан создавший пользователь");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Не указан фильм, о котором оставлен отзыв");
        }
        if (filmService.getFilm(review.getFilmId()) == null) {
            throw new ValidationException("Указанный фильм не найден в БД");
        }
        if (userService.getUser(review.getUserId()) == null) {
            throw new ValidationException("Указанный пользователь не найден в БД");
        }
        return review;
    }

}


