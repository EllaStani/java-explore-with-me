package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<Category, Integer> {
    List<Category> findCategoryByIdIn(List<Integer> ids);
}
