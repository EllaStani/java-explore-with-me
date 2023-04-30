package ru.practicum.event;


import ru.practicum.category.Category;
import ru.practicum.category.CategoryDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserShortDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventMapper {
    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventFullDto.setLocationDto(new LocationDto(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());

//        eventShortDto.setViews(Long.valueOf(0));
//        eventShortDto.setConfirmedRequests(event.get);
        return eventFullDto;
        }

    public static List<EventFullDto> mapToListEventFullDto(List<Event> events) {
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
        return eventFullDtos;
    }

    public static List<EventFullDto> mapToListEventFullDtoWithViews(List<Event> events, Map<Integer, String> statViewsMap) {
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::mapToEventFullDto)
//                .map(e -> e.setViews())
                .collect(Collectors.toList());
        eventFullDtos.forEach(e -> {
            e.setViews(Long.valueOf(statViewsMap.get(e.getId())));
        });
        return eventFullDtos;
    }

    public  static Event mapToEvent (User initiator, Category category, EventNewDto eventNewDto) {
        Event event = new Event();
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setAnnotation(eventNewDto.getAnnotation());
        event.setDescription(eventNewDto.getDescription());
        event.setEventDate(eventNewDto.getEventDate());
        if (eventNewDto.getLocationDto() != null) {
            event.setLat(eventNewDto.getLocationDto().getLat());
            event.setLon(eventNewDto.getLocationDto().getLon());
        }
        event.setPaid(eventNewDto.getPaid());
        event.setParticipantLimit(eventNewDto.getParticipantLimit());
        event.setRequestModeration(eventNewDto.getRequestModeration());
        event.setTitle(eventNewDto.getTitle());
        return event;
    }

    public static EventFullDto mapToNewEventDto(UserShortDto userShortDto, CategoryDto categoryDto, Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryDto);
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setInitiator(userShortDto);
        eventFullDto.setLocationDto(new LocationDto(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
//        if (event.getPublishedOn() != null) {
//            eventFullDto.setPublishedOn(event.getPublishedOn());
//        }
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(0L);
        return eventFullDto;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
//        eventShortDto.setViews(Long.valueOf(0));
//        eventShortDto.setConfirmedRequests(event.get);
        return eventShortDto;
    }

    public static List<EventShortDto> mapToListEventShortDto(List<Event> events) {
        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
        return eventShortDtos;
    }
}
