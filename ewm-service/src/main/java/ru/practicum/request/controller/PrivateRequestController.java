package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateStatusInDto;
import ru.practicum.request.dto.RequestUpdateStatusOutDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable int userId) {
        List<ParticipationRequestDto> requestDtos = requestService.getRequestsByUserId(userId);
        log.info("API PrivateRequest. GET:  найдены запросы для пользователя с userId={} : {}", userId, requestDtos);
        return requestDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestByUserIdAndEventId(@PathVariable int userId,
                                                                      @PathVariable int eventId) {
        List<ParticipationRequestDto> requestDtos = requestService.getRequestByUserIdAndEventId(userId, eventId);
        log.info("API PrivateRequest. GET:  найдены запросы с userId={}, eventId={}: {}", userId, eventId, requestDtos);
        return requestDtos;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto saveNewRequest(@PathVariable int userId,
                                                  @RequestParam(value = "eventId") int eventId) {
        ParticipationRequestDto requestDto = requestService.saveNewRequest(userId, eventId);
        log.info("API PrivateRequest. POST: Добавлен запрос для userId={} eventId={} : {}", userId, eventId, requestDto);
        return requestDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{userId}/events/{eventId}/requests")
    public RequestUpdateStatusOutDto updateStatus(@PathVariable(value = "userId") int userId,
                                                  @PathVariable(value = "eventId") int eventId,
                                                  @RequestBody RequestUpdateStatusInDto requestUpdateInDto) {
        RequestUpdateStatusOutDto requestUpdateOutDto = requestService.updateStatus(userId, eventId, requestUpdateInDto);
        log.info("API PrivateEvent. PATCH: Изменены данные запроса userId={}, eventId={} : {}",
                userId, eventId, requestUpdateOutDto);
        return requestUpdateOutDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(value = "userId") int userId,
                                                 @PathVariable(value = "requestId") int requestId) {
        ParticipationRequestDto requestDto = requestService.cancelRequest(userId, requestId);
        log.info("API PrivateEvent. PATCH: Пользователь с userId={} отменил запрос на участие requestId={}: ",
                userId, requestId);
        return requestDto;
    }
}
