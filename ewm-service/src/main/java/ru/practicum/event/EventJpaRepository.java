package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.common.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventJpaRepository extends JpaRepository<Event, Integer> {
    List<Event> findEventByInitiatorId(int userId, Pageable pageable);

    Event findEventByIdAndInitiatorId(int eventId, int userId);

    @Query("SELECT e from Event e " +
            "WHERE e.category.id IN :category " +
            "AND e.paid = :paid " +
            "AND e.publishedOn BETWEEN :start and :end " +
            "AND e.state = :state and " +
            "(lower(e.annotation) like %:text% or lower(e.description) like %:text%)")
    List<Event> getEventsWithSort(String text, int[] category, boolean paid, LocalDateTime start,
                                  LocalDateTime end, String state, Pageable pageable);

    @Query("SELECT e from Event e " +
            "WHERE (coalesce(:users, null) is null OR e.initiator.id IN :users) " +
            "AND (coalesce(:states, null) is null OR e.state IN :states) " +
            "AND (coalesce(:categories, null) is null OR e.category IN :categories) " +
            "AND (coalesce(:start, null) is null OR e.eventDate IN :start) " +
            "AND (coalesce(:end, null) is null OR e.eventDate IN :end) ")
    List<Event> getEventsFromAdmin(@Param("users") int[] users,
                                   @Param("states") String[] states,
                                   @Param("categories") int[] categories,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   Pageable pageable);
}
