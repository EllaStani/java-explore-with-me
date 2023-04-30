package ru.practicum.event;

import ru.practicum.common.SortMethod;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAdminEvents(int[] users, String[] states, int[] categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      int from, int size);

    List<EventShortDto> getPublicEventsWithSort(String text, int[] categories, boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                boolean onlyAvailable, SortMethod sort, int from,
                                                int size, String ip);

    EventFullDto getPublicEventById(int eventId, String ip);

    List<EventShortDto> getPrivateEventsByUserId(int userId, int from, int size);

    EventFullDto getPrivateEventByEventIdAndUserId(int userId, int eventId);

    EventFullDto saveNewEvent(int userId, EventNewDto eventNewDto);

    EventFullDto updatePrivateEvent(int userId, int eventId, EventUpdateDto eventUpdateDto);

    EventFullDto updateAdminEvent(int eventId, EventUpdateDto eventUpdateDto);

//    List<Event> getEventsByIds(List<Integer> events);
//    EventDtoOutput getByIdWithCount(Integer id, HttpServletRequest request);
//    List<Event> getFilteredEvents(
//            Integer users, String state, Integer categories,
//            String rangeStart,String rangeEnd, Integer from, Integer size);
//    List<Event> getByCategoryId(Integer id);
}
