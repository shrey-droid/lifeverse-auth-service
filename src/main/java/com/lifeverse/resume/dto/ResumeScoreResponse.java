// ResumeScoreResponse.java
package com.lifeverse.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResumeScoreResponse {
    private int score;
    private List<String> keywords;
    private String timestamp;
}

