package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.StatusRequest;
import ru.practicum.compilation.Compilation;

import java.util.List;
import java.util.Set;

public interface RequestJpaRepository extends JpaRepository<Request, Integer> {
    List<Request> findRequestByRequesterId(int userId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    List<Request> findRequestByEventIdAndRequesterId(int eventId, int userId);

    List<Request> findRequestByEventIdInAndStatusRequest(List<Integer> eventIds, StatusRequest status);

    List<Request> findRequestByEventIdAndStatusRequest(int eventId, String status);

}
