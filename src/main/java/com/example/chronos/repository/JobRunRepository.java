package com.example.chronos.repository;

import com.example.chronos.entity.JobRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRunRepository extends JpaRepository<JobRunEntity, Long> {
    List<JobRunEntity> findByJobId(Long jobId);
}
