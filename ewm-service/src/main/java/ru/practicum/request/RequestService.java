package ru.practicum.request;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateStatusInDto;
import ru.practicum.request.dto.RequestUpdateStatusOutDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsByUserId(int userId);

    List<ParticipationRequestDto> getRequestByUserIdAndEventId(int userId, int eventId);

    ParticipationRequestDto saveNewRequest(int userId, int eventId);

    RequestUpdateStatusOutDto updateStatus(int userId, int eventId, RequestUpdateStatusInDto updateStatusInDto);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}
