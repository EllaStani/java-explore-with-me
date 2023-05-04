package ru.practicum.event;

import hit.HitClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.CategoryJpaRepository;
import ru.practicum.category.CategoryMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ComponentScan(basePackages = {"hit"})
public class EventServiceImpl implements EventService {
    private final EventJpaRepository eventRepository;
    private final UserJpaRepository userRepository;
    private final RequestJpaRepository requestRepository;
    private final CategoryJpaRepository categoryRepository;
    private final HitClient hitClient;
    private final String app = "evm-service";
    private final LocalDateTime minStart = LocalDateTime.now().minusYears(100L);
    private final LocalDateTime maxEnd = LocalDateTime.now().plusYears(100L);

    @Override
    public List<EventFullDto> getAdminEvents(int[] users, String[] states, int[] categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        checkingExistCategories(categories);

        List<Integer> usersList = new ArrayList<>();
        if (users != null) {
            for (int i = 0; i < users.length; i++) {
                usersList.add(users[i]);
            }
        } else {
            usersList = null;
        }

        List<State> statesList = new ArrayList<>();
        if (states != null) {
            for (int i = 0; i < states.length; i++) {
                statesList.add(State.valueOf(states[i]));
            }
        } else {
            statesList = null;
        }

        List<Integer> categoriesList = new ArrayList<>();
        if (categories != null) {
            for (int i = 0; i < categories.length; i++) {
                categoriesList.add(categories[i]);
            }
        } else {
            categoriesList = null;
        }

        List<Event> events = eventRepository.getEventsFromAdmin(
                usersList, statesList, categoriesList, rangeStart, rangeEnd, pageable);
        return makeEventFullDtoList(events);
    }

