package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(value = "users",required = false) int[] users,
            @RequestParam(value = "states", required = false) String[] states,
            @RequestParam(value = "categories",required = false) int[] categories,
            @RequestParam(value = "rangeStart") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam (value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        System.out.println("----- 1 --------");
        List<EventFullDto> eventFullDtos = eventService.getAdminEvents(users, states,categories,
                rangeStart, rangeEnd, from, size);
        log.info("API AdminEvent. Get-запрос: события по users={}, states={}, categories={}", users, categories);
        log.info("from = {} size = {}", from, size);
        log.info("Всего найдено событий={} : {}", eventFullDtos.size(), eventFullDtos);
        return eventFullDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int eventId,
                                    @RequestBody EventUpdateDto eventUpdateDto) {
        System.out.println("----- 2 --------eventUpdateDto= " + eventUpdateDto);
        EventFullDto updateEvent = eventService.updateAdminEvent(eventId, eventUpdateDto);
        log.info("API AdminEvent. Patch-запрос: обновлены данные события с id={} : {}", eventId, updateEvent);
        return updateEvent;
    }
}
