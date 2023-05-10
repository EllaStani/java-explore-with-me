package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryNewDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto saveNewCategory(@Valid @RequestBody CategoryNewDto categoryNewDto) {
        CategoryDto newCategoryDto = categoryService.saveNewCategory(categoryNewDto);
        log.info("API AdminCategory. POST: Добавлена категория  {}", newCategoryDto);
        return newCategoryDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable int catId,
                                      @Valid @RequestBody CategoryNewDto categoryNewDto) {
        CategoryDto updateCategoryDto = categoryService.updateCategory(catId, categoryNewDto);
        log.info("API AdminCategory. PATCH: Изменены данные категории {}, catId={}", updateCategoryDto, catId);
        return updateCategoryDto;
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Integer catId) {
        categoryService.deleteCategoryById(catId);
        log.info("API AdminCategory. DELETE: Удалена категория catId={}", catId);
    }
}
