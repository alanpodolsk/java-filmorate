package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Event;
import java.util.List;

public interface EventDao {

    List<Event> getFeedById(Integer id);

    void addFeed(Integer user_id, String eventType, String operation, Integer entityId);
}
