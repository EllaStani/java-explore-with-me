package ru.practicum.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.Location;
import ru.practicum.user.User;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        return eventFullDto;
    }

    public static List<EventFullDto> mapToListEventFullDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
    }

    public static Event mapToEvent(User initiator, Category category, EventNewDto eventNewDto) {
        Event event = new Event();
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setAnnotation(eventNewDto.getAnnotation());
        event.setDescription(eventNewDto.getDescription());
        event.setEventDate(eventNewDto.getEventDate());
        if (eventNewDto.getLocation() != null) {
            event.setLat(eventNewDto.getLocation().getLat());
            event.setLon(eventNewDto.getLocation().getLon());
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
        eventFullDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
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
        return eventShortDto;
    }

    public static List<EventShortDto> mapToListEventShortDto(List<Event> events) {
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    public static EventShortDto mapToEventShortFromFullDto(EventFullDto eventFullDto) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(eventFullDto.getId());
        eventShortDto.setAnnotation(eventFullDto.getAnnotation());
        eventShortDto.setCategory(eventFullDto.getCategory());
        eventShortDto.setEventDate(eventFullDto.getEventDate());
        eventShortDto.setInitiator(eventFullDto.getInitiator());
        eventShortDto.setPaid(eventFullDto.isPaid());
        eventShortDto.setTitle(eventFullDto.getTitle());
        eventShortDto.setViews(eventFullDto.getViews());
        eventShortDto.setConfirmedRequests(eventFullDto.getConfirmedRequests());
        eventShortDto.setComments(eventFullDto.getComments());
        return eventShortDto;
    }

    public static List<EventShortDto> mapToListEventShortFromFullDto(List<EventFullDto> eventFullDtos) {
        return eventFullDtos.stream()
                .map(EventMapper::mapToEventShortFromFullDto)
                .collect(Collectors.toList());
    }
}
