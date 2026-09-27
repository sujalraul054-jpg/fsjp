package com.example.serialposition.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full detail for a single past attempt, shown when the user drills into
 * one row of their history. Same position-table shape as a fresh
 * RecallResultDto, minus the fields that only make sense at submit time
 * (duplicate/unknown word notes, semantic associations) since those
 * aren't stored.
 */
public class AttemptDetailDto {

    private Long id;
    private LocalDateTime timestamp;
    private int totalWords;
    private int totalRecalled;
    private double recallPercentage;
    private List<PositionResultDto> positionResults;

    public AttemptDetailDto() {
    }

    public AttemptDetailDto(Long id, LocalDateTime timestamp, int totalWords, int totalRecalled,
                             double recallPercentage, List<PositionResultDto> positionResults) {
        this.id = id;
        this.timestamp = timestamp;
        this.totalWords = totalWords;
        this.totalRecalled = totalRecalled;
        this.recallPercentage = recallPercentage;
        this.positionResults = positionResults;
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

    public List<PositionResultDto> getPositionResults() {
        return positionResults;
    }

    public void setPositionResults(List<PositionResultDto> positionResults) {
        this.positionResults = positionResults;
    }
}
