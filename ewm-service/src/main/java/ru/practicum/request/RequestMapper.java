package ru.practicum.request;

import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestUpdateStatusOutDto;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setStatusRequest(request.getStatusRequest());
        return requestDto;
    }

    public static List<ParticipationRequestDto> mapToListParticipationRequestDto (List<Request> requests){
        List<ParticipationRequestDto> requestDtos = requests.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
        return requestDtos;
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