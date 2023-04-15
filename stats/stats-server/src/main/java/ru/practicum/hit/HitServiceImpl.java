package ru.practicum.hit;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitInDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
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
        Hit newHit = hitRepository.save(HitMapper.mapToHit(hitDto));
        return newHit == null ? null : HitMapper.mapToHitInDto(newHit);
    }
}
