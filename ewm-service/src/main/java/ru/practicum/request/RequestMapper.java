package ru.practicum.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateStatusOutDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }

    public static List<ParticipationRequestDto> mapToListParticipationRequestDto(List<Request> requests) {
        return requests.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public static RequestUpdateStatusOutDto mapToRequestUpdateStatusOutDto(
            List<Request> confirmedRequests,
            List<Request> rejectedRequests) {

        List<ParticipationRequestDto> confirmedRequestDtos = confirmedRequests.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequestDtos = rejectedRequests.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());

        return new RequestUpdateStatusOutDto(confirmedRequestDtos, rejectedRequestDtos);
    }
}
