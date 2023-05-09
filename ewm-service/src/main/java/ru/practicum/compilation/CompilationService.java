package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);

    CompilationDto saveNewCompilation(CompilationNewDto compilationNewDto);

    CompilationDto updateCompilation(int compId, CompilationUpdateDto compilationUpdateDto);

    void deleteCompilationById(int compId);
}
