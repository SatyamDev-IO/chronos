package com.example.chronos.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class JobProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(JobProducer.class);

    public JobProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendJob(Long jobId) {
        kafkaTemplate.send("job-topic", String.valueOf(jobId))
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Job {} successfully published", jobId);
                    } else {
                        log.error("Failed to publish job {}", jobId, ex);
                    }
                });
        log.info("Publishing job {} to topic {}", jobId, "job-topic");
    }
}
