package ru.practicum.hit;

import org.springframework.http.HttpStatus;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @GetMapping(path = "/stats")
    public List<HitDto> getHits(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) String[] uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Stats: Get stats on views for uri={}, period from {} to {}, unique is {}", uris, start, end, unique);
        return hitService.getHits(start, end, uris, unique);
    }

    @PostMapping(path = "/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public HitInDto saveNewHit(@RequestBody @Valid HitInDto hitDto) {
        HitInDto newHitDto = hitService.saveNewHit(hitDto);
        log.info("Stats: Save new hit {}", newHitDto);
        return newHitDto;
    }
}
