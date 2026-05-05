package com.example.chronos.controller;

import com.example.chronos.dto.CreateJobRequest;
import com.example.chronos.entity.JobEntity;
import com.example.chronos.entity.JobRunEntity;
import com.example.chronos.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobEntity> createJob(@RequestBody CreateJobRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobEntity> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @GetMapping
    public ResponseEntity<List<JobEntity>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyJobs());
    }

    @GetMapping("/{id}/runs")
    public ResponseEntity<List<JobRunEntity>> getJobRuns(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobRuns(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelJob(@PathVariable Long id) {
        jobService.cancelJob(id);
        return ResponseEntity.ok("Job cancelled");
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<JobEntity> rescheduleJob(
            @PathVariable Long id,
            @RequestParam String  newTime
    ) {
        return ResponseEntity.ok(jobService.rescheduleJob(id, newTime));
    }
}
