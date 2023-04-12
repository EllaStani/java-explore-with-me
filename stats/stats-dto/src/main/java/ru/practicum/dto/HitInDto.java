package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitInDto {
    private int id;
    private String ip;
    private String uri;
    private String app;
    private String timestamp;
}
