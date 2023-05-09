package ru.practicum.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationJpaRepository extends JpaRepository<Compilation, Integer> {
    List<Compilation> findCompilationByPinnedIs(Boolean pinned, Pageable pageable);
}
