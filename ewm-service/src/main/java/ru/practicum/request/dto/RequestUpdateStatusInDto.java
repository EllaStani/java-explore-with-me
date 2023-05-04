package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateStatusInDto {
    private List<Integer> requestIds;
    private Status status;
}
