package com.example.chronos.entity;

import com.example.chronos.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String command;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime nextRunTime;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer repeatIntervalSeconds;
    @Column(nullable = false)
    private String createdBy;

}
