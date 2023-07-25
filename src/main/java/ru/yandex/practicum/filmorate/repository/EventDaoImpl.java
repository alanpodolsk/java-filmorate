package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
@Primary
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedById(Integer id) {
        return jdbcTemplate.query("SELECT moment, user_id, event_type, operation, event_id, entity_id " +
                "FROM events WHERE user_id = ? ", eventRowMapper(), id);
    }

    @Override
    public void addFeed(Integer userId, String eventType, String operation, Integer entityId) {
        String eventSqlQuery = "INSERT INTO events(moment,user_id,event_type,operation,entity_id) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(eventSqlQuery,
                Timestamp.from(Instant.now()),
                userId,
                eventType,
                operation,
                entityId);
    }

    private RowMapper<Event> eventRowMapper() {
        return (rs, rowNum) -> {
            Event event = new Event();
            event.setTimestamp(rs.getTimestamp("moment").getTime());
            event.setUserId(rs.getInt("user_id"));
            event.setEventType(rs.getString("event_type"));
            event.setOperation(rs.getString("operation"));
            event.setEventId(rs.getInt("event_id"));
            event.setEntityId(rs.getInt("entity_id"));
            return event;
        };
    }
}
