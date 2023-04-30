package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotNull(message = "UserDto. Field: name не задан")
    @NotBlank(message = "UserDto. Field: name не может быть пустым или содержать только пробелы")
    private String name;
    @NotNull(message = "UserDto. Field: email не задан")
    @Email(message = "UserDto. Field: email не соответствует формату электронной почты")
    private String email;
}
