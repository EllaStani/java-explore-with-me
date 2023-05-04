package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.State;
import ru.practicum.common.Status;
import ru.practicum.event.Event;
import ru.practicum.event.EventJpaRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateStatusInDto;
import ru.practicum.request.dto.RequestUpdateStatusOutDto;
import ru.practicum.user.User;
import ru.practicum.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestJpaRepository requestRepository;
    private final UserJpaRepository userRepository;
    private final EventJpaRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(int userId) {
        List<Request> requests = requestRepository.findRequestByRequesterId(userId);
        return RequestMapper.mapToListParticipationRequestDto(requests);
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserIdAndEventId(int userId, int eventId) {
        List<Request> requests = requestRepository.findRequestByEventId(eventId);
        return RequestMapper.mapToListParticipationRequestDto(requests);
    }

    @Transactional
    @Override
    public ParticipationRequestDto saveNewRequest(int userId, int eventId) {
        Event event = checkingExistEvent(eventId);

        if (event.getInitiator().getId() == userId) {
            log.error("Инициатор события не может добавить запрос на участие!");
            throw new ConflictException("Инициатор события не может добавить запрос на участие!");
        }

        if (event.getParticipantLimit() != 0) {
            if (requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED) >= event.getParticipantLimit()) {
                log.error("Достигнут лимит участников на событие!");
                throw new ConflictException("У события достигнут лимит участников!");
            }
        }

        if (event.getState() != State.PUBLISHED) {
            log.error("Нельзя участвовать в неопубликованном событии!");
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }

        List<Request> requests = requestRepository.findRequestByEventIdAndRequesterId(eventId, userId);

        if (requests.size() == 0) {
            User user = checkingExistUser(userId);

            Request request = new Request();
            request.setEvent(event);
            request.setRequester(user);

            if (event.getRequestModeration() == false) {
                request.setStatus(Status.CONFIRMED);
            } else {
                request.setStatus(Status.PENDING);
            }
            request.setCreated(LocalDateTime.now());
            log.info("Создан новый запрос на участие в событие eventId={} userId={} : {}", eventId, userId, request);
            requestRepository.save(request);
            return RequestMapper.mapToParticipationRequestDto(request);
        } else {
            log.error("Нельзя добавить повторный запрос от пользователя!");
            throw new ConflictException("Нельзя добавить повторный запрос от пользователя!");
        }
    }

    @Transactional
    @Override
    public RequestUpdateStatusOutDto updateStatus(int userId, int eventId, RequestUpdateStatusInDto requestInDto) {
        Event event = checkingExistEvent(eventId);
        Integer limit = event.getParticipantLimit();
        Integer reqConfirmed = 0;

        if (event.getInitiator().getId() != userId) {
            log.error("Пользователь не инициатор события!");
            throw new ConflictException("Пользователь не инициатор события!");
        }

        List<Request> requests = new ArrayList<>(requestRepository.findAllByIdIn(requestInDto.getRequestIds()));

        requests.forEach(request -> {
            if (request.getStatus() != Status.PENDING) {
                log.info("Изменение статуса отклонено - заявки не имеют статус PENDING!");
                throw new ConflictException("Заявки не имеют статус PENDING!");
            }
        });

        if (requestInDto.getStatus() == Status.REJECTED) {
            log.info("Все заявки отклонены");
            requests.forEach(r -> {
                r.setStatus(Status.REJECTED);
            });
            requestRepository.saveAll(requests);
            return RequestMapper.mapToRequestUpdateStatusOutDto(new ArrayList<>(), requests);
        }
        reqConfirmed = requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED);
        if (Objects.equals(reqConfirmed, event.getParticipantLimit())) {
            throw new ConflictException("У события достигнут лимит участников!");
        }

        List<Request> confRequests = new ArrayList<>();
        List<Request> rejRequests = new ArrayList<>(requests);

        for (Request req : requests) {
            if (reqConfirmed < limit) {
                req.setStatus(Status.CONFIRMED);
                confRequests.add(req);
                rejRequests.remove(req);
                requestRepository.save(req);
                reqConfirmed++;
            } else {
                log.info("Лимит заявок для события исчерпан!");
                break;
            }
        }

        if (rejRequests.size() > 0) {
            rejRequests.forEach(r -> {
                r.setStatus(Status.REJECTED);
                requestRepository.save(r);
            });
            requestRepository.saveAll(rejRequests);
        }
        return RequestMapper.mapToRequestUpdateStatusOutDto(confRequests, rejRequests);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        Request request = requestRepository.findById(requestId).get();

        if (request.getRequester().getId() != userId) {
            log.error("Пользователь не инициатор заявки!");
            throw new ConflictException("Пользователь не инициатор заявки!");
        }
        request.setStatus(Status.CANCELED);
        log.info("Отменен запрос с id = {}", requestId);
        requestRepository.save(request);
        return RequestMapper.mapToParticipationRequestDto(request);
    }


    private User checkingExistUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Event checkingExistEvent(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%s не найдено", eventId)));
    }
}
