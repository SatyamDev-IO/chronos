package com.example.chronos.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CreateJobRequest {
    private String command;
    private LocalDateTime runAt;
    private Integer repeatIntervalSeconds;
}
