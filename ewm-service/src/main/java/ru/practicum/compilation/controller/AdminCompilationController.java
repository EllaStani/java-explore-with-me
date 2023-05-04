package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto saveNewCompilation(@Validated @RequestBody CompilationNewDto compilationNewDto) {
        CompilationDto compilationDto = compilationService.saveNewCompilation(compilationNewDto);
        log.info("API AdminCompilation. POST: Добавлена новая подборка  {}", compilationDto);
        return compilationDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable int compId,
                                            @Validated @RequestBody CompilationNewDto compilationNewDto) {
        CompilationDto compilationDto = compilationService.updateCompilation(compId, compilationNewDto);
        log.info("API AdminCompilation. PATCH: Изменены данные подборки {}, compId={}", compilationDto, compId);
        return compilationDto;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilationById(compId);
        log.info("API AdminCompilation. DELETE: Удалена подборка compId={}", compId);
    }
}
