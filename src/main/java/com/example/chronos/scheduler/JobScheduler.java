package com.example.chronos.scheduler;

import com.example.chronos.entity.JobEntity;
import com.example.chronos.enums.Status;
import com.example.chronos.producer.JobProducer;
import com.example.chronos.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class JobScheduler {
    private final JobRepository jobRepository;

    private final JobProducer jobProducer;

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    public JobScheduler(JobRepository jobRepository, JobProducer jobProducer){
        this.jobRepository = jobRepository;
        this.jobProducer = jobProducer;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void pollJobs() {

        List<JobEntity> jobs = jobRepository.findTop10ByStatusAndNextRunTimeBefore(Status.SCHEDULED, LocalDateTime.now(ZoneOffset.UTC));

        for (JobEntity job : jobs) {
            int updated = jobRepository.updateStatusIfMatch(
                    job.getId(),
                    Status.SCHEDULED,
                    Status.DISPATCHED,
                    LocalDateTime.now(ZoneOffset.UTC)
            );

            if (updated > 0) {
                jobProducer.sendJob(job.getId());
                log.info("Job sent to Kafka: {}", job.getId());
            }
        }
    }
}
