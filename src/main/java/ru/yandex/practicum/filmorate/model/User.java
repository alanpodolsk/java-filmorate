package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    Integer id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Integer> friends;

    public void setNameLikeLogin() {
        this.name = this.login;
    }
}



