package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentNewDto {
    @NotNull(message = "EventNewDto. Field: text не задано")
    @NotBlank(message = "EventNewDto. Field: text не может быть пустым или содержать только пробелы")
    @Size(max = 5000)
    private String text;
}
