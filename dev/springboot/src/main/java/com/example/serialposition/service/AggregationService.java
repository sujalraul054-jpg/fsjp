package com.example.serialposition.service;

import com.example.serialposition.dto.AggregateStatsDto;
import com.example.serialposition.dto.PositionAggregateDto;
import com.example.serialposition.model.PositionResult;
import com.example.serialposition.repository.PositionResultRepository;
import com.example.serialposition.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Turns the flat pile of PositionResult rows (one per word per attempt)
 * into a per-position recall percentage across every attempt on record.
 * Plotting recallPercentage against position is what produces the
 * U-shaped serial position curve.
 */
@Service
public class AggregationService {

    private final TestAttemptRepository testAttemptRepository;
    private final PositionResultRepository positionResultRepository;

    public AggregationService(TestAttemptRepository testAttemptRepository,
                               PositionResultRepository positionResultRepository) {
        this.testAttemptRepository = testAttemptRepository;
        this.positionResultRepository = positionResultRepository;
    }

    public AggregateStatsDto getAggregateStats() {
        List<PositionResult> allResults = positionResultRepository.findAll();

        Map<Integer, List<PositionResult>> byPosition = allResults.stream()
                .collect(Collectors.groupingBy(PositionResult::getPosition));

        List<PositionAggregateDto> aggregates = byPosition.entrySet().stream()
                .map(entry -> {
                    int position = entry.getKey();
                    List<PositionResult> resultsForPosition = entry.getValue();
                    int timesPresented = resultsForPosition.size();
                    long timesRecalled = resultsForPosition.stream()
                            .filter(PositionResult::isRecalled)
                            .count();
                    double recallPercentage = percentage((int) timesRecalled, timesPresented);
                    return new PositionAggregateDto(position, timesPresented, (int) timesRecalled, recallPercentage);
                })
                .sorted(Comparator.comparingInt(PositionAggregateDto::getPosition))
                .collect(Collectors.toList());

        int totalAttempts = (int) testAttemptRepository.count();

        return new AggregateStatsDto(totalAttempts, aggregates);
    }

    private double percentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round((part * 10000.0) / total) / 100.0;
    }
}
