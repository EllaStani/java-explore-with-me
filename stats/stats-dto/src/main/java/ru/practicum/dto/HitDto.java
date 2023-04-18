package ru.practicum.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private String app;
    private String uri;
    private long hits;
}
