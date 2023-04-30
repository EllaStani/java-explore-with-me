package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Integer id;
    @NotNull(message = "CategoryDto. Field: name не задан")
    @NotBlank(message = "CategoryDto. Field: name не может быть пустым или содержать только пробелы")
    private String name;
}
