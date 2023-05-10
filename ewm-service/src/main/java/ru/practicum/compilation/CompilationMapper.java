package ru.practicum.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(compilation.getEvents());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }

    public static List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations) {
        List<CompilationDto> compilationDtos = compilations.stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
        return compilationDtos;
    }

    public Compilation mapToCompilation(CompilationNewDto compilationNewDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationNewDto.getPinned());
        compilation.setTitle(compilationNewDto.getTitle());
        return compilation;
    }
}
