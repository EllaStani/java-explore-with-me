package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto saveNewCompilation(@Valid @RequestBody CompilationNewDto compilationNewDto) {
        log.info("API AdminCompilation. POST: параметры compilationNewDto={}", compilationNewDto);
        CompilationDto compilationDto = compilationService.saveNewCompilation(compilationNewDto);
        log.info("API AdminCompilation. POST: Добавлена новая подборка  {}", compilationDto);
        return compilationDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable int compId,
                                            @Valid @RequestBody CompilationUpdateDto compilationUpdateDto) {
        log.info("API AdminCompilation. PATCH: параметры compId={}, updateDto={}", compId, compilationUpdateDto);
        CompilationDto compilationDto = compilationService.updateCompilation(compId, compilationUpdateDto);
        log.info("API AdminCompilation. PATCH: Изменены данные подборки {}, compId={}", compilationDto, compId);
        return compilationDto;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Integer compId) {
        log.info("API AdminCompilation. DELETE: Удаление подборки compId={}", compId);
        compilationService.deleteCompilationById(compId);
    }
}
