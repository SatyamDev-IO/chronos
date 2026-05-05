package com.example.chronos.repository;

import com.example.chronos.entity.JobEntity;
import com.example.chronos.entity.JobRunEntity;
import com.example.chronos.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    List<JobEntity> findTop10ByStatusAndNextRunTimeBefore(Status status, LocalDateTime time);

    @Modifying
    @Query("UPDATE JobEntity j SET j.status = :newStatus, j.updatedAt = :updatedAt WHERE j.id = :jobId AND j.status = :currentStatus")
    int updateStatusIfMatch(Long jobId, Status currentStatus, Status newStatus, LocalDateTime updatedAt);

    List<JobEntity> findTop10ByStatus(Status status);


    List<JobEntity> findByCreatedBy(String currentUser);
}
