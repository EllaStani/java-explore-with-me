package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryJpaRepository categoryRepository;
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Sort startSort = Sort.by("name");
        Pageable pageable = FromSizeRequest.of(from, size, startSort);
        Page<Category> categories = categoryRepository.findAll(pageable);
        log.info("CategoryService: Данные о всех категориях, сортировка по name");
        return CategoryMapper.mapToListCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(int catId) {
        Category category = checkingExistCategory(catId);
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto saveNewCategory(CategoryDto categoryDto) {
        Category newCategory = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));
        log.info("CategoryService: Добавлена категория: {}", newCategory);
        return CategoryMapper.mapToCategoryDto(newCategory);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(int catId, CategoryDto categoryDto) {
        Category updateCategory = checkingExistCategory(catId);
        updateCategory.setName(categoryDto.getName());
        categoryRepository.save(updateCategory);
        return CategoryMapper.mapToCategoryDto(updateCategory);
    }

    @Transactional
    @Override
    public void deleteCategoryById(int catId) {
        checkingExistCategory(catId);
        if (checkingExistEvents(catId)) {
            log.info("CategoryService: Удаление категории с id {}", catId);
            categoryRepository.deleteById(catId);
        }
    }

    private Category checkingExistCategory(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id=%s не найдена", catId)));
    }

    private Boolean checkingExistEvents(int catId) {
//        if (eventsRepository.findById(catId).size() > 0) {
//            throw new ConflictException("Нельзя удалить категорию: существуют события, связанные с категорией");
//        }
        return true;
    }
}
