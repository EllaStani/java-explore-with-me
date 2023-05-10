package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.SortMethod;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Validated
public class PublicEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Integer> categories,
            @RequestParam(value = "paid", required = false) boolean paid,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "EVENT_DATE") SortMethod sort,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {

        log.info("API PublicEvent. GET: параметры поиска: text={}, categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={},onlyAvailable={}, sort={}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort);

        List<EventShortDto> eventShortDtos = eventService.getPublicEventsWithSort(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
        log.info("API PublicEvent. GET: Информация о событиях: {}", eventShortDtos);
        return eventShortDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer eventId, HttpServletRequest request) {
        log.info("API PublicEvent. GET: параметры поиска: eventId={}", eventId);
        EventFullDto eventFullDto = eventService.getPublicEventById(eventId, request.getRemoteAddr());
        log.info("API PublicEvent. GET:  найдено событие: {}", eventFullDto);
        return eventFullDto;
    }
}
