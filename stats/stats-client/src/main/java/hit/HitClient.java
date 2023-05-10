package hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class HitClient {
    @Value("http://stats-server:9090")
    private String local;
    private final RestTemplate restTemplate = new RestTemplate();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<HitDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("HitClient. Запрос на получение статистики: uris={},start={}, end={},  unique={}",
                uris, start, end, unique);

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Не задан временной промежуток.");
        }

        String startFormat = start.format(dateTimeFormatter);
        String endFormat = end.format(dateTimeFormatter);
        StringBuilder uriBuilder = new StringBuilder(local + "/stats?start=" + startFormat +
                "&end=" + endFormat);

        if (uris != null && !uris.isEmpty() && uris.size() != 0) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }
        ResponseEntity<HitDto[]> list = restTemplate.getForEntity(uriBuilder.toString(), HitDto[].class);

        return Arrays.asList(Objects.requireNonNull(list.getBody()));
    }

    public void saveNewHit(String ip, String uri, String app) {
        HitInDto hitInDto = new HitInDto(0, ip, uri, app, LocalDateTime.now());
        log.info("HitClient. Запрос на сохранение статистики: {}", hitInDto);
        restTemplate.postForLocation(local + "/hit", hitInDto);
    }
}