    @Override
    public List<EventShortDto> getPublicEventsWithSort(String text, int[] categories, boolean paid,
                                                       LocalDateTime start, LocalDateTime end, boolean onlyAvailable,
                                                       SortMethod sortMethod, int from, int size, String ip) {
        Pageable pageable;

        List<Integer> categoriesList = new ArrayList<>();
        if (categories != null) {
            checkingExistCategories(categories);
            for (int i = 0; i < categories.length; i++) {
                categoriesList.add(categories[i]);
            }
        } else {
            categoriesList = null;
        }
        if (sortMethod == SortMethod.EVENT_DATE) {
            Sort sort = Sort.by("eventDate");
            pageable = FromSizeRequest.of(from, size, sort);
        } else {
            pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        }
        if (text != null) {
            text = text.toLowerCase();
        }
        List<Event> events = eventRepository.getEventsWithSort(categoriesList, start, end, text, paid, pageable);

        hitClient.saveNewHit(HitClient.makeHitInDto(ip, "/events", app));

        List<EventFullDto> eventFullDtos = makeEventFullDtoList(events);
        if (!onlyAvailable) {
            eventFullDtos.stream()
                    .filter(e -> e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = EventMapper.mapToListEventShortFromFullDto(eventFullDtos);

        if (sortMethod == SortMethod.VIEWS) {
            List<EventShortDto> eventShortDtos1 = eventShortDtos.stream()
                    .sorted((e1, e2) -> {
                        if (e1.getViews() > e2.getViews())
                            return 1;
                        else if (e1.getViews() < e2.getViews())
                            return -1;
                        else return 0;
                    })
                    .collect(Collectors.toList());
            return eventShortDtos1;
        } else {
            return eventShortDtos;
        }

    }

    @Override
    public EventFullDto getPublicEventById(int eventId, String ip) {
        Event event = checkingExistEvent(eventId);
        hitClient.saveNewHit(HitClient.makeHitInDto(ip, "/events/" + eventId, app));

        if (event.getState() == State.PUBLISHED) {
            EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

            eventFullDto = setConfRequestEventFullDto(eventFullDto, List.of(event.getId()));
            eventFullDto = setViewsEventFullDto(eventFullDto, List.of("/events/" + event.getId()));
            return eventFullDto;
        } else {
            log.error("Событие не опубликовано!");
            throw new ConflictException("Событие не опубликовано!");
        }
    }

    @Override
    public List<EventShortDto> getPrivateEventsByUserId(int userId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findEventByInitiatorId(userId, pageable);
        return makeEventShortDtoList(events);
    }

    @Override
    public EventFullDto getPrivateEventByEventIdAndUserId(int userId, int eventId) {
        Event event = checkingExistEventByUserId(userId, eventId);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEventFullDto(eventFullDto, List.of(event.getId()));
        eventFullDto = setViewsEventFullDto(eventFullDto, List.of("/events/" + event.getId()));
        return eventFullDto;
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

        if (event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING)) {
            updateEvent(event, eventUpdateDto);
        } else {
            throw new ConflictException("Изменить можно только отмененные события или в состоянии ожидания модерации.");
        }

        eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEventFullDto(eventFullDto, List.of(event.getId()));
        eventFullDto = setViewsEventFullDto(eventFullDto, List.of("/events/" + event.getId()));

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

        List<Integer> ids = new ArrayList<>(eventId);
        List<String> uris = new ArrayList<>();
        uris.add("/events/" + event.getId());

        eventFullDto = setConfRequestEventFullDto(eventFullDto, ids);
        eventFullDto = setViewsEventFullDto(eventFullDto, uris);
        return eventFullDto;
    }

    private User checkingExistUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Event checkingExistEvent(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id=%s не найдено", eventId)));
    }

    private Event checkingExistEventByUserId(int userId, int eventId) {
        Event event = checkingExistEvent(eventId);
        if (event.getInitiator().getId() == userId) {
            new NotFoundException(String.format("Событие с id=%s для пользователя с id=%s не найдено", eventId, userId));
        }
        return event;
    }

    private Category checkingExistCategory(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%s не найдена", catId)));
    }

    private void checkingExistCategories(int[] catId) {
        for (int i = 0; i < catId.length; i++) {
            checkingExistCategory(catId[i]);
        }
    }

    private Event updateEvent(Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now())) {
                log.info("Нельзя изменить дату события {} на уже наступившую.",
                        eventUpdateDto.getEventDate());
                throw new ConflictException("Нельзя изменить дату события на уже наступившую.");
            }
        }
        if (eventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(checkingExistCategory(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null) {
            event.setDescription(eventUpdateDto.getDescription());
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
            event.setTitle(eventUpdateDto.getTitle());
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {    // user
            if (event.getState().equals(State.PENDING)) {
                event.setState(State.CANCELED);
            } else {
                throw new ConflictException("Отменить можно только событие в состоянии ожидания модерации.");
            }
        }
        if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {   // user
            event.setState(State.PENDING);
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {    //admin
            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Событие уже опубликовано");
            }
            if (event.getState().equals(State.CANCELED)) {
                throw new ConflictException("Событие в состоянии CANCELED не может быть опубликовано.");
            }
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }

        if (eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) { //admin
            if (event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Событие уже опубликовано.");
            }
            event.setState(State.CANCELED);
        }
        return event;
    }

    private List<EventFullDto> makeEventFullDtoList(List<Event> events) {
        List<EventFullDto> eventFullDtos = EventMapper.mapToListEventFullDto(events);

        List<Integer> ids = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        events.forEach(event -> {
            ids.add(event.getId());
            uris.add("/events/" + event.getId());
        });

        eventFullDtos = setConfRequestEventFullDtos(eventFullDtos, ids);
        eventFullDtos = setViewsEventFullDtos(eventFullDtos, uris);
        return eventFullDtos;
    }

    private List<EventShortDto> makeEventShortDtoList(List<Event> events) {
        List<EventShortDto> eventShortDtos = EventMapper.mapToListEventShortDto(events);

        List<Integer> ids = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        events.forEach(event -> {
            ids.add(event.getId());
            uris.add("/events/" + event.getId());
        });

        eventShortDtos = setConfRequestEventShortDtos(eventShortDtos, ids);
        eventShortDtos = setViewsEventShortDtos(eventShortDtos, uris);
        return EventMapper.mapToListEventShortDto(events);
    }

    private List<EventFullDto> setConfRequestEventFullDtos(List<EventFullDto> eventFullDtos, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = getConfRequestsMap(eventIds);
        return eventFullDtos.stream()
                .map(eventFullDto -> makeEventFullDtoWithConfRequests(eventFullDto,
                        confRequestMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private List<EventShortDto> setConfRequestEventShortDtos(List<EventShortDto> eventShortDtos, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = getConfRequestsMap(eventIds);
        return eventShortDtos.stream()
                .map(eventShortDto -> makeEventShortDtoWithConfRequests(eventShortDto,
                        confRequestMap.getOrDefault(eventShortDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private EventFullDto setConfRequestEventFullDto(EventFullDto eventFullDto, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = getConfRequestsMap(eventIds);
        return makeEventFullDtoWithConfRequests(eventFullDto,
                confRequestMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size());
    }

    private EventShortDto setConfRequestEventShortDto(EventShortDto eventShortDto, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = getConfRequestsMap(eventIds);
        return makeEventShortDtoWithConfRequests(eventShortDto,
                confRequestMap.getOrDefault(eventShortDto.getId(), Collections.emptyList()).size());
    }

    private Map<Integer, List<Request>> getConfRequestsMap(List<Integer> eventIds) {
        return requestRepository.findRequestByEventIdInAndStatus(eventIds, Status.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(Request::getIdEvent));
    }

    private EventFullDto makeEventFullDtoWithConfRequests(EventFullDto eventFullDto, int count) {
        eventFullDto.setConfirmedRequests(count);
        return eventFullDto;
    }

    private EventShortDto makeEventShortDtoWithConfRequests(EventShortDto eventShortDto, int count) {
        eventShortDto.setConfirmedRequests(count);
        return eventShortDto;
    }

    private List<EventFullDto> setViewsEventFullDtos(List<EventFullDto> eventFullDtos, List<String> uris) {
        Map<String, List<HitDto>> statViewsMap = getStatViewsMap(uris);

        return eventFullDtos.stream()
                .map(eventFullDto -> makeEventFullDtoWithViews(eventFullDto,
                        statViewsMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private List<EventShortDto> setViewsEventShortDtos(List<EventShortDto> eventShortDtos, List<String> uris) {
        Map<String, List<HitDto>> statViewsMap = getStatViewsMap(uris);
        return eventShortDtos.stream()
                .map(eventShortDto -> makeEventShortDtoWithViews(eventShortDto,
                        statViewsMap.getOrDefault(eventShortDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private EventFullDto setViewsEventFullDto(EventFullDto eventFullDto, List<String> uris) {
        Map<String, List<HitDto>> statViewsMap = getStatViewsMap(uris);
        return makeEventFullDtoWithViews(eventFullDto,
                statViewsMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size());
    }

    private EventShortDto setViewsEventShortDto(EventShortDto eventShortDto, List<String> uris) {
        Map<String, List<HitDto>> statViewsMap = getStatViewsMap(uris);
        return makeEventShortDtoWithViews(eventShortDto,
                statViewsMap.getOrDefault(eventShortDto.getId(), Collections.emptyList()).size());
    }

    private Map<String, List<HitDto>> getStatViewsMap(List<String> uris) {
        List<HitDto> hitDtos = hitClient.getHits(minStart, maxEnd, uris, false);
        Map<String, List<HitDto>> map = hitDtos
                .stream()
                .collect(Collectors.groupingBy(HitDto::getUri));
        return map;
    }

    private EventFullDto makeEventFullDtoWithViews(EventFullDto eventFullDto, int count) {
        eventFullDto.setViews(count);
        return eventFullDto;
    }

    private EventShortDto makeEventShortDtoWithViews(EventShortDto eventShortDto, int count) {
        eventShortDto.setViews(count);
        return eventShortDto;
    }
}
