package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationNewDto {
    @NotNull(message = "CompilationNewDto. Field: events не задано")
    private List<Integer> events;
    @Value("false")
    private Boolean pinned;
    @NotBlank(message = "CompilationDto. Field: title не может быть пустым или содержать только пробелы")
    private String title;
}
