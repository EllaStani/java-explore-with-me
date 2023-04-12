package ru.practicum.hit;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HitJpaRepository hitRepository;

    @Override
    public List<HitDto> getHits(LocalDateTime start, LocalDateTime end, String[] uri, boolean unique) {
        List<HitDto> hitDtos;
        if (uri == null) {
            if (unique == true) {
                hitDtos = hitRepository.getHitsUniqueIp(start, end);
            } else {
                hitDtos = hitRepository.getHits(start, end);
            }
        } else {
            if (unique == true) {
                hitDtos = hitRepository.getHitsWithUriUniqueIp(start, end, uri);
            } else {
                hitDtos = hitRepository.getHitsWithUri(start, end, uri);
            }
        }
        return hitDtos;
    }

    @Transactional
    @Override
    public HitInDto saveNewHit(HitInDto hitDto) {
        Hit newHit = hitRepository.save(mapToHit(hitDto));
        return newHit == null ? null : mapToHitInDto(newHit);
    }

    private Hit mapToHit(HitInDto hitDto) {
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setApp(hitDto.getApp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), formatter));
        return hit;
    }

    private HitInDto mapToHitInDto(Hit hit) {
        HitInDto hitDto = new HitInDto();
        hitDto.setId(hit.getId());
        hitDto.setIp(hit.getIp());
        hitDto.setUri(hit.getUri());
        hitDto.setApp(hit.getApp());
        return hitDto;
    }
}
