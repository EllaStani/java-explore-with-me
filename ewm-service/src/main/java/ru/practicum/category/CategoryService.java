package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(int catId);

    CategoryDto saveNewCategory(CategoryNewDto categoryNewDto);

    CategoryDto updateCategory(int catId, CategoryNewDto categoryNewDto);

    void deleteCategoryById(int catId);
}
