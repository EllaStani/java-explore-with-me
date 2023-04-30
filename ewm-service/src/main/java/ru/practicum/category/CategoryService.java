package ru.practicum.category;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(int catId);

    CategoryDto saveNewCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(int catId, CategoryDto categoryDto);

    void deleteCategoryById(int catId);
}
