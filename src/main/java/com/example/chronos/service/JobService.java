package com.example.chronos.service;

import com.example.chronos.dto.CreateJobRequest;
import com.example.chronos.entity.JobEntity;
import com.example.chronos.entity.JobRunEntity;
import com.example.chronos.enums.Status;
import com.example.chronos.exception.NotFoundException;
import com.example.chronos.exception.UnauthorizedException;
import com.example.chronos.repository.JobRepository;
import com.example.chronos.repository.JobRunRepository;
import com.example.chronos.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRunRepository jobRunRepository;

    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);

    public JobEntity createJob(CreateJobRequest request) {

        JobEntity job = new JobEntity();
        job.setCommand(request.getCommand());
        job.setStatus(Status.SCHEDULED);
        job.setNextRunTime(request.getRunAt()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime());
        job.setRetryCount(0);
        job.setMaxRetries(3);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        job.setRepeatIntervalSeconds(request.getRepeatIntervalSeconds());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        job.setCreatedBy(auth.getName());
        return jobRepository.save(job);
    }

    public JobEntity getJob(Long id) {
        return getAuthorizedJob(id);
    }

    public List<JobRunEntity> getJobRuns(Long jobId) {
        getAuthorizedJob(jobId);
        return jobRunRepository.findByJobId(jobId);
    }

    public List<JobEntity> getMyJobs() {
        return jobRepository.findByCreatedBy(getCurrentUser());
    }

    public void cancelJob(Long id) {
        JobEntity job = getAuthorizedJob(id);
        job.setStatus(Status.CANCELLED);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
        log.info("Job cancelled: id={}, user={}", job.getId(), job.getCreatedBy());
    }

    public JobEntity rescheduleJob(Long id, String  newTime) {
        JobEntity job = getAuthorizedJob(id);
        LocalDateTime parsedTime = Instant.parse(newTime)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        job.setNextRunTime(parsedTime);
        job.setStatus(Status.SCHEDULED);
        job.setUpdatedAt(LocalDateTime.now());
        JobEntity savedJob = jobRepository.save(job);

        log.info("Job rescheduled: id={}, nextRunTime={}", savedJob.getId(), savedJob.getNextRunTime());

        return savedJob;
    }

    private JobEntity getAuthorizedJob(Long jobId) {

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (!job.getCreatedBy().equals(getCurrentUser())) {
            throw new UnauthorizedException("Unauthorized access");
        }

        return job;
    }

    private String getCurrentUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }


}
