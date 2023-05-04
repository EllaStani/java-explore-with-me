package ru.practicum.hit;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class HitController {
    private final HitService hitService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping(path = "/stats")
    public List<HitDto> getHits(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики Stats. GET: uris={}, period start={}, end={}, unique is {}",
                uris, start, end, unique);
        List<HitDto> hits = hitService.getHits(
                LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter), uris, unique);
        log.info("Stats. GET: по запросу получено: {}", hits);
        return hits;
    }

    @PostMapping(path = "/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveNewHit(@RequestBody @Valid HitInDto hitInDto) {
        log.info("Stats: новый запрос {}", hitInDto);
        hitService.saveNewHit(hitInDto);
    }
}
