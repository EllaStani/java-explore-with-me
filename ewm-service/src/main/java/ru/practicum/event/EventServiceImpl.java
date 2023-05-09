package ru.practicum.event;

import hit.HitClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.CategoryJpaRepository;
import ru.practicum.category.CategoryMapper;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentJpaRepository;
import ru.practicum.comment.CommentMapper;
import ru.practicum.common.*;
import ru.practicum.dto.HitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.Request;
import ru.practicum.request.RequestJpaRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserJpaRepository;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
@RequiredArgsConstructor
@ComponentScan(basePackages = {"hit"})
public class EventServiceImpl implements EventService {
    private final EventJpaRepository eventRepository;
    private final UserJpaRepository userRepository;
    private final RequestJpaRepository requestRepository;
    private final CategoryJpaRepository categoryRepository;
    private final CommentJpaRepository commentRepository;
    private final HitClient hitClient;
    private final String app = "evm-service";
    private final LocalDateTime minStart = LocalDateTime.now().minusYears(100L);
    private final LocalDateTime maxEnd = LocalDateTime.now().plusYears(100L);

    @Override
    public List<EventFullDto> getAdminEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Event> events = eventRepository.getEventsFromAdmin(
                users, states, categories, rangeStart, rangeEnd, pageable);
        return makeEventFullDtoList(events);
    }

    @Override
    public List<EventShortDto> getPublicEventsWithSort(String text, List<Integer> categories, boolean paid,
                                                       LocalDateTime start, LocalDateTime end, boolean onlyAvailable,
                                                       SortMethod sortMethod, int from, int size, String ip) {
        Pageable pageable;
        if (sortMethod == SortMethod.EVENT_DATE) {
            Sort sort = Sort.by("eventDate");
            pageable = FromSizeRequest.of(from, size, sort);
        } else {
            pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        }
        if (text != null) {
            text = text.toLowerCase();
        }
        List<Event> events = eventRepository.getEventsWithSort(categories, start, end, text, paid, pageable);
        hitClient.saveNewHit(ip, "/events", app);

        List<EventFullDto> eventFullDtos = makeEventFullDtoList(events);
        if (!onlyAvailable) {
            eventFullDtos.stream()
                    .filter(e -> e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = EventMapper.mapToListEventShortFromFullDto(eventFullDtos);

        if (sortMethod == SortMethod.VIEWS) {
            eventShortDtos.stream()
                    .sorted((e1, e2) -> {
                        if (e1.getViews() > e2.getViews())
                            return 1;
                        else if (e1.getViews() < e2.getViews())
                            return -1;
                        else return 0;
                    })
                    .collect(Collectors.toList());
            return eventShortDtos;
        } else {
            return eventShortDtos;
        }
    }

    @Override
    public EventFullDto getPublicEventById(int eventId, String ip) {
        Event event = checkingExistEvent(eventId);
        hitClient.saveNewHit(ip, "/events/" + eventId, app);

        if (event.getState() == State.PUBLISHED) {
            EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
            eventFullDto.setConfirmedRequests(requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED));
            eventFullDto.setViews(hitClient.getHits(
                    minStart, maxEnd, List.of("/events/" + event.getId()), false).size());
            eventFullDto.setComments(CommentMapper.mapToListCommentDto(commentRepository.findCommentByEventId(eventId)));
            return eventFullDto;
        } else {
            log.error("Событие не опубликовано!");
            throw new ConflictException("Событие не опубликовано!");
        }
    }

    @Override
    public EventFullDto getPrivateEventByEventIdAndUserId(int userId, int eventId) {
        Event event = checkingExistEventByUserId(userId, eventId);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED));
        eventFullDto.setViews(hitClient.getHits(
                minStart, maxEnd, List.of("/events/" + event.getId()), false).size());
        eventFullDto.setComments(CommentMapper.mapToListCommentDto(commentRepository.findCommentByEventId(eventId)));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getPrivateEventsByUserId(int userId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findEventByInitiatorId(userId, pageable);
        List<EventFullDto> eventFullDtos = makeEventFullDtoList(events);
        return EventMapper.mapToListEventShortFromFullDto(eventFullDtos);
    }

    @Transactional
    @Override
    public EventFullDto saveNewEvent(int userId, EventNewDto eventNewDto) {
        if (eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            log.info("Дата и время события {} - не может быть раньше, чем через два часа от текущего момента",
                    eventNewDto.getEventDate());
            throw new ConflictException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
        }

        User user = checkingExistUser(userId);
        Category category = checkingExistCategory(eventNewDto.getCategory());
        Event event = EventMapper.mapToEvent(user, category, eventNewDto);

        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        UserShortDto userShortDto = UserMapper.mapToUserShortDto(checkingExistUser(userId));
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(checkingExistCategory(eventNewDto.getCategory()));
        Event newEvent = eventRepository.save(event);
        return EventMapper.mapToNewEventDto(userShortDto, categoryDto, newEvent);
    }

    @Transactional
    @Override
    public EventFullDto updatePrivateEvent(int userId, int eventId, EventUpdateDto eventUpdateDto) {
        Event event = checkingExistEventByUserId(userId, eventId);
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                log.info("Дата и время события {} - не может быть раньше, чем через два часа от текущего момента",
                        eventUpdateDto.getEventDate());
                throw new ConflictException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
            }
        }
        if (event.getState() == State.PUBLISHED) {
            log.error("Нельзя изменить опубликованное событие.");
            throw new ConflictException("Нельзя изменить опубликованное событие.");
        }
        updateEvent(event, eventUpdateDto);

        eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED));
        eventFullDto.setViews(hitClient.getHits(
                minStart, maxEnd, List.of("/events/" + event.getId()), false).size());
        eventFullDto.setComments(CommentMapper.mapToListCommentDto(commentRepository.findCommentByEventId(eventId)));
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateAdminEvent(int eventId, EventUpdateDto eventUpdateDto) {
        Event event = checkingExistEvent(eventId);
        if (eventUpdateDto.getEventDate() != null && event.getPublishedOn() != null) {
            if (eventUpdateDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                log.info("Дата начала события {} - не может быть раньше, чем за час от даты публикации",
                        eventUpdateDto.getEventDate());
                throw new ConflictException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
            }
        }

        updateEvent(event, eventUpdateDto);
        eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository.getCountConfirmedRequest(eventId, Status.CONFIRMED));
        eventFullDto.setViews(hitClient.getHits(
                minStart, maxEnd, List.of("/events/" + event.getId()), false).size());
        eventFullDto.setComments(CommentMapper.mapToListCommentDto(commentRepository.findCommentByEventId(eventId)));
        return eventFullDto;
    }

    private User checkingExistUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Event checkingExistEvent(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Событие с id=%s не найдено", eventId)));
    }

    private Event checkingExistEventByUserId(int userId, int eventId) {
        Event event = checkingExistEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ConflictException("Пользователь не инициатор события.");
        }
        return event;
    }

    private Category checkingExistCategory(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new ConflictException(String.format("Категория с id=%s не найдена", catId)));
    }

    private void updateEvent(Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now())) {
                log.info("Нельзя изменить дату события {} на уже наступившую.",
                        eventUpdateDto.getEventDate());
                throw new ConflictException("Нельзя изменить дату события на уже наступившую.");
            }
        }
        if (eventUpdateDto.getAnnotation() != null) {
            if (!eventUpdateDto.getAnnotation().isBlank()) {
                event.setAnnotation(eventUpdateDto.getAnnotation());
            }
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(checkingExistCategory(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null) {
            if (!eventUpdateDto.getDescription().isBlank()) {
                event.setDescription(eventUpdateDto.getDescription());
            }
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLat(eventUpdateDto.getLocation().getLat());
            event.setLon(eventUpdateDto.getLocation().getLon());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getTitle() != null) {
            if (!eventUpdateDto.getTitle().isBlank()) {
                event.setTitle(eventUpdateDto.getTitle());
            }
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW.toString())) {
            if (event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
            } else {
                throw new ConflictException("Отменить можно только событие в состоянии ожидания модерации.");
            }
        }
        if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW.toString())) {
            event.setState(State.PENDING);
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())) {
            if (event.getState() == State.PENDING) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());

            } else {
                if (event.getState() == State.CANCELED) {
                    log.error("Событие в состоянии CANCELED не может быть опубликовано.");
                    throw new ConflictException("Событие в состоянии CANCELED не может быть опубликовано.");
                }
                if (event.getState() == State.PUBLISHED) {
                    log.error("Событие уже опубликовано.");
                    throw new ConflictException("Событие уже опубликовано.");
                }
            }
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT.toString())) {
            if (event.getState() == State.PENDING) {
                event.setState(State.CANCELED);
            } else if (event.getState() == State.PUBLISHED) {
                throw new ConflictException("Событие уже опубликовано.");
            }
        }
    }

    private List<EventFullDto> makeEventFullDtoList(List<Event> events) {
        List<EventFullDto> eventFullDtos = EventMapper.mapToListEventFullDto(events);
        List<Integer> ids = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        events.forEach(event -> {
            ids.add(event.getId());
            uris.add("/events/" + event.getId());
        });

        eventFullDtos = setConfRequestEvent(eventFullDtos, ids);
        eventFullDtos = setViewsEvent(eventFullDtos, uris);
        eventFullDtos = setCommentsEvent(eventFullDtos, ids);
        return eventFullDtos;
    }

    private List<EventFullDto> setConfRequestEvent(List<EventFullDto> eventFullDtos, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = requestRepository.findRequestByEventIdInAndStatus(
                        eventIds, Status.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(Request::getIdEvent));

        return eventFullDtos.stream()
                .map(eventFullDto -> setCountConfRequests(eventFullDto,
                        confRequestMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private EventFullDto setCountConfRequests(EventFullDto eventFullDto, int countConfRequests) {
        eventFullDto.setConfirmedRequests(countConfRequests);
        return eventFullDto;
    }

    private List<EventFullDto> setViewsEvent(List<EventFullDto> eventFullDtos, List<String> uris) {
        Map<String, List<HitDto>> statViewsMap = hitClient.getHits(minStart, maxEnd, uris, false)
                .stream()
                .collect(Collectors.groupingBy(HitDto::getUri));

        return eventFullDtos.stream()
                .map(eventFullDto -> setCountViews(eventFullDto,
                        statViewsMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private EventFullDto setCountViews(EventFullDto eventFullDto, int countViews) {
        eventFullDto.setViews(countViews);
        return eventFullDto;
    }

    private List<EventFullDto> setCommentsEvent(List<EventFullDto> eventFullDtos, List<Integer> eventIds) {
        Map<Integer, List<Comment>> commentsMap = commentRepository.findCommentByEventIdIn(eventIds)
                .stream()
                .collect(Collectors.groupingBy(Comment::getIdEvent));
        return eventFullDtos.stream()
                .map(eventFullDto -> setComments(eventFullDto,
                        commentsMap.getOrDefault(eventFullDto.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    private EventFullDto setComments(EventFullDto eventFullDto, List<Comment> comments) {
        eventFullDto.setComments(CommentMapper.mapToListCommentDto(comments));
        return eventFullDto;
    }
}
