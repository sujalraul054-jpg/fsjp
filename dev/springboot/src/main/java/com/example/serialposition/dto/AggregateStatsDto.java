package com.example.serialposition.dto;

import java.util.List;

/**
 * What GET /api/recall/aggregate returns: how many attempts have been
 * recorded in total, and each position's recall rate across all of them.
 */
public class AggregateStatsDto {

    private int totalAttempts;
    private List<PositionAggregateDto> positions;

    public AggregateStatsDto() {
    }

    public AggregateStatsDto(int totalAttempts, List<PositionAggregateDto> positions) {
        this.totalAttempts = totalAttempts;
        this.positions = positions;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public List<PositionAggregateDto> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionAggregateDto> positions) {
        this.positions = positions;
    }
}
