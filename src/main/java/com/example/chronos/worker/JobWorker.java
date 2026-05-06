package com.example.chronos.worker;

import com.example.chronos.entity.JobEntity;
import com.example.chronos.entity.JobRunEntity;
import com.example.chronos.enums.Status;
import com.example.chronos.repository.JobRepository;
import com.example.chronos.repository.JobRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobWorker {
    private final JobRepository jobRepository;
    private final JobRunRepository jobRunRepository;
    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);

    public JobWorker(JobRepository jobRepository, JobRunRepository jobRunRepository) {
        this.jobRepository = jobRepository;
        this.jobRunRepository = jobRunRepository;
    }


    // Migrated system from polling-based execution to event-driven architecture using Kafka,
    // reducing unnecessary database load and improving scalability.
    @Transactional(noRollbackFor = Exception.class)
    public void processSingleJob(Long jobId) throws Exception {

        JobEntity job = jobRepository.findById(jobId).orElseThrow();

        if (job.getStatus() == Status.FAILED) {
            log.warn("Job already failed, sending to DLQ: {}", jobId);
            throw new RuntimeException("Already failed job");
        }

        int updated = jobRepository.updateStatusIfMatch(
                jobId,
                Status.DISPATCHED,
                Status.RUNNING,
                LocalDateTime.now()
        );

        if (updated == 0) return;

        job = jobRepository.findById(job.getId()).orElseThrow();

        JobRunEntity run = new JobRunEntity();
        run.setJobId(job.getId());
        run.setStartTime(LocalDateTime.now());
        run.setStatus(Status.RUNNING);

        run = jobRunRepository.save(run);

        log.info("Job started safely: {}", job.getId());

        log.info("Executing job: {}", job.getId());

        try {
            String output = executeCommand(job.getCommand());

            run.setOutput(output);
            run.setStatus(Status.SUCCESS);
            run.setEndTime(LocalDateTime.now());

            job.setRetryCount(0);

            if (job.getRepeatIntervalSeconds() != null) {

                job.setNextRunTime(
                        LocalDateTime.now().plusSeconds(job.getRepeatIntervalSeconds())
                );

                job.setStatus(Status.SCHEDULED);

                log.info("Recurring job {} rescheduled", job.getId(), job.getNextRunTime());

            } else {
                job.setStatus(Status.SUCCESS);
            }

            job.setUpdatedAt(LocalDateTime.now());

            jobRepository.save(job);
            jobRunRepository.save(run);

            log.info("Job {} executed successfully", job.getId());
            log.debug("Job {} output: {}", job.getId(), output);

        } catch (Exception e) {

            run.setError(e.getMessage());
            run.setStatus(Status.FAILED);
            run.setEndTime(LocalDateTime.now());

            int retryCount = job.getRetryCount() == null ? 0 : job.getRetryCount();

            if (retryCount < job.getMaxRetries()) {

                job.setRetryCount(retryCount + 1);

                job.setStatus(Status.SCHEDULED);

                // retry after 10 seconds
                job.setNextRunTime(LocalDateTime.now().plusSeconds(10));

                log.warn("Retrying job: {}", job.getId());

                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
                jobRunRepository.save(run);

                return;

            } else {

                job.setStatus(Status.FAILED);

                log.error("Job failed: {}", job.getId());

                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
                jobRunRepository.save(run);

                throw e;  //throw this exception for Kafka
            }
        }

    }

    private String executeCommand(String command) throws Exception {

        ProcessBuilder processBuilder;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        }

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode);
        }

        return output.toString();
    }
}
