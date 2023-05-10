package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNewDto {
    @NotBlank(message = "CategoryInDto. Field: name не может быть пустым или содержать только пробелы")
    private String name;
}
