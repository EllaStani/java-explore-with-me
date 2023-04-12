package ru.practicum.hit;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @GetMapping(value = "/stats")
    public List<HitDto> getHits(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) String[] uri,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Stats: Get stats on views for uri={}, unique is {}", uri, unique);
        return hitService.getHits(start, end, uri, unique);
    }

    @PostMapping(value = "/hit")
    public HitInDto saveNewHit(@RequestBody HitInDto hitDto) {
        HitInDto newHitDto = hitService.saveNewHit(hitDto);
        log.info("Stats: Save new hit {}", newHitDto);
        return newHitDto;
    }

}
