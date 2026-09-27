package com.example.serialposition.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One row per submitted recall attempt. The word lists are stored as
 * comma-separated strings rather than a separate table, which keeps this
 * entity simple to read directly in the H2 console. Per-position detail
 * (needed for the U-curve) lives in PositionResult, one row per word.
 */
@Entity
@Table(name = "test_attempt")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    // Comma-separated, in original presentation order, e.g. "Apple,Table,River,..."
    private String originalWords;

    // Comma-separated, exactly as parsed from the user's recall input
    private String recalledWords;

    private int totalWords;
    private int totalRecalled;
    private double recallPercentage;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PositionResult> positionResults = new ArrayList<>();

    public TestAttempt() {
    }

    /** Keeps both sides of the relationship in sync when adding a result. */
    public void addPositionResult(PositionResult result) {
        positionResults.add(result);
        result.setTestAttempt(this);
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

    public String getOriginalWords() {
        return originalWords;
    }

    public void setOriginalWords(String originalWords) {
        this.originalWords = originalWords;
    }

    public String getRecalledWords() {
        return recalledWords;
    }

    public void setRecalledWords(String recalledWords) {
        this.recalledWords = recalledWords;
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

    public List<PositionResult> getPositionResults() {
        return positionResults;
    }

    public void setPositionResults(List<PositionResult> positionResults) {
        this.positionResults = positionResults;
    }
}
