package ru.practicum.user;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(int[] ids, Integer from, Integer size);

    UserDto saveNewUser(UserDto userDto);

    void deleteUserById(int userId);
}
