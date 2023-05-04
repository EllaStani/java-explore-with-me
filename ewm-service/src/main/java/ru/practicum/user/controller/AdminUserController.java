package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class AdminUserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> getUsersByIds(
            @RequestParam(value = "ids", required = false) int[] ids,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        List<UserDto> userDtos = userService.getUsers(ids, from, size);
        log.info("API AdminUser. Get-запрос: пользователи по списку={}, from = {} size = {}", ids, from, size);
        log.info("API AdminUser. Get-запрос: найдено {} пользователей: {}", userDtos.size(), userDtos);
        return userDtos;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto saveNewUser(@Validated @RequestBody UserDto userDto) {
        UserDto newUserDto = userService.saveNewUser(userDto);
        log.info("API AdminUser. Post-запрос: Пользователь зарегистрирован {}", newUserDto);
        return newUserDto;
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUserById(userId);
        log.info("API AdminUser. Delete-запрос: Удален пользователь userId={}", userId);
    }
}
