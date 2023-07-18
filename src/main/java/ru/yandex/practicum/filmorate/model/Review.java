package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    Integer id;
    Boolean isPositive;
    String content;
    Integer userId;
    Integer filmId;
    Integer useful;
}
