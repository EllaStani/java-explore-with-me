package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventNewDto {
    @NotNull(message = "EventNewDto. Field: annotation не задано")
    @NotBlank(message = "EventNewDto. Field: annotation не может быть пустым или содержать только пробелы")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "EventNewDto. Field: category не задано")
    private Integer category;

    @NotNull(message = "EventNewDto. Field: description не задано")
    @NotBlank(message = "EventNewDto. Field: description не может быть пустым или содержать только пробелы")
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull(message = "EventNewDto. Field: eventDate не задано")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "EventNewDto. Field: location не задано")
    private Location location;

    @Value("false")
    private Boolean paid;

    @PositiveOrZero
    @Value("0")
    private Integer participantLimit;

    @Value("true")
    private Boolean requestModeration;

    @NotNull(message = "EventNewDto. Field: title не задано")
    @NotBlank(message = "EventNewDto. Field: title не может быть пустым или содержать только пробелы")
    @Size(min = 3, max = 120)
    private String title;
}
