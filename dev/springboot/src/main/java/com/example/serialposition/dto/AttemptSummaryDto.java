package com.example.serialposition.dto;

import java.time.LocalDateTime;

/**
 * One row in the "past attempts" history list — enough to show a
 * scannable list without pulling every position's detail for each row.
 */
public class AttemptSummaryDto {

    private Long id;
    private LocalDateTime timestamp;
    private int totalWords;
    private int totalRecalled;
    private double recallPercentage;

    public AttemptSummaryDto() {
    }

    public AttemptSummaryDto(Long id, LocalDateTime timestamp, int totalWords,
                              int totalRecalled, double recallPercentage) {
        this.id = id;
        this.timestamp = timestamp;
        this.totalWords = totalWords;
        this.totalRecalled = totalRecalled;
        this.recallPercentage = recallPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getTotalRecalled() {
        return totalRecalled;
    }

    public void setTotalRecalled(int totalRecalled) {
        this.totalRecalled = totalRecalled;
    }

    public double getRecallPercentage() {
        return recallPercentage;
    }

    public void setRecallPercentage(double recallPercentage) {
        this.recallPercentage = recallPercentage;
    }
}
