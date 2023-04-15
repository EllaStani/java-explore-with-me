package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitInDto {
    private int id;
    @NotBlank
    private String ip;
    @NotBlank
    private String uri;
    @NotBlank
    private String app;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
}
