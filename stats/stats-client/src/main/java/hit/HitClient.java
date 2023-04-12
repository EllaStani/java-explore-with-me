package hit;

import ru.practicum.dto.HitInDto;
import client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

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

    public ResponseEntity<Object> getHits(String start, String end, String[] uri, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uri", uri,
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uri={{uri}}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> saveNewHit(HitInDto hitDto) {
        return post("/hit", hitDto);
    }
}
