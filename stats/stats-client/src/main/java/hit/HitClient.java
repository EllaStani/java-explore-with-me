package hit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

@Service
public class HitClient extends BaseClient {
    @Autowired
    public HitClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public List<HitDto> getHits(String start, String end, String[] uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<Object> objectResponseEntity =
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        List<HitDto> hitDtos = new ObjectMapper().convertValue(objectResponseEntity.getBody(), new TypeReference<>() {
        });
        return hitDtos;
    }

    public ResponseEntity<Object> saveNewHit(HitInDto hitDto) {
        return post("/hit", hitDto);
    }
}
