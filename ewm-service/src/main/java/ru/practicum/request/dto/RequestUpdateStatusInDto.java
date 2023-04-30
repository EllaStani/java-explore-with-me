package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.common.StatusRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateStatusInDto {
    @NotNull(message = "UpdateStatusRequestsInDto. Field: requestIds  не задано")
    @NotEmpty(message = "UpdateStatusRequestsInDto. Field: requestIds не может быть пустым")
    private List<Integer> requestIds;
    @NotNull(message = "UpdateStatusRequestsInDto. Field: status не задано")
    @NotBlank(message = "UpdateStatusRequestsInDto. Field: status не может быть пустым")
    private StatusRequest statusRequest;

}
