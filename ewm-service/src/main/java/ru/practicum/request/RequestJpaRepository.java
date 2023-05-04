package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.common.Status;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<Request, Integer> {
    List<Request> findRequestByRequesterId(int userId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    List<Request> findRequestByEventIdAndRequesterId(int eventId, int userId);

    List<Request> findRequestByEventId(int eventId);

    List<Request> findRequestByEventIdInAndStatus(List<Integer> eventIds, Status status);

    List<Request> findRequestByEventIdAndStatus(int eventId, Status status);

    @Query("select count (r.id) from Request r where r.event.id = ?1 and r.status = ?2")
    Integer getCountConfirmedRequest(Integer eventId, Status state);

}
