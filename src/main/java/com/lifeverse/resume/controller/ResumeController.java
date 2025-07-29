// ResumeController.java
package com.lifeverse.resume.controller;

import com.lifeverse.resume.dto.ResumeScoreResponse;
import com.lifeverse.resume.entity.ResumeScore;
import com.lifeverse.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeScoreResponse> uploadResume(@RequestParam("file") MultipartFile file,
                                                            @AuthenticationPrincipal UserDetails user) {
        ResumeScoreResponse response = resumeService.processResume(file, user.getUsername());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-scores")
    public ResponseEntity<List<ResumeScoreResponse>> getMyResumeScores(@AuthenticationPrincipal UserDetails user) {
        List<ResumeScore> scores = resumeService.getScoresByEmail(user.getUsername());
        List<ResumeScoreResponse> response = scores.stream()
                .map(s -> new ResumeScoreResponse(s.getScore(), Arrays.asList(s.getMatchedKeywords().split(",")), s.getTimestamp().toString()))
                .toList();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/tailor")
    public ResponseEntity<String> tailorResume(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("job") MultipartFile jobDescFile,
            @AuthenticationPrincipal UserDetails user) {

        String tailoredOutput = resumeService.tailorResumeToJob(resumeFile, jobDescFile);
        return ResponseEntity.ok(tailoredOutput);
    }
    @PostMapping("/linkedin-upload")
    public ResponseEntity<ResumeScoreResponse> uploadLinkedInResume(@RequestParam("file") MultipartFile file,
                                                                    @AuthenticationPrincipal UserDetails user) {
        ResumeScoreResponse response = resumeService.processLinkedInResume(file, user.getUsername());
        return ResponseEntity.ok(response);
    }
//    @PostMapping("/ai-fix")
//    public ResponseEntity<String> aiFixResume(@RequestParam("file") MultipartFile file) {
//        String suggestions = resumeService.improveResumeWithAI(file);
//        return ResponseEntity.ok(suggestions);
//    }
}