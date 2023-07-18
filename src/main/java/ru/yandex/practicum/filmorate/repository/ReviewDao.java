package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewDao {
    Review addReview (Review review);
    Review updateReview(Review review);
    void updateUseful(Integer id, Boolean isIncrease);


}
