package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentNewDto {
    @NotBlank(message = "EventNewDto. Field: text не может быть пустым или содержать только пробелы")
    @Size(max = 5000)
    private String text;
}
