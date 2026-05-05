package com.example.chronos.consumer;

import com.example.chronos.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JobConsumer {

    private final JobWorker jobWorker;

    public JobConsumer(JobWorker jobWorker) {
        this.jobWorker = jobWorker;
    }

    private static final Logger log = LoggerFactory.getLogger(JobConsumer.class);

    @KafkaListener(topics = "job-topic", groupId = "chronos-group")
    public void consume(String jobId) throws Exception {
        jobWorker.processSingleJob(Long.valueOf(jobId.trim()));
    }

    @KafkaListener(topics = "job-topic-dlq", groupId = "chronos-group")
    public void consumeDlq(String jobId) {
        log.error("DLQ received job: {}", jobId);
    }
}
