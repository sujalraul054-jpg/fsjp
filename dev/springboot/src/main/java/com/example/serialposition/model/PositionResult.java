package com.example.serialposition.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One row per word per attempt: which position it was in, what the word
 * was, and whether it was recalled. Many rows across many attempts, all
 * sharing the same position number, are what Phase 5's aggregation query
 * groups together to build the U-shaped curve.
 *
 * Note: there's no "recall percentage" column here, even though the
 * original spec mentions one at the position level. A single row can only
 * be recalled or not (0% or 100%), so that percentage is only meaningful
 * once you aggregate many attempts — which Phase 5 computes on the fly
 * from these rows, rather than storing a number here that would go stale
 * as new attempts come in.
 */
@Entity
@Table(name = "position_result")
public class PositionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int position;
    private String word;
    private boolean recalled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private TestAttempt testAttempt;

    public PositionResult() {
    }

    public PositionResult(int position, String word, boolean recalled) {
        this.position = position;
        this.word = word;
        this.recalled = recalled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isRecalled() {
        return recalled;
    }

    public void setRecalled(boolean recalled) {
        this.recalled = recalled;
    }

    public TestAttempt getTestAttempt() {
        return testAttempt;
    }

    public void setTestAttempt(TestAttempt testAttempt) {
        this.testAttempt = testAttempt;
    }
}
