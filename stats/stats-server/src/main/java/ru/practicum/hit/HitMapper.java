package ru.practicum.hit;

import ru.practicum.dto.HitInDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Hit mapToHit(HitInDto hitDto) {
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setApp(hitDto.getApp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), formatter));
        return hit;
    }

    public static HitInDto mapToHitInDto(Hit hit) {
        HitInDto hitDto = new HitInDto();
        hitDto.setId(hit.getId());
        hitDto.setIp(hit.getIp());
        hitDto.setUri(hit.getUri());
        hitDto.setApp(hit.getApp());
        hitDto.setTimestamp(hit.getTimestamp().toString());
        return hitDto;
    }
}
