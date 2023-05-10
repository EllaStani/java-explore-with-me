package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Validated
public class PublicCompilationController {
    private final CompilationService compService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(defaultValue = "false") Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("API PublicCompilation. GET: параметры pinned={} from = {}, size = {}", pinned, from, size);
        List<CompilationDto> categoryDtos = compService.getCompilations(pinned, from, size);
        log.info("API PublicCategory. GET: найдено {} подборок : {}", categoryDtos.size(), categoryDtos);
        return categoryDtos;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilations(@PathVariable int compId) {
        CompilationDto compilationDto = compService.getCompilationById(compId);
        log.info("API PublicCategory. GET: найдена подборка {}, compId={}", compilationDto, compId);
        return compilationDto;
    }
}
